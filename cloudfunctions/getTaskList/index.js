const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

exports.main = async (event, context) => {
  console.log('getTaskList called with event:', event)
  
  const wxContext = cloud.getWXContext()
  const openid = event.openid || wxContext.OPENID

  if (!openid) {
    console.error('getTaskList error: no openid')
    return {
      success: false,
      message: '用户未登录'
    }
  }

  const { expressPoint, pageSize = 10, pageIndex = 1, type = 'pending', sortBy = 'time' } = event

  try {
    const skip = (pageIndex - 1) * pageSize
    let query = db.collection('tasks')

    if (type === 'claimed') {
      query = query.where({
        status: 'claimed',
        receiverOpenid: openid
      })
    } else if (type === 'completed') {
      if (event.subType === 'published') {
        query = query.where({
          status: 'completed',
          openid: openid
        })
      } else {
        query = query.where({
          status: 'completed',
          receiverOpenid: openid
        })
      }
    } else {
      if (!expressPoint) {
        return {
          success: false,
          message: '请指定快递点'
        }
      }
      query = query.where({
        expressPoint: expressPoint,
        status: 'pending'
      })
    }

    // 根据排序类型设置排序字段
    const orderByField = sortBy === 'price' ? 'reward' : 'createTime'
    
    const listRes = await query
      .orderBy(orderByField, 'desc')
      .skip(skip)
      .limit(pageSize)
      .get()

    console.log('getTaskList result:', listRes.data.length, 'tasks')

    // 如果按价格排序，确保按数字类型排序
    let data = listRes.data
    if (sortBy === 'price') {
      data = data.sort((a, b) => {
        const rewardA = typeof a.reward === 'string' ? parseFloat(a.reward) : (a.reward || 0)
        const rewardB = typeof b.reward === 'string' ? parseFloat(b.reward) : (b.reward || 0)
        return rewardB - rewardA  // 降序，价格高的在前
      })
    }

    data = data.map(item => ({
      _id: item._id,
      expressPoint: item.expressPoint,
      size: item.size,
      reward: item.reward,
      dormBuilding: item.dormBuilding,
      createTime: item.createTime,
      pickupCode: item.pickupCode,
      finishTime: item.finishTime,
      status: item.status,
      showWechat: item.showWechat || false,
      wechat: item.wechat || ''
    }))

    return {
      success: true,
      data: data,
      total: listRes.data.length,
      pageIndex: pageIndex,
      pageSize: pageSize
    }
  } catch (err) {
    console.error('getTaskList error:', err)
    return {
      success: false,
      message: '获取任务列表失败: ' + err.message
    }
  }
}
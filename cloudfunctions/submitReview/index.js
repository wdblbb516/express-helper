const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext()
  // 优先使用传入的 openid（测试账号），否则使用微信官方 openid
  const openid = event.openid || wxContext.OPENID
  
  console.log('submitReview - event.openid:', event.openid, 'wxContext.OPENID:', wxContext.OPENID, 'used openid:', openid)

  if (!openid) {
    return {
      success: false,
      message: '用户未登录'
    }
  }

  const { taskId, role, rating, dimensions, comment } = event

  if (!taskId || !role || !rating || !dimensions) {
    return {
      success: false,
      message: '参数不完整'
    }
  }

  if (rating < 1 || rating > 5) {
    return {
      success: false,
      message: '评分必须在1-5星之间'
    }
  }

  try {
    const taskRes = await db.collection('tasks').where({
      _id: taskId
    }).get()

    if (taskRes.data.length === 0) {
      return {
        success: false,
        message: '任务不存在'
      }
    }

    const task = taskRes.data[0]

    let toOpenid = ''
    if (role === 'publisher') {
      toOpenid = task.receiverOpenid
    } else if (role === 'receiver') {
      toOpenid = task.openid
    }

    if (!toOpenid) {
      return {
        success: false,
        message: '被评价者不存在'
      }
    }

    const existingReview = await db.collection('reviews').where({
      taskId: taskId,
      fromOpenid: openid
    }).get()

    if (existingReview.data.length > 0) {
      return {
        success: false,
        message: '您已评价过该任务'
      }
    }

    await db.collection('reviews').add({
      data: {
        taskId: taskId,
        fromOpenid: openid,
        toOpenid: toOpenid,
        role: role,
        rating: rating,
        dimensions: {
          communication: dimensions.communication || 5,
          speed: dimensions.speed || 5,
          carefulness: dimensions.carefulness || 5
        },
        comment: comment || '',
        createTime: new Date()
      }
    })

    return {
      success: true,
      message: '评价成功'
    }
  } catch (err) {
    console.error('submitReview error:', err)
    return {
      success: false,
      message: '评价失败'
    }
  }
}
const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext()
  const openid = wxContext.OPENID

  if (!openid) {
    return {
      success: false,
      message: '用户未登录'
    }
  }

  const { taskId } = event

  if (!taskId) {
    return {
      success: false,
      message: '请指定任务ID'
    }
  }

  try {
    const taskRes = await db.collection('tasks')
      .where({
        _id: taskId,
        receiverOpenid: openid,
        status: 'claimed'
      })
      .get()

    if (taskRes.data.length === 0) {
      return {
        success: false,
        message: '任务不存在或无权操作'
      }
    }

    await db.collection('tasks')
      .where({
        _id: taskId,
        receiverOpenid: openid,
        status: 'claimed'
      })
      .update({
        data: {
          status: 'completed',
          finishTime: new Date()
        }
      })

    return {
      success: true,
      message: '确认送达成功'
    }
  } catch (err) {
    console.error('confirmDelivery error:', err)
    return {
      success: false,
      message: '确认失败，请重试'
    }
  }
}
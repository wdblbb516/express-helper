const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()
const _ = db.command

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext()
  // 优先使用传入的 openid（测试账号），否则使用微信官方 openid
  const openid = event.openid || wxContext.OPENID

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
    const transaction = await db.startTransaction()

    const taskRes = await transaction.collection('tasks')
      .where({
        _id: taskId,
        status: 'pending'
      })
      .get()

    if (taskRes.data.length === 0) {
      await transaction.rollback()
      return {
        success: false,
        message: '任务已被抢或不存在'
      }
    }

    const task = taskRes.data[0]

    await transaction.collection('tasks')
      .where({
        _id: taskId,
        status: 'pending'
      })
      .update({
        data: {
          status: 'claimed',
          receiverOpenid: openid
        }
      })

    await transaction.commit()

    return {
      success: true,
      message: '接单成功',
      pickupCode: task.pickupCode
    }
  } catch (err) {
    console.error('claimTask error:', err)
    try {
      await db.rollback()
    } catch (rollbackErr) {
      console.error('rollback error:', rollbackErr)
    }
    return {
      success: false,
      message: '接单失败，请重试'
    }
  }
}
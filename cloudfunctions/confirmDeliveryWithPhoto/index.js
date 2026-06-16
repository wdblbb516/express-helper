// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
  env: 'cloud1-d0g0233npd8de57ed'
})

const db = cloud.database()

// 云函数入口函数
exports.main = async (event, context) => {
  const { taskId, photoFileID } = event

  try {
    const taskRes = await db.collection('tasks').doc(taskId).get()
    
    if (!taskRes.data) {
      return {
        success: false,
        message: '任务不存在'
      }
    }

    const task = taskRes.data
    
    if (task.status !== 'claimed') {
      return {
        success: false,
        message: '任务状态不正确'
      }
    }

    await db.collection('tasks').doc(taskId).update({
      data: {
        status: 'pending_confirm',
        deliveryPhoto: photoFileID,
        updateTime: new Date()
      }
    })

    return {
      success: true,
      message: '送达成功'
    }
  } catch (err) {
    console.error('confirmDeliveryWithPhoto error:', err)
    return {
      success: false,
      message: '操作失败，请重试'
    }
  }
}
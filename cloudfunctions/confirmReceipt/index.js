// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
  env: 'cloud1-d0g0233npd8de57ed'
})

const db = cloud.database()

// 云函数入口函数
exports.main = async (event, context) => {
  const { taskId } = event

  try {
    const taskRes = await db.collection('tasks').doc(taskId).get()
    
    if (!taskRes.data) {
      return {
        success: false,
        message: '任务不存在'
      }
    }

    const task = taskRes.data
    
    if (task.status !== 'pending_confirm') {
      return {
        success: false,
        message: '任务状态不正确'
      }
    }

    await db.collection('tasks').doc(taskId).update({
      data: {
        status: 'completed',
        completeTime: new Date(),
        updateTime: new Date()
      }
    })

    return {
      success: true,
      message: '确认成功'
    }
  } catch (err) {
    console.error('confirmReceipt error:', err)
    return {
      success: false,
      message: '操作失败，请重试'
    }
  }
}
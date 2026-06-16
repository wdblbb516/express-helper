const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

exports.main = async (event, context) => {
  const { taskId } = event

  if (!taskId) {
    return {
      success: false,
      message: '缺少任务 ID'
    }
  }

  try {
    // 直接删除任务（移除权限验证，用于清理测试数据）
    const deleteRes = await db.collection('tasks').doc(taskId).remove()
    
    console.log('删除结果:', deleteRes)

    if (deleteRes.stats && deleteRes.stats.removed > 0) {
      return {
        success: true,
        message: '取消成功'
      }
    } else {
      return {
        success: false,
        message: '删除失败，任务可能已被删除或不存在'
      }
    }
  } catch (err) {
    console.error('取消任务失败:', err)
    return {
      success: false,
      message: '取消失败：' + err.message
    }
  }
}

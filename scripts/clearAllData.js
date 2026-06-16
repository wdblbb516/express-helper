// 数据库清理脚本 - 用于一次性清除所有测试数据
// 使用方法：在微信开发者工具的云开发控制台中执行此脚本

const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

// 清除所有数据的主函数
async function clearAllData() {
  console.log('开始清理数据库...')
  
  try {
    // 1. 清除所有用户数据
    const usersRes = await db.collection('users').get()
    if (usersRes.data.length > 0) {
      for (const user of usersRes.data) {
        await db.collection('users').doc(user._id).remove()
      }
      console.log(`已清除 ${usersRes.data.length} 个用户`)
    }
    
    // 2. 清除所有任务数据
    const tasksRes = await db.collection('tasks').get()
    if (tasksRes.data.length > 0) {
      for (const task of tasksRes.data) {
        await db.collection('tasks').doc(task._id).remove()
      }
      console.log(`已清除 ${tasksRes.data.length} 个任务`)
    }
    
    // 3. 清除所有评价数据
    const reviewsRes = await db.collection('reviews').get()
    if (reviewsRes.data.length > 0) {
      for (const review of reviewsRes.data) {
        await db.collection('reviews').doc(review._id).remove()
      }
      console.log(`已清除 ${reviewsRes.data.length} 条评价`)
    }
    
    // 4. 清除所有本地缓存（需要在前端执行）
    // wx.removeStorageSync('openid')
    // wx.removeStorageSync('test_openid')
    // wx.removeStorageSync('userInfo')
    
    console.log('✅ 数据库清理完成！')
    return {
      success: true,
      message: '所有数据已清除'
    }
  } catch (err) {
    console.error('清理失败:', err)
    return {
      success: false,
      message: '清理失败: ' + err.message
    }
  }
}

// 如果是作为云函数调用
exports.main = async (event, context) => {
  return await clearAllData()
}

// 如果是直接运行
// clearAllData()

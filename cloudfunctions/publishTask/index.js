const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

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

  const { expressPoint, size, reward, pickupCode, dormBuilding, showWechat, wechat } = event

  if (!expressPoint || !size || !reward || !pickupCode || !dormBuilding) {
    return {
      success: false,
      message: '参数不完整'
    }
  }

  if (reward < 1 || reward > 10) {
    return {
      success: false,
      message: '悬赏金额必须在1-10元之间'
    }
  }

  try {
    const result = await db.collection('tasks').add({
      data: {
        openid: openid,
        expressPoint: expressPoint,
        size: size,
        reward: reward,
        pickupCode: pickupCode,
        dormBuilding: dormBuilding,
        showWechat: showWechat || false,
        wechat: wechat || '',
        status: 'pending',
        receiverOpenid: '',
        createTime: new Date(),
        finishTime: null
      }
    })

    return {
      success: true,
      message: '发布成功',
      taskId: result._id
    }
  } catch (err) {
    console.error('publishTask error:', err)
    return {
      success: false,
      message: '发布失败'
    }
  }
}
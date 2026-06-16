const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

const db = cloud.database()

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext()
  // 优先使用前端传入的 openid（测试环境），否则使用微信官方 openid
  const openid = event.openid || wxContext.OPENID
  const { nickname, dormBuilding, wechat, avatarUrl } = event

  if (!openid) {
    return {
      success: false,
      message: '无法获取openid'
    }
  }

  if (!nickname || !nickname.trim()) {
    return {
      success: false,
      message: '昵称不能为空'
    }
  }

  try {
    const userRes = await db.collection('users').where({
      openid: openid
    }).get()

    if (userRes.data.length === 0) {
      return {
        success: false,
        message: '用户不存在'
      }
    }

    const updateData = {
      nickname: nickname.trim(),
      dormBuilding: dormBuilding ? dormBuilding.trim() : '',
      wechat: wechat ? wechat.trim() : ''
    }

    // 如果有头像URL，才更新头像
    if (avatarUrl) {
      updateData.avatarUrl = avatarUrl
    }

    await db.collection('users').where({
      openid: openid
    }).update({
      data: updateData
    })

    return {
      success: true,
      message: '更新成功'
    }
  } catch (err) {
    console.error('updateUserInfo error:', err)
    return {
      success: false,
      message: '数据库操作失败'
    }
  }
}
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
      message: '无法获取openid'
    }
  }

  try {
    const userRes = await db.collection('users').where({
      openid: openid
    }).get()

    // 如果用户已存在，更新信息（如果提供了新的昵称或头像）
    if (userRes.data.length > 0) {
      const existingUser = userRes.data[0]
      let needUpdate = false
      const updateData = {}

      if (event.nickname && event.nickname !== existingUser.nickname) {
        updateData.nickname = event.nickname
        needUpdate = true
      }
      if (event.avatar && event.avatar !== existingUser.avatarUrl) {
        updateData.avatarUrl = event.avatar
        needUpdate = true
      }

      if (needUpdate) {
        await db.collection('users').doc(existingUser._id).update({
          data: updateData
        })
        // 返回更新后的用户信息
        return {
          success: true,
          openid: openid,
          user: {
            ...existingUser,
            ...updateData
          }
        }
      }

      return {
        success: true,
        openid: openid,
        user: existingUser
      }
    }

    // 创建新用户，使用传入的昵称或默认昵称
    const randomNum = Math.floor(Math.random() * 10000)
    const newUser = {
      openid: openid,
      nickname: event.nickname || `快递侠${randomNum}`,
      avatarUrl: event.avatar || '',
      dormBuilding: '',
      wechat: '',
      createTime: new Date()
    }

    const addRes = await db.collection('users').add({
      data: newUser
    })

    return {
      success: true,
      openid: openid,
      user: {
        _id: addRes._id,
        ...newUser
      }
    }
  } catch (err) {
    console.error('login error:', err)
    return {
      success: false,
      message: '数据库操作失败'
    }
  }
}

App({
  onLaunch: async function () {
    if (!wx.cloud) {
      console.error('请使用 2.2.3 或以上的基础库以使用云能力')
    } else {
      wx.cloud.init({
        env: 'cloud1-d0g0233npd8de57ed',
        traceUser: true,
      })
    }

    this.globalData = {
      userInfo: null,
      openid: '',
      isLoggedOut: false,
      token: null
    }

    // 检查是否已经退出登录
    const isLoggedOut = wx.getStorageSync('isLoggedOut')
    
    // 如果没有明确退出，才检查测试账号
    if (!isLoggedOut) {
      const testOpenid = wx.getStorageSync('test_openid')
      if (testOpenid) {
        // 使用测试账号
        this.globalData.openid = testOpenid
        await this.loadUserInfo(testOpenid)
      } else {
        // 正常的微信登录
        await this.login()
      }
    } else {
      // 用户已退出，不自动登录
      this.globalData.isLoggedOut = true
      this.globalData.openid = ''
      this.globalData.userInfo = null
    }
  },

  async login() {
    try {
      wx.removeStorageSync('isLoggedOut')
      this.globalData.isLoggedOut = false
      
      const res = await wx.cloud.callFunction({
        name: 'login'
      })
      if (res.result.success) {
        this.globalData.openid = res.result.openid
        this.globalData.userInfo = res.result.user
      }
    } catch (err) {
      console.error('登录失败:', err)
    }
  },

  async loadUserInfo(openid) {
    try {
      const db = wx.cloud.database()
      const userRes = await db.collection('users').where({
        openid: openid
      }).get()
      
      if (userRes.data.length > 0) {
        this.globalData.userInfo = userRes.data[0]
      } else {
        this.globalData.userInfo = null
      }
    } catch (err) {
      console.error('加载用户信息失败:', err)
    }
  },

  logout() {
    // 清除所有缓存（包括 openid）
    wx.removeStorageSync('isLoggedOut')
    wx.removeStorageSync('test_openid')
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('openid')
    
    // 清除全局数据
    this.globalData.userInfo = null
    this.globalData.openid = ''
    this.globalData.isLoggedOut = true
    this.globalData.token = null
    
    // 跳转到登录页面
    wx.redirectTo({
      url: '/pages/login/login'
    })
  }
})
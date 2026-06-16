const app = getApp()
const { request } = require('../../utils/request.js')

Page({
  data: {
    isAgreed: false,
    isLoading: false,
    nickname: '',
    avatarUrl: '',
    showEditForm: false
  },

  onLoad(options) {
    const openid = wx.getStorageSync('openid')
    if (openid) {
      this.redirectToHome()
    }
  },

  toggleAgree() {
    this.setData({
      isAgreed: !this.data.isAgreed
    })
  },

  handleLoginRegister() {
    wx.showActionSheet({
      itemList: ['测试账号1（发布者）', '测试账号2（接单者）'],
      itemColor: '#07C160',
      success: (res) => {
        if (res.tapIndex === 0) {
          this.switchToTestAccount('test_openid_1', '用户1号')
        } else if (res.tapIndex === 1) {
          this.switchToTestAccount('test_openid_2', '用户2号')
        }
      },
      fail: () => {
        console.log('取消选择')
      }
    })
  },

  async switchToTestAccount(testOpenid, nickname) {
    try {
      wx.showLoading({ title: '登录中...' })

      wx.removeStorageSync('openid')
      wx.removeStorageSync('userInfo')

      wx.setStorageSync('openid', testOpenid)
      app.globalData.openid = testOpenid

      const user = await request('userService', '/users/login', 'POST', {
        openid: testOpenid,
        nickname: nickname,
        avatarUrl: ''
      })

      wx.setStorageSync('userInfo', user)
      app.globalData.userInfo = user

      wx.hideLoading()
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500,
        success: () => {
          setTimeout(() => {
            this.redirectToHome()
          }, 500)
        }
      })
    } catch (err) {
      wx.hideLoading()
      console.error('登录失败:', err)
      wx.showToast({ title: '登录失败', icon: 'none' })
    }
  },

  async handleWechatLogin() {
    const { isAgreed, isLoading } = this.data

    if (!isAgreed) {
      wx.showToast({ title: '请先阅读并同意用户协议', icon: 'none', duration: 2000 })
      return
    }

    if (isLoading) {
      return
    }

    this.setData({ isLoading: true })

    try {
      const userProfile = await this.getUserProfile()
      
      if (userProfile) {
        this.setData({
          nickname: userProfile.nickName,
          avatarUrl: userProfile.avatarUrl,
          showEditForm: true,
          isLoading: false
        })
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      wx.showToast({ title: '获取用户信息失败', icon: 'none' })
      this.setData({ isLoading: false })
    }
  },

  getUserProfile() {
    return new Promise((resolve, reject) => {
      wx.getUserProfile({
        desc: '用于完善会员资料',
        success: (res) => {
          resolve(res.userInfo)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0]
        this.setData({ avatarUrl: tempFilePath })
      },
      fail: () => {
        wx.showToast({ title: '选择图片失败', icon: 'none' })
      }
    })
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  async submitLogin() {
    const { nickname, avatarUrl, isLoading } = this.data

    if (!nickname.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' })
      return
    }

    if (isLoading) {
      return
    }

    this.setData({ isLoading: true })

    try {
      const user = await request('userService', '/users/login', 'POST', {
        openid: 'wechat_openid_' + Date.now(),
        nickname: nickname,
        avatarUrl: avatarUrl
      })

      wx.setStorageSync('openid', user.openid)
      wx.setStorageSync('userInfo', user)
      app.globalData.openid = user.openid
      app.globalData.userInfo = user

      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 2000,
        success: () => {
          setTimeout(() => {
            this.redirectToHome()
          }, 500)
        }
      })
    } catch (error) {
      console.error('登录失败:', error)
      wx.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
    } finally {
      this.setData({ isLoading: false })
    }
  },

  redirectToHome() {
    wx.reLaunch({ url: '/pages/index/index' })
  },

  goBack() {
    this.setData({ showEditForm: false, nickname: '', avatarUrl: '' })
  }
})
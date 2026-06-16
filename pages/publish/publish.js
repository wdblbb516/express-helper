const app = getApp()
const { request } = require('../../utils/request.js')

Page({
  data: {
    expressPoints: ['小镇菜鸟驿站', '国赛5号馆', '阳光顺丰'],
    packageSizes: ['小件（如信封）', '中件（鞋盒大小）', '大件（行李箱）'],
    dormArray: Array.from({length: 26}, (_, i) => `${i + 1}栋`),
    expressPointIndex: 0,
    sizeIndex: 0,
    dormIndex: 0,
    reward: '',
    pickupCode: '',
    showWechat: true,
    userWechat: ''
  },

  onLoad: function() {
    this.loadUserWechat()
  },

  async loadUserWechat() {
    try {
      const openid = app.globalData.openid || wx.getStorageSync('openid')
      if (!openid) return
      
      const user = await request('userService', `/users/openid/${openid}`, 'GET')
      if (user && user.wechat) {
        this.setData({
          userWechat: user.wechat
        })
      }
    } catch (err) {
      console.error('获取用户微信号失败:', err)
    }
  },

  onExpressPointChange(e) {
    this.setData({
      expressPointIndex: e.detail.value
    })
  },

  onSizeChange(e) {
    this.setData({
      sizeIndex: e.detail.value
    })
  },

  onDormChange(e) {
    this.setData({
      dormIndex: e.detail.value
    })
  },

  onShowWechatChange(e) {
    this.setData({
      showWechat: e.detail.value
    })
  },

  onRewardInput(e) {
    const value = e.detail.value
    const num = parseInt(value) || 0
    if (num > 10) {
      this.setData({
        reward: '10'
      })
    } else if (num < 1 && value !== '') {
      this.setData({
        reward: '1'
      })
    } else {
      this.setData({
        reward: value
      })
    }
  },

  onPickupCodeInput(e) {
    this.setData({
      pickupCode: e.detail.value
    })
  },

  async submitTask() {
    const { expressPoints, expressPointIndex, packageSizes, sizeIndex, dormArray, dormIndex, reward, pickupCode, showWechat, userWechat } = this.data

    if (!this.checkLogin()) {
      return
    }

    if (!reward || parseInt(reward) < 1 || parseInt(reward) > 10) {
      wx.showToast({
        title: '请输入1-10元的悬赏金额',
        icon: 'none'
      })
      return
    }

    if (!pickupCode.trim()) {
      wx.showToast({
        title: '请输入取件码',
        icon: 'none'
      })
      return
    }

    if (showWechat && !userWechat) {
      wx.showToast({
        title: '请先在个人资料中绑定微信号',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '发布中...'
    })

    try {
      const openid = app.globalData.openid || wx.getStorageSync('openid')
      
      await request('orderService', '/orders', 'POST', {
        publisherOpenid: openid,
        stationName: expressPoints[expressPointIndex],
        itemSize: packageSizes[sizeIndex],
        reward: parseFloat(reward),
        pickupCode: pickupCode.trim(),
        deliveryAddress: dormArray[dormIndex]
      })

      wx.hideLoading()
      wx.showToast({
        title: '发布成功',
        icon: 'success'
      })
      
      this.setData({
        expressPointIndex: 0,
        sizeIndex: 0,
        dormIndex: 0,
        reward: '',
        pickupCode: ''
      })

      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    } catch (err) {
      wx.hideLoading()
      console.error('发布任务失败:', err)
      wx.showToast({
        title: '发布失败，请重试',
        icon: 'none'
      })
    }
  },

  checkLogin() {
    const openid = app.globalData.openid || wx.getStorageSync('openid')
    if (!openid) {
      wx.showModal({
        title: '请先登录',
        content: '需要登录后才能发布任务',
        showCancel: false,
        success: () => {
          wx.reLaunch({
            url: '/pages/login/login'
          })
        }
      })
      return false
    }
    return true
  },

  goToHome() {
    wx.reLaunch({
      url: '/pages/index/index'
    })
  },

  goToPublish() {},

  goToProfile() {
    wx.navigateTo({
      url: '/pages/profile/profile'
    })
  }
})
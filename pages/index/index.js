const app = getApp()
const { request } = require('../../utils/request.js')

Page({
  data: {
    activeTab: '国赛5号馆',
    tasks: [],
    loading: false,
    hasMore: true,
    showModal: false,
    showConfirmModal: false,
    pickupCode: '',
    currentTaskId: '',
    sortType: 'time',
    showSort: false,
    sortLabel: '按时间排序'
  },

  onLoad: function () {
    this.loadData()
  },

  onShow: function () {
    this.loadData()
  },

  onPullDownRefresh: function () {
    this.loadData(true)
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab,
      tasks: [],
      hasMore: true
    })
    this.loadData()
  },

  showSortMenu() {
    this.setData({
      showSort: true
    })
  },

  hideSortMenu() {
    this.setData({
      showSort: false
    })
  },

  setSort(e) {
    const type = e.currentTarget.dataset.type
    const label = type === 'time' ? '按时间排序' : '按价格排序'
    
    this.setData({
      sortType: type,
      sortLabel: label,
      showSort: false,
      tasks: [],
      hasMore: true
    })
    
    this.loadData()
  },

  loadData(isRefresh = false) {
    const that = this
    
    const openid = app.globalData.openid || wx.getStorageSync('openid')
    
    if (!openid) {
      console.log('loadData: openid 为空，500ms后重试')
      setTimeout(() => {
        that.loadData(isRefresh)
      }, 500)
      return
    }

    this.setData({
      loading: true
    })

    this.doLoadTasks(openid, isRefresh)
  },

  async doLoadTasks(openid, isRefresh = false) {
    const { activeTab, sortType } = this.data

    try {
      const tasks = await request('orderService', `/orders/pending?stationName=${encodeURIComponent(activeTab)}`, 'GET')

      let newTasks = tasks.map(task => ({
        ...task,
        relativeTime: this.formatRelativeTime(task.createTime)
      }))

      if (sortType === 'price') {
        newTasks = newTasks.sort((a, b) => {
          const rewardA = typeof a.reward === 'string' ? parseFloat(a.reward) : (a.reward || 0)
          const rewardB = typeof b.reward === 'string' ? parseFloat(b.reward) : (b.reward || 0)
          return rewardB - rewardA
        })
      }

      this.setData({
        tasks: newTasks,
        hasMore: newTasks.length >= 20
      })
    } catch (err) {
      console.error('获取任务列表失败:', err)
    } finally {
      this.setData({
        loading: false
      })
      
      if (isRefresh) {
        wx.stopPullDownRefresh()
      }
    }
  },

  formatRelativeTime(dateStr) {
    if (!dateStr) return ''
    const now = new Date()
    const createTime = new Date(dateStr)
    const diff = now.getTime() - createTime.getTime()
    
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)

    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 7) return `${days}天前`
    return createTime.toLocaleDateString()
  },

  showConfirmModal(e) {
    const taskId = e.currentTarget.dataset.id
    this.setData({
      showConfirmModal: true,
      currentTaskId: taskId
    })
  },

  async confirmClaim() {
    const { currentTaskId } = this.data
    
    wx.showLoading({
      title: '接单中...'
    })

    try {
      const openid = app.globalData.openid || wx.getStorageSync('openid')
      
      const result = await request('orderService', `/orders/${currentTaskId}/claim?receiverOpenid=${openid}`, 'POST')

      wx.hideLoading()
      this.setData({
        showConfirmModal: false,
        showModal: true,
        pickupCode: result.pickupCode
      })
      this.loadData()
    } catch (err) {
      wx.hideLoading()
      this.setData({
        showConfirmModal: false
      })
      console.error('接单失败:', err)
      wx.showToast({
        title: '接单失败，请重试',
        icon: 'none'
      })
    }
  },

  cancelClaim() {
    this.setData({
      showConfirmModal: false,
      currentTaskId: ''
    })
  },

  closeModal() {
    this.setData({
      showModal: false,
      pickupCode: ''
    })
  },

  stopPropagation() {},

  copyWechat(e) {
    const wechat = e.currentTarget.dataset.wechat
    wx.setClipboardData({
      data: wechat,
      success: () => {
        wx.showToast({
          title: '已复制，请去微信添加好友沟通',
          icon: 'success'
        })
      },
      fail: () => {
        wx.showToast({
          title: '复制失败',
          icon: 'none'
        })
      }
    })
  },

  goToHome() {
    wx.reLaunch({
      url: '/pages/index/index'
    })
  },

  goToPublish() {
    wx.navigateTo({
      url: '/pages/publish/publish'
    })
  },

  goToProfile() {
    wx.navigateTo({
      url: '/pages/profile/profile'
    })
  }
})
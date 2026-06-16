const app = getApp()

Page({
  data: {
    expressPoint: '',
    tasks: [],
    total: 0,
    pageIndex: 1,
    pageSize: 10,
    loading: false,
    hasMore: true,
    refreshing: false,
    showModal: false,
    pickupCode: ''
  },

  onLoad: function (options) {
    const expressPoint = decodeURIComponent(options.expressPoint || '菜鸟驿站')
    this.setData({
      expressPoint
    })
    this.loadTasks()
  },

  async loadTasks(isRefresh = false) {
    const { expressPoint, pageIndex, pageSize } = this.data

    if (isRefresh) {
      this.setData({
        refreshing: true,
        pageIndex: 1,
        hasMore: true
      })
    } else {
      this.setData({
        loading: true
      })
    }

    try {
      const res = await wx.cloud.callFunction({
        name: 'getTaskList',
        data: {
          expressPoint,
          pageSize,
          pageIndex: isRefresh ? 1 : pageIndex
        }
      })

      if (res.result.success) {
        const newTasks = res.result.data.map(task => ({
          ...task,
          relativeTime: this.formatRelativeTime(task.createTime)
        }))

        if (isRefresh) {
          this.setData({
            tasks: newTasks,
            total: res.result.total,
            refreshing: false
          })
        } else {
          this.setData({
            tasks: [...this.data.tasks, ...newTasks],
            hasMore: newTasks.length >= pageSize
          })
        }
      }
    } catch (err) {
      console.error('获取任务列表失败:', err)
      if (isRefresh) {
        this.setData({ refreshing: false })
      }
    }

    this.setData({
      loading: false
    })
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

  async claimTask(e) {
    if (!await this.checkLogin()) {
      return
    }

    const taskId = e.currentTarget.dataset.id

    wx.showLoading({
      title: '接单中...'
    })

    try {
      const res = await wx.cloud.callFunction({
        name: 'claimTask',
        data: {
          taskId
        }
      })

      if (res.result.success) {
        wx.hideLoading()
        this.setData({
          showModal: true,
          pickupCode: res.result.pickupCode
        })
        this.loadTasks(true)
      } else {
        wx.hideLoading()
        wx.showToast({
          title: res.result.message || '接单失败',
          icon: 'none'
        })
      }
    } catch (err) {
      wx.hideLoading()
      console.error('接单失败:', err)
      wx.showToast({
        title: '接单失败，请重试',
        icon: 'none'
      })
    }
  },

  async checkLogin() {
    try {
      const res = await wx.cloud.callFunction({
        name: 'login'
      })
      if (!res.result.success) {
        wx.showModal({
          title: '请先登录',
          content: '需要登录后才能接单',
          showCancel: false,
          success: () => {
            wx.reLaunch({
              url: '/pages/index/index'
            })
          }
        })
        return false
      }
      return true
    } catch (err) {
      wx.showToast({
        title: '登录检查失败',
        icon: 'none'
      })
      return false
    }
  },

  closeModal() {
    this.setData({
      showModal: false,
      pickupCode: ''
    })
  },

  stopPropagation() {},

  goBack() {
    wx.navigateBack()
  },

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

  onPullDownRefresh() {
    this.loadTasks(true)
  },

  onLoadMore() {
    if (this.data.loading || !this.data.hasMore) return
    this.setData({
      pageIndex: this.data.pageIndex + 1
    })
    this.loadTasks()
  }
})
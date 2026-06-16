const baseUrls = {
  userService: 'http://127.0.0.1:8081/api',
  orderService: 'http://127.0.0.1:8082/api',
  stationService: 'http://127.0.0.1:8083/api'
}

const request = (service, url, method = 'GET', data = {}) => {
  return new Promise((resolve, reject) => {
    const baseUrl = baseUrls[service]
    const fullUrl = `${baseUrl}${url}`
    
    wx.request({
      url: fullUrl,
      method: method,
      data: data,
      header: {
        'content-type': 'application/json'
      },
      timeout: 30000,
      success: (res) => {
        if (res.data && res.data.code === 200) {
          resolve(res.data.data)
        } else {
          reject(res.data.message || '请求失败')
        }
      },
      fail: (err) => {
        reject(err.errMsg || '网络请求失败')
      }
    })
  })
}

module.exports = {
  request,
  baseUrls
}
// app.js
const { apiService } = require('./utils/api.js')

App({
  globalData: {
    userInfo: null,
    token: null,
    // apiBaseUrl: 'http://192.168.137.95:8080/api'
    apiBaseUrl: 'http://localhost:8080/api'
  },

  onLaunch() {
    console.log('小程序启动')
    
    // 从存储中恢复登录状态
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
      // 设置API服务的token
      apiService.setToken(token)
    }
  },

  // 登录成功后调用
  setLoginInfo(token, userInfo) {
    this.globalData.token = token
    this.globalData.userInfo = userInfo
    apiService.setToken(token)
    wx.setStorageSync('token', token)
    wx.setStorageSync('userInfo', userInfo)
  },

  // 退出登录
  logout() {
    this.globalData.token = null
    this.globalData.userInfo = null
    apiService.setToken(null)
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  // 显示加载提示
  showLoading(title = '加载中...') {
    if (this.globalData.isLoading) {
      this.hideLoading()
    }
    
    this.globalData.isLoading = true
    
    // 清除之前的定时器
    if (this.globalData.loadingTimer) {
      clearTimeout(this.globalData.loadingTimer)
    }
    
    // 设置超时自动隐藏
    this.globalData.loadingTimer = setTimeout(() => {
      this.hideLoading()
    }, 10000) // 10秒超时
    
    wx.showLoading({
      title: title,
      mask: true
    })
  },

  // 隐藏加载提示
  hideLoading() {
    if (this.globalData.isLoading) {
      wx.hideLoading()
      this.globalData.isLoading = false
    }
    
    // 清除定时器
    if (this.globalData.loadingTimer) {
      clearTimeout(this.globalData.loadingTimer)
      this.globalData.loadingTimer = null
    }
  },


  // 显示消息提示
  showToast(title, icon = 'none', duration = 2000) {
    wx.showToast({
      title,
      icon,
      duration
    })
  }
})
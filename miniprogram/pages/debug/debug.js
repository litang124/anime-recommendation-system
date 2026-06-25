// pages/debug/debug.js
const apiService = require('../../utils/api.js').apiService

Page({
  data: {
    apiBase: '',
    connectionResult: '',
    error: '',
    testing: false,
    networkInfo: ''
  },

  onLoad() {
    // 获取当前API地址
    const apiBase = require('../../utils/api.js').API_BASE || '未配置'
    this.setData({ apiBase })
    
    // 获取网络状态
    this.getNetworkInfo()
  },

  async testConnection() {
    this.setData({ 
      testing: true,
      connectionResult: '',
      error: ''
    })
    
    try {
      console.log('开始测试服务器连接...')
      
      // 测试基础连接
      const result = await apiService.request('GET', '/test/hello')
      
      this.setData({
        connectionResult: `连接成功！\n状态码：${result.code || 200}\n消息：${result.message || '成功'}`,
        testing: false
      })
      
      console.log('服务器连接测试成功:', result)
      
    } catch (error) {
      console.error('服务器连接测试失败:', error)
      
      this.setData({
        error: `连接失败：\n${error.message || error.errMsg || '未知错误'}\n\n请检查：\n1. 服务器是否运行\n2. 地址：${this.data.apiBase}\n3. 端口是否被占用`,
        testing: false
      })
    }
  },

  getNetworkInfo() {
    wx.getNetworkType({
      success: (res) => {
        this.setData({
          networkInfo: `类型：${res.networkType}`
        })
      }
    })
  },

  clearStorage() {
    wx.clearStorageSync()
    wx.showToast({
      title: '缓存已清除',
      icon: 'success'
    })
    
    // 重新获取API地址
    const apiBase = require('../../utils/api.js').API_BASE || '未配置'
    this.setData({ 
      apiBase,
      connectionResult: '',
      error: ''
    })
  },

  reloadApp() {
    wx.reLaunch({
      url: '/pages/index/index'
    })
  }
})
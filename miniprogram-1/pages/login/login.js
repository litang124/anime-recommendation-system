// pages/login/login.js
const { apiService } = require('../../utils/api.js')

Page({
  data: {
    loading: false,
    canIUseGetUserProfile: false
  },

  onLoad() {
    if (wx.getUserProfile) {
      this.setData({ canIUseGetUserProfile: true })
    }
  },

  async wxLogin() {
    this.setData({ loading: true })
    
    try {
      const app = getApp()
      app.showLoading('登录中...')
      
      // 获取登录code
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject
        })
      })
      
      if (!loginRes.code) {
        throw new Error('获取登录码失败')
      }
      
      console.log('获取到登录code:', loginRes.code)
      
      // 调用后端登录接口
      const result = await apiService.wechatLogin(loginRes.code)
      
      console.log('登录结果:', result)
      
      if (result.code === 200) {
        const { token, user } = result.data
        
        // 保存登录状态
        app.setLoginInfo(token, user)
        
        // 获取用户详细信息（如果需要）
        if (this.data.canIUseGetUserProfile) {
          await this.getUserProfile()
        }
        
        app.showToast('登录成功', 'success', 1500)
        
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' })
        }, 1500)
        
      } else {
        throw new Error(result.message || '登录失败')
      }
      
    } catch (error) {
      console.error('登录失败:', error)
      getApp().showToast(error.message || '登录失败')
    } finally {
      this.setData({ loading: false })
      getApp().hideLoading()
    }
  },

  async getUserProfile() {
    try {
      const userProfile = await new Promise((resolve, reject) => {
        wx.getUserProfile({
          desc: '用于完善用户资料',
          success: resolve,
          fail: reject
        })
      })
      
      const userInfo = wx.getStorageSync('userInfo')
      
      if (userInfo && userInfo.id) {
        // 更新用户信息到后端
        await apiService.updateUserInfo({
          userId: userInfo.id,
          nickname: userProfile.userInfo.nickName,
          avatarUrl: userProfile.userInfo.avatarUrl,
          gender: userProfile.userInfo.gender
        })
        
        // 更新本地存储
        const updatedUser = {
          ...userInfo,
          nickname: userProfile.userInfo.nickName,
          avatarUrl: userProfile.userInfo.avatarUrl || '/images/logo.png'
        }
        
        const app = getApp()
        app.globalData.userInfo = updatedUser
        wx.setStorageSync('userInfo', updatedUser)
      }
      
    } catch (error) {
      console.warn('获取用户信息失败:', error)
    }
  },

  guestLogin() {
    const tempUser = {
      id: 'guest_' + Date.now(),
      nickname: '游客用户',
      avatarUrl: '/images/default-avatar.png',
      isGuest: true
    }
    
    const app = getApp()
    app.globalData.userInfo = tempUser
    wx.setStorageSync('userInfo', tempUser)
    
    app.showToast('游客模式登录', 'success', 1500)
    
    setTimeout(() => {
      wx.switchTab({ url: '/pages/index/index' })
    }, 1500)
  }
})
// pages/settings/settings.js

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    settings: [
      {
        id: 'notification',
        title: '消息通知',
        desc: '管理订阅通知',
        icon: '🔔'
      },
      {
        id: 'privacy',
        title: '隐私设置',
        desc: '管理个人信息和权限',
        icon: '🔒'
      },
      {
        id: 'account',
        title: '账号安全',
        desc: '修改密码和绑定信息',
        icon: '🛡️'
      },
      {
        id: 'appearance',
        title: '外观设置',
        desc: '主题和显示偏好',
        icon: '🎨'
      }
    ]
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    if (this.data.isLoggedIn) {
      this.setData({ userInfo: wx.getStorageSync('userInfo') })
    }
  },

  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ 
        userInfo,
        isLoggedIn: true
      })
    }
  },

  // 跳转到设置详情
  goToSettingDetail(e) {
    const settingId = e.currentTarget.dataset.id
    let url = ''
    
    switch (settingId) {
      case 'notification':
        url = '/pages/settings/notification/notification'
        break
      case 'privacy':
        url = '/pages/settings/privacy/privacy'
        break
      case 'account':
        url = '/pages/settings/account/account'
        break
      case 'appearance':
        url = '/pages/settings/appearance/appearance'
        break
      default:
        wx.showToast({ title: '功能开发中', icon: 'none' })
        return
    }
    
    wx.navigateTo({ url })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出当前账号吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('userInfo')
          wx.removeStorageSync('token')
          this.setData({ 
            isLoggedIn: false,
            userInfo: null 
          })
          wx.showToast({ title: '已退出', icon: 'success' })
          // 返回首页
          wx.reLaunch({ url: '/pages/index/index' })
        }
      }
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '我的设置',
      path: '/pages/settings/settings'
    }
  }
})
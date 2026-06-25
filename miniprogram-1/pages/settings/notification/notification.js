// pages/settings/notification/notification.js

Page({
  data: {
    notifications: [
      {
        id: 1,
        title: '新番更新通知',
        desc: '当关注的动漫有新集数更新时接收通知',
        enabled: true
      },
      {
        id: 2,
        title: '推荐通知',
        desc: '接收个性化推荐的动漫通知',
        enabled: true
      },
      {
        id: 3,
        title: '评论回复通知',
        desc: '当有人回复你的评论时接收通知',
        enabled: false
      },
      {
        id: 4,
        title: '系统通知',
        desc: '接收系统重要更新和公告',
        enabled: true
      }
    ]
  },

  // 切换通知开关
  toggleNotification(e) {
    const index = e.currentTarget.dataset.index
    const notifications = [...this.data.notifications]
    notifications[index].enabled = !notifications[index].enabled
    this.setData({ notifications })
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '消息通知设置',
      path: '/pages/settings/notification/notification'
    }
  }
})
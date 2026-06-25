// pages/recommend/recommend.js
const { apiService } = require('../../utils/api.js')

Page({
  data: {
    userProfile: null,
    recommendations: [],
    loading: true,
    refreshing: false,
    error: null,
    isLoggedIn: false,
    userInfo: null,
    favoriteTags: [],
    favoriteCategories: [],
    stats: { totalViews: 0, avgScore: 0 }
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    if (this.data.isLoggedIn) {
      this.loadRecommendData()
    }
  },

  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    const token = wx.getStorageSync('token')
    
    if (userInfo && token) {
      this.setData({ isLoggedIn: true, userInfo })
      this.loadRecommendData()
    } else {
      this.setData({ isLoggedIn: false, loading: false })
    }
  },

  async loadRecommendData() {
    this.setData({ loading: true, error: null })
    
    try {
      const userId = this.data.userInfo.id
      const [profileRes, recommendRes] = await Promise.all([
        apiService.getUserProfile(userId),
        apiService.generateHybridRecommendations(userId, 10)
      ])

      if (profileRes.code === 200 && profileRes.data) {
        this.processUserProfile(profileRes.data)
      }
      
      if (recommendRes.code === 200 && recommendRes.data) {
        this.setData({
          recommendations: this.processRecommendations(recommendRes.data)
        })
      }
      
      this.setData({ loading: false })
    } catch (error) {
      console.error('加载失败:', error)
      this.setData({
        loading: false,
        error: '加载失败，请重试',
        recommendations: this.getMockData()
      })
    }
  },

  processUserProfile(profile) {
    let favoriteTags = []
    let favoriteCategories = []
    
    try {
      if (profile.favoriteTags) {
        const tagsObj = JSON.parse(profile.favoriteTags)
        favoriteTags = Object.entries(tagsObj)
          .sort((a, b) => b[1] - a[1])
          .slice(0, 5)
          .map(([tag, weight]) => ({ tag, weight: Math.round(weight * 100) }))
      }
      
      if (profile.favoriteCategories) {
        const catObj = JSON.parse(profile.favoriteCategories)
        favoriteCategories = Object.entries(catObj)
          .sort((a, b) => b[1] - a[1])
          .slice(0, 5)
          .map(([cat, weight]) => ({ category: cat, weight: Math.round(weight * 100) }))
      }
    } catch (e) {
      console.warn('解析偏好失败:', e)
    }

    this.setData({
      userProfile: profile,
      favoriteTags,
      favoriteCategories,
      stats: {
        totalViews: profile.totalViews || 0,
        avgScore: profile.avgScore || 0
      }
    })
  },

  processRecommendations(data) {
    return data.map((item, index) => ({
      id: item.id,
      title: item.title || '未知动漫',
      coverUrl: item.coverUrl || `/images/default-cover.png`,
      score: item.score || 0,
      rating: (item.score || 0).toFixed(1),
      tags: item.tags ? item.tags.split(',').slice(0, 3) : [],
      description: item.description || '',
      episodeCount: item.episodeCount || 0,
      status: item.status === 0 ? '连载中' : item.status === 1 ? '已完结' : '未开播',
      heat: item.heat || 0,
      releaseYear: item.releaseYear || '',
      recommendReason: item.recommendReason || '根据您的观看历史和偏好推荐',
      rank: index + 1,
      isTop3: index < 3
    }))
  },

  getMockData() {
    return [
      { id: 3, title: '进击的巨人', coverUrl: '', score: 9.8, rating: '9.8', tags: ['热血', '战斗'], description: '日本漫画家谏山创创作的漫画作品', episodeCount: 75, status: '已完结', heat: 12000, releaseYear: 2013, rank: 1, isTop3: true },
      { id: 5, title: '我的英雄学院', coverUrl: '', score: 8.9, rating: '8.9', tags: ['热血', '校园'], description: '日本漫画家堀越耕平创作的漫画作品', episodeCount: 138, status: '连载中', heat: 6000, releaseYear: 2016, rank: 2, isTop3: true },
      { id: 6, title: '航海王', coverUrl: '', score: 9.7, rating: '9.7', tags: ['热血', '冒险'], description: '尾田荣一郎创作的少年漫画', episodeCount: 1100, status: '连载中', heat: 15000, releaseYear: 1999, rank: 3, isTop3: true }
    ]
  },

  onPullDownRefresh() {
    this.setData({ refreshing: true })
    this.loadRecommendData().then(() => {
      wx.stopPullDownRefresh()
      this.setData({ refreshing: false })
    })
  },

  navigateToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/anime-detail/anime-detail?id=${id}` })
  },

  /**
   * 处理评分事件
   */
  async handleRating(e) {
    const animeId = e.currentTarget.dataset.id;
    const rating = parseFloat(e.currentTarget.dataset.rating);
    
    try {
      // 获取用户信息
      const userInfo = wx.getStorageSync('userInfo');
      if (!userInfo || !userInfo.id) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      // 调用评分接口
      const result = await apiService.rateAnime(userInfo.id, animeId, rating);
      
      if (result.code === 200) {
        wx.showToast({
          title: '评分成功！',
          icon: 'success'
        });
        
        // 刷新推荐列表
        this.loadRecommendData();
      } else {
        wx.showToast({
          title: '评分失败：' + result.message,
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('评分失败:', error);
      wx.showToast({
        title: '评分失败，请重试',
        icon: 'none'
      });
    }
  },

  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
// pages/index/index.js
const { apiService } = require('../../utils/api.js')

Page({
  data: {
    recommendations: [],
    hotAnime: [],
    newSchedules: [],
    loading: true,
    userInfo: null,
    error: null,
    debugInfo: '',
    // 新增分页相关
    currentPage: 0,
    pageSize: 10,
    hasMore: true,
    // banner数据
    banners: [
      { id: 1, image: 'https://via.placeholder.com/750x400/ff6600/ffffff?text=热门新番', url: '/pages/anime-detail/anime-detail?id=1' },
      { id: 2, image: 'https://via.placeholder.com/750x400/4caf50/ffffff?text=正在热播', url: '/pages/anime-detail/anime-detail?id=2' },
      { id: 3, image: 'https://via.placeholder.com/750x400/2196f3/ffffff?text=经典回顾', url: '/pages/anime-detail/anime-detail?id=3' }
    ],
    // 快速导航
    quickNavs: [
      { icon: 'search', text: '搜索', action: 'navigateToSearch' },
      { icon: 'calendar', text: '新番', action: 'navigateToSchedule' },
      { icon: 'message', text: '社区', action: 'navigateToCommunity' },
      { icon: 'star', text: '收藏', action: 'navigateToFavorites' }
    ]
  },

  onLoad() {
    console.log('=== 首页加载开始 ===')
    this.checkLoginStatus()
  },

  onShow() {
    // 每次显示页面时检查登录状态
    this.checkLoginStatus()
  },

  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    console.log('用户信息:', userInfo)
    
    if (userInfo) {
      this.setData({ userInfo })
    }
    
    this.loadData()
  },

  async loadData() {
    try {
      console.log('开始加载首页数据...')
      this.setData({ 
        loading: true, 
        error: null,
        debugInfo: '加载中...'
      })
      
      // 使用Promise.all并发加载
      const [hotRes, scheduleRes, recommendRes] = await Promise.all([
        this.getHotAnimeData(),
        this.getScheduleData(),
        this.getRecommendData()
      ])
      
      this.setData({
        hotAnime: hotRes,
        newSchedules: scheduleRes,
        recommendations: recommendRes,
        loading: false,
        debugInfo: `加载完成：热门${hotRes.length}部，今日新番${scheduleRes.length}部，推荐${recommendRes.length}部`
      })
      
    } catch (error) {
      console.error('数据加载失败:', error)
      this.handleLoadError(error)
    }
  },

  async getHotAnimeData() {
    try {
      const res = await apiService.getHotAnime(0, 8)
      if (res.code === 200 && res.data) {
        return this.processAnimeList(res.data.slice(0, 8))
      }
      return []
    } catch (error) {
      console.warn('热门动漫加载失败:', error)
      return this.getMockHotAnime()
    }
  },

  async getScheduleData() {
    try {
      const res = await apiService.getTodaySchedule()
      let schedules = []
      
      if (res.code === 200 && res.data) {
        schedules = res.data
      }
      
      return this.processSchedules(schedules.slice(0, 5))
    } catch (error) {
      console.warn('日程加载失败:', error)
      return []
    }
  },

  async getRecommendData() {
    const userInfo = this.data.userInfo
    
    try {
      let res
      if (userInfo && userInfo.id && !userInfo.isGuest) {
        console.log('加载个性化推荐，用户ID:', userInfo.id)
        // 使用新的混合推荐接口
        res = await apiService.generateHybridRecommendations(userInfo.id, 6)
      } else {
        console.log('加载最新动漫（游客模式）')
        res = await apiService.getNewAnime(6)
      }
      
      if (res.code === 200 && res.data) {
        return this.processRecommendations(res.data.slice(0, 6))
      }
      return []
    } catch (error) {
      console.warn('推荐加载失败:', error)
      return this.getMockRecommendations()
    }
  },

  processAnimeList(data) {
    return data.map(item => ({
      id: item.id || 0,
      title: item.title || '未知动漫',
      coverUrl: item.coverUrl || this.getPlaceholderImage(item.id),
      score: item.score || 0,
      rating: this.formatRating(item.score),
      heat: item.heat || 0,
      tags: this.formatTags(item.tags),
      status: this.getStatusText(item.status),
      isNew: this.isNewAnime(item.releaseYear)
    }))
  },

  processSchedules(data) {
    const today = new Date().toISOString().split('T')[0]
    
    return data.filter(item => {
      if (!item.airTime) return false
      const itemDate = new Date(item.airTime).toISOString().split('T')[0]
      return itemDate === today
    }).map(item => {
      const anime = item.anime || {}
      return {
        id: item.id || 0,
        animeId: anime.id || 0,
        title: anime.title || '未知动漫',
        cover: anime.coverUrl || this.getPlaceholderImage(anime.id),
        episode: `第${item.episode || 1}话`,
        time: this.formatScheduleTime(item.airTime),
        platform: item.platform || '待定',
        status: item.status || 0
      }
    })
  },

  processRecommendations(data) {
    return data.map(item => {
      const anime = item.anime || item
      return {
        id: anime.id || 0,
        title: anime.title || '未知动漫',
        coverUrl: anime.coverUrl || this.getPlaceholderImage(anime.id),
        score: anime.score || 0,
        reason: item.recommendReason || this.generateRecommendReason(anime),
        tags: this.formatTags(anime.tags, 2)
      }
    })
  },

  formatRating(score) {
    if (!score) return '暂无评分'
    return score.toFixed(1)
  },

  formatTags(tags, limit = 3) {
    if (!tags) return []
    const tagArray = tags.split(',').filter(tag => tag.trim())
    return tagArray.slice(0, limit)
  },

  getStatusText(status) {
    const statusMap = {
      0: '连载中',
      1: '已完结',
      2: '未开播'
    }
    return statusMap[status] || '状态未知'
  },

  isNewAnime(releaseYear) {
    if (!releaseYear) return false
    const currentYear = new Date().getFullYear()
    return currentYear - releaseYear <= 1
  },

  formatScheduleTime(timeStr) {
    if (!timeStr) return '时间待定'
    try {
      const date = new Date(timeStr)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    } catch (e) {
      return timeStr
    }
  },

  generateRecommendReason(anime) {
    if (!anime) return '热门推荐'
    if (anime.score >= 9.0) return '高分神作'
    if (anime.heat >= 10000) return '人气爆棚'
    if (this.isNewAnime(anime.releaseYear)) return '新鲜出炉'
    return '优质推荐'
  },

  handleLoadError(error) {
    const errorMsg = error.message || '网络连接失败'
    this.setData({ 
      loading: false, 
      error: errorMsg,
      debugInfo: '加载失败'
    })
    
    // 加载模拟数据作为降级方案
    setTimeout(() => {
      this.loadMockData()
      wx.showToast({
        title: '使用本地数据',
        icon: 'none',
        duration: 1500
      })
    }, 500)
  },

  loadMockData() {
    const mockData = {
      hotAnime: [
        { id: 1, title: '鬼灭之刃', coverUrl: this.getPlaceholderImage(1), score: 9.5, rating: '9.5', tags: ['热血', '战斗'], status: '已完结', isNew: false },
        { id: 2, title: '咒术回战', coverUrl: this.getPlaceholderImage(2), score: 9.2, rating: '9.2', tags: ['战斗', '校园'], status: '连载中', isNew: true },
        { id: 3, title: '间谍过家家', coverUrl: this.getPlaceholderImage(3), score: 9.1, rating: '9.1', tags: ['喜剧', '家庭'], status: '连载中', isNew: true }
      ],
      recommendations: [
        { id: 4, title: '进击的巨人', coverUrl: this.getPlaceholderImage(4), score: 9.8, reason: '高分神作', tags: ['热血', '悬疑'] },
        { id: 5, title: '葬送的芙莉莲', coverUrl: this.getPlaceholderImage(5), score: 9.3, reason: '新鲜出炉', tags: ['奇幻', '治愈'] }
      ],
      newSchedules: [
        { id: 1, title: '间谍过家家', cover: this.getPlaceholderImage(3), episode: '第25话', time: '20:00', platform: 'B站' }
      ]
    }
    
    this.setData({
      ...mockData,
      error: null,
      debugInfo: '使用模拟数据'
    })
  },

  getMockHotAnime() {
    return Array.from({ length: 8 }, (_, i) => ({
      id: i + 1,
      title: `动漫${i + 1}`,
      coverUrl: this.getPlaceholderImage(i + 1),
      score: 8.5 + Math.random() * 1.5,
      rating: (8.5 + Math.random() * 1.5).toFixed(1),
      tags: ['热血', '战斗', '奇幻'].slice(0, 2),
      status: i % 3 === 0 ? '连载中' : i % 3 === 1 ? '已完结' : '未开播',
      isNew: i < 3
    }))
  },

  getMockRecommendations() {
    return Array.from({ length: 6 }, (_, i) => ({
      id: i + 10,
      title: `推荐动漫${i + 1}`,
      coverUrl: this.getPlaceholderImage(i + 10),
      score: 8.0 + Math.random() * 2.0,
      reason: ['高分神作', '人气爆棚', '新鲜出炉', '优质推荐'][i % 4],
      tags: ['热血', '战斗', '奇幻', '治愈', '喜剧'].slice(0, 2)
    }))
  },

  getPlaceholderImage(id) {
    const colors = ['ff6600', '4caf50', '2196f3', '9c27b0', 'ff9800', 'e91e63']
    const color = colors[id % colors.length]
    return `https://via.placeholder.com/300x400/${color}/ffffff?text=动漫${id}`
  },

  // ========== 交互方法 ==========
  onPullDownRefresh() {
    console.log('下拉刷新...')
    wx.showNavigationBarLoading()
    
    this.setData({
      currentPage: 0,
      hasMore: true
    })
    
    this.loadData()
    
    setTimeout(() => {
      wx.stopPullDownRefresh()
      wx.hideNavigationBarLoading()
      wx.showToast({ title: '刷新成功', icon: 'success' })
    }, 1000)
  },

  onReachBottom() {
    if (!this.data.hasMore || this.data.loading) return
    
    console.log('加载更多...')
    this.loadMoreData()
  },

  async loadMoreData() {
    this.setData({ loading: true })
    
    try {
      const nextPage = this.data.currentPage + 1
      const res = await apiService.getAnimeList(nextPage, this.data.pageSize)
      
      if (res.code === 200 && res.data && res.data.length > 0) {
        const newAnime = this.processAnimeList(res.data)
        this.setData({
          hotAnime: [...this.data.hotAnime, ...newAnime],
          currentPage: nextPage,
          hasMore: res.data.length === this.data.pageSize
        })
      } else {
        this.setData({ hasMore: false })
      }
    } catch (error) {
      console.error('加载更多失败:', error)
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  navigateToDetail(e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({
        url: `/pages/anime-detail/anime-detail?id=${id}`
      })
    }
  },

  navigateToSearch() {
    wx.navigateTo({
      url: '/pages/search/search'
    })
  },

  navigateToSchedule() {
    wx.switchTab({
      url: '/pages/schedule/schedule'
    })
  },

  navigateToCommunity() {
    wx.switchTab({
      url: '/pages/community/community'
    })
  },

  navigateToFavorites() {
    wx.navigateTo({
      url: '/pages/favorites/favorites'
    })
  },

  quickNavAction(e) {
    const action = e.currentTarget.dataset.action
    if (action && typeof this[action] === 'function') {
      this[action]()
    }
  },

  handleBannerClick(e) {
    const url = e.currentTarget.dataset.url
    if (url) {
      wx.navigateTo({ url })
    }
  },

  refreshData() {
    wx.showLoading({ title: '刷新中...' })
    this.loadData()
    setTimeout(() => {
      wx.hideLoading()
    }, 1500)
  },

  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  navigateToProfile() {
    wx.navigateTo({
      url: '/pages/profile/profile'
    })
  },

  testApiConnection() {
    wx.navigateTo({
      url: '/pages/test-network/test-network'
    })
  }
})
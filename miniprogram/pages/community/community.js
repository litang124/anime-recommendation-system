// pages/community/community.js
const { apiService } = require('../../utils/api')

Page({
  data: {
    // 分类
    categories: [
      { id: 'hot', name: '热门', count: 0 },
      { id: 'new', name: '最新', count: 0 },
      { id: 'following', name: '我的', count: 0 }
    ],
    activeCategory: 'hot',
    
    // 评论数据
    reviews: [],
    hotAnime: [],
    recommendations: [],
    personalizedReviews: [],
    
    // 分页
    page: 0,
    pageSize: 10,
    hasMore: true,
    loading: false,
    
    // 统计
    stats: {
      totalReviews: 0,
      avgRating: 0,
      activeUsers: 0
    },
    
    // 用户信息
    userInfo: null,
    isLoggedIn: false
  },

  onLoad() {
    this.checkLoginStatus()
    this.loadCommunityData()
  },

  onShow() {
    this.checkLoginStatus()
  },

  // 检查登录状态
  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    this.setData({ 
      userInfo,
      isLoggedIn: !!userInfo
    })
  },

  // 加载社区数据
  async loadCommunityData() {
    this.setData({ loading: true })
    
    try {
      // 【新逻辑】加载所有有评论的动漫的前2条高赞评论（anime_id 1-38）
      const animeIds = Array.from({ length: 38 }, (_, i) => i + 1)
      const promises = animeIds.map(id => 
        apiService.getAnimeReviews(id, 2).catch(() => ({ code: 500, data: [] }))
      )
      
      const results = await Promise.all(promises)
      let allReviews = []
      
      results.forEach(res => {
        if (res.code === 200 && res.data && Array.isArray(res.data)) {
          allReviews = allReviews.concat(res.data)
        }
      })
      
      // 去重（按 review.id）
      const seenIds = new Set()
      const uniqueReviews = allReviews.filter(review => {
        if (seenIds.has(review.id)) return false
        seenIds.add(review.id)
        return true
      })
      
      // 按 likeCount 降序排列，取前20条
      const sortedReviews = uniqueReviews.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0)).slice(0, 20)
      
      let reviews = []
      if (sortedReviews.length > 0) {
        // 提取 animeId 并批量获取动漫信息
        const animeIdsFromReviews = [...new Set(sortedReviews.map(r => r.animeId))]
        const animeMap = await this.fetchAnimeBatch(animeIdsFromReviews)
        
        // 注入 anime 对象
        const enrichedReviews = sortedReviews.map(review => ({
          ...review,
          anime: animeMap[review.animeId] || {
            id: review.animeId,
            title: '未知动漫',
            coverUrl: '/images/default-cover.png',
            score: 0
          }
        }))
        
        reviews = this.processReviews(enrichedReviews)
      }
      
      // 加载个性化推荐评论
      let personalizedReviews = []
      if (this.data.isLoggedIn && this.data.userInfo && this.data.userInfo.id) {
        const personalizedRes = await apiService.getPersonalizedCommunityReviews(this.data.userInfo.id)
        if (personalizedRes.code === 200 && personalizedRes.data) {
          personalizedReviews = this.processReviews(personalizedRes.data)
        }
      }
      
      // 加载个性化推荐（为你推荐）
      let recommendations = []
      if (this.data.isLoggedIn && this.data.userInfo && this.data.userInfo.id) {
        recommendations = await this.getPersonalizedRecommendations(this.data.userInfo.id, 5)
      }
      
      // 按 animeId 分组，每组取前2条高赞评论
      const grouped = {}
      reviews.forEach(review => {
        const id = review.animeId
        if (!grouped[id]) grouped[id] = []
        grouped[id].push(review)
      })
      
      const topReviews = []
      Object.values(grouped).forEach(group => {
        group.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
        topReviews.push(...group.slice(0, 2))
      })
      
      reviews = topReviews
      
      // 从本地缓存恢复 liked 状态（用户级持久化）
      const userLikes = wx.getStorageSync('userLikes') || {}
      reviews = reviews.map(review => ({
        ...review,
        liked: userLikes[review.id] !== undefined ? userLikes[review.id] : review.liked
      }))
      
      // 计算统计
      const stats = this.calculateStats(reviews)
      
      this.setData({
        reviews,
        recommendations,
        personalizedReviews,
        stats,
        loading: false
      })
      
    } catch (error) {
      console.error('加载社区数据失败:', error)
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  // 处理评论数据
  processReviews(reviews) {
    return reviews.map(review => {
      const user = review.user || {}
      const anime = review.anime || {}
      
      return {
        id: review.id,
        userId: review.userId,
        animeId: review.animeId,
        rating: review.rating || 0,
        content: review.content || '',
        likeCount: review.likeCount || 0,
        createTime: this.formatTime(review.createTime),
        liked: review.liked !== undefined ? review.liked : false,
        recommendReason: review.recommendReason || undefined,
        isHot: (review.likeCount || 0) > 10,
        
        user: {
          id: user.id,
          nickname: review.userId === this.data.userInfo?.id 
            ? '我' 
            : user.nickname || '匿名用户',
          avatarUrl: user.avatarUrl || '/images/default-avatar.png'
        },
        
        anime: {
          id: anime.id,
          title: anime.title || '未知动漫',
          coverUrl: anime.coverUrl || '/images/default-cover.png',
          score: anime.score || 0
        }
      }
    })
  },

  // 获取个性化推荐（基于用户画像）- 社区页面简化版，不显示推荐理由
  async getPersonalizedRecommendations(userId, limit = 5) {
    try {
      const res = await apiService.generateHybridRecommendations(userId, limit)
      if (res.code === 200 && res.data) {
        return res.data.map(item => ({
          id: item.id,
          title: item.title,
          coverUrl: item.coverUrl || '/images/default-cover.png',
          score: item.score || 0
          // 不传递 recommendReason，社区页面不显示
        }))
      }
    } catch (e) {
      console.warn('获取个性化推荐失败:', e)
    }
    return []
  },

  // 批量获取动漫信息
  async fetchAnimeBatch(animeIds) {
    if (!animeIds || animeIds.length === 0) return {}
    
    const promises = animeIds.map(id => 
      apiService.getAnime(id).catch(() => ({ code: 500, data: null }))
    )
    
    const results = await Promise.all(promises)
    const animeMap = {}
    
    animeIds.forEach((id, index) => {
      const res = results[index]
      if (res.code === 200 && res.data) {
        animeMap[id] = res.data
      } else {
        animeMap[id] = {
          id,
          title: '未知动漫',
          coverUrl: '/images/default-cover.png',
          score: 0
        }
      }
    })
    
    return animeMap
  },

  // 计算统计
  calculateStats(reviews) {
    if (reviews.length === 0) {
      return { totalReviews: 0, avgRating: 0, activeUsers: 0 }
    }
    
    const totalRating = reviews.reduce((sum, r) => sum + r.rating, 0)
    const uniqueUsers = new Set(reviews.map(r => r.userId)).size
    
    return {
      totalReviews: reviews.length,
      avgRating: (totalRating / reviews.length).toFixed(1),
      activeUsers: uniqueUsers
    }
  },

  // 格式化时间
  formatTime(timeArray) {
    if (!timeArray || !Array.isArray(timeArray)) return '刚刚'
    
    const [year, month, day, hour, minute] = timeArray
    const date = new Date(year, month - 1, day, hour, minute)
    const now = new Date()
    const diff = now - date
    
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
    if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
    
    return `${month}-${day}`
  },

  // 切换分类
  selectCategory(e) {
    const categoryId = e.currentTarget.dataset.id
    if (this.data.activeCategory === categoryId) return
    
    // "我的"分类需要登录
    if (categoryId === 'following' && !this.data.isLoggedIn) {
      wx.showModal({
        title: '提示',
        content: '需要登录后才能查看我的评论',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' })
          }
        }
      })
      return
    }
    
    this.setData({ 
      activeCategory: categoryId,
      page: 0,
      hasMore: true
    })
    
    // 重新加载数据
    if (categoryId === 'following') {
      this.loadMyReviews()
    } else if (categoryId === 'new') {
      this.loadLatestReviews()
    } else {
      this.loadCommunityData()
    }
  },

  // 加载我的评论
  async loadMyReviews() {
    if (!this.data.userInfo) return
    
    this.setData({ loading: true })
    
    try {
      const res = await apiService.getUserReviews(this.data.userInfo.id)
      let reviews = []
      if (res.code === 200 && res.data) {
        // 提取 animeId 并批量获取动漫信息
        const animeIds = [...new Set(res.data.map(r => r.animeId))]
        const animeMap = await this.fetchAnimeBatch(animeIds)
        
        // 注入 anime 对象
        const enrichedReviews = res.data.map(review => ({
          ...review,
          anime: animeMap[review.animeId] || {
            id: review.animeId,
            title: '未知动漫',
            coverUrl: '/images/default-cover.png',
            score: 0
          }
        }))
        
        reviews = this.processReviews(enrichedReviews)
      }
      
      this.setData({
        reviews,
        loading: false
      })
    } catch (error) {
      console.error('加载我的评论失败:', error)
      this.setData({ loading: false })
    }
  },
  
  // 加载最新评论（全部，按时间倒序）
  async loadLatestReviews() {
    this.setData({ loading: true })
    
    try {
      const res = await apiService.request('GET', '/review/latest?page=0&size=100')
      let reviews = []
      if (res.code === 200 && res.data) {
        console.log('【DEBUG】最新评论第一条:', res.data[0])
        
        // 按 createTime 降序排序（最新在最上）
        const sortedData = res.data.sort((a, b) => {
          const dateA = new Date(...a.createTime)
          const dateB = new Date(...b.createTime)
          return dateB - dateA
        })
        
        // 提取 animeId 并批量获取动漫信息
        const animeIds = [...new Set(sortedData.map(r => r.animeId))]
        const animeMap = await this.fetchAnimeBatch(animeIds)
        
        // 注入 anime 对象
        const enrichedReviews = sortedData.map(review => ({
          ...review,
          anime: animeMap[review.animeId] || {
            id: review.animeId,
            title: '未知动漫',
            coverUrl: '/images/default-cover.png',
            score: 0
          }
        }))
        
        reviews = this.processReviews(enrichedReviews)
      }
      
      this.setData({
        reviews,
        loading: false
      })
    } catch (error) {
      console.error('加载最新评论失败（/review/latest 500）:', error)
      
      // Fallback：遍历 anime_id 1-38，拉取每部动漫的全部评论，合并后全局按时间排序
      try {
        console.log('【FALLBACK】开始拉取 anime_id 1-38 的全部评论...')
        
        // 生成 anime_id 数组：1 到 38
        const animeIds = Array.from({ length: 38 }, (_, i) => i + 1)
        
        // 并行拉取每部动漫的全部评论（无分页）
        const promises = animeIds.map(id => 
          apiService.request('GET', `/review/anime/${id}`).catch(e => ({ code: 500, data: [] }))
        )
        
        const results = await Promise.all(promises)
        
        // 合并所有评论
        let allReviews = []
        results.forEach(res => {
          if (res.code === 200 && Array.isArray(res.data)) {
            allReviews = allReviews.concat(res.data)
          }
        })
        
        console.log('【DEBUG】fallback 合并后 allReviews.length:', allReviews.length)
        if (allReviews.length > 0) {
          console.log('【DEBUG】allReviews[0]?.createTime:', allReviews[0]?.createTime)
          console.log('【DEBUG】allReviews[1]?.createTime:', allReviews[1]?.createTime)
        }
        
        // ✅ 全局按 createTime 降序排序（兼容字符串格式）
        allReviews.sort((a, b) => {
          const dateA = a.createTime ? new Date(...a.createTime) : new Date(0)
          const dateB = b.createTime ? new Date(...b.createTime) : new Date(0)
          return dateB - dateA
        })
        
        // 取前 100 条
        const top100 = allReviews.slice(0, 100)
        
        // 批量注入 anime 对象
        const uniqueAnimeIds = [...new Set(top100.map(r => r.animeId))]
        const animeMap = await this.fetchAnimeBatch(uniqueAnimeIds)
        
        const enrichedReviews = top100.map(review => ({
          ...review,
          anime: animeMap[review.animeId] || {
            id: review.animeId,
            title: '未知动漫',
            coverUrl: '/images/default-cover.png',
            score: 0
          }
        }))
        
        const reviews = this.processReviews(enrichedReviews)
        
        this.setData({
          reviews,
          loading: false
        })
        
        console.log(`【FALLBACK SUCCESS】共加载 ${reviews.length} 条最新评论`)
      } catch (fallbackError) {
        console.error('Fallback 加载失败:', fallbackError)
        this.setData({ loading: false })
      }
    }
  },

  // 点赞评论
  async likeReview(e) {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    const reviewId = e.currentTarget.dataset.id
    const review = this.data.reviews.find(r => r.id === reviewId)
    if (!review) {
      wx.showToast({ title: '评论不存在', icon: 'none' })
      return
    }

    const res = review.liked 
      ? await apiService.unlikeReview(reviewId) 
      : await apiService.likeReview(reviewId)

    // 无论后端是否成功，都更新本地状态（用户意图优先）
    const reviews = this.data.reviews.map(r => {
      if (r.id === reviewId) {
        return {
          ...r,
          liked: !r.liked,
          likeCount: r.liked ? Math.max(0, r.likeCount - 1) : r.likeCount + 1
        }
      }
      return r
    })

    this.setData({ reviews })

    // 同步更新本地点赞缓存（用户级持久化）
    const userLikes = wx.getStorageSync('userLikes') || {}
    userLikes[reviewId] = !review.liked
    wx.setStorageSync('userLikes', userLikes)

    if (res.code === 200) {
      wx.showToast({ 
        title: review.liked ? '取消点赞' : '点赞成功', 
        icon: 'none' 
      })
    } else if (res.code === 400) {
      wx.showToast({ 
        title: review.liked ? '已无点赞，无需取消' : '已点赞过，无法重复点赞', 
        icon: 'none' 
      })
    } else {
      wx.showToast({ 
        title: '服务繁忙，请稍后重试', 
        icon: 'none' 
      })
    }
  },

  // 跳转到评论详情（当前跳转至关联动漫）
  viewReviewDetail(e) {
    const reviewId = e.currentTarget.dataset.id
    const review = this.data.reviews.find(r => r.id === reviewId)
    if (review && review.anime && review.anime.id) {
      wx.navigateTo({
        url: `/pages/anime-detail/anime-detail?id=${review.anime.id}`
      })
    } else {
      wx.showToast({ title: '动漫信息缺失', icon: 'none' })
    }
  },

  // 跳转到动漫详情
  goToAnimeDetail(e) {
    const animeId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/anime-detail/anime-detail?id=${animeId}`
    })
  },

  // 写评论
  createReview() {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    
    wx.showToast({ title: '请选择动漫进行评分', icon: 'none' })
    // 可以跳转到动漫列表页
    setTimeout(() => {
      wx.switchTab({ url: '/pages/index/index' })
    }, 1500)
  },

  // 跳转到推荐页
  goToRecommendPage() {
    wx.navigateTo({
      url: '/pages/recommend/recommend'
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({ page: 0, hasMore: true })
    this.loadCommunityData().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 加载更多
  async loadMore() {
    // 简化处理，实际应该实现分页加载
    wx.showToast({ title: '没有更多了', icon: 'none' })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '动漫评分社区 - 发现优质评价',
      path: '/pages/community/community'
    }
  }
})

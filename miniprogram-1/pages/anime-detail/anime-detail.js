// pages/anime-detail/anime-detail.js
const { apiService } = require('../../utils/api.js')

Page({
  data: {
    // 动漫信息
    anime: null,
    // 加载状态
    loading: true,
    // 错误信息
    error: null,
    // 是否已登录
    isLoggedIn: false,
    // 用户信息
    userInfo: null,
    // 评论列表
    reviews: [],
    // 评论分页
    reviewPage: 0,
    hasMoreReviews: true,
    // 评分弹窗
    showRatingModal: false,
    userRating: 0,
    userComment: '',
    // 观看记录
    watchRecord: null,
    // 当前集数
    currentEpisode: 0,
    // 总集数
    totalEpisodes: 0
  },

  onLoad(options) {
    console.log('=== 动漫详情页加载 ===', options)
    const animeId = options.id
    if (!animeId) {
      this.setData({ error: '动漫ID不能为空', loading: false })
      return
    }
    
    this.setData({ animeId: parseInt(animeId) })
    this.checkLoginStatus()
    this.loadAnimeDetail()
  },

  onShow() {
    // 刷新观看记录
    if (this.data.animeId && this.data.isLoggedIn) {
      this.loadWatchRecord()
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    const token = wx.getStorageSync('token')
    
    if (userInfo && token) {
      this.setData({ 
        isLoggedIn: true,
        userInfo: userInfo
      })
    }
  },

  // 加载动漫详情
  async loadAnimeDetail() {
    this.setData({ loading: true, error: null })
    
    try {
      const animeId = this.data.animeId
      
      // 并行加载动漫详情、评论、观看记录
      const promises = [
        apiService.getAnime(animeId),
        this.loadReviews(animeId, 0),
      ]
      
      // 如果已登录，同时加载观看记录
      if (this.data.isLoggedIn) {
        promises.push(this.loadWatchRecord())
      }
      
      const [animeRes] = await Promise.all(promises)
      
      if (animeRes.code === 200 && animeRes.data) {
        const anime = this.processAnimeData(animeRes.data)
        this.setData({
          anime: anime,
          totalEpisodes: anime.episodeCount || 0,
          loading: false
        })
        
        // 更新页面标题
        wx.setNavigationBarTitle({
          title: anime.title || '动漫详情'
        })
      } else {
        throw new Error(animeRes.message || '获取动漫详情失败')
      }
    } catch (error) {
      console.error('加载动漫详情失败:', error)
      this.setData({
        loading: false,
        error: error.message || '加载失败，请重试'
      })
    }
  },

  // 处理动漫数据
  processAnimeData(data) {
    const episodeCount = data.episodeCount || 0
    return {
      id: data.id,
      title: data.title || '未知动漫',
      coverUrl: data.coverUrl || '/images/default-cover.png',
      description: data.description || '暂无简介',
      score: data.score || 0,
      rating: (data.score || 0).toFixed(1),
      episodeCount: episodeCount,
      status: data.status === 0 ? '连载中' : data.status === 1 ? '已完结' : '未开播',
      statusCode: data.status,
      tags: data.tags ? data.tags.split(',').filter(tag => tag.trim()) : [],
      heat: data.heat || 0,
      releaseYear: data.releaseYear || '',
      createTime: data.createTime,
      episodeRange: this.generateEpisodeRange(episodeCount),
      progressPercent: episodeCount > 0 ? (this.data.currentEpisode / episodeCount * 100) : 0
    }
  },

  // 生成集数选择范围
  generateEpisodeRange(totalEpisodes) {
    const range = []
    for (let i = 0; i <= totalEpisodes; i++) {
      range.push(`第 ${i} 集`)
    }
    return range
  },

  // 加载评论列表
  async loadReviews(animeId, page = 0) {
    try {
      const res = await apiService.getAnimeReviews(animeId, page, 10)
      
      if (res.code === 200 && res.data) {
        let newReviews = this.processReviews(res.data.content || res.data)
        
        // 从本地缓存恢复 liked 状态（用户级持久化）
        const userLikes = wx.getStorageSync('userLikes') || {}
        newReviews = newReviews.map(review => ({
          ...review,
          liked: userLikes[review.id] !== undefined ? userLikes[review.id] : review.liked
        }))
        
        this.setData({
          reviews: page === 0 ? newReviews : [...this.data.reviews, ...newReviews],
          reviewPage: page,
          hasMoreReviews: newReviews.length === 10
        })
      }
    } catch (error) {
      console.warn('加载评论失败:', error)
    }
  },

  // 处理评论数据
  processReviews(data) {
    return data.map(item => ({
      id: item.id,
      content: item.content || '',
      rating: item.rating || 0,
      username: item.userId === this.data.userInfo?.id ? '我' : (item.username || '匿名用户'),
      avatar: item.avatar || '/images/default-avatar.png',
      createTime: this.formatTime(item.createTime),
      likeCount: item.likeCount || 0,
      liked: item.liked !== undefined ? item.liked : false
    }))
  },

  // 加载观看记录
  async loadWatchRecord() {
    try {
      const userId = this.data.userInfo.id
      const animeId = this.data.animeId
      
      // 检查是否有观看记录
      const res = await apiService.checkWatchRecord(userId, animeId)
      
      if (res.code === 200 && res.data) {
        this.setData({
          watchRecord: res.data,
          currentEpisode: res.data.currentEpisode || 0
        })
      }
    } catch (error) {
      console.warn('加载观看记录失败:', error)
    }
  },

  // 显示评分弹窗
  showRatingModal() {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    this.setData({ showRatingModal: true, userRating: 0, userComment: '' })
  },

  // 隐藏评分弹窗
  hideRatingModal() {
    this.setData({ showRatingModal: false })
  },

  // 选择评分
  selectRating(e) {
    const rating = parseInt(e.currentTarget.dataset.rating)
    this.setData({ userRating: rating })
  },

  // 输入评论
  onCommentInput(e) {
    this.setData({ userComment: e.detail.value })
  },

  // 提交评分和评论
  async submitRating() {
    const { userRating, userComment, animeId, userInfo } = this.data
    
    if (userRating === 0) {
      wx.showToast({ title: '请选择评分', icon: 'none' })
      return
    }
    
    try {
      wx.showLoading({ title: '提交中...' })
      
      // 提交评论（包含评分）
      const res = await apiService.createReview({
        animeId: animeId,
        userId: userInfo.id,
        content: userComment || '用户评分',
        rating: userRating
      })
      
      wx.hideLoading()
      
      if (res.code === 200) {
        wx.showToast({ title: '评分成功', icon: 'success' })
        this.hideRatingModal()
        // 刷新评论列表
        this.loadReviews(animeId, 0)
        // 刷新动漫详情（更新平均分）
        this.loadAnimeDetail()
      } else {
        throw new Error(res.message || '提交失败')
      }
    } catch (error) {
      wx.hideLoading()
      wx.showToast({ title: error.message || '提交失败', icon: 'none' })
    }
  },

  // 点赞评论
  async likeReview(e) {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    const reviewId = e.currentTarget.dataset.id
    const review = this.data.reviews.find(item => item.id === reviewId)
    if (!review) {
      wx.showToast({ title: '评论不存在', icon: 'none' })
      return
    }

    const res = review.isLiked 
      ? await apiService.unlikeReview(reviewId) 
      : await apiService.likeReview(reviewId)

    // 更新本地状态（用户意图优先）
    const reviews = this.data.reviews.map(item => {
      if (item.id === reviewId) {
        return {
          ...item,
          isLiked: !item.isLiked,
          likeCount: item.isLiked ? Math.max(0, item.likeCount - 1) : item.likeCount + 1
        }
      }
      return item
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

  // 更新观看进度
  async updateProgress(e) {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    
    const episode = parseInt(e.detail.value)
    const { animeId, userInfo, watchRecord, anime } = this.data
    
    try {
      let res
      
      if (watchRecord) {
        // 更新现有记录
        res = await apiService.updateWatchProgress(watchRecord.id, {
          currentEpisode: episode,
          status: episode >= anime.episodeCount ? 2 : 1
        })
      } else {
        // 创建新记录
        res = await apiService.createWatchRecord({
          userId: userInfo.id,
          animeId: animeId,
          currentEpisode: episode,
          status: episode >= anime.episodeCount ? 2 : 1
        })
      }
      
      if (res.code === 200) {
        const progressPercent = anime.episodeCount > 0 ? (episode / anime.episodeCount * 100) : 0
        this.setData({
          currentEpisode: episode,
          watchRecord: res.data || { ...watchRecord, currentEpisode: episode },
          'anime.progressPercent': progressPercent
        })
        wx.showToast({ title: '进度已更新', icon: 'success' })
      }
    } catch (error) {
      wx.showToast({ title: '更新失败', icon: 'none' })
    }
  },

  // 分享
  onShareAppMessage() {
    const { anime } = this.data
    return {
      title: `推荐动漫：${anime.title}`,
      path: `/pages/anime-detail/anime-detail?id=${anime.id}`,
      imageUrl: anime.coverUrl
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadAnimeDetail().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 加载更多评论
  onReachBottom() {
    if (this.data.hasMoreReviews) {
      this.loadReviews(this.data.animeId, this.data.reviewPage + 1)
    }
  },

  // 格式化时间
  formatTime(timeArray) {
    if (!timeArray || !Array.isArray(timeArray)) return '未知时间'
    const [year, month, day] = timeArray
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  },

  // 去登录
  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  // 阻止事件冒泡
  preventHide() {
    // 什么都不做，只是阻止冒泡
  }
})
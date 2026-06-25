// pages/profile/profile.js
const { apiService } = require('../../utils/api')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    activeTab: 'planned', // planned, reviews → 仅保留 planned 和 reviews
    reviews: [],
    wishListAnimes: [],
    statistics: {
      planToWatch: 0
    },
    loading: false,
    // 新增：用户画像数据
    userProfile: null,
    favoriteCategories: [] // [{ name: '热血', value: 0.25 }, ...]
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    // 每次显示页面时刷新数据
    if (this.data.isLoggedIn) {
      this.loadUserData()
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ 
        userInfo,
        isLoggedIn: true
      })
      this.loadUserData()
    }
  },

  // 加载用户数据
  async loadUserData() {
    this.setData({ loading: true })
    
    try {
      const { userInfo } = this.data
      
      // 并行加载评论、用户画像
      const [reviewsRes, profileRes] = await Promise.all([
        apiService.getUserReviews(userInfo.id),
        apiService.getUserProfile(userInfo.id)
      ])
      
      // 处理评论
      let reviews = []
      if (reviewsRes.code === 200 && reviewsRes.data) {
        // 先处理评论基础数据
        const basicReviews = this.processReviews(reviewsRes.data)
        
        // 为每个评论加载对应的动漫信息
        for (const review of basicReviews) {
          try {
            const animeRes = await apiService.getAnime(review.animeId)
            if (animeRes.code === 200 && animeRes.data) {
              review.animeTitle = animeRes.data.title || '未知动漫'
              review.animeCover = animeRes.data.coverUrl || '/images/default-cover.png'
              review.episodeCount = animeRes.data.episodeCount || 0
            }
          } catch (e) {
            console.warn('加载评论对应动漫失败:', review.animeId, e)
          }
        }
        
        reviews = basicReviews
      }
      
      // 处理用户画像（favoriteCategories）
      let favoriteCategories = []
      if (profileRes.code === 200 && profileRes.data?.favoriteCategories) {
        try {
          const categoriesObj = JSON.parse(profileRes.data.favoriteCategories)
          // 转为数组并按值降序
          favoriteCategories = Object.entries(categoriesObj)
            .map(([name, value]) => ({ 
              name, 
              value: parseFloat(value),
              percent: Math.round(parseFloat(value) * 100) // ← 新增 percent
            }))
            .sort((a, b) => b.value - a.value)
            .slice(0, 3)
        } catch (e) {
          console.error('解析 favoriteCategories 失败:', e)
        }
      }
      
      // 计算统计
      const statistics = {
        planToWatch: wx.getStorageSync('wishList')?.length || 0,
        commentsCount: reviews.length,
        interestTagsCount: favoriteCategories.length
      }
      
      // 加载想看清单中的动漫
      let wishListAnimes = []
      const wishList = wx.getStorageSync('wishList') || []
      if (wishList.length > 0) {
        for (const animeId of wishList) {
          try {
            const res = await apiService.getAnime(animeId)
            if (res.code === 200 && res.data) {
              wishListAnimes.push(res.data)
            }
          } catch (e) {
            console.warn('加载想看动漫失败:', animeId, e)
          }
        }
      }
      
      this.setData({
        reviews,
        wishListAnimes,
        statistics,
        favoriteCategories,
        loading: false
      })
      
    } catch (error) {
      console.error('加载用户数据失败:', error)
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  // 处理评论
  processReviews(reviews) {
    return reviews.map(review => ({
      id: review.id,
      animeId: review.animeId,
      content: review.content || '',
      rating: review.rating || 0,
      likeCount: review.likeCount || 0,
      createTime: this.formatTime(review.createTime)
    }))
  },

  // 格式化时间
  formatTime(timeArray) {
    if (!timeArray || !Array.isArray(timeArray)) return ''
    const [year, month, day] = timeArray
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  },

  // 切换标签
  switchTab(e) {
    // 仅允许切换到 'planned' 或 'reviews'
    const tab = e.currentTarget.dataset.tab
    if (tab === 'planned' || tab === 'reviews') {
      this.setData({ activeTab: tab })
    }
  },

  // 获取当前标签的数据
  getCurrentTabData() {
    const { activeTab, watchRecords, reviews, wishListAnimes } = this.data
    
    switch (activeTab) {
      case 'planned':
        return wishListAnimes
      case 'reviews':
        return reviews
      default:
        return []
    }
  },

  // 跳转到动漫详情
  goToAnimeDetail(e) {
    const animeId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/anime-detail/anime-detail?id=${animeId}`
    })
  },

  // 跳转到动漫详情（从评论列表点击）
  goToReviewDetail(e) {
    const reviewId = e.currentTarget.dataset.id
    
    // 找到对应的评论
    const review = this.data.reviews.find(r => r.id === reviewId)
    if (!review || !review.animeId) {
      wx.showToast({ title: '无法找到对应动漫', icon: 'none' })
      return
    }
    
    // 跳转到动漫详情页
    wx.navigateTo({
      url: `/pages/anime-detail/anime-detail?id=${review.animeId}`
    })
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

    try {
      const res = review.liked 
        ? await apiService.unlikeReview(reviewId) 
        : await apiService.likeReview(reviewId)

      // 更新本地状态
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

      if (res.code === 200) {
        wx.showToast({ 
          title: review.liked ? '取消点赞' : '点赞成功', 
          icon: 'none' 
        })
      }
    } catch (error) {
      wx.showToast({ title: '操作失败', icon: 'none' })
    }
  },



  // 去登录
  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  // 去设置
  goToSettings() {
    wx.navigateTo({ url: '/pages/settings/settings' })
  },

  // 下拉刷新
  onPullDownRefresh() {
    if (this.data.isLoggedIn) {
      this.loadUserData().then(() => {
        wx.stopPullDownRefresh()
      })
    } else {
      wx.stopPullDownRefresh()
    }
  },

  // 分享
  onShareAppMessage() {
    return {
      title: `${this.data.userInfo?.nickname || '我'}的动漫中心`,
      path: '/pages/profile/profile'
    }
  }
})
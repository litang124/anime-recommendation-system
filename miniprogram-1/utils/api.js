const getApiBase = () => {
  // 获取系统信息判断环境
  const systemInfo = wx.getSystemInfoSync()
  console.log('系统信息:', systemInfo)
  
  if (systemInfo.platform === 'devtools') {
    // 开发者工具中使用 localhost
    return 'http://localhost:8080/api'
  } else {
    // 真机调试使用局域网IP
    // 实际局域网IP
    return 'http://192.168.230.95:8080/api'
  }
}

const API_BASE = getApiBase()
console.log('📡 API地址:', API_BASE)

class ApiService {
  constructor() {
    this.token = wx.getStorageSync('token')
    console.log('🔑 初始化ApiService，token:', this.token ? '有' : '无')
  }
  
  setToken(token) {
    this.token = token
    console.log('设置token:', token)
  }
  
  setToken(token) {
    this.token = token
    console.log('设置token:', token)
  }
  
  async request(method, endpoint, data = {}, headers = {}) {
    const url = `${API_BASE}${endpoint}`
    console.log(`🚀 发起请求: ${method} ${url}`, data)
    
    const requestHeaders = {
      'Content-Type': 'application/json',
      ...headers
    }
    
    if (this.token) {
      requestHeaders['Authorization'] = `Bearer ${this.token}`
    }
    
    console.log('请求头:', requestHeaders)
    
    return new Promise((resolve, reject) => {
      wx.request({
        url,
        method,
        data,
        header: requestHeaders,
        success: (res) => {
          console.log(`✅ 请求成功 ${endpoint}:`, {
            statusCode: res.statusCode,
            data: res.data,
            headers: res.header
          })
          
          if (res.statusCode >= 200 && res.statusCode < 300) {
            resolve(res.data)
          } else {
            console.error(`❌ 请求失败 ${endpoint}:`, res)
            reject(new Error(res.data?.message || `HTTP ${res.statusCode}`))
          }
        },
        fail: (err) => {
          console.error(`❌ 请求错误 ${endpoint}:`, err)
          reject(err)
        }
      })
    })
  }
  
  // 测试连接
  async testConnection() {
    try {
      console.log('测试服务器连接...')
      const result = await this.request('GET', '/test/hello')
      console.log('服务器连接测试结果:', result)
      return result
    } catch (error) {
      console.error('服务器连接失败:', error)
      throw error
    }
  }
  
  async getAnime(id) {
    return this.request('GET', `/anime/${id}`)
  }
  
  async getAnimeList(page = 0, size = 20) {
    return this.request('GET', `/anime/list?page=${page}&size=${size}`)
  }
  
  async getHotAnime(page = 0, size = 10) {
    // 根据测试报告，用 /anime/hot 而不是 /anime/hot/page
    return this.request('GET', `/anime/hot?page=${page}&size=${size}`)
  }
  
  async getNewAnime(limit = 10) {
    return this.request('GET', `/anime/new?limit=${limit}`)
  }
  
  async searchAnime(keyword, page = 0, size = 20) {
    return this.request('GET', `/anime/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
  }
  
  async searchAnimeByTag(tag, page = 0, size = 20) {
    return this.request('GET', `/anime/tag/${tag}?page=${page}&size=${size}`)
  }
  
  // ========== 推荐系统（旧接口 - 兼容）==========
  async getUserRecommendations(userId, limit = 10) {
    try {
      return await this.request('GET', `/recommend/user/${userId}?limit=${limit}`)
    } catch (error) {
      console.warn('推荐接口失败，使用备用方案:', error)
      // 推荐接口失败时，返回最新动漫作为备用
      return this.getNewAnime(limit)
    }
  }
  
  async getRecommendationHistory(userId) {
    return this.request('GET', `/recommend/history/${userId}`)
  }
  
  async recordRecommendationClick(recommendationId) {
    return this.request('POST', `/recommend/click/${recommendationId}`)
  }
  
  // ========== 推荐算法增强模块（新接口）==========
  
  /**
   * 获取用户画像
   * @param {number} userId - 用户ID
   */
  async getUserProfile(userId) {
    return this.request('GET', `/admin/recommend/user/${userId}/profile`)
  }
  
  /**
   * 生成混合推荐（User-CF + Item-CF + 内容推荐）
   * @param {number} userId - 用户ID
   * @param {number} limit - 推荐数量
   */
  async generateHybridRecommendations(userId, limit = 10) {
    return this.request('GET', `/recommend/hybrid/user/${userId}?limit=${limit}`)
  }
  
  /**
   * 更新用户画像（根据用户行为重新计算偏好）
   * @param {number} userId - 用户ID
   */
  async updateUserProfile(userId) {
    return this.request('POST', `/admin/recommend/user/${userId}/profile/update`)
  }
  
  // 评论系统
  async getAnimeReviews(animeId, page = 0, size = 20) {
    return this.request('GET', `/review/anime/${animeId}?page=${page}&size=${size}`)
  }
  
  async getHotReviews(limit = 10) {
    // 根据测试报告，路径是 /review/anime/{id}/hot
    // 但社区页面可能需要所有热门评论，暂时用这个
    return this.request('GET', `/review/anime/1/hot?limit=${limit}`)
  }
  
  /**
   * 获取个性化社区推荐评论
   * @param {number} userId - 用户ID
   * @param {number} limit - 评论数量
   */
  async getPersonalizedCommunityReviews(userId, limit = 10) {
    return this.request('GET', `/review/personalized/${userId}?limit=${limit}`)
  }
  
  async getUserReviews(userId, page = 0, size = 20) {
    return this.request('GET', `/review/user/${userId}?page=${page}&size=${size}`)
  }
  
  async createReview(data) {
    return this.request('POST', '/review/create', data)
  }
  
  async likeReview(reviewId) {
    return this.request('PUT', `/review/${reviewId}/like`)
  }
  
  async unlikeReview(reviewId) {
    return this.request('PUT', `/review/${reviewId}/unlike`)
  }
  
  async deleteReview(reviewId) {
    return this.request('DELETE', `/review/${reviewId}`)
  }
  
  // 观看记录
  async createWatchRecord(data) {
    return this.request('POST', '/watch/record', data)
  }
  
  async getUserWatchRecords(userId) {
    return this.request('GET', `/watch/user/${userId}`)
  }
  
  async getAnimeWatchRecords(animeId) {
    return this.request('GET', `/watch/anime/${animeId}`)
  }
  
  async updateWatchProgress(recordId, data) {
    return this.request('PUT', `/watch/progress/${recordId}`, data)
  }
  
  async checkWatchRecord(userId, animeId) {
    return this.request('GET', `/watch/check?userId=${userId}&animeId=${animeId}`)
  }
  
  async deleteWatchRecord(recordId) {
    return this.request('DELETE', `/watch/${recordId}`)
  }
  
  // 新番日历 - 注意：/schedule/week 
  async getTodaySchedule() {
    try {
      // 获取今天的开始时间和结束时间
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      
      const tomorrow = new Date(today)
      tomorrow.setDate(tomorrow.getDate() + 1)
      
      // 格式: yyyy-MM-dd HH:mm:ss 或 yyyy-MM-ddTHH:mm:ss
      const startTime = today.toISOString().split('T')[0] + 'T00:00:00'
      const endTime = tomorrow.toISOString().split('T')[0] + 'T00:00:00'
      
      console.log('📅 请求今日日程:', { startTime, endTime })
      
      // 尝试不同的参数格式
      return await this.request('GET', `/schedule/day?startTime=${encodeURIComponent(startTime)}&endTime=${encodeURIComponent(endTime)}`)
    } catch (error) {
      console.warn('📅 日程接口失败，使用空数据:', error.message)
      return { 
        code: 200, 
        message: 'success', 
        data: [],
        _error: error.message
      }
    }
  }
  
  
// 获取本周日程
async getWeekSchedule() {
  try {
    // 获取本周的起始和结束时间
    const now = new Date()
    const dayOfWeek = now.getDay() // 0=周日, 1=周一...
    const diff = now.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1) // 调整为周一
    
    const monday = new Date(now.setDate(diff))
    monday.setHours(0, 0, 0, 0)
    
    const nextMonday = new Date(monday)
    nextMonday.setDate(monday.getDate() + 7)
    
    // 格式: yyyy-MM-ddTHH:mm:ss
    const startTime = monday.toISOString().split('.')[0]
    const endTime = nextMonday.toISOString().split('.')[0]
    
    console.log('📅 请求本周日程:', { startTime, endTime })
    
    return await this.request('GET', `/schedule/week?startTime=${encodeURIComponent(startTime)}&endTime=${encodeURIComponent(endTime)}`)
  } catch (error) {
    console.warn('📅 周日程接口失败:', error.message)
    // 返回模拟数据或空数据
    return this.getMockScheduleData()
  }
}

// 模拟日程数据（备用方案）
async getMockScheduleData() {
  console.log('📅 使用模拟日程数据')
  
  // 从数据库看，有6个日程记录
  const mockSchedules = [
    {
      id: 1,
      animeId: 1,
      anime: {
        id: 1,
        title: '鬼灭之刃',
        coverUrl: 'https://img2.baidu.com/it/u=4176267732,218572410&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=667'
      },
      episode: 1,
      airTime: '2026-01-13T20:00:00',
      platform: 'Bilibili',
      status: 0
    },
    {
      id: 2,
      animeId: 2,
      anime: {
        id: 2,
        title: '咒术回战',
        coverUrl: 'https://img0.baidu.com/it/u=257068288,4150064699&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=750'
      },
      episode: 10,
      airTime: '2026-01-12T21:00:00',
      platform: 'Bilibili',
      status: 0
    }
  ]
  
  return {
    code: 200,
    message: 'success',
    data: mockSchedules
  }
}
  
  async getAnimeSchedule(animeId) {
    return this.request('GET', `/schedule/anime/${animeId}`)
  }
  
  async createSchedule(data) {
    return this.request('POST', '/schedule/create', data)
  }
  
  // 微信登录
  async wechatLogin(code) {
    return this.request('POST', '/wechat/login', { code })
  }
  
  async updateUserInfo(data) {
    return this.request('POST', '/wechat/update-userinfo', data)
  }
  
  async decryptUserInfo(data) {
    return this.request('POST', '/wechat/decrypt', data)
  }
  
  // 用户系统
  async getUser(userId) {
    return this.request('GET', `/user/${userId}`)
  }
  
  async updateUser(userId, data) {
    return this.request('PUT', `/user/${userId}`, data)
  }
  
  async deleteUser(userId) {
    return this.request('DELETE', `/user/${userId}`)
  }
  
  /**
   * 为动漫评分
   * @param {number} userId - 用户ID
   * @param {number} animeId - 动漫ID
   * @param {number} rating - 评分（0-10）
   */
  async rateAnime(userId, animeId, rating) {
    return this.request('POST', `/review/rating`, {
      userId,
      animeId,
      rating
    })
  }
  
  // 名场面识别
  async recognizeScene(imageData) {
    return this.request('POST', '/scene/recognize', { image: imageData })
  }
  
  async getPopularScenes(animeId) {
    return this.request('GET', `/scene/popular-scenes/${animeId}`)
  }
  
  async uploadScene(data) {
    return this.request('POST', '/scene/upload', data)
  }
  
  async recognizeQuote(text) {
    return this.request('POST', '/scene/recognize-quote', { text })
  }
}

const apiService = new ApiService()

module.exports = {
  apiService,
  API_BASE
}
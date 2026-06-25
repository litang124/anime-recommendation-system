// pages/test-network/test-network.js
const { apiService, API_BASE } = require('../../utils/api')

Page({
  data: {
    apiBaseUrl: '',
    platform: '',
    networkType: '',
    hasToken: false,
    testResults: [],
    testing: false,
    successCount: 0,
    failCount: 0,
    avgDuration: 0,
    testIdCounter: 0
  },

  onLoad() {
    this.initEnvInfo()
  },

  onShow() {
    this.checkToken()
  },

  // 初始化环境信息
  initEnvInfo() {
    const systemInfo = wx.getSystemInfoSync()
    
    this.setData({
      apiBaseUrl: API_BASE || '未配置',
      platform: systemInfo.platform === 'devtools' ? '开发者工具' : systemInfo.model || systemInfo.platform
    })

    // 获取网络状态
    wx.getNetworkType({
      success: (res) => {
        this.setData({ networkType: res.networkType })
      },
      fail: () => {
        this.setData({ networkType: '未知' })
      }
    })

    this.checkToken()
  },

  // 检查Token状态
  checkToken() {
    const token = wx.getStorageSync('token')
    this.setData({ hasToken: !!token })
  },

  // 清空结果
  clearResults() {
    this.setData({
      testResults: [],
      successCount: 0,
      failCount: 0,
      avgDuration: 0
    })
  },

  // 切换预览
  togglePreview(e) {
    const id = e.currentTarget.dataset.id
    const results = this.data.testResults.map(item => {
      if (item.id === id) {
        return { ...item, showPreview: !item.showPreview }
      }
      return item
    })
    this.setData({ testResults: results })
  },

  // 更新统计
  updateStats() {
    const results = this.data.testResults
    const success = results.filter(r => r.success).length
    const fail = results.filter(r => !r.success).length
    const totalDuration = results.reduce((sum, r) => sum + r.duration, 0)
    const avg = results.length > 0 ? Math.round(totalDuration / results.length) : 0

    this.setData({
      successCount: success,
      failCount: fail,
      avgDuration: avg
    })
  },

  // 测试基础连接
  async testBasic() {
    this.setData({ testing: true })
    await this.runTest('基础连接', '/test/hello')
    this.setData({ testing: false })
    this.updateStats()
  },

  // 测试动漫接口
  async testAnime() {
    this.setData({ testing: true })
    await this.runTest('热门动漫', '/anime/hot?page=0&size=5')
    await this.delay(300)
    await this.runTest('动漫列表', '/anime/list?page=0&size=5')
    await this.delay(300)
    await this.runTest('搜索动漫', '/anime/search?keyword=鬼灭&page=0&size=5')
    this.setData({ testing: false })
    this.updateStats()
  },

  // 测试日程接口
  async testSchedule() {
    this.setData({ testing: true })
    await this.runTest('本周日程', '/schedule/week')
    this.setData({ testing: false })
    this.updateStats()
  },

  // 测试用户接口
  async testUser() {
    this.setData({ testing: true })
    const userId = wx.getStorageSync('userId') || 1
    await this.runTest('用户信息', `/user/${userId}`)
    this.setData({ testing: false })
    this.updateStats()
  },

  // 测试评论接口
  async testReview() {
    this.setData({ testing: true })
    await this.runTest('动漫评论', '/review/anime/1?page=0&size=5')
    this.setData({ testing: false })
    this.updateStats()
  },

  // 测试推荐接口
  async testRecommend() {
    this.setData({ testing: true })
    const userId = wx.getStorageSync('userId') || 1
    await this.runTest('用户推荐', `/recommend/user/${userId}?limit=5`)
    this.setData({ testing: false })
    this.updateStats()
  },

  // 运行所有测试
  async runAllTests() {
    this.setData({ testing: true, testResults: [] })

    const tests = [
      { name: '基础连接', endpoint: '/test/hello' },
      { name: '热门动漫', endpoint: '/anime/hot?page=0&size=5' },
      { name: '动漫列表', endpoint: '/anime/list?page=0&size=5' },
      { name: '本周日程', endpoint: '/schedule/week' },
      { name: '动漫评论', endpoint: '/review/anime/1?page=0&size=3' },
      { name: '用户信息', endpoint: '/user/1' },
      { name: '用户推荐', endpoint: '/recommend/user/1?limit=3' }
    ]

    for (const test of tests) {
      await this.runTest(test.name, test.endpoint)
      await this.delay(300)
    }

    this.setData({ testing: false })
    this.updateStats()
  },

  // 执行单个测试
  async runTest(testName, endpoint) {
    const startTime = Date.now()
    const testId = ++this.data.testIdCounter

    try {
      const result = await apiService.request('GET', endpoint)
      const duration = Date.now() - startTime

      let dataCount = '-'
      if (result.data) {
        if (Array.isArray(result.data)) {
          dataCount = `${result.data.length} 条`
        } else if (typeof result.data === 'object') {
          dataCount = '对象'
        }
      }

      // 生成预览数据
      let preview = ''
      if (result.data) {
        try {
          preview = JSON.stringify(result.data, null, 2).substring(0, 500)
          if (preview.length >= 500) preview += '...'
        } catch (e) {
          preview = '无法序列化'
        }
      }

      const testResult = {
        id: testId,
        name: testName,
        endpoint: endpoint,
        success: true,
        duration,
        code: result.code || 200,
        dataCount,
        preview,
        showPreview: false
      }

      this.data.testResults.push(testResult)
      this.setData({ testResults: this.data.testResults })

    } catch (error) {
      const duration = Date.now() - startTime

      const testResult = {
        id: testId,
        name: testName,
        endpoint: endpoint,
        success: false,
        duration,
        error: error.message || error.errMsg || '请求失败'
      }

      this.data.testResults.push(testResult)
      this.setData({ testResults: this.data.testResults })
    }
  },

  // 延迟函数
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
})

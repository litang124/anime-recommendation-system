// pages/search/search.js
const { apiService } = require('../../utils/api')

Page({
  data: {
    searchKeyword: '',
    searchHistory: [],
    hotKeywords: ['进击的巨人', '鬼灭之刃', '咒术回战', '间谍过家家', '海贼王', '葬送的芙莉莲'],
    searchResults: [],
    loading: false,
    hasSearched: false,
    page: 0,
    hasMore: true
  },

  onLoad() {
    this.loadSearchHistory()
  },

  // 加载搜索历史
  loadSearchHistory() {
    const history = wx.getStorageSync('searchHistory') || []
    this.setData({ searchHistory: history.slice(0, 10) })
  },

  // 输入关键词
  onInput(e) {
    this.setData({ searchKeyword: e.detail.value })
  },

  // 清空输入
  clearInput() {
    this.setData({ 
      searchKeyword: '', 
      searchResults: [],
      hasSearched: false 
    })
  },

  // 搜索
  async doSearch() {
    const keyword = this.data.searchKeyword.trim()
    if (!keyword) {
      wx.showToast({ title: '请输入搜索内容', icon: 'none' })
      return
    }

    this.setData({ loading: true, hasSearched: true })

    try {
      const res = await apiService.searchAnime(keyword, 0, 20)
      
      if (res.code === 200 && res.data) {
        const results = this.processAnimeList(res.data)
        
        this.setData({
          searchResults: results,
          hasMore: res.data.length === 20
        })
        
        // 保存搜索历史
        this.saveSearchHistory(keyword)
      } else {
        this.setData({ searchResults: [] })
      }
    } catch (error) {
      console.error('搜索失败:', error)
      wx.showToast({ title: '搜索失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 处理动漫列表
  processAnimeList(list) {
    return list.map(item => ({
      id: item.id,
      title: item.title || '未知动漫',
      coverUrl: item.coverUrl || 'https://via.placeholder.com/300x400/f03d6d/ffffff?text=动漫',
      score: item.score || 0,
      status: item.status === 0 ? '连载中' : item.status === 1 ? '已完结' : '未开播',
      episodeCount: item.episodeCount || 0,
      tags: item.tags ? item.tags.split(',').slice(0, 3) : []
    }))
  },

  // 保存搜索历史
  saveSearchHistory(keyword) {
    let history = this.data.searchHistory.filter(h => h !== keyword)
    history.unshift(keyword)
    history = history.slice(0, 10)
    
    this.setData({ searchHistory: history })
    wx.setStorageSync('searchHistory', history)
  },

  // 点击历史关键词
  tapHistory(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ searchKeyword: keyword }, () => {
      this.doSearch()
    })
  },

  // 点击热门关键词
  tapHot(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ searchKeyword: keyword }, () => {
      this.doSearch()
    })
  },

  // 清空历史
  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({ searchHistory: [] })
          wx.removeStorageSync('searchHistory')
        }
      }
    })
  },

  // 跳转到详情
  goToDetail(e) {
    const animeId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/anime-detail/anime-detail?id=${animeId}`
    })
  },

  // 加载更多
  async loadMore() {
    if (this.data.loading || !this.data.hasMore) return

    const keyword = this.data.searchKeyword.trim()
    if (!keyword) return

    this.setData({ loading: true })

    try {
      const nextPage = this.data.page + 1
      const res = await apiService.searchAnime(keyword, nextPage, 20)

      if (res.code === 200 && res.data && res.data.length > 0) {
        const newResults = this.processAnimeList(res.data)
        this.setData({
          searchResults: [...this.data.searchResults, ...newResults],
          page: nextPage,
          hasMore: res.data.length === 20
        })
      } else {
        this.setData({ hasMore: false })
      }
    } catch (error) {
      console.error('加载更多失败:', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    if (this.data.searchKeyword) {
      this.doSearch().then(() => {
        wx.stopPullDownRefresh()
      })
    } else {
      wx.stopPullDownRefresh()
    }
  },

  // 触底加载
  onReachBottom() {
    this.loadMore()
  }
})

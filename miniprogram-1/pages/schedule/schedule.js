// pages/schedule/schedule.js
const { apiService } = require('../../utils/api')

// ✅ 内置 anime 映射表（与数据库完全一致）
const ANIME_MAP = {
  1: { title: '鬼灭之刃', coverUrl: 'https://img.example.com/kimetsu.jpg' },
  2: { title: '咒术回战', coverUrl: 'https://img.example.com/jujutsu.jpg' },
  3: { title: '进击的巨人', coverUrl: 'https://img.example.com/aot.jpg' },
  4: { title: '间谍过家家', coverUrl: 'https://img.example.com/spyfamily.jpg' },
  5: { title: '我的英雄学院', coverUrl: 'https://img.example.com/bnha.jpg' },
  6: { title: '航海王', coverUrl: 'https://img.example.com/onepiece.jpg' },
  7: { title: '火影忍者', coverUrl: 'https://img.example.com/naruto.jpg' },
  8: { title: '死神', coverUrl: 'https://img.example.com/bleach.jpg' },
  9: { title: '钢之炼金术师', coverUrl: 'https://img.example.com/fma.jpg' },
  10: { title: '命运石之门', coverUrl: 'https://img.example.com/steinsgate.jpg' },
  11: { title: '鬼灭之刃 游郭篇', coverUrl: 'https://img.example.com/kimetsu.jpg' },
  12: { title: '间谍过家家', coverUrl: 'https://img.example.com/spyfamily.jpg' },
  13: { title: '链锯人', coverUrl: 'https://img.example.com/chainsawman.jpg' },
  14: { title: '孤独摇滚', coverUrl: 'https://img.example.com/bocchi.jpg' },
  15: { title: '葬送的芙莉莲', coverUrl: 'https://img.example.com/frieren.jpg' },
  16: { title: '测试新番：异世界程序员', coverUrl: 'https://img2.baidu.com/it/u=4176267732,218572410&fm=253&fmt=auto&app=138&f=JPEG' },
  17: { title: '斗破苍穹', coverUrl: 'https://img.example.com/dpcq.jpg' },
  18: { title: '斗罗大陆', coverUrl: 'https://img.example.com/dldl.jpg' },
  19: { title: '完美世界', coverUrl: 'https://img.example.com/wmsj.jpg' },
  20: { title: '一人之下', coverUrl: 'https://img.example.com/yrzx.jpg' },
  21: { title: '狐妖小红娘', coverUrl: 'https://img.example.com/hyxhn.jpg' },
  22: { title: '灵笼', coverUrl: 'https://img.example.com/ll.jpg' },
  23: { title: '凡人修仙传', coverUrl: 'https://img.example.com/frxxz.jpg' },
  24: { title: '吞噬星空', coverUrl: 'https://img.example.com/tsxk.jpg' },
  25: { title: '孤独摇滚！', coverUrl: 'https://img.example.com/bocchi.jpg' },
  26: { title: 'MyGO!!!!!', coverUrl: 'https://img.example.com/mygo.jpg' },
  27: { title: 'Girls Band Cry', coverUrl: 'https://img.example.com/gbc.jpg' },
  28: { title: 'BanG Dream!', coverUrl: 'https://img.example.com/bangdream.jpg' },
  29: { title: 'K-ON! 轻音少女', coverUrl: 'https://img.example.com/k-on.jpg' },
  30: { title: '佐贺偶像是传奇', coverUrl: 'https://img.example.com/zombie.jpg' },
  31: { title: '莉可丽丝', coverUrl: 'https://img.example.com/lycoris.jpg' },
  32: { title: '街角魔族', coverUrl: 'https://img.example.com/machikado.jpg' },
  33: { title: '安达与岛村', coverUrl: 'https://img.example.com/adachi.jpg' },
  34: { title: '转生王女与天才千金的魔法革命', coverUrl: 'https://img.example.com/tenou.jpg' },
  35: { title: '摇曳露营△', coverUrl: 'https://img.example.com/yurucamp.jpg' },
  36: { title: '向山进发', coverUrl: 'https://img.example.com/yama.jpg' },
  37: { title: 'Slow Loop', coverUrl: 'https://img.example.com/slowloop.jpg' },
  38: { title: '明日酱的水手服', coverUrl: 'https://img.example.com/akebi.jpg' }
}

Page({
  data: {
    dates: [],
    currentDate: '',
    selectedDateIndex: 0,
    schedulesByDate: {},
    currentSchedules: [],
    weekHotAnime: [],
    loading: false
  },

  onLoad() {
    this.initWeekDates()
    this.loadScheduleData()
  },

  onShow() {
    // 刷新数据
  },

  // 初始化本周日期
  initWeekDates() {
    const dates = []
    const today = new Date()
    const currentDay = today.getDay()
    const weekDays = ['日', '一', '二', '三', '四', '五', '六']
    
    // 从周一开始
    const monday = new Date(today)
    monday.setDate(today.getDate() - (currentDay === 0 ? 6 : currentDay - 1))
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(monday)
      date.setDate(monday.getDate() + i)
      
      const dateStr = this.formatDateToString(date)
      const dayIndex = date.getDay()
      
      dates.push({
        date: dateStr,
        day: weekDays[dayIndex],
        displayDate: date.getDate(),
        isToday: this.formatDateToString(today) === dateStr,
        hasSchedule: false
      })
    }
    
    const todayStr = this.formatDateToString(today)
    const todayIndex = dates.findIndex(d => d.date === todayStr)
    
    this.setData({
      dates,
      currentDate: todayStr,
      selectedDateIndex: todayIndex >= 0 ? todayIndex : 0
    })
  },

  // 加载日程数据
  async loadScheduleData() {
    this.setData({ loading: true })
    
    try {
      // 获取本周日程
      const weekRes = await apiService.getWeekSchedule()
      const schedules = weekRes.data || []
      
      // 按日期分组
      const schedulesByDate = this.groupSchedulesByDate(schedules)
      
      // 更新日期标记
      const dates = this.data.dates.map(date => ({
        ...date,
        hasSchedule: !!(schedulesByDate[date.date] && schedulesByDate[date.date].length > 0)
      }))
      
      // 获取热门动漫
      let weekHotAnime = []
      try {
        const hotRes = await apiService.getHotAnime(0, 5)
        weekHotAnime = hotRes.data || []
      } catch (e) {
        console.warn('获取热门动漫失败:', e)
      }
      
      // 更新当前日期的日程
      const currentSchedules = schedulesByDate[this.data.currentDate] || []
      
      this.setData({
        dates,
        schedulesByDate,
        currentSchedules,
        weekHotAnime,
        loading: false
      })
      
    } catch (error) {
      console.error('加载日程数据失败:', error)
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  // 按日期分组日程
  groupSchedulesByDate(schedules) {
    const grouped = {}
    const wishList = wx.getStorageSync('wishList') || []
    
    schedules.forEach(item => {
      // 处理时间数组格式 [year, month, day, hour, minute]
      let dateStr = ''
      let timeStr = ''
      
      if (Array.isArray(item.airTime)) {
        const [year, month, day, hour = 0, minute = 0] = item.airTime
        dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        timeStr = `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`
      } else if (item.airTime) {
        const date = new Date(item.airTime)
        dateStr = this.formatDateToString(date)
        timeStr = `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
      }
      
      if (!dateStr) return
      
      if (!grouped[dateStr]) {
        grouped[dateStr] = []
      }
      
      // 使用 ANIME_MAP 查找真实 anime 数据
      const anime = ANIME_MAP[item.animeId] || {}
      grouped[dateStr].push({
        id: item.id,
        animeId: item.animeId,
        title: anime.title || '未知动漫',
        coverUrl: anime.coverUrl || 'https://via.placeholder.com/300x400/667eea/ffffff?text=动漫',
        episode: item.episode || 1,
        time: timeStr,
        platform: item.platform || '未知平台',
        status: item.status || 0,
        score: 0,
        inWishList: wishList.includes(item.animeId)  // 标记是否在想看列表中
      })
    })
    
    // 按时间排序
    Object.keys(grouped).forEach(date => {
      grouped[date].sort((a, b) => a.time.localeCompare(b.time))
    })
    
    return grouped
  },

  // 选择日期
  selectDate(e) {
    const index = e.currentTarget.dataset.index
    const date = this.data.dates[index]
    
    this.setData({
      selectedDateIndex: index,
      currentDate: date.date,
      currentSchedules: this.data.schedulesByDate[date.date] || []
    })
  },

  // 跳转到今天
  goToToday() {
    const today = new Date()
    const todayStr = this.formatDateToString(today)
    const todayIndex = this.data.dates.findIndex(d => d.date === todayStr)
    
    if (todayIndex >= 0) {
      this.setData({
        selectedDateIndex: todayIndex,
        currentDate: todayStr,
        currentSchedules: this.data.schedulesByDate[todayStr] || []
      })
    }
  },

  // 跳转到动漫详情
  goToAnimeDetail(e) {
    const animeId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/anime-detail/anime-detail?id=${animeId}`
    })
  },

  // 设置/取消提醒
  setReminder(e) {
    const scheduleId = e.currentTarget.dataset.id
    
    // 1. 根据 scheduleId 查找 animeId
    let animeId = null
    for (const item of this.data.currentSchedules) {
      if (item.id == scheduleId) {
        animeId = item.animeId
        break
      }
    }
    
    if (!animeId) {
      wx.showToast({ title: '获取动漫信息失败', icon: 'none' })
      return
    }
    
    // 2. 读取并更新 wishList
    let wishList = wx.getStorageSync('wishList') || []
    const index = wishList.indexOf(animeId)
    
    if (index === -1) {
      // 不在想看列表中，添加
      wishList.push(animeId)
      wx.setStorageSync('wishList', wishList)
      wx.showToast({ title: '✅ 已加入想看清单', icon: 'success' })
    } else {
      // 已在想看列表中，移除
      wishList.splice(index, 1)
      wx.setStorageSync('wishList', wishList)
      wx.showToast({ title: '❌ 已取消想看', icon: 'success' })
    }
    
    // 3. 刷新页面数据，更新按钮状态
    this.loadScheduleData()
  },

  // 格式化日期
  formatDateToString(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadScheduleData().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '新番日历 - 追番不迷路',
      path: '/pages/schedule/schedule'
    }
  }
})

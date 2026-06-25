Page({
  data: {
    userInfo: null,
    dashboardData: {},
    loading: true,
    error: false,
    showLogin: true
  },

  onLoad() {
    this.checkAdminLogin();
  },

  checkAdminLogin() {
    const adminInfo = wx.getStorageSync('adminInfo');
    if (adminInfo && adminInfo.token) {
      this.setData({
        userInfo: adminInfo,
        showLogin: false
      });
      this.loadDashboardData();
    }
  },

  loadDashboardData() {
    const that = this;
    wx.request({
      url: 'https://localhost:8080/admin/dashboard',
      method: 'GET',
      header: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + that.data.userInfo.token
      },
      success(res) {
        if (res.data.code === 200) {
          that.setData({
            dashboardData: res.data.data,
            loading: false
          });
        } else {
          that.setData({
            error: true,
            loading: false
          });
        }
      },
      fail() {
        that.setData({
          error: true,
          loading: false
        });
      }
    });
  },

  onPullDownRefresh() {
    this.loadDashboardData();
    wx.stopPullDownRefresh();
  },

  handleLogin(e) {
    const { username, password } = e.detail.value;
    wx.request({
      url: 'https://localhost:8080/admin/login',
      method: 'POST',
      data: { username, password },
      header: { 'Content-Type': 'application/json' },
      success(res) {
        if (res.data.code === 200) {
          wx.setStorageSync('adminInfo', {
            token: res.data.data.token,
            username: username
          });
          wx.showToast({ title: '登录成功', icon: 'success' });
          this.setData({
            userInfo: { token: res.data.data.token, username },
            showLogin: false
          });
          this.loadDashboardData();
        } else {
          wx.showToast({ title: res.data.message || '登录失败', icon: 'none' });
        }
      },
      fail() {
        wx.showToast({ title: '网络错误', icon: 'none' });
      }
    });
  }
});
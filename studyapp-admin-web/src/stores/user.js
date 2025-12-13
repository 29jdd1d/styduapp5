import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getAdminInfo, logout } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const adminInfo = ref(null)

  // 登录
  async function doLogin(loginData) {
    try {
      const res = await login(loginData)
      if (res.code === 200) {
        token.value = res.data.token
        localStorage.setItem('token', res.data.token)
        return { success: true }
      }
      return { success: false, message: res.message }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  // 获取管理员信息
  async function fetchAdminInfo() {
    try {
      const res = await getAdminInfo()
      if (res.code === 200) {
        adminInfo.value = res.data
        return { success: true }
      }
      return { success: false, message: res.message }
    } catch (error) {
      return { success: false, message: error.message || '获取信息失败' }
    }
  }

  // 登出
  async function doLogout() {
    try {
      await logout()
    } catch (error) {
      console.error('登出请求失败', error)
    } finally {
      token.value = ''
      adminInfo.value = null
      localStorage.removeItem('token')
      router.push('/login')
    }
  }

  return {
    token,
    adminInfo,
    doLogin,
    fetchAdminInfo,
    doLogout
  }
})

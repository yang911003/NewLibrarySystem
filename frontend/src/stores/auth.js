// Pinia：登入狀態、Token 存 localStorage
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/index.js'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user  = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  async function register(data) {
    const res = await authApi.register(data)
    return res.data
  }

  async function login(data) {
    const res = await authApi.login(data)
    const { token: jwt, userId, userName, phoneNumber } = res.data.data
    token.value = jwt
    user.value  = { userId, userName, phoneNumber }
    localStorage.setItem('token', jwt)
    localStorage.setItem('user', JSON.stringify(user.value))
    return res.data
  }

  function logout() {
    token.value = null
    user.value  = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLoggedIn, register, login, logout }
})
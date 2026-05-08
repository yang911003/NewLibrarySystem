<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <h2>登入</h2>

      <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>手機號碼</label>
          <input v-model="form.phoneNumber" type="tel"
                 placeholder="09xxxxxxxx" maxlength="10" />
        </div>
        <div class="form-group">
          <label>密碼</label>
          <input v-model="form.password" type="password" placeholder="請輸入密碼" />
        </div>
        <button class="btn btn-primary full-w" :disabled="loading" type="submit">
          {{ loading ? '登入中...' : '登入' }}
        </button>
      </form>

      <p class="switch">還沒有帳號？
        <router-link to="/register">立即註冊</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router  = useRouter()
const auth    = useAuthStore()
const loading = ref(false)
const errorMsg = ref('')
const form = ref({ phoneNumber: '', password: '' })

async function handleLogin() {
  errorMsg.value = ''
  loading.value  = true
  try {
    await auth.login(form.value)
    router.push('/books')
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '登入失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrap {
  display: flex;
  justify-content: center;
  padding: 48px 16px;
}
.auth-card {
  background: #fff;
  border-radius: 12px;
  padding: 36px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 4px 20px rgba(0,0,0,.08);
}
h2 { margin-bottom: 24px; font-size: 22px; color: #1e3a5f; }
.full-w { width: 100%; padding: 10px; font-size: 15px; }
.switch { margin-top: 20px; font-size: 13px; text-align: center; color: #555; }
.switch a { color: #3b82f6; }
</style>
<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <h2>會員註冊</h2>

      <div v-if="errorMsg"   class="alert alert-error">{{ errorMsg }}</div>
      <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>手機號碼（登入帳號）</label>
          <input v-model="form.phoneNumber" type="tel"
                 placeholder="09xxxxxxxx" maxlength="10" />
        </div>
        <div class="form-group">
          <label>密碼（至少 8 個字元）</label>
          <input v-model="form.password" type="password" placeholder="請輸入密碼" />
        </div>
        <div class="form-group">
          <label>確認密碼</label>
          <input v-model="form.confirmPassword" type="password" placeholder="請再次輸入密碼" />
        </div>
        <div class="form-group">
          <label>使用者名稱</label>
          <input v-model="form.userName" type="text" placeholder="請輸入姓名" />
        </div>
        <button class="btn btn-primary full-w" :disabled="loading" type="submit">
          {{ loading ? '註冊中...' : '立即註冊' }}
        </button>
      </form>

      <p class="switch">已有帳號？
        <router-link to="/login">返回登入</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router   = useRouter()
const auth     = useAuthStore()
const loading  = ref(false)
const errorMsg  = ref('')
const successMsg = ref('')
const form = ref({
  phoneNumber: '', password: '', confirmPassword: '', userName: ''
})

async function handleRegister() {
  errorMsg.value  = ''
  successMsg.value = ''

  if (!/^09\d{8}$/.test(form.value.phoneNumber)) {
    errorMsg.value = '手機號碼格式不正確（需為 09 開頭共 10 碼）'; return
  }
  if (form.value.password.length < 8) {
    errorMsg.value = '密碼至少需要 8 個字元'; return
  }
  if (form.value.password !== form.value.confirmPassword) {
    errorMsg.value = '兩次密碼輸入不一致'; return
  }
  if (!form.value.userName.trim()) {
    errorMsg.value = '請輸入使用者名稱'; return
  }

  loading.value = true
  try {
    await auth.register({
      phoneNumber: form.value.phoneNumber,
      password:    form.value.password,
      userName:    form.value.userName
    })
    successMsg.value = '註冊成功！即將跳轉至登入頁...'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '註冊失敗，請稍後再試'
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
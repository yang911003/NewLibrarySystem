<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <router-link to="/books" class="brand">📚 線上圖書借閱系統</router-link>

      <div class="nav-links">
        <router-link to="/books">書籍列表</router-link>
        <router-link v-if="auth.isLoggedIn" to="/my-borrows">我的借閱</router-link>

        <template v-if="auth.isLoggedIn">
          <span class="greeting">Hi, {{ auth.user?.userName }}</span>
          <button class="btn btn-secondary btn-sm" @click="handleLogout">登出</button>
        </template>
        <template v-else>
          <router-link to="/login">登入</router-link>
          <router-link to="/register">註冊</router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth.js'
import { useRouter } from 'vue-router'

const auth   = useAuthStore()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  background: #1e3a5f;
  color: #fff;
  padding: 0 24px;
  height: 56px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0,0,0,.3);
}
.navbar-inner {
  max-width: 1100px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.brand {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}
.nav-links a {
  color: #cbd5e1;
  text-decoration: none;
  font-size: 14px;
}
.nav-links a:hover,
.nav-links a.router-link-active { color: #fff; }
.greeting { font-size: 13px; color: #94a3b8; }
.btn-sm { padding: 5px 12px; font-size: 13px; }
</style>
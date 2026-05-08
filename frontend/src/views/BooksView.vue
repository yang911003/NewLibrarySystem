<template>
  <div>
    <div class="page-header">
      <h2>書籍列表</h2>
      <input v-model="search" class="search-input" placeholder="搜尋書名或作者..." />
    </div>

    <div v-if="loading" class="loading">載入中...</div>
    <div v-else-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>

    <div v-else class="book-grid">
      <div v-for="book in filteredBooks" :key="book.isbn" class="book-card">
        <div class="book-header">
          <h3 class="book-title">{{ book.name }}</h3>
          <span :class="['badge', book.availableCopies > 0 ? 'badge-ok' : 'badge-no']">
            {{ book.availableCopies > 0 ? `可借 ${book.availableCopies} 本` : '暫無庫存' }}
          </span>
        </div>
        <p class="book-author">✍️ {{ book.author }}</p>
        <p class="book-intro">{{ book.introduction }}</p>
        <p class="book-isbn">ISBN: {{ book.isbn }}</p>
        <p class="book-copies">館藏：{{ book.totalCopies }} 本 ／ 可借：{{ book.availableCopies }} 本</p>

        <button
          v-if="auth.isLoggedIn"
          class="btn btn-primary borrow-btn"
          :disabled="book.availableCopies === 0 || borrowingIsbn === book.isbn"
          @click="handleBorrow(book.isbn)">
          {{ borrowingIsbn === book.isbn ? '處理中...' : '借閱' }}
        </button>
        <router-link v-else to="/login" class="btn btn-secondary borrow-btn">
          登入後借閱
        </router-link>
      </div>
    </div>

    <div v-if="!loading && filteredBooks.length === 0 && !errorMsg" class="empty">
      查無符合條件的書籍
    </div>

    <!-- Toast 通知 -->
    <div v-if="toastMsg" :class="['toast', toastType === 'ok' ? 'toast-ok' : 'toast-err']">
      {{ toastMsg }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { bookApi, borrowApi } from '../api/index.js'
import { useAuthStore } from '../stores/auth.js'

const auth         = useAuthStore()
const books        = ref([])
const loading      = ref(true)
const errorMsg     = ref('')
const search       = ref('')
const borrowingIsbn = ref(null)
const toastMsg     = ref('')
const toastType    = ref('ok')

const filteredBooks = computed(() => {
  const q = search.value.toLowerCase()
  return books.value.filter(b =>
    b.name.toLowerCase().includes(q) ||
    (b.author || '').toLowerCase().includes(q)
  )
})

async function loadBooks() {
  try {
    const res = await bookApi.getAll()
    books.value = res.data.data || []
  } catch {
    errorMsg.value = '載入書籍失敗，請重新整理頁面'
  } finally {
    loading.value = false
  }
}

async function handleBorrow(isbn) {
  borrowingIsbn.value = isbn
  try {
    const res = await borrowApi.borrow(isbn)
    showToast(res.data.message || '借閱成功！', 'ok')
    await loadBooks()
  } catch (e) {
    showToast(e.response?.data?.message || '借閱失敗', 'err')
  } finally {
    borrowingIsbn.value = null
  }
}

function showToast(msg, type = 'ok') {
  toastMsg.value  = msg
  toastType.value = type
  setTimeout(() => { toastMsg.value = '' }, 3000)
}

onMounted(loadBooks)
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  gap: 16px;
}
.page-header h2 { font-size: 22px; color: #1e3a5f; }
.search-input {
  padding: 8px 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  width: 260px;
}
.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}
.book-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,.07);
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.book-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 8px; }
.book-title  { font-size: 16px; font-weight: 600; color: #1e3a5f; flex: 1; }
.badge       { font-size: 12px; padding: 3px 8px; border-radius: 12px; white-space: nowrap; }
.badge-ok    { background: #dcfce7; color: #15803d; }
.badge-no    { background: #fee2e2; color: #b91c1c; }
.book-author { font-size: 13px; color: #6b7280; }
.book-intro  { font-size: 13px; color: #555; line-height: 1.6; flex: 1; }
.book-isbn   { font-size: 11px; color: #aaa; }
.book-copies { font-size: 12px; color: #6b7280; }
.borrow-btn  { margin-top: 8px; width: 100%; text-align: center; }
.loading     { text-align: center; padding: 48px; color: #6b7280; }
.empty       { text-align: center; padding: 48px; color: #9ca3af; }
.toast {
  position: fixed;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 28px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 16px rgba(0,0,0,.15);
  z-index: 999;
}
.toast-ok  { background: #22c55e; color: #fff; }
.toast-err { background: #ef4444; color: #fff; }
</style>
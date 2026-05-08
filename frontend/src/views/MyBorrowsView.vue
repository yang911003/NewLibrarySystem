<template>
  <div>
    <h2 class="page-title">我的借閱紀錄</h2>

    <div class="tab-bar">
      <button :class="['tab', tab === 'active' ? 'tab-active' : '']"
              @click="tab = 'active'">
        借閱中 ({{ activeRecords.length }})
      </button>
      <button :class="['tab', tab === 'history' ? 'tab-active' : '']"
              @click="tab = 'history'">
        歷史紀錄 ({{ historyRecords.length }})
      </button>
    </div>

    <div v-if="loading" class="loading">載入中...</div>
    <div v-else-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>

    <template v-else>
      <!-- 借閱中 -->
      <div v-if="tab === 'active'">
        <div v-if="activeRecords.length === 0" class="empty">目前沒有借閱中的書籍</div>
        <div v-else class="record-list">
          <div v-for="r in activeRecords" :key="r.recordId" class="record-card active-card">
            <div class="record-info">
              <h3>{{ r.bookName }}</h3>
              <p>作者：{{ r.bookAuthor }}</p>
              <p>ISBN：{{ r.isbn }}</p>
              <p>庫存 ID：{{ r.inventoryId }}</p>
              <p class="borrow-date">借閱時間：{{ formatDate(r.borrowingTime) }}</p>
            </div>
            <button
              class="btn btn-danger"
              :disabled="returningId === r.inventoryId"
              @click="handleReturn(r.inventoryId)">
              {{ returningId === r.inventoryId ? '處理中...' : '還書' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 歷史紀錄 -->
      <div v-if="tab === 'history'">
        <div v-if="historyRecords.length === 0" class="empty">尚無還書紀錄</div>
        <div v-else class="record-list">
          <div v-for="r in historyRecords" :key="r.recordId" class="record-card history-card">
            <div class="record-info">
              <h3>{{ r.bookName }}</h3>
              <p>作者：{{ r.bookAuthor }}</p>
              <p>ISBN：{{ r.isbn }}</p>
              <p class="borrow-date">借閱：{{ formatDate(r.borrowingTime) }}</p>
              <p class="return-date">歸還：{{ formatDate(r.returnTime) }}</p>
            </div>
            <span class="badge badge-returned">已還書</span>
          </div>
        </div>
      </div>
    </template>

    <div v-if="toastMsg" :class="['toast', toastType === 'ok' ? 'toast-ok' : 'toast-err']">
      {{ toastMsg }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { borrowApi } from '../api/index.js'

const records    = ref([])
const loading    = ref(true)
const errorMsg   = ref('')
const returningId = ref(null)
const tab        = ref('active')
const toastMsg   = ref('')
const toastType  = ref('ok')

const activeRecords  = computed(() => records.value.filter(r => !r.returned))
const historyRecords = computed(() => records.value.filter(r =>  r.returned))

async function loadRecords() {
  try {
    const res = await borrowApi.myBorrows()
    records.value = res.data.data || []
  } catch {
    errorMsg.value = '載入借閱紀錄失敗'
  } finally {
    loading.value = false
  }
}

async function handleReturn(inventoryId) {
  returningId.value = inventoryId
  try {
    const res = await borrowApi.returnBook(inventoryId)
    showToast(res.data.message || '還書成功！', 'ok')
    await loadRecords()
  } catch (e) {
    showToast(e.response?.data?.message || '還書失敗', 'err')
  } finally {
    returningId.value = null
  }
}

function formatDate(dt) {
  if (!dt) return '-'
  return new Date(dt).toLocaleString('zh-TW', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function showToast(msg, type = 'ok') {
  toastMsg.value  = msg
  toastType.value = type
  setTimeout(() => { toastMsg.value = '' }, 3000)
}

onMounted(loadRecords)
</script>

<style scoped>
.page-title { font-size: 22px; color: #1e3a5f; margin-bottom: 20px; }
.tab-bar    { display: flex; gap: 8px; margin-bottom: 20px; }
.tab {
  padding: 8px 20px;
  border: 2px solid #d1d5db;
  border-radius: 20px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  color: #6b7280;
}
.tab-active { border-color: #3b82f6; color: #3b82f6; font-weight: 600; }
.record-list { display: flex; flex-direction: column; gap: 14px; }
.record-card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.07);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}
.active-card  { border-left: 4px solid #3b82f6; }
.history-card { border-left: 4px solid #6b7280; opacity: .85; }
.record-info h3  { font-size: 16px; font-weight: 600; color: #1e3a5f; margin-bottom: 4px; }
.record-info p   { font-size: 13px; color: #6b7280; }
.borrow-date     { color: #3b82f6 !important; }
.return-date     { color: #22c55e !important; }
.badge-returned  {
  font-size: 12px; padding: 4px 10px;
  background: #f3f4f6; color: #6b7280;
  border-radius: 12px; white-space: nowrap;
}
.loading { text-align: center; padding: 48px; color: #6b7280; }
.empty   { text-align: center; padding: 48px; color: #9ca3af; }
.toast {
  position: fixed; bottom: 32px; left: 50%;
  transform: translateX(-50%);
  padding: 12px 28px; border-radius: 8px;
  font-size: 14px; font-weight: 500;
  box-shadow: 0 4px 16px rgba(0,0,0,.15); z-index: 999;
}
.toast-ok  { background: #22c55e; color: #fff; }
.toast-err { background: #ef4444; color: #fff; }
</style>
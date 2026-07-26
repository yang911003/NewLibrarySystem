import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'

const app = createApp(App)
app.use(createPinia()) // 狀態管理
app.use(router)    // 路由
app.mount('#app')  // 掛載到 index.html 的 <div id="app">
// Vue Router（含 requiresAuth Guard）
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const routes = [
  { path: '/',          redirect: '/books' },
  { path: '/login',     component: () => import('../views/LoginView.vue') },
  { path: '/register',  component: () => import('../views/RegisterView.vue') },
  { path: '/books',     component: () => import('../views/BooksView.vue') },
  {
    path: '/my-borrows',
    component: () => import('../views/MyBorrowsView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
// Axios 封裝（自動帶 Token、統一 401 處理)
import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 自動附加 JWT Token
http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 統一錯誤處理
http.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const authApi = {
  register: data => http.post('/auth/register', data),
  login:    data => http.post('/auth/login', data)
}

export const bookApi = {
  getAll:  ()     => http.get('/books'),
  getOne:  isbn   => http.get(`/books/${isbn}`)
}

export const borrowApi = {
  borrow:     isbn        => http.post('/borrows/borrow', { isbn }),
  returnBook: inventoryId => http.put(`/borrows/return/${inventoryId}`),
  myBorrows:  ()          => http.get('/borrows/my')
}

//我是最強的 頂住壓力 加油
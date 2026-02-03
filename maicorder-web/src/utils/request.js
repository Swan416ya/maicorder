import axios from 'axios'

// 统一配置API基础路径（根据后端实际地址调整）
export const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'

// 创建axios实例（可选，可统一配置请求头、拦截器）
const request = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：添加token（登录后携带）
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}` // 若后端要求Bearer token格式
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  (response) => response.data, // 直接返回响应体，简化业务代码
  (error) => {
    const errMsg = error.response?.data?.message || error.message || '请求失败'
    alert(errMsg) // 也可替换为全局提示组件
    return Promise.reject(error)
  }
)

export default request
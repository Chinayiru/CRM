import axios, { type AxiosRequestConfig } from 'axios'
import type { ApiResponse, LoginData, CrmRecord, UserInfo } from './types'
import {
  DEMO_PASSWORD, DEMO_SESSION_KEY, DEMO_USERNAME, demoCreate, demoGet,
  demoList, demoLoginData, demoRemove, demoReview, demoUpdate,
} from './demo'

const http = axios.create({ baseURL: import.meta.env.VITE_API_BASE || '', timeout: 12000 })
http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
http.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401) {
    clearSession()
    if (location.pathname !== '/login') location.assign('/login')
  }
  return Promise.reject(error)
})

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await http.request<ApiResponse<T>>(config)
  const body = response.data
  if (!body || body.code !== 200) throw new Error(body?.message || '请求失败')
  return body.data
}

async function backendAvailable(): Promise<boolean> {
  const base = import.meta.env.VITE_API_BASE?.replace(/\/$/, '')
  try {
    const response = await axios.get(`${base || ''}/api/health`, { timeout: 1500 })
    const contentType = String(response.headers['content-type'] || '')
    return contentType.includes('application/json') && typeof response.data === 'object'
  } catch (error) {
    if (!axios.isAxiosError(error) || !error.response) return false
    if (String(error.response.headers['x-backend-unavailable'] || '').toLowerCase() === 'true') return false
    return String(error.response.headers['content-type'] || '').includes('application/json')
  }
}

async function requireOfflineDemo(): Promise<boolean> {
  if (!isDemoSession()) return false
  if (!await verifyDemoOffline()) {
    window.location.assign('/login')
    throw new Error('检测到后端已连接。离线演示账号已停用，请使用后端账号登录。')
  }
  return true
}

export async function verifyDemoOffline(): Promise<boolean> {
  if (!isDemoSession()) return false
  if (await backendAvailable()) {
    clearSession()
    return false
  }
  return true
}

export const api = {
  async login(username: string, password: string) {
    if (username.trim() === DEMO_USERNAME && password === DEMO_PASSWORD) {
      if (await backendAvailable()) throw new Error('检测到后端已连接。离线演示账号仅限后端不可用时使用。')
      return demoLoginData()
    }
    return request<LoginData>({ url: '/api/auth/login', method: 'POST', data: { username, password } })
  },
  register: (data: Record<string, string>) => request<UserInfo>({ url: '/api/auth/register', method: 'POST', data }),
  async logout() {
    if (isDemoSession()) return
    return request<void>({ url: '/api/auth/logout', method: 'POST' })
  },
  async list(url: string) {
    if (await requireOfflineDemo()) return demoList(url)
    return request<CrmRecord[]>({ url, method: 'GET' })
  },
  async get(url: string) {
    if (await requireOfflineDemo()) {
      const found=demoGet(url);if(!found)throw new Error('演示数据不存在');return found
    }
    return request<CrmRecord>({ url, method: 'GET' })
  },
  async create(url: string, data: CrmRecord) {
    if (await requireOfflineDemo()) return demoCreate(url,data)
    return request<CrmRecord>({ url, method: 'POST', data })
  },
  async update(url: string, id: number, data: CrmRecord) {
    if (await requireOfflineDemo()) { const found=demoUpdate(url,id,data);if(!found)throw new Error('演示数据不存在');return found }
    return request<CrmRecord>({ url: `${url}/${id}`, method: 'PUT', data })
  },
  async remove(url: string, id: number) {
    if (await requireOfflineDemo()) {
      const roleMenu=url.startsWith('/api/role-menus/')
      const parent=roleMenu?Number(url.split('/').pop()):undefined
      if(!demoRemove(url,id,parent))throw new Error('演示数据不存在')
      return
    }
    return request<void>({ url: `${url}/${id}`, method: 'DELETE' })
  },
  async approve(id: number) {
    if (await requireOfflineDemo()) { if(!demoReview(id,true))throw new Error('演示申请不存在');return }
    return request<void>({ url: `/api/admin/registrations/${id}/approve`, method: 'POST' })
  },
  async reject(id: number) {
    if (await requireOfflineDemo()) { if(!demoReview(id,false))throw new Error('演示申请不存在');return }
    return request<void>({ url: `/api/admin/registrations/${id}/reject`, method: 'POST' })
  },
}

const REMEMBER_KEY = 'crm_remember'
const REMEMBER_USER_KEY = 'crm_remember_username'

/** 登录成功后写入会话：令牌只进 sessionStorage，关闭浏览器即失效，不落本地持久化存储。 */
export function saveSession(data: LoginData) {
  clearSession()
  if (data.token === 'local-demo-session-only') localStorage.setItem(DEMO_SESSION_KEY, '1')
  sessionStorage.setItem('crm_token', data.token)
  sessionStorage.setItem('crm_user', JSON.stringify(data.user))
  if (localStorage.getItem(REMEMBER_KEY) === '1') localStorage.setItem(REMEMBER_USER_KEY, data.user?.username || '')
}

/** “记住我”只记住用户名，不持久化令牌。 */
export function setRememberMe(remember: boolean) {
  if (remember) localStorage.setItem(REMEMBER_KEY, '1')
  else { localStorage.removeItem(REMEMBER_KEY); localStorage.removeItem(REMEMBER_USER_KEY) }
}

/** 上次勾选“记住我”时保存的用户名，用于登录页预填。 */
export function rememberedUsername() {
  return localStorage.getItem(REMEMBER_USER_KEY) || ''
}

export function getToken() {
  return sessionStorage.getItem('crm_token')
}

export function isDemoSession() { return localStorage.getItem(DEMO_SESSION_KEY) === '1' }

export function currentUser(): UserInfo | null {
  try { return JSON.parse(sessionStorage.getItem('crm_user') || 'null') as UserInfo | null }
  catch { return null }
}

export function clearSession() {
  // localStorage 中的令牌是旧版本残留，这里一并清理。
  localStorage.removeItem('crm_token')
  localStorage.removeItem('crm_user')
  localStorage.removeItem(DEMO_SESSION_KEY)
  sessionStorage.removeItem('crm_token')
  sessionStorage.removeItem('crm_user')
}

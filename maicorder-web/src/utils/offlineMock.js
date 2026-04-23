/**
 * 离线模式 Mock：不请求后端，所有 API 返回本地假数据，便于前端单独查看各页面样式。
 * 启用方式：npm run dev:offline 或 访问时带 ?offline=1
 */

import axios from 'axios'

const MOCK_USER = {
  id: 'offline-user-1',
  username: '离线测试用户',
  email: 'offline@test.local'
}

const MOCK_TOKEN = 'mock-token-offline-mode'

/** 模拟打卡记录列表 */
const MOCK_CHECKINS = [
  {
    id: 1,
    userId: 'offline-user-1',
    arcadeId: 1,
    arcadeName: '机厅A（Mock）',
    checkInDate: '2025-03-10',
    checkInTime: '2025-03-10 14:30:00',
    coinCost: 50,
    foodCost: 30,
    waterCost: 10,
    transportCost: 20,
    comment: '离线模式示例备注',
    gameSessions: [
      {
        gameType: 'MAIMAI_DX',
        pcCount: 2,
        currentRating: '14.5',
        records: [
          { songName: 'Example Song', score: '98.5%', clearStatus: 'FC' }
        ]
      }
    ]
  },
  {
    id: 2,
    userId: 'offline-user-1',
    arcadeId: 2,
    arcadeName: '机厅B（Mock）',
    checkInDate: '2025-03-12',
    checkInTime: '2025-03-12 18:00:00',
    coinCost: 80,
    foodCost: 0,
    waterCost: 5,
    transportCost: 15,
    comment: '',
    gameSessions: []
  }
]

/** 模拟单条记录详情（与列表项结构一致，可带更全的 gameSessions） */
const MOCK_CHECKIN_DETAIL = {
  id: 1,
  userId: 'offline-user-1',
  arcadeId: 1,
  arcadeName: '机厅A（Mock）',
  checkInDate: '2025-03-10',
  checkInTime: '2025-03-10 14:30:00',
  coinCost: 50,
  foodCost: 30,
  waterCost: 10,
  transportCost: 20,
  comment: '离线模式示例备注。可在此查看记录详情页样式。',
  gameSessions: [
    {
      gameType: 'MAIMAI_DX',
      pcCount: 2,
      currentRating: '14.5',
      records: [
        { songName: 'Example Song A', score: '98.5%', clearStatus: 'FC' },
        { songName: 'Example Song B', score: '95.0%', clearStatus: 'Clear' }
      ]
    },
    {
      gameType: 'CHUNITHM',
      pcCount: 1,
      currentRating: '15.2',
      records: [
        { songName: 'CHUNITHM 曲目', score: 'SS', clearStatus: 'AJ' }
      ]
    }
  ]
}

/** 模拟机厅列表 */
const MOCK_ARCADES = [
  { id: 1, name: '机厅A（Mock）', address: '离线地址1', province: '', city: '', district: '' },
  { id: 2, name: '机厅B（Mock）', address: '离线地址2', province: '', city: '', district: '' }
]

/** 模拟 B50 数据（用于前端离线样式展示） */
const MOCK_B50 = {
  standard_total: 1234,
  dx_total: 5678,
  standard: [
    {
      id: 834,
      song_name: '离线示例曲 A',
      level: '14+',
      level_index: 4,
      achievements: 98.5,
      fc: 'fc',
      fs: null,
      dx_score: 980000,
      type: 'standard'
    }
  ],
  dx: [
    {
      id: 999,
      song_name: '离线示例曲 B（DX）',
      level: '14+',
      level_index: 4,
      achievements: 100.0,
      fc: 'app',
      fs: 'fsd',
      dx_score: 1010000,
      type: 'dx'
    }
  ]
}

/** 根据请求 URL 和 method 返回 mock 数据 */
function getMockResponse(config) {
  const url = (config.url || '').replace(/^https?:\/\/[^/]+/, '')
  const method = (config.method || 'get').toLowerCase()

  // 登录 / 注册
  if (url.includes('/api/register') && method === 'post') {
    return { code: 200, data: { token: MOCK_TOKEN, user: MOCK_USER }, message: '注册成功（离线）' }
  }
  if (url.includes('/api/login') && method === 'post') {
    return { code: 200, data: { token: MOCK_TOKEN, user: MOCK_USER }, message: '登录成功（离线）' }
  }

  // 打卡记录列表
  if (url.includes('/api/records/checkins/') && method === 'get') {
    return { code: 200, data: MOCK_CHECKINS }
  }
  // 打卡记录详情
  if (url.includes('/api/records/checkin/') && method === 'get') {
    const id = url.split('/').filter(Boolean).pop()
    const record = MOCK_CHECKINS.find(c => String(c.id) === String(id)) || MOCK_CHECKIN_DETAIL
    return { code: 200, data: { ...MOCK_CHECKIN_DETAIL, ...record, id: record.id } }
  }

  // 机厅名称批量查询
  if (url.includes('/api/arcades/names') && method === 'post') {
    const ids = config.data || []
    const nameMap = {}
    ids.forEach(id => {
      const arcade = MOCK_ARCADES.find(a => Number(a.id) === Number(id))
      nameMap[id] = arcade ? arcade.name : `机厅${id}（Mock）`
    })
    return { code: 200, data: nameMap }
  }

  // 机厅列表
  if ((url.includes('/api/arcades') && !url.includes('/add-arcade')) && method === 'get') {
    return { code: 200, data: MOCK_ARCADES }
  }
  // 添加机厅
  if (url.includes('/api/arcades/add-arcade') && method === 'post') {
    const body = config.data || {}
    const newArcade = { id: 99, name: body.name || '新机厅', ...body }
    return { code: 200, data: newArcade, message: '添加成功（离线）' }
  }

  // 提交打卡
  if (url.includes('/api/checkins') && method === 'post') {
    return { code: 200, data: { id: Date.now() }, message: '提交成功（离线）' }
  }

  // 个人页：API Key、更新用户
  if (url.includes('/api/get-apikey') && method === 'get') {
    return { code: 200, data: 'mock-apikey-offline' }
  }
  if (url.includes('/api/update-user') && method === 'post') {
    return { code: 200, data: config.data, message: '更新成功（离线）' }
  }

  // 落雪 B50：不走你们自己的数据库，直接 mock 用于样式展示
  if (url.includes('/api/maimai/b50') && method === 'post') {
    return { code: 200, data: MOCK_B50, message: 'B50 获取成功（离线）' }
  }

  return null
}

let originalRequest = null

/** 启用离线 Mock：劫持 axios 的 request，返回 mock 数据 */
export function enableOfflineMock() {
  if (originalRequest) return
  originalRequest = axios.request.bind(axios)
  axios.request = function (config) {
    const mock = getMockResponse(config)
    if (mock != null) {
      return Promise.resolve({ data: mock, status: 200, config })
    }
    return originalRequest(config)
  }
  console.log('[离线模式] API 已切换为本地 Mock，可查看各页面样式，无需连接后端。')
}

/** 设置离线模式下的本地存储，使需要登录的页面可直接访问 */
export function setOfflineAuth() {
  localStorage.setItem('token', MOCK_TOKEN)
  localStorage.setItem('userId', MOCK_USER.id)
  localStorage.setItem('currentUser', JSON.stringify(MOCK_USER))
}

/** 检测是否应启用离线模式：构建时 VITE_OFFLINE 或 URL 参数 ?offline=1 */
export function shouldEnableOffline() {
  if (import.meta.env.VITE_OFFLINE === 'true' || import.meta.env.VITE_OFFLINE === true) {
    return true
  }
  try {
    return new URLSearchParams(window.location.search).get('offline') === '1'
  } catch {
    return false
  }
}

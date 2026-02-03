<!-- CheckInDialog.vue -->
<template>
  <div class="checkin-dialog-wrapper">
    <div class="checkin-dialog-content">
      <h2 class="dialog-title">🎮 今日出勤记录</h2>
      <div class="form-card">
        <h3 class="card-title">📍 基础信息</h3>
        <div class="form-row">
          <div class="form-col">
            <label class="form-label">选择机厅</label>
            <select v-model="form.arcadeId" class="form-select">
              <option value="" disabled>-- 请选择机厅 --</option>
              <option v-for="arcade in arcades" :key="arcade.id" :value="arcade.id">
                {{ arcade.name }}
              </option>
            </select>
          </div>
          <div class="form-col">
            <label class="form-label">记录日期</label>
            <input type="date" v-model="form.checkInDate" class="form-input" />
          </div>
        </div>
      </div>
      <div class="form-card">
        <h3 class="card-title">💰 今日开销</h3>
        <div class="cost-grid">
          <div class="cost-item">
            <label class="form-label">游戏投币</label>
            <input 
              type="number" 
              v-model.number="form.coinCost" 
              placeholder="0" 
              class="form-input small" 
              min="0"
            />
          </div>
          <div class="cost-item">
            <label class="form-label">餐饮费用</label>
            <input 
              type="number" 
              v-model.number="form.foodCost" 
              placeholder="0" 
              class="form-input small" 
              min="0"
            />
          </div>
          <div class="cost-item">
            <label class="form-label">饮品费用</label>
            <input 
              type="number" 
              v-model.number="form.waterCost" 
              placeholder="0" 
              class="form-input small" 
              min="0"
            />
          </div>
          <div class="cost-item">
            <label class="form-label">交通费用</label>
            <input 
              type="number" 
              v-model.number="form.transportCost" 
              placeholder="0" 
              class="form-input small" 
              min="0"
            />
          </div>
        </div>
        <div class="form-row mt-2">
          <div class="form-col full-width">
            <label class="form-label">备注信息</label>
            <input type="text" v-model="form.comment" placeholder="填写今日感想或其他备注..." class="form-input" />
          </div>
        </div>
      </div>
      <div class="form-card">
        <h3 class="card-title">🎮 游戏记录（Rating按游戏独立填写）</h3>
        <div class="add-session-btn-group">
          <button 
            v-for="(game, key) in GAME_CONFIG" 
            :key="key" 
            @click="addSession(key)" 
            class="btn add-game-btn"
          >
            + {{ game.label }}
          </button>
        </div>
        <div class="session-list">
          <div v-for="(session, sIndex) in form.gameSessions" :key="sIndex" class="session-card">
            <div class="session-header">
              <h4 class="session-game-name">{{ GAME_CONFIG[session.gameName].label }} 游戏记录</h4>
              <button @click="removeSession(sIndex)" class="btn del-btn small">删除场次</button>
            </div>
            <div class="form-row session-info">
              <div class="form-col">
                <label class="form-label">游玩PC数</label>
                <input 
                  type="number" 
                  v-model.number="session.pcCount" 
                  placeholder="0" 
                  class="form-input" 
                  min="0"
                />
              </div>
              <div class="form-col">
                <!-- 核心优化：明确标注Rating归属的游戏名 -->
                <label class="form-label">{{ GAME_CONFIG[session.gameName].label }} 今日段位/Rating</label>
                <input 
                  type="text" 
                  v-model="session.currentRating" 
                  placeholder="如：12345 / Master（非必填）" 
                  class="form-input" 
                />
              </div>
            </div>
            <div class="records-list mt-2">
              <h5 class="records-title">{{ GAME_CONFIG[session.gameName].label }} 战绩详情</h5>
              <div v-for="(record, rIndex) in session.records" :key="rIndex" class="form-row record-item">
                <div class="form-col flex-2">
                  <input type="text" v-model="record.songName" placeholder="曲名" class="form-input" />
                </div>
                <div class="form-col">
                  <input type="text" v-model="record.score" placeholder="分数/达成率" class="form-input" />
                </div>
                <div class="form-col">
                  <select v-model="record.clearStatus" class="form-select">
                    <option value="" disabled>-- 牌子 --</option>
                    <option v-for="lamp in GAME_CONFIG[session.gameName].lamps" :key="lamp" :value="lamp">
                      {{ lamp }}
                    </option>
                  </select>
                </div>
                <div class="form-col flex-1">
                  <button @click="removeRecord(sIndex, rIndex)" class="btn del-btn tiny">×</button>
                </div>
              </div>
              <button @click="addRecordToSession(sIndex)" class="btn add-btn mt-1">+ 添加{{ GAME_CONFIG[session.gameName].label }}战绩</button>
            </div>
          </div>
        </div>
      </div>
      <div class="submit-btn-group">
        <button @click="submitCheckIn" class="btn submit-btn">✅ 提交今日记录</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// #################### 核心配置 ####################
const API_BASE = '/api'
const GAME_CONFIG = ref({
  'MAIMAI_DX': { label: '舞萌 DX', lamps: ['鸟', '鸟+', 'FC', 'AP', 'FDX'] },
  'CHUNITHM': { label: '中二节奏', lamps: ['AJ', 'AJC', 'FC'] },
  'ONGREKI': { label: '音击', lamps: ['AB', 'FB'] },
  'IIDX': { label: 'IIDX', lamps: ['Hard', 'ExHard'] }
})

// #################### 父子组件通信 ####################
const emit = defineEmits(['close'])

// #################### 响应式数据 ####################
const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || {})
const arcades = ref([])
const form = ref({
  arcadeId: '',
  checkInDate: getTodayString(),
  coinCost: 0,       // 初始值改为数字0，避免空字符串
  foodCost: 0,       // 初始值改为数字0
  waterCost: 0,      // 初始值改为数字0
  transportCost: 0,  // 初始值改为数字0
  comment: '',
  gameSessions: []
})

// #################### 工具方法 ####################
function getTodayString() {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// #################### 接口请求 ####################
// 获取机厅列表（新增日志）
const fetchArcades = async () => {
  console.log('===== 开始获取机厅列表 =====')
  console.log('API_BASE:', API_BASE)
  console.log('请求头Authorization:', axios.defaults.headers.common['Authorization'])
  try {
    const res = await axios.get(`${API_BASE}/arcades`)
    console.log('机厅列表请求成功，响应数据：', res.data)
    arcades.value = res.data?.data?.filter(arcade => arcade.id && arcade.name) || []
    console.log('处理后机厅列表：', arcades.value)
  } catch (error) {
    console.error('===== 获取机厅列表失败 =====')
    console.error('错误类型：', error.name)
    console.error('错误信息：', error.message)
    console.error('响应状态：', error.response?.status)
    console.error('响应数据：', error.response?.data)
    alert('获取机厅列表失败，请检查网络或登录状态')
  }
}

// 提交今日出勤记录（修复费用数值传递，强化Rating与游戏绑定）
const submitCheckIn = async () => {
  console.log('===== 开始执行提交流程 =====')
  
  // 1. 表单校验
  console.log('===== 第一步：表单校验 =====')
  console.log('当前表单数据：', JSON.parse(JSON.stringify(form.value)))
  
  // 校验机厅
  if (!form.value.arcadeId) {
    console.error('校验失败：未选择机厅')
    alert('请先选择机厅！')
    return
  }
  
  // 校验游戏场次
  if (form.value.gameSessions.length === 0) {
    console.error('校验失败：未添加任何游戏场次')
    alert('请至少添加一个游戏场次记录！')
    return
  }
  
  // 校验每个场次的战绩
  const hasEmptySession = form.value.gameSessions.some(session => session.records.length === 0)
  if (hasEmptySession) {
    console.error('校验失败：存在无战绩的游戏场次')
    alert('每个游戏场次至少添加一条战绩记录！')
    return
  }
  
  console.log('表单校验通过 ✅')

  // 2. 构造提交数据（修复费用数值类型，Rating随游戏场次传递）
  console.log('===== 第二步：构造提交数据 =====')
  const submitData = {
    userId: currentUser.value.id || '',
    arcadeId: form.value.arcadeId,
    checkInDate: form.value.checkInDate,
    // 修复：直接传递数字类型，避免空字符串转0
    cost: {
      coin: Number(form.value.coinCost) || 0,
      food: Number(form.value.foodCost) || 0,
      water: Number(form.value.waterCost) || 0,
      transport: Number(form.value.transportCost) || 0
    },
    comment: form.value.comment || '',
    gameSessions: form.value.gameSessions.map(session => ({
      gameType: session.gameName,          // 游戏类型（如MAIMAI_DX）
      pcCount: Number(session.pcCount) || 0,
      currentRating: session.currentRating || '', // Rating与游戏类型绑定传递
      records: session.records.map(record => ({
        songName: record.songName || '',
        score: record.score || '',
        clearStatus: record.clearStatus || ''
      }))
    }))
  }
  
  console.log('构造完成的提交数据：', JSON.parse(JSON.stringify(submitData)))
  console.log('提交数据是否包含必填项：')
  console.log('  - userId：', !!submitData.userId)
  console.log('  - arcadeId：', !!submitData.arcadeId)
  console.log('  - 游戏场次数量：', submitData.gameSessions.length)
  console.log('  - 费用数据（数字类型）：', submitData.cost)

  // 3. 发送提交请求
  console.log('===== 第三步：发送提交请求 =====')
  console.log('请求地址：', `${API_BASE}/checkins`)
  console.log('请求方法：', 'POST')
  console.log('请求头：', {
    'Authorization': axios.defaults.headers.common['Authorization'],
    'Content-Type': axios.defaults.headers.common['Content-Type']
  })
  
  try {
    const res = await axios.post(`${API_BASE}/checkins`, submitData)
    console.log('===== 提交请求成功 =====')
    console.log('响应状态：', res.status)
    console.log('响应数据：', res.data)
    alert('今日出勤记录提交成功！🎉')
    
    // 4. 提交成功后处理
    console.log('===== 第四步：提交成功后重置表单并关闭弹窗 =====')
    resetForm()
    emit('close')
  } catch (error) {
    console.error('===== 提交请求失败 =====')
    console.error('错误类型：', error.name)
    console.error('错误信息：', error.message)
    console.error('响应状态：', error.response?.status)
    console.error('响应数据：', error.response?.data)
    console.error('错误栈：', error.stack)
    
    // 友好提示错误信息
    const errorMsg = error.response?.data?.message || '服务器内部错误，请联系管理员'
    alert(`提交记录失败：${errorMsg}`)
  }
}

// 重置表单（修复初始值为数字0）
const resetForm = () => {
  console.log('===== 重置表单为初始状态 =====')
  form.value = {
    arcadeId: '',
    checkInDate: getTodayString(),
    coinCost: 0,       // 初始值改为数字0
    foodCost: 0,       // 初始值改为数字0
    waterCost: 0,      // 初始值改为数字0
    transportCost: 0,  // 初始值改为数字0
    comment: '',
    gameSessions: []
  }
  console.log('表单重置完成，当前表单数据：', form.value)
}

// #################### 表单操作方法 ####################
const addSession = (gameKey) => {
  console.log(`===== 添加游戏场次：${GAME_CONFIG.value[gameKey].label} =====`)
  form.value.gameSessions.push({
    gameName: gameKey,
    pcCount: 0,        // 初始值改为数字0
    currentRating: '', // Rating初始为空（非必填）
    records: []
  })
}

const removeSession = (sIndex) => {
  if (confirm('确定要删除该游戏场次吗？删除后无法恢复！')) {
    console.log(`===== 删除第 ${sIndex} 个游戏场次 =====`)
    form.value.gameSessions.splice(sIndex, 1)
  }
}

const addRecordToSession = (sIndex) => {
  const gameKey = form.value.gameSessions[sIndex].gameName
  console.log(`===== 为第 ${sIndex} 个游戏场次（${GAME_CONFIG.value[gameKey].label}）添加战绩 =====`)
  form.value.gameSessions[sIndex].records.push({
    songName: '',
    score: '',
    clearStatus: ''
  })
}

const removeRecord = (sIndex, rIndex) => {
  const gameKey = form.value.gameSessions[sIndex].gameName
  console.log(`===== 删除第 ${sIndex} 个游戏场次（${GAME_CONFIG.value[gameKey].label}）的第 ${rIndex} 条战绩 =====`)
  form.value.gameSessions[sIndex].records.splice(rIndex, 1)
}

// #################### 页面挂载初始化 ####################
onMounted(() => {
  console.log('===== CheckInDialog 组件挂载初始化 =====')
  // 初始化axios
  const token = localStorage.getItem('token')
  console.log('本地存储token：', token ? '存在' : '不存在')
  if (token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
  }
  axios.defaults.headers.common['Content-Type'] = 'application/json'
  
  console.log('当前登录用户：', currentUser.value)
  // 获取机厅列表
  fetchArcades()
})
</script>

<style scoped>
/* 样式保持不变，仅优化部分文案展示 */
.checkin-dialog-wrapper {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 8px 0;
  box-sizing: border-box;
}

.checkin-dialog-content {
  width: 100%;
  max-width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-title {
  text-align: center;
  font-size: 1.8rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.form-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
  box-sizing: border-box;
}

.card-title {
  font-size: 1.2rem;
  color: #34495e;
  margin: 0 0 15px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f2f6;
}

.form-row {
  display: flex;
  gap: 15px;
  width: 100%;
  box-sizing: border-box;
}

.form-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-col.full-width {
  flex: 1 1 100%;
}

.form-col.flex-1 {
  flex: 1;
}

.form-col.flex-2 {
  flex: 2;
}

.form-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-weight: 500;
}

.form-input, .form-select {
  padding: 10px 12px;
  border: 1px solid #dcdde1;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #2c3e50;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-input:focus, .form-select:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.form-input.small {
  padding: 8px 10px;
  font-size: 0.85rem;
}

.mt-1 {
  margin-top: 10px;
}

.mt-2 {
  margin-top: 15px;
}

.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cost-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  width: 100%;
}

@media (min-width: 576px) {
  .cost-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.cost-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.add-session-btn-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.add-game-btn {
  background: #e8f4fd;
  color: #3498db;
}

.add-game-btn:hover {
  background: #d1e7fd;
  color: #2980b9;
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.session-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 15px;
  box-sizing: border-box;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.session-game-name {
  font-size: 1.1rem;
  color: #2c3e50;
  margin: 0;
}

.session-info {
  margin-bottom: 15px;
}

.records-title {
  font-size: 1rem;
  color: #34495e;
  margin: 0 0 10px 0;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.record-item {
  align-items: center;
}

.add-btn {
  background: #eafaf1;
  color: #2ecc71;
  align-self: flex-start;
}

.add-btn:hover {
  background: #d5f5e3;
  color: #27ae60;
}

.del-btn {
  background: #fef5f5;
  color: #e74c3c;
}

.del-btn:hover {
  background: #fdeaea;
  color: #c0392b;
}

.del-btn.small {
  padding: 6px 12px;
  font-size: 0.8rem;
}

.del-btn.tiny {
  padding: 8px 12px;
  font-size: 0.8rem;
  align-self: stretch;
}

.submit-btn-group {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.submit-btn {
  background: #2ecc71;
  color: #ffffff;
  padding: 12px 32px;
  font-size: 1rem;
}

.submit-btn:hover {
  background: #27ae60;
  box-shadow: 0 2px 8px rgba(46, 204, 113, 0.3);
}

.checkin-dialog-wrapper::-webkit-scrollbar {
  width: 6px;
}

.checkin-dialog-wrapper::-webkit-scrollbar-thumb {
  background-color: #dcdde1;
  border-radius: 3px;
}

.checkin-dialog-wrapper::-webkit-scrollbar-thumb:hover {
  background-color: #bdc3c7;
}
</style>
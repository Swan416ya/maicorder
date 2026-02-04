<!-- CheckInDialog.vue -->
<template>
  <div class="checkin-dialog-wrapper">
    <div class="checkin-dialog-content">
      <h2 class="dialog-title">🎮 今日出勤记录</h2>
      
      <div class="form-card">
        <h3 class="card-title">📍 基础信息</h3>
        <!-- class change: form-row stays, but css handles the stacking -->
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
              class="form-input" 
              min="0"
            />
          </div>
          <div class="cost-item">
            <label class="form-label">餐饮费用</label>
            <input 
              type="number" 
              v-model.number="form.foodCost" 
              placeholder="0" 
              class="form-input" 
              min="0"
            />
          </div>
          <div class="cost-item">
            <label class="form-label">饮品费用</label>
            <input 
              type="number" 
              v-model.number="form.waterCost" 
              placeholder="0" 
              class="form-input" 
              min="0"
            />
          </div>
          <div class="cost-item">
            <label class="form-label">交通费用</label>
            <input 
              type="number" 
              v-model.number="form.transportCost" 
              placeholder="0" 
              class="form-input" 
              min="0"
            />
          </div>
        </div>
        <div class="form-row mt-2">
          <div class="form-col">
            <label class="form-label">备注信息</label>
            <input type="text" v-model="form.comment" placeholder="填写今日感想或其他备注..." class="form-input" />
          </div>
        </div>
      </div>

      <div class="form-card">
        <h3 class="card-title">🎮 游戏记录</h3>
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
              <h4 class="session-game-name">{{ GAME_CONFIG[session.gameName].label }} 记录</h4>
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
                <label class="form-label">{{ GAME_CONFIG[session.gameName].label }} Rating</label>
                <input 
                  type="text" 
                  v-model="session.currentRating" 
                  placeholder="如：12345 / Master" 
                  class="form-input" 
                />
              </div>
            </div>

            <div class="records-section mt-2">
              <h5 class="records-title">战绩详情</h5>
              
              <div v-for="(record, rIndex) in session.records" :key="rIndex" class="record-card-vertical">
                <div class="form-col">
                  <label class="form-label-mini">曲名</label>
                  <input type="text" v-model="record.songName" placeholder="输入曲名" class="form-input" />
                </div>
                <div class="form-col">
                  <label class="form-label-mini">分数/达成率</label>
                  <input type="text" v-model="record.score" placeholder="输入分数" class="form-input" />
                </div>
                <div class="form-col">
                  <label class="form-label-mini">评价/牌子</label>
                  <select v-model="record.clearStatus" class="form-select">
                    <option value="" disabled>-- 选择牌子 --</option>
                    <option v-for="lamp in GAME_CONFIG[session.gameName].lamps" :key="lamp" :value="lamp">
                      {{ lamp }}
                    </option>
                  </select>
                </div>
                <button @click="removeRecord(sIndex, rIndex)" class="btn del-btn full-width mt-1">删除此条战绩</button>
              </div>

              <button @click="addRecordToSession(sIndex)" class="btn add-btn mt-1 full-width">+ 添加{{ GAME_CONFIG[session.gameName].label }}战绩</button>
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

const API_BASE = '/api'
const GAME_CONFIG = ref({
  'MAIMAI_DX': { label: '舞萌 DX', lamps: ['鸟', '鸟+', 'FC', 'AP', 'FDX'] },
  'CHUNITHM': { label: '中二节奏', lamps: ['AJ', 'AJC', 'FC'] },
  'ONGREKI': { label: '音击', lamps: ['AB', 'FB'] },
  'IIDX': { label: 'IIDX', lamps: ['Hard', 'ExHard'] }
})

const emit = defineEmits(['close'])
const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || {})
const arcades = ref([])
const form = ref({
  arcadeId: '',
  checkInDate: getTodayString(),
  coinCost: 0,
  foodCost: 0,
  waterCost: 0,
  transportCost: 0,
  comment: '',
  gameSessions: []
})

function getTodayString() {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const fetchArcades = async () => {
  try {
    const res = await axios.get(`${API_BASE}/arcades`)
    arcades.value = res.data?.data?.filter(arcade => arcade.id && arcade.name) || []
  } catch (error) {
    console.error('获取机厅失败', error)
  }
}

const submitCheckIn = async () => {
  if (!form.value.arcadeId) return alert('请选择机厅！')
  if (form.value.gameSessions.length === 0) return alert('请添加至少一个游戏！')

  const submitData = {
    userId: currentUser.value.id || '',
    arcadeId: form.value.arcadeId,
    checkInDate: form.value.checkInDate,
    cost: {
      coin: Number(form.value.coinCost) || 0,
      food: Number(form.value.foodCost) || 0,
      water: Number(form.value.waterCost) || 0,
      transport: Number(form.value.transportCost) || 0
    },
    comment: form.value.comment || '',
    gameSessions: form.value.gameSessions.map(session => ({
      gameType: session.gameName,
      pcCount: Number(session.pcCount) || 0,
      currentRating: session.currentRating || '',
      records: session.records.map(record => ({
        songName: record.songName || '',
        score: record.score || '',
        clearStatus: record.clearStatus || ''
      }))
    }))
  }

  try {
    await axios.post(`${API_BASE}/checkins`, submitData)
    alert('提交成功！')
    resetForm()
    emit('close')
  } catch (error) {
    alert('提交失败：' + (error.response?.data?.message || '未知错误'))
  }
}

const resetForm = () => {
  form.value = {
    arcadeId: '',
    checkInDate: getTodayString(),
    coinCost: 0, foodCost: 0, waterCost: 0, transportCost: 0,
    comment: '',
    gameSessions: []
  }
}

const addSession = (gameKey) => {
  form.value.gameSessions.push({
    gameName: gameKey, pcCount: 0, currentRating: '', records: []
  })
}

const removeSession = (index) => form.value.gameSessions.splice(index, 1)

const addRecordToSession = (sIndex) => {
  form.value.gameSessions[sIndex].records.push({ songName: '', score: '', clearStatus: '' })
}

const removeRecord = (sIndex, rIndex) => {
  form.value.gameSessions[sIndex].records.splice(rIndex, 1)
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
  fetchArcades()
})
</script>

<style scoped>
.checkin-dialog-wrapper {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 8px 0;
  box-sizing: border-box;
}

.checkin-dialog-content {
  width: 100%;
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

/* 核心修改：将Flex方向改为Column，实现竖着排列 */
.form-row {
  display: flex;
  flex-direction: column; /* 改为垂直 */
  gap: 15px;
  width: 100%;
  box-sizing: border-box;
}

.form-col {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%; /* 占满宽度 */
}

.form-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-weight: 500;
}

/* 新增：战绩里的小标签 */
.form-label-mini {
  font-size: 0.8rem;
  color: #95a5a6;
  margin-bottom: 2px;
}

.form-input, .form-select {
  padding: 12px; /* 稍微增加高度以适应单行显示 */
  border: 1px solid #dcdde1;
  border-radius: 8px;
  font-size: 1rem; /* 字体稍大，方便手机点击 */
  color: #2c3e50;
  outline: none;
  width: 100%;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.form-input:focus, .form-select:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.mt-1 { margin-top: 10px; }
.mt-2 { margin-top: 15px; }

.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

/* 核心修改：Cost Grid 改为单列 */
.cost-grid {
  display: grid;
  grid-template-columns: 1fr; /* 只有一列 */
  gap: 15px;
  width: 100%;
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
  flex: 1; /* 让按钮也稍微撑开一点 */
  min-width: 45%;
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

/* 新增：垂直排列的战绩卡片样式 */
.record-card-vertical {
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.records-title {
  font-size: 1rem;
  color: #34495e;
  margin: 0 0 10px 0;
}

.add-btn {
  background: #eafaf1;
  color: #2ecc71;
}

.del-btn {
  background: #fef5f5;
  color: #e74c3c;
}

.del-btn.small {
  padding: 6px 12px;
  font-size: 0.8rem;
}

.full-width {
  width: 100%;
}

.submit-btn-group {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.submit-btn {
  background: #2ecc71;
  color: #ffffff;
  padding: 14px 0;
  width: 100%; /* 提交按钮也改为全宽 */
  font-size: 1.1rem;
  border-radius: 10px;
}

.submit-btn:hover {
  background: #27ae60;
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
}

/* 滚动条美化 */
.checkin-dialog-wrapper::-webkit-scrollbar {
  width: 5px;
}
.checkin-dialog-wrapper::-webkit-scrollbar-thumb {
  background-color: #bdc3c7;
  border-radius: 3px;
}
</style>
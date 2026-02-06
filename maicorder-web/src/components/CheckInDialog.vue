<!-- CheckInDialog.vue -->
<template>
  <div class="checkin-dialog-wrapper">
    <div class="checkin-dialog-content">
      
      <!-- 页面大标题：粗直线-文字-粗直线 -->
      <div class="geo-header">
        <div class="thick-line"></div>
        <h2 class="header-text">今日出勤</h2>
        <div class="thick-line"></div>
      </div>

      <!-- 第一部分：时间与机厅 -->
      <div class="section-container">
        <!-- 日期选择 -->
        <div class="input-group">
          <label class="geo-label">DATE / 日期</label>
          <input type="date" v-model="form.checkInDate" class="geo-input" />
        </div>

        <!-- 机厅搜索选择 (自定义下拉组件) -->
        <div class="input-group relative-container" ref="arcadeContainer">
          <label class="geo-label">ARCADE / 机厅</label>
          <input 
            type="text" 
            v-model="arcadeSearchQuery" 
            @focus="showArcadeList = true"
            @input="handleArcadeInput"
            placeholder="输入搜索并点击选择..." 
            class="geo-input" 
          />
          <!-- 下拉列表 -->
          <ul class="geo-dropdown" v-if="showArcadeList && filteredArcades.length > 0">
            <li 
              v-for="arcade in filteredArcades" 
              :key="arcade.id" 
              @click="selectArcade(arcade)"
            >
              {{ arcade.name }}
            </li>
          </ul>
          <!-- 无结果提示 -->
          <ul class="geo-dropdown" v-if="showArcadeList && filteredArcades.length === 0 && arcadeSearchQuery">
            <li class="disabled">未找到匹配的机厅</li>
          </ul>
        </div>
      </div>

      <!-- 第二部分：今日开销 -->
      <div class="section-container">
        <div class="geo-sub-header">
          <div class="thin-line"></div>
          <span class="sub-title">COST / 开销</span>
          <div class="thin-line"></div>
        </div>

        <div class="cost-grid">
          <!-- 游戏币 -->
          <div class="cost-card">
            <div class="icon-area">
              <img :src="coinImg" alt="coin" />
            </div>
            <div class="input-area">
              <input type="number" v-model.number="form.coinCost" placeholder="投币" min="0" />
            </div>
          </div>

          <!-- 餐饮 -->
          <div class="cost-card">
            <div class="icon-area">
              <img :src="foodImg" alt="food" />
            </div>
            <div class="input-area">
              <input type="number" v-model.number="form.foodCost" placeholder="餐饮" min="0" />
            </div>
          </div>

          <!-- 饮品 -->
          <div class="cost-card">
            <div class="icon-area">
              <img :src="drinkImg" alt="drink" />
            </div>
            <div class="input-area">
              <input type="number" v-model.number="form.waterCost" placeholder="饮料" min="0" />
            </div>
          </div>

          <!-- 交通 -->
          <div class="cost-card">
            <div class="icon-area">
              <img :src="trafficImg" alt="traffic" />
            </div>
            <div class="input-area">
              <input type="number" v-model.number="form.transportCost" placeholder="交通" min="0" />
            </div>
          </div>
        </div>

        <!-- 备注长条 -->
        <div class="comment-row">
          <input type="text" v-model="form.comment" placeholder="今日感想 / 备注信息..." class="geo-input full-width" />
        </div>
      </div>

      <!-- 第三部分：游戏记录 -->
      <div class="section-container">
        <div class="geo-sub-header">
          <div class="thin-line"></div>
          <span class="sub-title">GAMES / 记录</span>
          <div class="thin-line"></div>
        </div>

        <!-- 游戏选择按钮组 (纯黑背景白字) -->
        <div class="game-select-group">
          <button 
            v-for="(game, key) in GAME_CONFIG" 
            :key="key" 
            @click="addSession(key)" 
            class="geo-black-btn"
          >
            + {{ game.label }}
          </button>
        </div>

        <!-- 游戏记录列表 -->
        <div class="session-list">
          <div v-for="(session, sIndex) in form.gameSessions" :key="sIndex" class="geo-session-card">
            <div class="session-top-bar">
              <span class="game-name">{{ GAME_CONFIG[session.gameName].label }}</span>
              <button @click="removeSession(sIndex)" class="text-btn">DELETE</button>
            </div>
            
            <div class="geo-row">
              <div class="geo-col">
                <label class="geo-label-mini">PC COUNT</label>
                <input type="number" v-model.number="session.pcCount" placeholder="0" class="geo-input-mini" />
              </div>
              <div class="geo-col">
                <label class="geo-label-mini">RATING</label>
                <input type="text" v-model="session.currentRating" placeholder="Rating" class="geo-input-mini" />
              </div>
            </div>

            <!-- 详细战绩 -->
            <div class="records-container">
              <div class="records-header" v-if="session.records.length > 0">
                <span>SONG</span>
                <span>SCORE</span>
                <span>LAMP</span>
                <span></span>
              </div>
              
              <div v-for="(record, rIndex) in session.records" :key="rIndex" class="record-item">
                <input type="text" v-model="record.songName" placeholder="曲名" class="geo-input-micro grow" />
                <input type="text" v-model="record.score" placeholder="分数" class="geo-input-micro fixed-width-score" />
                <select v-model="record.clearStatus" class="geo-select-micro fixed-width-lamp">
                  <option value="" disabled>-</option>
                  <option v-for="lamp in GAME_CONFIG[session.gameName].lamps" :key="lamp" :value="lamp">{{ lamp }}</option>
                </select>
                <button @click="removeRecord(sIndex, rIndex)" class="x-btn">×</button>
              </div>
            </div>

            <button @click="addRecordToSession(sIndex)" class="geo-outline-btn full-width">
              + 添加单曲成绩
            </button>
          </div>
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-area">
        <button @click="submitCheckIn" class="geo-submit-btn">
          SUBMIT / 提交记录
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// 导入图片
import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'

// 配置
const API_BASE = '/api'
const GAME_CONFIG = ref({
  'MAIMAI_DX': { label: 'MAIMAI', lamps: ['鸟', '鸟+', 'FC', 'AP', 'FDX'] },
  'CHUNITHM': { label: 'CHUNITHM', lamps: ['AJ', 'AJC', 'FC'] },
  'ONGREKI': { label: 'ONGREKI', lamps: ['AB', 'FB'] },
  'IIDX': { label: 'IIDX', lamps: ['Hard', 'ExHard'] }
})

const emit = defineEmits(['close'])

// 状态
const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || {})
const arcades = ref([])
const showArcadeList = ref(false)
const arcadeSearchQuery = ref('')
const arcadeContainer = ref(null)

const form = ref({
  arcadeId: '',
  checkInDate: '',
  coinCost: null, 
  foodCost: null,
  waterCost: null,
  transportCost: null,
  comment: '',
  gameSessions: []
})

// 工具：获取今天日期字符串 yyyy-MM-dd
function getTodayString() {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 机厅搜索逻辑
const filteredArcades = computed(() => {
  if (!arcadeSearchQuery.value) return arcades.value
  return arcades.value.filter(a => 
    a.name && a.name.toLowerCase().includes(arcadeSearchQuery.value.toLowerCase())
  )
})

const handleArcadeInput = () => {
  showArcadeList.value = true
  if (form.value.arcadeId) form.value.arcadeId = '' 
}

const selectArcade = (arcade) => {
  form.value.arcadeId = arcade.id
  arcadeSearchQuery.value = arcade.name
  showArcadeList.value = false
}

const handleClickOutside = (e) => {
  if (arcadeContainer.value && !arcadeContainer.value.contains(e.target)) {
    showArcadeList.value = false
  }
}

// 初始化
onMounted(() => {
  form.value.checkInDate = getTodayString()
  const token = localStorage.getItem('token')
  if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
  
  fetchArcades()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// API 调用
const fetchArcades = async () => {
  try {
    const res = await axios.get(`${API_BASE}/arcades`)
    const list = res.data?.data || res.data || []
    arcades.value = list.filter(arcade => arcade.id && arcade.name)
  } catch (error) {
    console.error('获取机厅失败', error)
  }
}

const submitCheckIn = async () => {
  if (!form.value.arcadeId) return alert('请选择有效的机厅！')
  if (form.value.gameSessions.length === 0) return alert('请至少记录一个游戏！')

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
    alert('✅ 提交成功！')
    emit('close')
  } catch (error) {
    console.error(error)
    const msg = error.response?.data?.message || '未知错误'
    alert(`提交失败：${msg}`)
  }
}

// 游戏会话管理
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
</script>

<style scoped>
/* 
  全局设计语言：Geometric / Brutalism 
  - 强制黑色字体：修复父级可能残留的白色字体设置
*/
* {
  box-sizing: border-box;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.checkin-dialog-wrapper {
  width: 100%;
  height: 100%;
  /* 修复：左右留出空隙，防止内容贴到羽化遮罩边缘 */
  padding: 0 10px;
  /* 修复：强制所有内容默认为黑色，解决下拉框和标题白色的问题 */
  color: #000;
}

.checkin-dialog-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 30px; /* 模块垂直间距 */
  /* 修复：头部和底部的额外留白，视觉更舒适 */
  padding-top: 30px;
  padding-bottom: 80px; 
}

/* --- Header --- */
.geo-header {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 100%;
  margin-top: 10px;
}

.thick-line {
  height: 4px;
  background-color: #000;
  flex-grow: 1;
}

.header-text {
  font-size: 1.5rem;
  font-weight: 900;
  color: #000;
  margin: 0;
  letter-spacing: 2px;
  white-space: nowrap;
}

/* --- Section Common --- */
.section-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.geo-sub-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.thin-line {
  height: 1px;
  background-color: #000;
  flex-grow: 1;
}

/* 修复：强制标题颜色为黑色 */
.sub-title {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 1px;
  color: #000; 
}

.geo-label {
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 5px;
  display: block;
  text-transform: uppercase;
  color: #000;
}

/* --- Inputs --- */
.geo-input {
  border: 1px solid #000;
  border-radius: 0;
  padding: 12px;
  font-size: 1rem;
  background: #fff;
  color: #000;
  outline: none;
  width: 100%;
  transition: background 0.2s;
}

.geo-input:focus {
  background: #f4f4f4;
}

.input-group {
  display: flex;
  flex-direction: column;
}

/* --- Arcade Dropdown --- */
.relative-container {
  position: relative;
}

.geo-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #000;
  border-top: none;
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 200px;
  overflow-y: auto;
  z-index: 20;
}

.geo-dropdown li {
  padding: 12px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
  /* 修复：强制列表文字为黑色，防止被父级白色覆盖 */
  color: #000; 
  background-color: #fff;
}

.geo-dropdown li:hover {
  background: #000;
  color: #fff;
}

.geo-dropdown li.disabled {
  color: #999;
  cursor: default;
}

/* --- Cost Grid (修复重构) --- */
.cost-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

@media (min-width: 500px) {
  .cost-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.cost-card {
  border: 1px solid #000;
  /* 使得卡片比较修长 */
  aspect-ratio: 3 / 4; 
  display: flex;
  flex-direction: column;
  background: #fff;
  padding: 10px; /* 给整个卡片内边距 */
  justify-content: space-between; /* 上下分布 */
}

/* 图标区域：加大留白，图标变小 */
.icon-area {
  flex: 1; 
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px; /* 增加padding让图标不贴边 */
}

.icon-area img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 输入区域：独立的矩形框 */
.input-area {
  height: 40px; /* 固定高度 */
  width: 100%;
}

.input-area input {
  width: 100%;
  height: 100%;
  border: 1px solid #000; /* 独立的边框 */
  border-radius: 0;
  text-align: center;
  font-size: 1rem;
  font-weight: bold;
  outline: none;
  background: #fff;
  color: #000;
}

.input-area input::placeholder {
  font-size: 0.8rem;
  font-weight: normal;
  color: #aaa;
}

.comment-row {
  margin-top: 5px;
}

/* --- Games --- */
.game-select-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.geo-black-btn {
  background: #000;
  color: #fff;
  border: 1px solid #000;
  padding: 12px 20px;
  font-weight: bold;
  cursor: pointer;
  flex-grow: 1;
  text-transform: uppercase;
  transition: opacity 0.2s;
}

.geo-black-btn:hover {
  opacity: 0.8;
}

.geo-session-card {
  border: 1px solid #000;
  padding: 15px;
  margin-top: 20px;
  background: #fff;
}

.session-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 2px solid #000;
  padding-bottom: 8px;
}

.game-name {
  font-weight: 900;
  font-size: 1.2rem;
  text-transform: uppercase;
  color: #000;
}

.text-btn {
  background: none;
  border: none;
  color: #000;
  text-decoration: underline;
  font-size: 0.8rem;
  cursor: pointer;
  font-weight: bold;
}

.geo-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.geo-col {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.geo-label-mini {
  font-size: 0.7rem;
  font-weight: bold;
  color: #666;
  margin-bottom: 4px;
}

.geo-input-mini {
  border: 1px solid #000;
  border-radius: 0;
  padding: 8px;
  font-size: 1rem;
  width: 100%;
  outline: none;
  color: #000;
}

/* Records List */
.records-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 10px;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.records-header {
  display: flex;
  font-size: 0.7rem;
  font-weight: bold;
  color: #000;
  padding-bottom: 4px;
  padding-left: 5px;
}
.records-header span:nth-child(1) { flex-grow: 1; }
.records-header span:nth-child(2) { width: 70px; }
.records-header span:nth-child(3) { width: 70px; }
.records-header span:nth-child(4) { width: 25px; }

.record-item {
  display: flex;
  gap: 8px;
  align-items: center;
}

.geo-input-micro, .geo-select-micro {
  border: 1px solid #ccc;
  border-radius: 0;
  padding: 6px;
  font-size: 0.9rem;
  outline: none;
  height: 34px;
  color: #000;
}

.geo-input-micro:focus, .geo-select-micro:focus {
  border-color: #000;
}

.grow { flex-grow: 1; }
.fixed-width-score { width: 70px; }
.fixed-width-lamp { width: 70px; }

.x-btn {
  background: #000;
  color: #fff;
  border: none;
  width: 25px;
  height: 34px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 1.2rem;
}

.geo-outline-btn {
  background: transparent;
  border: 1px solid #000;
  color: #000;
  padding: 10px;
  margin-top: 15px;
  font-weight: bold;
  cursor: pointer;
  width: 100%;
}

.geo-outline-btn:hover {
  background: #f0f0f0;
}

/* --- Submit --- */
.submit-area {
  margin-top: 20px;
}

.geo-submit-btn {
  width: 100%;
  padding: 18px;
  background: #000;
  color: #fff;
  border: none;
  font-size: 1.1rem;
  font-weight: 900;
  letter-spacing: 2px;
  cursor: pointer;
  transition: transform 0.1s;
}

.geo-submit-btn:active {
  transform: scale(0.99);
}

/* 隐藏下拉列表滚动条 */
.geo-dropdown::-webkit-scrollbar {
  width: 4px;
}
.geo-dropdown::-webkit-scrollbar-thumb {
  background: #ccc;
}
</style>
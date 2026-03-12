<!-- CheckInDialog.vue -->
<template>
  <div class="checkin-dialog-wrapper">
    <div class="checkin-dialog-content">
      
      <!-- 页面大标题 -->
      <div class="geo-header">
        <div class="thick-line"></div>
        <h2 class="header-text">今日出勤</h2>
        <div class="thick-line"></div>
      </div>

      <!-- 第一部分：时间与机厅 -->
      <div class="section-container">
        <div class="input-group">
          <div class="geo-label-sbyqb">DATE / 日期</div>
          <input type="date" v-model="form.checkInDate" class="geo-input" />
        </div>

        <div class="input-group relative-container" ref="arcadeContainer">
          <div class="geo-label-sbyqb">ARCADE / 机厅</div>
          <input 
            type="text" 
            v-model="arcadeSearchQuery" 
            @focus="showArcadeList = true"
            @input="handleArcadeInput"
            placeholder="输入搜索并点击选择..." 
            class="geo-input" 
          />
          <ul class="geo-dropdown" v-if="showArcadeList && filteredArcades.length > 0">
            <li 
              v-for="arcade in filteredArcades" 
              :key="arcade.id" 
              @click="selectArcade(arcade)"
            >
              {{ arcade.name }}
            </li>
          </ul>
          <ul class="geo-dropdown" v-if="showArcadeList && filteredArcades.length === 0 && arcadeSearchQuery">
            <li class="add-arcade" @click="showAddArcadeDialog = true">
              未找到匹配的机厅,点击添加机厅
            </li>
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
          <div class="cost-card">
            <div class="icon-area"><img :src="coinImg" alt="coin" /></div>
            <div class="input-area">
              <input type="number" v-model.number="form.coinCost" placeholder="投币" min="0" />
            </div>
          </div>
          <div class="cost-card">
            <div class="icon-area"><img :src="foodImg" alt="food" /></div>
            <div class="input-area">
              <input type="number" v-model.number="form.foodCost" placeholder="餐饮" min="0" />
            </div>
          </div>
          <div class="cost-card">
            <div class="icon-area"><img :src="drinkImg" alt="drink" /></div>
            <div class="input-area">
              <input type="number" v-model.number="form.waterCost" placeholder="饮料" min="0" />
            </div>
          </div>
          <div class="cost-card">
            <div class="icon-area"><img :src="trafficImg" alt="traffic" /></div>
            <div class="input-area">
              <input type="number" v-model.number="form.transportCost" placeholder="交通" min="0" />
            </div>
          </div>
        </div>

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

        <!-- 游戏选择按钮组 (Toggle开关) -->
        <div class="game-select-group">
          <button 
            v-for="(game, key) in GAME_CONFIG" 
            :key="key" 
            @click="toggleSession(key)" 
            class="geo-black-btn"
            :class="{ 'active': isGameActive(key) }"
          >
            {{ isGameActive(key) ? '−' : '+' }} {{ game.label }}
          </button>
        </div>

        <!-- 游戏记录列表 -->
        <div class="session-list">
          <div v-for="(session, sIndex) in form.gameSessions" :key="session.gameName" class="geo-session-card">
            
            <!-- 核心修改：Logo与输入框在同一行，无分割线 -->
            <div class="session-header-row">
              <!-- 左侧 Logo -->
              <div class="header-logo-container">
                <img :src="GAME_CONFIG[session.gameName].img" class="game-logo" alt="Logo" />
              </div>

              <!-- 右侧两个输入框 -->
              <div class="header-inputs-container">
                <div class="geo-col">
                  <div class="geo-label-mini">PC COUNT</div>
                  <input type="number" v-model.number="session.pcCount" placeholder="0" class="geo-input-mini" />
                </div>
                <div class="geo-col">
                  <div class="geo-label-mini">RATING</div>
                  <input type="text" v-model="session.currentRating" placeholder="Rating" class="geo-input-mini" />
                </div>
              </div>
            </div>

            <!-- 详细战绩 -->
            <div class="records-container">
              <div class="records-header" v-if="session.records.length > 0">
                <span class="col-song">SONG</span>
                <span class="col-score">SCORE</span>
                <span class="col-lamp">LAMP</span>
                <span class="col-del"></span>
              </div>
              
              <div v-for="(record, rIndex) in session.records" :key="rIndex" class="record-item">
                <!-- 曲名：占据剩余空间 -->
                <input type="text" v-model="record.songName" placeholder="曲名" class="geo-input-micro grow" />
                
                <!-- 分数：加宽 -->
                <input type="text" v-model="record.score" placeholder="分数 / 达成率" class="geo-input-micro fixed-width-score" />
                
                <!-- 牌子 -->
                <select v-model="record.clearStatus" class="geo-select-micro fixed-width-lamp">
                  <option value="" disabled>-</option>
                  <option v-for="lamp in GAME_CONFIG[session.gameName].lamps" :key="lamp" :value="lamp">{{ lamp }}</option>
                </select>
                
                <!-- 删除按钮 -->
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

      <!-- 添加机厅弹窗 -->
      <div v-if="showAddArcadeDialog" class="add-arcade-modal" @click.self="showAddArcadeDialog = false">
        <div class="modal-content">
          <h3>添加新机厅</h3>
          <div class="form-group">
            <label>机厅名称 <span class="required">*</span></label>
            <input v-model="newArcade.name" placeholder="请输入机厅名称" class="geo-input" />
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <input v-model="newArcade.address" placeholder="请输入详细地址" class="geo-input" />
          </div>
          <div class="form-row">
            <div class="form-group half">
              <label>省</label>
              <input v-model="newArcade.province" placeholder="如：北京市" class="geo-input" />
            </div>
            <div class="form-group half">
              <label>市</label>
              <input v-model="newArcade.city" placeholder="如：北京市" class="geo-input" />
            </div>
          </div>
          <div class="form-group">
            <label>区/县</label>
            <input v-model="newArcade.district" placeholder="如：朝阳区" class="geo-input" />
          </div>
          <div class="form-row">
            <div class="form-group half">
              <label>纬度</label>
              <input v-model.number="newArcade.latitude" type="number" step="0.000001" placeholder="可选" class="geo-input" />
            </div>
            <div class="form-group half">
              <label>经度</label>
              <input v-model.number="newArcade.longitude" type="number" step="0.000001" placeholder="可选" class="geo-input" />
            </div>
          </div>
          <div class="form-actions">
            <button @click="addArcade" class="geo-submit-btn" :disabled="!newArcade.name.trim() || isAddingArcade">
              {{ isAddingArcade ? '添加中...' : '确认添加' }}
            </button>
            <button @click="showAddArcadeDialog = false" class="geo-outline-btn" :disabled="isAddingArcade">
              取消
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// 导入基础图标
import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'

// 导入游戏Logo
import maimaiImg from '@/assets/games/maimai.png'
import chunithmImg from '@/assets/games/chunithm.png'
import iidxImg from '@/assets/games/iidx.png'
import ongekiImg from '@/assets/games/ongeki.png'

const API_BASE = '/api'

const GAME_CONFIG = ref({
  'MAIMAI_DX': { label: 'MAIMAI', img: maimaiImg, lamps: ['鸟', '鸟+', 'FC', 'AP', 'FDX'] },
  'CHUNITHM': { label: 'CHUNITHM', img: chunithmImg, lamps: ['AJ', 'AJC', 'FC'] },
  'ONGREKI': { label: 'ONGREKI', img: ongekiImg, lamps: ['AB', 'FB'] },
  'IIDX': { label: 'IIDX', img: iidxImg, lamps: ['Hard', 'ExHard'] }
})

const emit = defineEmits(['close'])
const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || {})
const arcades = ref([])
const showArcadeList = ref(false)
const arcadeSearchQuery = ref('')
const arcadeContainer = ref(null)
const showAddArcadeDialog = ref(false)
const isAddingArcade = ref(false)
const newArcade = ref({
  name: '',
  address: '',
  province: '',
  city: '',
  district: '',
  latitude: null,
  longitude: null
})

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

function getTodayString() {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

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

const isGameActive = (gameKey) => {
  return form.value.gameSessions.some(session => session.gameName === gameKey)
}

const toggleSession = (gameKey) => {
  const index = form.value.gameSessions.findIndex(session => session.gameName === gameKey)
  if (index !== -1) {
    form.value.gameSessions.splice(index, 1)
  } else {
    form.value.gameSessions.push({
      gameName: gameKey, 
      pcCount: null,
      currentRating: '', 
      records: []
    })
  }
}

const addRecordToSession = (sIndex) => {
  form.value.gameSessions[sIndex].records.push({ songName: '', score: '', clearStatus: '' })
}

const removeRecord = (sIndex, rIndex) => {
  form.value.gameSessions[sIndex].records.splice(rIndex, 1)
}

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

const addArcade = async () => {
  if (!newArcade.value.name.trim()) {
    alert('请输入机厅名称,厅名称不能为空')
    return
  }

  isAddingArcade.value = true
  try {
    const response = await axios.post('/api/arcades/add-arcade', newArcade.value);
    if (response.data.code === 200) {
      alert('✅ 添加成功！')
      await fetchArcades()
      // 自动选择新机厅
      const addedArcade = response.data.data
      if (addedArcade) {
        selectArcade(addedArcade)
      }
      showAddArcadeDialog.value = false
      // 重置表单
      newArcade.value = {
        name: '',
        address: '',
        province: '',
        city: '',
        district: '',
        latitude: null,
        longitude: null
      }
    } else {
      const msg = response.data.message || '添加失败'
      alert(`添加失败：${msg}`)
    }
  } catch (error) {
    console.error(error)
    const msg = error.response?.data?.message || '未知错误'
    alert(`添加失败：${msg}`)
  } finally {
    isAddingArcade.value = false
  }
}
</script>

<style scoped>
/* 全局设定 */
* {
  box-sizing: border-box;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}
.checkin-dialog-wrapper {
  width: 100%;
  height: 100%;
  padding: 0 10px;
  color: #000;
}
.checkin-dialog-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding-top: 30px;
  padding-bottom: 80px; 
}

/* Header */
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

/* Sections */
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
.sub-title {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 1px;
  color: #000; 
}
.geo-label-sbyqb {
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 5px;
  display: block;
  text-transform: uppercase;
  color: #000;
}

.geo-input:focus {
  background-color: #fff; /* 改为白色背景 */
  border-color: #ccc; /* 改为灰色边框 */
  box-shadow: none; /* 如果有阴影也去掉 */
}

/* Inputs */
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

/* Dropdown */
.relative-container { position: relative; }
.geo-dropdown {
  position: absolute;
  top: 100%; left: 0; right: 0;
  background: #fff;
  border: 1px solid #000;
  border-top: none;
  list-style: none;
  padding: 0; margin: 0;
  max-height: 200px;
  overflow-y: auto;
  z-index: 20;
}
.geo-dropdown li {
  padding: 12px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
  color: #000; 
  background-color: #fff;
}
.geo-dropdown li:hover { background: #000; color: #fff; }
.geo-dropdown li.disabled { color: #999; cursor: default; }

/* Cost Grid */
.cost-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}
@media (min-width: 500px) {
  .cost-grid { grid-template-columns: repeat(4, 1fr); }
}
.cost-card {
  border: 1px solid #000;
  aspect-ratio: 3 / 4; 
  display: flex;
  flex-direction: column;
  background: #fff;
  padding: 10px; 
  justify-content: space-between; 
}
.icon-area {
  flex: 1; 
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px; 
}
.icon-area img { width: 100%; height: 100%; object-fit: contain; }
.input-area { height: 40px; width: 100%; }
.input-area input {
  width: 100%; height: 100%;
  border: 1px solid #000; 
  border-radius: 0;
  text-align: center;
  font-size: 1rem;
  font-weight: bold;
  outline: none;
  background: #fff;
  color: #000;
}
.input-area input::placeholder { font-size: 0.8rem; font-weight: normal; color: #aaa; }
.comment-row { margin-top: 5px; }

/* Games Buttons */
.game-select-group { display: flex; flex-wrap: wrap; gap: 10px; }
.geo-black-btn {
  background: #000;
  color: #fff;
  border: 1px solid #000;
  padding: 12px 20px;
  font-weight: bold;
  cursor: pointer;
  flex-grow: 1;
  text-transform: uppercase;
  transition: all 0.2s;
  user-select: none;
}
.geo-black-btn:hover { opacity: 0.8; }
.geo-black-btn.active {
  background: #fff;
  color: #000;
  border: 2px solid #000;
}

/* Session Card 重构 */
.geo-session-card {
  border: 1px solid #000;
  padding: 15px;
  margin-top: 20px;
  background: #fff;
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 核心修改：头部布局 */
.session-header-row {
  display: flex;
  gap: 15px;
  align-items: flex-end; /* 让Logo底部和输入框底部对齐 */
  margin-bottom: 20px; /* 和下方列表拉开距离 */
}

/* Logo 容器 */
.header-logo-container {
  width: 60px; /* 固定宽度，防止被挤压 */
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: 4px; /* 微调对齐 */
}

.game-logo {
  width: 100%;
  height: auto; /* 自适应高度 */
  aspect-ratio: 1/1;
  object-fit: contain;
  display: block;
}

/* 输入框容器 */
.header-inputs-container {
  flex-grow: 1;
  display: flex;
  gap: 15px;
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
  border-top: 1px solid #eee; /* 保留战绩列表和上方数据的细微分割，不影响整体 */
  padding-top: 10px;
}

/* 战绩表头 */
.records-header {
  display: flex;
  font-size: 0.7rem;
  font-weight: bold;
  color: #000;
  padding-bottom: 4px;
  padding-left: 5px;
  gap: 8px; /* 与下方 item gap 保持一致 */
}

/* 定义列宽比例 */
.col-song { flex-grow: 1; }
.col-score { width: 120px; } /* 对应下方的 fixed-width-score */
.col-lamp { width: 70px; }
.col-del { width: 25px; }

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
.geo-input-micro:focus, .geo-select-micro:focus { border-color: #000; }

.grow { flex-grow: 1; }

/* 核心修改：加宽分数栏 */
.fixed-width-score { 
  width: 120px; /* 从70px加宽到120px，适应 101.0000% */
}
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
.geo-outline-btn:hover { background: #f0f0f0; }

/* Submit */
.submit-area { margin-top: 20px; }
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
.geo-submit-btn:active { transform: scale(0.99); }

.geo-dropdown::-webkit-scrollbar { width: 4px; }
.geo-dropdown::-webkit-scrollbar-thumb { background: #ccc; }

/* 添加机厅弹窗样式 */
.add-arcade-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  color: #333;
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #000;
  text-align: center;
  font-size: 1.2rem;
  font-weight: 900;
  letter-spacing: 1px;
}

.form-group {
  margin-bottom: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.required {
  color: #ff4d4f;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 25px;
}

/* 添加机厅选项样式 */
.add-arcade {
  color: #4cc9f0;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
  padding: 12px;
}

.add-arcade:hover {
  background: rgba(76, 201, 240, 0.1);
  padding-left: 20px;
}
</style>
<template>
  <div class="records-page-container">
    
    <!-- 顶部导航栏 -->
    <div class="nav-header">
      <button class="back-btn" @click="router.push('/main')">← BACK</button>
      <h2 class="page-title">HISTORY / 履历</h2>
    </div>

    <div class="divider-thick"></div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      LOADING DATA...
    </div>

    <!-- 空状态 -->
    <div v-else-if="records.length === 0" class="empty-state">
      <div class="empty-box">
        NO RECORDS FOUND
      </div>
    </div>

    <!-- 记录列表 -->
    <div v-else class="records-list">
      <div v-for="record in records" :key="record.id" class="record-card">
        
        <!-- 卡片头部：日期与机厅 -->
        <div class="card-header">
          <span class="record-date">{{ formatDate(record.checkInDate) }}</span>
          <!-- 如果后端返回了 arcadeName 直接用，否则显示 ID -->
          <span class="record-arcade">{{ record.arcadeName || 'UNKNOWN ARCADE' }}</span>
        </div>

        <div class="divider-thin"></div>

        <!-- 开销统计 -->
        <div class="cost-row">
          <div class="cost-item" v-if="record.cost?.coin > 0">
            <img :src="coinImg" class="cost-icon-small" />
            <span>{{ record.cost.coin }}</span>
          </div>
          <div class="cost-item" v-if="record.cost?.food > 0">
            <img :src="foodImg" class="cost-icon-small" />
            <span>{{ record.cost.food }}</span>
          </div>
          <div class="cost-item" v-if="record.cost?.water > 0">
            <img :src="drinkImg" class="cost-icon-small" />
            <span>{{ record.cost.water }}</span>
          </div>
          <div class="cost-item" v-if="record.cost?.transport > 0">
            <img :src="trafficImg" class="cost-icon-small" />
            <span>{{ record.cost.transport }}</span>
          </div>
          <!-- 如果全是0 -->
          <span v-if="isTotalZero(record.cost)" class="no-cost">NO COST</span>
        </div>

        <!-- 备注 -->
        <div class="comment-box" v-if="record.comment">
          "{{ record.comment }}"
        </div>

        <!-- 游戏会话列表 -->
        <div class="sessions-list" v-if="record.gameSessions && record.gameSessions.length > 0">
          <div v-for="(session, idx) in record.gameSessions" :key="idx" class="session-item">
            
            <!-- 游戏头部：Logo + 概览 -->
            <div class="session-header">
              <div class="logo-wrapper">
                <img :src="getGameLogo(session.gameType)" class="game-logo-small" />
              </div>
              <div class="session-meta">
                <span v-if="session.pcCount">PC: {{ session.pcCount }}</span>
                <span v-if="session.currentRating">RT: {{ session.currentRating }}</span>
              </div>
            </div>

            <!-- 战绩详情表格 -->
            <div class="records-table" v-if="session.records && session.records.length > 0">
              <div v-for="(play, pIdx) in session.records" :key="pIdx" class="play-row">
                <span class="play-song">{{ play.songName }}</span>
                <span class="play-score">{{ play.score }}</span>
                <span class="play-lamp" v-if="play.clearStatus">{{ play.clearStatus }}</span>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
    
    <!-- 底部占位，防止内容被遮挡 -->
    <div class="footer-spacer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// 引入图片资源
import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'
import maimaiImg from '@/assets/games/maimai.png'
import chunithmImg from '@/assets/games/chunithm.png'
import iidxImg from '@/assets/games/iidx.png'
import ongekiImg from '@/assets/games/ongeki.png'

const router = useRouter()
const records = ref([])
const loading = ref(true)

// 游戏配置映射
const GAME_IMG_MAP = {
  'MAIMAI_DX': maimaiImg,
  'CHUNITHM': chunithmImg,
  'ONGREKI': ongekiImg,
  'IIDX': iidxImg
}

const getGameLogo = (type) => {
  return GAME_IMG_MAP[type] || null
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  // 简单格式化 yyyy-mm-dd
  return dateStr.split('T')[0]
}

const isTotalZero = (cost) => {
  if (!cost) return true
  return (cost.coin || 0) + (cost.food || 0) + (cost.water || 0) + (cost.transport || 0) === 0
}

const fetchRecords = async () => {
  try {
    const token = localStorage.getItem('token')
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    // 假设你的后端获取列表接口是 GET /api/checkins
    const res = await axios.get('/api/checkins')
    // 根据实际后端返回结构调整，这里假设数据在 data.data 或 data 中
    records.value = res.data?.data || res.data || []
    
    // 如果需要按日期倒序
    records.value.sort((a, b) => new Date(b.checkInDate) - new Date(a.checkInDate))
  } catch (error) {
    console.error('Failed to load records:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
/* 全局容器：几何风格，黑白 */
.records-page-container {
  width: 100vw;
  min-height: 100vh;
  background-color: #fff;
  padding: 20px;
  box-sizing: border-box;
  color: #000;
  display: flex;
  flex-direction: column;
}

/* 导航头 */
.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.back-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 8px 16px;
  font-weight: bold;
  font-size: 0.9rem;
  cursor: pointer;
  text-transform: uppercase;
}

.page-title {
  font-weight: 900;
  font-size: 1.5rem;
  text-transform: uppercase;
  margin: 0;
}

/* 分割线 */
.divider-thick {
  width: 100%;
  height: 4px;
  background: #000;
  margin-bottom: 30px;
}

.divider-thin {
  width: 100%;
  height: 1px;
  background: #000;
  margin: 10px 0;
}

/* 状态显示 */
.loading-state, .empty-state {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  font-size: 1.2rem;
  color: #999;
}

.empty-box {
  border: 1px dashed #000;
  padding: 40px;
}

/* 记录卡片 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-card {
  border: 2px solid #000;
  padding: 15px;
  background: #fff;
  /* 粗野主义阴影 */
  box-shadow: 4px 4px 0px #000;
  transition: transform 0.1s;
}

.record-card:active {
  transform: translate(2px, 2px);
  box-shadow: 2px 2px 0px #000;
}

.card-header {
  display: flex;
  justify-content: space-between;
  font-weight: 900;
  font-size: 1.1rem;
  text-transform: uppercase;
}

/* 开销行 */
.cost-row {
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 5px 0;
  font-weight: bold;
}

.cost-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.cost-icon-small {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.no-cost {
  color: #999;
  font-size: 0.8rem;
}

/* 备注 */
.comment-box {
  background: #f0f0f0;
  border-left: 4px solid #000;
  padding: 8px;
  margin: 10px 0;
  font-style: italic;
  font-size: 0.9rem;
}

/* 游戏会话 */
.sessions-list {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.session-item {
  border: 1px solid #000;
  padding: 10px;
}

.session-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.game-logo-small {
  width: 30px;
  height: 30px;
  object-fit: contain;
}

.session-meta {
  font-size: 0.8rem;
  font-weight: bold;
  display: flex;
  gap: 10px;
}

.session-meta span {
  background: #000;
  color: #fff;
  padding: 2px 6px;
}

/* 战绩表格 */
.records-table {
  display: flex;
  flex-direction: column;
  gap: 5px;
  border-top: 1px solid #eee;
  padding-top: 5px;
}

.play-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  align-items: center;
}

.play-song {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: bold;
}

.play-score {
  font-family: monospace;
  margin: 0 10px;
}

.play-lamp {
  font-size: 0.7rem;
  border: 1px solid #000;
  padding: 1px 4px;
}

.footer-spacer {
  height: 40px;
}
</style>
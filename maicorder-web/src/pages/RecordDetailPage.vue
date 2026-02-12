<template>
  <BackGround>
    <div class="record-detail-container">
      <!-- 顶部导航，与record页保持一致 -->
      <div class="fixed-header">
        <div class="nav-header">
          <button class="back-btn" @click="goBack">← BACK</button>
          <h2 class="page-title">RECORD DETAIL / 记录详情</h2>
        </div>
        <div class="divider-thick"></div>
      </div>

      <!-- 内容区域，使用与表单页相同的结构 -->
      <div class="scroll-container">
        <div v-if="loading" class="loading-state">
          加载中...
        </div>
        
        <div v-else-if="!recordData" class="empty-state">
          记录不存在
        </div>
        
        <div v-else class="checkin-dialog-wrapper">
          <div class="checkin-dialog-content">
            
            <!-- 页面大标题 -->
            <div class="geo-header">
              <div class="thick-line"></div>
              <h2 class="header-text">出勤记录</h2>
              <div class="thick-line"></div>
            </div>

            <!-- 第一部分：时间与机厅 -->
            <div class="section-container">
              <div class="input-group">
                <div class="geo-label-sbyqb">DATE / 日期</div>
                <div class="detail-text">{{ formatDate(recordData.checkInDate || recordData.checkInTime) }}</div>
              </div>

              <div class="input-group">
                <div class="geo-label-sbyqb">ARCADE / 机厅</div>
                <div class="detail-text">{{ recordData.arcadeName || recordData.arcadeId || '未知' }}</div>
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
                    <div class="detail-text">¥{{ recordData.coinCost || 0 }}</div>
                  </div>
                </div>
                <div class="cost-card">
                  <div class="icon-area"><img :src="foodImg" alt="food" /></div>
                  <div class="input-area">
                    <div class="detail-text">¥{{ recordData.foodCost || 0 }}</div>
                  </div>
                </div>
                <div class="cost-card">
                  <div class="icon-area"><img :src="drinkImg" alt="drink" /></div>
                  <div class="input-area">
                    <div class="detail-text">¥{{ recordData.waterCost || 0 }}</div>
                  </div>
                </div>
                <div class="cost-card">
                  <div class="icon-area"><img :src="trafficImg" alt="traffic" /></div>
                  <div class="input-area">
                    <div class="detail-text">¥{{ recordData.transportCost || 0 }}</div>
                  </div>
                </div>
              </div>

              <div v-if="recordData.comment && recordData.comment.trim()" class="comment-row">
                <div class="detail-text comment-box">{{ recordData.comment.trim() }}</div>
              </div>
            </div>

            <!-- 第三部分：游戏记录 -->
            <div class="section-container" v-if="recordData.gameSessions && recordData.gameSessions.length > 0">
              <div class="geo-sub-header">
                <div class="thin-line"></div>
                <span class="sub-title">GAMES / 记录</span>
                <div class="thin-line"></div>
              </div>

              <!-- 游戏记录列表 -->
              <div class="session-list">
                <div v-for="(session, sIndex) in recordData.gameSessions" :key="sIndex" class="geo-session-card">
                  
                  <!-- Logo与游戏信息 -->
                  <div class="session-header-row">
                    <!-- 左侧 Logo -->
                    <div class="header-logo-container">
                      <img :src="getGameImage(session.gameType)" class="game-logo" alt="Logo" />
                    </div>

                    <!-- 右侧信息 -->
                    <div class="header-inputs-container">
                      <div class="geo-col">
                        <div class="geo-label-mini">PC COUNT</div>
                        <div class="detail-text">{{ session.pcCount || 0 }}</div>
                      </div>
                      <div class="geo-col">
                        <div class="geo-label-mini">RATING</div>
                        <div class="detail-text">{{ session.currentRating || '-' }}</div>
                      </div>
                    </div>
                  </div>

                  <!-- 详细战绩 -->
                  <div class="records-container" v-if="session.records && session.records.length > 0">
                    <div class="records-header">
                      <span class="col-song">SONG</span>
                      <span class="col-score">SCORE</span>
                      <span class="col-lamp">LAMP</span>
                    </div>
                    
                    <div v-for="(record, rIndex) in session.records" :key="rIndex" class="record-item detail-record">
                      <!-- 曲名 -->
                      <div class="detail-text song-name">{{ record.songName || '-' }}</div>
                      
                      <!-- 分数 -->
                      <div class="detail-text score-value">{{ record.score || '-' }}</div>
                      
                      <!-- 牌子 -->
                      <div class="detail-text lamp-value">{{ record.clearStatus || '-' }}</div>
                    </div>
                  </div>

                  <div v-else class="no-records">
                    无单曲成绩记录
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="no-game-sessions">
              无游戏记录
            </div>

            <!-- 总计消费 -->
            <div class="total-cost-section">
              <div class="total-label">总计消费</div>
              <div class="total-amount">¥{{ calculateTotal() }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </BackGround>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

import BackGround from '@/components/BackGround.vue'

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

const router = useRouter()
const route = useRoute()
const recordData = ref(null)
const loading = ref(true)

const GAME_CONFIG = {
  'MAIMAI_DX': { label: 'MAIMAI', img: maimaiImg },
  'CHUNITHM': { label: 'CHUNITHM', img: chunithmImg },
  'ONGREKI': { label: 'ONGREKI', img: ongekiImg },
  'IIDX': { label: 'IIDX', img: iidxImg },
  'MAIMAI': { label: 'MAIMAI', img: maimaiImg } // 兼容处理
}

const getGameImage = (gameType) => {
  return GAME_CONFIG[gameType]?.img || maimaiImg
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
  } catch (e) {
    return dateStr
  }
}

const calculateTotal = () => {
  if (!recordData.value) return 0
  const { coinCost = 0, foodCost = 0, waterCost = 0, transportCost = 0 } = recordData.value
  return coinCost + foodCost + waterCost + transportCost
}

const goBack = () => {
  router.push('/records')
}

const fetchRecordDetail = async () => {
  try {
    loading.value = true
    const recordId = route.params.id
    
    if (!recordId) {
      console.error('未提供记录ID')
      return
    }

    const token = localStorage.getItem('token')
    const headers = token ? { Authorization: `Bearer ${token}` } : {}
    
    // 获取记录详情
    const res = await axios.get(`/api/records/checkin/${recordId}`, { headers })
    
    if (res.data.code === 200) {
      recordData.value = res.data.data || res.data
      
      // 如果有arcadeId，尝试获取机厅名称
      if (recordData.value.arcadeId) {
        try {
          const arcadeRes = await axios.post('/api/arcades/names', [Number(recordData.value.arcadeId)], { headers })
          if (arcadeRes.data?.code === 200) {
            const nameMap = arcadeRes.data.data || {}
            recordData.value.arcadeName = nameMap[recordData.value.arcadeId] || `ID: ${recordData.value.arcadeId}`
          }
        } catch (arcadeError) {
          console.error('获取机厅名称失败：', arcadeError.message)
        }
      }
    } else {
      console.error('获取记录详情失败：', res.data.message)
    }
  } catch (error) {
    console.error('获取记录详情失败：', error.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecordDetail()
})
</script>

<style scoped>
.record-detail-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.fixed-header {
  flex-shrink: 0;
  padding: 20px 20px 0 20px;
}

.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px 20px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

.scroll-container::-webkit-scrollbar { 
  width: 4px; 
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.divider-thick {
  width: 100%;
  height: 4px;
  background: #000;
  margin-bottom: 20px;
}

.back-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 8px 16px;
  font-weight: bold;
  cursor: pointer;
}

.page-title {
  font-weight: 900;
  font-size: 1.5rem;
  text-transform: uppercase;
  margin: 0;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
  font-size: 1.2rem;
}

/* 复用 CheckInDialog 的样式，但调整为只读 */
.checkin-dialog-wrapper {
  width: 100%;
  padding: 0 10px;
  color: #000;
}

.checkin-dialog-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding-top: 20px;
  padding-bottom: 40px;
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
  gap: 20px;
}

.geo-sub-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
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

/* 详情文本样式 */
.detail-text {
  border: 1px solid #ddd;
  border-radius: 0;
  padding: 12px;
  font-size: 1rem;
  background: #f9f9f9;
  color: #333;
  min-height: 46px;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-group {
  display: flex;
  flex-direction: column;
}

/* Cost Grid */
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

.icon-area img { 
  width: 100%; 
  height: 100%; 
  object-fit: contain; 
}

.input-area { 
  height: 40px; 
  width: 100%; 
}

.input-area .detail-text {
  width: 100%; 
  height: 100%;
  border: 1px solid #000; 
  border-radius: 0;
  text-align: center;
  font-size: 1rem;
  font-weight: bold;
  background: #fff;
  color: #000;
  justify-content: center;
}

.comment-row { 
  margin-top: 10px; 
}

.comment-box {
  background-color: #ffffff; 
  padding: 12px 14px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  font-style: italic;
  min-height: 60px;
}

/* Session Card 详情样式 */
.geo-session-card {
  border: 1px solid #000;
  padding: 15px;
  margin-top: 20px;
  background: #fff;
}

.session-header-row {
  display: flex;
  gap: 15px;
  align-items: flex-end;
  margin-bottom: 20px;
}

.header-logo-container {
  width: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: 4px;
}

.game-logo {
  width: 100%;
  height: auto;
  aspect-ratio: 1/1;
  object-fit: contain;
  display: block;
}

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

/* Records List 详情样式 */
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
  padding-bottom: 8px;
  gap: 8px;
}

.col-song { 
  flex-grow: 1; 
}

.col-score { 
  width: 120px; 
}

.col-lamp { 
  width: 70px; 
}

.detail-record {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-record:last-child {
  border-bottom: none;
}

.song-name {
  flex-grow: 1;
  border: 1px solid #eee;
  padding: 8px;
  background: #fff;
  min-height: 36px;
}

.score-value {
  width: 120px;
  border: 1px solid #eee;
  padding: 8px;
  background: #fff;
  min-height: 36px;
  text-align: center;
}

.lamp-value {
  width: 70px;
  border: 1px solid #eee;
  padding: 8px;
  background: #fff;
  min-height: 36px;
  text-align: center;
}

.no-records,
.no-game-sessions {
  text-align: center;
  padding: 20px;
  color: #999;
  font-style: italic;
  border: 1px dashed #ddd;
  margin-top: 20px;
  background: #fafafa;
}

/* 总计消费 */
.total-cost-section {
  margin-top: 30px;
  padding: 20px;
  border-top: 2px solid #000;
  border-bottom: 2px solid #000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
}

.total-label {
  font-size: 1.2rem;
  font-weight: bold;
  color: #000;
  text-transform: uppercase;
}

.total-amount {
  font-size: 1.8rem;
  font-weight: 900;
  color: #000;
}
</style>
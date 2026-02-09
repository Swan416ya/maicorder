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
    <div v-else-if="checkIns.length === 0" class="empty-state">
      <div class="empty-box">
        NO CHECK-IN RECORDS FOUND
      </div>
    </div>

    <!-- 签到记录列表 -->
    <div v-else class="records-list">
      <PurpleCard 
        v-for="checkIn in checkIns" 
        :key="checkIn.id"
        variant="filled"
        :title="formatDate(checkIn.checkInTime)"
        :subTitle="`Arcade ID: ${checkIn.arcadeId}`"
        clickable
        @click="viewCheckInDetail(checkIn.id)"
      >
        <div class="checkin-details">
          <div class="cost-row">
            <div class="cost-item">
              <img :src="coinImg" alt="Coin" class="cost-icon-small" />
              <span>¥{{ checkIn.coinCost || 0 }}</span>
            </div>
            <div class="cost-item">
              <img :src="foodImg" alt="Food" class="cost-icon-small" />
              <span>¥{{ checkIn.foodCost || 0 }}</span>
            </div>
            <div class="cost-item">
              <img :src="drinkImg" alt="Drink" class="cost-icon-small" />
              <span>¥{{ checkIn.waterCost || 0 }}</span>
            </div>
            <div class="cost-item">
              <img :src="trafficImg" alt="Transport" class="cost-icon-small" />
              <span>¥{{ checkIn.transportCost || 0 }}</span>
            </div>
          </div>
          <div class="total-cost">
            Total: ¥{{ calculateTotal(checkIn) }}
          </div>
          <div >
            {{ checkIn.comment==''?'用户无评论':checkIn.comment}}
          </div>
        </div>
        <template #actions>
          <button class="action-btn">View Details</button>
        </template>
      </PurpleCard>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import PurpleCard from '@/components/PurpleCard.vue'

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
const checkIns = ref([])
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
  return dateStr
}

const calculateTotal = (checkIn) => {
  return (checkIn.coinCost || 0) + (checkIn.foodCost || 0) + (checkIn.waterCost || 0) + (checkIn.transportCost || 0)
}

const viewCheckInDetail = (checkInId) => {
  // 跳转到签到详情页
  router.push(`/checkin/detail/${checkInId}`)
}

const fetchCheckIns = async () => {
  try {
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')
    console.log('userId:', userId)
    
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    // 使用后端提供的接口获取签到记录
    const res = await axios.get(`/api/records/checkins/${userId}`)
    console.log('签到记录:', res.data)
    
    // 根据后端返回结构调整
    if (res.data.code === 200) {
      checkIns.value = res.data.data || []
    } else {
      console.error('Failed to load check-ins:', res.data.message)
    }
  } catch (error) {
    console.error('Failed to load check-ins:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCheckIns()
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

/* 签到详情样式 */
.checkin-details {
  line-height: 1.6;
}

.total-cost {
  font-weight: bold;
  margin-top: 10px;
  color: #6750a4;
}

.action-btn {
  border: none;
  background: transparent;
  color: #6750a4;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(103, 80, 164, 0.1);
}
</style>
<template>
  <BackGround>
    <div class="records-page-container">
      <!-- 1. 固定顶部区域 (Header + Divider) -->
      <div class="fixed-header">
        <div class="nav-header">
          <button class="back-btn" @click="router.push('/main')">← BACK</button>
          <h2 class="page-title">HISTORY / 履历</h2>
        </div>
        <div class="divider-thick"></div>
      </div>

      <!-- 2. 使用 RecordsCardList 组件 -->
      <RecordsCardList 
        :loading="loading" 
        :isEmpty="checkIns.length === 0"
        :gap="16"
        :showScrollbar="true"
      >
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
            <div>
              {{"Note: " + (checkIn.comment==''?'用户无评论':checkIn.comment)}}
            </div>
          </div>
        </PurpleCard>
      </RecordsCardList>
    </div>
  </BackGround>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import PurpleCard from '@/components/PurpleCard.vue'
import RecordsCardList from '@/components/RecordsCardList.vue'

// 引入图片资源
import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'
import BackGround from './BackGround.vue'

const router = useRouter()
const checkIns = ref([])
const loading = ref(true)

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr
}

const calculateTotal = (checkIn) => {
  return (checkIn.coinCost || 0) + (checkIn.foodCost || 0) + (checkIn.waterCost || 0) + (checkIn.transportCost || 0)
}

const viewCheckInDetail = (checkInId) => {
  router.push(`/checkin/detail/${checkInId}`)
}

const fetchCheckIns = async () => {
  try {
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')
    
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    const res = await axios.get(`/api/records/checkins/${userId}`)
    
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
.cost-icon-small {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

/* ================= 核心布局 ================= */

/* 1. 页面容器：限制高度为视口高度，禁止整体滚动 */
.records-page-container {
  width: 100vw;
  height: 100vh; /* 关键：固定高度 */
  display: flex;
  flex-direction: column; /* 垂直排列 */
  overflow: hidden; /* 防止出现双重滚动条 */
}

/* 2. 头部区域：防止被压缩 */
.fixed-header {
  flex-shrink: 0;
  padding: 20px 20px 0 20px; /* Padding 移到这里 */
  background-color: #fff;
  z-index: 10; /* 确保阴影在滚动内容之上（如果加阴影的话） */
}

/* ================= 内容样式 ================= */

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

/* 卡片内部微调 */
.checkin-details {
  font-size: 14px;
  color: #49454f;
}

.cost-row {
  display: flex;
  gap: 12px;
  margin-bottom: 4px;
}

.total-cost {
  font-weight: 700;
  color: #6750a4;
  margin-top: 8px;
}
</style>
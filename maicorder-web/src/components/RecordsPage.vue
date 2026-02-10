<template>
  <BackGround>
  <div class="records-page-container">
    
    <!-- 1. 固定顶部区域 (Header + Divider) -->
    <!-- flex-shrink: 0 防止被压缩 -->
    <div class="fixed-header">
      <div class="nav-header">
        <button class="back-btn" @click="router.push('/main')">← BACK</button>
        <h2 class="page-title">HISTORY / 履历</h2>
      </div>
      <div class="divider-thick"></div>
    </div>

    <!-- 2. 可滚动父组件 (Scroll Container) -->
    <!-- 占据剩余高度，内容溢出时滚动 -->
    <div class="scroll-container">
      
      <!-- 加载状态 (居中显示) -->
      <div v-if="loading" class="state-center">
        LOADING DATA...
      </div>

      <!-- 空状态 (居中显示) -->
      <div v-else-if="checkIns.length === 0" class="state-center">
        <div class="empty-box">
          NO CHECK-IN RECORDS FOUND
        </div>
      </div>

      <!-- 卡片列表内容 -->
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
            {{"Note: " + (checkIn.comment==''?'用户无评论':checkIn.comment)}}
          </div>
        </div>
      </PurpleCard>
        
        <!-- 底部垫片，防止最后一个卡片被遮挡 -->
        <div class="list-footer-spacer"></div>
      </div>
      
    </div>
  </div>
  </BackGround>
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
import BackGround from './BackGround.vue'

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
  /* background-color: #fff; */
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

/* 3. 滚动父组件：占据剩余空间 + 内部滚动 */
.scroll-container {
  flex: 1; /* 自动填满剩余垂直空间 */
  overflow-y: auto; /* 允许垂直滚动 */
  padding: 0 20px; /* 内容左右内边距 */
  
  /* 优化滚动体验 */
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch; /* iOS 惯性滚动 */
}

/* ================= 滚动条美化 (Chrome/Safari) ================= */
.scroll-container::-webkit-scrollbar {
  width: 6px;
}

.scroll-container::-webkit-scrollbar-track {
  background: transparent;
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(103, 80, 164, 0.2); /* 浅紫色 */
  border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-thumb:hover {
  background-color: rgba(103, 80, 164, 0.5); /* 深紫色 */
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

/* 列表容器 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 16px; /* 卡片间距 */
  padding-top: 10px;
}

/* 列表底部留白 */
.list-footer-spacer {
  height: 40px; 
}

/* 状态居中显示 */
.state-center {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  font-size: 1.2rem;
  color: #999;
  min-height: 200px;
}

.empty-box {
  border: 1px dashed #000;
  padding: 40px;
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

.cost-label {
  font-weight: 600;
  margin-right: 4px;
}

.total-cost {
  font-weight: 700;
  color: #6750a4;
  margin-top: 8px;
}

.comment-text {
  margin-top: 8px;
  font-style: italic;
  opacity: 0.8;
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
  background: rgba(103, 80, 164, 0.08);
}

/* 简单的进入动画 */
.record-item-anim {
  animation: fadeIn 0.5s ease forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
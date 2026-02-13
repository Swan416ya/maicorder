<template>
  <BackGround>
    <div class="records-page-container" :class="{'wide-screen': isWideScreen}">
      <div class="fixed-header">
        <div class="nav-header">
          <button class="back-btn" @click="router.push('/main')">← BACK</button>
          <h2 class="page-title">HISTORY / 履历</h2>
        </div>
        <div class="divider-thick"></div>
      </div>

      <!-- 宽屏布局：左右分割 -->
      <div v-if="isWideScreen" class="wide-layout">
        <div class="calendar-section">
          <CheckInCalendar :check-ins="checkIns" />
        </div>
        <!-- 宽屏竖分割线 -->
        <div class="vertical-divider"></div>
        <div class="list-section">
          <CardList :items="checkIns" :loading="loading">
            <template #default="{ item: checkIn }">
              <PurpleCard 
                variant="filled"
                :title="formatDate(checkIn.checkInTime)"
                :subTitle="checkIn.arcadeName || checkIn.arcadeId || '未知'"
                clickable
                @click="viewCheckInDetail(checkIn.id)"
              >
                <div class="checkin-details">
                  <!-- 5等分消费容器（4项+total） -->
                  <div class="cost-container">
                    <div class="cost-item">
                      <img :src="coinImg" alt="Coin" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.coinCost || 0 }}</span>
                    </div>
                    <div class="cost-item">
                      <img :src="foodImg" alt="Food" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.foodCost || 0 }}</span>
                    </div>
                    <div class="cost-item">
                      <img :src="drinkImg" alt="Drink" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.waterCost || 0 }}</span>
                    </div>
                    <div class="cost-item">
                      <img :src="trafficImg" alt="Transport" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.transportCost || 0 }}</span>
                    </div>
                    <!-- Total项：最后1/5宽度，改为黑色文字 -->
                    <div class="cost-item total-item">
                      <span class="total-text">¥{{ calculateTotal(checkIn) }}</span>
                    </div>
                  </div>

                  <!-- 分隔线和Note区域 -->
                  <template v-if="checkIn.comment && checkIn.comment.trim()">
                    <!-- 分隔线 -->
                    <div class="comment-divider"></div>
                    <!-- Note区域 -->
                    <div class="comment-section">
                      <div class="comment-title">NOTE</div>
                      <div class="comment-content">{{ checkIn.comment.trim() }}</div>
                    </div>
                  </template>
                </div>
              </PurpleCard>
            </template>

            <template #empty>
              <div class="empty-box">
                NO CHECK-IN RECORDS FOUND
              </div>
            </template>
          </CardList>
        </div>
      </div>

      <!-- 窄屏布局：上下排列 -->
      <div v-else class="narrow-layout">
        <div class="calendar-top">
          <CheckInCalendar :check-ins="checkIns" />
        </div>
        <!-- 窄屏横分割线 -->
        <div class="horizontal-divider"></div>
        <div class="scroll-container">
          <CardList :items="checkIns" :loading="loading">
            <template #default="{ item: checkIn }">
              <PurpleCard 
                variant="filled"
                :title="formatDate(checkIn.checkInTime)"
                :subTitle="checkIn.arcadeName || checkIn.arcadeId || '未知'"
                clickable
                @click="viewCheckInDetail(checkIn.id)"
              >
                <div class="checkin-details">
                  <div class="cost-container">
                    <div class="cost-item">
                      <img :src="coinImg" alt="Coin" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.coinCost || 0 }}</span>
                    </div>
                    <div class="cost-item">
                      <img :src="foodImg" alt="Food" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.foodCost || 0 }}</span>
                    </div>
                    <div class="cost-item">
                      <img :src="drinkImg" alt="Drink" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.waterCost || 0 }}</span>
                    </div>
                    <div class="cost-item">
                      <img :src="trafficImg" alt="Transport" class="cost-icon-small" />
                      <span class="yuan-text">¥{{ checkIn.transportCost || 0 }}</span>
                    </div>
                    <div class="cost-item total-item">
                      <span class="total-text">¥{{ calculateTotal(checkIn) }}</span>
                    </div>
                  </div>

                  <template v-if="checkIn.comment && checkIn.comment.trim()">
                    <div class="comment-divider"></div>
                    <div class="comment-section">
                      <div class="comment-title">NOTE</div>
                      <div class="comment-content">{{ checkIn.comment.trim() }}</div>
                    </div>
                  </template>
                </div>
              </PurpleCard>
            </template>

            <template #empty>
              <div class="empty-box">
                NO CHECK-IN RECORDS FOUND
              </div>
            </template>
          </CardList>
        </div>
      </div>
    </div>
  </BackGround>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import BackGround from '../components/BackGround.vue'
import CardList from '../components/CardList.vue'
import PurpleCard from '@/components/PurpleCard.vue'
import CheckInCalendar from '@/components/CheckInCalendar.vue'

import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'

const router = useRouter()
const checkIns = ref([])
const loading = ref(true)
const isWideScreen = ref(false) // 是否宽屏布局（宽高比>0.9）

// 检测宽高比并更新布局
const checkLayout = () => {
  const width = window.innerWidth
  const height = window.innerHeight
  isWideScreen.value = (width / height) > 0.9 // 改回0.9
  console.log(`检测布局: ${width}x${height}, 宽高比: ${(width/height).toFixed(2)}, 是否宽屏: ${isWideScreen.value}`)
}

// 监听窗口大小变化
const handleResize = () => {
  checkLayout()
}

// 初始化时检测
onMounted(() => {
  checkLayout()
  window.addEventListener('resize', handleResize)
  fetchCheckIns()
})

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
  } catch (e) {
    return dateStr
  }
}

const calculateTotal = (c) => (c.coinCost || 0) + (c.foodCost || 0) + (c.waterCost || 0) + (c.transportCost || 0)
const viewCheckInDetail = (id) => router.push(`/checkin/detail/${id}`)

const fetchCheckIns = async () => {
  try {
    loading.value = true
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')

    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    }

    const res = await axios.get(`/api/records/checkins/${userId}`)
    if (res.data.code === 200) {
      checkIns.value = res.data.data || []
      console.log('✅ 打卡记录查询成功：', checkIns.value)

      if (checkIns.value.length > 0) {
        try {
          const arcadeIds = checkIns.value
            .map(item => {
              const id = Number(item.arcadeId)
              return isNaN(id) ? null : id
            })
            .filter(id => id !== null)
            .filter((id, index, self) => self.indexOf(id) === index)

          console.log('📋 提取的机厅ID：', arcadeIds)

          if (arcadeIds.length > 0) {
            const nameRes = await axios.post('/api/arcades/names', arcadeIds)
            console.log('📦 机厅名称接口返回：', nameRes.data)

            if (nameRes.data && nameRes.data.code === 200) {
              let nameMap = {};
              const rawData = nameRes.data.data || {};

              // 如果是对象，直接用
              if (typeof rawData === 'object' && !Array.isArray(rawData)) {
                nameMap = rawData;
              } 
              // 如果是数组，转换成对象
              else if (Array.isArray(rawData)) {
                rawData.forEach(item => {
                  // 按后端实际字段名调整
                  nameMap[Number(item.id)] = item.name; 
                });
              }

              checkIns.value = checkIns.value.map(item => ({
                ...item,
                arcadeName: nameMap[Number(item.arcadeId)] || `ID: ${item.arcadeId}`
              }));
            }
          }
        } catch (nameError) {
          console.error('机厅名称查询失败：', nameError.message)
        }
      }
    }
  } catch (error) {
    console.error('打卡记录查询失败：', error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.yuan-text {
  font-size: 20px;
}

.records-page-container {
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

/* ========== 宽屏布局（宽高比>0.9）：左右分割 ========== */
.wide-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
  padding: 0 20px 20px 20px;
  gap: 0; /* 移除gap，通过分割线控制间距 */
}

.calendar-section {
  flex: 0 0 35%; /* 左边35%宽度 */
  max-width: 35%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding-right: 20px; /* 给分割线留出空间 */
}

/* 宽屏竖分割线 */
.vertical-divider {
  width: 2px;
  background: linear-gradient(to bottom, 
    transparent 0%, 
    rgba(0, 0, 0, 0.1) 10%, 
    rgba(0, 0, 0, 0.3) 50%, 
    rgba(0, 0, 0, 0.1) 90%, 
    transparent 100%);
  margin: 0 10px;
  height: calc(100% - 20px); /* 减去底部边距 */
  align-self: center;
  border-radius: 1px;
}

.list-section {
  flex: 1; /* 右边65%宽度 */
  overflow-y: auto;
  padding-left: 20px; /* 给分割线留出空间 */
  padding-bottom: 20px;
}

.list-section::-webkit-scrollbar {
  width: 4px;
}

.list-section::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

/* ========== 窄屏布局（宽高比<=0.9）：上下排列 ========== */
.narrow-layout {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.calendar-top {
  padding: 0 20px 20px 20px;
  flex-shrink: 0;
}

/* 窄屏横分割线 */
.horizontal-divider {
  height: 2px;
  background: linear-gradient(to right, 
    transparent 0%, 
    rgba(0, 0, 0, 0.1) 10%, 
    rgba(0, 0, 0, 0.3) 50%, 
    rgba(0, 0, 0, 0.1) 90%, 
    transparent 100%);
  margin: 0 20px 20px 20px;
  border-radius: 1px;
}

/* 窄屏布局的滚动容器 */
.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px 20px 20px;
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

/* ========== 列表项内部样式 ========== */
.checkin-details { 
  font-size: 14px; 
  color: #49454f; 
  width: 100%;
}

/* 5等分消费容器 */
.cost-container {
  display: flex;
  width: 100%;
  margin: 0 0 12px 0;
  padding: 0;
  gap: 0;
}

.cost-item {
  flex: 1 0 20%;
  max-width: 20%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px 0;
  box-sizing: border-box;
}

.cost-icon-small { 
  width: 15px; 
  height: 15px; 
  object-fit: contain; 
  margin-bottom: 4px; 
}

.total-item .total-text {
  color: #000000;
  font-weight: 800;
  font-size: 22px;
}

/* Note样式 */
.comment-divider {
  width: 100%;
  height: 1px;
  background-color: #e0e0e0;
  margin: 12px 0;
}

.comment-section {
  margin-top: 8px;
  padding: 8px 0;
  width: 100%;
}

.comment-title {
  font-size: 12px;
  font-weight: bold;
  color: #666;
  text-transform: uppercase;
  margin-bottom: 4px;
  letter-spacing: 1px;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  font-style: italic;
  white-space: pre-line;
  word-break: break-word;
}

.empty-box {
  text-align: center;
  padding: 40px;
  border: 2px dashed #ccc;
  border-radius: 20px;
  color: #888;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .wide-layout {
    padding: 0 15px 15px 15px;
  }
  
  .calendar-section {
    flex: 0 0 40%;
    max-width: 40%;
    padding-right: 15px;
  }
  
  .vertical-divider {
    margin: 0 5px;
  }
  
  .list-section {
    padding-left: 15px;
  }
  
  .horizontal-divider {
    margin: 0 15px 15px 15px;
  }
  
  .scroll-container {
    padding: 0 15px 15px 15px;
  }
}
</style>
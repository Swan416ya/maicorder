<template>
  <BackGround>
    <StarTransition v-if="appLoading" @finished="appLoading = false" />

    <div class="records-page-container" v-if="!appLoading">
      <div class="fixed-header">
        <div class="nav-header">
          <button class="back-btn" @click="router.push('/main')">← BACK</button>
          <h2 class="page-title">HISTORY / 履历</h2>
        </div>
        <div class="divider-thick"></div>
      </div>

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
  </BackGround>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import BackGround from '../components/BackGround.vue'
import StarTransition from '../components/StarTransition.vue'
import CardList from '../components/CardList.vue'
import PurpleCard from '@/components/PurpleCard.vue'

import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'

const router = useRouter()
const checkIns = ref([])
const loading = ref(true)
const appLoading = ref(true)

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

onMounted(() => {
  fetchCheckIns()
})
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
.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}
.scroll-container::-webkit-scrollbar { width: 4px; }
.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2); /* 改为黑色 */
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
.checkin-details { 
  font-size: 14px; 
  color: #49454f; 
  width: 100%;
}

/* ========== 核心优化：5等分消费容器 ========== */
.cost-container {
  display: flex;
  width: 100%;
  margin: 0 0 12px 0;
  padding: 0;
  gap: 0; /* 无间距，严格5等分 */
}
/* 每个消费项强制占1/5宽度，内部完全居中 */
.cost-item {
  flex: 1 0 20%; /* 强制20%宽度，不可收缩/扩展 */
  max-width: 20%;
  display: flex;
  flex-direction: column;
  align-items: center; /* 水平居中 */
  justify-content: center; /* 垂直居中 */
  padding: 4px 0;
  box-sizing: border-box;
}
.cost-icon-small { 
  width: 15px; 
  height: 15px; 
  object-fit: contain; 
  margin-bottom: 4px; 
}
/* Total项特殊样式：改为黑色加粗 */
.total-item .total-text {
  color: #000000; /* 改为黑色 */
  font-weight: 800;
  font-size: 22px; /* 可选：比其他项稍大，更突出 */
}

/* ========== Note新样式：分隔线+标题+内容 ========== */
.comment-divider {
  width: 100%;
  height: 1px;
  background-color: #e0e0e0; /* 灰色分隔线 */
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
  white-space: pre-line; /* 保留换行符 */
  word-break: break-word;
}

/* 删除原来的.comment-box样式 */
.empty-box {
  text-align: center;
  padding: 40px;
  border: 2px dashed #ccc;
  border-radius: 20px;
  color: #888;
}
</style>
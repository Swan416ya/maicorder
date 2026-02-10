<template>
  <BackGround>
    <!-- 1. 转场动画：加载 1s 后消失露出背景 -->
    <StarTransition v-if="appLoading" @finished="appLoading = false" />

    <div class="records-page-container" v-if="!appLoading">
      <!-- 2. 固定顶部区域 -->
      <div class="fixed-header">
        <div class="nav-header">
          <button class="back-btn" @click="router.push('/main')">← BACK</button>
          <h2 class="page-title">HISTORY / 履历</h2>
        </div>
        <div class="divider-thick"></div>
      </div>

      <!-- 3. 可滚动内容区域 -->
      <div class="scroll-container">
        <!-- 
          使用 CardList 组件替换原有的 v-for/v-if/v-else 
          items: 传入数据数组
          loading: 传入请求状态
        -->
        <CardList :items="checkIns" :loading="loading">
          <!-- 作用域插槽：item 即为每一个 checkIn 对象 -->
          <template #default="{ item: checkIn }">
            <PurpleCard 
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
                </div>
                <div class="total-cost">
                  Total: ¥{{ calculateTotal(checkIn) }}
                </div>
                <div class="comment-text">
                  Note: {{ checkIn.comment === '' ? '用户无评论' : checkIn.comment }}
                </div>
              </div>
            </PurpleCard>
          </template>

          <!-- 可选：自定义空状态内容 -->
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

// 引入组件
import BackGround from './BackGround.vue'
import StarTransition from './StarTransition.vue'
import CardList from './CardList.vue'
import PurpleCard from '@/components/PurpleCard.vue'

// 引入图片资源
import coinImg from '@/assets/coin.png'
import foodImg from '@/assets/food.png'
import drinkImg from '@/assets/drink.png'
import trafficImg from '@/assets/traffic.png'

const router = useRouter()
const checkIns = ref([])
const loading = ref(true)    // 数据加载状态
const appLoading = ref(true) // 开屏动画状态

const formatDate = (dateStr) => dateStr || ''
const calculateTotal = (c) => (c.coinCost || 0) + (c.foodCost || 0) + (c.waterCost || 0) + (c.transportCost || 0)

const viewCheckInDetail = (id) => router.push(`/checkin/detail/${id}`)

const fetchCheckIns = async () => {
  try {
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    const res = await axios.get(`/api/records/checkins/${userId}`)
    if (res.data.code === 200) {
      checkIns.value = res.data.data || []
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
.yuan-text {
  font-size: 25px;
}
/* 核心布局 */
.records-page-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  z-index: 1; /* 确保在背景之上 */
}

.fixed-header {
  flex-shrink: 0;
  padding: 20px 20px 0 20px;
  /* 如果希望头部透明露出背景波纹，可以删掉 background-color */
  /* background-color: #fff; */ 
}

.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

/* 隐藏原有的滚动条或保持美化 */
.scroll-container::-webkit-scrollbar { width: 4px; }
.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(103, 80, 164, 0.2);
  border-radius: 4px;
}

/* 样式保持一致 */
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

/* 卡片内部样式内容 */
.checkin-details { font-size: 14px; color: #49454f; }
.cost-row { display: flex; gap: 12px; margin-bottom: 4px; }
.cost-icon-small { width: 24px; height: 24px; object-fit: contain; }
.total-cost { font-weight: 800; color: #6750a4; margin-top: 8px;font-size: 20px; }
.comment-text { margin-top: 8px; font-style: italic; opacity: 0.8; }

.empty-box {
  border: 1px dashed #000;
  padding: 40px;
  text-align: center;
  font-weight: bold;
}
</style>
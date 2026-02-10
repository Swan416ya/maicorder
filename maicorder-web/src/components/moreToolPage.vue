<template>
  <div class="page-root">
    <!-- 1. 开屏转场 -->
    <StarTransition v-if="appLoading" @finished="appLoading = false" />

    <!-- 2. 全屏动态背景 (固定在底层) -->
    <BackGround />

    <!-- 3. UI 内容层 -->
    <div v-if="!appLoading" class="ui-layer">
      
      <!-- 滚动区域 -->
      <main class="main-scroll-area">
        
        <!-- 直接调用 CardList 组件 -->
        <CardList :items="checkIns" :loading="dataLoading">
          
          <!-- 定义每一项的渲染方式 -->
          <template #default="{ item }">
            <PurpleCard 
              variant="elevated" 
              :title="item.ToolName" 
              :subTitle="item.location"
              clickable
            >
              <div class="card-inner">
                <div class="stats-row">
                  <span>可用状态 {{ item.status }}</span>
                  <!-- <span>🍱 ¥{{ item.meal }}</span> -->
                </div>
                <div class="card-footer">
                  <span class="total">使用人数: {{ item.userCount }}</span>
                  <button class="detail-btn">DETAIL</button>
                </div>
              </div>
            </PurpleCard>
          </template>

          <!-- 自定义空状态插槽 -->
          <template #empty>
            <div class="empty-state">没有发现任何签到数据</div>
          </template>

        </CardList>

      </main>

      <PurpleFab 
        label="返回" 
        variant="surface"
        fixed
        :extended="isFabExtended"
        @click="backhome"
      >
        <!-- 自定义图标插槽 (可选，默认是加号) -->
        <template #icon>
           <img :src="logoutIcon" width="24" height="24" />
        </template>
      </PurpleFab>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import StarTransition from '@/components/StarTransition.vue';
import BackGround from '@/components/BackGround.vue';
import CardList from '@/components/CardList.vue';
import PurpleCard from '@/components/PurpleCard.vue';
import PurpleFab from '@/components/PurpleFab.vue';
import logoutIcon from '@/assets/logout.svg';
import router from '@/router/index.js';

const isFabExtended = ref(true);

const appLoading = ref(true);
const dataLoading = ref(true);
const checkIns = ref([]);


const backhome = () => {
    // alert("返回")
    router.push('/')
}


// 模拟获取数据
onMounted(() => {
  setTimeout(() => {
    checkIns.value = [
      { id: 1, ToolName: '舞萌b50', location: 'TODO简介', status: '不可用',userCount:0 },
      { id: 2, ToolName: '中二b45', location: 'TODO简介', status: '不可用',userCount:0 },
      { id: 3, ToolName: '歌曲查询', location: 'TODO简介', status: '不可用',userCount:0 },
    ];
    dataLoading.value = false;
  }, 2000);
});
</script>

<style scoped>
/* 根容器：禁止屏幕整体滚动 */
.page-root {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #F3EDF7;
}

/* UI层：置于背景之上 */
.ui-layer {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 头部样式 */
.custom-header {
  padding: 24px 20px 0;
  flex-shrink: 0;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.m3-back-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 8px 14px;
  font-weight: 800;
  font-size: 0.8rem;
  cursor: pointer;
}

.m3-title {
  font-size: 1.4rem;
  font-weight: 900;
  margin: 0;
}

.m3-divider {
  height: 4px;
  background: #000;
}

/* 核心滚动区域 */
.main-scroll-area {
  flex: 1;
  overflow-y: auto;
  padding: 10px 20px;
  /* 隐藏滚动条 (可选) */
  scrollbar-width: none; 
}
.main-scroll-area::-webkit-scrollbar { display: none; }

/* 卡片内部业务样式 */
.card-inner {
  margin-top: 10px;
}

.stats-row {
  display: flex;
  gap: 15px;
  font-weight: 500;
  color: #49454f;
}

.card-footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total {
  font-weight: bold;
  color: #6750a4;
}

.detail-btn {
  background: rgba(103, 80, 164, 0.1);
  border: none;
  color: #6750a4;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
}

.empty-state {
  text-align: center;
  padding: 40px;
  border: 2px dashed #ccc;
  border-radius: 20px;
  color: #888;
}
</style>
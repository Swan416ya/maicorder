<!-- App.vue -->
<template>
    <BackGround>
  <StarTransition/>
     <!-- 使用封装后的 CardList -->
    <CardList :items="checkIns" :loading="loading">
      
      <!-- 使用作用域插槽自定义每一行卡片的显示 -->
      <template #default="{ item }">
        <PurpleCard 
          variant="filled"
          :title="item.checkInTime"
          :subTitle="`Arcade ID: ${item.arcadeId}`"
          clickable
          @click="viewDetail(item.id)"
        >
          <div class="checkin-details">
            <div class="cost-row">
              <div class="cost-item">
                <img :src="coinImg" class="icon" /> <span>¥{{ item.coinCost }}</span>
              </div>
              <div class="cost-item">
                <img :src="foodImg" class="icon" /> <span>¥{{ item.foodCost }}</span>
              </div>
              <!-- ...其他费用项 -->
            </div>
            
            <div class="total-row">
              <span class="total-label">Total:</span>
              <span class="total-value">¥{{ calculateTotal(item) }}</span>
            </div>
          </div>
        </PurpleCard>
      </template>

      <!-- 自定义空状态（可选） -->
      <template #empty>
        <p>暂时没有任何工具哦 等待后续开发</p>
      </template>
    </CardList>
  </BackGround> 
</template>

<script setup>
import { ref } from 'vue';
import StarTransition from './StarTransition.vue';
import BackGround from './BackGround.vue'
import CardList from './CardList.vue'
</script>
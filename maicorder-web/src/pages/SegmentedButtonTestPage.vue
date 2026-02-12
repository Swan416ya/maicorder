<template>
  <div class="page-container">
    <h2>Material Segmented Buttons</h2>

    <!-- 示例 1: 仅图标 + 开启选中对勾 (isShowSelected = true) -->
    <div class="row">
      <p>1. 仅图标 (选中显示对勾 - 默认行为)</p>
      <SegmentedButton
        v-model="val1"
        :items="itemsIconOnly"
        :is-show-selected="true"
      />
    </div>

    <!-- 示例 2: 图标+文字 + 关闭选中对勾 (isShowSelected = false) -->
    <div class="row">
      <p>2. 图标+文字 (选中不显示对勾，仅变色)</p>
      <SegmentedButton
        v-model="val2"
        :items="itemsWithLabel"
        :is-show-selected="false"
      />
    </div>
    
    <!-- 示例 3: 完全复刻原图效果 (圆形内五角星) -->
    <div class="row">
      <p>3. 复刻原图 (Icon Only, 选中保持星星)</p>
       <SegmentedButton
        v-model="val3"
        :items="itemsIconOnly"
        :is-show-selected="false"
      />
    </div>

    <div class="logs">
      选中值: {{ val1 }} / {{ val2 }} / {{ val3 }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import SegmentedButton from '@/components/SegmentedButton.vue'; // 请根据实际路径修改

const val1 = ref(2);
const val2 = ref('A');
const val3 = ref(2);

const itemsIconOnly = [
  { value: 1 },
  { value: 2 },
  { value: 3 },
  { value: 4 },
];

const itemsWithLabel = [
  { value: 'A', label: 'Label' },
  { value: 'B', label: 'Label' },
  { value: 'C', label: 'Label' },
];
</script>

<style>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding: 40px;
  background-color: #FDF7FF; /* 浅粉背景 */
  font-family: sans-serif;
  align-items: center;
}
.row {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
p {
  margin: 0;
  font-size: 14px;
  color: #666;
}
</style>
<template>
  <div class="segmented-group" role="group">
    <button
      v-for="(item, index) in items"
      :key="item.value"
      type="button"
      class="segment-btn"
      :class="{
        'is-selected': modelValue === item.value,
        'has-label': !!item.label,
        'icon-only': !item.label
      }"
      @click="handleSelect(item.value)"
    >
      <!-- 涟漪层 -->
      <div class="state-layer"></div>

      <!-- 图标容器 -->
      <span class="icon-container">
        <!-- 
           逻辑优先级：
           1. 如果被选中 且 开启了 isShowSelected -> 显示对勾图标
           2. 否则 -> 显示 item.icon 或 默认插槽 或 默认星星图标
        -->
        
        <!-- 情况A: 选中状态的对勾 (Material Design 规范) -->
        <template v-if="modelValue === item.value && isShowSelected">
          <svg class="md-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
        </template>

        <!-- 情况B: 正常图标 / 未选中时的图标 -->
        <template v-else>
          <!-- 具名插槽，允许外部自定义图标，回传 isSelected 状态 -->
          <slot name="icon" :item="item" :selected="modelValue === item.value">
            <!-- 默认图标：星星 (复刻您的图片) -->
            <svg class="md-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 20C7.59 20 4 16.41 4 12C4 7.59 7.59 4 12 4C16.41 4 20 7.59 20 12C20 16.41 16.41 20 12 20ZM12.92 6.38L14.36 9.74L18 10.06L15.24 12.46L16.09 16L12.92 14.11L9.75 16L10.6 12.46L7.84 10.06L11.48 9.74L12.92 6.38Z"/>
            </svg>
          </slot>
        </template>
      </span>

      <!-- 文本部分 -->
      <span class="label" v-if="item.label">{{ item.label }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
interface Item {
  value: string | number;
  label?: string;
  icon?: string;
}

const props = defineProps<{
  items: Item[];
  modelValue: string | number;
  // 新增：控制选中时是否显示“对勾”图标，默认为 true (MD3标准)
  isShowSelected?: boolean; 
}>();

// 设置默认值
const isShowSelected = props.isShowSelected ?? true; 

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void;
}>();

const handleSelect = (value: string | number) => {
  emit('update:modelValue', value);
};
</script>

<style scoped>
.segmented-group {
  display: inline-flex;
  flex-direction: row;
  border-radius: 20px;
  background-color: transparent; /* 不需要背景色，按钮自带 */
  border: 1px solid #79747E; /* 增加外边框以更像 MD3 */
  overflow: hidden; /* 裁剪圆角 */
}

.segment-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  background-color: transparent; /* 默认透明，显示父容器背景或白色 */
  color: #49454F;
  font-family: sans-serif;
  font-size: 14px;
  font-weight: 500;
  padding: 0 12px;
  height: 40px;
  min-width: 48px;
  cursor: pointer;
  transition: all 0.2s ease;
  /* 按钮之间添加分隔线 */
  border-right: 1px solid #79747E; 
}

/* 最后一个按钮去掉右边框 */
.segment-btn:last-child {
  border-right: none;
}

.segment-btn:hover {
  background-color: rgba(232, 222, 248, 0.5); /* 浅紫 Hover */
}

/* 选中状态 */
.segment-btn.is-selected {
  background-color: #E8DEF8; /* 浅紫色背景 */
  color: #1D192B; /* 深色文字 */
}

/* 如果需要在选中时变成深色背景（如原图），可以使用下面的样式覆盖上面的 */
/* 根据原图风格修改选中态：深灰背景，白色文字 */
.segment-btn.is-selected {
  background-color: #555060; 
  color: #FFFFFF;
}

/* 图标容器及样式 */
.icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.md-icon {
  width: 18px;
  height: 18px;
  fill: currentColor; /* 关键：跟随文字颜色 */
}

/* 间距控制 */
.icon-container + .label {
  margin-left: 8px;
}

/* 仅图标模式调整 */
.segment-btn.icon-only {
  padding: 0;
  width: 48px;
}

/* 涟漪效果层 (可选) */
.state-layer {
  position: absolute;
  inset: 0;
  background-color: currentColor;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s;
}
.segment-btn:active .state-layer {
  opacity: 0.12;
}
</style>
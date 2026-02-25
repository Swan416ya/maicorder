<template>
  <div class="md3-info-bar" :class="{ 'is-editing': edit }">
    <!-- 标签区域 -->
    <div class="info-label">{{ label }}</div>

    <!-- 内容区域 -->
    <div class="info-content">
      <input
        v-if="edit"
        class="info-input"
        type="text"
        :value="modelValue"
        @input="handleInput"
        :placeholder="`请输入${label}`"
      />
      <span v-else class="info-text">{{ modelValue || '未填写' }}</span>
    </div>

    <!-- 验证动画区域 -->
    <div class="info-action">
      <transition name="fade">
        <div v-if="verify && isValid" class="verify-badge">
          <svg class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 6L9 17l-5-5" />
          </svg>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: { type: String, required: true },
  modelValue: { type: [String, Number], default: '' },
  edit: { type: Boolean, default: false },
  verify: { type: Boolean, default: false },
  // 验证规则函数，接收当前值，返回布尔值
  rule: { type: Function, default: () => true } 
})

const emit = defineEmits(['update:modelValue'])

const handleInput = (e) => {
  emit('update:modelValue', e.target.value)
}

// 计算属性：是否验证通过
const isValid = computed(() => {
  if (!props.modelValue) return false
  return props.rule(props.modelValue)
})
</script>

<style scoped>
/* MD3 颜色变量 */
.md3-info-bar {
  --md-sys-color-surface: #FDFDF5;
  --md-sys-color-surface-variant: #E7E0EC;
  --md-sys-color-on-surface: #1C1B1F;
  --md-sys-color-on-surface-variant: #49454F;
  --md-sys-color-primary: #6750A4;
  --md-sys-color-success: #386A20;

  display: flex;
  align-items: center;
  min-height: 56px;
  padding: 8px 16px;
  margin-bottom: 12px;
  border-radius: 16px;
  background-color: transparent;
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
}

/* 编辑状态下的背景色 */
.md3-info-bar.is-editing {
  background-color: var(--md-sys-color-surface-variant);
}

.info-label {
  width: 90px;
  font-size: 14px;
  font-weight: 500;
  color: var(--md-sys-color-on-surface-variant);
  flex-shrink: 0;
}

.info-content {
  flex: 1;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.info-text {
  font-size: 16px;
  color: var(--md-sys-color-on-surface);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* MD3 风格的极简输入框 */
.info-input {
  width: 100%;
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  padding: 4px 0;
  font-size: 16px;
  color: var(--md-sys-color-on-surface);
  outline: none;
  transition: border-color 0.3s;
}

.info-input:focus {
  border-bottom-color: var(--md-sys-color-primary);
}

.info-action {
  width: 32px;
  display: flex;
  justify-content: flex-end;
}

/* 验证通过的徽标 */
.verify-badge {
  color: var(--md-sys-color-success);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* SVG 绘制动画 */
.check-icon {
  width: 24px;
  height: 24px;
}

.check-icon path {
  stroke-dasharray: 50;
  stroke-dashoffset: 50;
  animation: draw-check 0.4s cubic-bezier(0.65, 0, 0.45, 1) forwards;
}

@keyframes draw-check {
  to {
    stroke-dashoffset: 0;
  }
}

/* 淡入淡出过渡 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scale(0.5);
}
</style>
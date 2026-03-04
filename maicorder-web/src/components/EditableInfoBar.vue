<template>
  <div class="info-bar" :class="{ 'is-editing': edit }">
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

    <!-- 验证区域 -->
    <div class="info-action">
      <div v-if="verify && isValid" class="verify-badge">✓</div>
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
.info-bar {
  display: flex;
  align-items: center;
  min-height: 48px;
  padding: 8px 0;
  border-bottom: 1px solid #e0e0e0;
  transition: all 0.2s ease;
}

/* 编辑状态下的样式 */
.info-bar.is-editing {
  background-color: #f9f9f9;
  padding: 8px 12px;
  border-radius: 6px;
  border-bottom: none;
}

.info-label {
  width: 80px;
  font-size: 14px;
  font-weight: 500;
  color: #666666;
  flex-shrink: 0;
  margin-right: 16px;
}

.info-content {
  flex: 1;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.info-text {
  font-size: 15px;
  color: #333333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 简约风格输入框 */
.info-input {
  width: 100%;
  background: transparent;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  padding: 6px 10px;
  font-size: 15px;
  color: #333333;
  outline: none;
  transition: all 0.2s ease;
}

.info-input:focus {
  border-color: #666666;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
}

.info-action {
  width: 32px;
  display: flex;
  justify-content: flex-end;
  margin-left: 12px;
}

/* 验证通过的徽标 */
.verify-badge {
  color: #4CAF50;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
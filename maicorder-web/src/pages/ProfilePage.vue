<template>
  <div class="profile-page">
    <div class="profile-card">
      <!-- 头部区域 -->
      <div class="profile-header">
        <div class="avatar-container">
          <!-- 默认占位头像 -->
          <div class="avatar">
            {{ userInfo.username ? userInfo.username.charAt(0).toUpperCase() : 'U' }}
          </div>
        </div>
        
        <div class="header-actions">
          <label class="md3-switch-label">
            <span class="status-text">{{ isEditing ? '保存' : '编辑' }}</span>
            <input type="checkbox" v-model="isEditing" class="md3-switch" />
          </label>
        </div>
      </div>

      <div class="profile-title">
        <h2>个人资料</h2>
        <p>管理您的个人信息</p>
      </div>

      <!-- 信息表单区域 -->
      <div class="profile-body">
        <EditableInfoBar
          label="用户名"
          v-model="userInfo.username"
          :edit="isEditing"
          :verify="true"
          :rule="rules.username"
        />

        <EditableInfoBar
          label="邮箱"
          v-model="userInfo.email"
          :edit="isEditing"
          :verify="true"
          :rule="rules.email"
        />

        <EditableInfoBar
          label="api key"
          v-model="userInfo.apikey"
          :edit="isEditing"
          :verify="false"
        />

        <EditableInfoBar
          label="UID"
          v-model="userInfo.uid"
        />

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import EditableInfoBar from '@/components/EditableInfoBar.vue'

// 全局编辑状态
const isEditing = ref(false)

// 用户信息状态
const userInfo = reactive({
  username: '-',
  email: '-',
  phone: '-',
  password: '-',
  uid: '-',
})

// 初始化用户信息
const initUserInfo = () => {
  try {
    const currentUserStr = localStorage.getItem('currentUser')
    if (currentUserStr) {
      const currentUser = JSON.parse(currentUserStr)
      userInfo.username = currentUser.username || '-'
      userInfo.email = currentUser.email || '-'
      userInfo.uid = currentUser.id || '-'
      // userInfo.createTime = currentUser.createTime || '-'
    }
    
    const userId = localStorage.getItem('userId')
    if (userId && userInfo.uid === '-') {
      userInfo.uid = userId
    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
  }
}

onMounted(() => {
  initUserInfo()
})

// 验证规则
const rules = {
  // 用户名至少3个字符
  username: (val) => val.trim().length >= 3,
  // 简单邮箱验证正则
  email: (val) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val),
  // 签名不为空
  bio: (val) => val.trim().length > 0
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #F4F3F7; /* MD3 后台灰 */
  font-family: 'Roboto', system-ui, sans-serif;
}

.profile-card {
  width: 100%;
  max-width: 480px;
  background-color: #FEF7FF;
  border-radius: 28px; /* MD3 大卡片圆角 */
  padding: 32px;
  box-shadow: 0px 4px 16px rgba(0, 0, 0, 0.05);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

/* 头像样式 */
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 40px;
  background-color: #EADDFF;
  color: #21005D;
  font-size: 32px;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
}

.profile-title h2 {
  margin: 0;
  font-size: 24px;
  color: #1C1B1F;
  font-weight: 600;
}

.profile-title p {
  margin: 4px 0 24px 0;
  font-size: 14px;
  color: #49454F;
}

/* MD3 风格的原生 Checkbox 模拟 Switch */
.md3-switch-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.status-text {
  font-size: 14px;
  color: #6750A4;
  margin-right: 8px;
  font-weight: 500;
}

.md3-switch {
  appearance: none;
  width: 48px;
  height: 24px;
  background-color: #E7E0EC;
  border-radius: 12px;
  position: relative;
  outline: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.md3-switch::after {
  content: '';
  position: absolute;
  top: 4px;
  left: 4px;
  width: 16px;
  height: 16px;
  background-color: #79747E;
  border-radius: 50%;
  transition: transform 0.3s cubic-bezier(0.2, 0, 0, 1), background-color 0.3s;
}

.md3-switch:checked {
  background-color: #6750A4;
}

.md3-switch:checked::after {
  transform: translateX(24px) scale(1.2);
  background-color: #FFFFFF;
}

.profile-body {
  display: flex;
  flex-direction: column;
}
</style>
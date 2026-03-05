<template>
  <div class="profile-page">
    <div class="profile-card">
      <!-- 头部区域 -->
      <div class="profile-header">
        <div class="avatar">
          {{ userInfo.username ? userInfo.username.charAt(0).toUpperCase() : 'U' }}
        </div>
        
        <button @click="isEditing = !isEditing" class="edit-button">
          {{ isEditing ? '保存' : '编辑' }}
        </button>
      </div>

      <h1 class="profile-title">个人资料</h1>

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
          label="API Key"
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
  apikey: '-',
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
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.profile-card {
  width: 100%;
  max-width: 400px;
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

/* 头像样式 */
.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: #f0f0f0;
  color: #333333;
  font-size: 24px;
  font-weight: 600;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 2px solid #e0e0e0;
}

.profile-title {
  margin: 0 0 24px 0;
  font-size: 20px;
  color: #333333;
  font-weight: 600;
  text-align: center;
}

/* 简约风格按钮 */
.edit-button {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  background-color: #ffffff;
  color: #333333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-button:hover {
  background-color: #f5f5f5;
  border-color: #d0d0d0;
}

.edit-button:active {
  background-color: #e0e0e0;
}

.profile-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
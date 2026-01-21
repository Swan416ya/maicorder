<script setup>
import {ref, onMounted} from 'vue'
import axios from 'axios'

// --- 基础配置 ---
// 后端地址
const API_BASE = 'http://localhost:8080/api'

// --- 1. 机厅相关逻辑 ---
const arcades = ref([]) // 存储从后端拿到的机厅列表
const newArcadeName = ref('') // 输入框里的新机厅名字

// 获取所有机厅
const fetchArcades = async () => {
  try {
    const res = await axios.get(`${API_BASE}/arcades`)
    arcades.value = res.data
  } catch (e) {
    console.error("获取机厅失败", e)
  }
}

// 创建新机厅
const createArcade = async () => {
  if (!newArcadeName.value) return alert("请输入机厅名字")
  try {
    // 发送 POST 请求给后端
    await axios.post(`${API_BASE}/arcades`, {name: newArcadeName.value})
    alert("机厅创建成功！")
    newArcadeName.value = '' // 清空输入框
    fetchArcades() // 刷新下拉列表
  } catch (e) {
    alert("创建失败")
  }
}

// --- 2. 出勤记录相关逻辑 ---
// 游戏类型枚举 (必须和后端 Java 的 Enum 拼写完全一致)
const gameOptions = [
  {label: '舞萌 DX', value: 'MAIMAI_DX'},
  {label: '中二节奏', value: 'CHUNITHM'},
  {label: '音击', value: 'ONGREKI'}, // 假设你有
  {label: 'IIDX', value: 'BEATMANIA_IIDX'}
]

// 表单数据对象
const form = ref({
  arcade: null,          // 选中的机厅对象 (Spring 需要知道 {id: 1})
  totalMoneySpent: 0,    // 花费
  comment: '',           // 备注
  playRecords: []        // 战绩列表 (初始为空)
})

// 增加一行战绩
const addPlayRecord = () => {
  form.value.playRecords.push({
    game: 'MAIMAI_DX', // 默认选舞萌
    ratingChange: 0,   // Rating变化
    score: '',         // 分数/达成率
    comment: ''        // 单局备注
  })
}

// 删除一行战绩
const removePlayRecord = (index) => {
  form.value.playRecords.splice(index, 1)
}

// 提交出勤记录
const submitCheckIn = async () => {
  if (!form.value.arcade) return alert("请先选择机厅")

  try {
    // 构造发送给后端的数据
    // 注意：form.value.arcade 已经是下拉框绑定的对象 {id:1, name:...} 了
    // Spring Boot 收到这个对象会自动识别 ID 进行关联
    const payload = {
      arcade: {id: form.value.arcade}, // 只要传 ID 告诉后端是哪个机厅
      totalMoneySpent: form.value.totalMoneySpent,
      comment: form.value.comment,
      playRecords: form.value.playRecords
    }

    console.log("准备发送的数据:", payload)

    const res = await axios.post(`${API_BASE}/checkins`, payload)
    console.log("服务器返回:", res.data)
    alert("勤了吗？勤了！记录保存成功。")

    // 清空表单，准备记下一次
    form.value.totalMoneySpent = 0
    form.value.comment = ''
    form.value.playRecords = []
  } catch (e) {
    console.error(e)
    alert("保存失败，请看控制台报错")
  }
}

// 页面加载时自动运行
onMounted(() => {
  fetchArcades()
})
</script>

<template>
  <div class="container">
    <h1>🎮 勤了吗 - Maimai出勤助手</h1>

    <!-- 模块1：创建机厅 (因为数据库是空的，得先建一个) -->
    <div class="card">
      <h3>📍 找不到机厅？新建一个</h3>
      <div class="row">
        <input v-model="newArcadeName" placeholder="例如：广州天河机皇"/>
        <button @click="createArcade">添加机厅</button>
      </div>
    </div>

    <!-- 模块2：记录出勤 -->
    <div class="card">
      <h3>📝 记一笔出勤</h3>

      <!-- 选择机厅 -->
      <div class="form-group">
        <label>选择机厅：</label>
        <select v-model="form.arcade">
          <option :value="null" disabled>请选择...</option>
          <!-- 循环渲染机厅列表 -->
          <option v-for="item in arcades" :key="item.id" :value="item.id">
            {{ item.name }}
          </option>
        </select>
      </div>

      <!-- 基础信息 -->
      <div class="form-group">
        <label>今日花费：</label>
        <input type="number" v-model="form.totalMoneySpent"/>
      </div>

      <div class="form-group">
        <label>总备注：</label>
        <input v-model="form.comment" placeholder="今天排队人多吗？"/>
      </div>

      <!-- 动态战绩列表 -->
      <div class="records-section">
        <h4>
          战绩详情
          <button @click="addPlayRecord" class="small-btn">+ 加一局</button>
        </h4>

        <div v-if="form.playRecords.length === 0" class="empty-tip">
          还没有添加战绩，点击上面按钮添加 👇
        </div>

        <!-- 循环渲染每一行战绩 -->
        <div v-for="(record, index) in form.playRecords" :key="index" class="record-row">
          <span class="row-num">#{{ index + 1 }}</span>

          <!-- 游戏选择 -->
          <select v-model="record.game">
            <option v-for="opt in gameOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>

          <!-- 分数 -->
          <input v-model="record.score" placeholder="达成率/分数 (SSS+)" class="short-input"/>

          <!-- Rating变化 -->
          <input type="number" v-model="record.ratingChange" placeholder="Rating变化" class="short-input"/>

          <button @click="removePlayRecord(index)" class="del-btn">删除</button>
        </div>
      </div>

      <button @click="submitCheckIn" class="submit-btn">✅ 确认勤了</button>
    </div>
  </div>
</template>

<style scoped>
/* 简单写点样式，让界面不那么丑 */
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  font-family: sans-serif;
}

.card {
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  background: #f9f9f9;
}

.row {
  display: flex;
  gap: 10px;
}

.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: bold;
  margin-bottom: 5px;
}

input, select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  cursor: pointer;
  padding: 8px 15px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
}

button:hover {
  background: #3aa876;
}

.records-section {
  border-top: 1px solid #eee;
  margin-top: 20px;
  padding-top: 10px;
}

.record-row {
  display: flex;
  gap: 5px;
  margin-bottom: 10px;
  align-items: center;
}

.short-input {
  width: 80px;
}

.small-btn {
  font-size: 12px;
  padding: 4px 8px;
}

.del-btn {
  background: #ff4d4f;
}

.del-btn:hover {
  background: #d9363e;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
  font-size: 16px;
  padding: 12px;
}

.empty-tip {
  color: #888;
  font-size: 14px;
  margin-bottom: 10px;
}
</style>
<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

// 自动判断环境
const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'

// ==========================================
// 🔐 用户系统逻辑
// ==========================================
const currentUser = ref(null)
const isRegisterMode = ref(false)
const authForm = ref({ username: '', password: '', email: '' })

onMounted(() => {
  const savedUser = localStorage.getItem('qinlema_user')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
    fetchArcades() // 登录状态下才加载机厅
  }
})

const handleAuth = async () => {
  if (!authForm.value.username || !authForm.value.password) return alert("账号密码不能为空")

  try {
    if (isRegisterMode.value) {
      const res = await axios.post(`${API_BASE}/users`, authForm.value)
      if (res.data.success) {
        alert("注册成功！请登录")
        isRegisterMode.value = false
      } else {
        alert("注册失败：" + res.data.message)
      }
    } else {
      const res = await axios.post(`${API_BASE}/login`, {
        username: authForm.value.username,
        password: authForm.value.password
      })
      if (res.data.success) {
        currentUser.value = res.data.user
        localStorage.setItem('qinlema_user', JSON.stringify(res.data.user))
        fetchArcades()
      } else {
        alert("登录失败：" + res.data.message)
      }
    }
  } catch (e) {
    console.error(e)
    alert("请求出错，后端挂了？")
  }
}

const logout = () => {
  currentUser.value = null
  localStorage.removeItem('qinlema_user')
  authForm.value = { username: '', password: '', email: '' }
}

// ==========================================
// 🎮 出勤记录逻辑
// ==========================================
const GAME_CONFIG = {
  'MAIMAI_DX': { label: '舞萌 DX', lamps: ['鸟', '鸟+', 'FC', 'AP', 'FDX'] },
  'CHUNITHM': { label: '中二节奏', lamps: ['AJ', 'AJC', 'FC'] },
  'ONGREKI': { label: '音击', lamps: ['AB', 'FB'] },
  'IIDX': { label: 'IIDX', lamps: ['Hard', 'ExHard'] }
}

const getTodayString = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const arcades = ref([])
const newArcadeName = ref('')
const form = ref({
  arcadeId: null,
  checkInDate: getTodayString(),
  coinCost: '', foodCost: '', waterCost: '', transportCost: '',
  comment: '',
  gameSessions: [] 
})

const fetchArcades = async () => {
  try {
    const res = await axios.get(`${API_BASE}/arcades`)
    arcades.value = res.data.filter(a => a.name)
  } catch (e) {}
}

const createArcade = async () => {
  if(!newArcadeName.value) return
  await axios.post(`${API_BASE}/arcades`, {name: newArcadeName.value})
  newArcadeName.value = ''; fetchArcades()
}

const addSession = (gameKey) => form.value.gameSessions.push({ gameName: gameKey, pcCount: '', currentRating: '', records: [] })
const addRecordToSession = (idx) => form.value.gameSessions[idx].records.push({ songName: '', score: '', clearStatus: '' })
const removeSession = (idx) => form.value.gameSessions.splice(idx, 1)
const removeRecord = (sIdx, rIdx) => form.value.gameSessions[sIdx].records.splice(rIdx, 1)

const submitCheckIn = async () => {
  if (!form.value.arcadeId) return alert("请选择机厅！")
  if (!currentUser.value) return alert("请先登录！") // 安全校验

  try {
    const payload = {
      // --- 关键：带上用户ID ---
      userId: currentUser.value.id,
      
      arcade: { id: form.value.arcadeId },
      checkInDate: form.value.checkInDate,
      coinCost: form.value.coinCost, 
      foodCost: form.value.foodCost,
      waterCost: form.value.waterCost, 
      transportCost: form.value.transportCost,
      comment: form.value.comment,
      gameSessions: form.value.gameSessions
    }
    await axios.post(`${API_BASE}/checkins`, payload)
    alert("出勤记录保存成功！")
    
    // 重置表单(保留机厅和日期)
    form.value.gameSessions = []
    form.value.comment = ''
    form.value.coinCost = ''; form.value.foodCost = ''
    form.value.waterCost = ''; form.value.transportCost = ''
  } catch (e) {
    console.error(e)
    alert("保存失败：" + e.message)
  }
}
</script>

<template>
  <div class="container">
    
    <!-- 登录界面 -->
    <div v-if="!currentUser" class="login-container">
      <div class="card login-card">
        <h1>👋 欢迎来到 勤了吗</h1>
        <p class="subtitle">{{ isRegisterMode ? '注册新账号' : '登录你的账号' }}</p>

        <div class="form-group">
          <label>用户名</label>
          <input v-model="authForm.username" placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="authForm.password" placeholder="请输入密码" />
        </div>
        <div class="form-group" v-if="isRegisterMode">
          <label>邮箱 (可选)</label>
          <input v-model="authForm.email" placeholder="用于找回密码" />
        </div>

        <button @click="handleAuth" class="submit-btn main-action">
          {{ isRegisterMode ? '立即注册' : '登 录' }}
        </button>

        <div class="switch-mode">
          <span v-if="!isRegisterMode">还没账号？ <a @click="isRegisterMode = true">去注册</a></span>
          <span v-else>已有账号？ <a @click="isRegisterMode = false">去登录</a></span>
        </div>
      </div>
    </div>

    <!-- 主界面 -->
    <div v-else class="main-app">
      <div class="top-bar">
        <h2>👤 你好, {{ currentUser.username }}</h2>
        <button @click="logout" class="logout-btn">退出</button>
      </div>

      <h1>🎮 记一笔出勤</h1>

      <!-- 1. 基础信息 -->
      <div class="card">
        <h3>📍 基础信息</h3>
        <div class="row">
          <div class="col">
            <label>机厅</label>
            <div class="row" style="margin-bottom:0">
              <select v-model="form.arcadeId">
                <option :value="null">请选择机厅...</option>
                <option v-for="a in arcades" :value="a.id" :key="a.id">{{ a.name }}</option>
              </select>
            </div>
            <div class="row" style="margin-top:5px">
              <input v-model="newArcadeName" placeholder="新建机厅..." style="font-size:0.8rem; padding:4px"/>
              <button @click="createArcade" style="font-size:0.8rem; padding:4px 8px">加</button>
            </div>
          </div>
          <div class="col">
            <label>日期</label>
            <input type="date" v-model="form.checkInDate" />
          </div>
        </div>
      </div>

      <!-- 2. 费用 -->
      <div class="card">
        <h3>💰 今日开销</h3>
        <div class="cost-grid">
          <div class="cost-item"><label>🪙 投币</label><input type="number" v-model="form.coinCost" placeholder="0" /></div>
          <div class="cost-item"><label>🍜 吃饭</label><input type="number" v-model="form.foodCost" placeholder="0" /></div>
          <div class="cost-item"><label>🥤 喝水</label><input type="number" v-model="form.waterCost" placeholder="0" /></div>
          <div class="cost-item"><label>🚕 路费</label><input type="number" v-model="form.transportCost" placeholder="0" /></div>
        </div>
        <div class="row" style="margin-top: 10px;">
          <input v-model="form.comment" placeholder="今日总备注..." style="width: 100%"/>
        </div>
      </div>

      <!-- 3. 游戏场次 -->
      <div class="card">
        <h3>🎮 玩了啥？</h3>
        <div class="game-buttons">
          <button v-for="(conf, key) in GAME_CONFIG" :key="key" @click="addSession(key)" class="game-btn">+ {{ conf.label }}</button>
        </div>

        <div v-for="(sess, sIndex) in form.gameSessions" :key="sIndex" class="session-box">
          <div class="session-header">
            <h4>{{ GAME_CONFIG[sess.gameName].label }}</h4>
            <button @click="removeSession(sIndex)" class="del-btn">删除</button>
          </div>
          <div class="row">
            <input type="number" v-model="sess.pcCount" placeholder="PC数" />
            <input v-model="sess.currentRating" placeholder="今日Rating/段位" />
          </div>
          <div class="records-area">
            <div v-for="(rec, rIndex) in sess.records" :key="rIndex" class="record-row">
              <input v-model="rec.songName" placeholder="曲名" style="flex:2"/>
              <input v-model="rec.score" placeholder="分数/达成率" />
              <select v-model="rec.clearStatus">
                <option value="">牌子</option>
                <option v-for="l in GAME_CONFIG[sess.gameName].lamps" :key="l" :value="l">{{l}}</option>
              </select>
              <button @click="removeRecord(sIndex, rIndex)" class="del-btn small">×</button>
            </div>
            <button @click="addRecordToSession(sIndex)" class="add-sub-btn">+ 添加战绩</button>
          </div>
        </div>
      </div>

      <button @click="submitCheckIn" class="submit-btn">✅ 全部提交</button>
    </div>

  </div>
</template>

<style scoped>
/* 保持你之前的样式，这里简化列出核心样式 */
.container { max-width: 600px; margin: 0 auto; padding: 10px; font-family: sans-serif; }
.card { background: #fff; padding: 15px; border-radius: 8px; border: 1px solid #eee; margin-bottom: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.row { display: flex; gap: 10px; margin-bottom: 10px; }
.col { display: flex; flex-direction: column; flex: 1; }
input, select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; flex: 1; outline: none; }
button { cursor: pointer; border: none; border-radius: 4px; padding: 8px 12px; }

/* 登录样式 */
.login-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; }
.login-card { width: 100%; max-width: 400px; padding: 30px; }
.login-card h1 { text-align: center; color: #42b983; }
.subtitle { text-align: center; color: #666; margin-bottom: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.form-group input { width: 100%; box-sizing: border-box; }
.main-action { width: 100%; background: #42b983; color: white; padding: 12px; margin-top: 10px; font-size: 1.1rem; }
.switch-mode { text-align: center; margin-top: 15px; font-size: 0.9rem; }
.switch-mode a { color: #42b983; cursor: pointer; text-decoration: underline; }

/* 业务样式 */
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.logout-btn { background: #eee; color: #333; }
.cost-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.cost-item { display: flex; flex-direction: column; align-items: center; }
.cost-item input { text-align: center; }
.game-buttons { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 15px; }
.game-btn { background: #e3f2fd; color: #1565c0; font-weight: bold; }
.session-box { border: 2px dashed #ddd; padding: 10px; margin-bottom: 15px; background: #fafafa; border-radius: 8px; }
.session-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.records-area { border-top: 1px solid #eee; margin-top: 10px; padding-top: 10px; }
.record-row { display: flex; gap: 5px; margin-bottom: 5px; }
.del-btn { background: #ffebee; color: #c62828; }
.del-btn.small { padding: 0 10px; }
.add-sub-btn { background: #f1f8e9; color: #33691e; width: 100%; }
.submit-btn { width: 100%; background: #42b983; color: white; padding: 15px; font-size: 1.2rem; }
</style>
<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const API_BASE = 'http://localhost:8080/api'

// 游戏配置
const GAME_CONFIG = {
  'MAIMAI_DX': { label: '舞萌 DX', lamps: ['鸟', '鸟+', 'FC', 'AP', 'FDX'] },
  'CHUNITHM': { label: '中二节奏', lamps: ['AJ', 'AJC', 'FC'] },
  'ONGREKI': { label: '音击', lamps: ['AB', 'FB'] },
  'IIDX': { label: 'IIDX', lamps: ['Hard', 'ExHard'] }
}

// 获取今日日期字符串 YYYY-MM-DD
const getTodayString = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 数据状态
const arcades = ref([])
const newArcadeName = ref('')

const form = ref({
  arcadeId: null,
  
  // 新增：日期字段，默认为今天
  checkInDate: getTodayString(),

  coinCost: '',
  foodCost: '',
  waterCost: '',
  transportCost: '',
  
  comment: '',
  gameSessions: [] 
})

// 添加一个游戏场次
const addSession = (gameKey) => {
  form.value.gameSessions.push({
    gameName: gameKey,
    pcCount: '',
    currentRating: '',
    records: [] 
  })
}

const addRecordToSession = (sessionIndex) => {
  form.value.gameSessions[sessionIndex].records.push({
    songName: '', score: '', clearStatus: ''
  })
}

const removeSession = (index) => form.value.gameSessions.splice(index, 1)
const removeRecord = (sIndex, rIndex) => form.value.gameSessions[sIndex].records.splice(rIndex, 1)

// 提交
const submitCheckIn = async () => {
  if (!form.value.arcadeId) return alert("选机厅！")
  // 允许 session 为空，可能只是去吃饭了？如果强制要打游戏可以加上校验
  // if (form.value.gameSessions.length === 0) return alert("还没添加任何游戏！")

  try {
    const payload = {
      arcade: { id: form.value.arcadeId },
      // 传日期
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
    
    // 重置表单，但保留机厅和日期（用户可能连续记）
    form.value.gameSessions = [] 
    form.value.coinCost = ''
    form.value.foodCost = ''
    form.value.waterCost = ''
    form.value.transportCost = ''
    form.value.comment = ''
  } catch (e) {
    console.error(e)
    alert("失败：" + e.message)
  }
}

// 初始化
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
onMounted(fetchArcades)
</script>

<template>
  <div class="container">
    <h1>🎮 勤了吗 - 全能出勤记录</h1>

    <!-- 基础信息 (地点 + 时间) -->
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
          <!-- 新建机厅小行 -->
          <div class="row" style="margin-top:5px">
            <input v-model="newArcadeName" placeholder="新建机厅..." style="font-size:0.8rem; padding:4px"/>
            <button @click="createArcade" style="font-size:0.8rem; padding:4px 8px">加</button>
          </div>
        </div>

        <div class="col">
          <label>出勤日期</label>
          <!-- 日期选择器 -->
          <input type="date" v-model="form.checkInDate" />
        </div>
      </div>
    </div>

    <!-- 费用 -->
    <div class="card">
      <h3>💰 今日开销</h3>
      <div class="cost-grid">
        <div class="cost-item">
          <label>🪙 投币</label>
          <input type="number" v-model="form.coinCost" placeholder="0" />
        </div>
        <div class="cost-item">
          <label>🍜 吃饭</label>
          <input type="number" v-model="form.foodCost" placeholder="0" />
        </div>
        <div class="cost-item">
          <label>🥤 喝水</label>
          <input type="number" v-model="form.waterCost" placeholder="0" />
        </div>
        <div class="cost-item">
          <label>🚕 路费</label>
          <input type="number" v-model="form.transportCost" placeholder="0" />
        </div>
      </div>
      
      <div class="row" style="margin-top: 10px;">
        <input v-model="form.comment" placeholder="今日总备注..." style="width: 100%"/>
      </div>
    </div>

    <!-- 游戏场次区域 -->
    <div class="card">
      <h3>🎮 玩了啥？</h3>
      <div class="game-buttons">
        <button v-for="(conf, key) in GAME_CONFIG" :key="key" @click="addSession(key)" class="game-btn">
          + {{ conf.label }}
        </button>
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
</template>

<style scoped>
.container { max-width: 600px; margin: 0 auto; padding: 10px; font-family: sans-serif; }
.card { background: #fff; padding: 15px; border-radius: 8px; border: 1px solid #eee; margin-bottom: 15px; }
.row { display: flex; gap: 10px; margin-bottom: 10px; }
.col { display: flex; flex-direction: column; flex: 1; }
input, select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; flex: 1; }
button { cursor: pointer; border: none; border-radius: 4px; padding: 8px 12px; }

label { font-size: 0.85rem; color: #666; margin-bottom: 4px; }

/* 费用网格 */
.cost-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.cost-item { display: flex; flex-direction: column; align-items: center; }
.cost-item input { text-align: center; width: 100%; box-sizing: border-box; }

.game-buttons { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 15px; }
.game-btn { background: #e3f2fd; color: #1565c0; font-weight: bold; }

.session-box { border: 2px dashed #ddd; padding: 10px; margin-bottom: 15px; border-radius: 8px; background: #fafafa; }
.session-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.session-header h4 { margin: 0; color: #333; }

.records-area { margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee; }
.record-row { display: flex; gap: 5px; margin-bottom: 5px; }

.del-btn { background: #ffebee; color: #c62828; }
.del-btn.small { padding: 0 10px; }
.add-sub-btn { background: #f1f8e9; color: #33691e; width: 100%; font-size: 0.9rem; }
.submit-btn { width: 100%; background: #42b983; color: white; font-size: 1.2rem; padding: 15px; }
</style>
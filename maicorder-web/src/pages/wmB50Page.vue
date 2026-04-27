<template>
  <div class="page-root">
    <BackGround />

    <div class="ui-layer">
      <div class="top-bar">
        <button class="back-btn" @click="goBack">← BACK</button>
        <div class="top-title">B50 展示（后端接口）</div>
      </div>

      <div class="content">
        <section class="input-card">
          <h3 class="section-title">数据来源</h3>
          <div class="hint">只展示 B35（最佳）与 B15（新曲）。</div>
          <div class="form-row">
            <input v-model.trim="userIdInput" class="input" placeholder="id（用户id）" />
            <div>
              <button class="refresh-btn" :disabled="loading" @click="refreshB50">
                {{ loading ? '刷新中...' : '刷新' }}
              </button>

              <button class="refresh-btn" @click="goTOBind">
                前往绑定二维码
              </button>
            </div>
          </div>
          <div class="status-line">{{ statusText }}</div>
          <div class="totals top-totals">
            <div class="total-item">
              <div class="total-label">评分对象（新曲）</div>
              <div class="total-value">{{ targetDx.length }}</div>
            </div>
            <div class="total-item">
              <div class="total-label">评分对象（最佳）</div>
              <div class="total-value">{{ targetStandard.length }}</div>
            </div>
            <div class="total-item total-item--strong">
              <div class="total-label">B50 合计</div>
              <div class="total-value">{{ targetDx.length + targetStandard.length }}</div>
            </div>
          </div>
        </section>

        <section class="input-card">
          <div class="column-title">B35（评分对象：最佳）</div>
          <div v-if="targetStandard.length" class="track-grid">
            <article v-for="item in targetStandard" :key="`b35-${item.id}-${item.title}`" class="track-card">
              <div class="track-head">
                <div class="track-title" :title="item.title">{{ item.title }}</div>
                <div class="track-level" :class="levelClass(item.levelIndex)">{{ item.level }}</div>
              </div>
              <div class="song-row">
                <div class="song-cover-wrap">
                  <img class="song-cover" :src="item.coverUrl" :alt="item.title" />
                </div>
                <div class="song-main">
                  <div class="score-grid">
                    <div class="score-row">
                      <span class="score-value-wrap">
                        <span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </article>
          </div>
          <div v-else class="empty-state">没有标准谱面成绩</div>
        </section>

        <section class="input-card">
          <div class="column-title">B15（评分对象：新曲）</div>
          <div v-if="targetDx.length" class="track-grid">
            <article v-for="item in targetDx" :key="`b15-${item.id}-${item.title}`" class="track-card">
              <div class="track-head">
                <div class="track-title" :title="item.title">{{ item.title }}</div>
                <div class="track-level" :class="levelClass(item.levelIndex)">{{ item.level }}</div>
              </div>
              <div class="song-row">
                <div class="song-cover-wrap">
                  <img class="song-cover" :src="item.coverUrl" :alt="item.title" />
                </div>
                <div class="song-main">
                  <div class="score-grid">
                    <div class="score-row">
                      <span class="score-value-wrap">
                        <span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </article>
          </div>
          <div v-else class="empty-state">没有 DX 谱面成绩</div>
        </section>

      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import BackGround from '@/components/BackGround.vue'
import request from '@/utils/request'
import { wmB50DefaultMock } from '@/mocks/wmB50DefaultMock'

const router = useRouter()
const targetDx = ref([])
const targetStandard = ref([])
const loading = ref(false)
const statusText = ref('尚未加载，请先填写参数并点击刷新。')

const userIdInput = ref(localStorage.getItem('wmUserId') || '')
const B50_CACHE_KEY = 'wmB50CachedPayload'

const ASSET_BASE = 'https://assets2.lxns.net/maimai'

const normalizeSongId = (rawId) => {
  const id = Number(rawId)
  if (!Number.isFinite(id)) return 0
  if (id > 10000 && id < 100000) return id % 10000
  return id
}

const buildCoverUrl = (rawId) => `${ASSET_BASE}/jacket/${normalizeSongId(rawId)}.png`

const toNumberSafe = (value, fallback = 0) => {
  const n = Number(value)
  return Number.isFinite(n) ? n : fallback
}

const parseMaybeJson = (value) => {
  if (typeof value !== 'string') return value
  const s = value.trim()
  if (!(s.startsWith('{') || s.startsWith('['))) return value
  try {
    return JSON.parse(s)
  } catch {
    return value
  }
}

const getFieldCI = (obj, candidates) => {
  if (!obj || typeof obj !== 'object') return undefined
  for (const key of candidates) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) return obj[key]
  }
  const entries = Object.entries(obj)
  for (const [k, v] of entries) {
    const lk = String(k).toLowerCase()
    if (candidates.some((c) => String(c).toLowerCase() === lk)) return v
  }
  return undefined
}

const normalizeTrack = (item) => {
  // 兼容后端不同返回形态：
  // 1) 直接 Score 对象
  // 2) { score: {...}, song: {...} }
  // 3) { data: {...} } / { record: {...} }
  const itemParsed = parseMaybeJson(item)
  const base = parseMaybeJson(itemParsed?.score || itemParsed?.record || itemParsed?.data || itemParsed || {})
  const song = parseMaybeJson(itemParsed?.song || base?.song || {})

  const rawId = getFieldCI(base, ['id', 'song_id', 'songId']) ??
    getFieldCI(song, ['id', 'song_id', 'songId'])

  const title =
    getFieldCI(base, ['title', 'song_name', 'songName', 'name']) ||
    getFieldCI(song, ['title', 'song_name', 'songName', 'name']) ||
    '未知曲目'

  const achievement =
    getFieldCI(base, ['achievements', 'achievement', 'acc', 'accuracy']) ??
    0

  const level =
    getFieldCI(base, ['level', 'level_name', 'levelName']) ||
    getFieldCI(song, ['level', 'level_name', 'levelName']) ||
    '-'

  const levelIndex = getFieldCI(base, ['level_index', 'levelIndex', 'difficulty']) ??
    getFieldCI(song, ['level_index', 'levelIndex', 'difficulty'])

  return {
    id: rawId,
    title,
    level,
    levelIndex: toNumberSafe(levelIndex, -1),
    achievement: toNumberSafe(achievement, 0),
    coverUrl: buildCoverUrl(rawId)
  }
}

const levelClass = (levelIndex) => {
  switch (Number(levelIndex)) {
    case 0: return 'level-basic'
    case 1: return 'level-advanced'
    case 2: return 'level-expert'
    case 3: return 'level-master'
    case 4: return 'level-remaster'
    default: return 'level-unknown'
  }
}

const pickArray = (obj, keys) => {
  for (const key of keys) {
    const val = obj?.[key]
    if (Array.isArray(val)) return val
  }
  return []
}

const extractB50 = (raw) => {
  // 兼容常见字段：
  // 1) b35/b15
  // 2) standard/dx
  // 3) scores_b35/scores_b15（当前后端 Best50Data）
  const b35 = pickArray(raw, ['b35', 'standard', 'scores_b35', 'scoresB35'])
  const b15 = pickArray(raw, ['b15', 'dx', 'scores_b15', 'scoresB15'])
  if (b35.length || b15.length) return { b35, b15 }

  const nested = raw?.charts || raw?.chart || {}
  return {
    b35: pickArray(nested, ['b35', 'standard', 'scores_b35', 'scoresB35']),
    b15: pickArray(nested, ['b15', 'dx', 'scores_b15', 'scoresB15'])
  }
}

const applyB50Payload = async (payload) => {
  const { b35, b15 } = extractB50(payload || {})
  targetStandard.value = b35.map(normalizeTrack)
  targetDx.value = b15.map(normalizeTrack)

  // 调试辅助：若首项仍为“未知曲目”，把原始 key 打到状态文本
  const firstRaw = b35[0] || b15[0]
  const firstParsed = parseMaybeJson(firstRaw) || {}
  const firstBase = parseMaybeJson(firstParsed?.score || firstParsed?.record || firstParsed?.data || firstParsed || {})
  if ((targetStandard.value[0]?.title === '未知曲目' || targetDx.value[0]?.title === '未知曲目') && firstBase && typeof firstBase === 'object') {
    const keys = Object.keys(firstBase).slice(0, 12).join(', ')
    statusText.value = `字段调试：首条 key = [${keys}]`
  }
}

const clearCurrentList = () => {
  targetStandard.value = []
  targetDx.value = []
}

const readCachedPayload = () => {
  const raw = localStorage.getItem(B50_CACHE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

const saveCachedPayload = (payload) => {
  try {
    localStorage.setItem(B50_CACHE_KEY, JSON.stringify(payload))
  } catch {
    // 忽略本地存储异常，避免影响主流程
  }
}

const checkBackendReachable = async () => {
  try {
    const resp = await fetch('/api/maimai/id_b50?id=__ping__', { method: 'GET' })
    // 只要拿到 HTTP 响应，说明后端可达（即使是 4xx/5xx 业务错误）
    return !!resp
  } catch {
    return false
  }
}

const refreshB50 = async () => {
  const id = userIdInput.value
  if (!id) {
    statusText.value = 'id 不能为空。'
    return
  }

  loading.value = true
  statusText.value = '正在请求后端 /api/maimai/id_b50 ...'
  try {
    localStorage.setItem('wmUserId', id)

    const resp = await request.get('/maimai/id_b50', {
      params: { id }
    })

    // 兼容两种响应壳：
    // 1) { code, data, message }（旧）
    // 2) { status, data, message }（当前后端 ApiResponse）
    const ok = resp.code === 200 || resp.status === 200
    if (!ok || !resp.data) {
      throw new Error(resp.message || 'B50 接口返回异常')
    }

    // 如果 data 内再包一层 data，也兼容展开
    const payload = resp.data?.data || resp.data
    await applyB50Payload(payload)
    saveCachedPayload(payload)
    statusText.value = `刷新成功：B35 ${targetStandard.value.length}，B15 ${targetDx.value.length}`
  } catch (err) {
    const msg = err?.response?.data?.message || err?.message || '刷新失败'
    statusText.value = `刷新失败：${msg}`
  } finally {
    loading.value = false
  }
}
const maicorderId = ref('')
const goTOBind = () => {
  router.push('/bindQR')
}


onMounted(async () => {

  maicorderId.value = localStorage.getItem('maicorderId') || ''
  userIdInput.value = maicorderId.value

  const cached = readCachedPayload()
  if (cached) {
    await applyB50Payload(cached)
    statusText.value = `已加载本地缓存：B35 ${targetStandard.value.length}，B15 ${targetDx.value.length}`
    return
  }

  const backendReachable = await checkBackendReachable()
  if (!backendReachable) {
    await applyB50Payload(wmB50DefaultMock)
    statusText.value = `后端不可达，已使用默认 Mock：B35 ${targetStandard.value.length}，B15 ${targetDx.value.length}`
    return
  }

  clearCurrentList()
  statusText.value = '后端可用，暂无本地缓存。请输入 id 后点击刷新。'
})

const goBack = () => {
  if (window.history.length > 1) router.back()
  else router.push('/moreToolPage')
}
</script>

<style scoped>
.page-root {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f3edf7;
}

.ui-layer {
  position: relative;
  z-index: 1;
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.top-bar {
  flex-shrink: 0;
  padding: 16px 20px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 2px solid rgba(0, 0, 0, 0.75);
  background: rgba(255, 255, 255, 0.75);
}

.back-btn {
  background: #000;
  color: #fff;
  border: 2px solid #000;
  padding: 8px 14px;
  font-weight: 800;
  font-size: 0.85rem;
  cursor: pointer;
  white-space: nowrap;
}

.top-title {
  font-weight: 900;
  font-size: 1.05rem;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  text-align: right;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 14px 20px 24px;
  max-width: 1280px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.input-card {
  border: 2px solid #000;
  background: rgba(255, 255, 255, 0.82);
  padding: 14px;
  margin-bottom: 14px;
}

.top-totals {
  margin-top: 12px;
}

.section-title {
  font-weight: 900;
  margin: 0 0 10px 0;
  text-transform: uppercase;
}

.hint {
  margin-top: 2px;
  color: rgba(0, 0, 0, 0.65);
  font-size: 0.92rem;
}

.form-row {
  margin-top: 10px;
  display: grid;
  grid-template-columns: minmax(220px, 1fr) auto;
  gap: 8px;
}

.input {
  border: 2px solid #000;
  padding: 8px 10px;
  font-size: 0.9rem;
  background: #fff;
  outline: none;
}

.refresh-btn {
  margin-left: 2px;
  margin-right: 2px;
  margin-top: 2px;
  border: 2px solid #000;
  background: #000;
  color: #fff;
  font-weight: 800;
  padding: 0 16px;
  cursor: pointer;
  min-height: 36px;
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.status-line {
  margin-top: 10px;
  font-size: 0.88rem;
  color: rgba(0, 0, 0, 0.65);
}

.totals {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 8px;
  margin-bottom: 0;
}

.total-item {
  border: 2px solid #000;
  padding: 10px 8px;
  background: #fff;
}

.total-item--strong {
  background: #000;
  color: #fff;
}

.total-label {
  font-size: 0.82rem;
  color: rgba(0, 0, 0, 0.75);
  font-weight: 800;
}

.total-item--strong .total-label {
  color: rgba(255, 255, 255, 0.86);
}

.total-value {
  font-size: 1.1rem;
  font-weight: 1000;
  margin-top: 4px;
}

.column-title {
  font-weight: 1000;
  margin-bottom: 8px;
  text-transform: uppercase;
}

.track-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(210px, 1fr));
  gap: 10px;
}

.track-card {
  border: 2px solid #000;
  background: #fff;
  padding: 8px;
  min-width: 0;
}

.track-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.track-title {
  font-weight: 900;
  font-size: 0.9rem;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.track-level {
  border: 2px solid #000;
  background: #000;
  color: #fff;
  font-size: 0.74rem;
  font-weight: 900;
  padding: 0 6px;
  line-height: 1.4;
  flex-shrink: 0;
}

.track-level.level-basic {
  background: #21a558;
  color: #fff;
}

.track-level.level-advanced {
  background: #e3c500;
  color: #111;
}

.track-level.level-expert {
  background: #d62f2f;
  color: #fff;
}

.track-level.level-master {
  background: #7a3bd1;
  color: #fff;
}

.track-level.level-remaster {
  background: #d9c7ff;
  color: #2a144c;
}

.track-level.level-unknown {
  background: #000;
  color: #fff;
}

.empty-state {
  padding: 32px 10px;
  text-align: center;
  color: rgba(0, 0, 0, 0.55);
  font-weight: 700;
}

.score-grid {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.song-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.song-cover-wrap {
  width: 68px;
  height: 68px;
  border: 2px solid #000;
  padding: 1px;
  background: #fff;
  flex-shrink: 0;
}

.song-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: 1px solid #000;
  display: block;
}

.song-main {
  min-width: 0;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.score-row {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.score-value-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  min-height: 56px;
}

.score-value {
  font-weight: 900;
  width: 100%;
  text-align: right;
  font-size: clamp(1.05rem, 0.7vw + 0.75rem, 1.35rem);
  line-height: 1;
  letter-spacing: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: clip;
}

@media (max-width: 980px) {
  .top-bar {
    align-items: flex-start;
  }

  .top-title {
    font-size: 0.92rem;
    text-align: left;
    line-height: 1.2;
  }

  .track-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .totals {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .refresh-btn {
    width: 100%;
  }

  .content {
    padding: 12px 12px 18px;
  }

  .input-card {
    padding: 12px;
    margin-bottom: 12px;
  }

  .song-cover-wrap {
    width: 62px;
    height: 62px;
  }

  .track-title {
    font-size: 0.84rem;
  }

  .score-value {
    font-size: 1.12rem;
  }
}

@media (min-width: 981px) and (max-width: 1280px) {
  .content {
    padding-left: 14px;
    padding-right: 14px;
  }

  .track-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (min-width: 1281px) {
  .track-grid {
    grid-template-columns: repeat(5, minmax(0, 1fr));
  }
}
</style>

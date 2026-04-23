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
            <button class="refresh-btn" :disabled="loading" @click="refreshB50">
              {{ loading ? '刷新中...' : '刷新' }}
            </button>
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
          <div class="split">
            <div class="column">
              <div class="column-title">B35（评分对象：最佳）</div>
              <CardList :items="targetStandard" :loading="false">
                <template #default="{ item }">
                  <WordCard variant="filled" :title="item.title" :subTitle="item.level">
                    <div class="song-row">
                      <div class="song-cover-wrap">
                        <img class="song-cover" :src="item.coverUrl" :alt="item.title" />
                      </div>
                      <div class="song-main">
                        <div class="song-artist">Artist: {{ item.artist || '未知作曲者' }}</div>
                        <div class="score-grid">
                          <div class="score-row">
                            <span class="score-label">达成率</span><span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </WordCard>
                </template>
                <template #empty>
                  <div class="empty-state">没有标准谱面成绩</div>
                </template>
              </CardList>
            </div>

            <div class="column">
              <div class="column-title">B15（评分对象：新曲）</div>
              <CardList :items="targetDx" :loading="false">
                <template #default="{ item }">
                  <WordCard variant="filled" :title="item.title" :subTitle="item.level">
                    <div class="song-row">
                      <div class="song-cover-wrap">
                        <img class="song-cover" :src="item.coverUrl" :alt="item.title" />
                      </div>
                      <div class="song-main">
                        <div class="song-artist">Artist: {{ item.artist || '未知作曲者' }}</div>
                        <div class="score-grid">
                          <div class="score-row">
                            <span class="score-label">达成率</span><span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </WordCard>
                </template>
                <template #empty>
                  <div class="empty-state">没有 DX 谱面成绩</div>
                </template>
              </CardList>
            </div>
          </div>
        </section>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import BackGround from '@/components/BackGround.vue'
import CardList from '@/components/CardList.vue'
import WordCard from '@/components/WordCard.vue'
import request from '@/utils/request'

const router = useRouter()
const targetDx = ref([])
const targetStandard = ref([])
const loading = ref(false)
const statusText = ref('尚未加载，请先填写参数并点击刷新。')

const userIdInput = ref(localStorage.getItem('wmUserId') || '')
const songMetaCache = new Map()

const ASSET_BASE = 'https://assets2.lxns.net/maimai'
const API_BASE = 'https://maimai.lxns.net/api/v0/maimai'

const normalizeSongId = (rawId) => {
  const id = Number(rawId)
  if (!Number.isFinite(id)) return 0
  if (id > 10000 && id < 100000) return id % 10000
  return id
}

const buildCoverUrl = (rawId) => `${ASSET_BASE}/jacket/${normalizeSongId(rawId)}.png`

const fetchSongArtist = async (rawId) => {
  const songId = normalizeSongId(rawId)
  if (!songId) return ''
  if (songMetaCache.has(songId)) return songMetaCache.get(songId)
  try {
    const resp = await fetch(`${API_BASE}/song/${songId}`)
    if (!resp.ok) return ''
    const data = await resp.json()
    const artist = data?.artist || ''
    songMetaCache.set(songId, artist)
    return artist
  } catch {
    return ''
  }
}

const normalizeTrack = (item) => ({
  id: item.id,
  title: item.title || item.song_name || item.songName || item.name || '未知曲目',
  level: item.level || '-',
  achievement: Number(item.achievements || 0),
  artist: item.artist || '',
  coverUrl: buildCoverUrl(item.id)
})

const enrichTracks = async (tracks) => {
  const enriched = await Promise.all(
    tracks.map(async (track) => {
      if (track.artist) return track
      const artist = await fetchSongArtist(track.id)
      return { ...track, artist }
    })
  )
  return enriched
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
  const b35 = pickArray(raw, ['b35', 'standard', 'scores_b35'])
  const b15 = pickArray(raw, ['b15', 'dx', 'scores_b15'])
  if (b35.length || b15.length) return { b35, b15 }

  const nested = raw?.charts || raw?.chart || {}
  return {
    b35: pickArray(nested, ['b35', 'standard', 'scores_b35']),
    b15: pickArray(nested, ['b15', 'dx', 'scores_b15'])
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
    const { b35, b15 } = extractB50(payload)
    targetStandard.value = await enrichTracks(b35.map(normalizeTrack))
    targetDx.value = await enrichTracks(b15.map(normalizeTrack))
    statusText.value = `刷新成功：B35 ${targetStandard.value.length}，B15 ${targetDx.value.length}`
  } catch (err) {
    const msg = err?.response?.data?.message || err?.message || '刷新失败'
    statusText.value = `刷新失败：${msg}`
  } finally {
    loading.value = false
  }
}

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
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  background: #000;
  color: #fff;
  border: none;
  padding: 8px 14px;
  font-weight: 800;
  font-size: 0.85rem;
  cursor: pointer;
  white-space: nowrap;
}

.top-title {
  font-weight: 900;
  font-size: 1.15rem;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px 24px;
}

.input-card {
  border: 2px solid rgba(0, 0, 0, 0.7);
  background: rgba(255, 255, 255, 0.65);
  padding: 18px 16px;
  margin-bottom: 18px;
}
.top-totals {
  margin-top: 12px;
}

.section-title {
  font-weight: 900;
  margin: 0 0 12px 0;
}

.hint {
  margin-top: 2px;
  color: rgba(0, 0, 0, 0.65);
  font-size: 0.92rem;
}

.form-row {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.input {
  border: 1px solid rgba(0, 0, 0, 0.4);
  padding: 8px 10px;
  font-size: 0.9rem;
  background: rgba(255, 255, 255, 0.8);
}

.refresh-btn {
  border: none;
  background: #000;
  color: #fff;
  font-weight: 800;
  padding: 0 14px;
  cursor: pointer;
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
  gap: 12px;
  margin-bottom: 14px;
}

.total-item {
  border: 2px solid rgba(0, 0, 0, 0.35);
  padding: 12px 10px;
}

.total-item--strong {
  border-color: rgba(0, 0, 0, 0.85);
}

.total-label {
  font-size: 0.9rem;
  color: rgba(0, 0, 0, 0.7);
  font-weight: 800;
}

.total-value {
  font-size: 1.25rem;
  font-weight: 1000;
  margin-top: 6px;
}

.split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.column-title {
  font-weight: 1000;
  margin-bottom: 10px;
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
  gap: 8px;
}

.song-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.song-cover-wrap {
  width: 72px;
  height: 72px;
  border: 2px solid rgba(0, 0, 0, 0.7);
  border-radius: 8px;
  padding: 2px;
  background: rgba(255, 255, 255, 0.9);
  flex-shrink: 0;
}

.song-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
  display: block;
}

.song-main {
  min-width: 0;
  flex: 1;
}

.song-artist {
  font-size: 0.84rem;
  color: rgba(0, 0, 0, 0.72);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.score-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.score-label {
  font-weight: 800;
  color: rgba(0, 0, 0, 0.7);
}

.score-value {
  font-weight: 900;
}

@media (max-width: 980px) {
  .split {
    grid-template-columns: 1fr;
  }
  .totals {
    grid-template-columns: 1fr;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

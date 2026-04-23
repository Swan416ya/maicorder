<template>
  <div class="page-root">
    <BackGround />

    <div class="ui-layer">
      <div class="top-bar">
        <button class="back-btn" @click="goBack">← BACK</button>
        <div class="top-title">B50 展示（前端 Mock）</div>
      </div>

      <div class="content">
        <section class="input-card">
          <h3 class="section-title">数据来源</h3>
          <div class="hint">
            当前页面为纯前端 Mock 数据展示，不依赖后端与数据库。
          </div>
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

        <section class="result-card">
          <div class="split">
            <div class="column">
              <div class="column-title">B35（评分对象：最佳）</div>
              <CardList :items="targetStandard" :loading="false">
                <template #default="{ item }">
                  <WordCard variant="filled" :title="item.title" :subTitle="item.level">
                    <div class="score-grid">
                      <div class="score-row">
                        <span class="score-label">达成率</span><span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
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
                    <div class="score-grid">
                      <div class="score-row">
                        <span class="score-label">达成率</span><span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
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

        <section class="result-card">
          <div class="split">
            <div class="column">
              <div class="column-title">评分候选（最佳）</div>
              <CardList :items="candidateStandard" :loading="false">
                <template #default="{ item }">
                  <WordCard variant="outlined" :title="item.title" :subTitle="item.level">
                    <div class="score-row">
                      <span class="score-label">达成率</span><span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
                    </div>
                  </WordCard>
                </template>
              </CardList>
            </div>
            <div class="column">
              <div class="column-title">评分候选（新曲）</div>
              <CardList :items="candidateDx" :loading="false">
                <template #default="{ item }">
                  <WordCard variant="outlined" :title="item.title" :subTitle="item.level">
                    <div class="score-row">
                      <span class="score-label">达成率</span><span class="score-value">{{ item.achievement.toFixed(4) }}%</span>
                    </div>
                  </WordCard>
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
import { useRouter } from 'vue-router'

import BackGround from '@/components/BackGround.vue'
import CardList from '@/components/CardList.vue'
import WordCard from '@/components/WordCard.vue'
import {
  targetDx,
  targetStandard,
  candidateDx,
  candidateStandard
} from '@/mocks/wmB50Mock'

const router = useRouter()

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

.result-card {
  border: 2px solid rgba(0, 0, 0, 0.7);
  background: rgba(255, 255, 255, 0.6);
  padding: 18px 16px;
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
}
</style>

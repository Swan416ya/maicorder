<template>
  <div class="checkin-calendar-wrapper">
    <!-- 统计信息部分 - 不在卡片内 -->
    <div class="calendar-stats-section">
      <div class="calendar-user-info">
        <h2 class="username">{{ userName }}</h2>
      </div>
      <div class="stats-row">
        <div class="stat-item">
          <div class="stat-label">TOTAL</div>
          <div class="stat-value">{{ totalCheckIns }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">MAX STREAK</div>
          <div class="stat-value">{{ longestStreak }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">CURRENT</div>
          <div class="stat-value">{{ currentStreak }}</div>
        </div>
      </div>
    </div>
    
    <!-- 日历部分 - 用卡片包裹 -->
    <PurpleCard variant="filled" clickable="false">
      <div class="calendar-content">
        <!-- 日历网格 -->
        <div class="calendar-grid-container">
          <!-- 星期标签 -->
          <div class="weekday-labels">
            <div 
              v-for="day in weekDays" 
              :key="day.label"
              class="weekday-label"
            >
              {{ day.label }}
            </div>
          </div>
          
          <!-- 日历网格包装器 -->
          <div class="calendar-grid-wrapper" ref="calendarContainer">
            <!-- 月份标签 -->
            <div class="month-labels">
              <div 
                v-for="(month, index) in visibleMonths" 
                :key="index"
                class="month-label"
                :style="{
                  left: `${month.position}%`,
                  width: `${month.width}%`
                }"
              >
                {{ month.name }}
              </div>
            </div>
            
            <!-- 日历方格 -->
            <div class="calendar-grid" :style="gridStyle">
              <div 
                v-for="day in calendarDays"
                :key="day.date"
                :class="['day-cell', { 'future-day': day.isFuture }]"
                :style="{
                  gridColumn: day.weekIndex + 1,
                  gridRow: day.dayIndex + 1
                }"
                :title="getDayTooltip(day)"
              >
                <div 
                  v-if="!day.isFuture"
                  class="day-fill"
                  :style="{ backgroundColor: day.color }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </PurpleCard>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import PurpleCard from '@/components/PurpleCard.vue'

const props = defineProps({
  checkIns: {
    type: Array,
    default: () => []
  }
})

// 获取用户名 - 参考主页面获取方式
const userName = computed(() => {
  try {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      const userInfo = JSON.parse(userStr)
      return userInfo.username || userInfo.name || 'User'
    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
  }
  return 'User'
})

// 星期标签（从周日开始）
const weekDays = [
  { label: 'SUN', index: 0 },
  { label: 'MON', index: 1 },
  { label: 'TUE', index: 2 },
  { label: 'WED', index: 3 },
  { label: 'THU', index: 4 },
  { label: 'FRI', index: 5 },
  { label: 'SAT', index: 6 }
]

// 月份名称
const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

// 颜色定义
const COLOR_NO_CHECKIN = '#ebedf0' // 浅灰色
const COLOR_HAS_CHECKIN = '#216e39' // 深绿色

// 引用日历容器
const calendarContainer = ref(null)

// 处理出勤数据
const checkInMap = computed(() => {
  const map = {}
  props.checkIns.forEach(checkIn => {
    if (checkIn.checkInTime) {
      const date = new Date(checkIn.checkInTime)
      const dateStr = date.toISOString().split('T')[0] // YYYY-MM-DD
      map[dateStr] = (map[dateStr] || 0) + 1
    }
  })
  return map
})

// 统计信息
const totalCheckIns = computed(() => props.checkIns.length)

const longestStreak = computed(() => {
  let maxStreak = 0
  let currentStreak = 0
  let lastDate = null
  
  // 按日期排序的出勤记录
  const sortedDates = Object.keys(checkInMap.value).sort()
  
  sortedDates.forEach(dateStr => {
    const date = new Date(dateStr)
    
    if (lastDate) {
      const diffDays = Math.floor((date - lastDate) / (1000 * 60 * 60 * 24))
      if (diffDays === 1) {
        currentStreak++
      } else {
        maxStreak = Math.max(maxStreak, currentStreak)
        currentStreak = 1
      }
    } else {
      currentStreak = 1
    }
    
    lastDate = date
  })
  
  maxStreak = Math.max(maxStreak, currentStreak)
  return maxStreak
})

const currentStreak = computed(() => {
  let streak = 0
  const today = new Date('2026-02-13') // 固定日期：2026-02-13
  let checkDate = new Date(today)
  
  // 检查今天是否有出勤
  const todayStr = today.toISOString().split('T')[0]
  if (checkInMap.value[todayStr]) {
    streak++
    checkDate.setDate(checkDate.getDate() - 1)
    
    // 向前检查连续出勤天数
    while (true) {
      const dateStr = checkDate.toISOString().split('T')[0]
      if (checkInMap.value[dateStr]) {
        streak++
        checkDate.setDate(checkDate.getDate() - 1)
      } else {
        break
      }
    }
  }
  
  return streak
})

// 计算显示范围：53周，结束日期是今天（2026-02-13）所在周的周六
const calendarRange = computed(() => {
  const today = new Date('2026-02-13') // 固定日期：2026-02-13
  
  // 找到当前周的周六
  const saturday = new Date(today)
  const dayOfWeek = saturday.getDay() // 0 = Sunday, 6 = Saturday
  
  // 计算到周六的天数差
  const daysToSaturday = (dayOfWeek === 6) ? 0 : (6 - dayOfWeek)
  saturday.setDate(saturday.getDate() + daysToSaturday)
  
  // 开始日期是53周前的周一
  const startDate = new Date(saturday)
  startDate.setDate(startDate.getDate() - (53 * 7) + 1) // 53周=371天，+1是因为包含起始日期
  
  return {
    start: startDate,
    end: saturday,
    weeks: 53
  }
})

// 生成日历数据
const calendarDays = computed(() => {
  const days = []
  const currentDate = new Date(calendarRange.value.start)
  const today = new Date('2026-02-13') // 固定日期
  const endDate = new Date(calendarRange.value.end)
  
  for (let week = 0; week < calendarRange.value.weeks; week++) {
    for (let dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
      const date = new Date(currentDate)
      const dateStr = date.toISOString().split('T')[0]
      
      // 检查是否是未来日期
      const isFuture = date > endDate
      
      // 获取出勤次数
      const checkInCount = checkInMap.value[dateStr] || 0
      
      // 确定颜色：有出勤就绿色，没有就灰色，未来日期不显示
      const hasCheckIn = checkInCount > 0
      const color = hasCheckIn ? COLOR_HAS_CHECKIN : COLOR_NO_CHECKIN
      
      days.push({
        date: dateStr,
        dateObj: date,
        weekIndex: week,
        dayIndex: dayOfWeek,
        checkInCount,
        hasCheckIn,
        color,
        isFuture,
        isToday: dateStr === '2026-02-13'
      })
      
      currentDate.setDate(currentDate.getDate() + 1)
    }
  }
  
  return days
})

// 计算月份标签位置
const visibleMonths = computed(() => {
  const months = []
  const startDate = new Date(calendarRange.value.start)
  const totalDays = calendarRange.value.weeks * 7
  
  let lastMonth = -1
  let monthStartDay = 0
  
  // 遍历每一天，找到月份变化的位置
  for (let day = 0; day < totalDays; day++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + day)
    const month = date.getMonth()
    
    if (month !== lastMonth) {
      if (lastMonth !== -1) {
        // 计算上一个月份的宽度
        const width = ((day - monthStartDay) / totalDays) * 100
        months[months.length - 1].width = width
      }
      
      // 计算这个月份在日历中的位置（基于周数）
      const weekIndex = Math.floor(day / 7)
      months.push({
        name: monthNames[month],
        month,
        position: (weekIndex / calendarRange.value.weeks) * 100,
        width: 0
      })
      
      monthStartDay = day
      lastMonth = month
    }
  }
  
  // 设置最后一个月份的宽度
  if (months.length > 0) {
    const lastMonthData = months[months.length - 1]
    lastMonthData.width = ((totalDays - monthStartDay) / totalDays) * 100
  }
  
  return months
})

// 网格样式
const gridStyle = computed(() => {
  // 53周，每周1列，共53列
  // 7天，每天1行，共7行
  const columns = calendarRange.value.weeks
  const rows = 7
  
  // 每个方格大小（调整得更小）
  const daySize = 8 // 从10px改为8px
  const gap = 1 // 从2px改为1px
  
  // 计算总宽度（包含星期标签）
  const weekLabelWidth = 20 // 星期标签宽度
  const gridContentWidth = columns * (daySize + gap) - gap
  
  return {
    gridTemplateColumns: `repeat(${columns}, ${daySize}px)`,
    gridTemplateRows: `repeat(${rows}, ${daySize}px)`,
    gap: `${gap}px`,
    width: `${gridContentWidth}px`,
    height: `${rows * (daySize + gap) - gap}px`,
    marginLeft: `${weekLabelWidth}px`
  }
})

// 工具函数
const getDayTooltip = (day) => {
  if (day.isFuture) {
    const date = new Date(day.date)
    const formattedDate = `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')}`
    return `${formattedDate}: 未来日期`
  }
  
  const dateStr = day.date
  const checkInCount = day.checkInCount
  const date = new Date(dateStr)
  const formattedDate = `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')}`
  
  return `${formattedDate}: ${checkInCount} check-in${checkInCount !== 1 ? 's' : ''}`
}

// 确保容器宽度足够显示完整日历
const ensureCalendarVisibility = () => {
  nextTick(() => {
    if (calendarContainer.value) {
      const container = calendarContainer.value
      const gridWidth = parseInt(gridStyle.value.width)
      const weekLabelWidth = 20 // 星期标签宽度
      container.style.minWidth = `${gridWidth + weekLabelWidth}px`
    }
  })
}

onMounted(() => {
  ensureCalendarVisibility()
})

watch(() => props.checkIns, () => {
  ensureCalendarVisibility()
}, { immediate: true })
</script>

<style scoped>
.checkin-calendar-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 统计信息部分 - 不在卡片内 */
.calendar-stats-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.calendar-user-info {
  margin-bottom: 8px;
}

.username {
  font-size: 1.5rem;
  font-weight: 900;
  color: #000;
  text-transform: uppercase;
  margin: 0;
  letter-spacing: 1px;
}

/* 统计行样式 */
.stats-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border: 2px solid #000;
  background-color: rgba(255, 255, 255, 0.8);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4px 0;
}

.stat-label {
  font-size: 10px;
  font-weight: 700;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: 900;
  color: #000;
}

/* 日历内容 - 在卡片内 */
.calendar-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 日历网格容器 */
.calendar-grid-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* 星期标签容器 - 固定位置 */
.weekday-labels {
  position: absolute;
  left: 0;
  top: 20px; /* 为月份标签留出空间 */
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 20px;
  z-index: 1;
}

.weekday-label {
  font-size: 9px;
  font-weight: 700;
  color: #666;
  text-align: right;
  padding-right: 4px;
  height: 9px; /* 方格高度 + 间距 */
  line-height: 9px;
  letter-spacing: 0.5px;
}

/* 日历网格包装器 - 包含月份标签和方格 */
.calendar-grid-wrapper {
  width: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  position: relative;
  padding: 4px 0;
}

/* 月份标签 */
.month-labels {
  position: absolute;
  top: 0;
  left: 20px; /* 与星期标签对齐 */
  right: 0;
  height: 20px;
  pointer-events: none;
}

.month-label {
  position: absolute;
  font-size: 10px;
  color: #666;
  font-weight: 600;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: 0.5px;
}

/* 日历网格 */
.calendar-grid {
  position: relative;
  display: grid;
  margin-top: 20px; /* 为月份标签留出空间 */
  margin-left: 20px; /* 与weekday-labels对齐 */
}

/* 日历方格 */
.day-cell {
  position: relative;
  cursor: pointer;
  transition: transform 0.1s ease;
}

.day-cell:hover:not(.future-day) .day-fill {
  transform: scale(1.15);
  z-index: 1;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.5);
}

.day-fill {
  width: 100%;
  height: 100%;
  border: none;
  transition: transform 0.1s ease;
  border-radius: 1px;
}

.future-day {
  opacity: 0.2;
}

.future-day .day-fill {
  background-color: #f0f0f0 !important;
}

/* 滚动条样式 */
.calendar-grid-wrapper::-webkit-scrollbar {
  height: 6px;
}

.calendar-grid-wrapper::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.calendar-grid-wrapper::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.calendar-grid-wrapper::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stats-row {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .stat-item {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  
  .weekday-labels {
    top: 22px;
  }
  
  .weekday-label {
    font-size: 8px;
    height: 8px;
    line-height: 8px;
  }
  
  .month-label {
    font-size: 9px;
  }
  
  /* 调整网格大小以适应小屏幕 */
  .calendar-grid {
    margin-left: 18px;
  }
}
</style>
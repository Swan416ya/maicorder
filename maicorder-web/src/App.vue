<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 动态计算过渡名称
const transitionName = computed(() => {
  // 如果当前页面的路由元数据里标记了 noGlobalTransition (比如 Login)，则不使用全局动画
  return route.meta.noGlobalTransition ? '' : 'fade-white'
})
</script>

<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <!-- 
        新增属性：appear
        作用：强制在页面初次加载时也执行 Enter 动画。
        这样直接进入 MainLayout 时，也会触发从白到有的渐变，不会卡在白屏。
      -->
      <transition 
        :name="transitionName" 
        mode="out-in" 
        appear
      >
        <!-- 
          key="route.fullPath" 确保路由变化时强制重新渲染组件 
          这是防止白屏和动画卡顿的另一道保险
        -->
        <component :is="Component" :key="route.fullPath" />
      </transition>
    </router-view>
  </div>
</template>

<style>
/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 
  必须确保背景是纯白 
  这样当 opacity 为 0 时，显示的是白色背景
*/
body, html {
  width: 100%;
  height: 100%;
  background-color: #ffffff; 
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  overflow: hidden;
}

#app {
  width: 100vw;
  height: 100vh;
}

/* --- 全局过渡动画定义 --- */

/* 
  进入(Enter)和离开(Leave)的过渡时间
  0.6s 稍微慢一点，让渐变白的效果更明显，显得更优雅
*/
.fade-white-enter-active,
.fade-white-leave-active {
  transition: opacity 0.6s ease;
}

/* 
  进入前状态：透明
  离开后状态：透明
*/
.fade-white-enter-from,
.fade-white-leave-to {
  opacity: 0;
}
</style>
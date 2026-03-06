<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const transitionName = computed(() => {
  return route.meta.noGlobalTransition ? 'none' : 'fade-white'
})
</script>

<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition 
        :name="transitionName" 
        appear
      >
        <component :is="Component" :key="route.fullPath" class="page-transition-container" />
      </transition>
    </router-view>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body, html {
  width: 100%;
  height: 100%;
  background-color: #ffffff; 
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

#app {
  width: 100vw;
  height: 100vh;
  overflow: hidden; /* 防止过渡期间出现滚动条 */
  position: relative;
}

.page-transition-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.fade-white-enter-active {
  transition: opacity 0.3s ease-in-out;
  z-index: 2;
}
.fade-white-leave-active {
  transition: opacity 0.3s ease-in-out;
  z-index: 1;
}
.fade-white-enter-from,
.fade-white-leave-to {
  opacity: 0;
}

.none-enter-active,
.none-leave-active {
  transition: none;
}
.none-enter-from,
.none-leave-to {
  opacity: 1;
}
</style>
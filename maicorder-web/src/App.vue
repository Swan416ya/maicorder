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
        mode="out-in"
        appear
        @after-enter="() => {}"
      >
        <component :is="Component" :key="route.fullPath" />
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
  overflow: auto;
}

.fade-white-enter-active,
.fade-white-leave-active {
  transition: opacity 0.6s ease;
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
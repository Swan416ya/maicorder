<script setup>
// 删掉原来的import LoginLayout，路由会自动处理组件渲染
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div id="app">
    <!-- 路由出口：路由匹配到的组件会渲染在这里 -->
    <router-view />
  </div>
</template>

<style>
/* 保留你的全局样式，无需修改 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #fff;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

#app {
  width: 100vw;
  height: 100vh;
}
</style>
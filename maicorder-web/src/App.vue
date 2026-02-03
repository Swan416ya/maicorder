<script setup>
// 导入你实际的登录布局组件（路径完全匹配你的文件结构）
import LoginLayout from './components/LoginLayout.vue'
</script>

<template>
  <div id="app">
    <!-- 渲染登录布局组件 -->
    <LoginLayout />
  </div>
</template>

<style>
/* 全局基础样式：确保页面无默认边距、溢出隐藏，统一字体 */
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
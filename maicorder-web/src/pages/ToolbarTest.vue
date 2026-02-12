<template>
  <div class="demo-page">
    
    <!-- 场景演示区域 -->
    <div class="preview-box">
      <h2>Material 3 Toolbar Demo</h2>
      <p>当前点击/选中: <strong>{{ currentSelection }}</strong></p>
      
      <div class="controls">
        <label>
          <input type="checkbox" v-model="config.selectable"> 开启选中态 (Selectable)
        </label>
        <label>
          <input type="checkbox" v-model="config.showLabel"> 显示文字 (Show Label)
        </label>
      </div>
    </div>

    <!-- 
      === 组件调用 === 
      根据上面的 Checkbox 动态改变 props
    -->
    <FloatingToolbar
      v-model="currentSelection"
      :items="toolbarData"
      :selectable="config.selectable"
      :show-label="config.showLabel"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import FloatingToolbar, { type ToolbarItem } from '@/components/FloatingToolbar.vue';

// 状态管理
const currentSelection = ref('format_bold');

// 配置控制
const config = reactive({
  selectable: true, // 默认开启选中背景
  showLabel: true   // 默认开启文字
});

// 图标数据 (SVG 路径)
const icons = {
  bold: `<svg viewBox="0 0 24 24"><path d="M15.6 10.79c.97-.67 1.65-1.77 1.65-2.79 0-2.26-1.75-4-4-4H7v14h7.04c2.09 0 3.71-1.7 3.71-3.79 0-1.52-.98-2.83-2.15-3.42zM10 6.5h3c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5h-3v-3zm3.5 9H10v-3h3.5c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5z"/></svg>`,
  italic: `<svg viewBox="0 0 24 24"><path d="M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4z"/></svg>`,
  link: `<svg viewBox="0 0 24 24"><path d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"/></svg>`,
  color: `<svg viewBox="0 0 24 24"><path d="M12 22c4.97 0 9-4.03 9-9-4.97 0-9 4.03-9 9zM5.6 10.25a2.5 2.5 0 0 0 3.92 2.06l-.02.19a2.5 2.5 0 0 0 5 0l-.02-.19a2.5 2.5 0 0 0 3.92-2.06c0-1.38-1.12-2.5-2.5-2.5-.53 0-1.01.16-1.42.44l-.08-.69h-1.84l-.08.69c-.41-.28-.89-.44-1.42-.44-1.38 0-2.5 1.12-2.5 2.5z"/></svg>`,
};

// 工具栏数据定义
const toolbarData: ToolbarItem[] = [
  { 
    key: 'format_bold', 
    label: '加粗', 
    title: 'Bold', 
    icon: icons.bold 
  },
  { 
    key: 'format_italic', 
    label: '斜体', 
    title: 'Italic', 
    icon: icons.italic 
  },
  { 
    key: 'insert_link', 
    label: '链接', 
    title: 'Link', 
    icon: icons.link,
    // 示例：点击执行特殊逻辑
    action: () => alert('插入链接功能触发') 
  },
  { 
    key: 'color_fill', 
    label: '颜色', 
    title: 'Color', 
    icon: icons.color 
  }
];
</script>

<style>
/* 页面基础样式 */
body {
  margin: 0;
  background-color: #FDF7FF; /* MD3 极浅紫背景 */
  font-family: 'Roboto', sans-serif;
  color: #1D192B;
}

.demo-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 100px;
}

.preview-box {
  background: white;
  padding: 30px;
  border-radius: 24px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  text-align: center;
}

.controls {
  margin-top: 20px;
  display: flex;
  gap: 20px;
  justify-content: center;
}

label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 16px;
  background: #E8DEF8;
  border-radius: 8px;
  font-size: 14px;
}
</style>
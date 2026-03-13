# maicorder-web

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### 离线模式（不连后端看所有页面样式）

不启动后端时，可用离线模式单独跑前端，查看各页面样式与布局：

```sh
npm run dev:offline
```

或在正常 `npm run dev` 下访问带参数的地址：`http://localhost:8081/?offline=1`。  
离线模式下会使用本地 Mock 数据，并自动写入“已登录”状态，可直接在应用内跳转登录页、主页、履历、记录详情、个人页等所有路由。

### Compile and Minify for Production

```sh
npm run build
```

import { createSSRApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';

// uni-app 的入口文件。
// 必须导出 createApp 函数，uni-app 会调用它创建应用实例。
export function createApp() {
  const app = createSSRApp(App);
  // 注册 Pinia 状态管理，页面里才能使用 useUserStore 等 store
  app.use(createPinia());
  return { app };
}

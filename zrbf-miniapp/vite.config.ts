import { defineConfig } from 'vite';
import uni from '@dcloudio/vite-plugin-uni';

// uni-app 的 Vite 配置文件。
// 只需要把官方插件挂载进来，其余编译逻辑由 uni-app 自动处理。
export default defineConfig({
  plugins: [uni()],
});

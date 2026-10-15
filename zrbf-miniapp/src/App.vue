<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';

// 小程序冷启动时执行一次：检查本地 Token，不存在则静默微信登录
onLaunch(async () => {
  const userStore = useUserStore();
  if (!userStore.isLogged) {
    try {
      await userStore.login();
    } catch (err) {
      // 登录失败不阻塞首页展示，后续接口 401 时会统一处理
      console.error('静默登录失败', err);
    }
  }
});
</script>

<style lang="scss">
// 全局样式：先引入设计 token，再引入基础重置
@import '@/styles/tokens.scss';
@import '@/styles/reset.scss';
</style>

<template>
  <view class="mine-page">
    <!-- 用户信息卡 -->
    <view class="profile-card">
      <image
        v-if="userStore.userInfo?.avatar"
        class="profile-card__avatar"
        :src="userStore.userInfo.avatar"
        mode="aspectFill"
      />
      <view v-else class="profile-card__avatar profile-card__avatar--placeholder">
        <text class="profile-card__avatar-text">{{ avatarText }}</text>
      </view>
      <view class="profile-card__info">
        <text class="profile-card__nickname">{{ userStore.userInfo?.nickname || '暴富用户' }}</text>
        <text class="profile-card__id">ID: {{ userStore.userInfo?.id || '-' }}</text>
      </view>
    </view>

    <!-- 累计统计 -->
    <view class="mine-page__section">
      <text class="mine-page__section-title">累计统计</text>
      <view class="mine-page__row">
        <StatCard label="累计投入" :value="`¥${formatMoney(profile?.totalInvestment)}`" />
        <StatCard label="累计收益" :value="`¥${formatMoney(profile?.totalProfit)}`" />
      </view>
      <view class="mine-page__row">
        <StatCard
          label="累计盈亏"
          :value="formatSignedMoney(profile?.totalLoss)"
          :trend="profile && profile.totalLoss > 0 ? 'up' : profile && profile.totalLoss < 0 ? 'down' : 'flat'"
        />
      </view>
    </view>

    <!-- 设置等入口（V1.0 仅展示） -->
    <view class="menu-card">
      <view class="menu-card__item">
        <text>设置</text>
        <text class="menu-card__arrow">›</text>
      </view>
      <view class="menu-card__item">
        <text>关于早日暴富</text>
        <text class="menu-card__arrow">›</text>
      </view>
    </view>

    <button class="btn btn--danger btn--block mine-page__logout" @tap="handleLogout">
      退出登录
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import type { UserProfileResponse } from '@/types/api';
import { formatMoney, formatSignedMoney } from '@/utils/format';
import StatCard from '@/components/StatCard/StatCard.vue';

const userStore = useUserStore();
const profile = ref<UserProfileResponse | null>(null);

const avatarText = computed(() => (userStore.userInfo?.nickname || '富').slice(0, 1));

// 每次进入我的页刷新一次用户资料
onShow(async () => {
  if (!userStore.isLogged) return;
  try {
    profile.value = await userStore.fetchProfile();
  } catch (err) {
    profile.value = null;
  }
});

// 退出登录：二次确认 + 触感反馈
const handleLogout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定退出登录？',
    confirmColor: '#FF4D4F',
    success: (res) => {
      if (res.confirm) {
        userStore.logout();
        uni.vibrateShort();
        uni.reLaunch({ url: '/pages/dashboard/index' });
      }
    },
  });
};
</script>

<style lang="scss" scoped>
.mine-page {
  padding: var(--spacing-md);

  &__section {
    margin-top: var(--spacing-lg);
  }

  &__section-title {
    display: block;
    margin-bottom: var(--spacing-md);
    font-size: var(--font-md);
    font-weight: var(--font-weight-medium);
  }

  &__row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
  }

  &__logout {
    margin-top: var(--spacing-xxl);
  }
}

.profile-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: var(--radius-full);
    overflow: hidden;

    &--placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--color-primary-light);
    }
  }

  &__avatar-text {
    font-size: var(--font-xl);
    color: var(--color-primary);
  }

  &__nickname {
    font-size: var(--font-lg);
    font-weight: var(--font-weight-semibold);
  }

  &__id {
    display: block;
    margin-top: var(--spacing-xs);
    font-size: var(--font-sm);
    color: var(--color-text-secondary);
  }
}

.menu-card {
  margin-top: var(--spacing-lg);
  padding: 0 var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);

  &__item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--spacing-md) 0;
    font-size: var(--font-base);
    border-bottom: 1rpx solid var(--color-border);

    &:last-child {
      border-bottom: none;
    }
  }

  &__arrow {
    font-size: var(--font-lg);
    color: var(--color-text-placeholder);
  }
}
</style>

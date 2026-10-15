# 早日暴富小程序前端

「早日暴富」是一个记录彩票投入与收益的微信小程序。前端采用 **uni-app + Vue 3 + TypeScript**，一套代码可以编译成微信小程序和 H5 网页。

## 技术栈

- uni-app + Vue 3（`<script setup>`）
- TypeScript
- Pinia（状态管理）
- SCSS + rpx（响应式样式）
- ECharts（趋势折线图）
- uni.request 封装（接口请求）

## 目录结构

```text
zrbf-miniapp/
├── src/
│   ├── api/          # 接口调用（user / lottery / statistics / dashboard / chart）
│   ├── components/   # 自研业务组件（统计卡、状态徽章、图表等）
│   ├── pages/        # 页面（首页、登记、记录、趋势、统计、我的）
│   ├── stores/       # Pinia 状态（用户、彩票记录、全局）
│   ├── styles/       # 设计 token、重置样式、mixin
│   ├── types/        # TypeScript 类型（与后端 DTO 对齐）
│   ├── utils/        # 工具函数（登录缓存、格式化、校验、常量）
│   ├── App.vue       # 应用入口（静默登录）
│   ├── main.ts       # 创建应用
│   ├── pages.json    # 页面路由与 TabBar
│   ├── manifest.json # 小程序配置
│   └── uni.scss      # 全局 SCSS 变量
├── .env.development  # 开发环境变量
├── .env.production   # 生产环境变量
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 如何运行

```bash
# 安装依赖（首次运行必须）
pnpm install

# 启动 H5 网页开发
pnpm dev:h5

# 启动微信小程序开发
pnpm dev:mp-weixin

# 构建 H5
pnpm build:h5

# 构建微信小程序
pnpm build:mp-weixin
```

微信小程序构建完成后，用微信开发者工具打开 `dist/build/mp-weixin` 目录即可预览。

## 环境变量

接口地址在 `.env.development` / `.env.production` 中配置：

```text
VITE_API_BASE_URL=http://localhost:8585
VITE_APP_ENV=development
```

本地联调时改成你自己的后端地址，例如 `http://localhost:8585`（注意微信开发者工具需要勾选“不校验合法域名”）。

## 接口规范

- 接口前缀：`/api/v1`
- 统一返回：`{ code, message, data }`
- `code=200` 表示成功，`401` 表示登录过期
- 请求头携带：`Authorization: Bearer <token>`

完整接口见项目根目录 [6.接口设计.md](../6.接口设计.md)。

## 当前完成度

- 已完成（前端）：工程骨架、设计系统、请求封装、登录流程、首页、登记、列表、详情、编辑、趋势、周/月/年统计、我的页
- 已完成（后端）：开发模式登录、JWT 签发与登录拦截、彩票记录增删改查与实时聚合、日/周/月/年统计、Dashboard、趋势图表接口
- 待补齐：真实微信 AppID/Secret 配置、前后端联调、部署上线

## 开发模式登录

后端 `application.yml` 默认 `wx.miniapp.mock-login: true`，未配置真实微信 AppID/Secret 时，登录接口不调用微信，直接使用固定开发用户 `dev_mock_openid`。配置真实 `WX_APPID` / `WX_SECRET` 并设置 `WX_MOCK_LOGIN=false` 后走真实微信登录。

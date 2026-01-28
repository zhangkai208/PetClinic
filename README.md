# PetClinic-UI 前端

## 一、项目简介

- **项目名称**：PetClinic-UI（宠物健康管理与服务预约系统前端）
- **技术栈**：Vite 7.x、Vue 3.5、Vue Router 4、Pinia、Axios、Element Plus、ECharts、Sass
- **目标角色**：宠物主人(OWNER)、服务商(PROVIDER)、管理员(ADMIN) 三种角色，分别对应不同的功能入口与权限。

---

## 二、项目结构

```
PetClinic-UI/src/
├── main.js                 # 应用入口
├── App.vue                 # 根组件
├── api/                    # API接口封装
│   ├── sysuser.js          # 用户相关接口
│   ├── pet.js              # 宠物相关接口
│   ├── healthRecord.js     # 健康记录接口
│   ├── appointment.js      # 预约接口（含评价）
│   ├── serviceProvider.js  # 服务商接口
│   ├── dashboard.js        # 数据看板接口
│   ├── chat.js             # AI对话接口
│   └── reminder.js         # 预约提醒接口
├── stores/                 # Pinia状态管理
│   ├── token.js            # Token存储
│   └── userinfo.js         # 用户信息存储
├── router/                 # 路由配置
│   └── index.js            # 路由定义与守卫
├── utils/                  # 工具类
│   └── request.js          # Axios封装
└── views/                  # 页面组件
    ├── Login.vue           # 登录/注册页
    ├── Profile.vue         # 个人中心
    ├── layout/
    │   └── Layout.vue      # 全局布局（侧边栏+顶部+内容区）
    ├── admin/
    │   ├── Dashboard.vue   # 数据看板 (管理员)
    │   └── Users.vue       # 用户管理 (管理员)
    ├── pet/
    │   ├── Pets.vue        # 宠物列表
    │   └── PetGallery.vue  # 宠物相册
    ├── health/
    │   └── HealthRecords.vue  # 健康记录
    ├── appointment/
    │   └── Appointments.vue   # 预约管理（含评价、付款）
    ├── service/
    │   └── ServiceProviders.vue  # 服务商管理
    ├── chat/
    │   └── AIChat.vue      # AI智能助手
    └── reminder/
        └── Reminders.vue   # 邮件通知管理
```

---

## 三、当前完成进度

### ✅ 已完成功能

| 模块 | 页面 | 状态 | 说明 |
|------|------|------|------|
| 用户认证 | Login.vue | ✅ 完成 | 登录/注册二合一，支持表单验证 |
| 个人中心 | Profile.vue | ✅ 完成 | 查看/修改个人信息、头像上传 |
| 全局布局 | Layout.vue | ✅ 完成 | 响应式侧边栏、面包屑、角色菜单控制 |
| 宠物管理 | Pets.vue | ✅ 完成 | 列表、搜索、筛选、CRUD、头像上传 |
| 宠物相册 | PetGallery.vue | ✅ 完成 | 宠物照片管理、画廊展示 |
| 健康记录 | HealthRecords.vue | ✅ 完成 | 疫苗/驱虫/用药/笔记记录管理 |
| 预约管理 | Appointments.vue | ✅ 完成 | 预约列表、状态管理、**扫码付款**、**服务评价** |
| 服务商管理 | ServiceProviders.vue | ✅ 完成 | 服务商列表、申请入驻、审核 |
| 数据看板 | Dashboard.vue | ✅ 完成 | ECharts图表、统计卡片 |
| 用户管理 | Users.vue | ✅ 完成 | 用户CRUD、角色管理(管理员) |
| AI助手 | AIChat.vue | ✅ 完成 | 流式对话、会话管理 |
| 邮件通知 | Reminders.vue | ✅ 完成 | **邮件发送记录、手动触发提醒(管理员)** |

### 🔐 权限控制

| 角色 | 可访问页面 |
|------|------------|
| 宠物主人 (1) | 宠物管理、宠物相册、健康记录、预约管理、邮件通知、AI助手、个人中心 |
| 服务商 (2) | 服务商管理、健康记录、预约管理、邮件通知、AI助手、个人中心 |
| 管理员 (3) | **数据看板**、用户管理、服务商管理、健康记录、预约管理、**邮件通知(含手动发送)**、AI助手、个人中心 |

---

## 四、路由配置

```javascript
// 主要路由结构
/login              → 登录页 (公开)
/                   → Layout布局
  /admin/dashboard  → 数据看板 (ADMIN)
  /admin/users      → 用户管理 (ADMIN)
  /pets             → 宠物列表 (OWNER, ADMIN)
  /gallery          → 宠物相册 (OWNER, ADMIN)
  /health-records   → 健康记录 (ALL)
  /appointments     → 预约管理 (ALL)
  /service-providers→ 服务商管理 (PROVIDER, ADMIN)
  /reminders        → 邮件通知 (ALL)
  /chat             → AI助手 (ALL)
  /profile          → 个人中心 (ALL)
```

**登录后默认跳转**：
- 管理员 → `/admin/dashboard`
- 服务商 → `/service-providers`
- 宠物主人 → `/pets`

---

## 五、技术特性

### 状态管理
- 使用 **Pinia** 管理 Token 和用户信息
- 使用 **pinia-persistedstate-plugin** 实现状态持久化

### 请求封装
- 基于 Axios 封装请求拦截器（自动携带 Token）
- 响应拦截器处理 401 未授权自动跳转登录

### UI组件
- 使用 **Element Plus** 组件库
- 图标使用 **@element-plus/icons-vue**

### 数据可视化
- 使用 **ECharts** 绑制数据看板图表
- 支持饼图、柱状图等可视化展示

---

## 六、新增功能模块（重点）

### 1. 扫码付款功能

预约管理页面集成扫码付款流程：

**流程说明**：
1. 宠物主人创建预约时，填写服务类型、预约时间、金额
2. 提交后弹出付款码弹窗，展示微信/支付宝二维码
3. 15秒倒计时自动确认付款并创建预约
4. 服务商/管理员创建预约无需付款流程

**技术实现**：
- `paymentDialogVisible` 控制付款弹窗
- `paymentCountdown` 倒计时计数器
- 双二维码展示（微信+支付宝）

### 2. 服务评价功能

预约完成后支持用户评价：

**功能特性**：
- 仅 **已完成** 状态的预约可评价
- 仅 **宠物主人** 和 **管理员** 可评价
- 已评价的预约不可重复评价
- 评价内容限制500字

**技术实现**：
- `addEvaluation` API 接口
- `evaluationDialogVisible` 评价弹窗
- `ChatDotRound` 图标作为评价按钮

### 3. 邮件通知功能（重点）

独立的邮件通知管理页面（`Reminders.vue`）：

**功能特性**：

| 功能 | 普通用户 | 管理员 |
|------|---------|--------|
| 查看提醒记录 | ✅ 仅自己的 | ✅ 全部 |
| 发送状态统计 | ❌ | ✅ |
| 手动触发发送 | ❌ | ✅ |
| 选择发送日期 | ❌ | ✅ |

**页面组成**：
- 📊 统计卡片（成功/失败/总计）
- 📬 邮件列表（卡片式展示）
- 🔍 详情弹窗（查看发送详情）
- 📤 发送弹窗（管理员手动触发）

**日期快捷选项**：
- 明天、后天、下周

---

## 七、启动方式

```bash
# 安装依赖
npm install

# 开发环境运行
npm run dev

# 生产环境打包
npm run build
```

**默认端口**：`http://localhost:5173`

---

## 八、环境配置

项目通过 Vite 环境变量管理 API 地址：

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境配置 |
| `.env.production` | 生产环境配置 |

在 `src/utils/request.js` 中使用：
```javascript
baseURL: import.meta.env.VITE_APP_BASE_API || 'http://localhost:8080/api'
```

---

## 九、开发规范

- **命名规范**：组件使用 PascalCase，文件夹使用 kebab-case
- **样式**：使用 SCSS，组件内样式使用 `scoped`
- **提交规范**：feat(模块): 功能描述 / fix(模块): 修复描述

---

## 十、论文素材

每完成一个模块：
1. 截取关键页面截图，放入 `docs/screenshots/`
2. 记录接口联调日志
3. 输出包括：系统架构图、前端路由图、主要页面截图等

## 一、项目简介

- **项目名称**：PetClinic-UI（宠物健康管理与服务预约系统前端）
- **技术栈**：Vite、Vue 3、Vue Router、Pinia、Axios、Element Plus、ECharts（计划）、dayjs、nprogress
- **目标角色**：宠物主人、服务商、管理员三种角色，分别对应不同的功能入口与权限。

---

## 二、当前进度概述

- 已完成：项目脚手架初始化（Vite + Vue 3）；Axios 基础封装（`src/utils/request.js`）。
- 待完成：路由/状态管理/组件库搭建、各业务页面实现、前后端联调、打包部署、论文截图素材收集。

---

## 三、目录与分层规划

| 目录 | 说明 |
| --- | --- |
| `src/api/` | 按模块封装请求：`auth.js`、`user.js`、`pet.js`、`health.js`、`provider.js`、`appointment.js`、`admin.js` |
| `src/stores/` | Pinia 仓库：`userStore`、`petStore`、`appointmentStore`、`notificationStore` 等 |
| `src/layouts/` | 全局布局：`BaseLayout.vue`（侧边栏+顶部+内容）、`AuthLayout.vue`（登录/注册） |
| `src/router/` | 路由与守卫：定义角色所需的菜单、路由、重定向策略 |
| `src/views/` | 各业务页面（见下方模块拆解） |
| `src/components/` | 通用组件（表单、表格、状态 Tag、上传组件、空状态等） |

---

## 四、模块级待办（与后端 8 大模块一一对应）

### 1. 用户与权限管理
- **页面**：`Login.vue`、`Register.vue`、`Profile.vue`、`ChangePassword.vue`
- **交互**：
  - 登录成功后写入 Pinia + LocalStorage，按角色跳转首页。
  - 注册时支持上传头像、填写基础资料。
  - 个人中心可修改昵称、联系方式、头像。
- **技术点**：路由守卫、Token 续期、401 全局拦截、角色菜单控制。

### 2. 宠物档案管理
- **页面**：`PetList.vue`、`PetForm.vue`、`PetDetail.vue`
- **功能**：
  - 列表支持搜索、排序、分页。
  - 头像上传可采用 Element Plus `Upload` 组件 + OSS/本地接口。
  - 详情页展示宠物基础信息 + 最近健康记录。

### 3. 健康记录与追踪
- **页面**：`HealthRecordList.vue`、`HealthRecordForm.vue`
- **功能**：
  - 以时间线/卡片形式展示疫苗、驱虫、用药、日常记录。
  - 支持按记录类型、时间范围筛选。
  - 高亮即将到期的提醒事项，接口联动“提醒与消息”模块。

### 4. 服务商与服务展示
- **页面**：`ProviderList.vue`、`ProviderDetail.vue`、`ProviderApply.vue`
- **功能**：
  - 列表按类型（医院/美容/寄养/训练）筛选，支持地图或地址展示（可选）。
  - 详情展示服务项目、价格、预约情况、评价。
  - 服务商入驻申请表单带上传资质证书、联系信息等。

### 5. 预约与订单管理
- **页面**：`AppointmentCreate.vue`、`AppointmentList.vue`、`AppointmentCalendar.vue`
- **功能**：
  - 创建预约流程化：选择宠物 → 选择服务商/服务 → 选择时间 → 填写备注。
  - 列表按状态（待确认、已预约、已完成、已取消）分组，提供操作按钮。
  - 日历视图展示服务商的排期，便于选择空闲时间段。

### 6. 提醒与消息
- **页面**：`NotificationList.vue`、顶部消息组件
- **功能**：
  - 展示系统消息、预约提醒、健康提醒。
  - 未读标记、批量已读、清空等交互。
  - 后续可接入 WebSocket/轮询保持实时性。

### 7. 支付与财务（可模拟）
- **页面**：`PaymentResult.vue`、`OrderList.vue`
- **功能**：
  - 模拟支付流程：提交订单 → 支付成功/失败页面。
  - 展示历史订单金额、支付状态，供服务商/管理员查看。

### 8. 后台管理（管理员视角）
- **页面**：`AdminDashboard.vue`、`AdminUserManage.vue`、`AdminProviderAudit.vue`、`AdminAnnouncement.vue`
- **功能**：
  - 统计看板（用户数、服务商数、预约数、收入等），使用 ECharts 绘制。
  - 用户管理：禁用/启用、角色切换。
  - 服务商审核：列表 + 审核通过/驳回弹窗。
  - 公告管理：富文本编辑器发布系统公告。

---

## 五、里程碑式开发节奏

| 周次 | 目标 | 前端交付 |
| --- | --- | --- |
| Week1 | 基建搭建 | 引入 Element Plus、Pinia、全局布局、路由守卫、登录/注册 UI |
| Week2 | 用户 + 宠物 | 完成用户中心、宠物列表/表单/详情、与后端初次联调 |
| Week3 | 健康 + 服务商 | 完成健康记录页面、服务商列表/详情/申请 |
| Week4 | 预约 + 消息 | 搭建预约流程、预约日历、消息提醒模块 |
| Week5 | 后台 + 可视化 | 实现管理员看板、用户/服务商管理、ECharts 数据展示 |
| Week6 | 联调 + 优化 | 全量联调、UI 细节打磨、打包部署脚本、论文截图收集 |

---

## 六、工程与质量保障

- **规范**：配置 ESLint + Prettier；统一组件命名和目录命名；在 Git 提交信息中注明模块。
- **环境**：`.env.development`、`.env.production` 管理 API 地址；通过 `VITE_APP_BASE_API` 注入 Axios。
- **测试**：使用 Vitest + @vue/test-utils 对核心组件做烟雾测试；编写关键页面的 e2e 用例（可选）。
- **性能**：启用路由懒加载、组件按需引入；对大表格使用虚拟滚动（可选）。

---

## 七、联调与论文素材采集

- 每完成一个模块：
  1. 与后端接口联调，使用 Apifox/Postman 记录接口示例。
  2. 截取关键页面截图（列表、表单、统计图），放入 `docs/screenshots/`。
  3. 在 README 中追加开发日志，记录日期、完成内容、问题总结。
- 最终输出包括：系统架构图、前端路由图、主要页面截图、性能指标表等，直接用于论文章节。*** End Patch

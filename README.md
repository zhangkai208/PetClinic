## 一、项目简介（后端）

- **项目名称**：基于 Spring Boot 的宠物健康管理与服务预约系统（后端）
- **技术栈**：Spring Boot、Spring MVC、Spring Security + JWT、MyBatis-Plus、MySQL、Redis、ECache、Lombok、JUnit
- **整体目标**：围绕任务书中的 8 个核心模块（用户与权限、宠物档案、健康记录、预约、服务商与订单、提醒消息、支付结算、后台管理），提供稳定的 RESTful API，为 Vue 前端和后续论文撰写提供支撑。

## 二、当前进度概述

- **基础结构**：
  - `PetClinicApplication` 可正常启动。
  - 已集成 MyBatis-Plus（`MybatisPlusConfig`）、Redis（`RedisConfig`）、统一返回封装 `Result`。
- **领域模型**：
  - 已根据任务书/项目搭建文档创建核心实体：`SysUser`、`Pet`、`ServiceProvider`、`Appointment`、`HealthRecord`。
  - 已生成对应 `Mapper` 接口、`Service` 接口及 `ServiceImpl`，实现了基础 CRUD 能力。
- **缺失部分（需要补齐）**：
  - 控制层 Controller、DTO/VO、统一异常处理和参数校验。
  - 权限认证、安全配置（Spring Security + JWT）。
  - 具体业务规则（预约冲突校验、入驻审核、提醒推送等）。
  - 单元测试、接口文档、部署脚本和论文需要的图表资料。

---

## 三、按模块要做的事情（对应任务书 8 大模块）

### 1. 用户与权限管理模块（SysUser）

- **数据层**：
  - 补充用户角色枚举（主人/服务商/管理员）、账号状态枚举，避免 Magic Number。
  - 增加密码加密字段说明（BCrypt 等）和登录日志表（可选）。
- **业务层**：
  - 实现 `SysUserService` 中的注册、登录、修改资料、修改密码等业务方法。
  - 编写用户查重逻辑（用户名/手机号/邮箱唯一），封装统一业务异常。
- **控制层**：
  - 创建 `SysUserController`：
    - `POST /auth/register`：注册接口。
    - `POST /auth/login`：登录获取 JWT。
    - `GET /user/profile`：获取当前登录用户信息。
    - `PUT /user/profile`：修改个人信息。
- **安全与权限**：
  - 集成 Spring Security + JWT：
    - 登录签发 Token、解析 Token、刷新 Token。
    - 按 `role_type` 控制不同接口访问权限。
  - 整理一张“权限-接口映射表”，用于论文中说明 RBAC 设计。

### 2. 宠物档案管理模块（Pet）

- **数据与业务**：
  - 完善宠物信息字段的校验规则（名称必填、体重范围、生日不能晚于当前日期等）。
  - 设计宠物与主人（`owner_id`）的绑定逻辑，禁止越权访问他人宠物信息。
  - 支持宠物头像上传（本地路径/OSS 占位实现），并在数据库中保存访问 URL。
- **接口设计**（`PetController`）：
  - `GET /pets`：按主人分页查询宠物列表，可筛选类型、品种。
  - `GET /pets/{id}`：查看宠物详情。
  - `POST /pets`：新增宠物档案。
  - `PUT /pets/{id}`：修改宠物信息。
  - `DELETE /pets/{id}`：删除/软删除宠物档案。
- **论文素材**：
  - 绘制“宠物档案管理”流程图（添加/修改/删除）。
  - 统计接口 QPS 与响应时间，为性能章节准备数据。

### 3. 健康记录与追踪模块（HealthRecord）

- **业务规则**：
  - 根据 `record_type` 区分疫苗、驱虫、用药、健康笔记，不同类型校验不同字段。
  - 计算 `next_date`（下次提醒日期），为提醒模块提供数据。
- **接口设计**（`HealthRecordController`）：
  - `GET /health-records`：按宠物分页查询健康记录，可按类型、日期筛选。
  - `POST /health-records`：新增记录。
  - `PUT /health-records/{id}`：编辑记录。
  - `DELETE /health-records/{id}`：删除记录。
- **提醒对接**：
  - 提供查询“即将到期的疫苗/驱虫记录”的接口，供定时任务或消息模块调用。

### 4. 服务商与服务管理模块（ServiceProvider）

- **入驻与审核**：
  - `ServiceProviderService` 中实现服务商申请、资料修改、审核通过/驳回逻辑。
  - 设计服务商状态流转：待审核 → 已通过 → 已拒绝/已冻结。
- **接口设计**（`ServiceProviderController`）：
  - 服务商申请、修改、查看详情、管理员审核接口。
  - 服务商列表接口（支持按类型、地区、评分排序）。
- **订单衔接**：
  - 为后续订单/预约模块预留字段（服务项目、价格等，可先用简化版本）。

### 5. 预约与订单模块（Appointment）

- **预约规则**：
  - 防止同一时间段重复预约同一服务商。
  - 根据宠物、服务商状态判定是否允许预约。
  - 维护预约状态机：待确认 → 已预约 → 已完成/已取消。
- **接口设计**（`AppointmentController`）：
  - 创建预约、取消预约、确认完成、查询预约列表（主人/服务商视角）、查看详情。
  - 提供日历视图数据接口（按日期返回预约情况）。
- **订单扩展（选做）**：
  - 预留订单表、支付状态字段，为后续支付集成模块使用。

### 6. 提醒与消息模块

- **数据来源**：
  - 基于 `HealthRecord.next_date`、`Appointment` 时间字段生成提醒任务。
- **实现方案**：
  - 使用 Spring 定时任务轮询，或使用消息队列（如果时间允许）实现异步提醒。
  - 提供消息查询接口，供前端展示“我的消息”“系统公告”。

### 7. 支付与财务管理模块（可简化为模拟）

- **模拟支付流程**：
  - 定义支付订单实体（金额、支付状态、支付时间等）。
  - 提供“创建订单”“模拟支付成功”“查询订单”接口。
- **结算与统计**：
  - 统计服务商收入、订单数量，为后台看板提供数据。

### 8. 系统后台管理模块

- **管理员能力**：
  - 用户管理：禁用/启用用户、重置密码等。
  - 服务商管理：审核、上下线、查看统计。
  - 内容管理：公告、健康知识库等。
- **接口**：
  - 提供角色为管理员时可访问的后台接口集合，统一使用 `/admin/**` 前缀，便于在 Security 中配置。

---

## 四、按时间推进的开发节奏（可直接放进论文“进度安排”）

- **第 1 周**：补齐基础设施（异常处理、DTO/VO、日志、多环境配置）+ 用户注册登录 + JWT。
- **第 2 周**：完成用户与权限管理、宠物档案模块接口，实现基本增删改查和权限控制。
- **第 3 周**：完成健康记录模块 + 服务商入驻与审核模块，联调前端相关页面。
- **第 4 周**：实现预约模块（含状态机）+ 简单服务评价接口，完善订单数据结构。
- **第 5 周**：接入 Redis/ECache 缓存、优化查询性能，补充单元测试和接口文档。
- **第 6 周**：整理部署脚本、完成系统联调与压力测试，输出所有论文所需的图表和截图。

> 要求：每完成一个模块，至少做三件事：**代码实现 + 接口文档 + 截图/图表**，方便后期直接写入毕业论文。*** End Patch

完整实现步骤总结

确认问题原因
测试域名（如 xxx.bkt.clouddn.com）超过30天自动回收或流量超限，导致外链失效。
决定使用自定义域名
选择已备案域名 zhangkairedzack.top（你买的 .top 域名）。
在七牛云绑定自定义域名
登录七牛控制台 → 对象存储 → 域名管理
添加域名 zhangkairedzack.top
触发域名所有权验证（需要添加 TXT 记录）

发现 DNS 托管在 Cloudflare
原来域名 NS 已指向 Cloudflare（nick.ns.cloudflare.com 和 daphne.ns.cloudflare.com）
因此所有 DNS 操作必须在 Cloudflare 完成，阿里云解析无效

完成域名所有权验证（TXT 记录）
从七牛验证页面复制最新 TXT 值（verify_ 开头的长字符串）
登录 Cloudflare → 选择域名 → DNS → Records
添加记录：
Type: TXT
Name: verification
Content: 七牛给的完整验证串
Proxy status: DNS only（灰云）
保存 → 等待 1~5 分钟 → 回七牛点击“点此验证” → 通过（绿勾）

配置 CNAME 记录（核心步骤）
在七牛“如何配置 CNAME”页面获取 CNAME 值（zhangkairedzack-top-idvqy1m.qiniudns.com）
回到 Cloudflare DNS → 添加记录：
Type: CNAME
Name: @（使用主域名 zhangkairedzack.top）
Target: zhangkairedzack-top-idvqy1m.qiniudns.com
Proxy status: DNS only（灰云，必须！）
TTL: Auto
保存 → 等待 5~15 分钟生效

七牛侧设置外链默认域名
去空间（pet-clinic） → 文件管理
右上角“外链默认域名”下拉选择 zhangkairedzack.top → 保存

验证成功
直接浏览器访问 http://zhangkairedzack.top/001.png → 图片正常显示
七牛控制台文件列表外链也变成你的自定义域名
（状态显示“未配置”是七牛后台缓存延迟，不影响实际使用，后续会自动变绿）
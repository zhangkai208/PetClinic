# PetClinic 后端

## 一、项目简介

- **项目名称**：基于 Spring Boot 的宠物健康管理与服务预约系统（后端）
- **技术栈**：Spring Boot 3.x、Spring MVC、Spring Security + JWT、MyBatis-Plus、MySQL 8.0、Redis、Spring AI、七牛云OSS、Lombok
- **整体目标**：围绕任务书中的核心模块，提供稳定的 RESTful API，为 Vue 前端和后续论文撰写提供支撑。

---

## 二、项目结构

```
PetClinic/src/main/java/com/zk/petclinic/
├── PetClinicApplication.java    # 启动类
├── config/                      # 配置类
│   ├── SecurityConfig.java      # Spring Security配置
│   ├── MybatisPlusConfig.java   # MyBatis-Plus配置
│   ├── RedisConfig.java         # Redis配置
│   └── AIConfiguration.java     # Spring AI配置
├── controller/                  # 控制器层
│   ├── SysUserController.java   # 用户管理
│   ├── PetController.java       # 宠物管理
│   ├── HealthRecordController.java    # 健康记录
│   ├── AppointmentController.java     # 预约管理（含评价、付款）
│   ├── AppointmentReminderController.java # 预约提醒管理
│   ├── ServiceProviderController.java # 服务商管理
│   ├── DashboardController.java       # 数据看板统计
│   ├── ChatConversationController.java # AI对话会话
│   └── ChatMessageController.java      # AI对话消息
├── scheduler/                   # 定时任务
│   └── AppointmentReminderScheduler.java # 预约提醒定时调度
├── service/                     # 服务层
│   ├── impl/                    # 服务实现
│   └── ...Service.java          # 服务接口
├── mapper/                      # MyBatis Mapper
├── domain/                      # 实体类
│   ├── SysUser.java
│   ├── Pet.java
│   ├── HealthRecord.java
│   ├── Appointment.java         # 含评价(evaluation)、金额(money)字段
│   ├── AppointmentReminder.java # 预约提醒记录
│   ├── ServiceProvider.java
│   ├── ChatConversation.java
│   ├── ChatMessage.java
│   └── dto/                     # 数据传输对象
├── enums/                       # 枚举类
│   ├── SysUserRoleType.java     # 用户角色枚举
│   ├── Petgender.java           # 宠物性别枚举
│   ├── ServiceProviderStatus.java  # 服务商状态枚举
│   └── ServiceProviderType.java    # 服务商类型枚举
├── exception/                   # 异常处理
│   ├── GlobalExceptionHandler.java  # 全局异常处理器
│   └── BusinessException.java       # 自定义业务异常
├── security/                    # 安全相关
│   └── JwtAuthenticationFilter.java # JWT过滤器
├── interceptor/                 # 拦截器
└── util/                        # 工具类
    ├── JWTUtil.java
    ├── RedisUtil.java
    ├── ResultUtil.java          # 统一响应封装
    ├── QiniuOssUtil.java        # 七牛云上传工具
    └── ThreadLocalUtil.java
```

---

## 三、当前完成进度

### ✅ 已完成模块

| 模块 | 状态 | 说明 |
|------|------|------|
| 用户与权限管理 | ✅ 完成 | 注册、登录、JWT认证、多角色权限控制(OWNER/PROVIDER/ADMIN) |
| 宠物档案管理 | ✅ 完成 | CRUD、头像/相册上传、按性别筛选 |
| 健康记录管理 | ✅ 完成 | 疫苗/驱虫/用药/笔记记录，权限校验 |
| 服务商管理 | ✅ 完成 | 入驻申请、审核、状态管理 |
| 预约管理 | ✅ 完成 | 创建/取消预约、状态管理、**服务评价**、**预约金额** |
| 邮件提醒推送 | ✅ 完成 | **QQ邮箱SMTP**、定时任务(每日9点)、双向通知(宠物主人+服务商) |
| 管理员看板 | ✅ 完成 | 统计卡片、趋势图表、分布图表 |
| AI智能助手 | ✅ 完成 | 基于Spring AI，支持流式对话 |
| 全局异常处理 | ✅ 完成 | 多类型异常精细化处理 |

---

## 四、核心接口清单

### 1. 用户模块 `/sysUser`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/sysUser/register` | 用户注册 | 公开 |
| POST | `/sysUser/login` | 用户登录 | 公开 |
| POST | `/sysUser/logout` | 退出登录 | 已登录 |
| GET | `/sysUser/{id}` | 获取用户信息 | 已登录 |
| PUT | `/sysUser/{id}` | 更新用户信息 | 已登录 |
| GET | `/sysUser/page` | 分页查询用户 | ADMIN |
| POST | `/sysUser/upload` | 上传头像 | 已登录 |

### 2. 宠物模块 `/pet`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/pet/page` | 分页查询我的宠物 | OWNER/ADMIN |
| GET | `/pet/list` | 获取所有宠物 | PROVIDER/ADMIN |
| POST | `/pet/create` | 新增宠物 | OWNER/ADMIN |
| PUT | `/pet/{id}` | 更新宠物 | OWNER/ADMIN |
| DELETE | `/pet/{id}` | 删除宠物 | OWNER/ADMIN |
| POST | `/pet/upload` | 上传宠物头像 | OWNER/ADMIN |
| POST | `/pet/uploadPhotos` | 上传相册照片 | OWNER/ADMIN |
| GET | `/pet/gender/{gender}` | 按性别筛选 | OWNER/ADMIN |

### 3. 健康记录模块 `/healthRecord`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/healthRecord/page` | 分页查询健康记录 | OWNER/PROVIDER/ADMIN |
| POST | `/healthRecord/create` | 新增健康记录 | PROVIDER/ADMIN |
| PUT | `/healthRecord/{id}` | 更新健康记录 | PROVIDER/ADMIN |
| DELETE | `/healthRecord/delete` | 批量删除记录 | PROVIDER/ADMIN |

### 4. 预约模块 `/appointment`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/appointment/page` | 分页查询预约 | OWNER/PROVIDER/ADMIN |
| POST | `/appointment/create` | 创建预约（含金额） | OWNER/PROVIDER/ADMIN |
| PUT | `/appointment/{id}` | 更新预约状态 | OWNER/PROVIDER/ADMIN |
| DELETE | `/appointment/delete` | 删除预约 | OWNER/PROVIDER/ADMIN |
| PUT | `/appointment/evaluation` | **添加服务评价** | OWNER/PROVIDER/ADMIN |

### 5. 服务商模块 `/serviceProviders`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/serviceProviders/page` | 分页查询服务商 | OWNER/PROVIDER/ADMIN |
| GET | `/serviceProviders/list` | 服务商列表 | OWNER/PROVIDER/ADMIN |
| POST | `/serviceProviders/create` | 申请入驻 | PROVIDER/ADMIN |
| PUT | `/serviceProviders/{id}` | 更新服务商信息 | PROVIDER/ADMIN |

### 6. 管理员看板 `/admin/dashboard`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/admin/dashboard/stats` | 获取统计数据 | ADMIN |
| GET | `/admin/dashboard/pet-type-distribution` | 宠物类型分布 | ADMIN |
| GET | `/admin/dashboard/appointment-trend` | 预约趋势 | ADMIN |
| GET | `/admin/dashboard/user-role-distribution` | 用户角色分布 | ADMIN |

### 7. AI对话模块
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/ChatConversation/list` | 获取对话列表 | 已登录 |
| POST | `/ChatConversation/create` | 创建对话 | 已登录 |
| GET | `/chatMessage/stream` | 流式AI对话 | 已登录 |

### 8. 预约提醒模块 `/appointment-reminder`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/appointment-reminder/reminder` | 手动触发预约提醒 | ADMIN |
| GET | `/appointment-reminder/list` | 获取所有提醒记录 | ADMIN |
| GET | `/appointment-reminder/page` | 分页查询我的提醒记录 | 已登录 |

---

## 五、权限角色说明

| 角色 | roleType | 权限范围 |
|------|----------|----------|
| 宠物主人 | 1 (OWNER) | 管理自己的宠物、查看健康记录、创建预约 |
| 服务商 | 2 (PROVIDER) | 查看所有宠物、管理健康记录、处理预约、管理服务商信息 |
| 管理员 | 3 (ADMIN) | 全平台管理权限、数据看板、用户管理 |

---

## 六、QQ邮箱定时推送功能（重点）

### 功能概述

系统实现了基于 **Spring Boot Mail + QQ邮箱SMTP** 的预约提醒功能，通过定时任务在每天早上9点自动发送次日预约的邮件提醒。

### 技术实现

#### 1. 核心组件

| 组件 | 文件 | 说明 |
|------|------|------|
| 定时调度器 | `AppointmentReminderScheduler.java` | `@Scheduled(cron = "0 0 9 * * ?")` 每日9点执行 |
| 邮件服务 | `EmailServiceImpl.java` | 封装 `JavaMailSender`，发送简单文本邮件 |
| 业务逻辑 | `AppointmentReminderServiceImpl.java` | 查询预约、构建邮件内容、记录发送结果 |
| 提醒记录 | `AppointmentReminder.java` | 存储发送记录，防止重复发送 |

#### 2. 业务流程

```
定时任务触发 (每日9:00)
    ↓
查询次日有效预约（待确认/已预约状态）
    ↓
遍历每个预约
    ├── 发送给宠物主人（提醒赴约）
    └── 发送给服务商（提醒接待）
    ↓
记录发送结果到数据库
    ↓
返回成功发送数量
```

#### 3. 邮件内容示例

**宠物主人收到的邮件：**
```
亲爱的 张三：

您好！温馨提醒您，您的宠物明天有一个预约服务：

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🐾 宠物名字：小白
📅 预约时间：2026年01月25日 10:00
🏥 服务类型：疫苗接种
👨‍⚕️ 服务商：爱宠宠物医院
💰 预约金额：200 元
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

请提前做好准备，按时赴约哦！

—— 宠物健康管理系统
```

**服务商收到的邮件：**
```
尊敬的 爱宠宠物医院：

您好！温馨提醒您，明天有一位客户预约了您的服务：

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
👤 客户姓名：张三
📞 联系电话：13800138000
🐾 宠物名字：小白
🐕 宠物类型：狗
📅 预约时间：2026年01月25日 10:00
🏥 服务类型：疫苗接种
💰 预约金额：200 元
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

请提前做好准备，为客户提供优质服务！

—— 宠物健康管理系统
```

#### 4. 配置说明

在 `application.yml` 中配置 QQ 邮箱 SMTP：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-authorization-code  # QQ邮箱授权码（非登录密码）
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true

# 自定义配置
app:
  mail:
    reminder:
      enabled: true                    # 是否启用提醒功能
      sender-name: 宠物健康管理系统     # 发件人显示名称
```

> **注意**：QQ邮箱需要在设置中开启SMTP服务，并生成授权码。

#### 5. 防重复发送机制

- 每次发送前检查 `appointment_reminder` 表
- 按 `appointment_id + user_id` 判断是否已发送
- 避免同一预约对同一用户重复发送

#### 6. 手动触发测试

可通过 API 接口手动触发提醒（用于测试）：

```bash
# 发送明天的预约提醒
GET /api/appointment-reminder/reminder

# 发送指定日期的预约提醒
GET /api/appointment-reminder/reminder?date=2026-01-25
```

---

## 七、服务评价与付款功能

### 预约金额

- 预约表新增 `money` 字段（Long类型）
- 创建预约时可设置服务金额
- 金额会在邮件提醒中展示

### 服务评价

- 预约表新增 `evaluation` 字段（String类型）
- 服务完成后，用户可对服务进行评价
- 接口：`PUT /api/appointment/evaluation?id={预约ID}`

---

## 八、启动方式

```bash
# 1. 确保 MySQL 和 Redis 已启动
# 2. 修改 application.yml 中的数据库配置
# 3. 运行项目
mvn spring-boot:run

# 或使用 IDE 直接运行 PetClinicApplication.java
```

---

## 附录：七牛云OSS配置

项目使用七牛云对象存储保存图片文件（宠物头像、相册等）。

**配置步骤**：
1. 在七牛云创建存储空间（Bucket）
2. 绑定自定义域名并完成DNS验证（CNAME记录）
3. 在 `QiniuOssUtil.java` 中配置 AccessKey、SecretKey 和域名
4. 确保域名在 Cloudflare 等DNS服务商设置为 DNS Only 模式

> 详细步骤：控制台 → 对象存储 → 域名管理 → 添加域名 → 配置CNAME → 设置外链默认域名

---

## 九、宝塔面板部署指南

### 1. 服务器环境准备

在宝塔面板安装以下软件：
- **Nginx** 1.22+
- **MySQL** 8.0+
- **Java项目一键部署** 插件（软件商店搜索）
- **Redis**（可选，如果使用缓存）

### 2. 数据库配置

1. 宝塔 → **数据库** → **添加数据库**
2. 数据库名：`petclinic`
3. 用户名/密码：自定义
4. 导入 `petclinic.sql` 文件

### 3. 后端部署

#### 3.1 修改配置文件

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/petclinic?useSSL=false&serverTimezone=Asia/Shanghai
    username: 你的数据库用户名
    password: 你的数据库密码
```

#### 3.2 打包项目

```bash
mvn clean package -DskipTests
```

生成的 jar 包在 `target/` 目录下。

#### 3.3 宝塔部署 Java 项目

1. 宝塔 → **网站** → **Java项目** → **添加Java项目**
2. 项目路径：上传 jar 包的目录
3. 项目端口：`8080`
4. 启动后查看日志确认：`Started PetClinicApplication`

### 4. 前端部署

#### 4.1 打包前端

```bash
cd PetClinic-UI
npm run build
```

#### 4.2 上传 dist 文件

将 `dist/` 文件夹内容上传到服务器，**保持目录结构**：

```
/www/wwwroot/petclinic-frontend/
├── index.html
├── favicon.ico
└── assets/           ← 必须有这个文件夹
    ├── index-xxx.js
    └── index-xxx.css
```

### 5. Nginx 配置

在 Java 项目的 **nginx配置文件** 中添加：

```nginx
server {
    listen 80;
    server_name 你的域名或IP;
    root /www/wwwroot/petclinic-frontend;
    index index.html;

    # API 反向代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Vue SPA 路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### 6. 防火墙配置

确保以下端口已放行：

| 端口 | 用途 |
|------|------|
| 80 | HTTP 访问 |
| 443 | HTTPS 访问 |
| 8080 | 后端 API（可选，内网访问） |
| 3306 | MySQL（建议仅内网） |

**阿里云安全组** 也需要放行对应端口。

### 7. 域名配置（可选）

#### 7.1 域名解析

在域名服务商添加 A 记录：

| 主机记录 | 记录类型 | 记录值 |
|---------|---------|--------|
| @ | A | 服务器IP |
| www | A | 服务器IP |

#### 7.2 备案说明

> ⚠️ **注意**：使用中国大陆服务器，域名必须完成 ICP 备案才能通过 80/443 端口访问。
> 
> **临时方案**：使用非标准端口（如 8081）可绕过备案限制，访问地址改为 `http://域名:8081`

### 8. 常见问题

| 问题 | 解决方案 |
|------|---------|
| 页面白屏 | 检查 `assets/` 目录结构是否正确 |
| 接口 404 | 检查 Nginx 反向代理配置 |
| 接口 502 | 检查后端 Java 项目是否正常运行 |
| JS 加载失败 MIME 错误 | 确保 JS/CSS 文件在 `assets/` 子目录下 |
| 域名无法访问 | 检查域名解析、备案状态 |
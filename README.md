# 快递帮 - 校园快递代取微服务系统

<div align="center">
  <img src="https://neeko-copilot.bytedance.net/api/text_to_image?prompt=minimalist%20express%20delivery%20app%20icon%20with%20package%20and%20arrow%20blue%20theme&image_size=square" alt="快递帮 Logo" width="120" height="120">
  <h2>校园快递代取微服务平台</h2>
  <p>基于 Spring Cloud Alibaba 的分布式微服务架构，让同学之间互帮互助，高效解决快递代取需求</p>
</div>

---

## 🌟 项目简介

「快递帮」是一款专为校园场景设计的微服务系统，采用 **Monolithic → Microservice 演进架构**，旨在解决学生取快递难、排队久的问题。通过发布快递代取任务并提供悬赏激励，让同学之间互帮互助，打造高效便捷的校园快递代取生态。

### 架构演进历程

| 阶段 | 架构模式 | 特点 |
|------|---------|------|
| V1.0 | 单体应用 (Monolithic) | 所有功能集中在一个 Spring Boot 应用中 |
| V2.0 | 微服务 (Microservice) | 拆分为用户服务、订单服务、站点服务，通过 Nacos 实现服务注册与发现 |

---

## 🛠️ 技术选型

### 后端技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **框架** | Spring Boot | 3.2.x | 微服务基础框架 |
| **服务治理** | Spring Cloud Alibaba Nacos | 2023.0.1.0 | 服务注册与发现、配置中心 |
| **服务通信** | Spring Cloud OpenFeign | 4.1.x | 声明式 REST 客户端 |
| **ORM框架** | MyBatis-Plus | 3.5.x | 高效数据访问层 |
| **数据库** | MySQL | 8.0+ | 关系型数据库 |
| **API文档** | Knife4j | 4.4.x | Swagger UI 增强版 |
| **文件上传** | Spring Multipart | - | 文件上传处理 |

### 前端技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **框架** | 微信小程序原生框架 | 3.16.x | 官方小程序开发框架 |
| **语言** | JavaScript (ES6+) | - | 小程序开发语言 |
| **UI设计** | 自定义样式 | - | 响应式布局 |

---

## 🏗️ 微服务拆分说明

### 服务架构图

```
                          ┌─────────────────┐
                          │   Nacos Server  │
                          │   (8848端口)    │
                          │  服务注册中心   │
                          └────────┬────────┘
                                   │
          ┌────────────────────────┼────────────────────────┐
          │                        │                        │
          ▼                        ▼                        ▼
   ┌─────────────┐         ┌─────────────┐         ┌─────────────┐
   │user-service │         │order-service│         │station-service│
   │  (8081端口) │         │  (8082端口) │         │  (8083端口)  │
   └──────┬──────┘         └──────┬──────┘         └──────┬──────┘
          │                       │                       │
          │  用户管理              │  订单管理              │  站点管理
          │  微信授权登录          │  任务发布/接单          │  快递点管理
          │  用户信息维护          │  确认送达/收货          │  站点配置
          │                       │  图片上传存储          │
          └───────────────────────┴───────────────────────┘
                                       │
                                       ▼
                              ┌─────────────┐
                              │   MySQL     │
                              │   数据库    │
                              └─────────────┘
```

### 服务详细说明

#### user-service（用户服务）

**端口**: `8081`

**职责**:
- 用户注册与登录（微信授权）
- 用户信息管理（昵称、宿舍楼、微信号）
- 用户身份认证
- 提供用户信息查询接口供其他服务调用

**核心表**:
- `users` - 用户信息表

#### order-service（订单服务）

**端口**: `8082`

**职责**:
- 订单（任务）发布
- 任务接单
- 确认送达（含照片上传）
- 确认收货
- 订单状态流转管理
- 评价系统

**核心表**:
- `orders` - 订单信息表
- `reviews` - 评价信息表

**静态资源**:
- `/uploads/delivery_photos/` - 送达照片存储目录

#### station-service（站点服务）

**端口**: `8083`

**职责**:
- 快递站点管理
- 站点信息维护
- 站点查询接口

**核心表**:
- `stations` - 站点信息表

---

## 📡 服务注册与健康检查配置

### Nacos 注册中心配置

Nacos Server 以 **Standalone 模式**运行，端口 `8848`。

#### 启动命令

```bash
# Windows
cd backend/nacos/nacos/bin
startup.cmd -m standalone

# Linux/Mac
cd backend/nacos/nacos/bin
./startup.sh -m standalone
```

#### 工作机制

1. **服务注册**：每个微服务启动时，自动向 Nacos Server 注册自身实例信息（IP、端口、服务名）

2. **心跳检测**：
   - 每个服务实例每隔 **20 秒**向 Nacos Server 发送心跳包
   - Nacos Server 检测到实例连续 3 次（60 秒）未发送心跳，则将其标记为不健康状态
   - 不健康实例自动从服务列表中剔除，客户端调用时不会路由到该实例

3. **动态实例感知**：
   - Nacos Server 维护服务的实例列表
   - OpenFeign 客户端通过 Ribbon 实现负载均衡
   - 实例变化时，客户端自动感知并更新本地缓存

#### 服务配置示例（application.yml）

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        service: ${spring.application.name}
        heartbeat-interval: 20000  # 心跳间隔 20秒
        ip: ${spring.cloud.client.ip-address}
        port: ${server.port}
```

---

## 🔌 核心 API 契约文档

基于 Knife4j 自动生成的 API 文档，访问地址：`http://localhost:8082/doc.html`

### 订单服务 API

#### 1. 创建订单

**POST** `/api/orders`

**请求体**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `expressPoint` | String | 是 | 快递点名称 |
| `size` | String | 是 | 包裹大小（small/medium/large） |
| `reward` | BigDecimal | 是 | 悬赏金额 |
| `pickupCode` | String | 是 | 取件码 |
| `dormBuilding` | String | 是 | 目标宿舍楼 |
| `showWechat` | Boolean | 否 | 是否显示微信号 |
| `wechat` | String | 否 | 微信号 |
| `openid` | String | 是 | 发布者 openid |

**响应**:

```json
{
  "success": true,
  "message": "订单创建成功",
  "data": {
    "_id": 1,
    "expressPoint": "菜鸟驿站",
    "status": "pending",
    "createTime": "2026-06-16T10:00:00"
  }
}
```

#### 2. 确认送达 / 上传回执

**POST** `/api/orders/{id}/delivery`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | Long | 是 | 订单 ID（路径参数） |
| `file` | MultipartFile | 是 | 送达照片文件 |
| `openid` | String | 是 | 接单人 openid（form-data） |

**响应**:

```json
{
  "success": true,
  "message": "送达成功，请等待对方确认",
  "data": {
    "_id": 1,
    "status": "pending_confirm",
    "deliveryPhoto": "/uploads/delivery_photos/delivery_1_1718512800000.jpg"
  }
}
```

#### 3. 接单

**POST** `/api/orders/accept`

**请求体**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `orderId` | Long | 是 | 订单 ID |
| `openid` | String | 是 | 接单人 openid |

**响应**:

```json
{
  "success": true,
  "message": "接单成功",
  "data": {
    "_id": 1,
    "status": "claimed",
    "receiverOpenid": "test_openid_2"
  }
}
```

### 订单状态流转

```
pending（待接单）→ claimed（已接单）→ pending_confirm（待确认）→ completed（已完成）
     ↑                                      |
     └───────────────────────────────────────┘
                      取消/超时
```

---

## ⚡ 服务优雅降级机制

### deliveryPhoto 字段空值安全拦截

为避免跨服务传输大对象（如照片文件）导致的系统雪崩，系统采用以下降级策略：

#### 1. 前端空值安全拦截

在 `pages/profile/profile.js` 中，`formatDeliveryPhoto` 函数对 `deliveryPhoto` 字段进行多层安全校验：

```javascript
formatDeliveryPhoto(photoUrl) {
  // 第一层：空值检查
  if (!photoUrl) {
    return null
  }
  
  // 第二层：无效占位符过滤
  const invalidPlaceholders = ['photo_url', 'test.jpg', '']
  if (invalidPlaceholders.includes(photoUrl)) {
    return null
  }
  
  // 第三层：协议安全检查（微信临时路径直接返回）
  if (photoUrl.startsWith('wxfile://') || photoUrl.startsWith('http://tmp/')) {
    return photoUrl
  }
  
  // 第四层：路径补全
  if (!photoUrl.startsWith('http')) {
    return 'http://localhost:8082' + photoUrl
  }
  
  return photoUrl
}
```

#### 2. 标准网络静态对象降级

当 `deliveryPhoto` 字段为空或无效时，前端自动渲染**降级卡片**：

```html
<view class="delivery-photo-container">
  <image 
    wx:if="{{item.deliveryPhoto}}" 
    src="{{item.deliveryPhoto}}" 
    mode="aspectFill"
    class="delivery-photo"
  />
  <view wx:else class="photo-placeholder">
    <text class="placeholder-text">暂无照片</text>
  </view>
</view>
```

#### 3. 微信临时路径缓存方案

为解决微信开发者工具对 HTTP 图片的拦截问题，系统实现了临时路径缓存机制：

- 上传成功后，使用微信返回的临时路径（`wxfile://tmp/xxx`）直接渲染
- 临时路径不受 HTTPS 限制，可立即显示
- 使用 `tempPhotoMap` 对象缓存订单 ID 到临时路径的映射
- 重新加载数据时优先使用缓存的临时路径

#### 4. 服务降级策略

| 场景 | 降级策略 | 效果 |
|------|---------|------|
| 图片服务不可用 | 返回 null，渲染降级卡片 | 不影响订单列表展示 |
| 图片路径无效 | 过滤无效路径 | 避免 `<image>` 标签报错 |
| 网络延迟 | 使用本地临时路径 | 图片立即显示，提升用户体验 |

---

## 📁 项目结构

```
express-helper/                    # 项目根目录
├── backend/                        # 后端微服务
│   ├── pom.xml                    # 父工程依赖管理
│   ├── user-service/              # 用户服务（8081）
│   │   ├── src/main/java/com/example/userservice/
│   │   └── src/main/resources/
│   │       ├── application.yml    # 服务配置
│   │       └── mapper/            # MyBatis 映射文件
│   ├── order-service/             # 订单服务（8082）
│   │   ├── src/main/java/com/example/orderservice/
│   │   └── src/main/resources/
│   │       ├── application.yml
│   │       └── mapper/
│   ├── station-service/           # 站点服务（8083）
│   │   ├── src/main/java/com/example/stationservice/
│   │   └── src/main/resources/
│   │       ├── application.yml
│   │       └── mapper/
│   └── nacos/                     # Nacos 注册中心
│       └── nacos/bin/startup.cmd  # Nacos 启动脚本
├── frontend/                       # 前端小程序
│   ├── pages/                     # 页面目录
│   │   ├── index/                 # 首页（快递点选择）
│   │   ├── taskList/              # 任务列表页
│   │   ├── publish/               # 发布任务页
│   │   └── profile/               # 个人中心页
│   ├── app.js                     # 应用入口
│   ├── app.json                   # 全局配置
│   ├── app.wxss                   # 全局样式
│   └── project.config.json        # 项目配置
└── README.md                      # 项目说明文档
```

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 21+ | Java 开发环境 |
| Maven | 3.9+ | 依赖管理工具 |
| MySQL | 8.0+ | 数据库 |
| Nacos | 2.3.x | 服务注册中心 |
| 微信开发者工具 | >= 1.05.2103020 | 小程序开发工具 |

### 部署步骤

#### 1. 启动 Nacos

```bash
# Windows
cd backend/nacos/nacos/bin
startup.cmd -m standalone

# 访问 Nacos 控制台：http://localhost:8848/nacos
# 默认账号密码：nacos/nacos
```

#### 2. 配置数据库

创建数据库 `express_helper`，并执行 SQL 脚本：

```sql
CREATE DATABASE IF NOT EXISTS express_helper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 修改数据库配置

在各服务的 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/express_helper?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

#### 4. 启动微服务

按顺序启动以下服务：

```bash
# 启动用户服务（8081）
cd backend
mvn spring-boot:run -pl user-service

# 启动订单服务（8082）
mvn spring-boot:run -pl order-service

# 启动站点服务（8083）
mvn spring-boot:run -pl station-service
```

#### 5. 启动前端

- 打开微信开发者工具
- 导入 `frontend` 目录
- 配置 AppID（测试可使用测试号）
- 勾选"不校验合法域名"

---

## 🧪 测试运行

### API 文档访问

- **订单服务文档**: `http://localhost:8082/doc.html`
- **用户服务文档**: `http://localhost:8081/doc.html`
- **站点服务文档**: `http://localhost:8083/doc.html`

### 服务注册验证

访问 Nacos 控制台 `http://localhost:8848/nacos`，查看服务列表：

| 服务名 | 状态 | 实例数 |
|--------|------|--------|
| user-service | UP | 1 |
| order-service | UP | 1 |
| station-service | UP | 1 |

### 功能测试流程

```
1. 用户A发布任务（POST /api/orders）
   → 订单状态：pending

2. 用户B接单（POST /api/orders/accept）
   → 订单状态：claimed

3. 用户B确认送达（POST /api/orders/{id}/delivery）
   → 上传照片
   → 订单状态：pending_confirm

4. 用户A确认收货（POST /api/orders/{id}/confirm）
   → 订单状态：completed

5. 双方互评（POST /api/reviews）
   → 评价保存到数据库
```

---

## 📝 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|---------|
| **v1.0.0** | 2026-05 | 单体应用实现（发布任务、接单、确认送达） |
| **v2.0.0** | 2026-06 | 微服务架构重构，引入 Nacos 服务注册与发现 |
| **v2.1.0** | 2026-06 | 服务拆分：user-service、order-service、station-service |
| **v2.2.0** | 2026-06 | OpenFeign 服务间通信、Knife4j API 文档 |
| **v2.3.0** | 2026-06 | 照片上传功能、服务优雅降级机制 |

---

## 📄 License

MIT License

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

*Made with ❤️ for 分布式系统开发课程*

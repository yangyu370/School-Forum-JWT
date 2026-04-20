# 校园论坛系统

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-3.x-green.svg)

一个基于 Spring Boot + Vue 3 的现代化校园论坛系统

 [API文档](./API文档.md)

</div>

---

## 📖 项目介绍

本项目包含用户端论坛与管理员后台，支持注册登录、发帖评论、点赞收藏、公告通知、图片上传、封禁禁言、AI 聊天等功能。项目采用前后端分离架构，后端使用 Spring Boot 3.5.8，前端使用 Vue 3.5 + Element Plus。

### ✨ 核心功能

**用户端**
- 邮箱验证码注册、登录、密码重置
- 用户资料与隐私设置管理
- 论坛分类浏览、帖子列表、帖子详情
- 发帖、编辑、删除、评论、点赞、收藏、搜索
- 天气查询、系统通知
- AI 聊天窗口（SSE 流式）

**管理端**
- 用户分页查询、详情、封禁/禁言、密码重置
- 帖子管理（置顶、锁定、隐藏、删除）
- 评论删除、公告发布
- 邮件记录查看与重发
- 违禁词列表管理

---

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 核心框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis Plus | 3.5.x | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.x | 缓存 |
| RabbitMQ | 3.x | 消息队列 |
| MinIO | - | 对象存储 |
| JWT | - | 身份认证 |
| Swagger | 3.x | API文档 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Element Plus | - | UI组件库 |
| Vite | 5.x | 构建工具 |
| Axios | - | HTTP客户端 |
| Pinia | - | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Quill | - | 富文本编辑器 |

---

## 📁 项目结构

```
my-project/
├── my-project-backend/          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── filter/      # 过滤器
│   │   │   │   ├── mapper/      # Mapper接口
│   │   │   │   ├── service/     # 服务层
│   │   │   │   └── utils/       # 工具类
│   │   │   └── resources/
│   │   │       └── application.yml  # 配置文件
│   │   └── test/                # 测试
│   └── pom.xml                  # Maven配置
│
├── my-project-frontend/         # 前端项目
│   ├── public/                  # 静态资源
│   ├── src/
│   │   ├── assets/              # 资源文件
│   │   ├── components/          # 组件
│   │   ├── net/                 # 网络请求
│   │   ├── router/              # 路由配置
│   │   ├── store/               # 状态管理
│   │   ├── views/               # 页面
│   │   ├── App.vue              # 根组件
│   │   └── main.js              # 入口文件
│   ├── package.json             # 依赖配置
│   └── vite.config.js           # Vite配置
│
├── API文档.md                    # API接口文档
└── README.md                    # 项目说明
```

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.6+

### 后端部署

1. **克隆项目**
```bash
git clone https://github.com/your-repo/my-project.git
cd my-project/my-project-backend
```

2. **配置数据库**

创建数据库：
```sql
CREATE DATABASE campus_forum CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置文件**

编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_forum
    username: your_username
    password: your_password

  data:
    redis:
      host: localhost
      port: 6379

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

  mail:
    host: smtp.qq.com
    username: your_email@qq.com
    password: your_mail_password

minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
```

4. **启动项目**
```bash
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端部署

1. **进入前端目录**
```bash
cd my-project-frontend
```

2. **安装依赖**
```bash
npm install
```

3. **配置后端地址**

编辑 `vite.config.js`：
```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

4. **启动开发服务器**
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 访问地址

- **前端页面**：http://localhost:5173
- **后端 API**：http://localhost:8080
- **Swagger 文档**：http://localhost:8080/swagger-ui/index.html

### 接口分组

- 认证：`/api/auth/**`
- 用户：`/api/user/**`
- 论坛：`/api/forum/**`
- 图片：`/api/image/**`
- 通知：`/api/notification/**`
- AI：`/api/ai/**`
- 管理员：`/api/admin/**`

**统一响应**：
```json
{
  "code": 200,
  "data": {},
  "message": "请求成功"
}
```

---

## 📚 使用指南

### 用户端功能

#### 1. 注册与登录
- 使用邮箱注册账号
- 邮箱验证码验证
- 支持记住登录状态
- 密码找回功能

#### 2. 个人中心
- 查看和编辑个人资料
- 上传头像
- 修改密码和邮箱
- 隐私设置管理

#### 3. 论坛功能
- 浏览帖子列表（支持分类筛选）
- 查看帖子详情
- 发布新帖子（富文本编辑）
- 点赞和收藏帖子
- 评论和回复
- 查看热门帖子
- 帖子搜索

#### 4. 通知系统
- 接收系统通知
- 评论提醒
- 点赞提醒
- 管理通知消息

### 管理员功能

#### 1. 用户管理
- 查看用户列表（分页）
- 查看用户详细信息
- 封禁/解封用户
- 禁言/解禁用户
- 修改用户信息

#### 2. 内容管理
- 查看所有帖子
- 置顶/取消置顶帖子
- 锁定/解锁帖子讨论
- 设置帖子可见性
- 删除违规帖子与评论
- 发布系统公告

#### 3. 邮件与内容管理
- 查看邮件发送记录
- 重新发送邮件
- 维护违禁词列表

---

## 🔒 安全说明

- ✅ JWT Token 认证（有效期 7 天）
- ✅ Token 黑名单机制
- ✅ 密码加密存储（BCrypt）
- ✅ 接口访问限流
- ✅ CORS 跨域配置
- ✅ XSS 防护
- ✅ SQL 注入防护（MyBatis Plus）
- ✅ 邮箱验证码验证
- ✅ 封禁用户自动踢出
- ⚠️ **配置安全**：不要将真实凭证提交到仓库，使用 `application.example.yml` 作为模板

---

## 🛠️ 常见问题排查

### Q1: 获取不到帖子分类（`/api/forum/types`）

**现象**：前端请求分类接口返回 401 或 403

**排查步骤**：

1. **检查登录态**
   ```javascript
   // 浏览器控制台
   localStorage.getItem('access_token')
   // 或
   sessionStorage.getItem('access_token')
   ```
   应返回 `{"token":"xxx","expire":123456,"role":"user"}`

2. **确认请求头**
   ```
   Authorization: Bearer <your_token>
   ```

3. **验证后端启动**
   - 访问 `http://localhost:8080/swagger-ui/index.html` 可访问则启动成功
   - 查看后端日志是否有错误

4. **检查响应**
   - `401`：Token 过期或无效，需重新登录
   - `403`：权限不足或用户被封禁

### Q2: 图片上传失败

**可能原因**：
- MinIO 未启动或连接失败
- `spring.minio.*` 配置错误
- 文件大小超过 10MB 限制

**解决方案**：
```bash
# 检查 MinIO 连接
curl http://localhost:9000/minio/health/live

# 检查后端日志中的 MinIO 错误信息
```

### Q3: 邮件发送失败

**可能原因**：
- SMTP 配置使用的是密码而非授权码
- RabbitMQ 连接失败
- 邮箱配置有误

**解决方案**：
- QQ 邮箱：使用"授权码"（16 位），可在"账户设置 - 安全设置"获取
- 163 邮箱：同样需要生成授权码
- 检查 RabbitMQ 是否可访问：`http://localhost:15672`（默认用户 guest/guest）

### Q4: 前端无法连接后端

**排查**：
- 确认后端启动：`curl http://localhost:8080/swagger-ui/index.html`
- 检查 axios 基地址：`src/main.js` 中 `axios.defaults.baseURL`
- 检查防火墙或代理设置
- 浏览器控制台查看网络请求是否发出、错误信息

### Q5: Redis/RabbitMQ 连接超时

**排查**：
```bash
# 测试 Redis
redis-cli -h localhost -p 6379 ping

# 测试 RabbitMQ（如已安装 amqp-utils）
amqp-declare-queue -h localhost -u guest:guest -q test-queue
```

---

## 📊 数据库设计

### 主要数据表

| 表名 | 说明 |
|------|------|
| db_account | 用户账户表 |
| db_account_details | 用户详细信息表 |
| db_account_privacy | 用户隐私设置表 |
| db_topic | 帖子表 |
| db_topic_type | 帖子分类表 |
| db_topic_comment | 评论表 |
| db_topic_interact | 帖子互动表（点赞/收藏） |
| db_notification | 通知表 |
| db_image_store | 图片存储记录表 |

详细的表结构请查看数据库初始化脚本。

---

## 🔧 配置详解

### 后端配置关键项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.security.jwt.key` | JWT 加密密钥 | 需配置 |
| `spring.security.jwt.expire` | Token 有效期（小时） | 7 |
| `spring.datasource.url` | MySQL 连接 | `jdbc:mysql://localhost:3306/campus_forum` |
| `spring.data.redis.*` | Redis 连接 | `localhost:6379` |
| `spring.rabbitmq.*` | RabbitMQ 连接 | `localhost:5672` |
| `spring.mail.*` | SMTP 邮箱配置 | 需配置（授权码） |
| `spring.minio.*` | MinIO 对象存储 | `localhost:9000` |
| `spring.ai.deepseek.*` | DeepSeek API | 需 API Key |

### 前端配置

- **API 基地址**：`src/main.js` 中 `axios.defaults.baseURL = 'http://localhost:8080'`
- **Token 存储**：localStorage（记住登录）或 sessionStorage（不记住）
- **路由守卫**：`src/router/index.js` 检查登录与权限
- **状态管理**：Pinia Store `src/store/index.js`

---

## 📈 性能优化

- 🚀 Redis 缓存减少数据库查询（验证码、Token、天气、帖子）
- 🚀 图片 CDN 加速（MinIO）
- 🚀 接口限流防止恶意请求
- 🚀 分页加载减少数据传输
- 🚀 懒加载优化前端性能
- 🚀 Token 黑名单机制
- 🚀 数据库索引优化（已实现在 MyBatis Plus）

---

## 📝 开发计划

- [ ] 实现失物招领功能
- [ ] 实现校园活动功能  
- [ ] 实现表白墙功能
- [ ] 添加私信系统
- [ ] 添加关注/粉丝功能
- [ ] 实现全文搜索（ElasticSearch）
- [ ] 添加举报系统
- [ ] 实现 WebSocket 实时通知
- [ ] 移动端适配优化
- [ ] 数据统计看板

---

## 📚 关键文档

- **项目配置指南**：见 `CLAUDE.md`（开发约定与模块说明）
- **API 详细文档**：见 `API文档.md`（接口端点与参数）
- **环境配置模板**：见 `my-project-backend/src/main/resources/application.example.yml`

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建新的分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 提交 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

---

## 👥 作者

- GitHub: [@yangyu370](https://github.com/yangyu370)
- Email: 1904474691@qq.com
---

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis Plus](https://baomidou.com/)
- [MinIO](https://min.io/)

---

## 📞 联系我们

如有任何问题或建议，请通过以下方式联系我们：
- 提交 Issue
---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个Star！ ⭐**

Made with ❤️ by 养鱼

</div>


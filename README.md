# 校园论坛系统

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-3.x-green.svg)

一个基于 Spring Boot + Vue 3 的现代化校园论坛系统

[在线演示](http://your-demo-url.com) | [API文档](./API文档.md) | [使用指南](#使用指南)

</div>

---

## 📖 项目介绍

这是一个功能完善的校园论坛系统，提供用户注册登录、帖子发布与互动、评论系统、通知推送、用户管理等功能。项目采用前后端分离架构，后端使用Spring Boot 3.x，前端使用Vue 3 + Element Plus。

### ✨ 主要特性

- 🔐 **用户认证系统**：邮箱注册、JWT认证、密码找回
- 📝 **论坛功能**：发帖、评论、点赞、收藏、帖子分类
- 🖼️ **图片管理**：头像上传、图片存储（MinIO）
- 🔔 **通知系统**：实时消息推送、消息管理
- 👨‍💼 **管理后台**：用户管理、封禁禁言、数据统计
- 🌤️ **天气查询**：基于地理位置的天气信息
- 🎨 **现代化UI**：响应式设计、暗黑模式支持
- 🚀 **高性能**：Redis缓存、接口限流、会话管理

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

### 访问系统

- **前端页面**: http://localhost:5173
- **后端API**: http://localhost:8080
- **Swagger文档**: http://localhost:8080/swagger-ui/index.html

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

#### 4. 通知系统
- 接收系统通知
- 评论提醒
- 点赞提醒
- 管理通知消息

### 管理员功能

#### 1. 用户管理
- 查看用户列表
- 查看用户详细信息
- 封禁/解封用户
- 禁言/解禁用户
- 修改用户信息

#### 2. 内容管理
- 查看所有帖子
- 删除违规内容
- 查看举报信息

---

## 🎯 核心功能演示

### 用户注册流程
```
1. 输入邮箱 → 2. 获取验证码 → 3. 填写用户信息 → 4. 完成注册
```

### 发帖流程
```
1. 选择分类 → 2. 编辑内容 → 3. 上传图片 → 4. 发布帖子
```

### 管理员操作
```
1. 进入管理后台 → 2. 查看用户列表 → 3. 封禁违规用户 → 4. 审核内容
```

---

## 🔒 安全特性

- ✅ JWT Token 认证
- ✅ Token 黑名单机制
- ✅ 密码加密存储（BCrypt）
- ✅ 接口访问限流
- ✅ CORS 跨域配置
- ✅ XSS 防护
- ✅ SQL 注入防护（MyBatis）
- ✅ 邮箱验证码验证
- ✅ 封禁用户自动踢出

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

## 🔧 配置说明

### 邮件服务配置
支持使用QQ邮箱、163邮箱等SMTP服务发送验证码邮件。

### MinIO对象存储配置
用于存储用户头像和帖子图片，支持分布式部署。

### Redis缓存配置
- 验证码缓存
- Token黑名单
- 封禁用户列表
- 天气信息缓存
- 帖子预览缓存

### RabbitMQ消息队列配置
用于异步发送邮件通知。

---

## 📈 性能优化

- 🚀 Redis缓存减少数据库查询
- 🚀 图片CDN加速（MinIO）
- 🚀 接口限流防止恶意请求
- 🚀 分页加载减少数据传输
- 🚀 懒加载优化前端性能
- 🚀 Token黑名单机制
- 🚀 数据库索引优化

---

## 🐛 常见问题

### Q1: 无法发送邮件？
**A:** 检查邮箱配置是否正确，确保开启了SMTP服务并使用授权码。

### Q2: 图片上传失败？
**A:** 检查MinIO服务是否启动，配置是否正确。

### Q3: 登录后无法访问接口？
**A:** 检查Token是否正确携带在请求头中，格式为 `Authorization: Bearer {token}`。

### Q4: 被封禁用户无法登出？
**A:** 已修复，被封禁用户现在可以正常登出。

### Q5: 刷新页面后进入死循环？
**A:** 已修复，被封禁用户刷新页面会自动跳转到登录页。

---

## 📝 开发计划

- [ ] 实现失物招领功能
- [ ] 实现校园活动功能
- [ ] 实现表白墙功能
- [ ] 添加私信系统
- [ ] 添加关注/粉丝功能
- [ ] 实现全文搜索（ElasticSearch）
- [ ] 添加举报系统
- [ ] 实现WebSocket实时通知
- [ ] 移动端适配优化
- [ ] 数据统计看板

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

**您的名字**
- GitHub: [@your-github](https://github.com/your-github)
- Email: your-email@example.com

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
- 发送邮件至：your-email@example.com

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个Star！ ⭐**

Made with ❤️ by [Your Name]

</div>


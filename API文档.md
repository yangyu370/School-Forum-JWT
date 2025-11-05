# 校园论坛系统 API 接口文档

## 📖 目录
- [项目介绍](#项目介绍)
- [接口规范](#接口规范)
- [认证授权模块](#认证授权模块) (5个接口)
- [用户管理模块](#用户管理模块) (7个接口)
- [论坛模块](#论坛模块) (13个接口)
- [图片管理模块](#图片管理模块) (3个接口)
- [通知模块](#通知模块) (3个接口)
- [管理员模块](#管理员模块) (7个接口)
- [响应状态码](#响应状态码)

**接口总数：38个**

---

## 项目介绍

这是一个基于 Spring Boot + Vue 3 的校园论坛系统，提供用户注册登录、帖子发布、评论互动、通知推送等功能。

### 技术栈
- **后端**: Spring Boot 3.x, Spring Security, JWT, MyBatis Plus, Redis, RabbitMQ
- **数据库**: MySQL
- **文件存储**: MinIO
- **前端**: Vue 3, Element Plus, Vite

### 基础URL
```
开发环境: http://localhost:8080
生产环境: https://your-domain.com
```

---

## 接口规范

### 统一响应格式

所有接口返回格式统一为：

```json
{
  "code": 200,
  "data": {},
  "message": "请求成功"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200表示成功 |
| data | object | 返回的数据，可为null |
| message | string | 响应消息 |

### 认证方式

除公开接口外，其他接口需要在请求头中携带JWT Token：

```
Authorization: Bearer {token}
```

---

## 认证授权模块

### 1. 获取邮箱验证码

**接口地址**: `GET /api/auth/ask-code`

**权限**: 公开

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱地址 |
| type | string | 是 | 类型：register(注册)、reset(重置密码)、modify(修改邮箱) |

**请求示例**:
```
GET /api/auth/ask-code?email=user@example.com&type=register
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 2. 用户注册

**接口地址**: `POST /api/auth/register`

**权限**: 公开

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱地址 |
| code | string | 是 | 验证码 |
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**请求示例**:
```json
{
  "email": "user@example.com",
  "code": "123456",
  "username": "张三",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 3. 用户登录

**接口地址**: `POST /api/auth/login`

**权限**: 公开

**请求参数** (application/x-www-form-urlencoded):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名或邮箱 |
| password | string | 是 | 密码 |

**请求示例**:
```
username=zhangsan&password=password123
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "张三",
    "email": "user@example.com",
    "role": "user",
    "avatar": "/avatar/xxx.jpg",
    "registerTime": "2024-01-01 12:00:00",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expire": 1735689600000
  },
  "message": "请求成功"
}
```

---

### 4. 用户登出

**接口地址**: `GET /api/auth/logout`

**权限**: 所有用户（包括被封禁用户）

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 5. 确认重置密码

**接口地址**: `POST /api/auth/reset-confirm`

**权限**: 公开

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱地址 |
| code | string | 是 | 验证码 |

**请求示例**:
```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 6. 重置密码

**接口地址**: `POST /api/auth/reset-password`

**权限**: 公开

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 邮箱地址 |
| code | string | 是 | 验证码 |
| password | string | 是 | 新密码 |

**请求示例**:
```json
{
  "email": "user@example.com",
  "code": "123456",
  "password": "newpassword123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

## 用户管理模块

### 1. 获取当前用户信息

**接口地址**: `GET /api/user/info`

**权限**: 需要登录

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "张三",
    "email": "user@example.com",
    "role": "user",
    "avatar": "/avatar/xxx.jpg",
    "registerTime": "2024-01-01 12:00:00"
  },
  "message": "请求成功"
}
```

---

### 2. 获取用户详细信息

**接口地址**: `GET /api/user/details`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "gender": 1,
    "phone": "13800138000",
    "qq": "123456789",
    "wx": "wechat_id",
    "desc": "个人简介"
  },
  "message": "请求成功"
}
```

---

### 3. 保存用户详细信息

**接口地址**: `POST /api/user/save-details`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 否 | 用户名 |
| gender | int | 否 | 性别：0-女，1-男 |
| phone | string | 否 | 手机号 |
| qq | string | 否 | QQ号 |
| wx | string | 否 | 微信号 |
| desc | string | 否 | 个人简介 |

**请求示例**:
```json
{
  "username": "张三",
  "gender": 1,
  "phone": "13800138000",
  "qq": "123456789",
  "wx": "wechat_id",
  "desc": "这是我的个人简介"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 4. 修改邮箱

**接口地址**: `POST /api/user/modify-email`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | string | 是 | 新邮箱地址 |
| code | string | 是 | 验证码 |

**请求示例**:
```json
{
  "email": "newemail@example.com",
  "code": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 5. 修改密码

**接口地址**: `POST /api/user/change-password`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| password | string | 是 | 旧密码 |
| new_password | string | 是 | 新密码 |

**请求示例**:
```json
{
  "password": "oldpassword123",
  "new_password": "newpassword123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 6. 获取隐私设置

**接口地址**: `GET /api/user/privacy`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "phone": true,
    "qq": false,
    "wx": true,
    "email": false,
    "gender": true
  },
  "message": "请求成功"
}
```

---

### 7. 保存隐私设置

**接口地址**: `POST /api/user/save-privacy`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | boolean | 是 | 是否公开手机号 |
| qq | boolean | 是 | 是否公开QQ |
| wx | boolean | 是 | 是否公开微信 |
| email | boolean | 是 | 是否公开邮箱 |
| gender | boolean | 是 | 是否公开性别 |

**请求示例**:
```json
{
  "phone": true,
  "qq": false,
  "wx": true,
  "email": false,
  "gender": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

## 论坛模块

### 1. 获取天气信息

**接口地址**: `GET /api/forum/weather`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| longitude | double | 是 | 经度 |
| latitude | double | 是 | 纬度 |

**请求示例**:
```
GET /api/forum/weather?longitude=116.4074&latitude=39.9042
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "location": "北京",
    "temperature": 25,
    "weather": "晴",
    "wind": "东南风3级"
  },
  "message": "请求成功"
}
```

---

### 2. 获取帖子分类列表

**接口地址**: `GET /api/forum/types`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "校园生活",
      "desc": "分享校园日常",
      "color": "#42b983"
    },
    {
      "id": 2,
      "name": "学习交流",
      "desc": "学习心得分享",
      "color": "#409eff"
    }
  ],
  "message": "请求成功"
}
```

---

### 3. 创建帖子

**接口地址**: `POST /api/forum/create-topic`

**权限**: 需要登录（未被禁言）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | int | 是 | 帖子分类ID |
| title | string | 是 | 标题 |
| content | string | 是 | 内容（JSON格式） |

**请求示例**:
```json
{
  "type": 1,
  "title": "我的第一篇帖子",
  "content": "{\"ops\":[{\"insert\":\"这是帖子内容\\n\"}]}"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 4. 获取帖子列表（分页）

**接口地址**: `GET /api/forum/list-topic`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 是 | 页码（从0开始） |
| type | int | 是 | 分类ID，0表示全部 |

**请求示例**:
```
GET /api/forum/list-topic?page=0&type=0
```

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "我的第一篇帖子",
      "text": "这是帖子内容摘要...",
      "type": 1,
      "username": "张三",
      "avatar": "/avatar/xxx.jpg",
      "time": "2024-01-01 12:00:00",
      "like": 10,
      "collect": 5
    }
  ],
  "message": "请求成功"
}
```

---

### 5. 获取热门帖子

**接口地址**: `GET /api/forum/top-topic`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "热门帖子标题",
      "time": "2024-01-01 12:00:00",
      "like": 100
    }
  ],
  "message": "请求成功"
}
```

---

### 6. 获取帖子详情

**接口地址**: `GET /api/forum/topic`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tid | int | 是 | 帖子ID |

**请求示例**:
```
GET /api/forum/topic?tid=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "我的第一篇帖子",
    "content": "{\"ops\":[{\"insert\":\"这是帖子内容\\n\"}]}",
    "type": 1,
    "uid": 1,
    "username": "张三",
    "avatar": "/avatar/xxx.jpg",
    "time": "2024-01-01 12:00:00",
    "like": 10,
    "collect": 5,
    "isLike": false,
    "isCollect": false
  },
  "message": "请求成功"
}
```

---

### 7. 帖子互动（点赞/收藏）

**接口地址**: `GET /api/forum/interact`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tid | int | 是 | 帖子ID |
| type | string | 是 | 类型：like(点赞)、collect(收藏) |
| state | boolean | 是 | true-添加，false-取消 |

**请求示例**:
```
GET /api/forum/interact?tid=1&type=like&state=true
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 8. 获取收藏列表

**接口地址**: `GET /api/forum/collects`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "收藏的帖子",
      "text": "帖子摘要...",
      "type": 1,
      "username": "张三",
      "avatar": "/avatar/xxx.jpg",
      "time": "2024-01-01 12:00:00",
      "like": 10,
      "collect": 5
    }
  ],
  "message": "请求成功"
}
```

---

### 9. 更新帖子

**接口地址**: `POST /api/forum/update-topic`

**权限**: 需要登录（帖子作者）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 帖子ID |
| type | int | 是 | 帖子分类ID |
| title | string | 是 | 标题 |
| content | string | 是 | 内容（JSON格式） |

**请求示例**:
```json
{
  "id": 1,
  "type": 1,
  "title": "更新后的标题",
  "content": "{\"ops\":[{\"insert\":\"更新后的内容\\n\"}]}"
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 10. 添加评论

**接口地址**: `POST /api/forum/add-comment`

**权限**: 需要登录（未被禁言）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tid | int | 是 | 帖子ID |
| content | string | 是 | 评论内容 |
| quote | int | 否 | 引用的评论ID（回复评论时使用） |

**请求示例**:
```json
{
  "tid": 1,
  "content": "这是一条评论",
  "quote": null
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 11. 获取评论列表

**接口地址**: `GET /api/forum/comments`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tid | int | 是 | 帖子ID |
| page | int | 是 | 页码（从0开始） |

**请求示例**:
```
GET /api/forum/comments?tid=1&page=0
```

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "content": "{\"ops\":[{\"insert\":\"这是一条评论\\n\"}]}",
      "time": "2024-01-01 12:00:00",
      "quote": null,
      "user": {
        "id": 1,
        "username": "张三",
        "avatar": "/avatar/xxx.jpg",
        "role": "user",
        "gender": true,
        "phone": "13800138000",
        "email": "user@example.com",
        "wx": "wechat_id",
        "qq": "123456789"
      }
    }
  ],
  "message": "请求成功"
}
```

**说明**:
- `content`: 评论内容为 Quill 编辑器的 JSON 格式
- `quote`: 被引用的评论内容，如果是回复评论则显示原评论摘要
- `user`: 评论者的用户信息（根据隐私设置，某些字段可能不显示）
- `role`: 用户角色（user-普通用户，admin-管理员）

---

### 12. 删除评论

**接口地址**: `GET /api/forum/delete-comment`

**权限**: 需要登录（评论作者本人）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 评论ID |

**请求示例**:
```
GET /api/forum/delete-comment?id=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

**说明**:
- 只能删除自己发布的评论
- 管理员删除评论请使用管理员接口：`/api/admin/forum/Admin-deleteComment`

---

### 13. 删除帖子

**接口地址**: `GET /api/forum/delete-topic`

**权限**: 需要登录（帖子作者本人）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 帖子ID |

**请求示例**:
```
GET /api/forum/delete-topic?id=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

**说明**:
- 只能删除自己发布的帖子
- 删除帖子时会同时删除该帖子的所有评论、点赞、收藏记录和相关通知
- 管理员删除帖子请使用管理员接口：`/api/admin/forum/Admin-deleteTopic`

---

## 图片管理模块

### 1. 上传头像

**接口地址**: `POST /api/image/avatar`

**权限**: 需要登录

**请求参数** (multipart/form-data):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | file | 是 | 图片文件（最大10MB） |

**响应示例**:
```json
{
  "code": 200,
  "data": "/avatar/2024/01/01/xxx.jpg",
  "message": "请求成功"
}
```

---

### 2. 上传图片（临时）

**接口地址**: `POST /api/image/cache`

**权限**: 需要登录

**请求参数** (multipart/form-data):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | file | 是 | 图片文件（最大10MB） |

**响应示例**:
```json
{
  "code": 200,
  "data": "/cache/2024/01/01/xxx.jpg",
  "message": "请求成功"
}
```

---

### 3. 获取图片

**接口地址**: `GET /images/**`

**权限**: 公开

**请求示例**:
```
GET /images/avatar/2024/01/01/xxx.jpg
```

**响应**: 图片文件（image/jpg）

---

## 通知模块

### 1. 获取通知列表

**接口地址**: `GET /api/notification/list`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "新评论通知",
      "content": "张三评论了你的帖子",
      "type": "success",
      "url": "/topic-detail/1",
      "time": "2024-01-01 12:00:00"
    }
  ],
  "message": "请求成功"
}
```

---

### 2. 删除通知

**接口地址**: `GET /api/notification/delete`

**权限**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 通知ID |

**请求示例**:
```
GET /api/notification/delete?id=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 3. 删除全部通知

**接口地址**: `GET /api/notification/delete-all`

**权限**: 需要登录

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

## 管理员模块

### 1. 获取用户列表

**接口地址**: `GET /api/admin/user/list`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 是 | 页码 |
| size | int | 是 | 每页数量 |

**请求示例**:
```
GET /api/admin/user/list?page=1&size=10
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "username": "张三",
        "email": "user@example.com",
        "role": "user",
        "banned": false,
        "mute": false,
        "registerTime": "2024-01-01 12:00:00"
      }
    ]
  },
  "message": "请求成功"
}
```

---

### 2. 获取用户详情

**接口地址**: `GET /api/admin/user/detail`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 用户ID |

**请求示例**:
```
GET /api/admin/user/detail?id=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "detail": {
      "gender": 1,
      "phone": "13800138000",
      "qq": "123456789",
      "wx": "wechat_id",
      "desc": "个人简介"
    },
    "privacy": {
      "phone": true,
      "qq": false,
      "wx": true,
      "email": false,
      "gender": true
    }
  },
  "message": "请求成功"
}
```

---

### 3. 保存用户信息（封禁/禁言）

**接口地址**: `POST /api/admin/user/save`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 用户ID |
| username | string | 否 | 用户名 |
| email | string | 否 | 邮箱 |
| role | string | 否 | 角色 |
| banned | boolean | 否 | 是否封禁 |
| mute | boolean | 否 | 是否禁言 |
| detail | object | 否 | 用户详细信息 |
| privacy | object | 否 | 隐私设置 |

**请求示例**:
```json
{
  "id": 1,
  "username": "张三",
  "email": "user@example.com",
  "role": "user",
  "banned": false,
  "mute": true,
  "detail": {
    "gender": 1,
    "phone": "13800138000"
  },
  "privacy": {
    "phone": true,
    "qq": false
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

---

### 4. 获取帖子列表（管理员）

**接口地址**: `GET /api/admin/forum/list`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 是 | 页码 |
| size | int | 是 | 每页数量 |

**请求示例**:
```
GET /api/admin/forum/list?page=1&size=10
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "帖子标题",
        "text": "帖子摘要内容...",
        "type": 1,
        "username": "张三",
        "avatar": "/avatar/xxx.jpg",
        "role": "user",
        "time": "2024-01-01 12:00:00",
        "top": 0,
        "like": 10,
        "collect": 5
      }
    ]
  },
  "message": "请求成功"
}
```

---

### 5. 设置帖子置顶

**接口地址**: `POST /api/admin/forum/set-top`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tid | int | 是 | 帖子ID |
| status | boolean | 是 | true-置顶，false-取消置顶 |

**请求示例**:
```
POST /api/admin/forum/set-top?tid=1&status=true
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

**错误响应**:
```json
{
  "code": 404,
  "data": null,
  "message": "该帖子不存在或已删除"
}
```

---

### 6. 删除帖子（管理员）

**接口地址**: `GET /api/admin/forum/Admin-deleteTopic`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tid | int | 是 | 帖子ID |

**请求示例**:
```
GET /api/admin/forum/Admin-deleteTopic?tid=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

**错误响应**:
```json
{
  "code": 400,
  "data": null,
  "message": "无权删除管理员的帖子"
}
```

**说明**:
- 管理员不能删除其他管理员发布的帖子
- 删除帖子时会同时删除该帖子的所有评论、点赞、收藏记录和相关通知
- 删除操作会清除相关的Redis缓存

---

### 7. 删除评论（管理员）

**接口地址**: `GET /api/admin/forum/Admin-deleteComment`

**权限**: 管理员

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | int | 是 | 评论ID |

**请求示例**:
```
GET /api/admin/forum/Admin-deleteComment?id=1
```

**响应示例**:
```json
{
  "code": 200,
  "data": null,
  "message": "请求成功"
}
```

**错误响应**:

404 - 评论不存在:
```json
{
  "code": 404,
  "data": null,
  "message": "该评论不存在或已被删除"
}
```

403 - 无权删除:
```json
{
  "code": 403,
  "data": null,
  "message": "无权删除管理员发布的评论"
}
```

**说明**:
- 管理员不能删除其他管理员发布的评论
- 删除评论不会影响帖子本身

---

## 响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误或业务逻辑错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问（账户被封禁、权限不足等） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 错误处理

当请求失败时，响应格式如下：

```json
{
  "code": 400,
  "data": null,
  "message": "错误信息描述"
}
```

常见错误信息：
- `"用户名或密码错误"`
- `"邮箱已被注册"`
- `"验证码错误或已过期"`
- `"登录状态已过期，请重新登录"`
- `"您的账户已被封禁，请联系管理员"`
- `"你已被禁言，无法发表主题"`
- `"权限不足"`

---

## 开发建议

### 1. 接口调试
推荐使用 Postman、Apifox 或 Swagger UI 进行接口测试。

Swagger UI 地址：`http://localhost:8080/swagger-ui/index.html`

### 2. 认证流程
1. 调用登录接口获取 token
2. 在后续请求的 Header 中添加 `Authorization: Bearer {token}`
3. Token 过期后需要重新登录

### 3. 图片上传
- 图片大小限制：10MB
- 支持格式：JPG、PNG、GIF
- 存储方式：MinIO 对象存储

### 4. 内容编辑
- 帖子内容使用 Quill 编辑器的 JSON 格式
- 前端使用 Quill.js 进行富文本编辑
- 后端存储 JSON 格式的内容

---

## 更新日志

### v1.1.0 (2025-11-05)
- 新增管理员论坛管理接口
  - 获取帖子列表（管理员）
  - 设置帖子置顶
  - 删除帖子（管理员）
  - 删除评论（管理员）
- 优化评论接口返回数据结构
  - 评论用户信息中新增 `role` 字段
  - 完善评论数据说明
- 增强权限控制
  - 管理员无法删除其他管理员的帖子和评论
  - 明确区分普通用户和管理员的删除权限

### v1.0.0 (2025-03-01)
- 初始版本发布
- 实现基础的用户认证、论坛、通知功能
- 包含用户管理、帖子管理、评论系统、图片上传等核心功能

---




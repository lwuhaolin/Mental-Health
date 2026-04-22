# 星语航海 - 前端项目

基于Vue3 + Element Plus的心理测评与分析平台前端项目。

## 技术栈

- **框架**: Vue 3 + Composition API
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **样式**: SCSS

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   ├── assets/            # 静态资源
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   ├── stores/            # 状态管理
│   ├── styles/            # 样式文件
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html             # HTML模板
├── package.json           # 依赖配置
└── vite.config.js         # Vite配置
```

## 功能模块

### 1. 用户认证
- 用户登录/注册
- Token管理
- 路由守卫

### 2. 首页
- 轮播图展示
- 平台优势介绍
- 热门测评推荐
- 快速入口

### 3. 心理测评
- 测评分类筛选
- 测评详情查看
- 在线测评功能
- 测评历史记录

### 4. 漂流瓶
- 心情分享发布
- 点赞互动功能
- 回复交流
- 心情标签分类

### 5. AI互动
- AI疗愈对话
- 智能心理评估
- 对话历史管理
- 快速问题模板

### 6. 联系我们
- 联系信息展示
- 在线留言反馈
- 常见问题解答
- 团队介绍

## 安装与运行

### 环境要求
- Node.js >= 16.0.0
- npm >= 7.0.0

### 安装依赖
```bash
cd frontend
npm install
```

### 开发环境运行
```bash
npm run dev
```

### 生产环境构建
```bash
npm run build
```

### 预览构建结果
```bash
npm run preview
```

## 配置说明

### 环境变量
创建 `.env` 文件配置环境变量：

```env
# API基础地址
VITE_API_BASE_URL=http://localhost:8080

# 应用标题
VITE_APP_TITLE=星语航海心理平台
```

### API配置
后端API接口地址在 `src/utils/request.js` 中配置：

```javascript
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000
})
```

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 平台首页，重定向到登录页 |
| `/login` | 登录页 | 用户登录和注册 |
| `/home` | 首页 | 平台主页面 |
| `/assessment` | 测评中心 | 心理测评列表和详情 |
| `/assessment/taking/:id` | 测评页面 | 在线测评功能 |
| `/assessment/history` | 测评历史 | 用户测评记录 |
| `/bottle` | 漂流瓶 | 心情分享社区 |
| `/ai-chat` | AI互动 | AI心理助手对话 |
| `/contact` | 联系我们 | 联系信息和反馈 |

## 特色功能

### 1. 响应式设计
- 支持PC端和移动端
- 自适应布局
- 触摸友好的交互

### 2. 用户体验优化
- 页面加载动画
- 错误边界处理
- 友好的空状态提示

### 3. 性能优化
- 组件懒加载
- 图片懒加载
- API请求缓存

### 4. 安全性
- XSS防护
- CSRF防护
- 敏感信息脱敏

## 开发规范

### 代码规范
- 使用ESLint + Prettier
- 遵循Vue官方风格指南
- 组件使用Composition API

### 提交规范
- 使用Conventional Commits
- 提交信息格式规范
- 代码审查流程

## 部署说明

### 开发环境
```bash
npm run dev
```

### 生产环境
```bash
npm run build
```

构建产物位于 `dist` 目录，可部署到任意静态文件服务器。

## 浏览器支持

- Chrome >= 88
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 相关链接

- [Vue 3文档](https://v3.vuejs.org/)
- [Element Plus文档](https://element-plus.org/)
- [Vite文档](https://vitejs.dev/)

## 许可证

MIT License
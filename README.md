# Maicorder

Maicorder 是一个面向街机音游玩家的记录平台。

## 项目功能
- 支持音游游玩记录的存储与管理
- 支持记录机厅评价，方便玩家选择游玩地点
- 支持记录周边美食推荐，完善线下游玩体验
- 支持出勤频率统计，直观查看活跃情况
- 支持游玩战绩绑定，统一管理个人成绩信息

## 技术架构
后端框架：Spring Boot
前端框架：Vue
关系型数据库：MySQL
缓存中间件：Redis
部署环境：Linux + Nginx

## 快速部署
1. 准备环境：安装 JDK、MySQL、Nginx、Node.js
2. 导入数据库：执行项目 SQL 初始化脚本
3. 启动后端：在后端目录运行 Spring Boot 服务 ，运行maimaipy-app中的客户端服务
4. 构建前端：在前端目录执行 `npm install` 和 `npm run build`
5. 发布静态资源：将前端构建产物部署到 Nginx 目录
6. 配置反向代理：将 API 请求转发到后端服务端口

## 在线访问
项目线上地址：[https://umi7.top](http://119.29.193.206/)

## 上游依赖
- [MaimaiPy](https://github.com/maimaiPy/maimaiPy)：国服舞萌工具库
- 落雪咖啡屋曲绘查询

## 贡献
- 在提交pr前请保证代码可以成功运行

## 问题反馈
如果你在使用中遇到问题，欢迎在仓库的 Issues 页面提交反馈与建议。

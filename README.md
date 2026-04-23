# 请同时运行 maimaipy app

### JWT令牌格式: Bearer eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjMsInVzZXJuYW1lIjoiY2F0IiwiaWF0IjoxNzcyNjExODQyLCJleHAiOjE3ODEyNTE4NDJ9.giuM1EjkwoBjy1Yzl_kl1DE2Yp9CGn-vjQBDsrzCbVoTBRR9nv1IE1ludOWNM_gl

这是一个非常标准的**“全量更新”**流程。

既然你修改了数据库结构（Entity）、后端逻辑（Java）和前端界面（Vue），那么最稳妥的方法确实是**“推倒重来”**（保留服务器环境配置，只替换代码和数据）。

以下是为你定制的**《勤了吗·全量更新完全攻略》**，分为 **本地准备** 和 **服务器操作** 两个阶段。

---

### 第一阶段：本地工厂（打包新材料）

在你的 Windows 电脑上操作。

#### 1. 检查配置（非常重要！）
*   **后端**：打开 IDEA，检查 `src/main/resources/application.properties`。
    *   **数据库密码**：必须改成**服务器上的密码**（例如 `MyHardPass2026!`），不能是本地的密码。
    *   **数据库地址**：保持 `localhost`。
*   **前端**：打开 VS Code，检查 `src/App.vue`（或相关组件）。
    *   API 地址是否使用了自动判断逻辑 (`import.meta.env.PROD` ...)。

#### 2. 导出新数据库
1.  打开 **DataGrip** 或 Navicat。
2.  右键点击你本地最新的 `maicorder` 数据库 -> **导出/转储 SQL 文件**（包含结构+数据）。
3.  保存为 **`new_db.sql`**。

#### 3. 编译后端 JAR 包
1.  在 IDEA 右侧 **Maven** 面板。
2.  点 `Lifecycle` -> 双击 `clean`（清理旧的）。
3.  点 `Lifecycle` -> 双击 `package`（打包新的）。
4.  去项目目录 `target` 文件夹下找到新的 **`maicorder-0.0.1-SNAPSHOT.jar`**。

#### 4. 编译前端 Dist 包
1.  在 VS Code 终端运行：
    ```bash
    npm run build
    ```
2.  等待完成，确认根目录下生成了新的 **`dist`** 文件夹。

---

### 第二阶段：服务器施工（停机更新）

打开 **FinalShell** 连接到你的 RackNerd 服务器。

#### 步骤 1：暂停后端服务
先让网站“熄火”，防止文件被占用。

```bash
# 停止 Java 服务
systemctl stop qinlema

# 检查一下是不是真的停了（应该显示 inactive 或没有任何输出）
systemctl status qinlema
```

#### 步骤 2：重置数据库（删旧库，建新库）
我们要把服务器上的旧数据铲平。

1.  进入 MySQL：
    ```bash
    mysql -u root -p
    ```
    *(输入服务器数据库密码，回车)*

2.  **执行“毁灭”与“重生”命令**（一行行复制）：
    ```sql
    -- 1. 删除旧数据库（所有旧数据都会消失！）
    DROP DATABASE IF EXISTS maicorder;

    -- 2. 新建空数据库
    CREATE DATABASE maicorder DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

    -- 3. 退出
    exit;
    ```

#### 步骤 3：上传并导入新数据
1.  在 FinalShell 下方文件窗口，进入 `/root` 目录。
2.  把本地的 **`new_db.sql`** 拖进去。
3.  在命令行执行导入：
    ```bash
    # 格式：mysql -u root -p 数据库名 < sql文件路径
    mysql -u root -p maicorder < /root/new_db.sql
    ```
    *(输入密码回车，没报错就是成功)*

#### 步骤 4：替换后端 JAR 包
1.  在 FinalShell 文件窗口，依然在 `/root` 目录。
2.  **删除** 旧的 `maicorder-0.0.1-SNAPSHOT.jar`（右键删除，或命令 `rm maicorder-*.jar`）。
3.  把本地刚刚打包好的 **新 JAR 包** 拖进去上传。

#### 步骤 5：替换前端文件
1.  在命令行进入网站目录：
    ```bash
    cd /var/www/qinlema
    ```
2.  **清空旧文件**（小心操作，确保你在 `/var/www/qinlema` 下）：
    ```bash
    rm -rf *
    ```
3.  在 FinalShell 文件窗口，也跳转到 `/var/www/qinlema`。
4.  把本地 **`dist` 文件夹里的所有内容**（index.html, assets 等文件夹）拖进去。
    *   *注意：是把 dist 里面的东西拖进去，不是把 dist 文件夹拖进去。最终结构应该是 `/var/www/qinlema/index.html`。*

5.  **修复权限**（防止 403/500 错误）：
    ```bash
    chmod -R 755 /var/www/qinlema
    ```

#### 步骤 6：重新启动服务
一切就绪，重新点火。

1.  **启动后端**：
    ```bash
    systemctl start qinlema
    ```

2.  **检查后端是否启动成功**（这一步很关键）：
    ```bash
    # 查看实时日志，按 Ctrl+C 退出日志查看
    journalctl -u qinlema -f
    ```
    *如果你看到大大的 Spring 图标和 "Started MaicorderApplication"，说明后端活了。*

3.  **刷新 Nginx**（可选，清除缓存）：
    ```bash
    systemctl reload nginx
    ```

---

### 第三阶段：验证（验收成果）

1.  打开浏览器。
2.  **强制刷新**（清除浏览器缓存）：
    *   Windows: `Ctrl` + `F5`
    *   Mac: `Cmd` + `Shift` + `R`
3.  访问你的域名 `http://swan416.top`。
4.  测试登录、查看新功能。

**搞定！你的服务器现在运行的就是最新版本的“勤了吗”了！** 🎮
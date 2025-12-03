# ProxyForge

#### 介绍

ProxyForge 是一款基于 Java 技术栈的智能反向代理网关（镜像站）系统，旨在解决传统反向代理方案中的痛点：

- 手动维护 Nginx + Certbot 工作量大，证书易过期
- 目标网站内容无法按需改写，品牌合规困难
- 缺少可视化、插件化、零运维的私有化反向代理方案

**目标**：打造一款「自动 HTTPS + 内容改写 + 插件扩展 + 后台配置」的 Java 技术栈反向代理网关(镜像)，单 jar 即可运行，亦可 K8s 弹性伸缩。也可编译目标平台可执行文件运行。

## ✨ 核心功能

### 🔒 自动 HTTPS 证书管理
- 基于 ACME 协议自动申请和续期 SSL 证书
- 支持 SNI（Server Name Indication）动态加载证书
- 支持泛域名证书（通配符证书）
- 证书过期自动提醒和续期
- 证书存储在 `./Certificate/certs/` 目录

### 🔄 智能反向代理
- 基于 Undertow 高性能 Web 服务器
- 支持 HTTP/HTTPS 双向代理
- 支持 HTTP/2 协议
- 支持流式数据传输
- 自动请求转发和响应处理

### 🛡️ 设备指纹检测与访问控制
- **客户端指纹分析**：
  - 电池状态检测（满电正在充电）
  - GPU 信息检测
  - 平台信息检测
  - 自动化工具检测
  - 传感器检测（陀螺仪等）
- **IP 安全检测**：
  - 代理/VPN/Tor 检测
  - 威胁行为检测
  - 云服务提供商检测
  - 匿名访问检测
- **访问策略控制**：
  - PC/移动端/Android/iOS 访问控制
  - 国家/地区访问限制
  - IP 白名单管理

### 🔌 插件系统
- 支持 JAR 插件动态加载
- 插件热加载（无需重启服务）
- 插件生命周期管理（初始化、销毁）
- 插件回调机制（请求前/响应后）
- 插件代理信息回传
- 系统启动时自动加载所有插件

### 📝 内容改写
- 全局路径替换规则
- 自定义响应内容
- 支持多种内容类型（HTML、JSON、文本等）
- 动态内容注入（统计代码、错误页面等）

### 🎛️ 可视化后台管理
- 域名管理（添加、编辑、删除、查询）
- 站点配置管理
- 证书申请与管理
- 全局设置配置
- API 密钥管理
- IP 白名单管理
- DNS 查询工具
- 插件上传与管理

## 🏗️ 软件架构

### 技术栈
- **框架**: Spring Boot 3.5.7
- **Web 服务器**: Undertow（支持 HTTP/2）
- **数据库**: MySQL 8.4.0 + JPA/Hibernate
- **缓存**: Redis（配置缓存、Session 管理）
- **证书管理**: acme4j（ACME 协议）
- **HTTP 客户端**: Apache HttpClient（支持 HTTP/SOCKS5 代理）
- **其他**: FastJSON2、Jsoup、JWT、DNS Java

### 架构特点
1. **插件化架构**：支持插件热加载，扩展性强
2. **可视化配置**：Web 后台管理，零代码配置
3. **高性能**：基于 Undertow 异步非阻塞 I/O
4. **高可用**：支持 K8s 弹性伸缩

## 📦 安装教程

### 环境要求
- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 1. 克隆项目
```bash
git clone <repository-url>
cd proxy-forge
```

### 2. 配置数据库
编辑 `src/main/resources/application.properties`：
```properties
# MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/proxyforge?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_password
```

### 3. 创建数据库
```sql
CREATE DATABASE proxyforge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 编译打包
```bash
mvn clean package
```

### 5. 运行
```bash
java -jar target/ProxyForge-0.1.jar
```

### 6. 访问
- 管理后台：`http://your-server/pfadmin`
- 默认端口：80（HTTP）、443（HTTPS）

## 📖 使用说明

### 基本使用流程

1. **登录管理后台**
   - 访问 `/pfadmin/login` 接口进行登录
   - 首次运行会自动创建默认管理员账户

2. **添加域名**
   - 在管理后台添加需要代理的域名
   - 配置域名状态和 SSL 证书选项

3. **申请 SSL 证书**
   - 使用 `/pfadmin/certrequest` 接口申请证书
   - 支持 HTTP-01 和 DNS-01 验证方式
   - 证书自动存储在 `./Certificate/certs/` 目录

4. **配置站点**
   - 添加反向代理站点配置
   - 设置目标 URL、访问策略、设备检测规则等
   - 配置站点状态（running/stopped/maintenance）

5. **配置 DNS**
   - 将域名 A 记录指向服务器 IP
   - 等待 DNS 解析生效

6. **访问测试**
   - 通过配置的域名访问，系统会自动转发到目标站点

### 插件开发

#### 插件接口
插件需要实现 `ISitePlugin` 接口（接口定义由主框架提供）：

```java
public interface ISitePlugin {
    String getPluginName();
    String getPluginVersion();
    void init(WebSite webSite);
    void destroy();
    
    Object beforeRequest(String tk, String serverName, String clientIp,
                         HttpServletRequest request, HttpServletResponse response,
                         WebSite webSite, String[] proxyStr);
    
    Object afterResponse(String tk, String serverName, String clientIp,
                        HttpServletRequest request, HttpServletResponse response,
                        WebSite webSite, String[] proxyStr);
}
```

#### 插件开发注意事项

1. **依赖管理**
   - 插件依赖如果与主项目冲突，请在插件的 `pom.xml` 中使用 `<scope>provided</scope>`
   - 主框架提供的依赖（如 Spring、FastJSON 等）应使用 `provided` 作用域

2. **插件主类指定**
   - 方式一：在 JAR 的 `MANIFEST.MF` 中设置 `Plugin-Main-Class: com.example.MyPlugin`
   - 方式二：确保主类名与插件名称匹配（如插件名为 `MyPlugin`，则类名应为 `MyPlugin` 或 `com.proxy.forge.plugin.impl.MyPlugin`）

3. **插件回调说明**
   - `beforeRequest`: 在请求转发到目标服务器之前调用，可以修改请求参数、设置代理等
   - `afterResponse`: 在接收到目标服务器响应后调用，可以修改响应内容
   - 返回值：返回 `null` 表示继续后续处理，返回非 `null` 对象会作为响应返回给客户端
   - `proxyStr`: 代理信息数组（长度为1），插件可以通过修改 `proxyStr[0]` 来设置代理（格式：`http://host:port` 或 `socks5://host:port`），设置为 `null` 则不使用代理

4. **插件打包**
   ```bash
   mvn clean package
   # 将生成的 JAR 文件上传到服务器
   ```

5. **插件上传和加载**
   - 通过管理后台 `/pfadmin/uploadPlugin` 接口上传插件 JAR 文件
   - 在站点配置中设置插件路径、名称、版本
   - 系统启动时自动加载所有插件
   - 支持热加载（修改插件后重新加载，无需重启服务）

### 全局替换规则

通过管理后台配置全局路径替换规则：
- 设置匹配路径（支持路径匹配）
- 设置响应内容类型
- 设置响应内容

### IP 白名单

- 添加 IP 到白名单，白名单 IP 可绕过设备检测和访问策略
- 支持设置白名单过期时间
- 支持备注信息

### API 密钥管理

- 创建和管理 API 密钥
- 用于 API 接口认证

## 🔧 配置说明

### 主要配置项

```properties
# 服务端口
server.port=80

# Session 超时时间（秒）
server.servlet.session.timeout=86400

# 文件上传大小限制
spring.servlet.multipart.max-file-size=500MB
spring.servlet.multipart.max-request-size=500MB

# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_password

# MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/proxyforge
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
```

### 目录结构

```
proxy-forge/
├── Certificate/          # SSL 证书存储目录
│   └── certs/            # 各域名的证书文件
├── plugins/              # 插件存储目录
├── logs/                 # 日志文件目录
├── src/
│   └── main/
│       ├── java/         # Java 源代码
│       └── resources/    # 配置文件
└── target/               # 编译输出目录
```

## 🚀 部署说明

### 单机部署
```bash
java -jar ProxyForge-0.1.jar
```

### Docker 部署
```dockerfile
FROM openjdk:17-jre-slim
COPY target/ProxyForge-0.1.jar app.jar
EXPOSE 80 443
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### K8s 部署
- 支持水平扩展
- 建议使用 StatefulSet 部署
- 共享 Redis 和 MySQL 数据库

## 📝 API 接口

### 管理后台接口（/pfadmin）

- `POST /pfadmin/login` - 管理员登录
- `POST /pfadmin/certrequest` - 申请 SSL 证书
- `POST /pfadmin/certcheck` - 检查证书申请状态
- `POST /pfadmin/savedomain` - 保存域名
- `POST /pfadmin/domainlist` - 获取域名列表
- `POST /pfadmin/saveSite` - 保存站点配置
- `POST /pfadmin/webSiteList` - 获取站点列表
- `POST /pfadmin/uploadPlugin` - 上传插件
- `POST /pfadmin/saveGlobalReplace` - 保存全局替换规则
- `POST /pfadmin/saveWhiteList` - 添加 IP 白名单
- 更多接口请查看 `ManagerController.java`

### 代理接口

- `POST /check` - 设备指纹检查
- `GET /sr` - 启动请求
- `GET /**` - 代理所有请求

## ⚠️ 注意事项

1. **证书管理**
   - 证书存储在 `./Certificate/certs/` 目录，请定期备份
   - 证书过期前 10 天会自动提醒

2. **插件安全**
   - 只加载可信来源的插件
   - 插件具有完整的请求/响应访问权限，请谨慎使用

3. **性能优化**
   - 建议使用 Redis 集群提高缓存性能
   - 建议使用 MySQL 主从复制提高数据库性能
   - 建议使用 CDN 加速静态资源

4. **安全建议**
   - 修改默认管理员密码
   - 使用强密码策略
   - 定期更新依赖库
   - 配置防火墙规则

## 🤝 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 📄 许可证

    GPL-3.0

## 📧 联系方式

- 作者：Ts
- 微信：Q_Q-1992

---

**ProxyForge** - 让反向代理更简单、更智能、更强大！

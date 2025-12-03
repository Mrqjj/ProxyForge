# ProxyForge

#### Description

ProxyForge is an intelligent reverse proxy gateway (mirror site) system based on Java technology stack, designed to solve the pain points of traditional reverse proxy solutions:

- Manual maintenance of Nginx + Certbot is labor-intensive, and certificates are prone to expiration
- Target website content cannot be rewritten on demand, making brand compliance difficult
- Lack of visual, plugin-based, zero-maintenance private reverse proxy solutions

**Goal**: Build a Java technology stack reverse proxy gateway (mirror) with "Automatic HTTPS + Content Rewriting + Plugin Extension + Backend Configuration", which can run as a single JAR file and also supports K8s elastic scaling. Can also be compiled into executable files for target platforms.

## ✨ Core Features

### 🔒 Automatic HTTPS Certificate Management
- Automatically apply and renew SSL certificates based on ACME protocol
- Support SNI (Server Name Indication) dynamic certificate loading
- Support wildcard certificates
- Automatic certificate expiration reminders and renewal
- Certificates stored in `./Certificate/certs/` directory

### 🔄 Intelligent Reverse Proxy
- High-performance Web server based on Undertow
- Support HTTP/HTTPS bidirectional proxy
- Support HTTP/2 protocol
- Support streaming data transmission
- Automatic request forwarding and response processing

### 🛡️ Device Fingerprint Detection & Access Control
- **Client Fingerprint Analysis**:
  - Battery status detection (fully charged and charging)
  - GPU information detection
  - Platform information detection
  - Automation tool detection
  - Sensor detection (gyroscope, etc.)
- **IP Security Detection**:
  - Proxy/VPN/Tor detection
  - Threat behavior detection
  - Cloud service provider detection
  - Anonymous access detection
- **Access Policy Control**:
  - PC/Mobile/Android/iOS access control
  - Country/Region access restrictions
  - IP whitelist management

### 🔌 Plugin System
- Support JAR plugin dynamic loading
- Plugin hot reload (no service restart required)
- Plugin lifecycle management (initialization, destruction)
- Plugin callback mechanism (before request/after response)
- Plugin proxy information callback
- Automatically load all plugins on system startup

### 📝 Content Rewriting
- Global path replacement rules
- Custom response content
- Support multiple content types (HTML, JSON, text, etc.)
- Dynamic content injection (analytics code, error pages, etc.)

### 🎛️ Visual Backend Management
- Domain management (add, edit, delete, query)
- Site configuration management
- Certificate application and management
- Global settings configuration
- API key management
- IP whitelist management
- DNS query tool
- Plugin upload and management

## 🏗️ Software Architecture

### Technology Stack
- **Framework**: Spring Boot 3.5.7
- **Web Server**: Undertow (supports HTTP/2)
- **Database**: MySQL 8.4.0 + JPA/Hibernate
- **Cache**: Redis (configuration cache, Session management)
- **Certificate Management**: acme4j (ACME protocol)
- **HTTP Client**: Apache HttpClient (supports HTTP/SOCKS5 proxy)
- **Others**: FastJSON2, Jsoup, JWT, DNS Java

### Architecture Features
1. **Plugin Architecture**: Supports plugin hot reload, highly extensible
2. **Visual Configuration**: Web backend management, zero-code configuration
3. **High Performance**: Based on Undertow asynchronous non-blocking I/O
4. **High Availability**: Supports K8s elastic scaling

## 📦 Installation

### Requirements
- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 1. Clone Project
```bash
git clone <repository-url>
cd proxy-forge
unzip IP2LOCATION-LITE-DB11.IPV6.BIN.zip -d ip2location
```

### 2. Configure Database
Edit `src/main/resources/application.properties`:
```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/proxyforge?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_password
```

### 3. Create Database
```sql
CREATE DATABASE proxyforge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. Build Package
```bash
mvn clean package
```

### 5. Run
```bash
java -jar target/ProxyForge-0.1.jar
```

### 6. Access
- Management Backend: `http://your-server/admin/`
- Default Port: 80 (HTTP), 443 (HTTPS)

## 📖 Usage

### Basic Usage Flow

1. **Login to Management Backend**
   - Access `/pfadmin/login` endpoint to login
   - Default admin account will be created automatically on first run

2. **Add Domain**
   - Add domains that need to be proxied in the management backend
   - Configure domain status and SSL certificate options

3. **Apply SSL Certificate**
   - Use `/pfadmin/certrequest` endpoint to apply for certificates
   - Supports HTTP-01 and DNS-01 validation methods
   - Certificates are automatically stored in `./Certificate/certs/` directory

4. **Configure Site**
   - Add reverse proxy site configuration
   - Set target URL, access policies, device detection rules, etc.
   - Configure site status (running/stopped/maintenance)

5. **Configure DNS**
   - Point domain A record to server IP
   - Wait for DNS resolution to take effect

6. **Access Test**
   - Access through configured domain, system will automatically forward to target site

### Plugin Development

#### Plugin Interface
Plugins need to implement the `ISitePlugin` interface (interface definition provided by main framework):

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

#### Plugin Development Notes

1. **Dependency Management**
   - If plugin dependencies conflict with main project, use `<scope>provided</scope>` in plugin's `pom.xml`
   - Dependencies provided by main framework (such as Spring, FastJSON, etc.) should use `provided` scope

2. **Plugin Main Class Specification**
   - Method 1: Set `Plugin-Main-Class: com.example.MyPlugin` in JAR's `MANIFEST.MF`
   - Method 2: Ensure main class name matches plugin name (e.g., if plugin name is `MyPlugin`, class name should be `MyPlugin` or `com.proxy.forge.plugin.impl.MyPlugin`)

3. **Plugin Callback Notes**
   - `beforeRequest`: Called before request is forwarded to target server, can modify request parameters, set proxy, etc.
   - `afterResponse`: Called after receiving target server response, can modify response content
   - Return value: Return `null` to continue subsequent processing, return non-null object will be used as response to client
   - `proxyStr`: Proxy information array (length 1), plugin can set proxy by modifying `proxyStr[0]` (format: `http://host:port` or `socks5://host:port`), set to `null` to not use proxy

4. **Plugin Packaging**
   ```bash
   mvn clean package
   # Upload generated JAR file to server
   ```

5. **Plugin Upload and Loading**
   - Upload plugin JAR file through management backend `/pfadmin/uploadPlugin` endpoint
   - Set plugin path, name, version in site configuration
   - System automatically loads all plugins on startup
   - Supports hot reload (reload after modifying plugin, no service restart required)

## 🔧 Configuration

### Main Configuration Items

```properties
# Service Port
server.port=80

# Session Timeout (seconds)
server.servlet.session.timeout=86400

# File Upload Size Limit
spring.servlet.multipart.max-file-size=500MB
spring.servlet.multipart.max-request-size=500MB

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_password

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/proxyforge
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
```

### Directory Structure

```
proxy-forge/
├── Certificate/          # SSL certificate storage directory
│   └── certs/           # Certificate files for each domain
├── plugins/             # Plugin storage directory
├── logs/                # Log file directory
├── src/
│   └── main/
│       ├── java/        # Java source code
│       └── resources/   # Configuration files
└── target/              # Compilation output directory
```

## 🚀 Deployment

### Standalone Deployment
```bash
java -jar ProxyForge-0.1.jar
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jre-slim
COPY target/ProxyForge-0.1.jar app.jar
EXPOSE 80 443
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### K8s Deployment
- Supports horizontal scaling
- Recommended to use StatefulSet for deployment
- Share Redis and MySQL database

## 📝 API Endpoints

### Management Backend Endpoints (/pfadmin)

- `POST /pfadmin/login` - Administrator login
- `POST /pfadmin/certrequest` - Apply for SSL certificate
- `POST /pfadmin/certcheck` - Check certificate application status
- `POST /pfadmin/savedomain` - Save domain
- `POST /pfadmin/domainlist` - Get domain list
- `POST /pfadmin/saveSite` - Save site configuration
- `POST /pfadmin/webSiteList` - Get site list
- `POST /pfadmin/uploadPlugin` - Upload plugin
- `POST /pfadmin/saveGlobalReplace` - Save global replacement rule
- `POST /pfadmin/saveWhiteList` - Add IP whitelist
- More endpoints please refer to `ManagerController.java`

### Proxy Endpoints

- `POST /check` - Device fingerprint check
- `GET /sr` - Start request
- `GET /**` - Proxy all requests

## ⚠️ Notes

1. **Certificate Management**
   - Certificates stored in `./Certificate/certs/` directory, please backup regularly
   - Automatic reminder 10 days before certificate expiration

2. **Plugin Security**
   - Only load plugins from trusted sources
   - Plugins have full access to requests/responses, use with caution

3. **Performance Optimization**
   - Recommend using Redis cluster to improve cache performance
   - Recommend using MySQL master-slave replication to improve database performance
   - Recommend using CDN to accelerate static resources

4. **Security Recommendations**
   - Change default administrator password
   - Use strong password policy
   - Regularly update dependency libraries
   - Configure firewall rules

## 🤝 Contributing

1. Fork the repository
2. Create Feat_xxx branch
3. Commit your code
4. Create Pull Request

## 📄 License

    GPL-3.0

## 📧 Contact

- Author: Ts
- WeChat: Q_Q-1992

---

**ProxyForge** - Make reverse proxy simpler, smarter, and more powerful!

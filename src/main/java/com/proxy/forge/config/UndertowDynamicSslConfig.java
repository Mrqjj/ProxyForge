package com.proxy.forge.config;

import com.google.common.net.InternetDomainName;
import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Undertow 动态 SSL 配置
 * 支持根据请求域名自动加载 ssl/ 下的对应证书 (SNI)
 */
@Configuration
public class UndertowDynamicSslConfig {

    // 证目录
    private static final String SSL_DIR = "ssl";
    // 证书密码
    private static final String SSL_PASSWORD = "xiaoxiong";
    private static final Map<String, DomainKeyManager> KEY_MANAGER_CACHE = new ConcurrentHashMap<>();

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowSslFactoryCustomizer() {
        return factory -> factory.addBuilderCustomizers(builder -> {
            try {
                // 允许特殊字符
                builder.setServerOption(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, true);
                // 关闭自动解码
                builder.setServerOption(UndertowOptions.DECODE_URL, false);
                // 设置请求头最大长度（默认 8KB）
                builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, 64 * 1024); // 64KB
                // 设置请求行最大长度（默认 4096）
                builder.setServerOption(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 64 * 1024); // 64KB

                // 创建支持 SNI 的动态 SSLContext
                SSLContext sslContext = createDynamicSSLContext();
                builder.addHttpsListener(443, "0.0.0.0", sslContext);
                builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                System.out.println("✅ Undertow Dynamic SSL started on port 443");
            } catch (Exception e) {
                System.err.println("❌ 创建 Undertow SSLContext 失败: " + e.getMessage());
                throw new RuntimeException("创建 Undertow SSLContext 失败", e);
            }
        });
    }

    /**
     * 创建支持 SNI 的动态 SSLContext
     */
    private SSLContext createDynamicSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        // 初始化默认 KeyManager
        DomainKeyManager defaultKeyManager = loadKeyManager("default");

        // 使用动态 KeyManager
        DynamicKeyManager keyManager = new DynamicKeyManager(defaultKeyManager);
        sslContext.init(new KeyManager[]{keyManager}, null, null);

        return sslContext;
    }

    /**
     * 加载指定域名的 KeyManager
     */
    private static DomainKeyManager loadKeyManager(String domain) {
        return KEY_MANAGER_CACHE.computeIfAbsent(domain, d -> {
            try {
                String rootCert = "default";
                InternetDomainName rootDomain = InternetDomainName.from(d);
                if (rootDomain.isUnderPublicSuffix()) {
                    rootCert = "_." + rootDomain.topPrivateDomain();
                }
                //先加载 泛解析证书
                String certPath = SSL_DIR + "/" + rootCert + ".p12";
                InputStream inputStream = tryLoadResource(certPath);
                if (inputStream == null) {
                    System.out.println("⚠️ 泛解析证书 未找到证书文件: " + certPath);
                    certPath = SSL_DIR + "/" + d + ".p12";
                    inputStream = tryLoadResource(certPath);
                    if (inputStream == null) {
                        System.out.println("⚠️ ["+d+"]解析证书 未找到证书文件: " + certPath);
                        return null;
                    }
                }

                KeyStore ks = KeyStore.getInstance("PKCS12");
                ks.load(inputStream, SSL_PASSWORD.toCharArray());
                inputStream.close();

                // 获取第一个别名
                String alias = ks.aliases().nextElement();
                PrivateKey privateKey = (PrivateKey) ks.getKey(alias, SSL_PASSWORD.toCharArray());
                X509Certificate[] certificateChain = Arrays.stream(ks.getCertificateChain(alias))
                        .toArray(X509Certificate[]::new);

                // -------- 🔍 检查证书有效期 -----------
                X509Certificate cert = certificateChain[0];
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date notBefore = cert.getNotBefore();
                Date notAfter = cert.getNotAfter();

                long diffMs = notAfter.getTime() - now.getTime();
                long remainDays = diffMs / 1000 / 3600 / 24;

                System.out.println("📜 证书信息: " + certPath);
                System.out.println("   生效日期: " + sdf.format(notBefore));
                System.out.println("   过期日期: " + sdf.format(notAfter));
                System.out.println("   剩余天数: " + remainDays);
//                if (now.before(notBefore)) {
//                    System.err.println("❌ 证书尚未生效，拒绝加载: " + certPath);
//                    return null;
//                }
//                if (now.after(notAfter)) {
//                    System.err.println("❌ 证书已过期，拒绝加载: " + certPath);
//                    return null;
//                }
                // 提醒即将过期的证书,这里可以自动化续签证书,  发送webhook 报警
                if (remainDays <= 10) {
                    System.err.println("⚠️ 警告：证书将在 " + remainDays + " 天后过期: " + certPath);
                }

                System.out.println("✅ 已加载域名证书: " + certPath + " (别名: " + alias + ")");

                return new DomainKeyManager(alias, privateKey, certificateChain);

            } catch (Exception e) {
                System.err.println("❌ 加载证书失败 " + domain + ": " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * 尝试优先从外部文件加载，否则从 classpath 加载
     */
    private static InputStream tryLoadResource(String path) throws IOException {
        // 优先检查外部文件系统路径
        File external = new File(path);
        if (external.exists() && external.isFile()) {
            System.out.println("🔹 从外部文件加载证书: " + external.getAbsolutePath());
            return new FileInputStream(external);
        }

        // 其次从 classpath 加载
        InputStream classpathStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (classpathStream != null) {
            System.out.println("🔹 从 classpath 加载证书: " + path);
        }
        return classpathStream;
    }

    /**
     * 域名证书管理器
     */
    static class DomainKeyManager {
        private final String alias;
        private final PrivateKey privateKey;
        private final X509Certificate[] certificateChain;

        public DomainKeyManager(String alias, PrivateKey privateKey, X509Certificate[] certificateChain) {
            this.alias = alias;
            this.privateKey = privateKey;
            this.certificateChain = certificateChain;
        }

        public String getAlias() {
            return alias;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public X509Certificate[] getCertificateChain() {
            return certificateChain;
        }
    }

    /**
     * 动态 KeyManager，根据域名自动切换证书
     */
    static class DynamicKeyManager extends X509ExtendedKeyManager {
        private final DomainKeyManager defaultKeyManager;

        DynamicKeyManager(DomainKeyManager defaultKeyManager) {
            this.defaultKeyManager = defaultKeyManager;
        }

        @Override
        public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
            String hostname = extractHostnameFromSNI(engine);

            if (hostname != null) {
                System.out.println("🔍 SNI 请求域名: " + hostname);
                DomainKeyManager domainKeyManager = loadKeyManager(hostname);
                if (domainKeyManager != null) {
                    return domainKeyManager.getAlias();
                }
            }

            // 使用默认证书
            if (defaultKeyManager != null) {
                System.out.println("🔍 使用默认证书");
                return defaultKeyManager.getAlias();
            }

            System.err.println("❌ 未找到匹配的证书");
            return null;
        }

        /**
         * 从 SNI 扩展中提取主机名
         */
        private String extractHostnameFromSNI(SSLEngine engine) {
            try {
                SSLSession session = engine.getHandshakeSession();
                if (session instanceof ExtendedSSLSession) {
                    List<SNIServerName> serverNames = ((ExtendedSSLSession) session).getRequestedServerNames();
                    if (serverNames != null && !serverNames.isEmpty()) {
                        for (SNIServerName serverName : serverNames) {
                            if (serverName.getType() == StandardConstants.SNI_HOST_NAME) {
                                return ((SNIHostName) serverName).getAsciiName();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ 提取 SNI 主机名失败: " + e.getMessage());
            }
            return null;
        }

        @Override
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            List<String> aliases = new ArrayList<>();
            if (defaultKeyManager != null) {
                aliases.add(defaultKeyManager.getAlias());
            }
            aliases.addAll(KEY_MANAGER_CACHE.keySet());
            return aliases.toArray(new String[0]);
        }

        @Override
        public X509Certificate[] getCertificateChain(String alias) {
            if (alias != null) {
                // 查找匹配的 KeyManager
                for (DomainKeyManager keyManager : KEY_MANAGER_CACHE.values()) {
                    if (alias.equals(keyManager.getAlias())) {
                        return keyManager.getCertificateChain();
                    }
                }
                // 检查默认证书
                if (defaultKeyManager != null && alias.equals(defaultKeyManager.getAlias())) {
                    return defaultKeyManager.getCertificateChain();
                }
            }
            System.err.println("❌ 未找到证书链，别名: " + alias);
            return null;
        }

        @Override
        public PrivateKey getPrivateKey(String alias) {
            if (alias != null) {
                // 查找匹配的 KeyManager
                for (DomainKeyManager keyManager : KEY_MANAGER_CACHE.values()) {
                    if (alias.equals(keyManager.getAlias())) {
                        return keyManager.getPrivateKey();
                    }
                }
                // 检查默认证书
                if (defaultKeyManager != null && alias.equals(defaultKeyManager.getAlias())) {
                    return defaultKeyManager.getPrivateKey();
                }
            }
            System.err.println("❌ 未找到私钥，别名: " + alias);
            return null;
        }

        // 客户端相关方法（服务器端不需要）
        @Override
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return null;
        }

        @Override
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return null;
        }

        @Override
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return chooseEngineServerAlias(keyType, issuers, null);
        }
    }
}
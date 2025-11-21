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
 * Undertow 动态 SSL 配置，支持 SNI + 热加载 + 泛域名
 */
@Configuration
public class UndertowDynamicSslConfig {

    private static final String SSL_DIR = "./Certificate/certs/";
    private static final String SSL_PASSWORD = "xiaoxiong";

    private static final Map<String, DomainKeyManager> KEY_MANAGER_CACHE = new ConcurrentHashMap<>();

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowSslFactoryCustomizer() {
        return factory -> factory.addBuilderCustomizers(builder -> {
            try {
                builder.setServerOption(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, true);
                builder.setServerOption(UndertowOptions.DECODE_URL, false);
                builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, 64 * 1024);
                builder.setServerOption(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 64 * 1024);

                SSLContext sslContext = createDynamicSSLContext();
                builder.addHttpsListener(443, "0.0.0.0", sslContext);
                builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                System.out.println("✅ Undertow Dynamic SSL started on port 443");
            } catch (Exception e) {
                throw new RuntimeException("创建 Undertow SSLContext 失败", e);
            }
        });
    }

    private SSLContext createDynamicSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        DomainKeyManager defaultKeyManager = loadKeyManager("default");
        DynamicKeyManager keyManager = new DynamicKeyManager(defaultKeyManager);
        sslContext.init(new KeyManager[]{keyManager}, null, null);
        return sslContext;
    }

    private static DomainKeyManager loadKeyManager(String domain) {
        try {
            File certFile = findCertFile(domain);
            if (certFile == null) return null;
            // 这里重新加载证书，如果证书文件创建的时间有变化
            DomainKeyManager cached = KEY_MANAGER_CACHE.get(domain);
            if (cached != null && cached.getLastModified() == certFile.lastModified()) {
                return cached;
            }

            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream in = new FileInputStream(certFile)) {
                ks.load(in, SSL_PASSWORD.toCharArray());
            }

            String alias = ks.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, SSL_PASSWORD.toCharArray());
            X509Certificate[] chain = Arrays.stream(ks.getCertificateChain(alias))
                    .toArray(X509Certificate[]::new);
            checkCertificateValidity(domain, alias, certFile.getName(), chain[0]);

            DomainKeyManager km = new DomainKeyManager(alias, privateKey, chain, certFile.lastModified());
            KEY_MANAGER_CACHE.put(domain, km);
            return km;
        } catch (Exception e) {
            System.err.println("❌ 加载证书失败 " + domain + ": " + e.getMessage());
            return null;
        }
    }

    private static void checkCertificateValidity(String domain, String alias, String name, X509Certificate cert) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long remainDays = (cert.getNotAfter().getTime() - now.getTime()) / 1000 / 3600 / 24;
        System.out.println("📜 证书信息: " + name);
        System.out.println("   生效日期: " + sdf.format(cert.getNotBefore()));
        System.out.println("   过期日期: " + sdf.format(cert.getNotAfter()));
        System.out.println("   剩余天数: " + remainDays);
        System.out.println("📡 请求域名: " + domain);
        System.out.println("☃️   Alias: " + alias);
        if (remainDays <= 10) {
            // 这里应该推送任务, 开始申请证书
            System.err.println("⚠️ 警告：证书将在 " + remainDays + " 天后过期: " + name);
        }
    }

    private static File findCertFile(String domain) {
        List<String> candidates = new ArrayList<>();
        candidates.add(SSL_DIR + domain + "/" + domain + ".p12");

        // 泛域名 _domain.com
        try {
            InternetDomainName idn = InternetDomainName.from(domain);
            if (idn.isUnderPublicSuffix()) {
                candidates.add(SSL_DIR + "_." + idn.topPrivateDomain() + "/" + "_." + idn.topPrivateDomain() + ".p12");
            }
        } catch (Exception ignored) {
        }

        for (String path : candidates) {
            File f = new File(path);
            if (f.exists() && f.isFile()) return f;
        }
        return null;
    }

    static class DomainKeyManager {
        private final String alias;
        private final PrivateKey privateKey;
        private final X509Certificate[] certificateChain;
        private final long lastModified;

        public DomainKeyManager(String alias, PrivateKey privateKey, X509Certificate[] certificateChain, long lastModified) {
            this.alias = alias;
            this.privateKey = privateKey;
            this.certificateChain = certificateChain;
            this.lastModified = lastModified;
        }

        public long getLastModified() {
            return lastModified;
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

    static class DynamicKeyManager extends X509ExtendedKeyManager {
        private final DomainKeyManager defaultKeyManager;

        DynamicKeyManager(DomainKeyManager defaultKeyManager) {
            this.defaultKeyManager = defaultKeyManager;
        }

        @Override
        public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
            String hostname = extractHostnameFromSNI(engine);
            if (hostname != null) {
                DomainKeyManager km = loadKeyManager(hostname);
                if (km != null) return km.getAlias();
            }
            return defaultKeyManager != null ? defaultKeyManager.getAlias() : null;
        }

        private String extractHostnameFromSNI(SSLEngine engine) {
            try {
                SSLSession session = engine.getHandshakeSession();
                if (session instanceof ExtendedSSLSession) {
                    List<SNIServerName> serverNames = ((ExtendedSSLSession) session).getRequestedServerNames();
                    if (serverNames != null) {
                        for (SNIServerName sn : serverNames) {
                            if (sn.getType() == StandardConstants.SNI_HOST_NAME) {
                                return ((SNIHostName) sn).getAsciiName();
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
        public X509Certificate[] getCertificateChain(String alias) {
            DomainKeyManager km = findKeyManagerByAlias(alias);
            return km != null ? km.getCertificateChain() : null;
        }

        @Override
        public PrivateKey getPrivateKey(String alias) {
            DomainKeyManager km = findKeyManagerByAlias(alias);
            return km != null ? km.getPrivateKey() : null;
        }

        private DomainKeyManager findKeyManagerByAlias(String alias) {
            if (alias == null) return null;
            for (DomainKeyManager km : KEY_MANAGER_CACHE.values()) {
                if (alias.equals(km.getAlias())) return km;
            }
            if (defaultKeyManager != null && alias.equals(defaultKeyManager.getAlias())) return defaultKeyManager;
            return null;
        }

        // 客户端方法无需实现
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

        @Override
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            List<String> aliases = new ArrayList<>();
            if (defaultKeyManager != null) aliases.add(defaultKeyManager.getAlias());
            aliases.addAll(KEY_MANAGER_CACHE.keySet());
            return aliases.toArray(new String[0]);
        }
    }
}
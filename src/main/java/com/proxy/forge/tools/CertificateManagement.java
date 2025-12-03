package com.proxy.forge.tools;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.*;
import java.util.stream.Collectors;

public class CertificateManagement {

    public enum AuthType {
        HTTP("HTTP"), DNS("DNS");
        private final String code;

        AuthType(String code) {
            this.code = code;
        }

        public static AuthType fromCode(String code) {
            for (AuthType t : values()) {
                if (t.code.equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Invalid method code: " + code);
        }
    }

    private static final URI LETS_ENCRYPT_SERVER = URI.create("https://acme-v02.api.letsencrypt.org/directory");
    private static final File CERT_DIR = new File("./Certificate/");

    public static class OrderResult {
        public String taskId;
        public String domain;
        public Order order;
        public KeyPair accountKey;
        public KeyPair domainKey;
        public Authorization auth;
        public Http01Challenge httpChallenge;
        public Dns01Challenge dnsChallenge;
        public String token;         // HTTP-01 或 DNS-01 验证使用
        public String authorization; // HTTP-01 文件内容 或 DNS-01 TXT 值
    }

    public static class CheckResult {
        public boolean done;
        public boolean success;
        public String message;
        public Path p12Path;
    }

    public static synchronized OrderResult createOrder(String domain, AuthType type) throws Exception {
        if (!CERT_DIR.exists()) CERT_DIR.mkdirs();
        File accountKeyFile = new File(CERT_DIR, "account.key");
        KeyPair accountKey;
        if (accountKeyFile.exists()) {
            try (FileReader fr = new FileReader(accountKeyFile)) {
                accountKey = KeyPairUtils.readKeyPair(fr);
            }
        } else {
            accountKey = KeyPairUtils.createKeyPair(2048);
            try (FileWriter fw = new FileWriter(accountKeyFile)) {
                KeyPairUtils.writeKeyPair(accountKey, fw);
            }
        }

        Session session = new Session(LETS_ENCRYPT_SERVER);
        Account account = new AccountBuilder().agreeToTermsOfService().useKeyPair(accountKey).create(session);
        // 4️⃣ 输出账户 URI
        System.out.println("🎯 Account URI: " + account.getLocation());

        File domainKeyFile = generateDomainKey(domain, CERT_DIR.getPath() + "/certs/");
        KeyPair domainKey;
        try (FileReader fr = new FileReader(domainKeyFile)) {
            domainKey = KeyPairUtils.readKeyPair(fr);
        }

        Order order = account.newOrder().domains(domain.split(",")).create();
        System.out.println("📦 创建订单: " + order.getLocation());
        for (Authorization auth : order.getAuthorizations()) {
            OrderResult result = new OrderResult();
            result.domain = domain;
            result.order = order;
            result.accountKey = accountKey;
            result.domainKey = domainKey;
            result.auth = auth;
            result.taskId = UUID.randomUUID().toString();

            if (type == AuthType.HTTP) {
                Http01Challenge c = auth.findChallenge(Http01Challenge.TYPE)
                        .map(x -> (Http01Challenge) x)
                        .orElse(null);
                result.httpChallenge = c;
                if (c != null) {
                    // 成功拿到 HTTP-01 挑战
                    System.out.println("Token: " + c.getToken());
                    System.out.println("Authorization: " + c.getAuthorization());
                    System.out.println("🧩 验证路径: http://" + domain + "/.well-known/acme-challenge/" + c.getToken());
                    System.out.println("🧩 验证内容: " + c.getAuthorization());
                    result.token = c.getToken();              // 新增 token 字段
                    result.authorization = c.getAuthorization(); // 新增 authorization 字段
                } else {
                    System.err.println("No HTTP-01 challenge found.");
                }
            } else {
                Dns01Challenge c = auth.findChallenge(Dns01Challenge.TYPE)
                        .map(x -> (Dns01Challenge) x)
                        .orElse(null);
                result.dnsChallenge = c;
                if (domain.startsWith("*.")) domain = domain.substring(2);
                if (c != null) {
                    result.token = "_acme-challenge." + domain; // DNS-01 token 对应 TXT 记录名
                    result.authorization = c.getDigest();       // DNS-01 验证值
                }
            }
            TASK_CACHE.put(result.token, result);
            return result;
        }
        return null;
    }

    private static final Map<String, OrderResult> TASK_CACHE = new HashMap<>();

    public static synchronized CheckResult checkOrder(String token) throws Exception {
        OrderResult r = TASK_CACHE.get(token);
        String safeDomain = r.domain.replaceAll("[^a-zA-Z0-9.-]", "_");
        if (r == null) {
            CheckResult cr = new CheckResult();
            cr.done = true;
            cr.success = false;
            cr.message = "Task not found";
            return cr;
        }

        r.auth.update();
        if (r.auth.getStatus() == Status.PENDING) {
            if (r.httpChallenge != null) r.httpChallenge.trigger();
            if (r.dnsChallenge != null) r.dnsChallenge.trigger();
            CheckResult cr = new CheckResult();
            cr.done = false;
            cr.success = false;
            cr.message = "Waiting challenge validation";
            return cr;
        }

        if (r.auth.getStatus() == Status.INVALID) {
            System.out.println("验证状态: " + r.auth.getStatus());
            CheckResult cr = new CheckResult();
            cr.done = true;
            cr.success = false;
            cr.message = "Authorization failed";
            return cr;
        }

        if (r.auth.getStatus() != Status.VALID) {
            System.out.println("验证状态: " + r.auth.getStatus());
            CheckResult cr = new CheckResult();
            cr.done = false;
            cr.success = false;
            cr.message = "Authorization in progress";
            return cr;
        }
        System.out.println("✅ 域名验证通过");
        CSRBuilder csr = new CSRBuilder();
        csr.addDomain(r.domain);
        csr.sign(r.domainKey);
        r.order.execute(csr.getEncoded());

        System.out.println("证书状态: " + r.order.getStatus());
        if (r.order.getStatus() != Status.VALID) {
            CheckResult cr = new CheckResult();
            cr.done = false;
            cr.success = false;
            cr.message = "Order not yet valid";
            return cr;
        }
        Certificate cert = r.order.getCertificate();
        System.out.println("🎯 获取证书成功: " + cert.getLocation());


        File outDir = new File(CERT_DIR + "/certs/" + safeDomain);
        outDir.mkdirs();

        File chainFile = new File(outDir, "fullchain.pem");
        try (FileWriter fw = new FileWriter(chainFile)) {
            cert.writeCertificate(fw);
        }
        System.out.println("💾 证书保存成功: " + CERT_DIR + "/certs/" + safeDomain + "/" + "fullchain.pem");

        Path p12 = outDir.toPath().resolve(safeDomain + ".p12");
        X509Certificate[] chain = loadCertificateChain(chainFile.toPath());
        PrivateKey pk = loadRSAPrivateKey(new File(outDir, "private.key").toPath());
        String password = "xiaoxiong";  // p12 密码
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, null);
        ks.setKeyEntry(r.domain, pk, password.toCharArray(), chain);

        try (FileOutputStream fos = new FileOutputStream(p12.toFile())) {
            ks.store(fos, password.toCharArray());
        }
        System.out.println("✅ 成功生成 P12 文件：" + p12);
        CheckResult cr = new CheckResult();
        cr.done = true;
        cr.success = true;
        cr.message = "Certificate issued";
        cr.p12Path = p12;
        return cr;
    }

    public static File generateDomainKey(String domain, String baseDir) throws Exception {
        String safeDomain = domain.replaceAll("[^a-zA-Z0-9.-]", "_");
        File dir = new File(baseDir + "/" + safeDomain);
        dir.mkdirs();

        File keyFile = new File(dir, "private.key");
        if (keyFile.exists()) {
            System.out.println("🔑 域名 [" + domain + "] 已存在密钥文件：" + keyFile.getAbsolutePath());
            return keyFile;
        }
        // 生成 RSA 2048 KeyPair
        System.out.println("⚙️  正在为域名 [" + domain + "] 生成 RSA 私钥...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair kp = keyGen.generateKeyPair();

        // 写入 PEM 文件
        try (FileWriter fw = new FileWriter(keyFile)) {
            KeyPairUtils.writeKeyPair(kp, fw);
        }
        System.out.println("✅ 已生成密钥文件: " + keyFile.getAbsolutePath());
        return keyFile;
    }

    public static PrivateKey loadRSAPrivateKey(Path keyPath) throws Exception {
        String pem = Files.readString(keyPath).replaceAll("-----BEGIN RSA PRIVATE KEY-----", "").replaceAll("-----END RSA PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        ASN1Sequence seq = ASN1Sequence.getInstance(decoded);
        RSAPrivateKey rsa = RSAPrivateKey.getInstance(seq);
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(rsa.getModulus(), rsa.getPublicExponent(), rsa.getPrivateExponent(), rsa.getPrime1(), rsa.getPrime2(), rsa.getExponent1(), rsa.getExponent2(), rsa.getCoefficient());
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    public static X509Certificate[] loadCertificateChain(Path cert) throws Exception {
        String content = Files.readString(cert);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        List<X509Certificate> certs = Arrays.stream(content.split("(?=-----BEGIN CERTIFICATE-----)"))
                .filter(s -> s.contains("BEGIN CERTIFICATE"))
                .map(s -> {
                    try (InputStream in = new ByteArrayInputStream(s.getBytes())) {
                        return (X509Certificate) cf.generateCertificate(in);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return certs.toArray(new X509Certificate[0]);
    }
}

package com.proxy.forge.test;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collections;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-24 17:19
 **/
public class ExpCert {

    public static void main(String[] args) throws Exception {

        String p12Path = "./Certificate/certs/_.simone-aka.art/_.simone-aka.art.p12";   // p12 文件路径
        String password = "xiaoxiong";  // p12 密码

        // 加载 keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(p12Path), password.toCharArray());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String alias : Collections.list(keyStore.aliases())) {
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
            if (cert != null) {
                System.out.println("Alias: " + alias);
                System.out.println("Subject: " + cert.getSubjectDN());
                System.out.println("Issuer: " + cert.getIssuerDN());
                System.out.println("Valid From : " + sdf.format(cert.getNotBefore()));
                System.out.println("Valid Until: " + sdf.format(cert.getNotAfter()));  // 格式化输出
            }
        }
    }
}

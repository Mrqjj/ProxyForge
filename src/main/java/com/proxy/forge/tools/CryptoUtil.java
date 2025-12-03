package com.proxy.forge.tools;

import jakarta.xml.bind.DatatypeConverter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * <p>ProjectName: OverseasProject</p>
 * <p>PackageName: com.vivcms.overseas.tools</p>
 * <p>Description: gzip解压缩工具类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-07-06 23:13
 **/
public class CryptoUtil {


    /**
     * 计算 HMAC-SHA256，并取前 20 字节返回十六进制小写字符串。
     * <p>
     * 等价于 Python:
     * key_ = b"123123123123123123123"
     * buf  = ivBytes + data
     * mac  = hmac.new(key_, buf, hashlib.sha256).digest()[:20].hex()
     *
     * @param data 明文字节
     * @param seed 预留参数（当前逻辑未使用）
     * @param iv   16 字节 iv 的byte[] 数据
     * @return 40 位小写 hex
     */
    public static byte[] hmacDataIvSeed(byte[] data, String seed, String iv)
            throws GeneralSecurityException, DecoderException {
        /* 3. 拼接 iv + data */
        String key = generateKeyWithSeed(seed, "HmacSHA256");
        byte[] ivBytes = Hex.decodeHex(iv);
        byte[] msg = new byte[ivBytes.length + data.length];
        System.arraycopy(ivBytes, 0, msg, 0, ivBytes.length);
        System.arraycopy(data, 0, msg, ivBytes.length, data.length);
        /* 4. HMAC-SHA256 */
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(Hex.decodeHex(key), "HmacSHA256"));
        byte[] digest = mac.doFinal(msg);
        /* 5. 取前 20 字节并转 hex */
        return Arrays.copyOf(digest, 20);
    }

    /**
     * 根据种子 + 盐派生 16 字节密钥，并返回十六进制字符串。
     * 等价于 Python:
     * hashlib.pbkdf2_hmac('sha1', seed.encode('utf-8'),
     * salt.encode('utf-8'), 1000, dklen=16)
     */
    public static String generateKeyWithSeed(String seed, String salt) {
        try {
            // 1. 参数准备
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            int iterations = 1000;
            int keyLenBits = 16 * 8;               // dklen=16 bytes → 128 bits
            // 2. PBKDF2-HMAC-SHA1
            PBEKeySpec spec = new PBEKeySpec(
                    seed.toCharArray(),              // 密码（字符）
                    saltBytes,                       // 盐
                    iterations,
                    keyLenBits
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = skf.generateSecret(spec).getEncoded();
            // 3. Bytes → hex 小写
            return DatatypeConverter.printHexBinary(keyBytes).toLowerCase();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 运算失败", e);
        }
    }

    /**
     * 根据长度随机生成固定长度的字节
     *
     * @param length 长度
     * @return 返回的hex
     */
    public static String generateIvByLength(int length) {
        /* 生成 16 字节随机 IV，并转为十六进制小写字符串 */
        SecureRandom rng = new SecureRandom();
        byte[] ivBytes = new byte[16];
        rng.nextBytes(ivBytes);
        // 与 Python b2a_hex 等价
        return DatatypeConverter.printHexBinary(ivBytes).toLowerCase();
    }


    /**
     * AES-CBC-PKCS7 加密（与 CCCrypt + kCCOptionPKCS7Padding 等价）
     *
     * @param key       16 / 24 / 32 字节 AES 密钥
     * @param iv        16 字节初始向量
     * @param plaintext 明文字节
     * @return 密文字节
     * @throws GeneralSecurityException 参数无效或 JCE 限制
     */
    public static byte[] aesCbcPkcs7Encrypt(byte[] key, byte[] iv, byte[] plaintext)
            throws GeneralSecurityException {
        checkKeyIv(key, iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(plaintext);
    }
    /* ------------- 新增：解密方法 ------------- */

    /**
     * AES-CBC-PKCS7 解密，与 CCCrypt(kCCDecrypt, ...) 等价
     *
     * @param key        16/24/32 字节密钥
     * @param iv         16 字节初始向量（加密时用的同一个）
     * @param ciphertext 密文字节
     * @return 解密后的明文字节（自动去掉 PKCS7Padding）
     * @throws GeneralSecurityException BadPaddingException 意味着密钥/IV/数据不匹配或被篡改
     */
    public static byte[] aesCbcPkcs7Decrypt(byte[] key, byte[] iv, byte[] ciphertext)
            throws GeneralSecurityException {
        checkKeyIv(key, iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

        // doFinal 自动验证并去掉 PKCS7 填充；若数据被篡改将抛 BadPaddingException
        return cipher.doFinal(ciphertext);
    }

    /* ------------- 工具：校验 key / iv ------------- */
    private static void checkKeyIv(byte[] key, byte[] iv) {
        if (key == null || (key.length != 16 && key.length != 24 && key.length != 32)) {
            throw new IllegalArgumentException("AES key must be 16/24/32 bytes");
        }
        if (iv == null || iv.length != 16) {
            throw new IllegalArgumentException("IV must be 16 bytes");
        }
    }

    /**
     * GZIP 压缩：可自定义压缩级别（0-9）默认9。返回 gzip 格式字节串。
     */
    public static byte[] gzipCompress(byte[] data, int level) throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos) {{
            def.setLevel(level);   // ← 关键：0-9
        }}) {
            gzos.write(data);
            gzos.close();
            return baos.toByteArray();
        }

    }


    /**
     * 解压 GZIP 字节串。
     *
     * @param gzBytes GZIP 格式压缩数据
     * @return 解压后字节
     * @throws IOException 数据格式错误或读写异常
     */
    public static byte[] gzipDecompress(byte[] gzBytes) throws IOException {
        try (GZIPInputStream gzis =
                     new GZIPInputStream(new ByteArrayInputStream(gzBytes));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int n;
            while ((n = gzis.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        }
    }


}

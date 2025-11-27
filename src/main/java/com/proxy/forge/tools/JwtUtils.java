package com.proxy.forge.tools;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // 32+ 字节的密钥
    private static final String SECRET = "75d84ba6807872d94883525af0c3a662";
    private static final int expireMillis = 7 * 24 * 60 * 60 * 1000; //过期时间 7 天
    // 解析用的 key
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 Token，只传 userId，默认 7 天过期
     */
    public static String createToken(String userId) {
        return createToken(userId, expireMillis, null);
    }

    /**
     * 生成 Token，带自定义 Claims
     */
    public static String createToken(String userId, Map<String, Object> claims) {
        return createToken(userId, expireMillis, claims);
    }

    /**
     * 完整生成 Token 方法：用户 ID + 过期时间 + claims
     */
    public static String createToken(String userId, long expireMillis, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                .subject(userId)               // sub
                .issuer("web")            // iss
                .issuedAt(new Date(now))       // iat
                .expiration(new Date(now + expireMillis)) // exp
                .signWith(KEY);

        if (extraClaims != null) {
            extraClaims.forEach(builder::claim);
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     */
    public static Claims parse(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((SecretKey) KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查 Token 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            Claims claims = parse(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true; // 无效 token 也算过期
        }
    }

    /**
     * 获取用户 ID (Subject)
     */
    public static String getUserId(String token) {
        try {
            return parse(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 获取自定义 Claim
     */
    public static Object getClaim(String token, String key) {
        try {
            return parse(token).get(key);
        } catch (JwtException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String token = createToken("{\"BigIntSupported\":995815895020119788889,\"date\":\"20180322\",\"url\":\"https://www.baidu.com?wd=fehelper\",\"img\":\"http://gips0.baidu.com/it/u=1490237218,4115737545&fm=3028&app=3028&f=JPEG&fmt=auto?w=1280&h=720\",\"message\":\"Success !\",\"status\":200,\"city\":\"北京\",\"count\":632,\"data\":{\"shidu\":\"34%\",\"pm25\":73,\"pm10\":91,\"quality\":\"良\",\"wendu\":\"5\",\"ganmao\":\"极少数敏感人群应减少户外活动\",\"yesterday\":{\"date\":\"21日星期三\",\"sunrise\":\"06:19\",\"high\":\"高温 11.0℃\",\"low\":\"低温 1.0℃\",\"sunset\":\"18:26\",\"aqi\":85,\"fx\":\"南风\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"阴晴之间，谨防紫外线侵扰\"},\"forecast\":[{\"date\":\"22日星期四\",\"sunrise\":\"06:17\",\"high\":\"高温 17.0℃\",\"low\":\"低温 1.0℃\",\"sunset\":\"18:27\",\"aqi\":98,\"fx\":\"西南风\",\"fl\":\"<3级\",\"type\":\"晴\",\"notice\":\"愿你拥有比阳光明媚的心情\"},{\"date\":\"23日星期五\",\"sunrise\":\"06:16\",\"high\":\"高温 18.0℃\",\"low\":\"低温 5.0℃\",\"sunset\":\"18:28\",\"aqi\":118,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"阴晴之间，谨防紫外线侵扰\"},{\"date\":\"24日星期六\",\"sunrise\":\"06:14\",\"high\":\"高温 21.0℃\",\"low\":\"低温 7.0℃\",\"sunset\":\"18:29\",\"aqi\":52,\"fx\":\"西南风\",\"fl\":\"<3级\",\"type\":\"晴\",\"notice\":\"愿你拥有比阳光明媚的心情\"},{\"date\":\"25日星期日\",\"sunrise\":\"06:13\",\"high\":\"高温 22.0℃\",\"low\":\"低温 7.0℃\",\"sunset\":\"18:30\",\"aqi\":71,\"fx\":\"西南风\",\"fl\":\"<3级\",\"type\":\"晴\",\"notice\":\"愿你拥有比阳光明媚的心情\"},{\"date\":\"26日星期一\",\"sunrise\":\"06:11\",\"high\":\"高温 21.0℃\",\"low\":\"低温 8.0℃\",\"sunset\":\"18:31\",\"aqi\":97,\"fx\":\"西南风\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"阴晴之间，谨防紫外线侵扰\"}]}}");
        System.out.println(token);
        System.out.println(isExpired(token));
        System.out.println(parse(token));
    }
}
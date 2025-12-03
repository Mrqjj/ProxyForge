package com.proxy.forge.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Log4j2
@Configuration
public class RedisConfig {

//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
////        // 使用 FastJsonRedisSerializer 来序列化和反序列化redis 的 value的值
////        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
//////        ParserConfig.getGlobalInstance().addAccept("com.wechat");
////        FastJsonConfig fastJsonConfig = new FastJsonConfig();
////        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
////        serializer.setFastJsonConfig(fastJsonConfig);
//        FastJson2RedisSerializer<Object> serializer = new FastJson2RedisSerializer<>(Object.class);

    /// /        FastJsonConfig fastJsonConfig = new FastJsonConfig();
    /// /        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
    /// /        serializer.setFastJsonConfig(fastJsonConfig);
//        log.info("redis自定义序列化完成.");
//        return serializer;
//    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 推荐的写法：使用构造函数传入 ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);

        // 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jacksonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSerializer);
        template.setDefaultSerializer(jacksonSerializer);

        return template;
    }

}
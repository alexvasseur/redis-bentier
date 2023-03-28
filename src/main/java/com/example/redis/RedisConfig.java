package com.example.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.security.SecureRandom;

@Configuration
public class RedisConfig {

    //public static int MAX = 20000000;//2M
    public static int MAX = 100000;//100K for testing

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    static String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static final String VALUE = randomString(150);

//    @Bean
//    public JedisClientConfiguration jedisClientConfiguration() {
//        JedisClientConfiguration jcc = JedisClientConfiguration.defaultConfiguration();
//        System.out.println("POOL CONFIG: "+jcc.getPoolConfig().toString());
//        return jcc;
//    }

//        @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
//        redisConfig.setHostName("localhost");
//        redisConfig.setPort(6379);
//        JedisConnectionFactory jcf = new JedisConnectionFactory(redisConfig, jedisClientConfiguration());
//        jcf.afterPropertiesSet();
//        System.out.println("POOL: "+jcf.getPoolConfig().getMaxTotal());
//        return jcf;
//    }

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        System.out.println("Redis pool max: "+jedisConnectionFactory.getPoolConfig().getMaxTotal());

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        //redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
        //redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setEnableTransactionSupport(false);//note needed and may mandate pool close
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

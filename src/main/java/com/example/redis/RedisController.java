package com.example.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/read/{key}")
    public Object readKey(@PathVariable String key)
    {
        Object value = redisTemplate.opsForValue().get(key);
        return value;
    }

    @GetMapping("/noop")
    public Object noop()
    {
        return RedisConfig.VALUE;
    }

    @GetMapping("/read")
    public Object readKeyNoArg()
    {
        int rand = ThreadLocalRandom.current().nextInt(RedisConfig.MAX);
        String key = String.format("bentier:%010d", rand);
        Object value = redisTemplate.opsForValue().get(key);
        return value;
    }

    @PostMapping("/write")
    public void writeKeyNoArg()
    {
        int rand = ThreadLocalRandom.current().nextInt(RedisConfig.MAX);
        String key = String.format("%010d", rand);
        redisTemplate.opsForValue().set("bentier:"+key, RedisConfig.VALUE);
    }

    @GetMapping("/readint/{nbr}")
    public Object readKeyAsInt(@PathVariable Integer nbr)
    {
        String key = String.format("bentier:%010d", nbr);
        Object value = redisTemplate.opsForValue().get(key);
        return value;
    }

    @PostMapping("/writeint/{nbr}")
    public void writeKeyAsInt(@PathVariable Integer nbr)
    {
        String key = String.format("%010d", nbr);
        redisTemplate.opsForValue().set("bentier:"+key, RedisConfig.VALUE);
    }

    @GetMapping("/load100")
    public String load()
    {
        redisTemplate.executePipelined(new RedisCallback< Object >() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for( int i = 0; i < 100; ++i ) {
                    int rand = ThreadLocalRandom.current().nextInt(RedisConfig.MAX);
                    String key = String.format("%010d", rand);
                    connection.stringCommands().set(("bentier:"+key).getBytes(), RedisConfig.VALUE.getBytes());
                }
                return null;
            }
        } );
        return "OK";
    }

    @PostMapping("/write/{key}")
    public void writeKey(@PathVariable String key)
    {
        redisTemplate.opsForValue().set(key, RedisConfig.VALUE);
    }

    @PostMapping("/write/{key}/{value}")
    public void writeKeyValue(@PathVariable String key, @PathVariable String value)
    {
        redisTemplate.opsForValue().set(key, value);
    }
}

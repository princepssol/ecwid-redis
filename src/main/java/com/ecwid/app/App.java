package com.ecwid.app;

import com.ecwid.app.redis.RedisClient;
import com.ecwid.app.redis.config.RedisConfig;

public class App 
{
    public static void main( String[] args ) throws Exception {
        try (RedisClient redisClient = RedisClient.getInstance(RedisConfig.URL, RedisConfig.PORT)) {
            redisClient.set("1", "2");
            System.out.println(redisClient.get("1"));
        }

    }
}

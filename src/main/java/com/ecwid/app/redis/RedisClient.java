package com.ecwid.app.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.net.URI;
import java.net.URISyntaxException;

import static com.ecwid.app.redis.config.RedisConfig.*;

public final class RedisClient implements AutoCloseable {
    private static volatile RedisClient instance;
    private static JedisPool jedisPool;

    private RedisClient(String ip, int port) {
        try {
            if (jedisPool == null) {
                jedisPool = new JedisPool(new URI("http://" + ip + ":" + port));
            }
        } catch (URISyntaxException e) {
            System.err.println("Malformed server address");
        }
    }

    public static RedisClient getInstance() {
        if (instance == null) {
            synchronized (RedisClient.class) {
                if (instance == null) {
                    instance = new RedisClient(URL, PORT);
                }
            }
        }
        return instance;
    }

    @Override
    public void close() throws Exception {
        jedisPool.close();
    }
}

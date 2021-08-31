package com.ecwid.app.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;

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

    public static RedisClient getInstance(String ip, final int port) {
        if (instance == null) {
            synchronized (RedisClient.class) {
                if (instance == null) {
                    instance = new RedisClient(ip, port);
                }
            }
        }
        return instance;
    }

    public void set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        return jedis.get(key);
    }

    @Override
    public void close() throws Exception {
        jedisPool.close();
    }
}

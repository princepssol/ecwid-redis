package com.ecwid.app.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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
    public void close() {
        jedisPool.close();
    }

    public int getSizeMap(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hlen(name).intValue();
        }
    }

    public boolean existsMap(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(name);
        }
    }


    public boolean containsKeyMap(String name, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(name, key);
        }
    }

    public boolean containsValueMap(String name, Integer value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(name).contains(value.toString());
        }
    }

    public Integer getFromMap(String name, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.hget(name, key);
            if (Objects.nonNull(value)) {
                return convertToInt(value);
            }
            return null;
        }
    }

    public Integer putToMap(String name, String key, Integer value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(name, key, value.toString());
            return value;
        }
    }

    public void putToMap(String name, Map<? extends String, ? extends Integer> m) {
        Map<String, String> map = new HashMap<>();
        m.forEach(
                (key, value) -> map.put(key, value.toString())
        );
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(name, map);
        }
    }

    public void removeMap(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(name);
        }
    }

    public Integer removeFromMap(String name, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.hget(name, key);
            if (jedis.hdel(name, key) == 1L && Objects.nonNull(value)) {
                return convertToInt(value);
            }
            return null;
        }
    }

    public Set<String> getKeysFromMap(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(name);
        }
    }

    public Collection<Integer> getValuesFromMap(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(name)
                    .stream()
                    .map(this::convertToInt)
                    .collect(Collectors.toList());
        }
    }

    public Map<String, Integer> getMap(String name) {
        Map<String, Integer> map = new HashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hgetAll(name).forEach(
                    (key, value) -> map.put(key, convertToInt(value))
            );
            return map;
        }
    }

    private Integer convertToInt(String string) {
        return Integer.parseInt(string);
    }
}

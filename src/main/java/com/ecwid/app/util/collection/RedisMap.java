package com.ecwid.app.util.collection;

import com.ecwid.app.redis.RedisClient;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RedisMap implements Map<String, Integer> {
    private static int mapCount;
    private final RedisClient redisClient = RedisClient.getInstance();
    private final String mapKey;

    public RedisMap(String key, Integer value) {
        mapKey = this.getClass().getSimpleName() + "::" + mapCount;
        redisClient.createMap(mapKey, key, value);
        mapCount++;
    }

    public RedisMap(Map<String, Integer> map) {
        mapKey = this.getClass().getSimpleName() + "::" + mapCount;
        redisClient.createMap(mapKey, map);
        mapCount++;
    }

    @Override
    public int size() {
        return redisClient.getMapSize(mapKey);
    }

    @Override
    public boolean isEmpty() {
        return redisClient.exists(mapKey);
    }

    @Override
    public boolean containsKey(Object key) {
        return redisClient.containsKeyMap(mapKey, key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Integer) {
            return redisClient.;
        }
    }

    @Override
    public Integer get(Object key) {
        return null;
    }

    @Override
    public Integer put(String key, Integer value) {
        return null;
    }

    @Override
    public Integer remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Integer> values() {
        return null;
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return null;
    }
}

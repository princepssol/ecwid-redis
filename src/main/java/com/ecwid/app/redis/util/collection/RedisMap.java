package com.ecwid.app.redis.util.collection;

import com.ecwid.app.redis.RedisClient;

import java.util.Collection;
import java.util.Map;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisMap implements Map<String, Integer> {
    private static final AtomicInteger mapCount = new AtomicInteger(0);
    private final RedisClient redisClient = RedisClient.getInstance();
    private final String mapKey;

    public RedisMap(String key, Integer value) {
        mapKey = this.getClass().getSimpleName() + "::" + mapCount.get();
        if (isEmpty()) {
            put(key, value);
            mapCount.incrementAndGet();
        } else {
            put(key, value);
        }
    }

    public RedisMap(Map<? extends String, ? extends Integer> map) {
        if (map.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        mapKey = this.getClass().getSimpleName() + "::" + mapCount.get();
        if (isEmpty()) {
            putAll(map);
            mapCount.incrementAndGet();
        } else {
            putAll(map);
        }
    }

    @Override
    public int size() {
        return redisClient.getSizeMap(mapKey);
    }

    @Override
    public boolean isEmpty() {
        return redisClient.nonExists(mapKey);
    }

    @Override
    public boolean containsKey(Object key) {
        Objects.requireNonNull(key);
        if (key instanceof String) {
            return redisClient.containsKeyMap(mapKey, (String) key);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        Objects.requireNonNull(value);
        if (value instanceof Integer) {
            return redisClient.containsValueMap(mapKey, (Integer) value);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public Integer get(Object key) {
        Objects.requireNonNull(key);
        if (key instanceof String) {
            return redisClient.getFromMap(mapKey, (String) key);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public Integer put(String key, Integer value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return redisClient.putToMap(mapKey, key, value);
    }

    @Override
    public Integer remove(Object key) {
        Objects.requireNonNull(key);
        if (key instanceof String) {
            return redisClient.removeFromMap(mapKey, (String) key);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> m) {
        checkNull(m);
        redisClient.putToMap(mapKey, m);
    }

    @Override
    public void clear() {
        redisClient.remove(mapKey);
    }

    @Override
    public Set<String> keySet() {
        return redisClient.getKeysFromMap(mapKey);
    }

    @Override
    public Collection<Integer> values() {
        return redisClient.getValuesFromMap(mapKey);
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        Map<String, Integer> map = redisClient.getMap(mapKey);
        return map.entrySet();
    }

    private void checkNull(Map<? extends String, ? extends Integer> map) {
        Objects.requireNonNull(map);
        map.forEach(
                (key, value) -> {
                    Objects.requireNonNull(key);
                    Objects.requireNonNull(value);
                }
        );
    }
}

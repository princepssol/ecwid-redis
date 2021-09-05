package com.ecwid.app.redis.util.collection;

import com.ecwid.app.redis.RedisClient;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.isNull;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisMap implements Map<String, Integer> {
    private static final String NOT_KEY = "Key is not a string";
    private static final String NOT_VALUE = "Value is not an integer";
    private static final String VALUE_NULL = "value=null";
    private static final String KEY_NULL = "key=null";
    private static final String MAP_NULL = "Accepted Map is null";
    private static final String EMPTY_MAP = "Can't accept empty Map";
    private static final String ELEMENT_MAP = "Map with ";
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
            throw new UnsupportedOperationException(EMPTY_MAP);
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
        return !redisClient.existsMap(mapKey);
    }

    @Override
    public boolean containsKey(Object key) {
        checkNull(key, KEY_NULL);
        if (key instanceof String) {
            return redisClient.containsKeyMap(mapKey, (String) key);
        } else {
            throw new ClassCastException(NOT_KEY);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        checkNull(value, VALUE_NULL);
        if (value instanceof Integer) {
            return redisClient.containsValueMap(mapKey, (Integer) value);
        } else {
            throw new ClassCastException(NOT_VALUE);
        }
    }

    @Override
    public Integer get(Object key) {
        checkNull(key, KEY_NULL);
        if (key instanceof String) {
            return redisClient.getFromMap(mapKey, (String) key);
        } else {
            throw new ClassCastException(NOT_VALUE);
        }
    }

    @Override
    public Integer put(String key, Integer value) {
        checkNull(key, KEY_NULL);
        checkNull(value, VALUE_NULL);
        return redisClient.putToMap(mapKey, key, value);
    }

    @Override
    public Integer remove(Object key) {
        checkNull(key, KEY_NULL);
        if (key instanceof String) {
            return redisClient.removeFromMap(mapKey, (String) key);
        } else {
            throw new ClassCastException(NOT_KEY);
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> m) {
        checkNull(m);
        redisClient.putToMap(mapKey, m);
    }

    @Override
    public void clear() {
        redisClient.removeMap(mapKey);
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

    private void checkNull(Object object, String message) {
        if (isNull(object)) {
            throw new NullPointerException(message);
        }
    }

    private void checkNull(Map<? extends String, ? extends Integer> map) {
        checkNull(map, MAP_NULL);
        map.forEach(
                (key, value) -> {
                    checkNull(key, ELEMENT_MAP + KEY_NULL);
                    checkNull(value, ELEMENT_MAP + VALUE_NULL);
                }
        );
    }
}

package com.ecwid.app.redis;

import redis.clients.jedis.*;
import redis.clients.jedis.params.LPosParams;

import java.util.*;
import java.util.stream.Collectors;

import static com.ecwid.app.redis.config.RedisConfig.*;

public final class RedisClient implements AutoCloseable {
    private static volatile RedisClient instance;
    private static JedisSentinelPool jedisPool;

    private RedisClient(String master, String ip, int port) {
        if (jedisPool == null) {
            jedisPool = new JedisSentinelPool(master, Set.of(ip + ":" + port));
        }
    }

    public static RedisClient getInstance() {
        if (instance == null) {
            synchronized (RedisClient.class) {
                if (instance == null) {
                    instance = new RedisClient(MASTER_NAME, URL, PORT);
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

    public boolean exists(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(name);
        }
    }

    public boolean nonExists(String name) {
        return !exists(name);
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
                return Integer.parseInt(value);
            }
            return null;
        }
    }

    public Integer putToMap(String name, String key, Integer value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String oldString = jedis.hget(name, key);
            Integer oldInteger = null;
            if (Objects.nonNull(oldString)) {
                oldInteger = Integer.parseInt(oldString);
            }
            jedis.hset(name, key, value.toString());
            return oldInteger;
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

    public void remove(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(name);
        }
    }

    public Integer removeFromMap(String name, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.hget(name, key);
            if (jedis.hdel(name, key) == 1L && Objects.nonNull(value)) {
                return Integer.parseInt(value);
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
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    public Map<String, Integer> getMap(String name) {
        Map<String, Integer> map = new HashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hgetAll(name).forEach(
                    (key, value) -> map.put(key, Integer.parseInt(value))
            );
            return map;
        }
    }

    public int getSizeList(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(name).intValue();
        }
    }

    public boolean addToTailList(String name, Integer integer) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush(name, integer.toString());
            return true;
        }
    }

    public boolean containsElementList(String name, Integer o) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long result = jedis.lpos(name, o.toString());
            return Objects.nonNull(result);
        }
    }

    public List<Integer> getList(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(name, 0, -1).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    public boolean removeFromList(String name, Integer o) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrem(name, -1, o.toString()) > 0L;
        }
    }

    public boolean containsAllList(String name, Collection<? extends Integer> collection) {
        try (Jedis jedis = jedisPool.getResource()) {
            return collection.stream()
                    .map(integer -> jedis.lpos(name, integer.toString()))
                    .allMatch(Objects::nonNull);
        }
    }

    public boolean addAllToTailList(String name, Collection<? extends Integer> collection) {
        try (Jedis jedis = jedisPool.getResource()) {
            long before = jedis.llen(name);
            String[] strings = collection.stream()
                    .map(Objects::toString)
                    .toArray(String[]::new);
            return jedis.rpush(name, strings) > before;
        }
    }

    public boolean addAllToIndexList(String name, int index, Collection<? extends Integer> collection) {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();
            List<String> total = transaction.lrange(name, 0, -1).get();
            long before = total.size();
            List<String> toAdd = new ArrayList<>();
            collection.forEach(element -> toAdd.add(element.toString()));
            total.addAll(index, toAdd);
            jedis.del(name);
            long after = jedis.rpush(name, total.toArray(String[]::new));
            transaction.exec();
            return after > before;
        }
    }

    public boolean removeAllFromList(String name, Collection<? extends Integer> collection) {
        try (Jedis jedis = jedisPool.getResource()) {
            return collection.stream()
                    .mapToLong(integer -> jedis.lrem(name, -1, integer.toString()))
                    .sum() > 0L;
        }
    }

    public boolean retainAllFromList(String name, Collection<? extends Integer> collection) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<Integer> newList = jedis.lrange(name, 0, -1).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            Transaction transaction = jedis.multi();
            if (newList.retainAll(collection)) {
                transaction.del(name);
                String[] strings = newList.stream()
                        .map(Objects::toString)
                        .toArray(String[]::new);
                transaction.rpush(name, strings);
                transaction.exec();
                return true;
            } else {
                transaction.discard();
                return false;
            }
        }
    }

    public Integer getFromListByIndex(String name, int index) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Integer.parseInt(jedis.lindex(name, index));
        }
    }

    public Integer setElementInListByIndex(String name, int index, Integer element) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (Objects.isNull(jedis.lindex(name, index))) {
                return null;
            }
            List<String> oldList = jedis.lrange(name, 0, -1);
            Integer old = Integer.parseInt(oldList.set(index, element.toString()));
            Transaction transaction = jedis.multi();
            transaction.del(name);
            transaction.rpush(name, oldList.toArray(String[]::new));
            transaction.exec();
            return old;
        }
    }

    public void addToIndexList(String name, int index, Integer element) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> oldList = jedis.lrange(name, 0, -1);
            Transaction transaction = jedis.multi();
            oldList.add(index, element.toString());
            transaction.del(name);
            transaction.rpush(name, oldList.toArray(String[]::new));
            transaction.exec();
        }
    }


    public Integer removeFromListByIndex(String name, int index) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> oldList = jedis.lrange(name, 0, -1);
            Transaction transaction = jedis.multi();
            String result = oldList.remove(index);
            transaction.del(name);
            if (oldList.size() != 0) {
                transaction.rpush(name, oldList.toArray(String[]::new));
                transaction.exec();
                return Integer.parseInt(result);
            } else {
                transaction.exec();
                return null;
            }
        }
    }

    public int getIndexFromList(String name, Integer element) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(jedis.lpos(name, element.toString()))
                    .map(Long::intValue)
                    .orElse(-1);
        }
    }

    public int getLastIndexFromList(String name, Integer element) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(jedis.lpos(name, element.toString(), LPosParams.lPosParams().rank(-1)))
                    .map(Long::intValue)
                    .orElse(-1);
        }
    }
}

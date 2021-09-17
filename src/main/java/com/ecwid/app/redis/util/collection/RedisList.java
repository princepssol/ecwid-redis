package com.ecwid.app.redis.util.collection;

import com.ecwid.app.redis.RedisClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

public class RedisList implements List<Integer> {
    private static AtomicInteger listCount = new AtomicInteger(0);
    private final RedisClient redisClient = RedisClient.getInstance();
    private final String listKey;

    public RedisList(Integer element) {
        listKey = this.getClass().getSimpleName() + "::" + listCount.get();
        if (isEmpty()) {
            add(element);
            listCount.incrementAndGet();
        } else {
            add(element);
        }
    }

    public RedisList(Collection<? extends Integer> collection) {
        if (collection.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        listKey = this.getClass().getSimpleName() + "::" + listCount.get();
        if (isEmpty()) {
            addAll(collection);
            listCount.incrementAndGet();
        } else  {
            addAll(collection);
        }
    }

    @Override
    public int size() {
        return redisClient.getSizeList(listKey);
    }

    @Override
    public boolean isEmpty() {
        return redisClient.nonExists(listKey);
    }

    @Override
    public boolean contains(Object o) {
        Objects.requireNonNull(o);
        if (o instanceof Integer) {
            return redisClient.containsElementList(listKey, (Integer) o);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return redisClient.getList(listKey).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return redisClient.getList(listKey).toArray(a);
    }

    @Override
    public boolean add(Integer integer) {
        Objects.requireNonNull(integer);
        return redisClient.addToTailList(listKey, integer);
    }

    @Override
    public boolean remove(Object o) {
        Objects.requireNonNull(o);
        if (o instanceof Integer) {
            return redisClient.removeFromList(listKey, (Integer) o);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        c.forEach(Objects::requireNonNull);
        return redisClient.containsAllList(listKey, (Collection<? extends Integer>) c);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        Objects.requireNonNull(c);
        c.forEach(Objects::requireNonNull);
        return redisClient.addAllToTailList(listKey, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
        Objects.requireNonNull(c);
        c.forEach(Objects::requireNonNull);
        return redisClient.addAllToIndexList(listKey, index, c);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        Objects.requireNonNull(collection);
        collection.forEach(Objects::requireNonNull);
        return redisClient.removeAllFromList(listKey, (Collection<? extends Integer>) collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        Objects.requireNonNull(collection);
        collection.forEach(Objects::requireNonNull);
        return redisClient.retainAllFromList(listKey, (Collection<? extends Integer>) collection);
    }

    @Override
    public void clear() {
        redisClient.remove(listKey);
    }

    @Override
    public Integer get(int index) {
        return redisClient.getFromListByIndex(listKey, index);
    }

    @Override
    public Integer set(int index, Integer element) {
        Objects.requireNonNull(element);
        return redisClient.setElementInListByIndex(listKey, index, element);
    }

    @Override
    public void add(int index, Integer element) {
        Objects.requireNonNull(element);
        redisClient.addToIndexList(listKey, index, element);
    }

    @Override
    public Integer remove(int index) {
        return redisClient.removeFromListByIndex(listKey, index);
    }

    @Override
    public int indexOf(Object o) {
        Objects.requireNonNull(o);
        if (o instanceof Integer) {
            return redisClient.getIndexFromList(listKey, (Integer) o);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        Objects.requireNonNull(o);
        if (o instanceof Integer) {
            return redisClient.getLastIndexFromList(listKey, (Integer) o);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public ListIterator<Integer> listIterator() {
        return new ListItr();
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        return null;
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        return null;
    }

    private void checkNull(Object o) {
        if (isNull(o)) {
            throw new NullPointerException();
        }
    }

    private class ListItr implements ListIterator<Integer> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Integer next() {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public Integer previous() {
            return null;
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return 0;
        }

        @Override
        public void remove() {

        }

        @Override
        public void set(Integer integer) {

        }

        @Override
        public void add(Integer integer) {

        }
    }
}

package com.ecwid.app.redis.util.collection;

import com.ecwid.app.redis.RedisClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.*;

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
        requireNonNull(o);
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
        requireNonNull(o);
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
        requireNonNull(c);
        c.forEach(Objects::requireNonNull);
        return redisClient.addAllToTailList(listKey, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
        checkIndex(index, size());
        requireNonNull(c);
        c.forEach(Objects::requireNonNull);
        return redisClient.addAllToIndexList(listKey, index, c);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        requireNonNull(collection);
        collection.forEach(Objects::requireNonNull);
        return redisClient.removeAllFromList(listKey, (Collection<? extends Integer>) collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        requireNonNull(collection);
        collection.forEach(Objects::requireNonNull);
        return redisClient.retainAllFromList(listKey, (Collection<? extends Integer>) collection);
    }

    @Override
    public void clear() {
        redisClient.remove(listKey);
    }

    @Override
    public Integer get(int index) {
        checkIndex(index, size());
        return redisClient.getFromListByIndex(listKey, index);
    }

    @Override
    public Integer set(int index, Integer element) {
        requireNonNull(element);
        checkIndex(index, size());
        return redisClient.setElementInListByIndex(listKey, index, element);
    }

    @Override
    public void add(int index, Integer element) {
        requireNonNull(element);
        checkIndex(index, size());
        redisClient.addToIndexList(listKey, index, element);
    }

    @Override
    public Integer remove(int index) {
        checkIndex(index, size());
        return redisClient.removeFromListByIndex(listKey, index);
    }

    @Override
    public int indexOf(Object o) {
        requireNonNull(o);
        if (o instanceof Integer) {
            return redisClient.getIndexFromList(listKey, (Integer) o);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        requireNonNull(o);
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
        checkIndex(index, size());
        return new ListItr(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private class ListItr implements ListIterator<Integer> {
        private final RedisList thisList;
        private final List<Integer> listArray;
        private final ListIterator<Integer> listIterator;

        private ListItr() {
            thisList = RedisList.this;
            listArray = redisClient.getList(listKey);
            listIterator = listArray.listIterator();
        }

        private ListItr(int index) {
            thisList = RedisList.this;
            listArray = redisClient.getList(listKey);
            listIterator = listArray.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            return listIterator.hasNext();
        }

        @Override
        public Integer next() {
            return listIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return listIterator.hasPrevious();
        }

        @Override
        public Integer previous() {
            return listIterator.previous();
        }

        @Override
        public int nextIndex() {
            return listIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return listIterator.previousIndex();
        }

        @Override
        public void remove() {
            listIterator.remove();
            thisList.remove(nextIndex());
        }

        @Override
        public void set(Integer integer) {
            listIterator.set(integer);
            thisList.set(nextIndex(), integer);
        }

        @Override
        public void add(Integer integer) {
            listIterator.add(integer);
            thisList.add(nextIndex(), integer);
        }
    }
}

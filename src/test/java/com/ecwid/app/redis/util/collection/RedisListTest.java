package com.ecwid.app.redis.util.collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedisListTest {
    private RedisList actual;
    private List<Integer> expected;

    @BeforeEach
    void setUp() {
        expected = new ArrayList<>(List.of(1, 2, 3));
        actual = new RedisList(expected);
    }

    @AfterEach
    void tearDown() {
        actual.clear();
    }

    @Test
    void size() {
        assertEquals(expected.size(), actual.size());
        actual.remove(0);
        assertEquals(expected.size(), actual.size() + 1);
    }

    @Test
    void isEmpty() {
        assertFalse(actual.isEmpty());
        actual.clear();
        assertTrue(actual.isEmpty());
    }

    @Test
    void contains() {
        assertFalse(actual.contains(0));
        assertTrue(actual.contains(1));
        assertThrows(ClassCastException.class, () -> actual.contains("wrong type"));
    }

    @Test
    void iterator() {
        assertNotNull(actual.iterator());
    }

    @Test
    void toArray() {
        assertEquals(Arrays.toString(expected.toArray()), Arrays.toString(actual.toArray()));
    }

    @Test
    void testToArray() {
        String actualString = Arrays.toString(actual.toArray(new Integer[0]));
        String expectedString = Arrays.toString(expected.toArray(new Integer[0]));
        assertEquals(expectedString, actualString);
    }

    @Test
    void add() {
        expected.add(0);
        assertTrue(actual.add(0));
        assertEquals(0, actual.get(actual.size() - 1));
        expected.add(1, 123);
        actual.add(1, 123);
        assertEquals(123, actual.get(1));
        assertEquals(expected.get(1), actual.get(1));
        assertEquals(expected.size(), actual.size());
        assertThrows(IndexOutOfBoundsException.class, () -> actual.add(-1, 2));
    }

    @Test
    void remove() {
        expected.remove(Integer.valueOf(1));
        assertTrue(actual.remove(Integer.valueOf(1)));
        Integer expectedInt = expected.remove(0);
        assertEquals(expectedInt, actual.remove(0));
        assertEquals(expected.size(), actual.size());
        assertThrows(ClassCastException.class, () -> actual.remove("wrong type"));
    }

    @Test
    void containsAll() {
        assertTrue(actual.containsAll(List.of(1, 2)));
        assertFalse(actual.containsAll(List.of(3, 4)));
    }

    @Test
    void addAll() {
        expected.addAll(List.of(4, 5));
        assertTrue(actual.addAll(List.of(4, 5)));
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void removeAll() {
        expected.removeAll(List.of(1, 2));
        assertTrue(actual.removeAll(List.of(1, 2)));
        assertFalse(actual.containsAll(List.of(1, 2)));
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void retainAll() {
        expected.retainAll(List.of(3, 4));
        assertTrue(actual.retainAll(List.of(3, 4)));
        assertTrue(actual.contains(3));
        assertFalse(actual.contains(4));
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void clear() {
        assertFalse(actual.isEmpty());
        actual.clear();
        assertTrue(actual.isEmpty());
    }

    @Test
    void get() {
        assertEquals(expected.get(0), actual.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(-1));
    }

    @Test
    void set() {
        assertEquals(expected.set(0, 123), actual.set(0, 123));
        assertEquals(123, actual.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.set(-1, 1000));
    }

    @Test
    void indexOf() {
        expected.addAll(List.of(1, 3, 1, 2));
        actual.addAll(List.of(1, 3, 1, 2));
        assertEquals(expected.indexOf(1), actual.indexOf(1));
        assertEquals(2, actual.indexOf(3));
        assertEquals(-1, actual.indexOf(123));
    }

    @Test
    void lastIndexOf() {
        expected.addAll(List.of(1, 3, 1, 2));
        actual.addAll(List.of(1, 3, 1, 2));
        assertEquals(expected.lastIndexOf(1), actual.lastIndexOf(1));
        assertEquals(6, actual.lastIndexOf(2));
        assertEquals(-1, actual.indexOf(123));
    }

    @Test
    void listIterator() {
        assertNotNull(actual.listIterator());
        assertNotNull(actual.listIterator(1));
    }

    @Test
    void subList() {
        assertThrows(UnsupportedOperationException.class, () -> actual.subList(0, -1));
    }
}
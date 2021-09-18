package com.ecwid.app.redis.util.collection;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RedisMapTest {
    private RedisMap actual;
    private Map<String, Integer> expected;

    @BeforeEach
    void setUp() {
        expected = new HashMap<>();
        expected.put("0", 0);
        actual = new RedisMap(expected);
    }

    @AfterEach
    void tearDown() {
        actual.clear();
    }

    @Test
    void size() {
        assertEquals(actual.size(), expected.size());
    }

    @Test
    void isEmpty() {
        assertFalse(actual.isEmpty());
        actual.clear();
        assertTrue(actual.isEmpty());
    }

    @Test
    void containsKey() {
        assertTrue(actual.containsKey("0"));
        assertFalse(actual.containsKey("not exists"));
    }

    @Test
    void containsValue() {
        assertTrue(actual.containsValue(0));
        assertFalse(actual.containsValue(10));
    }

    @Test
    void get() {
        assertEquals(expected.get("0"), actual.get("0"));
    }

    @Test
    void put() {
        assertFalse(actual.containsKey("1"));
        actual.put("1", 1);
        assertTrue(actual.containsKey("1"));
        assertTrue(actual.containsValue(1));
    }

    @Test
    void remove() {
        assertFalse(actual.containsKey("1"));
        actual.put("1", 1);
        assertTrue(actual.containsKey("1"));
        assertTrue(actual.containsValue(1));
        actual.remove("1");
        assertFalse(actual.containsKey("1"));
        assertFalse(actual.containsValue(1));
    }

    @Test
    void putAll() {
        expected.put("new", 1);
        expected.put("new2", 2);
        assertFalse(actual.containsKey("new"));
        actual.putAll(expected);
        assertTrue(actual.containsKey("new"));
        assertTrue(actual.containsKey("new2"));
    }

    @Test
    void clear() {
        assertTrue(actual.size() > 0);
        actual.clear();
        assertEquals(0, actual.size());
        assertTrue(actual.isEmpty());
    }

    @Test
    void keySet() {
        assertEquals(expected.keySet(), actual.keySet());
    }

    @Test
    void values() {
        List<Integer> actualValues = new ArrayList<>(actual.values());
        List<Integer> expectedValues = new ArrayList<>(expected.values());
        assertEquals(actualValues, expectedValues);
    }

    @Test
    void entrySet() {
        assertEquals(actual.entrySet(), expected.entrySet());
    }
}
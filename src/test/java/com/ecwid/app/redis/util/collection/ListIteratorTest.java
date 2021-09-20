package com.ecwid.app.redis.util.collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

public class ListIteratorTest {
    private List<Integer> expectedList;
    private RedisList actualList;
    private ListIterator<Integer> expectedListIterator;
    private ListIterator<Integer> actualListIterator;

    @BeforeEach
    void setUp() {
        expectedList = new ArrayList<>(List.of(1, 2, 3, 4));
        actualList = new RedisList(expectedList);
        expectedListIterator = expectedList.listIterator();
        actualListIterator = actualList.listIterator();
    }

    @AfterEach
    void tearDown() {
        actualList.clear();
    }

    @Test
    void hasNext() {
        assertTrue(actualListIterator.hasNext());

        while (actualListIterator.hasNext()) {
            assertEquals(expectedListIterator.hasNext(), actualListIterator.hasNext());
            actualListIterator.next();
        }

        assertFalse(actualListIterator.hasNext());
    }

    @Test
    void next() {
        while (actualListIterator.hasNext()) {
            assertEquals(expectedListIterator.next(), actualListIterator.next());
        }
    }

    @Test
    void remove() {
        while (actualListIterator.hasNext()) {
            expectedListIterator.next();
            expectedListIterator.remove();
            actualListIterator.next();
            actualListIterator.remove();
        }
        assertEquals(0, actualList.size());
        assertEquals(expectedList.size(), actualList.size());
    }

    @Test
    void previous() {
        actualListIterator = actualList.listIterator(actualList.size() - 1);
        expectedListIterator = expectedList.listIterator(expectedList.size() - 1);

        while (actualListIterator.hasPrevious()) {
            assertEquals(expectedListIterator.previous(), actualListIterator.previous());
        }
    }

    @Test
    void hasPrevious() {
        assertFalse(actualListIterator.hasPrevious());
        assertEquals(expectedListIterator.hasPrevious(), actualListIterator.hasPrevious());

        actualListIterator = actualList.listIterator(actualList.size() - 1);
        expectedListIterator = expectedList.listIterator(expectedList.size() - 1);

        assertTrue(actualListIterator.hasPrevious());
        assertEquals(expectedListIterator.hasPrevious(), actualListIterator.hasPrevious());
    }

    @Test
    void nextIndex() {
        while (actualListIterator.hasNext()) {
            assertEquals(expectedListIterator.nextIndex(), actualListIterator.nextIndex());
            actualListIterator.next();
            expectedListIterator.next();
        }
    }

    @Test
    void previousIndex() {
        expectedListIterator = expectedList.listIterator(expectedList.size() - 1);
        actualListIterator = actualList.listIterator(actualList.size() - 1);

        while (actualListIterator.hasPrevious()) {
            assertEquals(expectedListIterator.previousIndex(), actualListIterator.previousIndex());
            actualListIterator.previous();
            expectedListIterator.previous();
        }
    }

    @Test
    void set() {
        while (expectedListIterator.hasNext()) {
            expectedListIterator.next();
            expectedListIterator.set(123);
            actualListIterator.next();
            actualListIterator.set(123);
        }

        while (actualListIterator.hasPrevious()) {
            Integer element = actualListIterator.previous();
            assertEquals(123, element);
            assertEquals(expectedListIterator.previous(), element);
        }
    }

    @Test
    void add() {
        while (actualListIterator.hasNext()) {
            if (actualListIterator.nextIndex() == 2) {
                actualListIterator.add(1234);
            }
            actualListIterator.next();

            if (expectedListIterator.nextIndex() == 2) {
                expectedListIterator.add(1234);
            }
            expectedListIterator.next();
        }

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(1234, actualList.get(2));
        assertEquals(expectedList.get(2), actualList.get(2));
    }
}

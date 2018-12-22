package ru.progrm_jarvis.minecraft.commons.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilTest {

    @Test
    void testFillMapFromArray() {
        assertEquals(new HashMap<>(), MapUtil.fillMap(new HashMap<>()));

        assertEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                    put(2, "world");
                }},
                MapUtil.fillMap(new HashMap<>(), 1, "Hello", 2, "world")
        );

        assertNotEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                }},
                MapUtil.fillMap(new HashMap<>(), 1, "Hello", 2, "world")
        );

        assertThrows(IllegalArgumentException.class, () -> MapUtil.fillMap(new HashMap<>(), 1));

        assertThrows(IllegalArgumentException.class, () -> MapUtil.fillMap(new HashMap<>(), 1, 3, "String"));
    }

    @Test
    void testFillMapFromIterator() {
        assertEquals(new HashMap<>(), MapUtil.fillMap(new HashMap<>()));

        assertEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                    put(2, "world");
                }},
                MapUtil.fillMap(new HashMap<>(), Arrays.asList(Pair.of(1, "Hello"), Pair.of(2, "world")).iterator())
        );

        assertNotEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                }},
                MapUtil.fillMap(new HashMap<>(), Arrays.asList(Pair.of(1, "Hello"), Pair.of(2, "world")).iterator())
        );
    }

    @Test
    void testFillMapFromIterable() {
        assertEquals(new HashMap<>(), MapUtil.fillMap(new HashMap<>()));

        assertEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                    put(2, "world");
                }},
                MapUtil.fillMap(new HashMap<>(), Arrays.asList(Pair.of(1, "Hello"), Pair.of(2, "world")))
        );

        assertNotEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                }},
                MapUtil.fillMap(new HashMap<>(), Arrays.asList(Pair.of(1, "Hello"), Pair.of(2, "world")))
        );
    }

    @Test
    void testFillMapFromStream() {
        assertEquals(new HashMap<>(), MapUtil.fillMap(new HashMap<>()));

        assertEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                    put(2, "world");
                }},
                MapUtil.fillMap(new HashMap<>(), Stream.of(Pair.of(1, "Hello"), Pair.of(2, "world")))
        );

        assertNotEquals(
                new HashMap<Integer, String>() {{
                    put(1, "Hello");
                }},
                MapUtil.fillMap(new HashMap<>(), Stream.of(Pair.of(1, "Hello"), Pair.of(2, "world")))
        );
    }
}
package ru.progrm_jarvis.minecraft.commons.util;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.immutableEntry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    @Test
    void testMapFillerConstructWithFirst() {
        assertThat(
                MapUtil.mapFiller(new HashMap<>()).map().keySet(),
                empty()
        );

        assertEquals(
                new HashMap<String, Integer>() {{
                    put("Hello", 1);
                }},
                MapUtil.mapFiller(new HashMap<>(), "Hello", 1).map()
        );
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerPut() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .put("one", 1)
                .put("two", 2)
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillIterator() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Arrays.asList(Pair.of("one", 1), Pair.of("two", 2)).iterator())
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillIterable() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Arrays.asList(Pair.of("one", 1), Pair.of("two", 2)))
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillStream() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Stream.of(Pair.of("one", 1), Pair.of("two", 2)))
                .map()
                .entrySet();

        assertThat(entries,
                hasSize(2)
        );

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillEveryKind() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .put("one", 1)
                .put("two", 2)
                .fill(Arrays.asList(Pair.of("five", 5), Pair.of("six", 6)))
                .fill(Arrays.asList(Pair.of("three", 3), Pair.of("four", 4)))
                .fill(Stream.of(Pair.of("seven", 7), Pair.of("eight", 8)))
                .map()
                .entrySet();

        assertThat(entries, hasSize(8));

        assertThat(
                entries,
                hasItems(
                        immutableEntry("one", 1),
                        immutableEntry("two", 2),
                        immutableEntry("three", 3),
                        immutableEntry("four", 4),
                        immutableEntry("five", 5),
                        immutableEntry("six", 6),
                        immutableEntry("seven", 7),
                        immutableEntry("eight", 8)
                )
        );
    }
}
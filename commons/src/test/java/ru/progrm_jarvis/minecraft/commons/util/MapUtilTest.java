package ru.progrm_jarvis.minecraft.commons.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilTest {

    @Test
    void testFillMap() {
        assertEquals(new HashMap<>(), MapUtil.fillMap(new HashMap<>()));

        assertEquals(new HashMap<Integer, String>() {{
            put(1, "Hello");
            put(2, "world");
        }}, MapUtil.fillMap(new HashMap<>(), 1, "Hello", 2, "world"));

        assertNotEquals(new HashMap<Integer, String>() {{
            put(1, "Hello");
        }}, MapUtil.fillMap(new HashMap<>(), 1, "Hello", 2, "world"));

        assertThrows(IllegalArgumentException.class, () -> MapUtil.fillMap(new HashMap<>(), 1));

        assertThrows(IllegalArgumentException.class, () -> MapUtil.fillMap(new HashMap<>(), 1, 3, "String"));
    }
}
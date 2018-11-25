package ru.progrm_jarvis.minecraft.commons.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ObjectUtilTest {

    @Test
    void testNonNull() {
        assertEquals("foo", ObjectUtil.nonNull("foo"));
        assertEquals("foo", ObjectUtil.nonNull("foo", null));
        assertEquals("foo", ObjectUtil.nonNull(null, "foo", null));
        assertEquals("foo", ObjectUtil.nonNull(null, "foo"));
        assertEquals("foo", ObjectUtil.nonNull("foo", "bar"));
        assertEquals("foo", ObjectUtil.nonNull("foo", null, "bar"));
        assertEquals("foo", ObjectUtil.nonNull(null, "foo", null, "bar"));
        assertEquals("foo", ObjectUtil.nonNull(null, "foo", "bar"));
        assertNull(ObjectUtil.nonNull((Object) null));
        assertNull(ObjectUtil.nonNull(null, null));
        assertNull(ObjectUtil.nonNull(null, null, null));
    }

    @Test
    void testOptionalNonNull() {
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull("foo"));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull("foo", null));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull(null, "foo", null));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull(null, "foo"));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull("foo", "bar"));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull("foo", null, "bar"));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull(null, "foo", null, "bar"));
        assertEquals(Optional.of("foo"), ObjectUtil.optionalNonNull(null, "foo", "bar"));
        assertEquals(Optional.empty(), ObjectUtil.optionalNonNull((Object) null));
        assertEquals(Optional.empty(), ObjectUtil.optionalNonNull(null, null));
        assertEquals(Optional.empty(), ObjectUtil.optionalNonNull(null, null, null));
    }

    @Test
    void testOnlyNonNull() {
        assertEquals("foo", ObjectUtil.onlyNonNull("foo"));
        assertEquals("foo", ObjectUtil.onlyNonNull("foo", null));
        assertEquals("foo", ObjectUtil.onlyNonNull(null, "foo", null));
        assertEquals("foo", ObjectUtil.onlyNonNull(null, "foo"));
        assertEquals("foo", ObjectUtil.onlyNonNull("foo", "bar"));
        assertEquals("foo", ObjectUtil.onlyNonNull("foo", null, "bar"));
        assertEquals("foo", ObjectUtil.onlyNonNull(null, "foo", null, "bar"));
        assertEquals("foo", ObjectUtil.onlyNonNull(null, "foo", "bar"));
        assertThrows(NullPointerException.class, () -> ObjectUtil.onlyNonNull((Object) null));
        assertThrows(NullPointerException.class, () -> ObjectUtil.onlyNonNull(null, null));
        assertThrows(NullPointerException.class, () -> ObjectUtil.onlyNonNull(null, null, null));
    }
}
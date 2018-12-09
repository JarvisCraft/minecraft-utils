package ru.progrm_jarvis.minecraft.commons.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.minecraft.commons.util.ObjectUtil.*;

class ObjectUtilTest {

    @Test
    void testNonNull() {
        assertEquals("foo", nonNull("foo"));
        assertEquals("foo", nonNull("foo", null));
        assertEquals("foo", nonNull(null, "foo", null));
        assertEquals("foo", nonNull(null, "foo"));
        assertEquals("foo", nonNull("foo", "bar"));
        assertEquals("foo", nonNull("foo", null, "bar"));
        assertEquals("foo", nonNull(null, "foo", null, "bar"));
        assertEquals("foo", nonNull(null, "foo", "bar"));
        assertNull(nonNull((Object) null));
        assertNull(nonNull(null, null));
        assertNull(nonNull(null, null, null));
    }

    @Test
    void testOptionalNonNull() {
        assertEquals(Optional.of("foo"), optionalNonNull("foo"));
        assertEquals(Optional.of("foo"), optionalNonNull("foo", null));
        assertEquals(Optional.of("foo"), optionalNonNull(null, "foo", null));
        assertEquals(Optional.of("foo"), optionalNonNull(null, "foo"));
        assertEquals(Optional.of("foo"), optionalNonNull("foo", "bar"));
        assertEquals(Optional.of("foo"), optionalNonNull("foo", null, "bar"));
        assertEquals(Optional.of("foo"), optionalNonNull(null, "foo", null, "bar"));
        assertEquals(Optional.of("foo"), optionalNonNull(null, "foo", "bar"));
        assertEquals(Optional.empty(), optionalNonNull((Object) null));
        assertEquals(Optional.empty(), optionalNonNull(null, null));
        assertEquals(Optional.empty(), optionalNonNull(null, null, null));
    }

    @Test
    void testNonNullOrThrow() {
        assertEquals("foo", nonNullOrThrow("foo"));
        assertEquals("foo", nonNullOrThrow("foo", null));
        assertEquals("foo", nonNullOrThrow(null, "foo", null));
        assertEquals("foo", nonNullOrThrow(null, "foo"));
        assertEquals("foo", nonNullOrThrow("foo", "bar"));
        assertEquals("foo", nonNullOrThrow("foo", null, "bar"));
        assertEquals("foo", nonNullOrThrow(null, "foo", null, "bar"));
        assertEquals("foo", nonNullOrThrow(null, "foo", "bar"));
        assertThrows(NullPointerException.class, () -> nonNullOrThrow((Object) null));
        assertThrows(NullPointerException.class, () -> nonNullOrThrow(null, null));
        assertThrows(NullPointerException.class, () -> nonNullOrThrow(null, null, null));
    }

    @Test
    void testMap() {
        assertEquals("f", ObjectUtil.map("foo", t -> t.substring(0, 1)));
        assertEquals("1", ObjectUtil.map(1, t -> Integer.toString(t)));
        assertNull(ObjectUtil.map(123, t -> null));
        assertThrows(NullPointerException.class, () -> ObjectUtil.map(null, t -> {
            if (t == null) throw new NullPointerException();
            return "nonnull";
        }));
        assertThrows(NullPointerException.class, () -> ObjectUtil.map(null, t -> {
            throw new NullPointerException();
        }));
        assertThrows(IOException.class, () -> ObjectUtil.map(null, t -> {
            throw new IOException();
        }));
    }

    @Test
    void testMapNonNull() {
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), "foo"));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), "foo"));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), "foo", null));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), null, "foo", null));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), null, "foo"));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), "foo", "bar"));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), "foo", null, "bar"));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), null, "foo", null, "bar"));
        assertEquals("f", mapNonNull(t -> t.substring(0, 1), null, "foo", "bar"));
        assertEquals("+", mapNonNull(t -> t == null ? "+" : "-", (String) null));
        assertEquals("+", mapNonNull(t -> t == null ? "+" : "-", (Object) null, null));
        assertEquals("+", mapNonNull(t -> t == null ? "+" : "-", (Object) null, null, null));
    }

    @Test
    void testMapOnlyNonNull() {
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), "foo"));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), "foo"));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), "foo", null));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), null, "foo", null));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), null, "foo"));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), "foo", "bar"));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), "foo", null, "bar"));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), null, "foo", null, "bar"));
        assertEquals("f", mapOnlyNonNull(t -> t.substring(0, 1), null, "foo", "bar"));
        assertNull(mapOnlyNonNull(t -> t.substring(0, 1), (String) null));
        assertNull(mapOnlyNonNull(t -> t.substring(0, 1), (String) null, null));
        assertNull(mapOnlyNonNull(t -> t.substring(0, 1), (String) null, null, null));
    }

    @Test
    void testMapNonNullOrThrow() {
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), "foo"));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), "foo", null));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), null, "foo", null));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), null, "foo"));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), "foo", "bar"));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), "foo", null, "bar"));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), null, "foo", null, "bar"));
        assertEquals("f", mapNonNullOrThrow(t -> t.substring(0, 1), null, "foo", "bar"));
        // mapping function should not be called so the one which allows nulls is used
        assertThrows(NullPointerException.class, () -> mapNonNullOrThrow(Objects::isNull, (Object) null));
        assertThrows(NullPointerException.class, () -> mapNonNullOrThrow(Objects::isNull, (Object) null, null));
        assertThrows(NullPointerException.class, () -> mapNonNullOrThrow(Objects::isNull, (Object) null, null, null));
    }
}
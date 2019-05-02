package ru.progrm_jarvis.minecraft.commons.util;

import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.minecraft.commons.util.UuidUtil.uuidFromBytes;
import static ru.progrm_jarvis.minecraft.commons.util.UuidUtil.uuidToBytes;

class UuidUtilTest {

    @Test
    void testBytesToUuidFailSafe() {
        val random = ThreadLocalRandom.current();
        var iterations = 32 + random.nextInt(33);
        for (int i = 0; i < iterations; i++) {
            val bytes = new byte[random.nextInt(16)];
            assertThrows(IllegalArgumentException.class, () -> uuidFromBytes(bytes));
        }

        iterations = 32 + random.nextInt(33);
        for (int i = 0; i < iterations; i++) {
            val bytes = new byte[17 + random.nextInt(Integer.MAX_VALUE - 16)];
            assertThrows(IllegalArgumentException.class, () -> uuidFromBytes(bytes));
        }
    }

    @Test
    void testUuidToBytesAndOpposite() {
        var iterations = 32 + ThreadLocalRandom.current().nextInt(33);
        for (int i = 0; i < iterations; i++) {
            val uuid = UUID.randomUUID();
            assertEquals(uuid, uuidFromBytes(uuidToBytes(uuid)));
        }
    }

    @Test
    void testBytesToUuidAndOpposite() {
        val random = ThreadLocalRandom.current();
        var iterations = 32 + random.nextInt(33);
        for (int i = 0; i < iterations; i++) {
            val bytes = new byte[16];
            random.nextBytes(bytes);

            assertArrayEquals(bytes, uuidToBytes(uuidFromBytes(bytes)));
        }
    }
}
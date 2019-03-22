package ru.progrm_jarvis.minecraft.commons.util.function.lazy;

import lombok.val;
import lombok.var;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LaziesTest {

    @Test
    void testLazy() {
        val foo = mock(Foo.class);
        val number = new AtomicInteger();

        when(foo.createBar()).thenAnswer(invocation -> "<3" + number.getAndIncrement());

        val lazy = Lazies.lazy(foo::createBar);

        assertFalse(lazy.isInitialized());
        verify(foo, times(0)).createBar();
        assertFalse(lazy.isInitialized());
        verify(foo, times(0)).createBar();

        assertEquals("<3" + 0, lazy.get());
        verify(foo, times(1)).createBar();
        assertTrue(lazy.isInitialized());
        verify(foo, times(1)).createBar();

        assertEquals("<3" + 0, lazy.get());
        verify(foo, times(1)).createBar();
        assertTrue(lazy.isInitialized());
        verify(foo, times(1)).createBar();

        for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertEquals("<3" + 0, lazy.get());
        for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertTrue(lazy.isInitialized());
        for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) {
            if (RandomUtils.nextBoolean()) assertEquals("<3" + 0, lazy.get());
            else assertTrue(lazy.isInitialized());
        }
        for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertEquals("<3" + 0, lazy.get());
        for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertTrue(lazy.isInitialized());
        verify(foo, times(1)).createBar();
    }

    @Test
    void testConcurrentLazy() {
        val foo = mock(Foo.class);
        val number = new AtomicInteger();

        when(foo.createBar()).thenAnswer(invocation -> "<3" + number.getAndIncrement());

        val lazy = Lazies.concurrentLazy(foo::createBar);

        assertFalse(lazy.isInitialized());
        verify(foo, times(0)).createBar();
        assertFalse(lazy.isInitialized());
        verify(foo, times(0)).createBar();

        val workers = 7 + RandomUtils.nextInt(8);
        val executors = Executors.newFixedThreadPool(workers);
        for (int executorId = 0; executorId < workers; executorId++) executors.submit(() -> {
            for (int j = 0; j < 15 + RandomUtils.nextInt(16); j++) {

                assertFalse(lazy.isInitialized());
                verify(foo, times(1)).createBar();
                assertFalse(lazy.isInitialized());
                verify(foo, times(1)).createBar();

                assertEquals("<3" + 0, lazy.get());
                verify(foo, times(1)).createBar();
                assertTrue(lazy.isInitialized());
                verify(foo, times(1)).createBar();

                assertEquals("<3" + 0, lazy.get());
                verify(foo, times(1)).createBar();
                assertTrue(lazy.isInitialized());
                verify(foo, times(1)).createBar();

                for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertEquals("<3" + 0, lazy.get());
                for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertTrue(lazy.isInitialized());
                for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) {
                    if (RandomUtils.nextBoolean()) assertEquals("<3" + 0, lazy.get());
                    else assertTrue(lazy.isInitialized());
                }
                for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertEquals("<3" + 0, lazy.get());
                for (var i = 0; i < 127 + RandomUtils.nextInt(128); i++) assertTrue(lazy.isInitialized());
                verify(foo, times(1)).createBar();
            }
        });
    }

    @FunctionalInterface
    private interface Foo {

        Object createBar();
    }
}
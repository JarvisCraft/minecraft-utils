package ru.progrm_jarvis.minecraft.commons.util.number;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.progrm_jarvis.minecraft.commons.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

@RunWith(Parameterized.class)
class IntWrapperTest {

    static List<Arguments> provideIntWrappers() {
        val arguments = new ArrayList<Arguments>();

        arguments.add(of(IntWrapper.create()));
        arguments.add(of(IntWrapper.createAtomic()));

        for (var i = 0; i < 8 + RandomUtils.nextInt(8); i++) arguments
                .add(of(IntWrapper.create(RandomUtils.nextInt())));
        for (var i = 0; i < 8 + RandomUtils.nextInt(8); i++) arguments
                .add(of(IntWrapper.create(-RandomUtils.nextInt())));
        for (var i = 0; i < 8 + RandomUtils.nextInt(8); i++) arguments
                .add(of(IntWrapper.createAtomic(RandomUtils.nextInt())));
        for (var i = 0; i < 8 + RandomUtils.nextInt(8); i++) arguments
                .add(of(IntWrapper.createAtomic(-RandomUtils.nextInt())));

        return arguments;
    }

    @Test
    void testCreate() {
        assertDoesNotThrow((ThrowingSupplier<IntWrapper>) IntWrapper::create);
        for (var i = 0; i < 128; i++) assertDoesNotThrow(() -> IntWrapper.create(RandomUtils.nextInt()));
    }

    @Test
    void testCreateAtomic() {
        assertDoesNotThrow((ThrowingSupplier<IntWrapper>) IntWrapper::createAtomic);
        for (var i = 0; i < 128; i++) assertDoesNotThrow(() -> IntWrapper.createAtomic(RandomUtils.nextInt()));
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetValueOfSpecificNumericType(@NonNull final IntWrapper wrapper) {
        val value = wrapper.get();

        assertEquals((byte) value, wrapper.byteValue());
        assertEquals((short) value, wrapper.shortValue());
        assertEquals(value, wrapper.intValue());
        assertEquals((long) value, wrapper.longValue());
        assertEquals((float) value, wrapper.floatValue());
        assertEquals((double) value, wrapper.doubleValue());
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetSet(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val value = RandomUtils.nextInt();
            wrapper.set(value);
            assertEquals(value, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetAndIncrement(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val value = wrapper.get();
            assertEquals(value, wrapper.getAndIncrement());
            assertEquals(value + 1, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testIncrementAndGet(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val newValue = wrapper.get() + 1;
            assertEquals(newValue, wrapper.incrementAndGet());
            assertEquals(newValue, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetAndDecrement(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val value = wrapper.get();
            assertEquals(value, wrapper.getAndDecrement());
            assertEquals(value - 1, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testDecrementAndGet(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val newValue = wrapper.get() - 1;
            assertEquals(newValue, wrapper.decrementAndGet());
            assertEquals(newValue, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetAndAdd(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val delta = RandomUtils.nextInt() * RandomUtil.randomSign();

            val value = wrapper.get();
            assertEquals(value, wrapper.getAndAdd(delta));
            assertEquals(value + delta, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testAddAndGet(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val delta = RandomUtils.nextInt() * RandomUtil.randomSign();

            val newValue = wrapper.get() + delta;
            assertEquals(newValue, wrapper.addAndGet(delta));
            assertEquals(newValue, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetAndUpdate(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val function = randomIntUnaryFunction();

            val value = wrapper.get();
            assertEquals(value, wrapper.getAndUpdate(function));
            assertEquals(function.applyAsInt(value), wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testUpdateAndGet(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val function = randomIntUnaryFunction();

            val newValue = function.applyAsInt(wrapper.get());
            assertEquals(newValue, wrapper.updateAndGet(function));
            assertEquals(newValue, wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testGetAndAccumulate(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val function = randomIntBinaryFunction();
            val updateValue = RandomUtils.nextInt();

            val value = wrapper.get();
            assertEquals(value, wrapper.getAndAccumulate(updateValue, function));
            assertEquals(function.applyAsInt(value, updateValue), wrapper.get());
        }
    }

    @ParameterizedTest
    @MethodSource("provideIntWrappers")
    void testAccumulateAndGet(@NonNull final IntWrapper wrapper) {
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            val function = randomIntBinaryFunction();
            val updateValue = RandomUtils.nextInt();

            val newValue = function.applyAsInt(wrapper.get(), updateValue);
            assertEquals(newValue, wrapper.accumulateAndGet(updateValue, function));
            assertEquals(newValue, wrapper.get());
        }
    }

    private static IntUnaryOperator randomIntUnaryFunction() {
        val randomValue = RandomUtils.nextBoolean() ? RandomUtils.nextInt() : -RandomUtils.nextInt();

        switch (RandomUtils.nextInt(4)) {
            case 0: return i -> i += randomValue;
            case 1: return i -> i -= randomValue;
            case 2: return i -> i *= randomValue;
            case 3: return i -> i /= randomValue;
            default: throw new IllegalStateException("RandomUtils.nextInt(4) returned an illegal value");
        }
    }

    private static IntBinaryOperator randomIntBinaryFunction() {
        switch (RandomUtils.nextInt(4)) {
            case 0: return (i, k) -> i += k;
            case 1: return (i, k) -> i -= k;
            case 2: return (i, k) -> i *= k;
            case 3: return (i, k) -> i /= k;
            default: throw new IllegalStateException("RandomUtils.nextInt(4) returned an illegal value");
        }
    }
}
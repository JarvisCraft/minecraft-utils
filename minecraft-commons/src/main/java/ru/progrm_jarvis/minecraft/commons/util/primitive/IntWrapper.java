package ru.progrm_jarvis.minecraft.commons.util.primitive;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * Wrapper for an {@link int} focused at providing polymorphism to its operations concurrency.
 */
public abstract class IntWrapper extends Number {

    /**
     * Gets the value.
     *
     * @return value
     */
    abstract int get();

    /**
     * Sets the value.
     *
     * @param value value to be set
     */
    abstract void set(int value);

    /**
     * Gets the value after what it gets incremented.
     *
     * @return value before increment
     */
    abstract int getAndIncrement();

    /**
     * Increments the value after what it is returned.
     *
     * @return value after increment
     */
    abstract int incrementAndGet();

    /**
     * Gets the value after what it gets decremented.
     *
     * @return value before decrement
     */
    abstract int getAndDecrement();

    /**
     * Decrements the value after what it is returned.
     *
     * @return value after decrement
     */
    abstract int decrementAndGet();

    /**
     * Gets the value after what delta is added to it.
     *
     * @param delta the value which should be added to the current value
     * @return value before addition
     */
    abstract int getAndAdd(int delta);

    /**
     * Adds the delta to the value after what it is returned.
     *
     * @param delta the value which should be added to the current value
     * @return value after addition
     */
    abstract int addAndGet(int delta);

    /**
     * Updates the current value using the specified function after what the new value is returned.
     *
     * @param updateFunction function to be used for updating the value
     * @return value after update
     */
    abstract int getAndUpdate(@NonNull IntUnaryOperator updateFunction);

    /**
     * Gets the value after what it gets updated using the specified function.
     *
     * @param updateFunction function to be used for updating the value
     * @return value after update
     */
    abstract int updateAndGet(@NonNull IntUnaryOperator updateFunction);

    /**
     * Updates the current value using specified function and update value after what the new value is returned.
     *
     * @param updateValue update value (will be passed as the second function parameter)
     * @param accumulatorFunction function to be used for updating the value
     * @return value after update
     */
    abstract int getAndAccumulate(int updateValue, @NonNull IntBinaryOperator accumulatorFunction);

    /**
     * Gets the value after what it gets updated using the specified function and update value.
     *
     * @param updateValue update value (will be passed as the second function parameter)
     * @param accumulatorFunction function to be used for updating the value
     * @return value after update
     */
    abstract int accumulateAndGet(int updateValue, @NonNull IntBinaryOperator accumulatorFunction);

    /**
     * Creates new simple int wrapper.
     *
     * @param value initial value of int wrapper
     * @return created int wrapper
     */
    public static IntWrapper create(final int value) {
        return new IntIntWrapper(value);
    }

    /**
     * Creates new simple int wrapper with initial value set to {@code 0}.
     *
     * @return created int wrapper
     */
    public static IntWrapper create() {
        return new IntIntWrapper();
    }

    /**
     * Creates new atomic int wrapper.
     *
     * @param value initial value of int wrapper
     * @return created int wrapper
     */
    public static IntWrapper createAtomic(final int value) {
        return new AtomicIntegerIntWrapper(value);
    }

    /**
     * Creates new atomic int wrapper with initial value set to {@code 0}.
     *
     * @return created int wrapper
     */
    public static IntWrapper createAtomic() {
        return new AtomicIntegerIntWrapper();
    }

    /**
     * {@link IntWrapper} implementation based on {@link int}.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class IntIntWrapper extends IntWrapper {

        int value;

        @Override
        int get() {
            return value;
        }

        @Override
        void set(final int value) {
            this.value = value;
        }

        @Override
        int getAndIncrement() {
            return value++;
        }

        @Override
        int incrementAndGet() {
            return ++value;
        }

        @Override
        int getAndDecrement() {
            return value--;
        }

        @Override
        int decrementAndGet() {
            return --value;
        }

        @Override
        int getAndAdd(final int delta) {
            val oldValue = value;
            value += delta;

            return oldValue;
        }

        @Override
        int addAndGet(final int delta) {
            return value += delta;
        }

        @Override
        int getAndUpdate(final @NonNull IntUnaryOperator updateFunction) {
            val oldValue = value;
            value = updateFunction.applyAsInt(oldValue);

            return oldValue;
        }

        @Override
        int updateAndGet(final @NonNull IntUnaryOperator updateFunction) {
            return value = updateFunction.applyAsInt(value);
        }

        @Override
        int getAndAccumulate(final int updateValue, final @NonNull IntBinaryOperator accumulatorFunction) {
            val oldValue = value;
            value = accumulatorFunction.applyAsInt(value, updateValue);

            return oldValue;
        }

        @Override
        int accumulateAndGet(final int updateValue, final @NonNull IntBinaryOperator accumulatorFunction) {
            return value = accumulatorFunction.applyAsInt(value, updateValue);
        }

        @Override
        public int intValue() {
            return value;
        }

        @Override
        public long longValue() {
            return value;
        }

        @Override
        public float floatValue() {
            return value;
        }

        @Override
        public double doubleValue() {
            return value;
        }
    }

    /**
     * {@link IntWrapper} implementation based on {@link AtomicInteger}.
     */
    @Value
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class AtomicIntegerIntWrapper extends IntWrapper{

        @NonNull AtomicInteger value;

        /**
         * Creates new atomic integer int wrapper.
         *
         * @param value initial value
         */
        public AtomicIntegerIntWrapper(final int value) {
            this.value = new AtomicInteger(value);
        }

        /**
         * Creates new atomic integer int wrapper with initial value set to {@code 0}.
         */
        public AtomicIntegerIntWrapper() {
            this.value = new AtomicInteger();
        }

        @Override
        int get() {
            return value.get();
        }

        @Override
        void set(final int value) {
            this.value.set(value);
        }

        @Override
        int getAndIncrement() {
            return value.getAndIncrement();
        }

        @Override
        int incrementAndGet() {
            return value.incrementAndGet();
        }

        @Override
        int getAndDecrement() {
            return value.getAndDecrement();
        }

        @Override
        int decrementAndGet() {
            return value.decrementAndGet();
        }

        @Override
        int getAndAdd(final int delta) {
            return value.getAndAdd(delta);
        }

        @Override
        int addAndGet(final int delta) {
            return value.addAndGet(delta);
        }

        @Override
        int getAndUpdate(final @NonNull IntUnaryOperator updateFunction) {
            return value.getAndUpdate(updateFunction);
        }

        @Override
        int updateAndGet(final @NonNull IntUnaryOperator updateFunction) {
            return value.updateAndGet(updateFunction);
        }

        @Override
        int getAndAccumulate(final int updateValue, final @NonNull IntBinaryOperator accumulatorFunction) {
            return value.getAndAccumulate(updateValue, accumulatorFunction);
        }

        @Override
        int accumulateAndGet(final int updateValue, final @NonNull IntBinaryOperator accumulatorFunction) {
            return value.accumulateAndGet(updateValue, accumulatorFunction);
        }

        @Override
        public int intValue() {
            return value.intValue();
        }

        @Override
        public long longValue() {
            return value.longValue();
        }

        @Override
        public float floatValue() {
            return value.floatValue();
        }

        @Override
        public double doubleValue() {
            return value.doubleValue();
        }
    }
}

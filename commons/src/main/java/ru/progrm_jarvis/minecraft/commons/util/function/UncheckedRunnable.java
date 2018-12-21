package ru.progrm_jarvis.minecraft.commons.util.function;

import lombok.SneakyThrows;

@FunctionalInterface
public interface UncheckedRunnable extends Runnable {

    void call() throws Throwable;

    @Override
    @SneakyThrows
    default void run() {
        call();
    }
}

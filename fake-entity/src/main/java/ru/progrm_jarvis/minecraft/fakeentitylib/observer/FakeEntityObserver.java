package ru.progrm_jarvis.minecraft.fakeentitylib.observer;

import ru.progrm_jarvis.minecraft.fakeentitylib.entity.ObservableFakeEntity;

/**
 * An object responsible
 */
public interface FakeEntityObserver<E extends ObservableFakeEntity> {

    E observe(E entity);

    E unobserve(E entity);

    void shutdown();
}

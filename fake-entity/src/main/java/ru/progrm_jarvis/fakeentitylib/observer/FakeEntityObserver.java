package ru.progrm_jarvis.fakeentitylib.observer;

import ru.progrm_jarvis.fakeentitylib.entity.ObservableFakeEntity;

/**
 * An object responsible
 */
public interface FakeEntityObserver<E extends ObservableFakeEntity> {

    E observe(E entity);

    E unobserve(E entity);

    void shutdown();
}

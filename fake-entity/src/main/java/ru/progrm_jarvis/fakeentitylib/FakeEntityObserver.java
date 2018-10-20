package ru.progrm_jarvis.fakeentitylib;

/**
 * An object responsible
 */
public interface FakeEntityObserver<E extends ObservableFakeEntity> {

    void observe(ObservableFakeEntity entity);

    void unobserve(ObservableFakeEntity entity);

    void shutdown();
}

package ru.progrm_jarvis.minecraft.fakeentitylib.entity.observer;

import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.ObservableFakeEntity;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.FakeEntityManager;

/**
 * An object responsible for entity observation.
 *
 * @param <P> type of parent plugin
 * @param <E> type of entity managed
 */
public interface FakeEntityObserver<P extends Plugin, E extends ObservableFakeEntity> extends FakeEntityManager<P, E> {
}

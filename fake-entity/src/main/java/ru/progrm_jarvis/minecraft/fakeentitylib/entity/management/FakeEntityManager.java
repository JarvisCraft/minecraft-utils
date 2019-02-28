package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.player.registry.ShouldBeRegisteredInPlayerRegistry;
import ru.progrm_jarvis.minecraft.commons.util.concurrent.ConcurrentCollections;
import ru.progrm_jarvis.minecraft.commons.plugin.BukkitPluginContainer;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import java.util.*;

/**
 * Basic class for entity management providing basic general functionality for it.
 *
 * @param <P> type of parent plugin
 * @param <E> type of managed entity
 *
 * @implSpec its highly recommended (read <b>necessary</b>) for implementations
 * to store managed entities weakly so that un-managing entity manually is not required
 * as if there are no strong references on it the GC should collect it.
 */
@ShouldBeRegisteredInPlayerRegistry
public interface FakeEntityManager<P extends Plugin, E extends FakeEntity> extends BukkitPluginContainer<P> {

    /**
     * Creates a new weak {@link Set} valid (and <i>recommended</i>) for storing entities
     *
     * @param <E> type of entities stored
     * @return new weak {@link Set} for storing entities
     */
    static <E extends FakeEntity> Set<E> weakEntitySet() {
        return Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Creates a new weak concurrent {@link Set} valid (and <i>recommended</i>) for storing entities
     *
     * @param <E> type of entities stored
     * @return new weak concurrent {@link Set} for storing entities
     */
    static <E extends FakeEntity> Set<E> concurrentWeakEntitySet() {
        return ConcurrentCollections.concurrentSetFromMap(new WeakHashMap<>());
    }

    /**
     * Creates a new weak {@link Set} valid (and <i>recommended</i>) for storing entities
     *
     * @param <E> type of entities stored
     * @return new weak {@link Set} for storing entities
     */
    static <E extends FakeEntity, V> Map<E, V> weakEntityMap() {
        return new WeakHashMap<>();
    }

    /**
     * Creates a new weak concurrent {@link Map} valid (and <i>recommended</i>) for storing entities
     *
     * @param <E> type of entities stored
     * @return new weak concurrent {@link Map} for storing entities
     */
    static <E extends FakeEntity, V> Map<E, V> concurrentWeakEntityMap() {
        return ConcurrentCollections.concurrentMap(new WeakHashMap<>());
    }

    /**
     * Gets amount of entities managed by this manager.
     *
     * @return amount of entities managed
     */
    int managedEntitiesSize();

    /**
     * Gets all entities managed by this manager.
     *
     * @return all entities managed by this manager
     *
     * @apiNote the returned collection is immutable and will prohibit any attempts to modify its contents
     */
    Collection<E> getManagedEntities();

    /**
     * Enables management of specified entity by this manager.
     *
     * @param entity entity to manage
     *
     * @apiNote Normally management should happen until {@link #unmanageEntity(E)} is called with this entity
     * or (in most cases) if the entity is no longer non-weakly referenced.
     */
    void manageEntity(@NonNull E entity);

    /**
     * Disables management of specified entity by this manager.
     *
     * @param entity entity to manage
     *
     * @implSpec normally, this method should not necessarily be called
     * as this interface specification recommends storing entities weakly.
     */
    void unmanageEntity(@NonNull E entity);
}

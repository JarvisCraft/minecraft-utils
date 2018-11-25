package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Simple abstract {@link FakeEntityManager} storing entities in a weak, optionally concurrent set.
 *
 * @param <E> type of entities stored
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class AbstractSetBasedEntityManager<E extends FakeEntity> implements FakeEntityManager<E> {

    protected final Set<E> entities;

    /**
     * Constructs a new AbstractSetBasedEntityManager based on weak set with optional concurrency
     *
     * @param concurrent whether or not this map should be thread-safe
     */
    public AbstractSetBasedEntityManager(final boolean concurrent) {
        this(concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet());
    }

    @Override
    public int managedEntitiesSize() {
        return entities.size();
    }

    @Override
    public Collection<E> getManagedEntities() {
        return entities;
    }

    @Override
    public Collection<E> getManagedEntitiesCollection() {
        return new ArrayList<>(entities);
    }

    @Override
    public void manageEntity(@NonNull final E entity) {
        entities.add(entity);
    }

    @Override
    public void unmanageEntity(@NonNull final E entity) {
        entities.remove(entity);
    }
}

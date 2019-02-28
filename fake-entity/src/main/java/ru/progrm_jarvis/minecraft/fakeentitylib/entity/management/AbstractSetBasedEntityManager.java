package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Simple abstract {@link FakeEntityManager} storing entities in a weak, optionally concurrent set.
 *
 * @param <P> type of parent plugin
 * @param <E> type of entities stored
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractSetBasedEntityManager<P extends Plugin, E extends FakeEntity>
        implements FakeEntityManager<P, E> {

    @NonNull P plugin;
    @NonNull Set<E> entities;
    @NonNull Set<E> entitiesView;

    public AbstractSetBasedEntityManager(@NonNull final P plugin,
                                         @NonNull final Set<E> entities) {
        this.plugin = plugin;
        this.entities = entities;
        this.entitiesView = Collections.unmodifiableSet(entities);
    }

    /**
     * Constructs a new AbstractSetBasedEntityManager based on weak set with optional concurrency
     *
     * @param plugin parent plugin of this manager
     * @param concurrent whether or not this map should be thread-safe
     */
    public AbstractSetBasedEntityManager(@Nonnull final P plugin, final boolean concurrent) {
        this(plugin, concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet());
    }

    @Override
    public P getBukkitPlugin() {
        return plugin;
    }

    @Override
    public int managedEntitiesSize() {
        return entities.size();
    }

    @Override
    public boolean isManaged(@NonNull final E entity) {
        return entities.contains(entity);
    }

    @Override
    public Collection<E> getManagedEntities() {
        return entitiesView;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void manageEntity(@NonNull final E entity) {
        entities.add(entity);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void unmanageEntity(@NonNull final E entity) {
        entities.remove(entity);
    }
}

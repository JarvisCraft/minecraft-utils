package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.ShutdownHooks;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Simple abstract {@link FakeEntityManager} storing entities in a weak, optionally concurrent set.
 *
 * @param <E> type of entities stored
 */
@ToString
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractSetBasedEntityManager<E extends FakeEntity> implements FakeEntityManager<E> {

    @NonNull Plugin plugin;
    @NonNull @ToString.Exclude Set<E> entities;
    @NonNull @ToString.Exclude Set<E> entitiesView;

    @Delegate(types = Shutdownable.class) @NonNull final ShutdownHooks shutdownHooks;

    public AbstractSetBasedEntityManager(@NonNull final Plugin plugin, @NonNull final Set<E> entities) {
        this.plugin = plugin;
        this.entities = entities;
        this.entitiesView = Collections.unmodifiableSet(entities);

        shutdownHooks = ShutdownHooks.createConcurrent(this)
                .registerBukkitShutdownHook(plugin);
    }

    /**
     * Constructs a new AbstractSetBasedEntityManager based on weak set with optional concurrency
     *
     * @param plugin parent plugin of this manager
     * @param concurrent whether or not this map should be thread-safe
     */
    public AbstractSetBasedEntityManager(@Nonnull final Plugin plugin, final boolean concurrent) {
        this(plugin, concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet());
    }

    @Override
    public Plugin getBukkitPlugin() {
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

package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static ru.progrm_jarvis.minecraft.commons.util.hack.PreSuperCheck.beforeSuper;

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

    P plugin;
    Set<E> entities;

    /**
     * Constructs a new AbstractSetBasedEntityManager based on weak set with optional concurrency
     *
     * @param plugin parent plugin of this manager
     * @param concurrent whether or not this map should be thread-safe
     */
    public AbstractSetBasedEntityManager(@Nonnull final P plugin, final boolean concurrent) {
        this(
                beforeSuper(plugin, () -> Preconditions.checkNotNull(plugin, "plugin should be nonnull")),
                concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet()
        );
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

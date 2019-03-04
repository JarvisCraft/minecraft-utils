package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import com.google.common.collect.ImmutableList;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistryRegistration;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;


/**
 * Facade grouping multiple {@link FakeEntityManager}s into a single one.
 * Its general methods delegate the calls to each of the managers.
 *
 * @param <P> type of parent plugin
 * @param <E> type of managed entity
 */
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@PlayerRegistryRegistration(PlayerRegistryRegistration.Policy.MANUAL)
public class FakeEntityManagerGroup<P extends Plugin, E extends FakeEntity>
        extends AbstractSetBasedEntityManager<P, E> {

    @NonNull Collection<FakeEntityManager<P, E>> managers;

    @Override
    public P getBukkitPlugin() {
        return plugin;
    }

    public FakeEntityManagerGroup(@NonNull final P plugin,
                                  @NonNull final Set<E> entities,
                                  @NonNull final Collection<BiFunction<P, Set<E>,
                                          ? extends FakeEntityManager<P, E>>> managerCreators) {
        super(plugin, entities);

        //noinspection unchecked
        managers = ImmutableList.copyOf(managerCreators.stream()
                .map(managerCreator -> managerCreator.apply(plugin, entities))
                .toArray(FakeEntityManager[]::new)
        );
    }

    @SafeVarargs
    public FakeEntityManagerGroup(@NonNull final P plugin,
                                  @NonNull final Set<E> entities,
                                  @NonNull final BiFunction<P, Set<E>,
                                          ? extends FakeEntityManager<P, E>>... managerCreators) {
        this(plugin, entities, Arrays.asList(managerCreators));
    }

    @Override
    public void manageEntity(@NonNull final E entity) {
        super.manageEntity(entity);

        for (val manager : managers) manager.manageEntity(entity);
    }

    @Override
    public void unmanageEntity(@NonNull final E entity) {
        super.unmanageEntity(entity);

        for (val manager : managers) manager.unmanageEntity(entity);
    }

    @Override
    public void remove(@NonNull final E entity) {
        for (val manager : managers) manager.unmanageEntity(entity);

        entity.remove();
    }

    @Override
    public void shutdown() {
        for (val manager : managers) manager.shutdown();

        super.shutdown();
    }
}

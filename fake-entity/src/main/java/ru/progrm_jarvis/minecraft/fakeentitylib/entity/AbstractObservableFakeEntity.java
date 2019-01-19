package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Base for most common implementations of {@link ObservableFakeEntity}.
 */
@ToString
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractObservableFakeEntity extends AbstractFakeEntity implements ObservableFakeEntity {

    @Getter boolean global;
    @Getter int viewDistance;
    @Getter int viewDistanceSquared;

    public AbstractObservableFakeEntity(final boolean global, final int viewDistance,
                                        @NonNull final Location location) {
        super(location);

        this.global = global;
        this.viewDistance = viewDistance;
        viewDistanceSquared = viewDistance * viewDistance;
    }

    @Override
    public boolean shouldSee(final Player player) {
        return player.getWorld() == location.getWorld()
                && player.getEyeLocation().distanceSquared(location) <= viewDistanceSquared;
    }
}

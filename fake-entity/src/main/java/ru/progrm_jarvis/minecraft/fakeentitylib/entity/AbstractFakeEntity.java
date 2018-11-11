package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Base for most common implementations of {@link ObservableFakeEntity}.
 */
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractFakeEntity implements ObservableFakeEntity {

    @NonNull @Getter Location location;

    @Override
    public World getWorld() {
        return location.getWorld();
    }
}

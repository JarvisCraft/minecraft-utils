package ru.progrm_jarvis.minecraft.commons.player.registry;

import java.lang.annotation.*;

/**
 * Marker to indicate that the annotated type self-registered in {@link PlayerRegistries} whenever instantiated.
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RegistersSelfInPlayerRegistry {
}

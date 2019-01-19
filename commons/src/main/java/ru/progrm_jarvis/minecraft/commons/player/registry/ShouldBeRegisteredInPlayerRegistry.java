package ru.progrm_jarvis.minecraft.commons.player.registry;

import java.lang.annotation.*;

/**
 * Marker to indicate that the annotated type should be registered in {@link PlayerRegistry} whenever instantiated.
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ShouldBeRegisteredInPlayerRegistry {
}
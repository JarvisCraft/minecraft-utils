package ru.progrm_jarvis.minecraft.commons.annotation;

import java.lang.annotation.*;

/**
 * Marker indicating that {@link Object#hashCode()} and {@link Object#equals(Object)} methods
 * are not overridden for some reason and so (in most cases) should not be overridden.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(value = ElementType.TYPE)
public @interface DontOverrideEqualsAndHashCode {

    /**
     * The reason why this object's {@link Object#hashCode()} and {@link Object#equals(Object)}
     * methods are not overridden.
     *
     * @return reason for not overriding {@link Object#hashCode()} and {@link Object#equals(Object)} methods
     */
    String value() default "";
}

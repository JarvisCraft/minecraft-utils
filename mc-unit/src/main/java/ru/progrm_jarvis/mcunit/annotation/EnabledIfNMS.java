package ru.progrm_jarvis.mcunit.annotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.lang.annotation.*;

/**
 * Indicates that the test should only be run if current environment has NMS-classes.
 */
@Test
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EnabledIf(
        value = "Java.type(\"ru.progrm_jarvis.mcunit.util.NmsTestUtil\").isEnvironmentNms()",
        reason = "Current environment doesn't have NMS classes needed for testing"
)
public @interface EnabledIfNMS {}

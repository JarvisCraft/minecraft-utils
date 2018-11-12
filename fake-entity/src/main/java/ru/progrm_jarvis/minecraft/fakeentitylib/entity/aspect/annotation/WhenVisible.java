package ru.progrm_jarvis.minecraft.fakeentitylib.entity.aspect.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import java.lang.annotation.*;

/**
 * Aspect annotation to perform method call only when the fake entity is visible.
 * If the method returns an object then if an entity is invisible then {@code null} will be returned
 *
 * @apiNote can be used only on {@link FakeEntity} implementations
 */
// @Inherited NOT used because JVM will not inherit it for methods ¯\_(ツ)_/¯
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhenVisible {

    @Aspect
    class AspectJ {

        @Around("@annotation(WhenVisible)")
        public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
            if (((FakeEntity) joinPoint.getTarget()).isVisible()) return joinPoint.proceed();
            return null;
        }
    }
}
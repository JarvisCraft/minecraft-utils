package ru.progrm_jarvis.nmsutils;

import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.Vector3F;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utilities for <i>NSM {@literal <}{@literal >} API</i> conversions
 */
@UtilityClass
public class Conversions {

    // simple objects
    private final EquivalentConverter<Vector3F> VECTOR_3_F_EQUIVALENT_CONVERTER = Vector3F.getConverter();
    // enum wrappers
    private final EquivalentConverter<Direction> DIRECTION_EQUIVALENT_CONVERTER = EnumWrappers.getDirectionConverter();
    private final EquivalentConverter<Particle> PARTICLE_EQUIVALENT_CONVERTER = EnumWrappers.getParticleConverter();

    public static Object toNms(@NonNull final Vector3F vector3F) {
        return VECTOR_3_F_EQUIVALENT_CONVERTER.getGeneric(vector3F);
    }

    public static Vector3F toVector3F(@NonNull final Object nms) {
        return VECTOR_3_F_EQUIVALENT_CONVERTER.getSpecific(nms);
    }

    public static Object toNms(@NonNull final Direction direction) {
        return DIRECTION_EQUIVALENT_CONVERTER.getGeneric(direction);
    }

    public static Direction toDirection(@NonNull final Object nms) {
        return DIRECTION_EQUIVALENT_CONVERTER.getSpecific(nms);
    }

    public static Object toNms(@NonNull final Particle particle) {
        return PARTICLE_EQUIVALENT_CONVERTER.getGeneric(particle);
    }

    public static Particle toParticle(@NonNull final Object nms) {
        return PARTICLE_EQUIVALENT_CONVERTER.getSpecific(nms);
    }
}

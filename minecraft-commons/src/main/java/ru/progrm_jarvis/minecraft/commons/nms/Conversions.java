package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.Vector3F;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

/**
 * Utilities for <i>NSM {@literal <}{@literal >} API</i> conversions
 */
@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class Conversions {

    // simple objects
    EquivalentConverter<Vector3F> VECTOR_3_F_EQUIVALENT_CONVERTER = Vector3F.getConverter();
    // enum wrappers
    EquivalentConverter<Direction> DIRECTION_EQUIVALENT_CONVERTER = EnumWrappers.getDirectionConverter();
    EquivalentConverter<Particle> PARTICLE_EQUIVALENT_CONVERTER = EnumWrappers.getParticleConverter();

    public Object toNms(final @NonNull Vector3F vector3F) {
        return VECTOR_3_F_EQUIVALENT_CONVERTER.getGeneric(vector3F);
    }

    public Vector3F toVector3F(final @NonNull Object nms) {
        return VECTOR_3_F_EQUIVALENT_CONVERTER.getSpecific(nms);
    }

    public Object toNms(final @NonNull Direction direction) {
        return DIRECTION_EQUIVALENT_CONVERTER.getGeneric(direction);
    }

    public Direction toDirection(final @NonNull Object nms) {
        return DIRECTION_EQUIVALENT_CONVERTER.getSpecific(nms);
    }

    public Object toNms(final @NonNull Particle particle) {
        return PARTICLE_EQUIVALENT_CONVERTER.getGeneric(particle);
    }

    public Particle toParticle(final @NonNull Object nms) {
        return PARTICLE_EQUIVALENT_CONVERTER.getSpecific(nms);
    }
}

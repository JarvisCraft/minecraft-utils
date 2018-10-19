package ru.progrm_jarvis.nmsutils;

import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.Vector3F;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utilities for <i>NSM {@literal <}{@literal >} API</i> convertions
 */
@UtilityClass
public class Convertions {

    private final EquivalentConverter<Vector3F> VECTOR_3_F_EQUIVALENT_CONVERTER = Vector3F.getConverter();

    public static Object toNms(@NonNull final Vector3F vector3F) {
        return VECTOR_3_F_EQUIVALENT_CONVERTER.getGeneric(vector3F);
    }

    public static Vector3F toVector3F(@NonNull final Object nms) {
        return VECTOR_3_F_EQUIVALENT_CONVERTER.getSpecific(nms);
    }
}

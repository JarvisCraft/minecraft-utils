package ru.progrm_jarvis.minecraft.commons.util;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

import java.util.Map;

/**
 * Utilities related to {@link Map>}.
 */
@UtilityClass
public class MapUtil {

    /**
     * Fills the map specified with the values specified.
     *
     * @param map map to fill with the values
     * @param keyValuePairs pairs of keys and values in order <i>key1, value1, key2, value2, key3, value3...</i>
     * @param <K> type of keys
     * @param <V> type of values
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     * @throws IllegalArgumentException if {@code keyValuePairs}'s length is odd
     */
    @SuppressWarnings("unchecked")
    public <K, V, M extends Map<K, V>> M fillMap(@NonNull final M map, @NonNull final Object... keyValuePairs) {
        val length = keyValuePairs.length;
        Preconditions.checkArgument(length % 2 == 0, "Key-Value pairs array should have an even number of elements");

        { // filling of the map
            var value = true; // will get reverted for the first value

            K key = null; // requires to be initialized for some reason :)
            for (final Object keyValuePair : keyValuePairs) if (value = !value) map.put(key, (V) keyValuePair);
            else key = (K) keyValuePair;
        }

        return map;
    }
}

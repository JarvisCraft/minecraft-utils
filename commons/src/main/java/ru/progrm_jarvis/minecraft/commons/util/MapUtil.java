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
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     * @throws IllegalArgumentException if {@code keyValuePairs}'s length is odd
     *
     * @see #fillMap(Map, Object, Object, Object...)
     */
    public <M extends Map<?, ?>> M fillMap(@NonNull final M map, @NonNull final Object... keyValuePairs) {
        val length = keyValuePairs.length;
        if (length == 0) return map;
        Preconditions.checkArgument(length % 2 == 0, "Key-Value pairs array should have an even number of elements");

        fillMapNoChecks(map, keyValuePairs);

        return map;
    }

    /**
     * Fills the map specified with the values specified.
     *
     * @param map map to fill with the values
     * @param keyValuePairs pairs of keys and values in order <i>key1, value1, key2, value2, key3, value3...</i>
     * @param firstValueKey the key of first value
     * @param firstValue first value to be put to the map
     * @param <K> type of keys
     * @param <V> type of values
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     * @throws IllegalArgumentException if {@code keyValuePairs}'s length is odd
     *
     * @see #fillMap(Map, Object...)
     */
    public <K, V, M extends Map<K, V>> M fillMap(@NonNull final M map, final K firstValueKey, final V firstValue,
                                                 @NonNull final Object... keyValuePairs) {
        val length = keyValuePairs.length;
        Preconditions.checkArgument(length % 2 == 0, "Key-Value pairs array should have an even number of elements");

        map.put(firstValueKey, firstValue);
        fillMapNoChecks(map, keyValuePairs);

        return map;
    }

    @SuppressWarnings("unchecked")
    private void fillMapNoChecks(final Map map, final Object... keyValuePairs) {
        var value = true; // will get reverted for the first value

        Object key = null; // requires to be initialized for some reason :)
        for (final Object keyValuePair : keyValuePairs) if (value = !value) map.put(key, keyValuePair);
        else key = keyValuePair;
    }
}

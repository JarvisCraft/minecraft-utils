package ru.progrm_jarvis.minecraft.commons.util;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

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
     * @param firstValueKey the key of first value
     * @param firstValue first value to be put to the map
     * @param keyValuePairs pairs of keys and values in order <i>key1, value1, key2, value2, key3, value3...</i>
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

    /**
     * Fills the map specified with the values specified.
     *
     * @param map map to fill with the values
     * @param entries entries to fill the map with
     * @param <K> type of keys
     * @param <V> type of values
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     */
    public <K, V, M extends Map<K, V>> M fillMap(@NonNull final M map, final Iterator<Pair<K, V>> entries) {
        while (entries.hasNext()) {
            val entry = entries.next();
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    /**
     * Fills the map specified with the values specified.
     *
     * @param map map to fill with the values
     * @param entries entries to fill the map with
     * @param <K> type of keys
     * @param <V> type of values
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     */
    public <K, V, M extends Map<K, V>> M fillMap(@NonNull final M map, final Iterable<Pair<K, V>> entries) {
        return fillMap(map, entries.iterator());
    }

    /**
     * Fills the map specified with the values specified.
     *
     * @param map map to fill with the values
     * @param entries entries to fill the map with
     * @param <K> type of keys
     * @param <V> type of values
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     */
    public <K, V, M extends Map<K, V>> M fillMap(@NonNull final M map, final Stream<Pair<K, V>> entries) {
        entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    /**
     * Fills the map specified with the values specified keeping order.
     *
     * @param map map to fill with the values
     * @param entries entries to fill the map with
     * @param <K> type of keys
     * @param <V> type of values
     * @param <M> map type
     * @return the map passed filled with key-value pairs specified
     */
    public <K, V, M extends Map<K, V>> M fillMapOrdered(@NonNull final M map, final Stream<Pair<K, V>> entries) {
        entries.forEachOrdered(entry -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    /**
     * Creates new {@link MapFiller} from the map specified.
     *
     * @param map map for which to create the filler
     * @param <K> type of keys
     * @param <V> type of values
     * @return map filler created for the specified map
     *
     * @see MapFiller
     * @see #mapFiller(Map, Object, Object)
     */
    public <K, V> MapFiller<K, V> mapFiller(@NonNull final Map<K, V> map) {
        return new MapFiller<>(map);
    }

    /**
     * Creates new {@link MapFiller} from the map specified initialized with the value specified.
     *
     * @param map map for which to create the filler
     * @param firstValueKey the key of first value
     * @param firstValue first value to be put to the map
     * @param <K> type of keys
     * @param <V> type of values
     * @return map filler created for the specified map with initial value put
     *
     * @see MapFiller
     * @see #mapFiller(Map)
     */
    public <K, V> MapFiller<K, V> mapFiller(@NonNull final Map<K, V> map, K firstValueKey, final V firstValue) {
        return new MapFiller<>(map)
                .put(firstValueKey, firstValue);
    }

    /**
     * An utility-object to fill the map following the chain pattern which may useful when initializing class fields.
     *
     * @param <K> type of map's key
     * @param <V> type of map's value
     */
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    public static final class MapFiller<K, V> {

        /**
         * Map being filled
         */
        @NonNull @Getter private final Map<K, V> map;

        /**
         * Puts the specified value by the specified key to the map.
         *
         * @param key key of the value to put
         * @param value value to put by the key
         * @return this map filler for chaining
         */
        public MapFiller<K, V> put(final K key, final V value) {
            map.put(key, value);

            return this;
        }

        /**
         * Fills the map based on the specified {@link Iterator}.
         *
         * @param entries iterator of the entries whose elements will be put to the map
         * @return this map filler for chaining
         */
        public MapFiller<K, V> fill(final Iterator<Pair<K, V>> entries) {
            while (entries.hasNext()) {
                val entry = entries.next();
                map.put(entry.getKey(), entry.getValue());
            }

            return this;
        }

        /**
         * Fills the map with the values of specified {@link Iterable}.
         *
         * @param entries entries which will be put to the map
         * @return this map filler for chaining
         */
        public MapFiller<K, V> fill(final Iterable<Pair<K, V>> entries) {
            for (val entry : entries) map.put(entry.getKey(), entry.getValue());

            return this;
        }

        /**
         * Fills the map based on the specified {@link Stream}.
         *
         * @param entries stream of the entries whose elements will be put to the map
         * @return this map filler for chaining
         */
        public MapFiller<K, V> fill(final Stream<Pair<K, V>> entries) {
            entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));

            return this;
        }

        /**
         * Fills the map based on the specified {@link Stream} keeping order.
         *
         * @param entries stream of the entries whose elements will be put to the map
         * @return this map filler for chaining
         */
        public MapFiller<K, V> fillOrdered(final Stream<Pair<K, V>> entries) {
            entries.forEachOrdered(entry -> map.put(entry.getKey(), entry.getValue()));

            return this;
        }
    }
}

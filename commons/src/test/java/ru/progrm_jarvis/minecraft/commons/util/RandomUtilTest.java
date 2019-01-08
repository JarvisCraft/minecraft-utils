package ru.progrm_jarvis.minecraft.commons.util;

import lombok.var;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomUtilTest {

    @Test
    void testGetRandomFromMapOfChances() {
        var values = MapUtil.fillMap(new HashMap<>(), "One", 1, "Two", 2);
        var keySet = values.keySet();
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                RandomUtil.getRandom(values), isIn(keySet)
        );

        values = MapUtil.fillMap(new HashMap<>(), "One", 1, "Two", 2, "Three", 3, "Four", 4, "Five", 5);
        keySet = values.keySet();
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                RandomUtil.getRandom(values), isIn(keySet)
        );

        values = MapUtil.fillMap(new HashMap<>(), "Hi", 1);
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                "Hi", equalTo(RandomUtil.getRandom(values))
        );

        assertThrows(IllegalArgumentException.class, () -> RandomUtil.getRandom(new HashMap<>()));
    }

    @Test
    void testGetRandomFromList() {
        var values = new ArrayList<String>();
        for (var i = 0; i < 64 + RandomUtils.nextInt(65); i++) values.add(Integer.toString(RandomUtils.nextInt()));

        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                RandomUtil.getRandom(values), isIn(values)
        );

        assertThrows(IllegalArgumentException.class, () -> RandomUtil.getRandom(new ArrayList<>()));
    }

    @Test
    void testGetRandomFromCollection() {
        final Collection<String> values;
        { // hide List type
            var valuesList = new ArrayList<String>();
            for (var i = 0; i < 64 + RandomUtils.nextInt(65); i++) valuesList.add(Integer.toString(RandomUtils.nextInt()));
            values = valuesList;
        }

        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                RandomUtil.getRandom(values), isIn(values)
        );

        assertThrows(IllegalArgumentException.class, () -> RandomUtil.getRandom(new HashSet<>()));
    }
}
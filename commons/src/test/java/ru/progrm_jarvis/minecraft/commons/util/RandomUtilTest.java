package ru.progrm_jarvis.minecraft.commons.util;

import lombok.var;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomUtilTest {

    @Test
    void testPeekRandom() {
        var values = MapUtil.fillMap(new HashMap<>(), "One", 1, "Two", 2);
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                values.keySet(), hasItem(RandomUtil.peekRandom(values))
        );

        values = MapUtil.fillMap(new HashMap<>(), "One", 1, "Two", 2, "Three", 3, "Four", 4, "Five", 5);
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                values.keySet(), hasItem(RandomUtil.peekRandom(values))
        );

        values = MapUtil.fillMap(new HashMap<>(), "Hi", 1);
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) assertThat(
                "Hi", equalTo(RandomUtil.peekRandom(values))
        );

        assertThrows(IllegalArgumentException.class, () -> RandomUtil.peekRandom(new HashMap<>()));
    }
}
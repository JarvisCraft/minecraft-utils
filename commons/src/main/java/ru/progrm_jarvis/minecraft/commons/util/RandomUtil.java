package ru.progrm_jarvis.minecraft.commons.util;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomUtil {

    public <T> T peekRandom(final Map<T, Integer> chancedValues) {
        {
            val size = chancedValues.size();

            Preconditions.checkArgument(size > 0, "There should be at least one chanced value");
            if (size == 1) return chancedValues.keySet().iterator().next();
        }

        long chancesSum = 0; // sum of all chances
        for (val chance : chancedValues.values()) {
            Preconditions.checkArgument(chance > 0, "Chances should all be positive");
            chancesSum += chance;
        }
        // the chance should be up to chancesSum (exclusive)
        val chance = ThreadLocalRandom.current().nextLong(chancesSum);
        for (val entry : chancedValues.entrySet()) if ((chancesSum -= entry.getValue()) <= chance) return
                entry.getKey();

        throw new IllegalStateException("Could not peek any chanced value");
    }
}

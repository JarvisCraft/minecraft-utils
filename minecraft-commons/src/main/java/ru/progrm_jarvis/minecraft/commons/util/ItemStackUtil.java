package ru.progrm_jarvis.minecraft.commons.util;

import lombok.experimental.UtilityClass;
import lombok.var;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for {@link ItemStack}-related functionality.
 */
@UtilityClass
public class ItemStackUtil {

    /**
     * Deeply copies the given array of {@link ItemStack}s.
     *
     * @param items array to be deeply copied
     * @return deep copy of the given items array
     *
     * @implNote {@link ItemStack#clone()} is used for cloning
     */
    public @NotNull ItemStack @Nullable [] deepCopy(final @NotNull ItemStack[] items) {
        final int length;
        final ItemStack[] copy = new ItemStack[length = items.length];
        for (var i = 0; i < length; i++) copy[i] = items[i].clone();

        return copy;
    }
}

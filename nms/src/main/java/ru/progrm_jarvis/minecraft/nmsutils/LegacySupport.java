package ru.progrm_jarvis.minecraft.nmsutils;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import ru.progrm_jarvis.minecraft.commons.util.MapUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Various utilities used for supporting older versions of Minecraft-related environment,
 * such as changed enum names and updated method signatures.
 */
@UtilityClass
public class LegacySupport {

    private static final boolean LEGACY_MATERIALS = NmsUtil.getVersion().getGeneration() < 13;

    private static final Map<String, LegacyItem> legacyItems = MapUtil.mapFiller(new HashMap<String, LegacyItem>())
            // TODO or find better solution .put("CAVE_AIR", new LegacyItem("CAVE_AIR", "AIR"))
            // .put("VOID_AIR", new LegacyItem("VOID_AIR", "AIR"))
            .map();

    static {
        System.out.println(Arrays.stream(Material.values()).filter(Material::isLegacy).collect(Collectors.toSet()));
    }

    private static LegacyItem legacyItem(@NonNull final Material material, final int legacyData) {
        return new LegacyItem(material, (byte) legacyData);
    }

    private static LegacyItem legacyItem(@NonNull final Material material) {
        return legacyItem(material, 0);
    }

    private static LegacyItem legacyItem(@NonNull final String materialName,
                                         @NonNull final String legacyMaterialName,
                                         final int legacyData) {
        return new LegacyItem(Material.valueOf(LEGACY_MATERIALS ? legacyMaterialName : materialName), legacyData);
    }

    private static LegacyItem legacyItem(@NonNull final String materialName,
                                         @NonNull final String legacyMaterialName) {
        return legacyItem(materialName, legacyMaterialName, 0);
    }

    @Value
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class LegacyItem {
        @NonNull Material material;
        final byte legacyData;

        private LegacyItem(@NonNull final Material material, final int legacyData) {
            this(material, (byte) legacyData);
        }

        private LegacyItem(@NonNull final Material material) {
            this(material, 0);
        }

        private LegacyItem(@NonNull final String materialName, @NonNull final String legacyMaterialName,
                           final int legacyData) {
            this(Material.valueOf(LEGACY_MATERIALS ? legacyMaterialName : materialName), (byte) legacyData);
        }

        private LegacyItem(@NonNull final String materialName, @NonNull final String legacyMaterialName) {
            this(materialName, legacyMaterialName, 0);
        }
    }
}

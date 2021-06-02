package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;
import ru.progrm_jarvis.minecraft.commons.nms.ProtocolLibConversions;

import java.util.Optional;
import java.util.UUID;

/**
 * Editor for Metadata of {@link WrappedWatchableObject} providing classes
 * containing static methods for developer-friendly object creation.
 *
 * @see <a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=7410">1.8 metadata format</a>
 */
@UtilityClass
@SuppressWarnings({"ClassWithOnlyPrivateConstructors", "EmptyClass", "unused", "NonFinalUtilityClass"})
public class MetadataGenerator {

    private final int VERSION = NmsUtil.getVersion().getGeneration();
    private final DataWatcherFactory FACTORY = NmsUtil.getDataWatcherFactory();

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Entity {

        public static WrappedWatchableObject entityFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(0, flagBytes);
        }

        public static WrappedWatchableObject air(final int air) {
            return VERSION >= 9 ? FACTORY.createWatchable(1, air) : FACTORY.createWatchable(1, (short) air);
        }

        public static WrappedWatchableObject name(final WrappedChatComponent name) {
            return FACTORY.createWatchable(2, name);
        }

        public static WrappedWatchableObject name(final String name) {
            if (VERSION >= 13) return name(WrappedChatComponent.fromText(name));
            return FACTORY.createWatchable(2, name);
        }

        public static WrappedWatchableObject nameVisible(final boolean nameVisible) {
            return FACTORY.createWatchable(3, nameVisible);
        }

        public static WrappedWatchableObject silent(final boolean silent) {
            return FACTORY.createWatchable(4, silent);
        }

        public static WrappedWatchableObject noGravity(final boolean noGravity) {
            if (VERSION >= 9) return FACTORY.createWatchable(5, noGravity);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support No Gravity entity flag");
        }

        // TODO add Pose support for late versions (available since 1.14)

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            ON_FIRE((byte) 0x01),
            CROUCHED((byte) 0x02),
            RIDING((byte) 0x04),
            SPRINTING((byte) 0x08),
            INTERACTING((byte) 0x10), // legacy
            SWIMMING((byte) 0x10),
            INVISIBLE((byte) 0x20),
            GLOWING((byte) 0x80);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Projectile extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Snowball extends Projectile {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Egg extends Projectile {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Potion extends Projectile {

        public static WrappedWatchableObject potion(final Object nmsItemStackPotion) {
            if (VERSION >= 9) return FACTORY.createWatchableItemStack(6, nmsItemStackPotion);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject potion(final ItemStack potion) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, potion);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FallingBlock extends Entity {

        public static WrappedWatchableObject position(final BlockPosition position) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, position);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AreaEffectCloud extends Entity {

        public static WrappedWatchableObject radius(final float radius) {
            return FACTORY.createWatchableItemStack(6, radius);
        }

        public static WrappedWatchableObject color(final int color) {
            return FACTORY.createWatchable(7, color);
        }

        public static WrappedWatchableObject singlePoint(final boolean singlePoint) {
            return FACTORY.createWatchable(8, singlePoint);
        }

        public static WrappedWatchableObject singlePoint(final Particle particle) {
            // unsure (?)
            return FACTORY.createWatchableObject(9, ProtocolLibConversions.toNms(particle));
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FishingHook extends Entity {

        public static WrappedWatchableObject hookedEntity(final int hookedEntityId) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, hookedEntityId);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject hookedEntity(final org.bukkit.entity.Entity entity) {
            if (VERSION >= 9) return hookedEntity(entity.getEntityId() + 1);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Arrow extends Entity {

        public static WrappedWatchableObject arrowFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 16, flagBytes);
        }

        public static WrappedWatchableObject shooter(final @Nullable UUID shooterUuid) {
            if (VERSION >= 9) return FACTORY.createWatchableOptional(7, shooterUuid);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flags {
            CRITICAL((byte) 0x01),
            NO_CLIP((byte) 0x02);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TippedArrow extends Arrow {

        public static WrappedWatchableObject color(final int color) {
            return FACTORY.createWatchable(8, color);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Trident extends Arrow {

        public static WrappedWatchableObject loyaltyLevel(final int loyaltyLevel) {
            return FACTORY.createWatchable(8, loyaltyLevel);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Boat extends Entity {

        public static WrappedWatchableObject timeSinceLastHit(final int timeSinceLastHit) {
            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 17, timeSinceLastHit);
        }

        public static WrappedWatchableObject forwardDirection(final int forwardDirection) {
            return FACTORY.createWatchable(VERSION >= 9 ? 7 : 18, forwardDirection);
        }

        public static WrappedWatchableObject damageTaken(final float damageTaken) {
            return FACTORY.createWatchable(VERSION >= 9 ? 8 : 19, damageTaken);
        }

        public static WrappedWatchableObject type(final Type type) {
            if (VERSION >= 9) return FACTORY.createWatchable(9, type.value());
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject rightPaddleTurning(final boolean rightPaddleTurning) {
            if (VERSION >= 9) return FACTORY.createWatchable(10, rightPaddleTurning);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject leftPaddleTurning(final boolean leftPaddleTurning) {
            if (VERSION >= 9) return FACTORY.createWatchable(11, leftPaddleTurning);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject splashTimer(final int splashTimer) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, splashTimer);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Type {
            OAK((byte) 0),
            SPRUCE((byte) 1),
            BIRCH((byte) 2),
            JUNGLE((byte) 3),
            ACACIA((byte) 4),
            DARK_OAK((byte) 5);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EnderCrystal extends Entity {

        public static WrappedWatchableObject position(final BlockPosition position) {
            return FACTORY.createWatchableOptional(6, position);
        }

        public static WrappedWatchableObject showBottom(final boolean showBottom) {
            return FACTORY.createWatchable(7, showBottom);
        }

        public static WrappedWatchableObject health(final int health) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "1.9 and later don't support this metadata"
            );
            return FACTORY.createWatchable(8, health);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fireball extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WitherSkull extends Entity {

        public static WrappedWatchableObject invulnerable(final boolean invulnerable) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, invulnerable);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fireworks extends Entity {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(6, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(6, item);
        }

        public static WrappedWatchableObject shooter(final int shooter) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, shooter);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject shooter(final org.bukkit.entity.Entity entity) {
            if (VERSION >= 9) return shooter(entity.getEntityId());
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Hanging extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ItemFrame extends Hanging {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(VERSION >= 9 ? 6 : 8, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 8, item);
        }

        public static WrappedWatchableObject rotation(final int rotation) {
            if (VERSION >= 9) return FACTORY.createWatchable(7, rotation);
            return FACTORY.createWatchable(9, (byte) rotation);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Item extends Entity {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(VERSION >= 9 ? 6 : 10, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 10, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LivingEntity extends Entity {

        public static WrappedWatchableObject handStates(final HandState... handStates) {
            if (VERSION >= 9) {
                var handStateBytes = (byte) 0;
                for (val handState : handStates) handStateBytes |= handState.value();

                return FACTORY.createWatchableObject(VERSION >= 14 ? 7 : 6, handStateBytes);
            }
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject health(final float health) {
            return FACTORY.createWatchable(VERSION >= 14 ? 8 : VERSION >= 9 ? 7 : 6, health);
        }

        public static WrappedWatchableObject potionEffectColor(final int potionEffectColor) {
            return FACTORY.createWatchable(VERSION >= 14 ? 9 : VERSION >= 9 ? 8 : 7, potionEffectColor);
        }

        public static WrappedWatchableObject potionEffectAmbient(final boolean potionEffectAmbient) {
            return FACTORY.createWatchable(VERSION >= 14 ? 10 : VERSION >= 9 ? 8 : 9, potionEffectAmbient);
        }

        public static WrappedWatchableObject numberOfArrows(final int numberOfArrows) {
            return FACTORY.createWatchable(VERSION >= 14 ? 11 : VERSION >= 9 ? 10 : 9, numberOfArrows);
        }

        public static WrappedWatchableObject healthAddedByAbsorption(final int healthAddedByAbsorption) {
            // TODO check version
            if (VERSION >= 14) return FACTORY.createWatchable(12, healthAddedByAbsorption);
            throw new UnsupportedOperationException("Versions prior to 1.14 don't support this metadata");
        }

        public static WrappedWatchableObject bedLocation(final @Nullable BlockPosition bedLocation) {
            // TODO check version & if nullability is correct
            if (VERSION >= 14) return FACTORY.createWatchableOptional(13, bedLocation);
            throw new UnsupportedOperationException("Versions prior to 1.14 don't support this metadata");
        }

        @Deprecated // since 1.9 this is part of Insentient
        public static WrappedWatchableObject noAi(final boolean noAi) {
            if (VERSION >= 9) return noAi
                    ? Mob.insentientFlags(Mob.Flag.NO_AI) : Mob.insentientFlags();
            return FACTORY.createWatchable(15, noAi);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum HandState {
            HAND_ACTIVE((byte) 0x01),
            OFFHAND((byte) 0x02),
            RIPTIDE_SPIN_ATTACK((byte) 0x04);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Player extends LivingEntity {

        public static WrappedWatchableObject additionalHearts(final float additionalHearts) {
            if (VERSION >= 9) return FACTORY.createWatchable(11, additionalHearts);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support this metadata"
            );
        }

        public static WrappedWatchableObject score(final int score) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, score);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support this metadata"
            );
        }

        public static WrappedWatchableObject skinParts(final SkinPart... skinParts) {
            if (VERSION >= 9) {
                var skinPartBytes = (byte) 0;
                for (val skinPart : skinParts) skinPartBytes |= skinPart.value();

                return FACTORY.createWatchableObject(13, skinPartBytes);
            }
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support this metadata"
            );
        }

        public static WrappedWatchableObject mainHand(final MainHand mainHand) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, mainHand.value());
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support this metadata"
            );
        }

        public static WrappedWatchableObject leftShoulderEntity(final NbtCompound leftShoulderEntity) {
            if (VERSION >= 13) return FACTORY.createWatchable(15, leftShoulderEntity);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support this metadata"
            );
        }

        public static WrappedWatchableObject rightShoulderEntity(final NbtCompound rightShoulderEntity) {
            if (VERSION >= 13) return FACTORY.createWatchable(15, rightShoulderEntity);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support this metadata"
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum SkinPart {
            CAPE((byte) 0x01),
            JACKET((byte) 0x02),
            LEFT_SLEEVE((byte) 0x04),
            RIGHT_SLEEVE((byte) 0x08),
            LEFT_PANT((byte) 0x10),
            RIGHT_PANT((byte) 0x20),
            HAT((byte) 0x40),
            UNUSED((byte) 0x80);

            @Getter byte value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum MainHand {
            LEFT((byte) 0),
            RIGHT((byte) 1);

            @Getter byte value;
        }
    }

    // TODO check versions
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArmorStand extends LivingEntity {

        public static WrappedWatchableObject armorStandFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 14 ? 14 : VERSION >= 9 ? 11 : 10, flagBytes);
        }

        public static WrappedWatchableObject headRotation(final Vector3F headRotation) {
            return FACTORY.createWatchable(VERSION >= 14 ? 15 : VERSION >= 9 ? 12 : 11, headRotation);
        }

        public static WrappedWatchableObject bodyRotation(final Vector3F bodyRotation) {
            return FACTORY.createWatchable(VERSION >= 14 ? 16 : VERSION >= 9 ? 13 : 12, bodyRotation);
        }

        public static WrappedWatchableObject leftArmRotation(final Vector3F leftArmRotation) {
            return FACTORY.createWatchable(VERSION >= 14 ? 17 : VERSION >= 9 ? 14 : 13, leftArmRotation);
        }

        public static WrappedWatchableObject rightArmRotation(final Vector3F rightArmRotation) {
            return FACTORY.createWatchable(VERSION >= 14 ? 18 : VERSION >= 9 ? 15 : 14, rightArmRotation);
        }

        public static WrappedWatchableObject leftLegRotation(final Vector3F leftLegRotation) {
            return FACTORY.createWatchable(VERSION >= 14 ? 19 : VERSION >= 9 ? 16 : 15, leftLegRotation);
        }

        public static WrappedWatchableObject rightLegRotation(final Vector3F rightLegRotation) {
            return FACTORY.createWatchable(VERSION >= 14 ? 20 : VERSION >= 9 ? 17 : 16, rightLegRotation);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            SMALL((byte) 0x01),
            HAS_ARMS((byte) 0x04),
            NO_BASE_PLATE((byte) 0x08),
            MARKER((byte) 0x10);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Mob extends LivingEntity {

        public static WrappedWatchableObject insentientFlags(final Flag... flags) {
            if (VERSION >= 9) {
                var flagBytes = (byte) 0;
                for (val flag : flags) flagBytes |= flag.value();

                return FACTORY.createWatchable(11, flagBytes);
            }

            // for versions prior to 1.9 NO_AI is byte at index <15>
            for (val flag : flags) if (flag == Flag.NO_AI) return FACTORY.createWatchable(15, (byte) 0x1);
            return FACTORY.createWatchable(15, (byte) 0x0);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            NO_AI((byte) 0x01),
            LEFT_HANDED((byte) 0x02);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AmbientCreature extends Mob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Bat extends AmbientCreature {

        public static WrappedWatchableObject batFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            HANGING((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PathfinderMob extends Mob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WaterAnimal extends PathfinderMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Squid extends WaterAnimal {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Dolphin extends WaterAnimal {

        public static WrappedWatchableObject treasurePosition(final BlockPosition treasurePosition) {
            return FACTORY.createWatchable(12, treasurePosition);
        }

        public static WrappedWatchableObject canFindTreasure(final boolean canFindTreasure) {
            return FACTORY.createWatchable(13, canFindTreasure);
        }

        public static WrappedWatchableObject hasFish(final boolean hasFish) {
            return FACTORY.createWatchable(14, hasFish);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fish extends WaterAnimal {

        public static WrappedWatchableObject fromBucket(final boolean fromBucket) {
            return FACTORY.createWatchable(12, fromBucket);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cod extends Fish {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PufferFish extends Fish {

        public static WrappedWatchableObject puffState(final int puffState) {
            return FACTORY.createWatchable(13, puffState);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Salmon extends Fish {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TropicalFish extends Fish {

        public static WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(13, variant);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ageable extends PathfinderMob {

        public static WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(12, baby);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Animal extends Ageable {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractHorse extends Animal {

        public static WrappedWatchableObject horseFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(13, flagBytes);
        }

        public static WrappedWatchableObject owner(final @Nullable UUID ownerUuid) {
            if (VERSION >= 9) return FACTORY.createWatchableOptional(14, ownerUuid);
            return FACTORY.createWatchable(21, Bukkit.getOfflinePlayer(ownerUuid).getName()); // FIXME
        }

        @Deprecated
        public static WrappedWatchableObject owner(final String ownerName) {
            if (VERSION >= 9) return FACTORY.createWatchableOptional(
                    14, Bukkit.getOfflinePlayer(ownerName).getUniqueId()
            );
            return FACTORY.createWatchable(21, ownerName);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            TAME((byte) 0x02),
            CHEST((byte) 0x0, (byte) 0x8), // legacy
            SADDLED((byte) 0x04),
            BRED((byte) 0x08, (byte) 0x10),
            EATING((byte) 0x10, (byte) 0x20),
            REARING((byte) 0x20, (byte) 0x40),
            MOUTH_OPEN((byte) 0x40, (byte) 0x80);

            @Getter byte value;
            @Getter byte legacyValue;

            Flag(final byte value) {
                this(value, value);
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Horse extends AbstractHorse {

        @Deprecated
        public static WrappedWatchableObject horseType(final Type horseType) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "Versions 1.9 and later don't support this metadata"
            );
            return FACTORY.createWatchable(19, horseType.value());
        }

        public static WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(VERSION > 9 ? 15 : 20, variant);
        }

        public static WrappedWatchableObject armor(final Armor armor) {
            return FACTORY.createWatchable(VERSION > 9 ? 16 : 22, armor.value());
        }

        public static WrappedWatchableObject forgeArmor(final Object nmsItem) {
            // not sure whether to disable for old versions as it is related to Forge
            return FACTORY.createWatchableItemStack(17, nmsItem);
        }

        public static WrappedWatchableObject forgeArmor(final ItemStack item) {
            // not sure whether to disable for old versions as it is related to Forge
            return FACTORY.createWatchable(17, item);
        }

        @Deprecated
        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Type {
            HORSE((byte) 0),
            DONKEY((byte) 1),
            MULE((byte) 2),
            ZOMBIE((byte) 3),
            SKELETON((byte) 4);

            @Getter byte value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Armor {
            NONE(0),
            IRON(1),
            GOLD(2),
            DIAMOND(3);

            @Getter int value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            TAME((byte) 0x02),
            CHEST((byte) 0x0, (byte) 0x8), // legacy
            SADDLED((byte) 0x04),
            BRED((byte) 0x08, (byte) 0x10),
            EATING((byte) 0x10, (byte) 0x20),
            REARING((byte) 0x20, (byte) 0x40),
            MOUTH_OPEN((byte) 0x40, (byte) 0x80);

            @Getter byte value;
            @Getter byte legacyValue;

            Flag(final byte value) {
                this(value, value);
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ZombieHorse extends AbstractHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SkeletonHorse extends AbstractHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChestedHorse extends AbstractHorse {

        public static WrappedWatchableObject chest(final boolean chest) {
            if (VERSION >= 9) return FACTORY.createWatchable(15, chest);
            return FACTORY.createWatchable(16, (byte) 0x08);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Donkey extends ChestedHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Llama extends ChestedHorse {

        public static WrappedWatchableObject strength(final int strength) {
            return FACTORY.createWatchable(16, strength);
        }

        public static WrappedWatchableObject carpetColor(final int carpetColor) {
            return FACTORY.createWatchable(17, carpetColor);
        }

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(18, variant.value());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant {
            CREAMY((byte) 0),
            WHITE((byte) 1),
            BROWN((byte) 2),
            GRAY((byte) 3);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Mule extends ChestedHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pig extends Animal {

        public static WrappedWatchableObject saddle(final boolean saddle) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 16, saddle);
        }

        public static WrappedWatchableObject boostTime(final int boostTime) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, boostTime);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Rabbit extends Animal {

        public static WrappedWatchableObject type(final int type) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 18, type);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Turtle extends Animal {

        public static WrappedWatchableObject home(final BlockPosition home) {
            return FACTORY.createWatchable(13, home);
        }

        public static WrappedWatchableObject hasEgg(final boolean hasEgg) {
            return FACTORY.createWatchable(14, hasEgg);
        }

        public static WrappedWatchableObject layingEgg(final boolean layingEgg) {
            return FACTORY.createWatchable(15, layingEgg);
        }

        public static WrappedWatchableObject travelPosition(final BlockPosition travelPosition) {
            return FACTORY.createWatchable(16, travelPosition);
        }

        public static WrappedWatchableObject goingHome(final boolean goingHome) {
            return FACTORY.createWatchable(17, goingHome);
        }

        public static WrappedWatchableObject travelling(final boolean travelling) {
            return FACTORY.createWatchable(18, travelling);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PolarBear extends Animal {

        public static WrappedWatchableObject standingUp(final boolean standingUp) {
            return FACTORY.createWatchable(13, standingUp);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Sheep extends Animal {

        public static WrappedWatchableObject sheepData(final byte color, final boolean sheared) {
            return FACTORY.createWatchable(13, color & 0x0F | (sheared ? 0x0 : 0x10));
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Tameable extends Animal {

        public static WrappedWatchableObject tameableFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 16, flagBytes);
        }

        public static WrappedWatchableObject owner(final @Nullable UUID ownerUuid) {
            return FACTORY.createWatchableOptional(VERSION >= 9 ? 14 : 17, ownerUuid);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            SITTING((byte) 0x01),
            ANGRY((byte) 0x02),
            TAMED((byte) 0x04);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ocelot extends Tameable {

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(VERSION >= 9 ? 15 : 18, variant.value());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant {
            UNTAMED(0),
            TUXEDO(1),
            TABBY(2),
            SIAMESE(3);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Wolf extends Tameable {

        public static WrappedWatchableObject damageTaken(final float damageTaken) {
            return FACTORY.createWatchable(VERSION >= 9 ? 15 : 18, damageTaken);
        }

        public static WrappedWatchableObject begging(final boolean begging) {
            return FACTORY.createWatchable(VERSION >= 9 ? 16 : 19, begging);
        }

        public static WrappedWatchableObject collarColor(final byte collarColor) {
            return FACTORY.createWatchable(VERSION >= 9 ? 17 : 20, collarColor);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Parrot extends Tameable {

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(15, variant.value());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant {
            RED_BLUE(0),
            BLUE(1),
            GREEN(2),
            YELLOW_BLUE(3),
            SILVER(4);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Villager extends Ageable {

        public static WrappedWatchableObject profession(final Profession profession) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "1.9 and later don't support this metadata"
            );
            return FACTORY.createWatchable(13, profession.value());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Profession {
            FARMER(0),
            LIBRARIAN(1),
            PRIEST(2),
            BLACKSMITH(3),
            SILVER(4);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Golem extends PathfinderMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IronGolem extends Golem {

        public static WrappedWatchableObject ironGolemFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            PLAYER_CREATED((byte) 0x01);

            @Getter
            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Snowman extends Golem {

        public static WrappedWatchableObject snowmanFlags(final Flag... flags) {
            if (VERSION >= 9) {
                var flagBytes = (byte) 0;
                for (val flag : flags) flagBytes |= flag.value();

                return FACTORY.createWatchable(12, flagBytes);
            }
            throw new UnsupportedOperationException("Versions lower than 1.9 don't support this metadata");
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            HAS_PUMPKIN((byte) 0x10);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Shulker extends Golem {

        public static WrappedWatchableObject facing(final @NonNull Object enumDirection) {
            return FACTORY.createWatchableEnumDirection(12, enumDirection);
        }

        public static WrappedWatchableObject facing(final @NonNull EnumWrappers.Direction direction) {
            return FACTORY.createWatchable(12, direction);
        }

        public static WrappedWatchableObject attachmentPosition(final @Nullable BlockPosition attachmentPosition) {
            return FACTORY.createWatchableOptional(13, attachmentPosition);
        }

        public static WrappedWatchableObject shieldHeight(final byte shieldHeight) {
            return FACTORY.createWatchable(14, shieldHeight);
        }

        public static WrappedWatchableObject color(final byte color) {
            return FACTORY.createWatchable(15, color);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Monster extends PathfinderMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Blaze extends Monster {

        public static WrappedWatchableObject blazeFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            ON_FIRE((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Creeper extends Monster {

        public static WrappedWatchableObject creeperState(final State state) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, state.value());
        }

        public static WrappedWatchableObject charged(final boolean charged) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 17, charged);
        }

        public static WrappedWatchableObject ignited(final boolean ignited) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, ignited);
            throw new UnsupportedOperationException("Versions lower than 1.9 don't support this metadata");
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum State {
            IDLE(-1),
            FUSE(1);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Endermite extends Monster {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GiantZombie extends Monster {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Guardian extends Monster {

        public static WrappedWatchableObject retractingSpikes(final boolean retractingSpikes) {
            if (VERSION >= 13) return FACTORY.createWatchable(12, retractingSpikes);
            return FACTORY.createWatchable(16, (byte) 0x04);
        }

        @Deprecated
        public static WrappedWatchableObject guardianFlags(final boolean elder, final boolean retractingSpikes) {
            if (VERSION >= 13) return FACTORY.createWatchable(12, retractingSpikes);
            return FACTORY.createWatchable(
                    16, ((elder ? (byte) 0x02 : (byte) 0x0) | (retractingSpikes ? (byte) 0x04 : (byte) 0x0))
            );
        }

        public static WrappedWatchableObject targetEntity(final int targetEntityId) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 17, targetEntityId);
        }

        public static WrappedWatchableObject targetEntity(final org.bukkit.entity.Entity entity) {
            return targetEntity(entity.getEntityId());
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ElderGuardian extends Guardian {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Silverfish extends Monster {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Illager extends Monster {

        public static WrappedWatchableObject illagerState(final State state) {
            return FACTORY.createWatchable(12, state.value());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum State {
            HAS_TARGET((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VindicatorIllager extends Illager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SpellcasterIllager extends Illager {

        public static WrappedWatchableObject spell(final Spell spell) {
            return FACTORY.createWatchable(13, spell.value());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Spell {
            NONE((byte) 0),
            SUMMON_VEX((byte) 1),
            ATTACK((byte) 2),
            WOLOLO((byte) 3);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EvocationIllager extends SpellcasterIllager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IllusionIllager extends SpellcasterIllager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Vex extends Monster {

        public static WrappedWatchableObject vexFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            ATTACK_MODE((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EvocationFangs extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractSkeleton extends Monster {

        public static WrappedWatchableObject swingingArms(final boolean swingingArms) {
            return FACTORY.createWatchable(12, swingingArms);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Skeleton extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WitherSkeleton extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Stray extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Spider extends Monster {

        public static WrappedWatchableObject spiderFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value();

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            CLIMBING((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Witch extends Monster {

        public static WrappedWatchableObject drinkingPotion(final boolean drinkingPotion) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 21, drinkingPotion);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Wither extends Monster {

        public static WrappedWatchableObject centerHeadTarget(final int centerHeadTargetId) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 17, centerHeadTargetId);
        }

        public static WrappedWatchableObject centerHeadTarget(final org.bukkit.entity.Entity centerHeadTarget) {
            return centerHeadTarget(centerHeadTarget == null ? 0 : centerHeadTarget.getEntityId());
        }

        public static WrappedWatchableObject leftHeadTarget(final int leftHeadTargetId) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 18, leftHeadTargetId);
        }

        public static WrappedWatchableObject leftHeadTarget(final org.bukkit.entity.Entity leftHeadTarget) {
            return leftHeadTarget(leftHeadTarget == null ? 0 : leftHeadTarget.getEntityId());
        }

        public static WrappedWatchableObject rightHeadTarget(final int rightHeadTargetId) {
            return FACTORY.createWatchable(VERSION >= 9 ? 14 : 19, rightHeadTargetId);
        }

        public static WrappedWatchableObject rightHeadTarget(final org.bukkit.entity.Entity rightHeadTarget) {
            return rightHeadTarget(rightHeadTarget == null ? 0 : rightHeadTarget.getEntityId());
        }

        public static WrappedWatchableObject invulnerableTime(final int invulnerableTime) {
            return FACTORY.createWatchable(VERSION >= 9 ? 15 : 20, invulnerableTime);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Zombie extends Monster {

        public static WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(12, baby);
        }

        @Deprecated
        public static WrappedWatchableObject zombieVillager(final boolean zombieVillager) {
            return FACTORY.createWatchable(13, zombieVillager);
        }

        public static WrappedWatchableObject handsUp(final boolean handsUp) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, handsUp);
            else throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support this metadata"
            );
        }

        public static WrappedWatchableObject becomingDrowned(final boolean becomingDrowned) {
            if (VERSION >= 13) return FACTORY.createWatchable(15, becomingDrowned);
            else throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support this metadata"
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ZombieVillager extends Zombie {

        public static WrappedWatchableObject converting(final boolean converting) {
            return FACTORY.createWatchable(VERSION >= 9 ? 16 : 14, converting);
        }

        public static WrappedWatchableObject profession(final Villager.Profession profession) {
            return FACTORY.createWatchable(17, profession.value());
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Husk extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Drowned extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Enderman extends Monster {

        public static WrappedWatchableObject carriedBlock(final Object carriedBlock) {
            if (VERSION >= 9) return FACTORY.createWatchableOptionalIBlockData(12, Optional.of(carriedBlock));
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject carriedBlockId(final short carriedBlockId) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "Versions 1.9 and later don't support this metadata"
            );
            return FACTORY.createWatchable(16, carriedBlockId);
        }

        public static WrappedWatchableObject carriedBlockData(final byte carriedBlockData) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "Versions 1.9 and later don't support this metadata"
            );
            return FACTORY.createWatchable(17, carriedBlockData);
        }

        public static WrappedWatchableObject screaming(final boolean screaming) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 18, screaming);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EnderDragon extends Monster {

        public static WrappedWatchableObject phase(final Phase phase) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, phase.value());
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Phase {
            CIRCLING(0),
            STRAFING(1),
            FLYING(2),
            LANDING(3),
            TAKING_OFF(4),
            BREATHING(5),
            LOOKING_FOR_PLAYER(6),
            ROARING(7),
            CHARGING_PLAYER(8),
            FLYDYING(9),
            NO_AI(10);

            @Getter
            int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Flying extends Mob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ghast extends Flying {

        public static WrappedWatchableObject attacking(final boolean attacking) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, attacking);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Phantom extends Flying {

        public static WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(12, size);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Slime extends Mob {

        public static WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, size);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LlamaSpit extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Minecart extends Entity {

        public static WrappedWatchableObject shakingPower(final int shakingPower) {
            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 17, shakingPower);
        }

        public static WrappedWatchableObject shakingDirection(final int shakingDirection) {
            return FACTORY.createWatchable(VERSION >= 9 ? 7 : 18, shakingDirection);
        }

        public static WrappedWatchableObject shakingMultiplier(final int shakingMultiplier) {
            return FACTORY.createWatchable(VERSION >= 9 ? 8 : 19, shakingMultiplier);
        }

        public static WrappedWatchableObject customBlockIdAndDamage(final int customBlockIdAndDamage) {
            return FACTORY.createWatchable(VERSION >= 9 ? 9 : 20, customBlockIdAndDamage);
        }

        public static WrappedWatchableObject customBlockY(final int customBlockY) {
            return FACTORY.createWatchable(VERSION >= 9 ? 10 : 21, customBlockY);
        }

        public static WrappedWatchableObject showCustomBlock(final boolean showCustomBlock) {
            return FACTORY.createWatchable(VERSION >= 9 ? 11 : 22, showCustomBlock);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartRideable extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartContainer extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartHopper extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartChest extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartFurnace extends Minecart {

        public static WrappedWatchableObject powered(final boolean powered) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, powered);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartTnt extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartSpawner extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartCommandBlock extends Minecart {

        public static WrappedWatchableObject command(final String command) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, command);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }

        public static WrappedWatchableObject lastOutput(final WrappedChatComponent lastOutput) {
            if (VERSION >= 9) return FACTORY.createWatchable(13, lastOutput);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TNTPrimed extends Entity {

        public static WrappedWatchableObject fuseTime(final int fuseTime) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, fuseTime);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support this metadata");
        }
    }
}

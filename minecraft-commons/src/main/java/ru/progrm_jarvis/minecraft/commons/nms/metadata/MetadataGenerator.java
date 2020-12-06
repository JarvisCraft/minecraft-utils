package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.nms.Conversions;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * Editor for Metadata of {@link WrappedWatchableObject} providing classes
 * containing static methods for developer-friendly object creation.
 *
 * @see <a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=7410">1.8 metadata format</a>
 */
@UtilityClass
public class MetadataGenerator {

    private final int VERSION = NmsUtil.getVersion().getGeneration();
    private final DataWatcherFactory FACTORY = NmsUtil.getDataWatcherFactory();

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Entity {

        public static WrappedWatchableObject entityFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

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

        @RequiredArgsConstructor
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

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Projectile extends Entity {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Snowball extends Projectile {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Egg extends Projectile {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Potion extends Projectile {

        public static WrappedWatchableObject potion(final Object nmsItemStackPotion) {
            if (VERSION >= 9) return FACTORY.createWatchableItemStack(6, nmsItemStackPotion);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Potion metadata");
        }

        public static WrappedWatchableObject potion(final ItemStack potion) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, potion);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Potion metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class FallingBlock extends Entity {

        public static WrappedWatchableObject position(final BlockPosition position) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, position);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Potion metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
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
            return FACTORY.createWatchableObject(9, Conversions.toNms(particle));
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class FishingHook extends Entity {

        public static WrappedWatchableObject hookedEntity(final int hookedEntityId) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, hookedEntityId);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Fishing Hook metadata");
        }

        public static WrappedWatchableObject hookedEntity(final org.bukkit.entity.Entity entity) {
            if (VERSION >= 9) return hookedEntity(entity.getEntityId() + 1);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Fishing Hook metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Arrow extends Entity {

        public static WrappedWatchableObject arrowFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 16, flagBytes);
        }

        public static WrappedWatchableObject shooter(final @Nullable UUID shooterUuid) {
            if (VERSION >= 9) return FACTORY.createWatchableOptionalUUID(7, Optional.ofNullable(shooterUuid));
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Arrow metadata");
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flags {
            CRITICAL((byte) 0x01),
            NO_CLIP((byte) 0x02);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class TippedArrow extends Arrow {

        public static WrappedWatchableObject color(final int color) {
            return FACTORY.createWatchable(8, color);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Trident extends Arrow {

        public static WrappedWatchableObject loyaltyLevel(final int loyaltyLevel) {
            return FACTORY.createWatchable(8, loyaltyLevel);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
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
            if (VERSION >= 9) return FACTORY.createWatchable(9, type.value);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Boat type metadata");
        }

        public static WrappedWatchableObject rightPaddleTurning(final boolean rightPaddleTurning) {
            if (VERSION >= 9) return FACTORY.createWatchable(10, rightPaddleTurning);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Boat right paddle turning metadata");
        }

        public static WrappedWatchableObject leftPaddleTurning(final boolean leftPaddleTurning) {
            if (VERSION >= 9) return FACTORY.createWatchable(11, leftPaddleTurning);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Boat left paddle turning metadata");
        }

        public static WrappedWatchableObject splashTimer(final int splashTimer) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, splashTimer);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Boat splash timer metadata");
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Type {
            OAK((byte) 0),
            SPRUCE((byte) 1),
            BIRCH((byte) 2),
            JUNGLE((byte) 3),
            ACACIA((byte) 4),
            DARK_OAK((byte) 5);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class EnderCrystal extends Entity {

        public static WrappedWatchableObject position(final BlockPosition position) {
            return FACTORY.createWatchableOptionalBlockPosition(6, Optional.of(position));
        }

        public static WrappedWatchableObject showBottom(final boolean showBottom) {
            return FACTORY.createWatchable(7, showBottom);
        }

        public static WrappedWatchableObject health(final int health) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "1.9 and later don't support Ender Crystal health metadata"
            );
            return FACTORY.createWatchable(8, health);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Fireball extends Entity {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class WitherSkull extends Entity {

        public static WrappedWatchableObject invulnerable(final boolean invulnerable) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, invulnerable);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Wither Skull invulnerable metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Fireworks extends Entity {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(6, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(6, item);
        }

        public static WrappedWatchableObject shooter(final int shooter) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, shooter);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Wither Fireworks shooter metadata");
        }

        public static WrappedWatchableObject shooter(final org.bukkit.entity.Entity entity) {
            if (VERSION >= 9) return shooter(entity.getEntityId());
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Fireworks shooter metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Hanging extends Entity {}

    @NoArgsConstructor(access = AccessLevel.NONE)
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

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Item extends Entity {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(VERSION >= 9 ? 6 : 10, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(VERSION >= 9 ? 6 : 10, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Living extends Entity {

        public static WrappedWatchableObject handStates(final HandState... handStates) {
            if (VERSION >= 9) {
                var handStateBytes = (byte) 0;
                for (val handState : handStates) handStateBytes |= handState.value;

                return FACTORY.createWatchableObject(6, handStateBytes);
            }
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Living handStates metadata");
        }

        public static WrappedWatchableObject health(final float health) {
            return FACTORY.createWatchable(VERSION >= 9 ? 7 : 6, health);
        }

        public static WrappedWatchableObject potionEffectColor(final int potionEffectColor) {
            return FACTORY.createWatchable(VERSION >= 9 ? 8 : 7, potionEffectColor);
        }

        public static WrappedWatchableObject potionEffectAmbient(final boolean potionEffectAmbient) {
            return FACTORY.createWatchable(VERSION >= 9 ? 8 : 9, potionEffectAmbient);
        }

        public static WrappedWatchableObject numberOfArrows(final int numberOfArrows) {
            return FACTORY.createWatchable(VERSION >= 9 ? 10 : 9, numberOfArrows);
        }

        @Deprecated
        public static WrappedWatchableObject noAi(final boolean noAi) {
            if (VERSION >= 9) return noAi
                    ? Insentient.insentientFlags(Insentient.Flag.NO_AI) : Insentient.insentientFlags();
            return FACTORY.createWatchable(15, noAi);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum HandState {
            HAND_ACTIVE((byte) 0x01),
            OFFHAND((byte) 0x02),
            RIPTIDE_SPIN_ATTACK((byte) 0x04);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Player extends Living {

        public static WrappedWatchableObject additionalHearts(final float additionalHearts) {
            if (VERSION >= 9) return FACTORY.createWatchable(11, additionalHearts);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support Player additional hearts metadata"
            );
        }

        public static WrappedWatchableObject score(final int score) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, score);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support Player score metadata"
            );
        }

        public static WrappedWatchableObject skinParts(final SkinPart... skinParts) {
            if (VERSION >= 9) {
                var skinPartBytes = (byte) 0;
                for (val skinPart : skinParts) skinPartBytes |= skinPart.value;

                return FACTORY.createWatchableObject(13, skinPartBytes);
            }
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support Player skin parts metadata"
            );
        }

        public static WrappedWatchableObject mainHand(final MainHand mainHand) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, mainHand.value);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.9 don't support Player main hand metadata"
            );
        }

        public static WrappedWatchableObject leftShoulderEntity(final Object leftShoulderEntityNbtTagCompound) {
            if (VERSION >=13) return FACTORY.createWatchableObject(15, leftShoulderEntityNbtTagCompound);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support Player left shoulder entity metadata"
            );
        }

        public static WrappedWatchableObject leftShoulderEntity(final NbtCompound leftShoulderEntityNbt) {
            if (VERSION >= 13) return leftShoulderEntity(leftShoulderEntityNbt.getHandle());
            throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support Player left shoulder entity metadata"
            );
        }

        public static WrappedWatchableObject rightShoulderEntity(final Object rightShoulderEntityNbtTagCompound) {
            if (VERSION >= 13) return FACTORY.createWatchableNBTTagCompound(16, rightShoulderEntityNbtTagCompound);
            throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support Player right shoulder entity metadata"
            );
        }

        public static WrappedWatchableObject rightShoulderEntity(final NbtCompound rightShoulderEntityNbt) {
            if (VERSION >= 13) return rightShoulderEntity(rightShoulderEntityNbt.getHandle());
            throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support Player right shoulder entity metadata"
            );
        }

        @RequiredArgsConstructor
        public enum SkinPart {
            CAPE((byte) 0x01),
            JACKET((byte) 0x02),
            LEFT_SLEEVE((byte) 0x04),
            RIGHT_SLEEVE((byte) 0x08),
            LEFT_PANT((byte) 0x10),
            RIGHT_PANT((byte) 0x20),
            HAT((byte) 0x40),
            UNUSED((byte) 0x80);

            private final byte value;
        }

        @RequiredArgsConstructor
        public enum MainHand {
            LEFT((byte) 0),
            RIGHT((byte) 1);

            private final byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class ArmorStand extends Living {

        public static WrappedWatchableObject armorStandFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 11 : 10, flagBytes);
        }

        public static WrappedWatchableObject headRotation(final Vector3F headRotation) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 11, headRotation);
        }

        public static WrappedWatchableObject bodyRotation(final Vector3F bodyRotation) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 12, bodyRotation);
        }

        public static WrappedWatchableObject leftArmRotation(final Vector3F leftArmRotation) {
            return FACTORY.createWatchable(VERSION >= 9 ? 14 : 13, leftArmRotation);
        }

        public static WrappedWatchableObject rightArmRotation(final Vector3F rightArmRotation) {
            return FACTORY.createWatchable(VERSION >= 9 ? 15 : 14, rightArmRotation);
        }

        public static WrappedWatchableObject leftLegRotation(final Vector3F leftLegRotation) {
            return FACTORY.createWatchable(VERSION >= 9 ? 16 : 15, leftLegRotation);
        }

        public static WrappedWatchableObject rightLegRotation(final Vector3F rightLegRotation) {
            return FACTORY.createWatchable(VERSION >= 9 ? 17 : 16, rightLegRotation);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            SMALL((byte) 0x01),
            HAS_ARMS((byte) 0x04),
            NO_BASE_PLATE((byte) 0x08),
            MARKER((byte) 0x10);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Insentient extends Living {

        public static WrappedWatchableObject insentientFlags(final Flag... flags) {
            if (VERSION >= 9) {
                var flagBytes = (byte) 0;
                for (val flag : flags) flagBytes |= flag.value;

                return FACTORY.createWatchable(11, flagBytes);
            }

            // for versions prior to 1.9 NO_AI is byte at index <15>
            for (val flag : flags) if (flag == Flag.NO_AI) return FACTORY.createWatchable(15, (byte) 0x1);
            return FACTORY.createWatchable(15, (byte) 0x0);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            NO_AI((byte) 0x01),
            LEFT_HANDED((byte) 0x02);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Ambient extends Insentient {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Bat extends Ambient {

        public static WrappedWatchableObject batFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            HANGING((byte) 0x01);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Creature extends Insentient {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class WaterMob extends Creature {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Squid extends WaterMob {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Dolphin extends WaterMob {

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

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Fish extends WaterMob {

        public static WrappedWatchableObject fromBucket(final boolean fromBucket) {
            return FACTORY.createWatchable(12, fromBucket);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Cod extends Fish {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class PufferFish extends Fish {

        public static WrappedWatchableObject puffState(final int puffState) {
            return FACTORY.createWatchable(13, puffState);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Salmon extends Fish {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class TropicalFish extends Fish {

        public static WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(13, variant);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Ageable extends Creature {

        public static WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(12, baby);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Animal extends Ageable {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class AbstractHorse extends Animal {

        public static WrappedWatchableObject horseFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(13, flagBytes);
        }

        public static WrappedWatchableObject owner(final UUID ownerUuid) {
            if (VERSION >= 9) return FACTORY.createWatchableOptionalUUID(14, Optional.ofNullable(ownerUuid));
            return FACTORY.createWatchable(21, Bukkit.getOfflinePlayer(ownerUuid).getName());
        }

        @Deprecated
        public static WrappedWatchableObject owner(final String ownerName) {
            if (VERSION >= 9) return FACTORY.createWatchableOptionalUUID(
                    14, Optional.of(Bukkit.getOfflinePlayer(ownerName).getUniqueId())
            );
            return FACTORY.createWatchable(21, ownerName);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            TAME((byte) 0x02),
            CHEST((byte) 0x0, (byte) 0x8), // legacy
            SADDLED((byte) 0x04),
            BRED((byte) 0x08, (byte) 0x10),
            EATING((byte) 0x10, (byte) 0x20),
            REARING((byte) 0x20, (byte) 0x40),
            MOUTH_OPEN((byte) 0x40, (byte) 0x80);

            byte value, legacyValue;

            Flag(final byte value) {
                this(value, value);
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Horse extends AbstractHorse {

        @Deprecated
        public static WrappedWatchableObject horseType(final Type horseType) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "Versions 1.9 and later don't support Horse type metadata"
            );
            return FACTORY.createWatchable(19, horseType.value);
        }

        public static WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(VERSION > 9 ? 15 : 20, variant);
        }

        public static WrappedWatchableObject armor(final Armor armor) {
            return FACTORY.createWatchable(VERSION > 9 ? 16 : 22, armor.value);
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
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Type {
            HORSE((byte) 0),
            DONKEY((byte) 1),
            MULE((byte) 2),
            ZOMBIE((byte) 3),
            SKELETON((byte) 4);

            byte value;
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Armor {
            NONE(0),
            IRON(1),
            GOLD(2),
            DIAMOND(3);

            int value;
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            TAME((byte) 0x02),
            CHEST((byte) 0x0, (byte) 0x8), // legacy
            SADDLED((byte) 0x04),
            BRED((byte) 0x08, (byte) 0x10),
            EATING((byte) 0x10, (byte) 0x20),
            REARING((byte) 0x20, (byte) 0x40),
            MOUTH_OPEN((byte) 0x40, (byte) 0x80);

            byte value, legacyValue;

            Flag(final byte value) {
                this(value, value);
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class ZombieHorse extends AbstractHorse {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class SkeletonHorse extends AbstractHorse {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class ChestedHorse extends AbstractHorse {

        public static WrappedWatchableObject chest(final boolean chest) {
            if (VERSION >= 9) return FACTORY.createWatchable(15, chest);
            return FACTORY.createWatchable(16, (byte) 0x08);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Donkey extends ChestedHorse {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Llama extends ChestedHorse {

        public static WrappedWatchableObject strength(final int strength) {
            return FACTORY.createWatchable(16, strength);
        }

        public static WrappedWatchableObject carpetColor(final int carpetColor) {
            return FACTORY.createWatchable(17, carpetColor);
        }

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(18, variant.value);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant {
            CREAMY((byte) 0),
            WHITE((byte) 1),
            BROWN((byte) 2),
            GRAY((byte) 3);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Mule extends ChestedHorse {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Pig extends Animal {

        public static WrappedWatchableObject saddle(final boolean saddle) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 16, saddle);
        }

        public static WrappedWatchableObject boostTime(final int boostTime) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, boostTime);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Pig boost time metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Rabbit extends Animal {

        public static WrappedWatchableObject type(final int type) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 18, type);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
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

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class PolarBear extends Animal {

        public static WrappedWatchableObject standingUp(final boolean standingUp) {
            return FACTORY.createWatchable(13, standingUp);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Sheep extends Animal {

        public static WrappedWatchableObject sheepData(final byte color, final boolean sheared) {
            return FACTORY.createWatchable(13, color & 0x0F | (sheared ? 0x0 : 0x10));
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Tameable extends Animal {

        public static WrappedWatchableObject tameableFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 16, flagBytes);
        }

        public static WrappedWatchableObject owner(final UUID ownerUuid) {
            return FACTORY.createWatchableOptionalUUID(VERSION >= 9 ? 14 : 17, Optional.ofNullable(ownerUuid));
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            SITTING((byte) 0x01),
            ANGRY((byte) 0x02),
            TAMED((byte) 0x04);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Ocelot extends Tameable {

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(VERSION >= 9 ? 15 : 18, variant.value);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant {
            UNTAMED(0),
            TUXEDO(1),
            TABBY(2),
            SIAMESE(3);

            int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
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

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Parrot extends Tameable {

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(15, variant.value);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant {
            RED_BLUE(0),
            BLUE(1),
            GREEN(2),
            YELLOW_BLUE(3),
            SILVER(4);

            int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Villager extends Ageable {

        public static WrappedWatchableObject profession(final Profession profession) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "1.9 and later don't support Villager profession metadata"
            );
            return FACTORY.createWatchable(13, profession.value);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Profession {
            FARMER(0),
            LIBRARIAN(1),
            PRIEST(2),
            BLACKSMITH(3),
            SILVER(4);

            int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Golem extends Creature {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class IronGolem extends Golem {

        public static WrappedWatchableObject ironGolemFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            PLAYER_CREATED((byte) 0x01);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Snowman extends Golem {

        public static WrappedWatchableObject snowmanFlags(final Flag... flags) {
            if (VERSION >= 9) {
                var flagBytes = (byte) 0;
                for (val flag : flags) flagBytes |= flag.value;

                return FACTORY.createWatchable(12, flagBytes);
            }
            throw new UnsupportedOperationException("Versions lower than 1.9 don't support Snowman flags metadata");
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            HAS_PUMPKIN((byte) 0x10);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Shulker extends Golem {

        public static WrappedWatchableObject facing(final Object enumDirection) {
            return FACTORY.createWatchableEnumDirection(12, enumDirection);
        }

        public static WrappedWatchableObject facing(final EnumWrappers.Direction direction) {
            return FACTORY.createWatchable(12, direction);
        }

        public static WrappedWatchableObject attachmentPosition(final BlockPosition attachmentPosition) {
            return FACTORY.createWatchableOptionalBlockPosition(13, Optional.ofNullable(attachmentPosition));
        }

        public static WrappedWatchableObject shieldHeight(final byte shieldHeight) {
            return FACTORY.createWatchable(14, shieldHeight);
        }

        public static WrappedWatchableObject color(final byte color) {
            return FACTORY.createWatchable(15, color);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Monster extends Creature {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Blaze extends Monster {

        public static WrappedWatchableObject blazeFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            ON_FIRE((byte) 0x01);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Creeper extends Monster {

        public static WrappedWatchableObject creeperState(final State state) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, state.value);
        }

        public static WrappedWatchableObject charged(final boolean charged) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 17, charged);
        }

        public static WrappedWatchableObject ignited(final boolean ignited) {
            if (VERSION >= 9) return FACTORY.createWatchable(14, ignited);
            throw new UnsupportedOperationException("Versions lower than 1.9 don't support Creeper ignited metadata");
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum State {
            IDLE(-1),
            FUSE(1);

            int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Endermite extends Monster {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class GiantZombie extends Monster {}

    @NoArgsConstructor(access = AccessLevel.NONE)
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

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class ElderGuardian extends Guardian {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Silverfish extends Monster {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Illager extends Monster {

        public static WrappedWatchableObject illagerState(final State state) {
            return FACTORY.createWatchable(12, state.value);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum State {
            HAS_TARGET((byte) 0x01);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class VindicatorIllager extends Illager {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class SpellcasterIllager extends Illager {

        public static WrappedWatchableObject spell(final Spell spell) {
            return FACTORY.createWatchable(13, spell.value);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Spell {
            NONE((byte) 0),
            SUMMON_VEX((byte) 1),
            ATTACK((byte) 2),
            WOLOLO((byte) 3);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class EvocationIllager extends SpellcasterIllager {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class IllusionIllager extends SpellcasterIllager {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Vex extends Monster {

        public static WrappedWatchableObject vexFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            ATTACK_MODE((byte) 0x01);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class EvocationFangs extends Entity {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class AbstractSkeleton extends Monster {

        public static WrappedWatchableObject swingingArms(final boolean swingingArms) {
            return FACTORY.createWatchable(12, swingingArms);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Skeleton extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class WitherSkeleton extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Stray extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Spider extends Monster {

        public static WrappedWatchableObject spiderFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, flagBytes);
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag {
            CLIMBING((byte) 0x01);

            byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Witch extends Monster {

        public static WrappedWatchableObject drinkingPotion(final boolean drinkingPotion) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 21, drinkingPotion);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
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

    @NoArgsConstructor(access = AccessLevel.NONE)
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
                    "Versions prior to 1.9 don't support Zombie hands up flag metadata"
            );
        }

        public static WrappedWatchableObject becomingDrowned(final boolean becomingDrowned) {
            if (VERSION >= 13) return FACTORY.createWatchable(15, becomingDrowned);
            else throw new UnsupportedOperationException(
                    "Versions prior to 1.13 don't support Zombie becoming drowned flag metadata"
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class ZombieVillager extends Zombie {

        public static WrappedWatchableObject converting(final boolean converting) {
            return FACTORY.createWatchable(VERSION >= 9 ? 16 : 14, converting);
        }

        public static WrappedWatchableObject profession(final Villager.Profession profession) {
            return FACTORY.createWatchable(17, profession.value);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Husk extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Drowned extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Enderman extends Monster {

        public static WrappedWatchableObject carriedBlock(final Object carriedBlock) {
            if (VERSION >= 9) return FACTORY.createWatchableOptionalIBlockData(12, Optional.of(carriedBlock));
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Enderman carriedBlock<NMSBlockData> metadata");
        }

        public static WrappedWatchableObject carriedBlockId(final short carriedBlockId) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "Versions 1.9 and later don't support Enderman carried block ID metadata"
            );
            return FACTORY.createWatchable(16, carriedBlockId);
        }

        public static WrappedWatchableObject carriedBlockData(final byte carriedBlockData) {
            if (VERSION >= 9) throw new UnsupportedOperationException(
                    "Versions 1.9 and later don't support Enderman carried block data metadata"
            );
            return FACTORY.createWatchable(17, carriedBlockData);
        }

        public static WrappedWatchableObject screaming(final boolean screaming) {
            return FACTORY.createWatchable(VERSION >= 9 ? 13 : 18, screaming);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class EnderDragon extends Monster {

        public static WrappedWatchableObject phase(final Phase phase) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, phase.value);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Ender Dragon phase metadata");
        }

        @RequiredArgsConstructor
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

            int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Flying extends Insentient {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Ghast extends Flying {

        public static WrappedWatchableObject attacking(final boolean attacking) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, attacking);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Phantom extends Flying {

        public static WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(12, size);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class Slime extends Insentient {

        public static WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, size);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class LlamaSpit extends Entity {}

    @NoArgsConstructor(access = AccessLevel.NONE)
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

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartRideable extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartContainer extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartHopper extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartChest extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartFurnace extends Minecart {

        public static WrappedWatchableObject powered(final boolean powered) {
            return FACTORY.createWatchable(VERSION >= 9 ? 12 : 16, powered);
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartTnt extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartSpawner extends Minecart {}

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class MinecartCommandBlock extends Minecart {
        
        public static WrappedWatchableObject command(final String command) {
            if (VERSION >= 9) return FACTORY.createWatchable(12, command);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Minecart Command Block command metadata");
        }
        
        public static WrappedWatchableObject lastOutput(final WrappedChatComponent lastOutput) {
            if (VERSION >= 9) return FACTORY.createWatchable(13, lastOutput);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support Minecart Command Block last output metadata");
        }
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static class TNTPrimed extends Entity {

        public static WrappedWatchableObject fuseTime(final int fuseTime) {
            if (VERSION >= 9) return FACTORY.createWatchable(6, fuseTime);
            throw new UnsupportedOperationException("Versions prior to 1.9 don't support TNT Primed fuse time metadata");
        }
    }
}

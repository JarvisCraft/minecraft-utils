package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.bukkit.inventory.ItemStack;
import ru.progrm_jarvis.minecraft.commons.nms.Conversions;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * Editor for Metadata of {@link WrappedWatchableObject} providing classes
 * containing static methods for developer-friendly object creation.
 */
@UtilityClass
public class MetadataGenerator {

    private final int VERSION = NmsUtil.getVersion().getGeneration();
    private final DataWatcherFactory FACTORY = NmsUtil.getDataWatcherFactory();

    public static class Entity {

        public static WrappedWatchableObject entityFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(0, flagBytes);
        }

        public static WrappedWatchableObject air(final boolean air) {
            return FACTORY.createWatchable(1, air);
        }

        public static WrappedWatchableObject name(final WrappedChatComponent name) {
            return FACTORY.createWatchable(2, name);
        }

        public static WrappedWatchableObject name(final String name) {
            if (VERSION > 13) return name(WrappedChatComponent.fromText(name));
            return FACTORY.createWatchable(2, name);
        }

        public static WrappedWatchableObject nameVisible(final boolean nameVisible) {
            return FACTORY.createWatchable(3, nameVisible);
        }

        public static WrappedWatchableObject silent(final boolean silent) {
            return FACTORY.createWatchable(4, silent);
        }

        public static WrappedWatchableObject noGravity(final boolean noGravity) {
            return FACTORY.createWatchable(5, noGravity);
        }

        @RequiredArgsConstructor
        public enum Flag {
            ON_FIRE((byte) 0x01),
            CROUCHED((byte) 0x02),
            RIDING((byte) 0x04),
            SPRINTING((byte) 0x08),
            SWIMMING((byte) 0x10),
            INVISIBLE((byte) 0x20),
            GLOWING((byte) 0x80);

            private final byte value;
        }
    }

    public static class Projectile extends Entity {}

    public static class Snowball extends Projectile {}

    public static class Egg extends Projectile {}

    public static class Potion extends Projectile {

        public static WrappedWatchableObject potion(final Object potion) {
            return FACTORY.createWatchableItemStack(6, potion);
        }

        public static WrappedWatchableObject potion(final ItemStack potion) {
            return FACTORY.createWatchable(6, potion);
        }
    }

    public static class FallingBlock extends Entity {

        public static WrappedWatchableObject position(final BlockPosition position) {
            return FACTORY.createWatchable(6, position);
        }
    }

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

    public static class FishingHook extends Entity {

        public static WrappedWatchableObject hookedEntity(final int hookedEntityId) {
            return FACTORY.createWatchable(6, hookedEntityId);
        }

        public static WrappedWatchableObject hookedEntity(final org.bukkit.entity.Entity entity) {
            return hookedEntity(entity.getEntityId() + 1);
        }
    }

    public static class Arrow extends Entity {

        public static WrappedWatchableObject arrowFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(6, flagBytes);
        }

        public static WrappedWatchableObject shooter(final UUID shooterUuid) {
            return FACTORY.createWatchableOptionalUUID(7, Optional.ofNullable(shooterUuid));
        }

        @RequiredArgsConstructor
        public enum Flags {
            CRITICAL((byte) 0x01);

            private final byte value;
        }
    }

    public static class TippedArrow extends Arrow {

        public static WrappedWatchableObject color(final int color) {
            return FACTORY.createWatchable(8, color);
        }
    }

    public static class Trident extends Arrow {

        public static WrappedWatchableObject loyaltyLevel(final int loyaltyLevel) {
            return FACTORY.createWatchable(8, loyaltyLevel);
        }
    }

    public static class Boat extends Entity {

        public static WrappedWatchableObject timeSinceLastHit(final int timeSinceLastHit) {
            return FACTORY.createWatchable(6, timeSinceLastHit);
        }

        public static WrappedWatchableObject forwardDirection(final int forwardDirection) {
            return FACTORY.createWatchable(7, forwardDirection);
        }

        public static WrappedWatchableObject damageTaken(final float damageTaken) {
            return FACTORY.createWatchable(8, damageTaken);
        }

        public static WrappedWatchableObject type(final Type type) {
            return FACTORY.createWatchable(9, type.value);
        }

        public static WrappedWatchableObject rightPaddleTurning(final boolean rightPaddleTurning) {
            return FACTORY.createWatchable(10, rightPaddleTurning);
        }

        public static WrappedWatchableObject leftPaddleTurning(final boolean leftPaddleTurning) {
            return FACTORY.createWatchable(11, leftPaddleTurning);
        }

        public static WrappedWatchableObject splashTimer(final int splashTimer) {
            return FACTORY.createWatchable(12, splashTimer);
        }

        @RequiredArgsConstructor
        public enum Type {
            OAK((byte) 0),
            SPRUCE((byte) 1),
            BIRCH((byte) 2),
            JUNGLE((byte) 3),
            ACACIA((byte) 4),
            DARK_OAK((byte) 5);

            private final byte value;
        }
    }

    public static class EnderCrystal extends Entity {

        public static WrappedWatchableObject position(final BlockPosition position) {
            return FACTORY.createWatchableOptionalBlockPosition(6, Optional.of(position));
        }

        public static WrappedWatchableObject showBottom(final boolean showBottom) {
            return FACTORY.createWatchable(7, showBottom);
        }
    }

    public static class Fireball extends Entity {}

    public static class WitherSkull extends Entity {

        public static WrappedWatchableObject invulnerable(final boolean invulnerable) {
            return FACTORY.createWatchable(6, invulnerable);
        }
    }

    public static class Fireworks extends Entity {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(6, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(6, item);
        }

        public static WrappedWatchableObject shooter(final int shooter) {
            return FACTORY.createWatchable(6, shooter);
        }

        public static WrappedWatchableObject shooter(final org.bukkit.entity.Entity entity) {
            return shooter(entity.getEntityId());
        }
    }

    public static class Hanging extends Entity {}

    public static class ItemFrame extends Hanging {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(6, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(6, item);
        }

        public static WrappedWatchableObject rotation(final int rotation) {
            return FACTORY.createWatchable(7, rotation);
        }
    }

    public static class Item extends Entity {

        public static WrappedWatchableObject item(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(6, nmsItem);
        }

        public static WrappedWatchableObject item(final ItemStack item) {
            return FACTORY.createWatchable(6, item);
        }
    }

    public static class Living extends Entity {

        public static WrappedWatchableObject handStates(final HandState... handStates) {
            var handStateBytes = (byte) 0;
            for (val handState : handStates) handStateBytes |= handState.value;

            return FACTORY.createWatchableObject(6, handStateBytes);
        }

        public static WrappedWatchableObject health(final float health) {
            return FACTORY.createWatchable(7, health);
        }

        public static WrappedWatchableObject potionEffectColor(final int potionEffectColor) {
            return FACTORY.createWatchable(8, potionEffectColor);
        }

        public static WrappedWatchableObject potionEffectAmbient(final boolean potionEffectAmbient) {
            return FACTORY.createWatchable(9, potionEffectAmbient);
        }

        public static WrappedWatchableObject numberOfArrows(final int numberOfArrows) {
            return FACTORY.createWatchable(10, numberOfArrows);
        }

        @RequiredArgsConstructor
        public enum HandState {
            HAND_ACTIVE((byte) 0x01),
            OFFHAND((byte) 0x02),
            RIPTIDE_SPIN_ATTACK((byte) 0x04);

            private final byte value;
        }
    }

    public static class Player extends Living {

        public static WrappedWatchableObject additionalHearts(final float additionalHearts) {
            return FACTORY.createWatchable(11, additionalHearts);
        }

        public static WrappedWatchableObject score(final int score) {
            return FACTORY.createWatchable(12, score);
        }

        public static WrappedWatchableObject skinParts(final SkinPart... skinParts) {
            var skinPartBytes = (byte) 0;
            for (val skinPart : skinParts) skinPartBytes |= skinPart.value;

            return FACTORY.createWatchableObject(13, skinPartBytes);
        }

        public static WrappedWatchableObject mainHand(final MainHand mainHand) {
            return FACTORY.createWatchable(14, mainHand.value);
        }

        public static WrappedWatchableObject leftShoulderEntity(final Object leftShoulderEntityNbtTagCompound) {
            return FACTORY.createWatchableObject(15, leftShoulderEntityNbtTagCompound);
        }

        public static WrappedWatchableObject leftShoulderEntity(final NbtCompound leftShoulderEntityNbt) {
            return leftShoulderEntity(leftShoulderEntityNbt.getHandle());
        }

        public static WrappedWatchableObject rightShoulderEntity(final Object rightShoulderEntityNbtTagCompound) {
            return FACTORY.createWatchableNBTTagCompound(16, rightShoulderEntityNbtTagCompound);
        }

        public static WrappedWatchableObject rightShoulderEntity(final NbtCompound rightShoulderEntityNbt) {
            return rightShoulderEntity(rightShoulderEntityNbt.getHandle());
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

    public static class ArmorStand extends Living {

        public static WrappedWatchableObject armorStandFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(11, flagBytes);
        }

        public static WrappedWatchableObject headRotation(final Vector3F headRotation) {
            return FACTORY.createWatchable(12, headRotation);
        }

        public static WrappedWatchableObject bodyRotation(final Vector3F bodyRotation) {
            return FACTORY.createWatchable(13, bodyRotation);
        }

        public static WrappedWatchableObject leftArmRotation(final Vector3F leftArmRotation) {
            return FACTORY.createWatchable(14, leftArmRotation);
        }

        public static WrappedWatchableObject rightArmRotation(final Vector3F rightArmRotation) {
            return FACTORY.createWatchable(15, rightArmRotation);
        }

        public static WrappedWatchableObject leftLegRotation(final Vector3F leftLegRotation) {
            return FACTORY.createWatchable(16, leftLegRotation);
        }

        public static WrappedWatchableObject rightLegRotation(final Vector3F rightLegRotation) {
            return FACTORY.createWatchable(17, rightLegRotation);
        }

        @RequiredArgsConstructor
        public enum Flag {
            SMALL((byte) 0x01),
            HAS_ARMS((byte) 0x04),
            NO_BASE_PLATE((byte) 0x08),
            MARKER((byte) 0x10);

            private final byte value;
        }
    }

    public static class Insentient extends Living {

        public static WrappedWatchableObject insentientFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(11, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            NO_AI((byte) 0x01),
            LEFT_HANDED((byte) 0x02);

            private final byte value;
        }
    }

    public static class Ambient extends Insentient {}

    public static class Bat extends Ambient {

        public static WrappedWatchableObject batFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            HANGING((byte) 0x01);

            private final byte value;
        }
    }

    public static class Creature extends Insentient {}

    public static class WaterMob extends Creature {}

    public static class Squid extends WaterMob {}

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

    public static class Fish extends WaterMob {

        public static WrappedWatchableObject fromBucket(final boolean fromBucket) {
            return FACTORY.createWatchable(12, fromBucket);
        }
    }

    public static class Cod extends Fish {}

    public static class PufferFish extends Fish {

        public static WrappedWatchableObject puffState(final int puffState) {
            return FACTORY.createWatchable(13, puffState);
        }
    }

    public static class Salmon extends Fish {}

    public static class TropicalFish extends Fish {

        public static WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(13, variant);
        }
    }

    public static class Ageable extends Creature {

        public static WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(12, baby);
        }
    }

    public static class Animal extends Ageable {}

    public static class AbstractHorse extends Animal {

        public static WrappedWatchableObject horseFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(13, flagBytes);
        }

        public static WrappedWatchableObject owner(final UUID ownerUuid) {
            return FACTORY.createWatchableOptionalUUID(14, Optional.ofNullable(ownerUuid));
        }

        @RequiredArgsConstructor
        public enum Flag {
            UNUSED_1((byte) 0x01),
            TAME((byte) 0x02),
            SADDLED((byte) 0x04),
            BRED((byte) 0x08),
            EATING((byte) 0x10),
            REARING((byte) 0x20),
            MOUTH_OPEN((byte) 0x40),
            UNUSED_2((byte) 0x80);

            private final byte value;
        }
    }

    public static class Horse extends AbstractHorse {

        public static WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(15, variant);
        }

        public static WrappedWatchableObject armor(final Armor armor) {
            return FACTORY.createWatchable(16, armor.value);
        }

        public static WrappedWatchableObject forgeArmor(final Object nmsItem) {
            return FACTORY.createWatchableItemStack(17, nmsItem);
        }

        public static WrappedWatchableObject forgeArmor(final ItemStack item) {
            return FACTORY.createWatchable(17, item);
        }

        @RequiredArgsConstructor
        public enum Armor {
            NONE(0),
            IRON(1),
            GOLD(2),
            DIAMOND(3);

            private final int value;
        }
    }

    public static class ZombieHorse extends AbstractHorse {}

    public static class SkeletonHorse extends AbstractHorse {}

    public static class ChestedHorse extends AbstractHorse {

        public static WrappedWatchableObject chest(final boolean chest) {
            return FACTORY.createWatchable(15, chest);
        }
    }

    public static class Donkey extends ChestedHorse {}

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
        public enum Variant {
            CREAMY((byte) 0),
            WHITE((byte) 1),
            BROWN((byte) 2),
            GRAY((byte) 3);

            private final byte value;
        }
    }

    public static class Mule extends ChestedHorse {}

    public static class Pig extends Animal {

        public static WrappedWatchableObject saddle(final boolean saddle) {
            return FACTORY.createWatchable(13, saddle);
        }

        public static WrappedWatchableObject boostTime(final int boostTime) {
            return FACTORY.createWatchable(14, boostTime);
        }
    }

    public static class Rabbit extends Animal {

        public static WrappedWatchableObject type(final int type) {
            return FACTORY.createWatchable(13, type);
        }
    }

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

    public static class PolarBear extends Animal {

        public static WrappedWatchableObject standingUp(final boolean standingUp) {
            return FACTORY.createWatchable(13, standingUp);
        }
    }

    public static class Sheep extends Animal {

        public static WrappedWatchableObject standingUp(final byte color, final boolean sheared) {
            return FACTORY.createWatchable(13, color & 0x0F | (sheared ? 0 : 0x10));
        }
    }

    public static class Tameable extends Animal {

        public static WrappedWatchableObject tameableFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(13, flagBytes);
        }

        public static WrappedWatchableObject owner(final UUID ownerUuid) {
            return FACTORY.createWatchableOptionalUUID(14, Optional.ofNullable(ownerUuid));
        }

        @RequiredArgsConstructor
        public enum Flag {
            SITTING((byte) 0x01),
            ANGRY((byte) 0x02),
            TAMED((byte) 0x04);

            private final byte value;
        }
    }

    public static class Ocelot extends Tameable {

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(15, variant.value);
        }

        @RequiredArgsConstructor
        public enum Variant {
            UNTAMED(0),
            TUXEDO(1),
            TABBY(2),
            SIAMESE(3);

            private final int value;
        }
    }

    public static class Wolf extends Tameable {

        public static WrappedWatchableObject damageTaken(final float damageTaken) {
            return FACTORY.createWatchable(15, damageTaken);
        }

        public static WrappedWatchableObject begging(final boolean begging) {
            return FACTORY.createWatchable(16, begging);
        }

        public static WrappedWatchableObject collarColor(final byte collarColor) {
            return FACTORY.createWatchable(17, collarColor);
        }
    }

    public static class Parrot extends Tameable {

        public static WrappedWatchableObject variant(final Variant variant) {
            return FACTORY.createWatchable(15, variant.value);
        }

        @RequiredArgsConstructor
        public enum Variant {
            RED_BLUE(0),
            BLUE(1),
            GREEN(2),
            YELLOW_BLUE(3),
            SILVER(4);

            private final int value;
        }
    }

    public static class Villager extends Ageable {

        public static WrappedWatchableObject profession(final Profession profession) {
            return FACTORY.createWatchable(13, profession.value);
        }

        @RequiredArgsConstructor
        public enum Profession {
            FARMER(0),
            LIBRARIAN(1),
            PRIEST(2),
            BLACKSMITH(3),
            SILVER(4);

            private final int value;
        }
    }

    public static class Golem extends Creature {}

    public static class IronGolem extends Golem {

        public static WrappedWatchableObject ironGolemFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            PLAYER_CREATED((byte) 0x01);

            private final byte value;
        }
    }

    public static class Snowman extends Golem {

        public static WrappedWatchableObject ironGolemFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            HAS_PUMPKIN((byte) 0x10);

            private final byte value;
        }
    }

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

    public static class Monster extends Creature {}

    public static class Blaze extends Monster {

        public static WrappedWatchableObject blazeFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            ON_FIRE((byte) 0x01);

            private final byte value;
        }
    }

    public static class Creeper extends Monster {

        public static WrappedWatchableObject creeperState(final State state) {
            return FACTORY.createWatchable(12, state.value);
        }

        public static WrappedWatchableObject charged(final boolean charged) {
            return FACTORY.createWatchable(13, charged);
        }

        public static WrappedWatchableObject ignited(final boolean ignited) {
            return FACTORY.createWatchable(14, ignited);
        }

        @RequiredArgsConstructor
        public enum State {
            IDLE(-1),
            FUSE(1);

            private final int value;
        }
    }

    public static class Endermite extends Monster {}

    public static class GiantZombie extends Monster {}

    public static class Guardian extends Monster {

        public static WrappedWatchableObject retractingSpikes(final boolean retractingSpikes) {
            return FACTORY.createWatchable(12, retractingSpikes);
        }

        public static WrappedWatchableObject targetEntity(final int targetEntityId) {
            return FACTORY.createWatchable(13, targetEntityId);
        }

        public static WrappedWatchableObject targetEntity(final org.bukkit.entity.Entity entity) {
            return targetEntity(entity.getEntityId());
        }
    }

    public static class ElderGuardian extends Guardian {}

    public static class Silverfish extends Monster {}

    public static class Illager extends Monster {

        public static WrappedWatchableObject illagerState(final State state) {
            return FACTORY.createWatchable(12, state.value);
        }

        @RequiredArgsConstructor
        public enum State {
            HAS_TARGET((byte) 0x01);

            private final byte value;
        }
    }

    public static class VindicatorIllager extends Illager {}

    public static class SpellcasterIllager extends Illager {

        public static WrappedWatchableObject spell(final Spell spell) {
            return FACTORY.createWatchable(13, spell.value);
        }

        @RequiredArgsConstructor
        public enum Spell {
            NONE((byte) 0),
            SUMMON_VEX((byte) 1),
            ATTACK((byte) 2),
            WOLOLO((byte) 3);

            private final byte value;
        }
    }

    public static class EvocationIllager extends SpellcasterIllager {}

    public static class IllusionIllager extends SpellcasterIllager {}

    public static class Vex extends Monster {

        public static WrappedWatchableObject vexFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            ATTACK_MODE((byte) 0x01);

            private final byte value;
        }
    }

    public static class EvocationFangs extends Entity {}

    public static class AbstractSkeleton extends Monster {

        public static WrappedWatchableObject swingingArms(final boolean swingingArms) {
            return FACTORY.createWatchable(12, swingingArms);
        }
    }

    public static class Skeleton extends AbstractSkeleton {}

    public static class WitherSkeleton extends AbstractSkeleton {}

    public static class Stray extends AbstractSkeleton {}

    public static class Spider extends Monster {

        public static WrappedWatchableObject spiderFlags(final Flag... flags) {
            var flagBytes = (byte) 0;
            for (val flag : flags) flagBytes |= flag.value;

            return FACTORY.createWatchable(12, flagBytes);
        }

        @RequiredArgsConstructor
        public enum Flag {
            CLIMBING((byte) 0x01);

            private final byte value;
        }
    }

    public static class Witch extends Monster {

        public static WrappedWatchableObject drinkingPotion(final boolean drinkingPotion) {
            return FACTORY.createWatchable(12, drinkingPotion);
        }
    }

    public static class Wither extends Monster {

        public static WrappedWatchableObject centerHeadTarget(final int centerHeadTargetId) {
            return FACTORY.createWatchable(12, centerHeadTargetId);
        }

        public static WrappedWatchableObject centerHeadTarget(final org.bukkit.entity.Entity centerHeadTarget) {
            return centerHeadTarget(centerHeadTarget == null ? 0 : centerHeadTarget.getEntityId());
        }

        public static WrappedWatchableObject leftHeadTarget(final int leftHeadTargetId) {
            return FACTORY.createWatchable(13, leftHeadTargetId);
        }

        public static WrappedWatchableObject leftHeadTarget(final org.bukkit.entity.Entity leftHeadTarget) {
            return leftHeadTarget(leftHeadTarget == null ? 0 : leftHeadTarget.getEntityId());
        }

        public static WrappedWatchableObject rightHeadTarget(final int rightHeadTargetId) {
            return FACTORY.createWatchable(14, rightHeadTargetId);
        }

        public static WrappedWatchableObject rightHeadTarget(final org.bukkit.entity.Entity rightHeadTarget) {
            return rightHeadTarget(rightHeadTarget == null ? 0 : rightHeadTarget.getEntityId());
        }

        public static WrappedWatchableObject invulnerableTime(final int invulnerableTime) {
            return FACTORY.createWatchable(15, invulnerableTime);
        }
    }

    public static class Zombie extends Monster {

        public static WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(12, baby);
        }

        public static WrappedWatchableObject legacyType(final int legacyType) {
            return FACTORY.createWatchable(13, legacyType);
        }

        public static WrappedWatchableObject handsUp(final boolean handsUp) {
            return FACTORY.createWatchable(14, handsUp);
        }

        public static WrappedWatchableObject becomingDrowned(final boolean becomingDrowned) {
            return FACTORY.createWatchable(15, becomingDrowned);
        }
    }

    public static class ZombieVillager extends Zombie {

        public static WrappedWatchableObject converting(final boolean converting) {
            return FACTORY.createWatchable(16, converting);
        }

        public static WrappedWatchableObject profession(final Villager.Profession profession) {
            return FACTORY.createWatchable(17, profession.value);
        }
    }

    public static class Husk extends Zombie {}

    public static class Drowned extends Zombie {}

    public static class Enderman extends Monster {

        public static WrappedWatchableObject carriedBlock(final Object carriedBlock) {
            return FACTORY.createWatchableOptionalIBlockData(12, Optional.of(carriedBlock));
        }

        public static WrappedWatchableObject screaming(final boolean screaming) {
            return FACTORY.createWatchable(13, screaming);
        }
    }

    public static class EnderDragon extends Monster {

        public static WrappedWatchableObject phase(final Phase phase) {
            return FACTORY.createWatchable(12, phase.value);
        }

        @RequiredArgsConstructor
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

            private final int value;
        }
    }

    public static class Flying extends Insentient {}

    public static class Ghast extends Flying {

        public static WrappedWatchableObject attacking(final boolean attacking) {
            return FACTORY.createWatchable(12, attacking);
        }
    }

    public static class Phantom extends Flying {

        public static WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(12, size);
        }
    }

    public static class Slime extends Insentient {

        public static WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(12, size);
        }
    }

    public static class LlamaSpit extends Entity {}

    public static class Minecart extends Entity {

        public static WrappedWatchableObject shakingPower(final int shakingPower) {
            return FACTORY.createWatchable(6, shakingPower);
        }

        public static WrappedWatchableObject shakingDirection(final int shakingDirection) {
            return FACTORY.createWatchable(7, shakingDirection);
        }

        public static WrappedWatchableObject shakingMultiplier(final int shakingMultiplier) {
            return FACTORY.createWatchable(8, shakingMultiplier);
        }

        public static WrappedWatchableObject customBlockIdAndDamage(final int customBlockIdAndDamage) {
            return FACTORY.createWatchable(9, customBlockIdAndDamage);
        }

        public static WrappedWatchableObject customBlockY(final int customBlockY) {
            return FACTORY.createWatchable(10, customBlockY);
        }

        public static WrappedWatchableObject showCustomBlock(final boolean showCustomBlock) {
            return FACTORY.createWatchable(11, showCustomBlock);
        }
    }

    public static class MinecartRideable extends Minecart {}

    public static class MinecartContainer extends Minecart {}

    public static class MinecartHopper extends Minecart {}

    public static class MinecartChest extends Minecart {}

    public static class MinecartFurnace extends Minecart {

        public static WrappedWatchableObject powered(final boolean powered) {
            return FACTORY.createWatchable(12, powered);
        }
    }

    public static class MinecartTnt extends Minecart {}

    public static class MinecartSpawner extends Minecart {}

    public static class MinecartCommandBlock extends Minecart {
        
        public static WrappedWatchableObject command(final String command) {
            return FACTORY.createWatchable(12, command);
        }
        
        public static WrappedWatchableObject lastOutput(final WrappedChatComponent lastOutput) {
            return FACTORY.createWatchable(13, lastOutput);
        }
    }

    public static class TNTPrimed extends Entity {

        public static WrappedWatchableObject fuseTime(final int fuseTime) {
            return FACTORY.createWatchable(6, fuseTime);
        }
    }
}

package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour.FakeEntityInteraction.Hand;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.FakeEntityManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProtocolBasedFakeEntityInteractionHandler<P extends Plugin, E extends InteractableFakeEntity>
        extends PacketAdapter implements FakeEntityInteractionHandler<P, E> {

    P plugin;
    Set<E> entities;
    Set<E> entitiesView;

    public ProtocolBasedFakeEntityInteractionHandler(@NonNull final P plugin, final boolean concurrent) {
        super(
                checkNotNull(plugin, "plugin should not be null"),
                PacketType.Play.Client.USE_ENTITY
        );

        this.plugin = plugin;
        entities = concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet();
        entitiesView = Collections.unmodifiableSet(entities);

        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public P getBukkitPlugin() {
        return plugin;
    }

    protected Hand hand(final WrapperPlayClientUseEntity packet) {
        return packet.getHandle().getHands().read(0) == EnumWrappers.Hand.MAIN_HAND ? Hand.MAIN : Hand.OFF;
    }

    @Override
    public void onPacketReceiving(final PacketEvent event) {
        val packet = new WrapperPlayClientUseEntity(event.getPacket());
        val id = packet.getTargetID();

        InteractableFakeEntity entity = null;
        for (val checkedEntity : entities) if (checkedEntity.getEntityId() == id) entity = checkedEntity;
        if (entity == null) return;

        switch (packet.getType()) {
            case INTERACT: {
                entity.handleInteraction(event.getPlayer(), FakeEntityInteraction.interact(id, hand(packet)));

                break;
            }
            case ATTACK: {
                entity.handleInteraction(event.getPlayer(), FakeEntityInteraction.attack(id));

                break;
            }
            case INTERACT_AT: {
                entity.handleInteraction(event.getPlayer(), FakeEntityInteraction
                        .exactInteract(id, hand(packet), packet.getTargetVector())
                );

                break;
            }
        }
    }

    @Override
    public int managedEntitiesSize() {
        return entities.size();
    }

    @Override
    public boolean isManaged(@NonNull final E entity) {
        return entities.contains(entity);
    }

    @Override
    public Collection<E> getManagedEntities() {
        return entities;
    }

    @Override
    public void manageEntity(@NonNull E entity) {
        entities.add(entity);
    }

    @Override
    public void unmanageEntity(@NonNull E entity) {
        entities.remove(entity);
    }
}

package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.FakeEntityManager;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour.FakeEntityInteraction.Hand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ProtocolBasedFakeEntityInteractionHandler<E extends InteractableFakeEntity>
        extends PacketAdapter implements FakeEntityInteractionHandler<E> {

    private Set<E> entities;

    public ProtocolBasedFakeEntityInteractionHandler(@NonNull final Plugin plugin, final boolean concurrent) {
        super(plugin, PacketType.Play.Client.USE_ENTITY);

        ProtocolLibrary.getProtocolManager().addPacketListener(this);

        entities = concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet();
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
    public Collection<E> getManagedEntities() {
        return entities;
    }

    @Override
    public Collection<E> getManagedEntitiesCollection() {
        return new ArrayList<>(entities);
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

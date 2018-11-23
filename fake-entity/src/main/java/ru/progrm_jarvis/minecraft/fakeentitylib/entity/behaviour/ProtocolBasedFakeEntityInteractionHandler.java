package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.NonNull;
import lombok.val;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour.FakeEntityInteraction.Hand;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ProtocolBasedFakeEntityInteractionHandler extends PacketAdapter implements FakeEntityInteractionHandler {

    private Set<InteractableFakeEntity> entities = Collections
            .synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    public ProtocolBasedFakeEntityInteractionHandler(@NonNull final Plugin plugin) {
        super(plugin, PacketType.Play.Client.USE_ENTITY);

        ProtocolLibrary.getProtocolManager().addPacketListener(this);
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
    public void register(@NonNull final InteractableFakeEntity entity) {
        entities.add(entity);
    }

    @Override
    public void unregister(@NonNull final InteractableFakeEntity entity) {
        entities.remove(entity);
    }
}

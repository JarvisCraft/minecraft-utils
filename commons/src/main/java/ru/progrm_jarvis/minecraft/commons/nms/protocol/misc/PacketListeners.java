package ru.progrm_jarvis.minecraft.commons.nms.protocol.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

@UtilityClass
public class PacketListeners {

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> inboundPacketCallback,
                                                 @NonNull final Consumer<PacketEvent> outboundPacketCallback,
                                                 @NonNull final PacketType... packetTypes) {
        return new CallbackServerPacketListener(plugin, inboundPacketCallback, outboundPacketCallback, packetTypes);
    }

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> inboundPacketCallback,
                                                 @NonNull final Consumer<PacketEvent> outboundPacketCallback,
                                                 @NonNull final Iterable<PacketType> packetTypes) {
        return new CallbackServerPacketListener(plugin, inboundPacketCallback, outboundPacketCallback, packetTypes);
    }

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> inboundPacketCallback,
                                                 @NonNull final Consumer<PacketEvent> outboundPacketCallback,
                                                 @NonNull final PacketCategory... packetCategories) {
        return callbackPacketListener(plugin, inboundPacketCallback, outboundPacketCallback, Arrays
                .stream(packetCategories)
                .flatMap(packetCategory -> Arrays.stream(packetCategory.packetTypes))
                .toArray(PacketType[]::new)
        );
    }

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> inboundPacketCallback,
                                                 @NonNull final Consumer<PacketEvent> outboundPacketCallback,
                                                 @NonNull final Collection<PacketCategory> packetCategories) {
        return callbackPacketListener(plugin, inboundPacketCallback, outboundPacketCallback, packetCategories
                .stream()
                .flatMap(packetCategory -> Arrays.stream(packetCategory.packetTypes))
                .toArray(PacketType[]::new)
        );
    }

    @Getter
    @ToString
    public enum PacketCategory {
        PROTOCOL(PacketType.Protocol.class),
        LEGACY_CLIENT(PacketType.Legacy.Client.class),
        LEGACY_SERVER(PacketType.Legacy.Server.class),
        LOGIN_CLIENT(PacketType.Login.Client.class),
        LOGIN_SERVER(PacketType.Login.Server.class),
        STATUS_CLIENT(PacketType.Status.Client.class),
        STATUS_SERVER(PacketType.Status.Server.class),
        PLAY_CLIENT(PacketType.Play.Client.class),
        PLAY_SERVER(PacketType.Play.Server.class),
        HANDSHAKE_CLIENT(PacketType.Handshake.Client.class),
        HANDSHAKE_SERVER(PacketType.Handshake.Server.class),;

        private final PacketType[] packetTypes;

        PacketCategory(@NonNull final Class containingClass) {
            packetTypes = packetTypesFromContainingClass(containingClass);
        }
    }

    private static PacketType[] packetTypesFromContainingClass(@NonNull final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> PacketType.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(PacketType[]::new);
    }

    private static class CallbackServerPacketListener extends PacketAdapter {

        @NonNull private final Consumer<PacketEvent> inboundPacketCallback;
        @NonNull private final Consumer<PacketEvent> outboundPacketCallback;

        public CallbackServerPacketListener(@NonNull final Plugin plugin,
                                            @NonNull final Consumer<PacketEvent> inboundPacketCallback,
                                            @NonNull final Consumer<PacketEvent> outboundPacketCallback,
                                            @NonNull final PacketType... packetTypes) {
            super(plugin, packetTypes);

            this.inboundPacketCallback = inboundPacketCallback;
            this.outboundPacketCallback = outboundPacketCallback;
        }

        public CallbackServerPacketListener(@NonNull final Plugin plugin,
                                            @NonNull final Consumer<PacketEvent> inboundPacketCallback,
                                            @NonNull final Consumer<PacketEvent> outboundPacketCallback,
                                            @NonNull final Iterable<PacketType> packetTypes) {
            super(plugin, packetTypes);

            this.inboundPacketCallback = inboundPacketCallback;
            this.outboundPacketCallback = outboundPacketCallback;
        }

        @Override
        public void onPacketSending(final PacketEvent event) {
            inboundPacketCallback.accept(event);
        }

        @Override
        public void onPacketReceiving(final PacketEvent event) {
            inboundPacketCallback.accept(event);
        }
    }

}

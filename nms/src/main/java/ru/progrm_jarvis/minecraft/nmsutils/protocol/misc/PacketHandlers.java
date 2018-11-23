package ru.progrm_jarvis.minecraft.nmsutils.protocol.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import lombok.*;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class PacketHandlers {

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> callback,
                                                 @NonNull final PacketType... packetTypes) {
        return new CallbackServerPacketListener(plugin, callback, packetTypes);
    }

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> callback,
                                                 @NonNull final Iterable<PacketType> packetTypes) {
        return new CallbackServerPacketListener(plugin, callback, packetTypes);
    }

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> callback,
                                                 @NonNull final PacketCategory... packetCategories) {
        return callbackPacketListener(plugin, callback, Arrays.stream(packetCategories)
                .map(packetCategory -> packetCategory.packetTypes)
                .toArray(PacketType[]::new)
        );
    }

    public PacketListener callbackPacketListener(@NonNull final Plugin plugin,
                                                 @NonNull final Consumer<PacketEvent> callback,
                                                 @NonNull final Collection<PacketCategory> packetCategories) {
        return callbackPacketListener(plugin, callback, packetCategories.stream()
                .map(packetCategory -> packetCategory.packetTypes)
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
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(PacketType[]::new);
    }

    private static class CallbackServerPacketListener extends PacketAdapter {

        @NonNull private final Consumer<PacketEvent> callback;

        public CallbackServerPacketListener(@NonNull final Plugin plugin,
                                            @NonNull final Consumer<PacketEvent> callback,
                                            @NonNull final PacketType... packetTypes) {
            super(plugin, packetTypes);

            this.callback = callback;
        }

        public CallbackServerPacketListener(@NonNull final Plugin plugin,
                                            @NonNull final Consumer<PacketEvent> callback,
                                            @NonNull final Iterable<PacketType> packetTypes) {
            super(plugin, packetTypes);

            this.callback = callback;
        }

        @Override
        public void onPacketSending(final PacketEvent event) {
            callback.accept(event);
        }
    }

}

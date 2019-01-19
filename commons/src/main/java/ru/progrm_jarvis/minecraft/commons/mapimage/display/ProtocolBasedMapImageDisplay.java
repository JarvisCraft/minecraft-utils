package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import com.comphenix.packetwrapper.WrapperPlayServerMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.mapimage.MapImage;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistries;
import ru.progrm_jarvis.minecraft.commons.player.registry.RegistersSelfInPlayerRegistry;
import ru.progrm_jarvis.minecraft.commons.util.MapUtil;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RegistersSelfInPlayerRegistry
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProtocolBasedMapImageDisplay implements MapImageDisplay {

    @NonNull MapImage image;
    @NonNull Map<Player, MapView> playerMaps;

    public ProtocolBasedMapImageDisplay(@NonNull final MapImage image, @NonNull final Map<Player, MapView> playerMaps,
                                        @NonNull final Plugin plugin) {
        this.image = image;
        this.playerMaps = playerMaps;

        PlayerRegistries.registerInDefaultRegistry(plugin, this);
        image.subscribeOnUpdates(this::sendDeltaToAllPlayers);
    }

    public ProtocolBasedMapImageDisplay(@NonNull final MapImage image, @NonNull final Plugin plugin) {
        this(image, new HashMap<>(), plugin);
    }

    @Override
    public MapImage image() {
        return image;
    }

    /**
     * Sends the whole image to the players.
     */
    protected void sendFullImage(@NonNull final Player player) {
        new WrapperPlayServerMap() {{
            setItemDamage(PlayerMapManager.getMapId(playerMaps.get(player)).intValue());
            setScale(image.getDisplay());
            setColumns(image.getWidth());
            setRows(image.getHeight());
            setX(0);
            setZ(0);
            setData(image.getMapData());
        }}.sendPacket(player);
    }

    protected WrapperPlayServerMap newDeltaPacket(@Nonnull final MapImage.Delta delta) {
        return new WrapperPlayServerMap() {{
            setScale(image.getDisplay());
            setColumns(delta.width());
            setRows(delta.height());
            setX(delta.leastX());
            setZ(delta.leastY());
            setData(image.getMapData(delta));
        }};
    }

    protected void sendDelta(@NonNull final Player player, @NonNull final MapImage.Delta delta) {
        if (delta.isEmpty()) return;

        val packet = newDeltaPacket(delta);
        packet.setItemDamage(PlayerMapManager.getMapId(playerMaps.get(player)).intValue());

        packet.sendPacket(player);
    }

    protected void sendDeltaToAllPlayers(@NonNull final MapImage.Delta delta) {
        if (delta.isEmpty()) return;

        val packet = newDeltaPacket(delta);

        for (val entry : playerMaps.entrySet()) {
            packet.setItemDamage(PlayerMapManager.getMapId(entry.getValue()).intValue());
            packet.sendPacket(entry.getKey());
        }
    }

    @Override
    public void addPlayer(final Player player) {
        // computeIfAbsent not to allocate the ID if the player is already contained (and so has ID allocated)
        playerMaps.computeIfAbsent(player, p -> {
            val map = PlayerMapManager.allocateMap(player);
            // although the rendering is contextual there is no need to use Bukkit's contextual renderer
            map.addRenderer(BlankMapRenderer.NON_CONTEXTUAL);

            return map;
        });
        sendFullImage(player);
    }

    @Override
    public void removePlayer(final Player player) {
        val map = playerMaps.remove(player);
        if (map != null) /* null-check just in case */ PlayerMapManager.freeMap(player, map);
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return playerMaps.containsKey(player);
    }

    @Override
    public Collection<Player> getPlayers() {
        return playerMaps.keySet();
    }

    @Override
    public Integer getMapId(final Player player) {
        return MapUtil.getOrDefault(
                playerMaps, player, map -> PlayerMapManager.getMapId(map).intValue(), (Integer) null
        );
    }
}
package ru.progrm_jarvis.minecraft.commons.chunk;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 *
 * @apiNote chunks are (by default) returned as a single {@link long} as the limit of chunk at non-Y-axis is 3750000
 * where the first 32-most significant stand for X coordinate and the last 32 bits stand for Z coordinate
 *
 * @see <a href="https://minecraft.gamepedia.com/Chunk">General info about chinks</a>
 */
@UtilityClass
public class ChunkUtil {

    /**
     * Returns a single {@link long} value storing chunk data for X- and Z-axises.
     *
     * @param x X coordinate of a chunk
     * @param z Z coordinate of a chunk
     * @return chunk treated as {@link long}
     */
    public long chunk(final int x, final int z) {
        return ((long) x << 32) | ((long) z & 0xFFFFFFFFL);
    }

    /**
     * Returns the X coordinate value from a long-serialized chunk
     *
     * @param longChunk chunk data treated as {@link long}
     * @return X coordinate of a chunk
     */
    public int chunkX(final long longChunk) {
        return (int) (longChunk >> 32);
    }

    /**
     * Returns the Z coordinate value from a long-serialized chunk
     *
     * @param longChunk chunk data treated as long
     * @return Z coordinate of a chunk
     */
    public int chunkZ(final long longChunk) {
        return (int) (longChunk);
    }

    /**
     * Gets the chunk by location in a world.
     *
     * @param x X coordinate of the location
     * @param z Y coordinate of the location
     * @return chunk location treated as {@link long}
     */
    public long chunkByLocation(final long x, final long z) {
        return chunk((int) (x >> 4), (int) (z >> 4));
    }

    /**
     * Gets the chunk in the world specified from a {@link long} chunk representation.
     *
     * @param world world to get chunk from
     * @param chunk chunk treated as {@link long}
     * @return specified chunk of the world
     */
    public Chunk getChunk(@NonNull final World world, final long chunk) {
        return world.getChunkAt(chunkX(chunk), chunkZ(chunk));
    }
}

package ru.progrm_jarvis.minecraft.commons.chunk;

import lombok.val;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static ru.progrm_jarvis.minecraft.commons.chunk.ChunkUtil.*;

class ChunkUtilTest {

    @Test
    void testChunkAsLong() {
        assertEquals(1, chunkX(chunk(1, 0)));
        assertEquals(0, chunkZ(chunk(1, 0)));

        assertEquals(0, chunkX(chunk(0, 1)));
        assertEquals(1, chunkZ(chunk(0, 1)));

        assertEquals(-1, chunkX(chunk(-1, 0)));
        assertEquals(0, chunkZ(chunk(-1, 0)));

        assertEquals(0, chunkX(chunk(0, -1)));
        assertEquals(-1, chunkZ(chunk(0, -1)));

        assertEquals(1, chunkX(chunk(1, 255)));
        assertEquals(255, chunkZ(chunk(1, 255)));

        assertEquals(255, chunkX(chunk(255, 1)));
        assertEquals(1, chunkZ(chunk(255, 1)));

        assertEquals(-1, chunkX(chunk(-1, 255)));
        assertEquals(255, chunkZ(chunk(-1, 255)));

        assertEquals(255, chunkX(chunk(255, -1)));
        assertEquals(-1, chunkZ(chunk(255, -1)));

        assertEquals(1, chunkX(chunk(1, -255)));
        assertEquals(-255, chunkZ(chunk(1, -255)));

        assertEquals(-255, chunkX(chunk(-255, 1)));
        assertEquals(1, chunkZ(chunk(-255, 1)));

        assertEquals(-1, chunkX(chunk(-1, -255)));
        assertEquals(-255, chunkZ(chunk(-1, -255)));

        assertEquals(-255, chunkX(chunk(-255, -1)));
        assertEquals(-1, chunkZ(chunk(-255, -1)));
    }

    @Test
    void testChunkByLocation() {
        long chunk;

        chunk = chunkByLocation(0, 0);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkByLocation(15, 15);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkByLocation(16, 16);
        assertEquals(1, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkByLocation(31, 31);
        assertEquals(1, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkByLocation(32, 32);
        assertEquals(2, chunkX(chunk));
        assertEquals(2, chunkZ(chunk));

        chunk = chunkByLocation(47, 47);
        assertEquals(2, chunkX(chunk));
        assertEquals(2, chunkZ(chunk));

        chunk = chunkByLocation(-1, -1);
        assertEquals(-1, chunkX(chunk));
        assertEquals(-1, chunkZ(chunk));

        chunk = chunkByLocation(-16, -16);
        assertEquals(-1, chunkX(chunk));
        assertEquals(-1, chunkZ(chunk));

        chunk = chunkByLocation(-17, -17);
        assertEquals(-2, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkByLocation(-32, -32);
        assertEquals(-2, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkByLocation(-33, -33);
        assertEquals(-3, chunkX(chunk));
        assertEquals(-3, chunkZ(chunk));

        chunk = chunkByLocation(-48, -48);
        assertEquals(-3, chunkX(chunk));
        assertEquals(-3, chunkZ(chunk));

        chunk = chunkByLocation(0, 15);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkByLocation(-1, 15);
        assertEquals(-1, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkByLocation(0, 15);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkByLocation(-31, 31);
        assertEquals(-2, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkByLocation(64, -32);
        assertEquals(4, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkByLocation(10, 20);
        assertEquals(0, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkByLocation(-10, 20);
        assertEquals(-1, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkByLocation(10, -20);
        assertEquals(0, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkByLocation(-10, -20);
        assertEquals(-1, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));
    }

    @Test
    void testGetChunkFromWorld() {
        val world = mock(World.class);

        getChunk(world, chunk(0, 0));
        verify(world).getChunkAt(ArgumentMatchers.eq(0), ArgumentMatchers.eq(0));

        getChunk(world, chunk(1, 0));
        verify(world).getChunkAt(ArgumentMatchers.eq(1), ArgumentMatchers.eq(0));

        getChunk(world, chunk(0, 1));
        verify(world).getChunkAt(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));

        getChunk(world, chunk(-1, 0));
        verify(world).getChunkAt(ArgumentMatchers.eq(-1), ArgumentMatchers.eq(0));

        getChunk(world, chunk(0, -1));
        verify(world).getChunkAt(ArgumentMatchers.eq(0), ArgumentMatchers.eq(-1));

        getChunk(world, chunk(25, 10));
        verify(world).getChunkAt(ArgumentMatchers.eq(25), ArgumentMatchers.eq(10));

        getChunk(world, chunk(-14, 8));
        verify(world).getChunkAt(ArgumentMatchers.eq(-14), ArgumentMatchers.eq(8));

        getChunk(world, chunk(23, -9));
        verify(world).getChunkAt(ArgumentMatchers.eq(23), ArgumentMatchers.eq(-9));

        getChunk(world, chunk(-6, -22));
        verify(world).getChunkAt(ArgumentMatchers.eq(-6), ArgumentMatchers.eq(-22));
    }
}
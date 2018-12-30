package ru.progrm_jarvis.minecraft.commons.mapimage;

import lombok.val;
import lombok.var;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.progrm_jarvis.minecraft.commons.mapimage.MapImage.Delta;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MapImageTest {

    ///////////////////////////////////////////////////////////////////////////
    // Delta
    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testDeltaOf() {
        assertTrue(Delta.EMPTY.isEmpty());

        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], 0, 0));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], 0, 135));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], 1213, 0));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], 125, 65));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], -12, 24));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], 19, -32));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][0], 12, -24));

        assertSame(Delta.EMPTY, Delta.of(new byte[1][0], Delta.NONE, 100));
        assertSame(Delta.EMPTY, Delta.of(new byte[0][3], Delta.NONE, 100));
        assertSame(Delta.EMPTY, Delta.of(new byte[1][3], 80, Delta.NONE));
        assertSame(Delta.EMPTY, Delta.of(new byte[12][23], Delta.NONE, Delta.NONE));
        assertSame(Delta.EMPTY, Delta.of(new byte[10][2], Delta.NONE, Delta.NONE));

        assertSame(Delta.EMPTY, Delta.of(new byte[1][1], Delta.NONE, 100));
        assertSame(Delta.EMPTY, Delta.of(new byte[1][1], 102, Delta.NONE));
        assertSame(Delta.EMPTY, Delta.of(new byte[1][1], Delta.NONE, Delta.NONE));

        assertEquals(new Delta.SinglePixel((byte) 1, 10, 10), Delta.of(new byte[][]{{1}}, 10, 10));
        assertEquals(new Delta.SinglePixel((byte) 32, 100, 10), Delta.of(new byte[][]{{32}}, 100, 10));
        assertEquals(new Delta.SinglePixel((byte) 12, 100, 92), Delta.of(new byte[][]{{12}}, 100, 92));
        assertEquals(new Delta.SinglePixel((byte) -127, 13, 12), Delta.of(new byte[][]{{-127}}, 13, 12));
        assertEquals(new Delta.SinglePixel((byte) 127, 28, 10), Delta.of(new byte[][]{{127}}, 28, 10));
        assertEquals(new Delta.SinglePixel((byte) -128, 23, 23), Delta.of(new byte[][]{{-128}}, 23, 23));
        assertEquals(new Delta.SinglePixel((byte) -23, 25, 122), Delta.of(new byte[][]{{-23}}, 25, 122));

        assertEquals(new Delta.SinglePixel((byte) 1, 10, 10), Delta.of(new byte[][]{{1}}, 10, 10));
        assertEquals(new Delta.SinglePixel((byte) 32, 100, 10), Delta.of(new byte[][]{{32}}, 100, 10));
        assertEquals(new Delta.SinglePixel((byte) 12, 100, 92), Delta.of(new byte[][]{{12}}, 100, 92));
        assertEquals(new Delta.SinglePixel((byte) -127, 13, 12), Delta.of(new byte[][]{{-127}}, 13, 12));
        assertEquals(new Delta.SinglePixel((byte) 127, 28, 10), Delta.of(new byte[][]{{127}}, 28, 10));
        assertEquals(new Delta.SinglePixel((byte) -128, 23, 23), Delta.of(new byte[][]{{-128}}, 23, 23));
        assertEquals(new Delta.SinglePixel((byte) -23, 25, 12), Delta.of(new byte[][]{{-23}}, 25, 12));

        val random = new Random();

        for (var i = 0; i < 128 + random.nextInt(128); i++) {
            final int
                    xLength = 2 + random.nextInt(MapImage.WIDTH - 1), // [2 ; WIDTH]
                    yLength = 2 + random.nextInt(MapImage.HEIGHT - 1), // [2 ; HEIGHT]
                    leastX = random.nextInt(MapImage.WIDTH - xLength + 1), // [0 ; WIDTH - xLength]
                    leastY = random.nextInt(MapImage.HEIGHT - yLength + 1); // [0 ; HEIGHT - yLength]

            val pixels = new byte[xLength][yLength];
            // populate pixels with random values
            for (val column : pixels) random.nextBytes(column);

            val pixelsCopy = new byte[xLength][yLength];
            for (int k = 0; k < pixelsCopy.length; k++) pixelsCopy[k] = pixels[k].clone();

            // verify that pixels were normally copied
            assertArrayEquals(pixels, pixelsCopy);

            MatcherAssert.assertThat(
                    new Delta.NonEmpty(pixelsCopy, leastX, leastY),
                    Matchers.is(Delta.of(pixels, leastX, leastY))
            );
        }
    }
}
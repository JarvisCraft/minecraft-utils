package ru.progrm_jarvis.minecraft.commons.mapimage;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.progrm_jarvis.minecraft.commons.util.function.lazy.Lazies;
import ru.progrm_jarvis.minecraft.commons.util.function.lazy.Lazy;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImage.blankPixels;

/**
 * The default {@link MapImage} implementation which stores its pixels as a 2-dimensional {@link byte}-array.
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DefaultMapImage implements MapImage {

    /**
     * Byte array of pixels of an image by X, Y indexes (columns of rows).
     */
    byte[][] pixels;

    /**
     * Lazily initialized non-buffered drawer
     */
    Lazy<Drawer> drawer = Lazies.lazy(Drawer::new);

    /**
     * Lazily initialized buffered drawer
     */
    Lazy<BufferedDrawer> bufferedDrawer = Lazies.lazy(BufferedDrawer::new);

    /**
     * Creates new map image from pixels.
     *
     * @param pixels array of Minecraft color IDs (columns of rows)
     */
    public DefaultMapImage(final byte[][] pixels) {
        checkArgument(pixels.length == WIDTH, "Pixels length should be " + WIDTH);
        for (val column : pixels) checkArgument(column.length == HEIGHT, "Pixels height should be " + HEIGHT);

        this.pixels = pixels;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    /**
     * Creates new map image from image.
     *
     * @param image from which to create the map image
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @return created map image
     */
    public static DefaultMapImage from(@NonNull final BufferedImage image, final boolean resize) {
        return new DefaultMapImage(MapImages.getMapImagePixels(image, resize));
    }

    @Override
    public MapImage.Drawer drawer() {
        return drawer.get();
    }

    @Override
    public MapImage.BufferedDrawer bufferedDrawer() {
        return bufferedDrawer.get();
    }

    @ToString
    @EqualsAndHashCode
    protected final class Drawer implements MapImage.Drawer {

        @Override
        public MapImage.Drawer px(final int x, final int y, final byte color) {
            pixels[x][y] = color;

            return this;
        }

        @Override
        public MapImage.Drawer fill(final byte color) {
            for (val column : pixels) Arrays.fill(column, color);

            return this;
        }
    }

    /**
     * Buffered drawer based on 2-dimensional {@link byte}-array of changed pixels and {@link int}-bounds.
     */
    @Getter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PROTECTED)
    protected final class BufferedDrawer implements MapImage.BufferedDrawer {

        /**
         * Array of changed pixels
         */
        final byte[][] buffer = blankPixels(new byte[DefaultMapImage.this.getWidth()][DefaultMapImage.this.getWidth()]);

        boolean unchanged = true;

        /**
         * The least X-coordinate of changed image segment.
         */
        int leastChangedX = Delta.NONE,
        /**
         * The least Y-coordinate of changed image segment.
         */
        leastChangedY = Delta.NONE,
        /**
         * The most X-coordinate of changed image segment.
         */
        mostChangedX = Delta.NONE,
        /**
         * The most Y-coordinate of changed image segment.
         */
        mostChangedY = Delta.NONE;

        /**
         * Resets this buffered drawer setting {@link #unchanged} to {@code true} and resetting its buffer.
         */
        protected void reset() {
            unchanged = true;
            leastChangedX = leastChangedY = mostChangedX = mostChangedY = Delta.NONE;

            blankPixels(buffer);
        }

        @Override
        public MapImage.Drawer dispose() {
            // disposal is not needed if there are no changes
            if (!unchanged) {
                for (var x = leastChangedX; x <= mostChangedX; x++) {
                    if (mostChangedY + 1 - leastChangedY >= 0) System.arraycopy(
                            buffer[x], leastChangedY, pixels[x], leastChangedY, mostChangedY + 1 - leastChangedY
                    );
                }

                reset();
            }

            return this;
        }

        @Override
        public Delta getDelta() {
            if (unchanged) return Delta.EMPTY;

            // length of pixels changed at y (rows affected)
            val yLength = mostChangedY - leastChangedY + 1;
            // target array to store changed pixels
            val changedPixels = new byte[mostChangedX - leastChangedX + 1][yLength];
            // for each column copy its rows from leastChangedX to mostChangedX to the next available
            // row in changedPixels copying from leastChangedY to mostChangedY (of yLength)
            // indexing happens from 0 by each axis, so i is target array index, and x is source array index (column)
            for (int x = leastChangedX, i = 0; x < mostChangedX; x++, i++) System.arraycopy(
                    pixels[x] /* source column */, leastChangedY, changedPixels[i] /* target column*/, 0, yLength
            );

            return Delta.of(changedPixels, leastChangedX, leastChangedY);
        }

        ///////////////////////////////////////////////////////////////////////////
        // Drawing
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public MapImage.Drawer px(final int x, final int y, final byte color) {
            // put the changed pixel to the buffer
            buffer[x][y] = color;

            // perform delta update if needed
            // if it is the first update then the pixels is the zone of changes
            if (unchanged) {
                unchanged = false;

                leastChangedX = mostChangedX = x;
                leastChangedY = mostChangedY = y;
            } else {
                if (x < leastChangedX) leastChangedX = x;
                else if (x > mostChangedX) mostChangedX = x;

                if (y < leastChangedY) leastChangedY = y;
                else if (y > mostChangedY) mostChangedY = y;
            }

            return this;
        }

        @Override
        public MapImage.Drawer fill(final byte color) {
            unchanged = false;

            for (val column : buffer) Arrays.fill(column, color);

            return this;
        }
    }
}

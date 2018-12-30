package ru.progrm_jarvis.minecraft.commons.mapimage;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImage.HEIGHT;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImage.WIDTH;

/**
 * Utilities related to {@link MapImage}.
 */
@UtilityClass
public class MapImages {

    /**
     * Normalizes the RGB-pixels 2-dimensional array making them valid Minecraft {@link MapImageColor}s.
     *
     * @param pixels pixels to normalizes
     * @return 2-dimensional array of valid Minecraft map color IDs.
     */
    protected static byte[][] normalizePixels(final int[][] pixels) {
        val normalizedPixels = new byte[WIDTH][HEIGHT];
        for (var x = 0; x < WIDTH; x++) {
            val column = pixels[x];
            for (var y = 0; y < HEIGHT; y++) normalizedPixels[x][y] = MapImageColor.getClosestColorCode(column[y]);
        }

        return normalizedPixels;
    }

    /**
     * Gets the 2-dimensional {@link byte}-array (the 1-st index is columns, the 2-nd index is rows)
     * of size {@link MapImage#WIDTH}×{@link MapImage#HEIGHT} of RGB-{@link int} colors.
     *
     * @param image image whose pixels to get
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @return 2-dimensional array of RGB-{@link int} colors.
     */
    public int[][] getNonNormalizedMapImagePixels(@NonNull BufferedImage image, final boolean resize) {
        final int width = image.getWidth(), height = image.getHeight();

        // if an image is of wrong size and should be resized (but not cut) then perform resizing
        if (width != WIDTH || height != HEIGHT || !resize) {
            // resizing
            // create new image of valid size
            val newImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            // draw the resized image on
            val graphics = image.getGraphics();
            graphics.drawImage(
                    image.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH), 0, 0, null
            );
            graphics.dispose();
            image = newImage;
        }

        val pixels = new int[WIDTH][HEIGHT];

        for (var x = 0; x < WIDTH; x++) for (var y = 0; y < HEIGHT; y++) pixels[x][y]
                = image.getRGB(x, y);

        return pixels;
    }

    /**
     * Gets the 2-dimensional {@link byte}-array (the 1-st index is columns, the 2-nd index is rows)
     * of size {@link MapImage#WIDTH}×{@link MapImage#HEIGHT} of valid map color ids.
     *
     * @param image image whose pixels to get
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @return 2-dimensional array of {@link MapImageColor} IDs valid for minecraft.
     */
    public byte[][] getMapImagePixels(@NonNull final BufferedImage image, final boolean resize) {
        return normalizePixels(getNonNormalizedMapImagePixels(image, resize));
    }
}

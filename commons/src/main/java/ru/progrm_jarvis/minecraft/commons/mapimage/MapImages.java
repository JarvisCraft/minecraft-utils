package ru.progrm_jarvis.minecraft.commons.mapimage;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Contract;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.min;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImage.*;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImageColor.NO_COLOR_CODE;

/**
 * Utilities related to {@link MapImage}.
 *
 * @apiNote the standard for 1-dimensional arrays of image pixels is {@code pixel(x, y) = pixels[x + y * width}
 */
@UtilityClass
public class MapImages {

    /**
     * Normalizes the RGB-pixels array making them valid Minecraft {@link MapImageColor}s.
     *
     * @param pixels pixels to normalizes
     * @return array of valid Minecraft map color IDs.
     */
    protected static byte[] normalizePixels(final int[] pixels) {
        // check before allocations
        checkArgument(pixels.length == PIXELS_COUNT, "Length of pixels should be " + PIXELS_COUNT);

        val normalizedPixels = new byte[PIXELS_COUNT];
        for (var i = 0; i < pixels.length; i++) normalizedPixels[i] = MapImageColor.getClosestColorCode(pixels[i]);

        return normalizedPixels;
    }

    /**
     * Normalizes the RGB-pixels 2-dimensional array making them valid Minecraft {@link MapImageColor}s.
     *
     * @param pixels pixels to normalizes
     * @return 2-dimensional array of valid Minecraft map color IDs.
     */
    protected static byte[][] normalizePixels(final int[][] pixels) {
        val normalizedPixels = new byte[pixels.length][];
        for (var x = 0; x < WIDTH; x++) {
            val column = pixels[x];
            val normalizedColumn = new byte[pixels[x].length];
            for (var y = 0; y < HEIGHT; y++) normalizedColumn[y] = MapImageColor.getClosestColorCode(column[y]);
            normalizedPixels[x] = normalizedColumn;
        }

        return normalizedPixels;
    }

    /**
     * Makes the image fit the bounds of map image.
     * <p>
     * The logic is the following:
     *
     * <dt>The image is {@link MapImage#WIDTH}×{@link MapImage#HEIGHT} or is smaller</dt>
     * <dd>Do nothing and return this image</dd>
     *
     * <dt>The image's width is bigger than {@link MapImage#WIDTH}
     * or its height is bigger than{@link MapImage#HEIGHT}</dt>
     * <dd>The image is resized proportionally to be of maximal possible size
     * yet fitting the bounds of {@link MapImage#WIDTH}×{@link MapImage#HEIGHT}</dd>
     *
     * @param image image to fit (will be redrawn)
     * @return the given image redrawn so that its non-empty pixels
     * are in bound of {@link MapImage#WIDTH}×{@link MapImage#HEIGHT}
     *
     * @apiNote returned object may be ignored as all changes happen to the provided image
     */
    @Contract("null -> fail; _ -> param1")
    public BufferedImage fitImage(@NonNull final BufferedImage image) {
        final int width = image.getWidth(), height = image.getHeight();

        // if an image is bigger at any bound
        if (width > WIDTH || height > HEIGHT) {
            // resizing should be proportional, so:
            // k(bound) = bound / maxAllowed(bound)
            // then divide both by bigger k (treat same optimally!)

            final float kWidth = width / WIDTH_F, kHeight = height / HEIGHT_F;
            if (kHeight == kHeight) {
                // if both overflow coefficients are the same than the image's proportions are same to image map's
                val graphics = image.getGraphics();
                graphics.drawImage(image.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
                graphics.dispose();
            } else if (kWidth > kHeight) {
                // width overflow coefficient is bigger
                val graphics = image.getGraphics();
                graphics.drawImage(
                        image.getScaledInstance(WIDTH, (int) (height / kWidth), Image.SCALE_SMOOTH), 0, 0, null
                );
                graphics.dispose();
            } else {
                // height overflow coefficient is bigger
                val graphics = image.getGraphics();
                graphics.drawImage(
                        image.getScaledInstance((int) (width / kHeight), HEIGHT, Image.SCALE_SMOOTH), 0, 0, null
                );
                graphics.dispose();
            }
        }

        return image;
    }

    /**
     * Gets the 2-dimensional {@link byte}-array (the 1-st index is columns, the 2-nd index is rows)
     * of size {@link MapImage#WIDTH}×{@link MapImage#HEIGHT} of RGB-{@link int} colors.
     *
     * @param image image whose pixels to get
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @return 2-dimensional array of RGB-{@link int} colors.
     */
    public int[][] getNonNormalizedMapImagePixels2D(@NonNull final BufferedImage image, final boolean resize) {
        if (resize) fitImage(image);
        final int width = min(image.getWidth(), WIDTH), height = min(image.getHeight(), HEIGHT);

        val pixels = new int[WIDTH][HEIGHT];
        val rgb = image.getRGB(0, 0, width, height, new int[width * height], 0, width);

        // whether or not empty pixels should be added as height is not ideal
        val requiresEmptyYFilling = width < WIDTH;

        for (var x = 0; x < width; x++) {
            val column = pixels[x];
            for (var y = 0; y < height; y++) column[y] = rgb[x + y * width];
            if (requiresEmptyYFilling) Arrays.fill(column, height, HEIGHT, NO_COLOR_CODE);
        }
        if (width < WIDTH) for (var x = width; x < WIDTH; x++) Arrays.fill(pixels[x], NO_COLOR_CODE);

        return pixels;
    }

    /**
     * Gets the {@link byte}-array of RGB-{@link int} colors.
     *
     * @param image image whose pixels to get
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @return array of RGB-{@link int} colors.
     */
    public int[] getNonNormalizedMapImagePixels(@NonNull BufferedImage image, final boolean resize) {
        if (resize) fitImage(image);
        final int width = min(image.getWidth(), WIDTH), height = min(image.getHeight(), HEIGHT);

        val pixels = new int[PIXELS_COUNT];
        val rgb = image.getRGB(0, 0, width, height, new int[width * height], 0, width);

        // whether or not empty pixels should be added as width is not ideal
        val requiresEmptyXFilling = width < WIDTH;

        // index in rgb
        var i = 0;
        for (var y = 0; y < height; y++) {
            val rowOffset = y * width;
            for (var x = 0; x < width; x++) pixels[x + rowOffset] = rgb[i++];
            if (requiresEmptyXFilling) Arrays.fill(pixels, width + rowOffset, WIDTH + rowOffset, NO_COLOR_CODE);
        }
        if (height < HEIGHT) Arrays.fill(pixels, height * WIDTH, pixels.length, NO_COLOR_CODE);

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
    public byte[][] getMapImagePixels2D(@NonNull final BufferedImage image, final boolean resize) {
        return normalizePixels(getNonNormalizedMapImagePixels2D(image, resize));
    }

    /**
     * Gets the {@link byte}-array of size {@link MapImage#PIXELS_COUNT} of valid map color ids.
     *
     * @param image image whose pixels to get
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @return array of {@link MapImageColor} IDs valid for minecraft.
     */
    public byte[] getMapImagePixels(@NonNull final BufferedImage image, final boolean resize) {
        return normalizePixels(getNonNormalizedMapImagePixels(image, resize));
    }
}

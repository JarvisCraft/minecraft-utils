package ru.progrm_jarvis.minecraft.commons.mapimage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.map.MapPalette;
import ru.progrm_jarvis.minecraft.commons.util.BitwiseUtil;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;

import java.awt.*;

import static java.lang.Math.abs;

/**
 * A cached color which provides easy conversions between full 24-bit RGB and Minecraft Map colors.
 *
 * @apiNote Minecraft maps don't allow alpha channel
 */
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MapImageColor {

    /**
     * A primitive constant value to use when there is no color code.
     */
    public static final byte NO_COLOR_CODE = 0;

    private static final Object COLOR_IDS_CACHE_MUTEX = new Object[0];

    private static final Cache<Integer, MapImageColor> COLOR_CACHE = CacheBuilder.newBuilder()
            .weakValues()
            .concurrencyLevel(SystemPropertyUtil.getSystemPropertyInt(
                    MapImageColor.class.getCanonicalName() + ".color-cache-concurrency-level", 2
            ))
            .build();

    /**
     * All associations of color's with their available IDs.
     */
    private static final TObjectByteMap<MapImageColor> COLOR_CODE_CACHE = new TObjectByteHashMap<>();

    /**
     * 8 bits describing red part of the color
     */
    byte red,
    /**
     * 8 bits describing green part of the color
     */
    green,
    /**
     * 8 bits describing blue part of the color
     */
    blue;

    @EqualsAndHashCode.Include int rgb;

    ///////////////////////////////////////////////////////////////////////////
    // Conversions
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Transforms 3 color channels
     *
     * @param red red color channel (between {@code 0} and {@code 255})
     * @param green green color channel (between {@code 0} and {@code 255})
     * @param blue blue color channel (between {@code 0} and {@code 255})
     * @return color as a single RGB {@link int}
     */
    public static int asRgb(final int red, final int green, final int blue) {
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Gets the red color channel value for the specified RGB {@link int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return red color channel value
     */
    public static byte red(final int rgb) {
        return (byte) ((rgb >> 16) & 0xFF);
    }

    /**
     * Gets the green color channel value for the specified RGB {@link int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return green color channel value
     */
    public static byte green(final int rgb) {
        return (byte) ((rgb >> 8) & 0xFF);
    }

    /**
     * Gets the blue color channel value for the specified RGB {@link int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return blue color channel value
     */
    public static byte blue(final int rgb) {
        return (byte) (rgb & 0xFF);
    }

    /**
     * Creates new map image color from specified {@link java.awt} {@link Color}.
     *
     * @param color color to convert to map image color object
     * @return map image color equivalent of specified color object
     */
    public static MapImageColor from(@NonNull final Color color) {
        return of(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Gets the id of the color closest to the one given. This value is cached for further usage.
     *
     * @param color color for which to find the closest available color code
     * @return closest available color code
     *
     * @implNote uses {@link MapPalette#matchColor(Color)} because (although it is deprecated) it is the simplest way
     */
    @SneakyThrows
    public static byte getClosestColorCode(final MapImageColor color) {
        if (COLOR_CODE_CACHE.containsKey(color)) return COLOR_CODE_CACHE.get(color);

        // the value which will store the color code
        final byte colorCode;
        synchronized (COLOR_IDS_CACHE_MUTEX) {
            //noinspection deprecation ( use of MapPalette#matchColor(..)
            COLOR_CODE_CACHE.put(color, colorCode = MapPalette.matchColor(new Color(color.rgb)));
        }

        return colorCode;
    }

    /**
     * Gets the id of the color closest to the one given by calculating
     * dissimilarity rate of each available with the one given.
     * This value is cached for further usage.
     *
     * @param rgb RGB-color {@link int} for which to find the closest available color code
     * @return closest available color id
     */
    @SneakyThrows
    public static byte getClosestColorCode(final int rgb) {
        return getClosestColorCode(of(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Construction
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a new map image color instance based on 3 base colors.
     * This is an internal constructor as, normally, there should only exist one cached instance of any used color.
     *
     * @param red red color channel
     * @param green green color channel
     * @param blue blue color channel
     */
    private MapImageColor(final byte red, final byte green, final byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

        this.rgb = asRgb(red, green, blue);
    }

    @SneakyThrows
    public static MapImageColor of(final int rgb) {
        return COLOR_CACHE.get(rgb, () -> new MapImageColor(red(rgb), green(rgb), blue(rgb)));
    }

    @SneakyThrows
    public static MapImageColor of(final byte red, final byte green, final byte blue) {
        return COLOR_CACHE.get(asRgb(red, green, blue), () -> new MapImageColor(red, green, blue));
    }

    public static MapImageColor of(final int red, final int green, final int blue) {
        return of((byte) red, (byte) green, (byte) blue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Difference counting
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Distance squared (as if colors were 3 axises)
    ///////////////////////////////////////////////////////////////////////////

    public static int getDistanceSquared(final byte red1, final byte green1, final byte blue1,
                                         final byte red2, final byte green2, final byte blue2) {
        val dRed = BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1);
        val dGreen = BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1);
        val dBlue = BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1);

        return dRed * dRed + dBlue * dBlue + dGreen * dGreen;
    }

    public static int getDistanceSquared(@NonNull final MapImageColor color1, @NonNull final MapImageColor color2) {
        return getDistanceSquared(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getDistanceSquared(final int rgb1, final int rgb2) {
        return getDistanceSquared(red(rgb1), green(rgb1), blue(rgb1), red(rgb2), green(rgb2), blue(rgb2));
    }

    public int getDistanceSquared(final byte red, final byte green, final byte blue) {
        return getDistanceSquared(this.red, this.green, this.blue, red, green, blue);
    }

    public int getDistanceSquared(@NonNull final MapImageColor other) {
        return getDistanceSquared(red, green, blue, other.red, other.green, other.blue);
    }

    public int getDistanceSquared(final int rgb) {
        return getDistanceSquared(red(rgb), green(rgb), blue(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sum of channels
    ///////////////////////////////////////////////////////////////////////////

    public static int getSum(final byte red1, final byte green1, final byte blue1,
                             final byte red2, final byte green2, final byte blue2) {
        return abs(BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1))
                + abs(BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1))
                + abs(BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1));
    }

    public static int getSum(@NonNull final MapImageColor color1, @NonNull final MapImageColor color2) {
        return getSum(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getSum(final int rgb1, final int rgb2) {
        return getSum(red(rgb1), green(rgb1), blue(rgb1), red(rgb2), green(rgb2), blue(rgb2));
    }

    public int getSum(final byte red, final byte green, final byte blue) {
        return getSum(this.red, this.green, this.blue, red, green, blue);
    }

    public int getSum(@NonNull final MapImageColor other) {
        return getSum(red, green, blue, other.red, other.green, other.blue);
    }

    public int getSum(final int rgb) {
        return getSum(red(rgb), green(rgb), blue(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Multiplication product of channels
    ///////////////////////////////////////////////////////////////////////////

    public static int getMultiplicationProduct(final byte red1, final byte green1, final byte blue1,
                                               final byte red2, final byte green2, final byte blue2) {
        return (BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1))
                * (BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1))
                * (BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1));
    }

    public static int getMultiplicationProduct(@NonNull final MapImageColor color1, @NonNull final MapImageColor color2) {
        return getMultiplicationProduct(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getMultiplicationProduct(final int rgb1, final int rgb2) {
        return getMultiplicationProduct(red(rgb1), green(rgb1), blue(rgb1), red(rgb2), green(rgb2), blue(rgb2));
    }

    public int getMultiplicationProduct(final byte red, final byte green, final byte blue) {
        return getMultiplicationProduct(this.red, this.green, this.blue, red, green, blue);
    }

    public int getMultiplicationProduct(@NonNull final MapImageColor other) {
        return getMultiplicationProduct(red, green, blue, other.red, other.green, other.blue);
    }

    public int getMultiplicationProduct(final int rgb) {
        return getMultiplicationProduct(red(rgb), green(rgb), blue(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Natural distance
    // Due to nature of human's eyes it is more accurate to use
    // unequal coefficients for color-channels to be more accurate
    //   (red) = 0.3
    // (green) = 0.59
    //  (blue) = 0.11
    ///////////////////////////////////////////////////////////////////////////

    public static int getNaturalDistanceSquared(final byte red1, final byte green1, final byte blue1,
                                                final byte red2, final byte green2, final byte blue2) {
        val dRed = (BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1)) * 0.3;
        val dGreen = (BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1)) * 0.59;
        val dBlue = (BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1)) * 0.11;

        return (int) (dRed * dRed + dBlue * dBlue + dGreen * dGreen);
    }

    public static int getNaturalDistanceSquared(@NonNull final MapImageColor color1, @NonNull final MapImageColor color2) {
        return getNaturalDistanceSquared(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getNaturalDistanceSquared(final int rgb1, final int rgb2) {
        return getNaturalDistanceSquared(red(rgb1), green(rgb1), blue(rgb1), red(rgb2), green(rgb2), blue(rgb2));
    }

    public int getNaturalDistanceSquared(final byte red, final byte green, final byte blue) {
        return getNaturalDistanceSquared(this.red, this.green, this.blue, red, green, blue);
    }

    public int getNaturalDistanceSquared(@NonNull final MapImageColor other) {
        return getNaturalDistanceSquared(red, green, blue, other.red, other.green, other.blue);
    }

    public int getNaturalDistanceSquared(final int rgb) {
        return getNaturalDistanceSquared(red(rgb), green(rgb), blue(rgb));
    }
}

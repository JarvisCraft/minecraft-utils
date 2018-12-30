package ru.progrm_jarvis.minecraft.commons.mapimage;

import gnu.trove.impl.unmodifiable.TUnmodifiableIntByteMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.hash.TIntByteHashMap;
import lombok.experimental.UtilityClass;
import lombok.val;

import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImageColor.NO_COLOR_CODE;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImageColor.asRgb;

/**
 * Utilities related to Minecraft color codes for a map.
 */
@UtilityClass
public class MapImageMinecraftColors {

    /**
     * all available colors available in Minecraft associated with their {@link byte}-codes
     * <p>
     * {@link <a href="https://minecraft.gamepedia.com/Map_item_format#1.12_Color_Table">
     * Taken from Minecraft Wiki</a>}
     */
    public final TIntByteMap MINECRAFT_RGB_COLOR_CODES;

    /**
     * all available colors available in Minecraft
     * <p>
     * {@link <a href="https://minecraft.gamepedia.com/Map_item_format#1.12_Color_Table">
     * Taken from Minecraft Wiki</a>}
     */
    private final int[] MINECRAFT_RGB_COLORS;

    static {
        //<editor-fold desc="Minecraft colors registration" defaultstate="collapsed">
        val minecraftColors = new TIntByteHashMap(NO_COLOR_CODE);
        minecraftColors.put(asRgb((byte) 89, (byte) 125, (byte) 39), (byte) 4);
        minecraftColors.put(asRgb((byte) 109, (byte) 153, (byte) 48), (byte) 5);
        minecraftColors.put(asRgb((byte) 127, (byte) 178, (byte) 56), (byte) 6);
        minecraftColors.put(asRgb((byte) 67, (byte) 94, (byte) 29), (byte) 7);
        minecraftColors.put(asRgb((byte) 174, (byte) 164, (byte) 115), (byte) 8);
        minecraftColors.put(asRgb((byte) 213, (byte) 201, (byte) 140), (byte) 9);
        minecraftColors.put(asRgb((byte) 247, (byte) 233, (byte) 163), (byte) 10);
        minecraftColors.put(asRgb((byte) 130, (byte) 123, (byte) 86), (byte) 11);
        minecraftColors.put(asRgb((byte) 140, (byte) 140, (byte) 140), (byte) 12);
        minecraftColors.put(asRgb((byte) 171, (byte) 171, (byte) 171), (byte) 13);
        minecraftColors.put(asRgb((byte) 199, (byte) 199, (byte) 199), (byte) 14);
        minecraftColors.put(asRgb((byte) 105, (byte) 105, (byte) 105), (byte) 15);
        minecraftColors.put(asRgb((byte) 180, (byte) 0, (byte) 0), (byte) 16);
        minecraftColors.put(asRgb((byte) 220, (byte) 0, (byte) 0), (byte) 17);
        minecraftColors.put(asRgb((byte) 255, (byte) 0, (byte) 0), (byte) 18);
        minecraftColors.put(asRgb((byte) 135, (byte) 0, (byte) 0), (byte) 19);
        minecraftColors.put(asRgb((byte) 112, (byte) 112, (byte) 180), (byte) 20);
        minecraftColors.put(asRgb((byte) 138, (byte) 138, (byte) 220), (byte) 21);
        minecraftColors.put(asRgb((byte) 160, (byte) 160, (byte) 255), (byte) 22);
        minecraftColors.put(asRgb((byte) 84, (byte) 84, (byte) 135), (byte) 23);
        minecraftColors.put(asRgb((byte) 117, (byte) 117, (byte) 117), (byte) 24);
        minecraftColors.put(asRgb((byte) 144, (byte) 144, (byte) 144), (byte) 25);
        minecraftColors.put(asRgb((byte) 167, (byte) 167, (byte) 167), (byte) 26);
        minecraftColors.put(asRgb((byte) 88, (byte) 88, (byte) 88), (byte) 27);
        minecraftColors.put(asRgb((byte) 0, (byte) 87, (byte) 0), (byte) 28);
        minecraftColors.put(asRgb((byte) 0, (byte) 106, (byte) 0), (byte) 29);
        minecraftColors.put(asRgb((byte) 0, (byte) 124, (byte) 0), (byte) 30);
        minecraftColors.put(asRgb((byte) 0, (byte) 65, (byte) 0), (byte) 31);
        minecraftColors.put(asRgb((byte) 180, (byte) 180, (byte) 180), (byte) 32);
        minecraftColors.put(asRgb((byte) 220, (byte) 220, (byte) 220), (byte) 33);
        minecraftColors.put(asRgb((byte) 255, (byte) 255, (byte) 255), (byte) 34);
        minecraftColors.put(asRgb((byte) 135, (byte) 135, (byte) 135), (byte) 35);
        minecraftColors.put(asRgb((byte) 115, (byte) 118, (byte) 129), (byte) 36);
        minecraftColors.put(asRgb((byte) 141, (byte) 144, (byte) 158), (byte) 37);
        minecraftColors.put(asRgb((byte) 164, (byte) 168, (byte) 184), (byte) 38);
        minecraftColors.put(asRgb((byte) 86, (byte) 88, (byte) 97), (byte) 39);
        minecraftColors.put(asRgb((byte) 106, (byte) 76, (byte) 54), (byte) 40);
        minecraftColors.put(asRgb((byte) 130, (byte) 94, (byte) 66), (byte) 41);
        minecraftColors.put(asRgb((byte) 151, (byte) 109, (byte) 77), (byte) 42);
        minecraftColors.put(asRgb((byte) 79, (byte) 57, (byte) 40), (byte) 43);
        minecraftColors.put(asRgb((byte) 79, (byte) 79, (byte) 79), (byte) 44);
        minecraftColors.put(asRgb((byte) 96, (byte) 96, (byte) 96), (byte) 45);
        minecraftColors.put(asRgb((byte) 112, (byte) 112, (byte) 112), (byte) 46);
        minecraftColors.put(asRgb((byte) 59, (byte) 59, (byte) 59), (byte) 47);
        minecraftColors.put(asRgb((byte) 45, (byte) 45, (byte) 180), (byte) 48);
        minecraftColors.put(asRgb((byte) 55, (byte) 55, (byte) 220), (byte) 49);
        minecraftColors.put(asRgb((byte) 64, (byte) 64, (byte) 255), (byte) 50);
        minecraftColors.put(asRgb((byte) 33, (byte) 33, (byte) 135), (byte) 51);
        minecraftColors.put(asRgb((byte) 100, (byte) 84, (byte) 50), (byte) 52);
        minecraftColors.put(asRgb((byte) 123, (byte) 102, (byte) 62), (byte) 53);
        minecraftColors.put(asRgb((byte) 143, (byte) 119, (byte) 72), (byte) 54);
        minecraftColors.put(asRgb((byte) 75, (byte) 63, (byte) 38), (byte) 55);
        minecraftColors.put(asRgb((byte) 180, (byte) 177, (byte) 172), (byte) 56);
        minecraftColors.put(asRgb((byte) 220, (byte) 217, (byte) 211), (byte) 57);
        minecraftColors.put(asRgb((byte) 255, (byte) 252, (byte) 245), (byte) 58);
        minecraftColors.put(asRgb((byte) 135, (byte) 133, (byte) 129), (byte) 59);
        minecraftColors.put(asRgb((byte) 152, (byte) 89, (byte) 36), (byte) 60);
        minecraftColors.put(asRgb((byte) 186, (byte) 109, (byte) 44), (byte) 61);
        minecraftColors.put(asRgb((byte) 216, (byte) 127, (byte) 51), (byte) 62);
        minecraftColors.put(asRgb((byte) 114, (byte) 67, (byte) 27), (byte) 63);
        minecraftColors.put(asRgb((byte) 125, (byte) 53, (byte) 152), (byte) 64);
        minecraftColors.put(asRgb((byte) 153, (byte) 65, (byte) 186), (byte) 65);
        minecraftColors.put(asRgb((byte) 178, (byte) 76, (byte) 216), (byte) 66);
        minecraftColors.put(asRgb((byte) 94, (byte) 40, (byte) 114), (byte) 67);
        minecraftColors.put(asRgb((byte) 72, (byte) 108, (byte) 152), (byte) 68);
        minecraftColors.put(asRgb((byte) 88, (byte) 132, (byte) 186), (byte) 69);
        minecraftColors.put(asRgb((byte) 102, (byte) 153, (byte) 216), (byte) 70);
        minecraftColors.put(asRgb((byte) 54, (byte) 81, (byte) 114), (byte) 71);
        minecraftColors.put(asRgb((byte) 161, (byte) 161, (byte) 36), (byte) 72);
        minecraftColors.put(asRgb((byte) 197, (byte) 197, (byte) 44), (byte) 73);
        minecraftColors.put(asRgb((byte) 229, (byte) 229, (byte) 51), (byte) 74);
        minecraftColors.put(asRgb((byte) 121, (byte) 121, (byte) 27), (byte) 75);
        minecraftColors.put(asRgb((byte) 89, (byte) 144, (byte) 17), (byte) 76);
        minecraftColors.put(asRgb((byte) 109, (byte) 176, (byte) 21), (byte) 77);
        minecraftColors.put(asRgb((byte) 127, (byte) 204, (byte) 25), (byte) 78);
        minecraftColors.put(asRgb((byte) 67, (byte) 108, (byte) 13), (byte) 79);
        minecraftColors.put(asRgb((byte) 170, (byte) 89, (byte) 116), (byte) 80);
        minecraftColors.put(asRgb((byte) 208, (byte) 109, (byte) 142), (byte) 81);
        minecraftColors.put(asRgb((byte) 242, (byte) 127, (byte) 165), (byte) 82);
        minecraftColors.put(asRgb((byte) 128, (byte) 67, (byte) 87), (byte) 83);
        minecraftColors.put(asRgb((byte) 53, (byte) 53, (byte) 53), (byte) 84);
        minecraftColors.put(asRgb((byte) 65, (byte) 65, (byte) 65), (byte) 85);
        minecraftColors.put(asRgb((byte) 76, (byte) 76, (byte) 76), (byte) 86);
        minecraftColors.put(asRgb((byte) 40, (byte) 40, (byte) 40), (byte) 87);
        minecraftColors.put(asRgb((byte) 108, (byte) 108, (byte) 108), (byte) 88);
        minecraftColors.put(asRgb((byte) 132, (byte) 132, (byte) 132), (byte) 89);
        minecraftColors.put(asRgb((byte) 153, (byte) 153, (byte) 153), (byte) 90);
        minecraftColors.put(asRgb((byte) 81, (byte) 81, (byte) 81), (byte) 91);
        minecraftColors.put(asRgb((byte) 53, (byte) 89, (byte) 108), (byte) 92);
        minecraftColors.put(asRgb((byte) 65, (byte) 109, (byte) 132), (byte) 93);
        minecraftColors.put(asRgb((byte) 76, (byte) 127, (byte) 153), (byte) 94);
        minecraftColors.put(asRgb((byte) 40, (byte) 67, (byte) 81), (byte) 95);
        minecraftColors.put(asRgb((byte) 89, (byte) 44, (byte) 125), (byte) 96);
        minecraftColors.put(asRgb((byte) 109, (byte) 54, (byte) 153), (byte) 97);
        minecraftColors.put(asRgb((byte) 127, (byte) 63, (byte) 178), (byte) 98);
        minecraftColors.put(asRgb((byte) 67, (byte) 33, (byte) 94), (byte) 99);
        minecraftColors.put(asRgb((byte) 36, (byte) 53, (byte) 125), (byte) 100);
        minecraftColors.put(asRgb((byte) 44, (byte) 65, (byte) 153), (byte) 101);
        minecraftColors.put(asRgb((byte) 51, (byte) 76, (byte) 178), (byte) 102);
        minecraftColors.put(asRgb((byte) 27, (byte) 40, (byte) 94), (byte) 103);
        minecraftColors.put(asRgb((byte) 72, (byte) 53, (byte) 36), (byte) 104);
        minecraftColors.put(asRgb((byte) 88, (byte) 65, (byte) 44), (byte) 105);
        minecraftColors.put(asRgb((byte) 102, (byte) 76, (byte) 51), (byte) 106);
        minecraftColors.put(asRgb((byte) 54, (byte) 40, (byte) 27), (byte) 107);
        minecraftColors.put(asRgb((byte) 72, (byte) 89, (byte) 36), (byte) 108);
        minecraftColors.put(asRgb((byte) 88, (byte) 109, (byte) 44), (byte) 109);
        minecraftColors.put(asRgb((byte) 102, (byte) 127, (byte) 51), (byte) 110);
        minecraftColors.put(asRgb((byte) 54, (byte) 67, (byte) 27), (byte) 111);
        minecraftColors.put(asRgb((byte) 108, (byte) 36, (byte) 36), (byte) 112);
        minecraftColors.put(asRgb((byte) 132, (byte) 44, (byte) 44), (byte) 113);
        minecraftColors.put(asRgb((byte) 153, (byte) 51, (byte) 51), (byte) 114);
        minecraftColors.put(asRgb((byte) 81, (byte) 27, (byte) 27), (byte) 115);
        minecraftColors.put(asRgb((byte) 17, (byte) 17, (byte) 17), (byte) 116);
        minecraftColors.put(asRgb((byte) 21, (byte) 21, (byte) 21), (byte) 117);
        minecraftColors.put(asRgb((byte) 25, (byte) 25, (byte) 25), (byte) 118);
        minecraftColors.put(asRgb((byte) 13, (byte) 13, (byte) 13), (byte) 119);
        minecraftColors.put(asRgb((byte) 176, (byte) 168, (byte) 54), (byte) 120);
        minecraftColors.put(asRgb((byte) 215, (byte) 205, (byte) 66), (byte) 121);
        minecraftColors.put(asRgb((byte) 250, (byte) 238, (byte) 77), (byte) 122);
        minecraftColors.put(asRgb((byte) 132, (byte) 126, (byte) 40), (byte) 123);
        minecraftColors.put(asRgb((byte) 64, (byte) 154, (byte) 150), (byte) 124);
        minecraftColors.put(asRgb((byte) 79, (byte) 188, (byte) 183), (byte) 125);
        minecraftColors.put(asRgb((byte) 92, (byte) 219, (byte) 213), (byte) 126);
        minecraftColors.put(asRgb((byte) 48, (byte) 115, (byte) 112), (byte) 127);
        minecraftColors.put(asRgb((byte) 52, (byte) 90, (byte) 180), (byte) 128);
        minecraftColors.put(asRgb((byte) 63, (byte) 110, (byte) 220), (byte) 129);
        minecraftColors.put(asRgb((byte) 74, (byte) 128, (byte) 255), (byte) 130);
        minecraftColors.put(asRgb((byte) 39, (byte) 67, (byte) 135), (byte) 131);
        minecraftColors.put(asRgb((byte) 0, (byte) 153, (byte) 40), (byte) 132);
        minecraftColors.put(asRgb((byte) 0, (byte) 187, (byte) 50), (byte) 133);
        minecraftColors.put(asRgb((byte) 0, (byte) 217, (byte) 58), (byte) 134);
        minecraftColors.put(asRgb((byte) 0, (byte) 114, (byte) 30), (byte) 135);
        minecraftColors.put(asRgb((byte) 91, (byte) 60, (byte) 34), (byte) 136);
        minecraftColors.put(asRgb((byte) 111, (byte) 74, (byte) 42), (byte) 137);
        minecraftColors.put(asRgb((byte) 129, (byte) 86, (byte) 49), (byte) 138);
        minecraftColors.put(asRgb((byte) 68, (byte) 45, (byte) 25), (byte) 139);
        minecraftColors.put(asRgb((byte) 79, (byte) 1, (byte) 0), (byte) 140);
        minecraftColors.put(asRgb((byte) 96, (byte) 1, (byte) 0), (byte) 141);
        minecraftColors.put(asRgb((byte) 112, (byte) 2, (byte) 0), (byte) 142);
        minecraftColors.put(asRgb((byte) 59, (byte) 1, (byte) 0), (byte) 143);
        minecraftColors.put(asRgb((byte) 147, (byte) 124, (byte) 113), (byte) 144);
        minecraftColors.put(asRgb((byte) 180, (byte) 152, (byte) 138), (byte) 145);
        minecraftColors.put(asRgb((byte) 209, (byte) 177, (byte) 161), (byte) 146);
        minecraftColors.put(asRgb((byte) 110, (byte) 93, (byte) 85), (byte) 147);
        minecraftColors.put(asRgb((byte) 112, (byte) 57, (byte) 25), (byte) 148);
        minecraftColors.put(asRgb((byte) 137, (byte) 70, (byte) 31), (byte) 149);
        minecraftColors.put(asRgb((byte) 159, (byte) 82, (byte) 36), (byte) 150);
        minecraftColors.put(asRgb((byte) 84, (byte) 43, (byte) 19), (byte) 151);
        minecraftColors.put(asRgb((byte) 105, (byte) 61, (byte) 76), (byte) 152);
        minecraftColors.put(asRgb((byte) 128, (byte) 75, (byte) 93), (byte) 153);
        minecraftColors.put(asRgb((byte) 149, (byte) 87, (byte) 108), (byte) 154);
        minecraftColors.put(asRgb((byte) 78, (byte) 46, (byte) 57), (byte) 155);
        minecraftColors.put(asRgb((byte) 79, (byte) 76, (byte) 97), (byte) 156);
        minecraftColors.put(asRgb((byte) 96, (byte) 93, (byte) 119), (byte) 157);
        minecraftColors.put(asRgb((byte) 112, (byte) 108, (byte) 138), (byte) 158);
        minecraftColors.put(asRgb((byte) 59, (byte) 57, (byte) 73), (byte) 159);
        minecraftColors.put(asRgb((byte) 131, (byte) 93, (byte) 25), (byte) 160);
        minecraftColors.put(asRgb((byte) 160, (byte) 114, (byte) 31), (byte) 161);
        minecraftColors.put(asRgb((byte) 186, (byte) 133, (byte) 36), (byte) 162);
        minecraftColors.put(asRgb((byte) 98, (byte) 70, (byte) 19), (byte) 163);
        minecraftColors.put(asRgb((byte) 72, (byte) 82, (byte) 37), (byte) 164);
        minecraftColors.put(asRgb((byte) 88, (byte) 100, (byte) 45), (byte) 165);
        minecraftColors.put(asRgb((byte) 103, (byte) 117, (byte) 53), (byte) 166);
        minecraftColors.put(asRgb((byte) 54, (byte) 61, (byte) 28), (byte) 167);
        minecraftColors.put(asRgb((byte) 112, (byte) 54, (byte) 55), (byte) 168);
        minecraftColors.put(asRgb((byte) 138, (byte) 66, (byte) 67), (byte) 169);
        minecraftColors.put(asRgb((byte) 160, (byte) 77, (byte) 78), (byte) 170);
        minecraftColors.put(asRgb((byte) 84, (byte) 40, (byte) 41), (byte) 171);
        minecraftColors.put(asRgb((byte) 40, (byte) 28, (byte) 24), (byte) 172);
        minecraftColors.put(asRgb((byte) 49, (byte) 35, (byte) 30), (byte) 173);
        minecraftColors.put(asRgb((byte) 57, (byte) 41, (byte) 35), (byte) 174);
        minecraftColors.put(asRgb((byte) 30, (byte) 21, (byte) 18), (byte) 175);
        minecraftColors.put(asRgb((byte) 95, (byte) 75, (byte) 69), (byte) 176);
        minecraftColors.put(asRgb((byte) 116, (byte) 92, (byte) 84), (byte) 177);
        minecraftColors.put(asRgb((byte) 135, (byte) 107, (byte) 98), (byte) 178);
        minecraftColors.put(asRgb((byte) 71, (byte) 56, (byte) 51), (byte) 179);
        minecraftColors.put(asRgb((byte) 61, (byte) 64, (byte) 64), (byte) 180);
        minecraftColors.put(asRgb((byte) 75, (byte) 79, (byte) 79), (byte) 181);
        minecraftColors.put(asRgb((byte) 87, (byte) 92, (byte) 92), (byte) 182);
        minecraftColors.put(asRgb((byte) 46, (byte) 48, (byte) 48), (byte) 183);
        minecraftColors.put(asRgb((byte) 86, (byte) 51, (byte) 62), (byte) 184);
        minecraftColors.put(asRgb((byte) 105, (byte) 62, (byte) 75), (byte) 185);
        minecraftColors.put(asRgb((byte) 122, (byte) 73, (byte) 88), (byte) 186);
        minecraftColors.put(asRgb((byte) 64, (byte) 38, (byte) 46), (byte) 187);
        minecraftColors.put(asRgb((byte) 53, (byte) 43, (byte) 64), (byte) 188);
        minecraftColors.put(asRgb((byte) 65, (byte) 53, (byte) 79), (byte) 189);
        minecraftColors.put(asRgb((byte) 76, (byte) 62, (byte) 92), (byte) 190);
        minecraftColors.put(asRgb((byte) 40, (byte) 32, (byte) 48), (byte) 191);
        minecraftColors.put(asRgb((byte) 53, (byte) 35, (byte) 24), (byte) 192);
        minecraftColors.put(asRgb((byte) 65, (byte) 43, (byte) 30), (byte) 193);
        minecraftColors.put(asRgb((byte) 76, (byte) 50, (byte) 35), (byte) 194);
        minecraftColors.put(asRgb((byte) 40, (byte) 26, (byte) 18), (byte) 195);
        minecraftColors.put(asRgb((byte) 53, (byte) 57, (byte) 29), (byte) 196);
        minecraftColors.put(asRgb((byte) 65, (byte) 70, (byte) 36), (byte) 197);
        minecraftColors.put(asRgb((byte) 76, (byte) 82, (byte) 42), (byte) 198);
        minecraftColors.put(asRgb((byte) 40, (byte) 43, (byte) 22), (byte) 199);
        minecraftColors.put(asRgb((byte) 100, (byte) 42, (byte) 32), (byte) 200);
        minecraftColors.put(asRgb((byte) 122, (byte) 51, (byte) 39), (byte) 201);
        minecraftColors.put(asRgb((byte) 142, (byte) 60, (byte) 46), (byte) 202);
        minecraftColors.put(asRgb((byte) 75, (byte) 31, (byte) 24), (byte) 203);
        minecraftColors.put(asRgb((byte) 26, (byte) 15, (byte) 11), (byte) 204);
        minecraftColors.put(asRgb((byte) 31, (byte) 18, (byte) 13), (byte) 205);
        minecraftColors.put(asRgb((byte) 37, (byte) 22, (byte) 16), (byte) 206);
        minecraftColors.put(asRgb((byte) 19, (byte) 11, (byte) 8), (byte) 207);

        MINECRAFT_RGB_COLOR_CODES = new TUnmodifiableIntByteMap(minecraftColors);
        MINECRAFT_RGB_COLORS = MINECRAFT_RGB_COLOR_CODES.keys();
        //</editor-fold>
    }

    /**
     * Creates a new primitive map
     * with keys being an {@link int} representation of an RGB color in Minecraft map
     * and value being its code in Minecraft.
     *
     * @return primitive map of available map color codes in minecraft by their RGB colors
     */
    public TIntByteMap asNewPrimitiveMap() {
        return new TIntByteHashMap(MINECRAFT_RGB_COLOR_CODES);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Minecraft color conversions
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks whether the specified color can be shown on a map without any distortion.
     *
     * @param rgb RGB color as {@link int}
     * @return {@code true} if this color can be shown on an in-game map without distortion anf {@code false} otherwise
     */
    public boolean isMinecraftColor(final int rgb) {
        for (val minecraftRgbColor : MINECRAFT_RGB_COLORS) if (rgb == minecraftRgbColor) return true;
        return false;
    }

    /**
     * Checks whether the specified color can be shown on a map without any distortion.
     *
     * @param red red channel of the RGB color
     * @param green green channel of the RGB color
     * @param blue blue channel of the RGB color
     * @return {@code true} if this color can be shown on an in-game map without distortion anf {@code false} otherwise
     */
    public boolean isMinecraftColor(final byte red, final byte green, final byte blue) {
        return isMinecraftColor(asRgb(red, green, blue));
    }

    /**
     * Gets Minecraft map color code for the specified color.
     *
     * @param rgb RGB color as {@link int}
     * @return non-zero value being the found minecraft code for the color or {@code 0} if none was found
     */
    public byte getMinecraftColorCode(final int rgb) {
        return MINECRAFT_RGB_COLOR_CODES.get(rgb);
    }

    /**
     * Gets Minecraft map color code for the specified color.
     *
     * @param red red channel of the RGB color
     * @param green green channel of the RGB color
     * @param blue blue channel of the RGB color
     * @return value being the found minecraft code for the color
     * or {@link MapImageColor#NO_COLOR_CODE} if none was found
     */
    public byte getMinecraftColorCode(final byte red, final byte green, final byte blue) {
        return MINECRAFT_RGB_COLOR_CODES.get(asRgb(red, green, blue));
    }
}

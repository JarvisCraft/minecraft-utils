package ru.progrm_jarvis.fakeentitylib.misc.structure;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

public class BlockItemStructureDescriptor {

    @Data
    public static final class JsonRepresentation {

        private Element[] objects;
        private Keyframe[] keyframes;

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static final class Element {

            ElementDescriptor userData;

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static final class ElementDescriptor {

                String name;
                boolean selected, isItem, enableRotCenter, size, isDynamic, visible, justAdded, highlight;
                @SerializedName("customnbt") boolean customNbt;
                @SerializedName("rawid") String rawId;
                @SerializedName("blockid") String blockId;
                @SerializedName("customname") String customName;
                Translation translation;

                @Data
                @FieldDefaults(level = AccessLevel.PRIVATE)
                private static final class Translation {
                    double x, y, z;
                }
            }
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static final class Keyframe {

            @SerializedName("ontick") int tick;
            Element[] objects;

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static final class Element {

                UUID uuid;
                double size;
                @SerializedName("customname") String customName;
                Position position;
                Rotation rotation;
                boolean visible;

                @Data
                @FieldDefaults(level = AccessLevel.PRIVATE)
                private static final class Position {
                    double x, y, z;
                }

                @Data
                @FieldDefaults(level = AccessLevel.PRIVATE)
                private static final class Rotation {
                    double x, y, z;
                }
            }
        }
    }
}

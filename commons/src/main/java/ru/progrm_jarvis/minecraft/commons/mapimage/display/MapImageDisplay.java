package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import ru.progrm_jarvis.minecraft.commons.mapimage.MapImage;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

/**
 * Display of {@link MapImage}.
 * It is responsible for displaying actual images to the players and resolving map-ID conflicts.
 */
public interface MapImageDisplay extends PlayerContainer {

    MapImage image();
}

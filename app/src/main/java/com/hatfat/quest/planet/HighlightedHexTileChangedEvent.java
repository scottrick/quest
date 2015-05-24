package com.hatfat.quest.planet;

public class HighlightedHexTileChangedEvent {

    private final HexTile tile;

    public HighlightedHexTileChangedEvent(final HexTile tile) {
        this.tile = tile;
    }

    public HexTile getTile() {
        return tile;
    }
}

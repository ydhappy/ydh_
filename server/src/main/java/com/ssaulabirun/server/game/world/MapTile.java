package com.ssaulabirun.server.game.world;

public class MapTile {
    public enum TileType {
        GRASS, WATER, MOUNTAIN, FOREST, TOWN, DUNGEON
    }

    private final TileType tileType;
    private final boolean passable;
    private final int elevation;

    public MapTile(TileType tileType, boolean passable, int elevation) {
        this.tileType = tileType;
        this.passable = passable;
        this.elevation = elevation;
    }

    public TileType getTileType() { return tileType; }
    public boolean isPassable() { return passable; }
    public int getElevation() { return elevation; }
}

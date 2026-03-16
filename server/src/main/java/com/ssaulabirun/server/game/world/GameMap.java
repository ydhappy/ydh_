package com.ssaulabirun.server.game.world;

import com.ssaulabirun.server.game.character.L1Character;
import com.ssaulabirun.server.game.character.L1NpcInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameMap {
    private final int mapId;
    private final String name;
    private final int width;
    private final int height;
    private final MapTile[][] tiles;
    private final ConcurrentHashMap<Long, L1Character> characters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, L1NpcInstance> npcs = new ConcurrentHashMap<>();

    public GameMap(int mapId, String name, int width, int height) {
        this.mapId = mapId;
        this.name = name;
        this.width = width;
        this.height = height;
        this.tiles = new MapTile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = new MapTile(MapTile.TileType.GRASS, true, 0);
            }
        }
    }

    public void setTile(int x, int y, MapTile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
    }

    public MapTile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null;
    }

    public boolean canMove(int x, int y) {
        MapTile tile = getTile(x, y);
        return tile != null && tile.isPassable();
    }

    public void addChar(L1Character character) {
        characters.put(character.getId(), character);
    }

    public void removeChar(long charId) {
        characters.remove(charId);
    }

    public void addNpc(L1NpcInstance npc) {
        npcs.put(npc.getId(), npc);
    }

    public void removeNpc(long npcId) {
        npcs.remove(npcId);
    }

    public List<L1Character> getCharsInRange(int x, int y, int range) {
        List<L1Character> result = new ArrayList<>();
        for (L1Character ch : characters.values()) {
            int dx = ch.getX() - x;
            int dy = ch.getY() - y;
            if (dx * dx + dy * dy <= range * range) {
                result.add(ch);
            }
        }
        return result;
    }

    public int getMapId() { return mapId; }
    public String getName() { return name; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public ConcurrentHashMap<Long, L1Character> getCharacters() { return characters; }
    public ConcurrentHashMap<Long, L1NpcInstance> getNpcs() { return npcs; }
}

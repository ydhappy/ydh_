package com.ssaulabirun.server.game.world;

import com.ssaulabirun.server.game.character.L1Character;
import com.ssaulabirun.server.game.character.L1PcInstance;
import com.ssaulabirun.server.network.ClientSession;
import com.ssaulabirun.server.network.packet.PacketWriter;
import com.ssaulabirun.server.network.packet.PacketType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class L1World {
    private static L1World instance;

    private final ConcurrentHashMap<String, ClientSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, L1PcInstance> onlinePlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, GameMap> maps = new ConcurrentHashMap<>();

    private L1World() {}

    public static synchronized L1World getInstance() {
        if (instance == null) {
            instance = new L1World();
        }
        return instance;
    }

    public void addSession(String accountName, ClientSession session) {
        sessions.put(accountName, session);
    }

    public void removeSession(String accountName) {
        sessions.remove(accountName);
    }

    public ClientSession getSession(String accountName) {
        return sessions.get(accountName);
    }

    public void addPlayer(L1PcInstance player) {
        onlinePlayers.put(player.getId(), player);
        GameMap map = maps.get(player.getMapId());
        if (map != null) {
            map.addChar(player);
        }
    }

    public void removePlayer(long charId) {
        L1PcInstance player = onlinePlayers.remove(charId);
        if (player != null) {
            GameMap map = maps.get(player.getMapId());
            if (map != null) {
                map.removeChar(charId);
            }
        }
    }

    public L1PcInstance getPlayer(long charId) {
        return onlinePlayers.get(charId);
    }

    public GameMap getMap(int mapId) {
        return maps.get(mapId);
    }

    public void addMap(GameMap map) {
        maps.put(map.getMapId(), map);
    }

    public void broadcastMessage(String message) {
        PacketWriter pw = new PacketWriter(PacketType.SERVER_MESSAGE.getOpcode());
        pw.writeString(message);
        for (ClientSession session : sessions.values()) {
            session.sendPacket(pw);
        }
    }

    public List<L1Character> findNearbyChars(int mapId, int x, int y, int range) {
        GameMap map = maps.get(mapId);
        if (map == null) return new ArrayList<>();
        return map.getCharsInRange(x, y, range);
    }

    public ConcurrentHashMap<String, ClientSession> getSessions() { return sessions; }
    public ConcurrentHashMap<Long, L1PcInstance> getOnlinePlayers() { return onlinePlayers; }
    public ConcurrentHashMap<Integer, GameMap> getMaps() { return maps; }
}

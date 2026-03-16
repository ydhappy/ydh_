package com.ssaulabirun.server.game.npc;

import com.ssaulabirun.server.game.character.L1NpcInstance;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NpcManager {
    private static NpcManager instance;
    private final Map<Integer, NpcTemplate> templates = new ConcurrentHashMap<>();

    private NpcManager() {}

    public static synchronized NpcManager getInstance() {
        if (instance == null) {
            instance = new NpcManager();
        }
        return instance;
    }

    public void addTemplate(NpcTemplate template) {
        templates.put(template.getId(), template);
    }

    public NpcTemplate getTemplate(int id) {
        return templates.get(id);
    }

    public Collection<NpcTemplate> getAllTemplates() {
        return templates.values();
    }

    public L1NpcInstance spawnNpc(int templateId, int mapId, int x, int y) {
        NpcTemplate template = templates.get(templateId);
        if (template == null) return null;
        return new L1NpcInstance(template, mapId, x, y);
    }
}

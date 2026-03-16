package com.ssaulabirun.server.game.item;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemManager {
    private static ItemManager instance;
    private final Map<Integer, L1Item> items = new ConcurrentHashMap<>();

    private ItemManager() {}

    public static synchronized ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    public void loadItem(L1Item item) {
        items.put(item.getItemId(), item);
    }

    public L1Item getItem(int id) {
        return items.get(id);
    }

    public Collection<L1Item> getAllItems() {
        return items.values();
    }
}

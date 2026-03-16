package com.ssaulabirun.server.game.character;

import com.ssaulabirun.server.game.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class L1NpcInstance extends L1Character {
    private static final AtomicLong ID_GEN = new AtomicLong(100000L);

    private int npcId;
    private NpcTemplate template;
    private long respawnTime;
    private boolean isDead;

    public L1NpcInstance(NpcTemplate template, int mapId, int x, int y) {
        this.id = ID_GEN.getAndIncrement();
        this.npcId = template.getId();
        this.template = template;
        this.name = template.getName();
        this.level = template.getLevel();
        this.maxHp = template.getHp();
        this.hp = maxHp;
        this.maxMp = template.getMp();
        this.mp = maxMp;
        this.str = template.getStr();
        this.dex = template.getDex();
        this.con = template.getCon();
        this.intel = template.getIntel();
        this.wis = template.getWis();
        this.mapId = mapId;
        this.x = x;
        this.y = y;
        this.isDead = false;
    }

    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);
        if (hp <= 0) {
            die();
        }
    }

    public void die() {
        isDead = true;
        hp = 0;
        respawnTime = System.currentTimeMillis() + 30_000L;
    }

    public void respawn() {
        isDead = false;
        hp = maxHp;
        mp = maxMp;
    }

    public List<Integer> getDrops() {
        List<Integer> result = new ArrayList<>();
        if (template.getDrops() != null) {
            for (int dropId : template.getDrops()) {
                result.add(dropId);
            }
        }
        return result;
    }

    public int getNpcId() { return npcId; }
    public NpcTemplate getTemplate() { return template; }
    public long getRespawnTime() { return respawnTime; }
    public boolean isDead() { return isDead; }
}

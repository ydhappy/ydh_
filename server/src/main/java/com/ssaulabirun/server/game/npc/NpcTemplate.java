package com.ssaulabirun.server.game.npc;

public class NpcTemplate {
    private int id;
    private String name;
    private int level;
    private int hp;
    private int mp;
    private int str;
    private int dex;
    private int con;
    private int intel;
    private int wis;
    private int attackRange;
    private String element;
    private int[] drops;
    private int expReward;
    private int adenaReward;

    public NpcTemplate(int id, String name, int level, int hp, int mp,
                       int str, int dex, int con, int intel, int wis,
                       int attackRange, String element, int[] drops,
                       int expReward, int adenaReward) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.mp = mp;
        this.str = str;
        this.dex = dex;
        this.con = con;
        this.intel = intel;
        this.wis = wis;
        this.attackRange = attackRange;
        this.element = element;
        this.drops = drops;
        this.expReward = expReward;
        this.adenaReward = adenaReward;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMp() { return mp; }
    public int getStr() { return str; }
    public int getDex() { return dex; }
    public int getCon() { return con; }
    public int getIntel() { return intel; }
    public int getWis() { return wis; }
    public int getAttackRange() { return attackRange; }
    public String getElement() { return element; }
    public int[] getDrops() { return drops; }
    public int getExpReward() { return expReward; }
    public int getAdenaReward() { return adenaReward; }
}

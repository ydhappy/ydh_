package com.ssaulabirun.server.game.character;

public abstract class L1Character {
    protected long id;
    protected String name;
    protected int mapId;
    protected int x;
    protected int y;
    protected int hp;
    protected int maxHp;
    protected int mp;
    protected int maxMp;
    protected int str;
    protected int dex;
    protected int con;
    protected int intel;
    protected int wis;
    protected int cha;
    protected int level;
    protected int ac;
    protected int heading;

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int amount) {
        if (amount < 0) amount = 0;
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        if (amount < 0) amount = 0;
        hp = Math.min(maxHp, hp + amount);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMapId() { return mapId; }
    public void setMapId(int mapId) { this.mapId = mapId; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getMp() { return mp; }
    public void setMp(int mp) { this.mp = mp; }
    public int getMaxMp() { return maxMp; }
    public void setMaxMp(int maxMp) { this.maxMp = maxMp; }
    public int getStr() { return str; }
    public void setStr(int str) { this.str = str; }
    public int getDex() { return dex; }
    public void setDex(int dex) { this.dex = dex; }
    public int getCon() { return con; }
    public void setCon(int con) { this.con = con; }
    public int getIntel() { return intel; }
    public void setIntel(int intel) { this.intel = intel; }
    public int getWis() { return wis; }
    public void setWis(int wis) { this.wis = wis; }
    public int getCha() { return cha; }
    public void setCha(int cha) { this.cha = cha; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getAc() { return ac; }
    public void setAc(int ac) { this.ac = ac; }
    public int getHeading() { return heading; }
    public void setHeading(int heading) { this.heading = heading; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", name='" + name + "', level=" + level
                + ", hp=" + hp + "/" + maxHp + ", pos=(" + x + "," + y + ") map=" + mapId + "}";
    }
}

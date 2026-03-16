package com.ssaulabirun.server.game.character;

public enum CharacterClass {
    NONE(0, "없음", "None", 10, 10, 10, 10, 10, 10),
    MAGE(1, "마법사", "Mage", 8, 10, 8, 16, 14, 10),
    WARRIOR(2, "전사", "Warrior", 16, 12, 14, 8, 8, 10),
    PALADIN(3, "성기사", "Paladin", 14, 10, 14, 10, 12, 14),
    RANGER(4, "궁수", "Ranger", 10, 16, 10, 12, 12, 10),
    THIEF(5, "도적", "Thief", 10, 16, 10, 12, 10, 12),
    PRIEST(6, "사제", "Priest", 8, 10, 10, 12, 16, 12),
    SHAMAN(7, "주술사", "Shaman", 10, 12, 10, 14, 14, 10),
    NECROMANCER(8, "강령술사", "Necromancer", 8, 10, 8, 16, 12, 10),
    BERSERKER(9, "광전사", "Berserker", 18, 12, 14, 6, 6, 10),
    MONK(10, "수도사", "Monk", 14, 14, 12, 10, 12, 8),
    ASSASSIN(11, "암살자", "Assassin", 12, 18, 10, 10, 8, 12),
    BARD(12, "음유시인", "Bard", 8, 12, 8, 12, 12, 18),
    DRUID(13, "드루이드", "Druid", 10, 10, 12, 12, 16, 10),
    KNIGHT(14, "기사", "Knight", 14, 10, 16, 8, 10, 14),
    ARCHMAGE(15, "대마법사", "Archmage", 6, 10, 6, 18, 16, 10),
    WARLORD(16, "전쟁군주", "Warlord", 16, 12, 16, 10, 8, 12),
    SSAULABIRUN(17, "싸울아비", "Ssaulabirun", 18, 16, 16, 14, 12, 12);

    private final int id;
    private final String koreanName;
    private final String englishName;
    private final int baseStr;
    private final int baseDex;
    private final int baseCon;
    private final int baseInt;
    private final int baseWis;
    private final int baseCha;

    CharacterClass(int id, String koreanName, String englishName,
                   int baseStr, int baseDex, int baseCon,
                   int baseInt, int baseWis, int baseCha) {
        this.id = id;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.baseStr = baseStr;
        this.baseDex = baseDex;
        this.baseCon = baseCon;
        this.baseInt = baseInt;
        this.baseWis = baseWis;
        this.baseCha = baseCha;
    }

    public int getId() { return id; }
    public String getKoreanName() { return koreanName; }
    public String getEnglishName() { return englishName; }
    public int getBaseStr() { return baseStr; }
    public int getBaseDex() { return baseDex; }
    public int getBaseCon() { return baseCon; }
    public int getBaseInt() { return baseInt; }
    public int getBaseWis() { return baseWis; }
    public int getBaseCha() { return baseCha; }

    public static CharacterClass getById(int id) {
        for (CharacterClass cls : values()) {
            if (cls.id == id) return cls;
        }
        return NONE;
    }
}

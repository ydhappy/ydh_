package com.ssaulabirun.server.game.character;

public class CharacterStats {
    public static final long[] EXP_TABLE = new long[101];

    static {
        EXP_TABLE[0] = 0;
        EXP_TABLE[1] = 0;
        for (int i = 2; i <= 100; i++) {
            EXP_TABLE[i] = (long) (100 * Math.pow(i, 2.5));
        }
    }

    public static long getExpForLevel(int level) {
        if (level < 1) return 0;
        if (level > 100) return EXP_TABLE[100];
        return EXP_TABLE[level];
    }

    public static int getStatGainOnLevelUp(CharacterClass cls, String stat) {
        return switch (stat) {
            case "str" -> isStrClass(cls) ? 2 : 1;
            case "dex" -> isDexClass(cls) ? 2 : 1;
            case "con" -> isConClass(cls) ? 2 : 1;
            case "int" -> isIntClass(cls) ? 2 : 1;
            case "wis" -> isWisClass(cls) ? 2 : 1;
            default -> 1;
        };
    }

    private static boolean isStrClass(CharacterClass cls) {
        return cls == CharacterClass.WARRIOR || cls == CharacterClass.BERSERKER
                || cls == CharacterClass.KNIGHT || cls == CharacterClass.WARLORD
                || cls == CharacterClass.SSAULABIRUN;
    }

    private static boolean isDexClass(CharacterClass cls) {
        return cls == CharacterClass.THIEF || cls == CharacterClass.RANGER
                || cls == CharacterClass.ASSASSIN || cls == CharacterClass.SSAULABIRUN;
    }

    private static boolean isConClass(CharacterClass cls) {
        return cls == CharacterClass.PALADIN || cls == CharacterClass.MONK
                || cls == CharacterClass.KNIGHT || cls == CharacterClass.SSAULABIRUN;
    }

    private static boolean isIntClass(CharacterClass cls) {
        return cls == CharacterClass.MAGE || cls == CharacterClass.NECROMANCER
                || cls == CharacterClass.ARCHMAGE;
    }

    private static boolean isWisClass(CharacterClass cls) {
        return cls == CharacterClass.PRIEST || cls == CharacterClass.DRUID
                || cls == CharacterClass.SHAMAN;
    }

    public static int getBaseHpGain(int conModifier) {
        return 10 + conModifier;
    }

    public static int getBaseMpGain(int intModifier) {
        return 5 + intModifier;
    }

    public static int getModifier(int stat) {
        return (stat - 10) / 2;
    }
}

package com.ssaulabirun.server.game.combat;

import com.ssaulabirun.server.game.character.L1Character;
import com.ssaulabirun.server.game.character.L1PcInstance;

import java.util.Random;

public class CombatEngine {
    private static final Random random = new Random();

    public static boolean calculateHit(L1Character attacker, L1Character defender) {
        int attackRoll = random.nextInt(20) + 1 + attacker.getStr() / 2;
        int defenseValue = 10 + defender.getAc() + defender.getDex() / 4;
        return attackRoll >= defenseValue;
    }

    public static int calculateDamage(L1Character attacker, L1Character defender) {
        int strMod = (attacker.getStr() - 10) / 2;
        int baseDamage = random.nextInt(10) + 1 + strMod;
        int armorReduction = Math.max(0, defender.getCon() / 5);
        int damage = Math.max(1, baseDamage - armorReduction);
        return damage;
    }

    public static int calculateMagicDamage(L1PcInstance caster, L1Character target, int basePower) {
        int intMod = (caster.getIntel() - 10) / 2;
        int wisMod = (caster.getWis() - 10) / 2;
        int damage = basePower + random.nextInt(basePower / 2 + 1) + intMod + wisMod;
        int resist = Math.max(0, target.getWis() / 5);
        damage = Math.max(1, damage - resist);
        return damage;
    }
}

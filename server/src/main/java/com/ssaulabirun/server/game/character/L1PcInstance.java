package com.ssaulabirun.server.game.character;

import com.ssaulabirun.server.network.packet.PacketWriter;
import com.ssaulabirun.server.network.packet.PacketType;

public class L1PcInstance extends L1Character {
    private String accountName;
    private CharacterClass charClass;
    private long exp;
    private int sp;
    private long adena;
    private int magicCircle;

    public L1PcInstance() {
        this.charClass = CharacterClass.NONE;
    }

    public void gainExp(long amount) {
        if (amount <= 0) return;
        exp += amount;
        long needed = CharacterStats.getExpForLevel(level + 1);
        while (level < 99 && exp >= needed) {
            levelUp();
            needed = CharacterStats.getExpForLevel(level + 1);
        }
    }

    public void levelUp() {
        level++;
        int conMod = CharacterStats.getModifier(con);
        int intMod = CharacterStats.getModifier(intel);
        int hpGain = CharacterStats.getBaseHpGain(conMod);
        int mpGain = CharacterStats.getBaseMpGain(intMod);
        maxHp += hpGain;
        maxMp += mpGain;
        hp = maxHp;
        mp = maxMp;
        str += CharacterStats.getStatGainOnLevelUp(charClass, "str");
        dex += CharacterStats.getStatGainOnLevelUp(charClass, "dex");
        con += CharacterStats.getStatGainOnLevelUp(charClass, "con");
        intel += CharacterStats.getStatGainOnLevelUp(charClass, "int");
        wis += CharacterStats.getStatGainOnLevelUp(charClass, "wis");
        sp++;
    }

    // TODO: Implement skill usage system
    public void useSkill(int skillId) {
        throw new UnsupportedOperationException("Skill system not yet implemented");
    }

    public byte[] toPacketData() {
        PacketWriter pw = new PacketWriter(PacketType.CHARACTER_INFO.getOpcode());
        pw.writeLong(id);
        pw.writeString(name);
        pw.writeInt(charClass.getId());
        pw.writeInt(level);
        pw.writeLong(exp);
        pw.writeInt(hp);
        pw.writeInt(maxHp);
        pw.writeInt(mp);
        pw.writeInt(maxMp);
        pw.writeInt(str);
        pw.writeInt(dex);
        pw.writeInt(con);
        pw.writeInt(intel);
        pw.writeInt(wis);
        pw.writeInt(cha);
        pw.writeInt(mapId);
        pw.writeInt(x);
        pw.writeInt(y);
        pw.writeLong(adena);
        pw.writeInt(magicCircle);
        return pw.toByteArray();
    }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public CharacterClass getCharClass() { return charClass; }
    public void setCharClass(CharacterClass charClass) { this.charClass = charClass; }
    public long getExp() { return exp; }
    public void setExp(long exp) { this.exp = exp; }
    public int getSp() { return sp; }
    public void setSp(int sp) { this.sp = sp; }
    public long getAdena() { return adena; }
    public void setAdena(long adena) { this.adena = adena; }
    public int getMagicCircle() { return magicCircle; }
    public void setMagicCircle(int magicCircle) { this.magicCircle = magicCircle; }
}

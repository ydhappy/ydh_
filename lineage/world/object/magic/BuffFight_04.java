package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class BuffFight_04 extends Magic {

	public BuffFight_04(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new BuffFight_04(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() + 3);
			target.setDynamicAddDmgBow(target.getDynamicAddDmgBow() + 3);
			target.setDynamicSp(target.getDynamicSp() + 3);
			target.setDynamicHp(target.getDynamicHp() + 200);

			target.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), target));
			target.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), target));
			target.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_12336, getTime()));
			toBuffUpdate(o);
		}
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicAddDmg(target.getDynamicAddDmg() - 3);
			target.setDynamicAddDmgBow(target.getDynamicAddDmgBow() - 3);
			target.setDynamicSp(target.getDynamicSp() - 3);
			target.setDynamicHp(target.getDynamicHp() - 200);

			target.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), target));
			target.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), target));
			target.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_12336, 0));
			ChattingController.toChatting(o, "\\fY변신 지배 반지 효과 종료", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "\\fY변신 지배 반지 효과: " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, BuffFight_04.clone(BuffController.getPool(BuffFight_04.class), SkillDatabase.find(602), time));
	}

	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		// 버프 등록
		BuffController.append(o, BuffFight_04.clone(BuffController.getPool(BuffFight_04.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(o, "변신 지배 반지 효과: 추가 타격+3, SP+3, HP+200", Lineage.CHATTING_MODE_MESSAGE);
	}

}

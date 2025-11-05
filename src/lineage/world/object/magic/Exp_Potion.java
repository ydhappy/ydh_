package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffBlue;
import lineage.network.packet.server.S_BuffEva;
import lineage.network.packet.server.S_BuffShield;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Exp_Potion extends Magic {

	public Exp_Potion(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Character cha, Skill skill, int time) {
		if (bi == null)
			bi = new Exp_Potion(skill);

		bi.setCharacter(cha);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
			o.setBuffExpPotion(true);
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_9904, getTime()));

	}

	@Override
	public void toBuffUpdate(object o) {
		o.setBuffExpPotion(true);
	}

	@Override
	public void toBuffStop(object o) {
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.setBuffExpPotion(false);
		ChattingController.toChatting(o, "드래곤의 다이아몬드 효과 종료", Lineage.CHATTING_MODE_MESSAGE);
		o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_9904, 0));
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "드래곤의 다이아몬드 효과 " + getTime() + "초 후 종료", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(Character cha, int time) {
		BuffController.append(cha, Exp_Potion.clone(BuffController.getPool(Exp_Potion.class), cha, SkillDatabase.find(211), time));
	}

	
	static public void onBuff(Character cha, Skill skill, int time, boolean restart) {
		// 버프 시간 중첩
		if (!restart)
			time = BuffController.addBuffTime(cha, skill, time);

		if (skill.getCastGfx() > 0)
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);

		BuffController.append(cha, Exp_Potion.clone(BuffController.getPool(Exp_Potion.class), cha, skill, time));

		ChattingController.toChatting(cha, "드래곤의 다이아몬드 효과: 추가 경험치 30%", Lineage.CHATTING_MODE_MESSAGE);
	}

}

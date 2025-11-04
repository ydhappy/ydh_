package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class BuffFight_03 extends Magic {

	public BuffFight_03(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new BuffFight_03(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setDynamicSp( target.getDynamicSp() + 3);
			target.setDynamicMagicHit( target.getDynamicMagicHit() + 5);
			target.setDynamicReduction(target.getDynamicReduction() + 3);
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_2430, getTime()));

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
			target.setDynamicSp( target.getDynamicSp() - 3);
			target.setDynamicMagicHit( target.getDynamicMagicHit() - 5);
			target.setDynamicReduction(target.getDynamicReduction() - 3);
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_2430, 0));

			ChattingController.toChatting(o, "당신의 기분이 원래대로 돌아왔습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
		if (getTime() == Lineage.buff_magic_time_max || getTime() == Lineage.buff_magic_time_min)
			ChattingController.toChatting(o, "현자의 전투 강화마법이 종료까지 " + getTime() + "초 남았습니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

	static public void init(object o, int time) {
		BuffController.append(o, BuffFight_03.clone(BuffController.getPool(BuffFight_03.class), SkillDatabase.find(403), time));
	}

	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
		// 버프 등록
		BuffController.append(o, BuffFight_03.clone(BuffController.getPool(BuffFight_03.class), skill, skill.getBuffDuration()));
		ChattingController.toChatting(o, "SP+3, 마법 적중+5, PvP 대미지 감소+3", Lineage.CHATTING_MODE_MESSAGE);
	}

}

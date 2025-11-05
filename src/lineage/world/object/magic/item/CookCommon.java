package lineage.world.object.magic.item;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.Magic;

public class CookCommon extends Magic {

	public CookCommon(Skill skill) {
		super(null, skill);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time) {
		if (bi == null)
			bi = new CookCommon(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		if (o instanceof Character) {
			Character target = (Character) o;
			target.setMagicdollTimeHpTic( target.getMagicdollTimeHpTic() + 10);
			target.setMagicdollTimeMpTic( target.getMagicdollTimeMpTic() + 10);
			
			ChattingController.toChatting(o, "상아탑의 묘약 요리 효과 : HP틱+10, MP틱+10", Lineage.CHATTING_MODE_MESSAGE);
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
			target.setMagicdollTimeHpTic( target.getMagicdollTimeHpTic() - 10);
			target.setMagicdollTimeMpTic( target.getMagicdollTimeMpTic() - 10);

			ChattingController.toChatting(o, "당신의 배고품이 원래대로 돌아왔습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	@Override
	public void toBuff(object o) {
	}

	static public void init(object o, int time) {
		BuffController.append(o, CookCommon.clone(BuffController.getPool(CookCommon.class), SkillDatabase.find(705), time));
	}

	static public void onBuff(object o, Skill skill) {
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 6392));
		// 버프 등록
		BuffController.append(o, CookCommon.clone(BuffController.getPool(CookCommon.class), skill, skill.getBuffDuration()));
	}
}
package lineage.world.object.magic.monster;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoisonLock;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Magic;

public class CurseGhast extends Magic {
	
	public CurseGhast(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new CurseGhast(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffCurseGhast(true);
		o.setBuffSilence(true);
		o.setPoison(true);
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 310));
	}
	
	@Override
	public void toBuff(object o){
		if(!o.isDead() && !o.isLockHigh())
			o.setNowHp(o.getNowHp() - Util.random((int) Math.round(getDamage() * 0.08), (int) Math.round(getDamage() * 0.12)));
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBuffCurseGhast(false);
		o.setBuffSilence(false);
		o.setPoison(false);
		o.toSender(S_ObjectPoisonLock.clone(BasePacketPooling.getPool(S_ObjectPoisonLock.class), o), true);
		ChattingController.toChatting(o, "혀의 감각이 돌아왔습니다. 다시 말을 할 수 있게 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);

	}
	
	/**
	 * 월드 접속할때 해당 객체 버프 적용하기 위한것
	 * @param cha
	 * @param time
	 */
	static public void init(Character cha, int time){
		BuffController.append(cha, CurseGhast.clone(BuffController.getPool(CurseGhast.class), SkillDatabase.find(304), time));
	}

	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		if(check && !SkillController.isMagic(mi, ms, true) && !SkillController.isFigure(mi, o, ms, true, false))
			return;
		if(SkillController.isFigure(mi, o, ms.getSkill(), true, false) && !o.isLock()){
			if(effect > 0)
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
			BuffController.append(o, CurseGhast.clone(BuffController.getPool(CurseGhast.class), ms.getSkill(), ms.getBuffDuration() > 0 ? ms.getBuffDuration() : ms.getSkill().getBuffDuration() + 10));
		}
	}
	
}

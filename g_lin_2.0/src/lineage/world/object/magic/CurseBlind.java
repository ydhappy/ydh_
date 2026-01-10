package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Ability;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectBlind;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class CurseBlind extends Magic {

    // true: 기존 슬로우 효과(아이콘 BUFFID_480), false: 인탱글 효과(아이콘 BUFFID_6176)
    private boolean curse;
    
	public CurseBlind(Skill skill){
		super(null, skill);
	}
	
    public void setCurse(boolean curse) {
        this.curse = curse;
    }
    
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, boolean curse){
		if(bi == null)
			bi = new CurseBlind(skill);
		bi.setSkill(skill);
		bi.setTime(time);
        ((CurseBlind)bi).setCurse(curse);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o) {
		// 공격당한거 알리기.
		o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
		o.setBuffCurseBlind(true);
		if (curse) {
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_5318, getTime()));
		} else {
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_751, getTime()));
		}
		if (o.isMapHack())
			o.toSender(new S_Ability(3, false));
		//
		toBuffUpdate(o);
	}

	@Override
	public void toBuffUpdate(object o){
		o.toSender(S_ObjectBlind.clone(BasePacketPooling.getPool(S_ObjectBlind.class), o));
		if (curse) {
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_5318, getTime()));
		} else {
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_751, getTime()));
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffCurseBlind(false);
		o.toSender(S_ObjectBlind.clone(BasePacketPooling.getPool(S_ObjectBlind.class), o));
		if (curse) {
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_5318, 0));
		} else {
			o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), S_Ext_BuffTime.BUFFID_751, 0));
		}
		
		if (o.isMapHack())
			o.toSender(new S_Ability(3, true));
	}
	
	static public void init(Character cha, Skill skill, int object_id, boolean curse){
		object o = cha.findInsideList(object_id);
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true))
				onBuff(cha, o, skill, curse);
		}
	}
	
	/**
	 * 몬스터 용
	 * @param mi
	 * @param o
	 * @param ms
	 * @param action
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean curse){
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		if(o.isBuffCurseBlind()){
			return;
		}
		if(SkillController.isMagic(mi, ms, true) && SkillController.isFigure(mi, o, ms.getSkill(), false, false)){
			if(effect > 0)
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect), true);
			BuffController.append(o, CurseBlind.clone(BuffController.getPool(CurseBlind.class), ms.getSkill(), ms.getBuffDuration(), true));
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, CurseBlind.clone(BuffController.getPool(CurseBlind.class), SkillDatabase.find(3, 3), time, true));
	}
	
	/**
	 * 중복코드 방지용.
	 * @param cha
	 * @param o
	 * @param skill
	 */
	static public void onBuff(Character cha, object o, Skill skill, boolean curse) {
		// 투망상태 해제
		Detection.onBuff(cha);
		// 처리
		if (SkillController.isFigure(cha, o, skill, true, false)) {
			o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
			if (curse) {
				BuffController.append(o, CurseBlind.clone(BuffController.getPool(CurseBlind.class), skill, skill.getBuffDuration(), true));
			} else {
				BuffController.append(o, CurseBlind.clone(BuffController.getPool(CurseBlind.class), skill, skill.getBuffDuration(), false));
			}
			// \f1마법이 실패했습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
		}
	}
}

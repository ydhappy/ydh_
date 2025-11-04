package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class Slow extends Magic {

    // true: 기존 슬로우 효과(아이콘 BUFFID_480), false: 인탱글 효과(아이콘 BUFFID_6176)
    private boolean slow;

    public Slow(Skill skill) {
        super(null, skill);
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    /**
     * slow 파라미터에 따라 슬로우/인탱글 모드를 설정하여 클론 생성.
     */
    static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, boolean slow) {
        if(bi == null)
            bi = new Slow(skill);
        bi.setSkill(skill);
        bi.setTime(time);
        ((Slow)bi).setSlow(slow);
        return bi;
    }

    @Override
    public void toBuffStart(object o) {
        o.setSpeed(2);
        toBuffUpdate(o);
        // 공격받은 효과 처리
        o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
        // slow 값에 따라 아이콘 분기: true이면 기존 아이콘, false이면 인탱글 아이콘
        if(slow) {
 //           o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), 
 //                   S_Ext_BuffTime.BUFFID_480, getTime()));
        } else {
            o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), 
                    S_Ext_BuffTime.BUFFID_6176, getTime()));
        }
    }

    @Override
    public void toBuffUpdate(object o) {
        o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), getTime()), true);
    }

    @Override
    public void toBuffStop(object o) {
        toBuffEnd(o);
    }

    @Override
    public void toBuffEnd(object o) {
        if(o.isWorldDelete())
            return;
        o.setSpeed(0);
        o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 0, o.getSpeed(), 0), true);
        // 종료 시에도 slow 값에 따라 아이콘 해제 처리
        if(slow) {
 //           o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), 
 //                   S_Ext_BuffTime.BUFFID_480, 0));
        } else {
            o.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), 
                    S_Ext_BuffTime.BUFFID_6176, 0));
        }
    }

    static public void init(Character cha, Skill skill, int object_id, boolean slow) {
        object o = null;
        if(object_id == cha.getObjectId())
            o = cha;
        else
            o = cha.findInsideList(object_id);
        if(o != null) {
            cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
            if(Util.isDistance(cha, o, 10) && SkillController.isMagic(cha, skill, true)) {
                if(SkillController.isFigure(cha, o, skill, true, false))
                    onBuff(o, skill, slow);
                // 투망상태 해제
                Detection.onBuff(cha);
            }
        }
    }

    /**
     * 몬스터용 초기화
     */
    static public void init(Character cha, object o, MonsterSkill ms, int action, int effect, boolean slow) {
        if(o != null && SkillController.isMagic(cha, ms, true)) {
            cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, action), true);
            onBuff(o, ms.getSkill(), slow);
        }
    }

    static public void init(Character cha, int time) {
        // 기본은 slow 모드로 처리 (필요시 호출하는 쪽에서 별도 처리)
        BuffController.append(cha, Slow.clone(BuffController.getPool(Slow.class), SkillDatabase.find(4, 4), time, true));
    }

    /**
     * onBuff() : 대상 o에게 효과를 적용.
     * slow 파라미터가 true이면 기존 슬로우 효과로 처리하고, false이면 인탱글 효과로 처리하며 아이콘도 각각 분기하여 표시합니다.
     */
    static public void onBuff(object o, Skill skill, boolean slow) {
        ItemInstance item1 = o.getInventory() != null ? o.getInventory().getSlot(Lineage.SLOT_WEAPON) : null;
        ItemInstance item2 = o.getInventory() != null ? o.getInventory().getSlot(Lineage.SLOT_SHIELD) : null;
        if ((item1 != null && item1.getItem().getNameIdNumber() == 418) || 
            (item2 != null && item2.getItem().getNameIdNumber() == 419)) {
            // 무시..
        } else {
            o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
            
            if(slow) {
                // 슬로우(기존) 효과 적용 시 필요한 버프 해제 처리
                if(o.getSpeed() == 1 && o.isBrave()) {
                    BuffController.remove(o, Bravery.class);
                    BuffController.remove(o, HolyWalk.class);
                    BuffController.remove(o, Wafer.class);
                } else if(o.getSpeed() == 1 && !o.isBrave()) {
                    BuffController.remove(o, Haste.class);
                    BuffController.remove(o, HastePotionMagic.class);
                } else if(o.getSpeed() == 0 && o.isBrave()) {
                    BuffController.remove(o, Bravery.class);
                    BuffController.remove(o, HolyWalk.class);
                    BuffController.remove(o, Wafer.class);
                    BuffController.remove(o, movingacceleratic.class);
                }
                BuffController.append(o, Slow.clone(BuffController.getPool(Slow.class), skill, skill.getBuffDuration(), true));
            } else {
                // 인탱글 효과 처리: Haste 관련 버프 해제 후 인탱글 아이콘 적용
                BuffController.remove(o, Haste.class);
                BuffController.remove(o, HastePotionMagic.class);
                BuffController.append(o, Slow.clone(BuffController.getPool(Slow.class), skill, skill.getBuffDuration(), false));
            }
        }
    }
}

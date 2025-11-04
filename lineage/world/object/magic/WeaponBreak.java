package lineage.world.object.magic;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.RobotController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;

public class WeaponBreak {

	static public void init(Character cha, Skill skill, int object_id) {
		object o = cha.findInsideList(object_id);
		if (o != null) {
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);

			if (SkillController.isMagic(cha, skill, true)) {
				if (o.isBuffSoulOfFlame() == false && SkillController.isFigure(cha, o, skill, true, false)) {
					onBuff(cha, o, skill);

					// 로봇 멘트 출력
					if ((cha instanceof PcInstance || cha instanceof PcRobotInstance) && o instanceof PcRobotInstance) {
					    if (Util.random(1, 100) <= Lineage.robot_ment_probability) {
						RobotController.getRandomMentAndChat(Lineage.AI_BREAK_MENT, o, cha, Lineage.CHATTING_MODE_NORMAL, Lineage.AI_BREAK_MENT_DELAY);
					    }
					}
				}
				// 투망상태 해제
				Detection.onBuff(cha);
			}
		}
	}
	
	static public void onBuff(Character cha, object o, Skill skill) {
		// 처리
		if(o.getInventory() != null){
			ItemInstance item = o.getInventory().getSlot(Lineage.SLOT_WEAPON);
			if(item != null){
				item.setDurability(item.getDurability() + Util.random(1, 5));
				o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
				if(Lineage.server_version<=144){
					o.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
					o.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
				}else{
					o.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
				}
				// \f1당신의 %0%s 손상되었습니다.
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 268, item.toString()));
				// 공격당한거 알리기.
				o.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
				return;
			}
		}
		// \f1마법이 실패했습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
	}
	
	/**
	 * 몬스터 전용
	 * 2019-11-25
	 * by connector12@nate.com
	 */
	static public void init(Character cha, MonsterSkill ms, object o, int action) {
		if (o != null) {
			if (action > 0)
				cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, action), true);

			if (SkillController.isMagic(cha, ms, true)) {
				if (o.isBuffSoulOfFlame() == false)
					onBuff(cha, o, ms.getSkill());
				// 투망상태 해제
				Detection.onBuff(cha);
			}
		}
	}
}

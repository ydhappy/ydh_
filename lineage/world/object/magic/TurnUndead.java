package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectEffectLocation;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.DamageController;
import lineage.world.controller.QuestController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class TurnUndead {

	static public void init(Character cha, Skill skill, int object_id, int x, int y){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		if(o!=null){
			// 모션취하기.
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true) && onBuff(cha, o, skill, x, y))
				return;
		}
		// \f1마법이 무효화되었습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 281));
	}
	
	/**
	 * 중복코드 방지용.
	 * @param cha
	 * @param o
	 * @param skill
	 * @param x
	 * @param y
	 * @return
	 */
	static public boolean onBuff(Character cha, object o, Skill skill, int x, int y) {
		if (o instanceof MonsterInstance && Util.isAreaAttack(cha, o) && Util.isAreaAttack(o, cha)) {
			MonsterInstance mon = (MonsterInstance) o;
		
			if (mon.getMonster().isUndead() && mon.getMonster().isTurnUndead() && SkillController.isFigure(cha, mon, skill, true, false)) {
				System.println("✅ 조건 통과: 언데드 + TurnUndead + SkillController 성공");

				String monName = mon.getMonster().getName();
				System.println("▶ 몬스터 이름: " + monName);

				if (monName.equalsIgnoreCase("좀비(퀘)")) {
					int chance = Util.random(1, 100);
					System.println("▶ 좀비(퀘) 드롭 시도 - 랜덤값: " + chance);
					if (chance < 90) {
						System.println("🎉 좀비 열쇠 드롭!");
						dropMultipleItemsOnGround(356, 5, mon);
					} else {
						System.println("❌ 좀비 열쇠 드롭 실패");
					}
				} else if (monName.equalsIgnoreCase("해골(퀘)")) {
					int chance = Util.random(1, 100);
					System.println("▶ 해골(퀘) 드롭 시도 - 랜덤값: " + chance);
					if (chance < 90) {
						System.println("🎉 해골 열쇠 드롭!");
						dropMultipleItemsOnGround(357, 5, mon);
					} else {
						System.println("❌ 해골 열쇠 드롭 실패");
					}
				} else if (monName.equalsIgnoreCase("언데드의 배신자")) {
					int chance = Util.random(1, 100);
					System.println("▶ 언데드의 배신자 드롭 시도 - 랜덤값: " + chance);
					if (chance < 90) {
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find_ItemCode(358));
						ii.setCount(1);
						ii.setObjectId(ServerDatabase.nextItemObjId());
						cha.getInventory().append(ii, true); 
						cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, mon.getName(), ii.getName()));
						System.println("🎉 언데드의 뼈 인벤토리 지급 완료!");
					} else {
						System.println("❌ 언데드의 뼈 드롭 실패");
					}
				} else {
					System.println("⚠️ 조건에 맞는 몬스터 이름이 아님");
				}			
			
				// 데미지 처리.
				DamageController.toDamage(cha, mon, mon.getTotalHp(), Lineage.ATTACK_TYPE_MAGIC);
				// 패킷 처리.
				if (Lineage.server_version > 144)
					mon.toSender(S_ObjectEffectLocation.clone(BasePacketPooling.getPool(S_ObjectEffectLocation.class), skill.getCastGfx(), x, y), false);
				else
					mon.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon, skill.getCastGfx()), false);
			} else {
				// 인식 처리.
				mon.toDamage(cha, 0, Lineage.ATTACK_TYPE_MAGIC);
				// 투망상태 해제
				Detection.onBuff(cha);
				// \f1마법이 무효화되었습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 281));
				// 턴 언데드 실패시 확률적으로 버서커스 상태
				if (mon.getMonster().isUndead() && mon.getMonster().isTurnUndead() && Util.random(0, 99) < Util.random(1, 100))
					BuffController.append(mon, Berserks.clone(BuffController.getPool(Berserks.class), SkillDatabase.find(23), -1));

			}
			return true;
		}
		return false;
	}
	
	private static void dropMultipleItemsOnGround(int itemCode, int count, MonsterInstance mon) {
	    for (int i = 0; i < count; i++) {
	        ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find_ItemCode(itemCode));
	        ii.setCount(1);
	        dropItemOnGround(ii, mon);
	    }
	}

	private static void dropItemOnGround(ItemInstance ii, MonsterInstance mon) {
	    if (ii.getObjectId() == 0) {
	        ii.setObjectId(ServerDatabase.nextItemObjId());
	    }

	    int x = Util.random(mon.getX() - 1, mon.getX() + 1);
	    int y = Util.random(mon.getY() - 1, mon.getY() + 1);

	    if (World.isThroughObject(x, y + 1, mon.getMap(), 0)) {
	        ii.toTeleport(x, y, mon.getMap(), false);
	    } else {
	        ii.toTeleport(mon.getX(), mon.getY(), mon.getMap(), false);
	    }
	    ii.toDrop(mon);
	}
}
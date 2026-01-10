package lineage.world.object.item.all_night;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.BuffFight;
import lineage.world.object.magic.BuffFight_01;
import lineage.world.object.magic.BuffFight_02;
import lineage.world.object.magic.BuffFight_03;

public class FightPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new FightPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		
		if(BuffController.find(cha).find(BuffFight.class) != null) {
			ChattingController.toChatting(cha, "전투 강화의 주문서가 현재 적용중입니다", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		Skill s = SkillDatabase.find(601);
		if(s != null)
			//제거
			BuffController.remove(cha, BuffFight_01.class);
			//제거
			BuffController.remove(cha, BuffFight_02.class);
			//제거
			BuffController.remove(cha, BuffFight_03.class);
			//적용
			BuffFight.onBuff(cha, SkillDatabase.find(601));
			
			
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount()-1, true);
		}
	}

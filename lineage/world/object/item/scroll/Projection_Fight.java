package lineage.world.object.item.scroll;

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

public class Projection_Fight extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Projection_Fight();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		
		if(BuffController.find(cha).find(BuffFight_01.class) != null) {
			ChattingController.toChatting(cha, "투사의 전투 강화 주문서가 현재 적용중입니다", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		Skill s = SkillDatabase.find(401);
		if(s != null)
			//제거
			BuffController.remove(cha, BuffFight.class);
			//제거
			BuffController.remove(cha, BuffFight_02.class);
			//제거
			BuffController.remove(cha, BuffFight_03.class);
			//적용
			BuffFight_01.onBuff(cha, SkillDatabase.find(401)); 
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount()-1, true);
	}
}


package lineage.world.object.item.bundle;

import all_night.Lineage_Balance;
import lineage.database.ItemDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Beginnersupplies extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Beginnersupplies();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isClick(cha))
			return;
		
		// 공통 추가 부분.
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 확인 주문서"), (long) Lineage_Balance.ivorytower_check, false);
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 변신 주문서"), (long) Lineage_Balance.ivorytower_disguise, false);
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 귀환 주문서"), (long) Lineage_Balance.ivorytower_homing, false);
		CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 순간이동 주문서"), (long) Lineage_Balance.ivorytower_movement, false);
		
		// 클레스별 장비 추가 부분.
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				//소모품
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 체력 회복제"), (long) Lineage_Balance.ivorytower_health, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 속도향상 물약"), (long) Lineage_Balance.ivorytower_speed, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 악마의 피"), (long) Lineage_Balance.ivorytower_devilPotion, false);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				//소모품
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 체력 회복제"), (long) Lineage_Balance.ivorytower_health, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 속도향상 물약"), (long) Lineage_Balance.ivorytower_speed, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 용기의 물약"), (long) Lineage_Balance.ivorytower_BraveryPotion, false);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				//소모품
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 체력 회복제"), (long) Lineage_Balance.ivorytower_health, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 속도향상 물약"), (long) Lineage_Balance.ivorytower_speed, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 엘븐 와퍼"), (long) Lineage_Balance.ivorytower_ElvenWafer, false);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				//소모품
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 체력 회복제"), (long) Lineage_Balance.ivorytower_health, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 속도향상 물약"), (long) Lineage_Balance.ivorytower_speed, false);
				CraftController.toCraft(this, cha, ItemDatabase.find("상아탑의 지혜의 물약"), (long) Lineage_Balance.ivorytower_WisdomPotion	, false);
				break;
		}	
		ChattingController.toChatting(cha, "보급품이 지급되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount() - 1, true);
	}

}

	


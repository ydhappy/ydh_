package lineage.world.object.item.bundle;

import lineage.database.ItemDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Small_Pocket extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Small_Pocket();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isClick(cha))
			return;
		
		switch(item.getNameIdNumber()){
		case 19570:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[1]"), 1, false);
			break;
		case 19571:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[2]"), 1, false);
			break;
		case 19572:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[3]"), 1, false);
			break;
		case 19573:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[4]"), 1, false);
			break;
		case 19574:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[5]"), 1, false);
			break;
		case 19575:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[6]"), 1, false);
			break;
		case 19576:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[7]"), 1, false);
			break;
		case 19577:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[8]"), 1, false);
			break;
		case 19578:
			CraftController.toCraft(this, cha, ItemDatabase.find("작은 보물지도[9]"), 1, false);
			break;
	    }
		ChattingController.toChatting(cha, "작은 보물지도를 찾았습니다.", Lineage.CHATTING_MODE_MESSAGE);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount() - 1, true);
	}

}
	


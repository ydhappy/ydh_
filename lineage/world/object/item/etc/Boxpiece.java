package lineage.world.object.item.etc;

import lineage.database.ItemDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Boxpiece extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Boxpiece();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		ItemInstance item = cha.getInventory().value(cbp.readD());
		switch(getItem().getNameIdNumber()) {
			case 5717:
				if(item.getItem().getNameIdNumber() != 5716) {
					super.toClick(cha, cbp);
					return;
				}
				// 잠긴 하급 오시리스의 보물상자
				CraftController.tuCraft1(this, cha, ItemDatabase.find("잠긴 하급 오시리스의 보물상자"), 1, true);
				break;
			case 5956:
				if(item.getItem().getNameIdNumber() != 5955) {
					super.toClick(cha, cbp);
					return;
				}
				// 잠긴 상급 오시리스의 보물상자
				CraftController.tuCraft1(this, cha, ItemDatabase.find("잠긴 상급 오시리스의 보물상자"), 1, true);
				break;
				// 쿠쿨칸
			case 6421:
				if(item.getItem().getNameIdNumber() != 6420) {
					super.toClick(cha, cbp);
					return;
				}
				// 잠긴 하급 쿠쿨칸의 보물상자
				CraftController.tuCraft1(this, cha, ItemDatabase.find("잠긴 하급 쿠쿨칸의 보물상자"), 1, true);
				break;
			case 6425:
				if(item.getItem().getNameIdNumber() != 6424) {
					super.toClick(cha, cbp);
					return;
				}
				// 잠긴 상급 쿠쿨칸의 보물상자
				CraftController.tuCraft1(this, cha, ItemDatabase.find("잠긴 상급 쿠쿨칸의 보물상자"), 1, true);
				break;
		}
		cha.getInventory().count(item, item.getCount()-1, true);
		cha.getInventory().count(this, getCount()-1, true);
	}
}

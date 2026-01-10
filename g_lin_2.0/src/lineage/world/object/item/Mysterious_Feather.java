package lineage.world.object.item;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Mysterious_Feather extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Mysterious_Feather();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!checkMap(cha)) {
			ChattingController.toChatting(cha, "해당 맵에서 '신비한 날개깃털'을 사용 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if (LocationController.isTeleportVerrYedHoraeZone(cha, true))
			cha.toTeleport(32776, 32866, 623, true);
		    cha.getInventory().count(this, getCount()-1, true);
	}

	// 맵 제한걸기 (해당 설정한 맵에서는 사용이 불가능)
	private boolean checkMap(Character cha) {
		switch (cha.getMap()) {
		case 70: // 잊혀진섬
		case 621: // 낚시터
		case 5124: // 낚시터
		case 101: // 오만의탑 1
		case 102: // 오만의탑 2
		case 103: // 오만의탑 3
		case 104: // 오만의탑 4
		case 105: // 오만의탑 5
			return false;
		}

		return true;
	}
}

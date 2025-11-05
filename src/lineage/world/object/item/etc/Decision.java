package lineage.world.object.item.etc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Decision extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Decision();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		//
		if (cha.getMap() == 440 && cha.getX() >= 32666 && cha.getX() <= 32673 && cha.getY() >= 32978 && cha.getY() <= 32984) {
			cha.toPotal(32927, 32800, 430);
		} else {
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
	}
}

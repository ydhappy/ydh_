package lineage.world.object.item;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Solvent extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Solvent();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isClick(cha))
			return;

		//
		ItemInstance target = cha.getInventory().value(cbp.readD());
		if (target == null)
			return;

		// 인첸트된 아이템은 용해제 할 수 없음.
		if (target.getEnLevel() > 0) {
			ChattingController.toChatting(cha, "인첸트된 아이템은 용해제할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		// 착용중인 아이템은 할 수 없음.
		if (target.isEquipped()) {
			ChattingController.toChatting(cha, "착용중인 아이템은 용해제할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		//
		int solvent_cnt = target.getItem().getSolvent();
		if (solvent_cnt <= 0)
			solvent_cnt = target.getItem().getShopPrice() == 0 ? 0 : target.getItem().getShopPrice() / 5;
		if (solvent_cnt <= 0) {
			// 용해할 수 없습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 1161));
			return;
		}

		//
		if (Util.random(0, 100) < 10)
			// \f1%0%s 증발되어 사라집니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 158, target.getName()));
		else
			// 결정체
			CraftController.toCraft(cha, ItemDatabase.find(5240), solvent_cnt, true);

		// 아이템 수량 갱신
		cha.getInventory().count(target, target.getCount() - 1, true);
		cha.getInventory().count(this, getCount() - 1, true);
	}

}

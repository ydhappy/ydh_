package lineage.world.object.npc;

import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Tikal extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		Inventory inv = pc.getInventory();
		boolean hasItem = false;

		for (ItemInstance item : inv.getListColl()) {
			if (item.getItem().getNameIdNumber() == 6435) {
				hasItem = true;
				break;
			}
		}

		if (hasItem) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tikalgate1"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tikalgate3"));
		}
	}


	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("e")) {
			for (ItemInstance item : pc.getInventory().getList()) {
				if (item.getItem().getNameIdNumber() == 6435) {
					pc.getInventory().remove(item, true);
					pc.toTeleport(32730, 32862, 784, true);
					break;
				}
			}
			if (pc.getMap() != 784)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tikalgate3"));
		}
	}
}
package lineage.world.object.item;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ExchangeController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Exchangeitem extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Exchangeitem();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    if (cha.getInventory() != null) {
	        PcInstance pc = (PcInstance) cha;
	        pc.setItemClick(true); 
	        ExchangeController.ExchangeNpc.toTalk(pc, cbp);
	    }
	}
}
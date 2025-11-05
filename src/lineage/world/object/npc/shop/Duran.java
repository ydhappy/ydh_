package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Duran extends ShopInstance {
	
	public Duran(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "duran"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (pc.getInventory().isAden(20000, true)) {
	        if(action.equalsIgnoreCase("duranT")) {
	            pc.toTeleport(32299, 33068, 440, true);
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, null));
	        }
	    } else {
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "duran9"));
	    }
	}
}

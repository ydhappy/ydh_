package lineage.world.object.npc.craft;

import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class icqwand extends CraftInstance {

	public icqwand(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icqwand1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//
		if (action.equalsIgnoreCase("a")) {
			// 화염의 막대를 요구한다
			ItemInstance item = pc.getInventory().findDbNameId(12940);
			if(item==null || item.getCount()<120) {
				CraftController.toCraft(this, pc, ItemDatabase.find(12940), item==null ? 120 : 120-item.getCount(), true, 0, 0, 1, true);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icqwand2"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icqwand4"));
			}
			
		}else if (action.equalsIgnoreCase("b")) {
			// 신비한 회복 물약을 요구한다.
			ItemInstance item = pc.getInventory().findDbNameId(12942);
			if(item==null || item.getCount()<100) {
				CraftController.toCraft(this, pc, ItemDatabase.find(12942), item==null ? 100 : 100-item.getCount(), true, 0, 0, 1, true);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icqwand3"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icqwand4"));
			}
			
		}
	}
	
}

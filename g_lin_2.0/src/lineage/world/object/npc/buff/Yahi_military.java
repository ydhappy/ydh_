package lineage.world.object.npc.buff;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.world.controller.BuffController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Balrog_buff;
import lineage.world.object.magic.Yahi_buff;

public class Yahi_military extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "u_hunt"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("a")){
			if(BuffController.find(pc).find(Balrog_buff.class) != null) {
				// 아무일도 일어나지 않았습니다.
				pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79) );
			} else {
				//
				Yahi_buff.onBuff(pc, 1020);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "u_hunt1"));
			}
		}
	}

}

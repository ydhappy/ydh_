package lineage.world.object.npc.quest;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Minitos extends object {
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (getMap() == 777) {
			// 그신
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "minitos10"));
		} else {
			// 욕망
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "minicod10"));
		}
	}
}
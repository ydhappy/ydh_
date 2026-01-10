package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Talkinggate extends TeleportInstance {

	public Talkinggate(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talkinggate1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("a")) {
			if (pc.getInventory().isAden(5000, true)) {
				if (pc.getLevel() >= 30) {
					pc.toPotal(32668, 32803, 814);
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talkinggate2"));
				}
			} else {
				// \f1아데나가 충분치 않습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
			}
		}
	}
}
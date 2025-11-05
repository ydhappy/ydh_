package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Icecastle extends TeleportInstance {

	public Icecastle(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (getNpc().getNameIdNumber()) {
		case 129055502: // 적대적인
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icq_1"));
			break;
		case 129065502: // 우호적인
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "icq_2"));
			break;
		}
	}

   @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        if ("enter1".equalsIgnoreCase(action)) {
            pc.toTeleport(32728, 32819, 2101, true);
        } else if ("enter2".equalsIgnoreCase(action)) {
            pc.toTeleport(32728, 32819, 2151, true);
        }
    }
} 
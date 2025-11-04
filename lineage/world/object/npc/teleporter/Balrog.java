package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Balrog extends TeleportInstance {

	public Balrog(Npc npc) {
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (pc.getKarma() < 100000) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uturn2"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uturn1"));
		}
	}
	
 @Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    if (pc.getMap() == 601 || pc.getKarma() < 100000) {
	    if (action.equalsIgnoreCase("4")) {
				pc.toTeleport(32747, 32813, 410, true);
            }
		}
    }
}
	  
   

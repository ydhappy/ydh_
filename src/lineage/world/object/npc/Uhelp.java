package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Uhelp extends TeleportInstance {

	public Uhelp(Npc npc) {
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (pc.getKarma() > -100000) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uhelp1"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uhelp2"));
	   }
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    // 맵이 522번인지 또는 카르마가 -100000보다 큰지 확인
	    if (pc.getMap() == 522 || pc.getKarma() > -100000) {
	        // action이 "4"인지 확인
	        if (action.equalsIgnoreCase("4")) {
	                pc.toTeleport(32903, 32801, 410, true);
	        }
	    }
	}
}
	  
   

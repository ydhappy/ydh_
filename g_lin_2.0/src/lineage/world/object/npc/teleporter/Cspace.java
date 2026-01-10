package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Cspace extends TeleportInstance {

	public Cspace(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if(getMap() == 601) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, getGfx()==5460 ? "gpass01" : "wpass01"));

		} else {
			if(pc.getLevel() <= 44)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cpass03"));
			else if(pc.getLevel() <= 59)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cpass02"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cpass01"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if(getMap() == 601) {
			if(action.equalsIgnoreCase("a")) {
				switch(getGfx()) {
					case 5456:	// 화염의 방 605
						pc.toPotal(32836, 32823, 605);
						break;
					case 5457:	// 지진의 방 607
						pc.toPotal(32774, 32834, 607);
						break;
					case 5458:	// 폭풍의 방 606
						//pc.toPotal(32756, 32843, 606);
						break;
					case 5459:	// 파도의 방 604
						pc.toPotal(32830, 32830, 604);
						break;
					case 5460:	// 발록의 아지트
						pc.toPotal(32670, 32832, 603);
				}
			}
		} else {
			if(action.equalsIgnoreCase("a"))
			    pc.toTeleport(32758, 32794, 600, true);
			else if(action.equalsIgnoreCase("b"))
				pc.toTeleport(32834, 32796, 778, true);
		}
	}

}

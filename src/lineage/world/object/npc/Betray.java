package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Betray extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		int type = getMap()==601 ? 0 : 1;
		if(pc.getLevel()<45 || pc.getKarmaLevel()<8)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "betray" +type+ "2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "betray" +type+ "1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if(pc.getLevel() < 45)
			return;
		if(pc.getKarmaLevel() < 8)
			return;
		if(action.equalsIgnoreCase("1")) {
			// 반대 우호도로 50%만큼만.
			double karma = pc.getKarma();
			karma = (~(long)karma) + 1;
			pc.setKarma(karma);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		}
	}
}

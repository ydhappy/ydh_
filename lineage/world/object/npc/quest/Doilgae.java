package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Doilgae extends QuestInstance {

	public Doilgae(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ratsyu2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ratsyu1"));
	}
}
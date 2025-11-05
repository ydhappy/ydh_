package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.util.Util;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class PremiumShop extends ShopInstance {

	public PremiumShop(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		// 방향 설정
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);

		super.toTalk(pc, "buy", null, null);
	}

}

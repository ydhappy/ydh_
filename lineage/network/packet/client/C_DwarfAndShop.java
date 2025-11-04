package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.ExchangeController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.자동판매;

public class C_DwarfAndShop extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_DwarfAndShop(data, length);
		else
			((C_DwarfAndShop) bp).clone(data, length);
		return bp;
	}

	public C_DwarfAndShop(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
	    // 버그 방지용
	    if (pc == null || pc.isWorldDelete() || !isRead(4) || pc.getInventory() == null)
	        return this;

	    int objId = readD();
	    object o = pc.findInsideList(objId);
	    if (pc.isAutoSellAdding) {
	        자동판매.addShopItem(pc, this); 
	        return this; 
	    }

	    // objId가 ExchangeNpc일 때의 처리
	    if (objId == ExchangeController.ExchangeNpc.getObjectId()) {
	        ExchangeController.appendHtml(pc, this);
	        return this;
	    }

	    // 기존 로직 처리
	    if (o == null) {
	        if (objId == pc.getObjectId()) {
	            pc.toDwarfAndShop(pc, this);
	            return this;
	        }
	    }

	    if (o != null && (pc.getGm() > 0 || !pc.isTransparent())) {
	        o.toDwarfAndShop(pc, this);
	    } else {
	        if (o == null && pc.getTempShop() != null) {
	            pc.getTempShop().toDwarfAndShop(pc, this);
	        }
	        if (o == null && pc.getTempGmShop() != null && pc.getGm() > 0) {
	            pc.getTempGmShop().toDwarfAndShop(pc, this);
	        }
	    }
	    return this;
	}
}

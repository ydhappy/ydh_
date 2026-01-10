package lineage.network.packet.server;

import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class S_PcTradeShopSellAdd extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, List<ItemInstance> list) {
		if (bp == null)
			bp = new S_PcTradeShopSellAdd(o, list);
		else
			((S_PcTradeShopSellAdd) bp).toClone(o, list);
		return bp;
	}

	public S_PcTradeShopSellAdd(object o, List<ItemInstance> list) {
		toClone(o, list);
	}

	public void toClone(object o, List<ItemInstance> list) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOPSELL);
		writeD(o.getObjectId());
		writeH(list.size()); // 인벤토리에서 팔수잇는 아템 갯수

		for (ItemInstance item : list) {
			writeD(item.getObjectId());
			writeD(0);
		}
	}
}

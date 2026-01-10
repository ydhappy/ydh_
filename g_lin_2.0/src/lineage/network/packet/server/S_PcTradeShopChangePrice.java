package lineage.network.packet.server;

import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.PcTradeShop;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.controller.ExchangeController;
import lineage.world.object.object;

public class S_PcTradeShopChangePrice extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, List<PcTradeShop> list) {
		if (bp == null)
			bp = new S_PcTradeShopChangePrice(o, list);
		else
			((S_PcTradeShopChangePrice) bp).toClone(o, list);
		return bp;
	}

	public S_PcTradeShopChangePrice(object o, List<PcTradeShop> list) {
		toClone(o, list);
	}

	public void toClone(object o, List<PcTradeShop> list) {
		clear();

		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(o.getObjectId());
		writeH(list.size());
		writeC(0);
		show(list);
	}

	private void show(List<PcTradeShop> list) {
		int idx = 0;
		
		for (PcTradeShop pts : list) {
			try {
				if (pts != null) {
					Item item = ItemDatabase.find(pts.getItemName());

					if (item != null) {
						writeD(idx++); // 번호
						writeC(0); // 타입
						writeH(item.getInvGfx()); // GFX 아이디
						writeC(pts.getBless()); // 1: 보통 0: 축 2: 저주
						writeD(2000000000); // 현재아템 총수량
						writeC(1); // 1: 확인 0: 미확인
						writeS(ExchangeController.getItemString(pts, 0, true)); // 아이템 이름
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
package lineage.network.packet.server;

import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.lineage.PcTradeShopAdd;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_PcTradeShopSellAdd2 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, List<PcTradeShopAdd> list) {
		if (bp == null)
			bp = new S_PcTradeShopSellAdd2(o, list);
		else
			((S_PcTradeShopSellAdd2) bp).toClone(o, list);
		return bp;
	}

	public S_PcTradeShopSellAdd2(object o, List<PcTradeShopAdd> list) {
		toClone(o, list);
	}

	public void toClone(object o, List<PcTradeShopAdd> list) {
		clear();

		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(o.getObjectId());
		writeH(list.size());
		writeC(0);
		show(list);
	}

	private void show(List<PcTradeShopAdd> list) {
		int idx = 0;
		
		for (PcTradeShopAdd pta : list) {
			try {
				if (pta != null && pta.getItem() != null) {
					Item item = pta.getItem().getItem();

					if (item != null) {
						writeD(idx++); // 번호
						writeC(0); // 타입
						writeH(item.getInvGfx()); // GFX 아이디
						writeC(pta.getItem().getBless()); // 1: 보통 0: 축 2: 저주
						writeD(2000000000); // 현재아템 총수량
						writeC(1); // 1: 확인 0: 미확인
						writeS(pta.getItem().toStringSearch2(pta.getCount())); // 아이템 이름
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

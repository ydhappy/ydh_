package lineage.network.packet.server;

import java.util.List;

import lineage.bean.database.AutoHuntSell;
import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.PcInstance;

public class S_AutoHuntSellDelete extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, PcInstance pc, List<AutoHuntSell> list) {
		if (bp == null)
			bp = new S_AutoHuntSellDelete(pc, list);
		else
			((S_AutoHuntSellDelete) bp).toClone(pc, list);
		return bp;
	}

	public S_AutoHuntSellDelete(PcInstance pc, List<AutoHuntSell> list) {
		toClone(pc, list);
	}

	public void toClone(PcInstance pc, List<AutoHuntSell> list) {
		clear();

		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(pc.getObjectId());
		writeH(list.size());
		writeC(0);
		show(list);
	}

	private void show(List<AutoHuntSell> list) {
		int idx = 0;
		
		for (AutoHuntSell s : list) {
			Item item = ItemDatabase.find(s.getItem());
			
			if (item != null) {
				StringBuffer sb = new StringBuffer();

				// 인첸트 레벨 표현
				if (s.getEn() != 0 && (item.getType1().equalsIgnoreCase("weapon") || item.getType1().equalsIgnoreCase("armor")))
					sb.append(s.getEn() >= 0 ? "+" : "-").append(s.getEn());
				
				// 이름 표현
				sb.append(" ").append(s.getItem());

				writeD(idx++); // 번호
				writeC(0); // 타입
				writeH(item.getInvGfx()); // GFX 아이디
				writeC(s.getBless()); // 1: 보통 0: 축 2: 저주
				writeD(1); // 현재아템 총수량
				writeC(1); // 1: 확인 0: 미확인
				writeS(sb.toString()); // 아이템 이름
			}
		}
	}
}

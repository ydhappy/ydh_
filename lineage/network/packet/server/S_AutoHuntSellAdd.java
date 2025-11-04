package lineage.network.packet.server;

import java.sql.Connection;
import java.util.List;

import lineage.database.DatabaseConnection;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class S_AutoHuntSellAdd extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, PcInstance pc, List<ItemInstance> list) {
		if (bp == null)
			bp = new S_AutoHuntSellAdd(pc, list);
		else
			((S_AutoHuntSellAdd) bp).toClone(pc, list);
		return bp;
	}

	public S_AutoHuntSellAdd(PcInstance pc, List<ItemInstance> list) {
		toClone(pc, list);
	}

	public void toClone(PcInstance pc, List<ItemInstance> list) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOPSELL);
		writeD(pc.getObjectId());
		writeH(list.size()); // 인벤토리에서 팔수잇는 아템 갯수

		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();

			for (ItemInstance item : list) {
				writeD(item.getObjectId());
				writeD(NpcSpawnlistDatabase.sellShop.getPrice(con, item));
			}
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(con);
		}
	}
}

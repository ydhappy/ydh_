package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_KingdomAgent extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int kingdom_id, long obj_id){
		if(bp == null)
			bp = new S_KingdomAgent(kingdom_id, obj_id);
		else
			((S_KingdomAgent)bp).clone(kingdom_id, obj_id);
		return bp;
	}
	
	public S_KingdomAgent(int kingdom_id, long obj_id){
		clone(kingdom_id, obj_id);
	}
	
	public void clone(int kingdom_id, long obj_id){
		clear();

		// kingdom_id가 2일 경우 7로 변경
		int finalKingdomId = (kingdom_id == 2) ? 7 : kingdom_id;

		writeC(Opcodes.S_OPCODE_CASTLEMASTER);
		writeC(finalKingdomId);
		writeD(obj_id);
	}
}
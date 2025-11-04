package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BuffMajok extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int type, int time) {
		if (bp == null)
			bp = new S_BuffMajok(type, time);
		else
			((S_BuffMajok) bp).clone(type, time);
		return bp;
	}

	public S_BuffMajok(int type, int time) {
		clone(type, time);
	}

	public void clone(int type, int time) {
		clear();
		
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x16);
		writeC(0xdd);
		writeH(time);
		writeC(type);
	}
}

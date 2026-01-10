package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;


public class S_CharacterKarma extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int karma) {
		if (bp == null)
			bp = new S_CharacterKarma(karma);
		else
			((S_CharacterKarma) bp).clone(karma);
		return bp;
	}

	public S_CharacterKarma(int karma) {
		clone(karma);
	}

	public void clone(int karma) {
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x57); // type
		writeD(karma); //
	}

}

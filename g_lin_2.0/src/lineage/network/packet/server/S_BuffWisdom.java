package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BuffWisdom extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_BuffWisdom();
		else
			((S_BuffWisdom)bp).Clone();
		return bp;
	}
	
	public S_BuffWisdom(){
		Clone();
	}
	
	public void Clone(){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x39);
		writeC(0x2c);
		writeC(0x4b);							// 증가한 값
	}

}

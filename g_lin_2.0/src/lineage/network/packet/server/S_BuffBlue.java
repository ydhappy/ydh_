package lineage.network.packet.server;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BuffBlue extends ServerBasePacket {

   static public BasePacket clone(BasePacket bp, int type, int time){
      if(bp == null)
         bp = new S_BuffBlue(type, time);
      else
         ((S_BuffBlue)bp).clone(type, time);
      return bp;
   }
   
   public S_BuffBlue(int type, int time){
      clone(type, time);
   }
   
   public void clone(int type, int time){
      clear();
      writeC(Opcodes.S_OPCODE_UNKNOWN2);
      writeC(type);
      writeH(time);
   }

}
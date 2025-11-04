package lineage.world.object.npc.buff;

import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class PolymorphMagician extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "newgypsy"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("해골(10)")){
			type = "해골";
		}else if(action.equalsIgnoreCase("라이칸스로프(10)")){
			type = "라이칸스로프";
		}else if(action.equalsIgnoreCase("오크 궁수")){
			type = "오크 궁수";
		}else if(action.equalsIgnoreCase("가스트(10)")){
			type = "가스트";
		}else if(action.equalsIgnoreCase("아투바오크(10)")){
			type = "아투바 오크";
		}else if(action.equalsIgnoreCase("해골도끼병(10)")){
			type = "해골 도끼병";
		}else if(action.equalsIgnoreCase("오크스카우트(15)")){
			type = "오크 스카우트";
		}else if(action.equalsIgnoreCase("트롤(15)")){
			type = "트롤";
		}
		
		if(type!=null && pc.getInventory().isAden(100, true)){
			ShapeChange.onBuff(pc, pc, PolyDatabase.getPolyName(type), 1800, false, true);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		}else{
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189, "아데나"));
		}
	}

}
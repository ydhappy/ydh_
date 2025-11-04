package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Lapyahee extends CraftInstance {

	public Lapyahee(Npc npc) {
		super(npc);

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("악몽의 장궁");
		if(i != null){
			craft_list.put("a1", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("달의 장궁"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 100) );
			l.add( new Craft(ItemDatabase.find("검은 혈흔"), 200) );
			l.add( new Craft(ItemDatabase.find("붉은빛의 수정"), 30) );
			list.put(i, l);
		}
		
/*		i = ItemDatabase.find("나이트 발드의 양손검");
		if(i != null){
			craft_list.put("a2", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("데스나이트의 불검"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 100) );
			l.add( new Craft(ItemDatabase.find("검은 혈흔"), 200) );
			l.add( new Craft(ItemDatabase.find("붉은빛의 수정"), 30) );
			list2.put("a2", l);
		}
		i = ItemDatabase.find("나이트 발드의 양손검");
		if(i != null){
			craft_list.put("a3", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("커츠의 검"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 100) );
			l.add( new Craft(ItemDatabase.find("검은 혈흔"), 200) );
			l.add( new Craft(ItemDatabase.find("붉은빛의 수정"), 30) );
			list2.put("a3", l);
		}
		i = ItemDatabase.find("진노의 크로우");
		if(i != null){
			craft_list.put("a4", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("론드의 이도류"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 100) );
			l.add( new Craft(ItemDatabase.find("검은 혈흔"), 200) );
			l.add( new Craft(ItemDatabase.find("붉은빛의 수정"), 30) );
			list.put(i, l);
		}
		i = ItemDatabase.find("포효의 이도류");
		if(i != null){
			craft_list.put("a5", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("론드의 이도류"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 100) );
			l.add( new Craft(ItemDatabase.find("검은 혈흔"), 200) );
			l.add( new Craft(ItemDatabase.find("붉은빛의 수정"), 30) );
			list.put(i, l);
		}
		i = ItemDatabase.find("제로스의 지팡이");
		if(i != null){
			craft_list.put("a6", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("바포메트의 지팡이"), 1) );
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 100) );
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 100) );
			l.add( new Craft(ItemDatabase.find("검은 혈흔"), 200) );
			l.add( new Craft(ItemDatabase.find("붉은빛의 수정"), 30) );
			list.put(i, l);
		}*/
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lapyahee"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if(action.equalsIgnoreCase("request craft")) {
			if(pc.getKarmaLevel() <= 3)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lapyahee01"));
			else if(pc.getKarmaLevel() <= 6)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lapyahee02"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lapyahee03"));
		} else {
			//
			super.toTalk(pc, action, type, cbp);
		}
	}
}

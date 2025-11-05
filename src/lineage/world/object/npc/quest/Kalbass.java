package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Kalbass extends QuestInstance {
	
	public Kalbass(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("연금술사의 돌");
		if(i != null){
			craft_list.put("request stone of alchemist", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고대인의 주술서 1권"), 1) );
			l.add( new Craft(ItemDatabase.find("고대인의 주술서 2권"), 1) );
			l.add( new Craft(ItemDatabase.find("고대인의 주술서 3권"), 1) );
			l.add( new Craft(ItemDatabase.find("고대인의 주술서 4권"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kalbass1"));
	}

}

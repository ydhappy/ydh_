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

public class Yahi extends CraftInstance {

	public Yahi(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("데몬의 지팡이");
		if (i != null) {
			craft_list.put("request enchanted staff of demon", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("마력을 잃은 데몬의 지팡이"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("바포메트의 지팡이");
		if (i != null) {
			craft_list.put("request enchanted staff of baphomet", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("마력을 잃은 바포메트의 지팡이"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("베레스의 지팡이");
		if (i != null) {
			craft_list.put("request enchanted staff of beleth", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("마력을 잃은 베레스의 지팡이"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fsflame1"));
	}

}

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

public class Lesserdemon extends CraftInstance {

	public Lesserdemon(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("악마의 크로스보우");
		if (i != null) {
			craft_list.put("request crossbow of evil", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("타락의 외침"), 1));
			l.add(new Craft(ItemDatabase.find("악마의 검은색 족쇄"), 10));
			l.add(new Craft(ItemDatabase.find("악마의 붉은색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 파란색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 흰색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("정신력의 물약"), 10));
			list.put(i, l);
		}

		i = ItemDatabase.find("악마의 칼");
		if (i != null) {
			craft_list.put("request sword of evil", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("타락의 독"), 1));
			l.add(new Craft(ItemDatabase.find("악마의 검은색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 붉은색 족쇄"), 10));
			l.add(new Craft(ItemDatabase.find("악마의 파란색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 흰색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("정신력의 물약"), 10));
			list.put(i, l);
		}

		i = ItemDatabase.find("악마의 이도류");
		if (i != null) {
			craft_list.put("request dualblade of evil", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("타락의 이빨"), 1));
			l.add(new Craft(ItemDatabase.find("악마의 검은색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 붉은색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 파란색 족쇄"), 10));
			l.add(new Craft(ItemDatabase.find("악마의 흰색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("정신력의 물약"), 10));
			list.put(i, l);
		}

		i = ItemDatabase.find("악마의 크로우");
		if (i != null) {
			craft_list.put("request claw of evil", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("타락의 손길"), 1));
			l.add(new Craft(ItemDatabase.find("악마의 검은색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 붉은색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 파란색 족쇄"), 5));
			l.add(new Craft(ItemDatabase.find("악마의 흰색 족쇄"), 10));
			l.add(new Craft(ItemDatabase.find("정신력의 물약"), 10));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (getY() == 32828) {
			// 발록
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ldemon1"));
		} else {
			// 야히
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tldemon1"));
		}
	}
}

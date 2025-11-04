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

public class Balrogbunsin extends CraftInstance {

	public Balrogbunsin(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("저주받은 피");
		if (i != null) {
			craft_list.put("request cursed blood", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("타락의 악마서 1권"), 1));
			l.add(new Craft(ItemDatabase.find("타락의 악마서 2권"), 1));
			l.add(new Craft(ItemDatabase.find("타락의 악마서 3권"), 1));
			l.add(new Craft(ItemDatabase.find("타락의 악마서 4권"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("발록의 핏빛 망토");
		if (i != null) {
			craft_list.put("request blood cloak of barlog", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("타락의 머리카락"), 1));
			l.add(new Craft(ItemDatabase.find("타락의 낫"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("그림자 신전 2층 열쇠");
		if (i != null) {
			craft_list.put("request key2 tos", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혼돈의 머리카락"), 1));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 1000));
			list.put(i, l);
		}

		i = ItemDatabase.find("그림자 신전 3층 열쇠 (흰)");
		if (i != null) {
			craft_list.put("request key31 tos", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("죽음의 머리카락 (흰)"), 1));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 1000));
			list.put(i, l);
		}

		i = ItemDatabase.find("그림자 신전 3층 열쇠 (파)");
		if (i != null) {
			craft_list.put("request key32 tos", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("죽음의 머리카락 (파)"), 1));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 1000));
			list.put(i, l);
		}

		i = ItemDatabase.find("그림자 신전 3층 열쇠 (빨)");
		if (i != null) {
			craft_list.put("request key33 tos", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("죽음의 머리카락 (빨)"), 1));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 1000));
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fakebarlog1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("request craft"))
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fakebarlog2"));
		else
			super.toTalk(pc, action, type, cbp);
	}
}

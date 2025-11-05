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

public class Blacksmith extends CraftInstance {

	public Blacksmith(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("야히의 셔츠");
		if (i != null) {
			craft_list.put("a", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혼돈의 문장"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 겉옷"), 50));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 갑옷");
		if (i != null) {
			craft_list.put("b", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("죽음의 갑옷"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 의지"), 5));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 망토");
		if (i != null) {
			craft_list.put("c", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("죽음의 망토"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 날개"), 50));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 장갑");
		if (i != null) {
			craft_list.put("d", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("죽음의 비늘"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 손톱"), 20));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 부츠");
		if (i != null) {
			craft_list.put("e", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혼돈의 손길"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 손톱"), 40));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 반지");
		if (i != null) {
			craft_list.put("f", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혼돈의 망토"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 손톱"), 10));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 목걸이");
		if (i != null) {
			craft_list.put("g", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혼돈의 망토"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 꼬리"), 1));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
		i = ItemDatabase.find("야히의 투구");
		if (i != null) {
			craft_list.put("h", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혼돈의 머리카락"), 1));
			l.add(new Craft(ItemDatabase.find("야히의 가면"), 1));
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		//
		if (pc.isKarmaType() <= 0)
			return;
		//
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmith" + pc.getKarmaLevel()));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("request craft") || action.equalsIgnoreCase("1") || action.equalsIgnoreCase("2") || action.equalsIgnoreCase("3") || action.equalsIgnoreCase("4")
				|| action.equalsIgnoreCase("5") || action.equalsIgnoreCase("6") || action.equalsIgnoreCase("7") || action.equalsIgnoreCase("8")) {
			switch (pc.getKarmaLevel()) {
				case 1:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmitha"));
					break;
				case 2:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithb"));
					break;
				case 3:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithc"));
					break;
				case 4:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithd"));
					break;
				case 5:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithe"));
					break;
				case 6:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithf"));
					break;
				case 7:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithg"));
					break;
				case 8:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithh"));
					break;
				default:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lsmithm"));
					break;
			}
		} else {
			//
			super.toTalk(pc, action, type, cbp);
		}
	}

}

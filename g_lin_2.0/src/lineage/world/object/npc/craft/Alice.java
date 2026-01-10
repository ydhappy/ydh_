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
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Alice extends CraftInstance {

	public Alice(Npc npc) {
		super(npc);

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("앨리스 (1단계)");
		if (i != null) {
			craft_list.put("mate_1", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("발록의 양손검"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 손톱"), 100));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (2단계)");
		if (i != null) {
			craft_list.put("mate_2", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (1단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 이빨"), 100));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (3단계)");
		if (i != null) {
			craft_list.put("mate_3", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (2단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 날개"), 100));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (4단계)");
		if (i != null) {
			craft_list.put("mate_4", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (3단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 분신"), 50));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (5단계)");
		if (i != null) {
			craft_list.put("mate_5", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (4단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 뿔"), 50));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (6단계)");
		if (i != null) {
			craft_list.put("mate_6", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (5단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 의지"), 50));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (7단계)");
		if (i != null) {
			craft_list.put("mate_7", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (6단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 전율"), 10));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

		i = ItemDatabase.find("앨리스 (8단계)");
		if (i != null) {
			craft_list.put("mate_8", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("앨리스 (7단계)"), 1));
			l.add(new Craft(ItemDatabase.find("발록의 가면"), 10));
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		ItemInstance item = null;
		for(Item i : list.keySet()) {
			item = pc.getInventory().find(i);
			if(item != null)
				break;
		}
		if (item == null) {
			if (pc.getKarmaLevel() < 1)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gd"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_1"));
		} else {
			if (pc.isKarmaType() != -1)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "alice_gd"));
			else {
				// 찾은 앨리스 무기 단계에 맞는 메세지 출력.
				switch(item.getItem().getName()) {
					case "앨리스 (1단계)":
						if (pc.getKarmaLevel() < 2) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
				        } else {
				        	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_2"));
				        }
						break;
					case "앨리스 (2단계)":
						if (pc.getKarmaLevel() < 3) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
				        } else {
				        	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_3"));
				        }
						break;
					case "앨리스 (3단계)":
						if (pc.getKarmaLevel() < 4) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
						} else {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_4"));
						}
						break;
					case "앨리스 (4단계)":
						if (pc.getKarmaLevel() < 5) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
						} else {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_5"));
						}
						break;
					case "앨리스 (5단계)":
						if (pc.getKarmaLevel() < 6) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
						} else {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_6"));
						}
						break;
					case "앨리스 (6단계)":
						if (pc.getKarmaLevel() < 7) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
						} else {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_7"));
						}
						break;
					case "앨리스 (7단계)":
						if (pc.getKarmaLevel() < 8) {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aliceyet"));
						} else {
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mate_8"));
						}
						break;
					default:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "alice_8"));
						break;
				}
			}
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("a")) {
			ItemInstance item = null;
			for(Item i : list.keySet()) {
				item = pc.getInventory().find(i);
				if(item != null)
					break;
			}
			if (item == null)
				action = "mate_1";
			else 
				switch(item.getItem().getName()) {
					case "앨리스 (1단계)":
						action = "mate_2";
						break;
					case "앨리스 (2단계)":
						action = "mate_3";
						break;
					case "앨리스 (3단계)":
						action = "mate_4";
						break;
					case "앨리스 (4단계)":
						action = "mate_5";
						break;
					case "앨리스 (5단계)":
						action = "mate_6";
						break;
					case "앨리스 (6단계)":
						action = "mate_7";
						break;
					case "앨리스 (7단계)":
						action = "mate_8";
						break;
				}
			
			super.toTalk(pc, action, type, cbp);
		}
	}

}

package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Suspicious extends CraftInstance {

	List<Item> list;
	
	public Suspicious(Npc npc) {
		super(npc);
		list = new ArrayList<Item>();
		list.add(ItemDatabase.find("신비한 날개깃털"));
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "subsusp1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		String pet_name = null;
		int pet_level = 0;
		int pet_hp = 0;
		int pet_mp = 0;

		if (Lineage.server_version > 144) {
			switch (action) {
			case "buy 11": // 야생견 레벨 4 400
				pet_name = "아기 캥거루";
				pet_level = 6;
				pet_hp = 40 + Util.random(4, 7);
				pet_mp = 12 + Util.random(1, 2);
				break;
			case "buy 22": // 야생견 레벨 5 800
				pet_name = "아기 판다곰";
				pet_level = 6;
				pet_hp = 45 + Util.random(4, 7);
				pet_mp = 12 + Util.random(1, 2);
				break;
			case "buy 33": // 야생견 레벨 6 1600
				pet_name = "아기 진돗개";
				pet_level = 6;
				pet_hp = 45 + Util.random(8, 14);
				pet_mp = 12 + Util.random(2, 4);
				break;
			}
		}
		// 구매할려는 펫 네임 체크
		if (pet_name == null)
		    return;
		for (Item item : list) {
		    ItemInstance ii = pc.getInventory().find(item);
		    // 생성을위해 체크
		    MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(pet_name));
		    mi.setLevel(pet_level);
		    mi.setMaxHp(pet_hp);
		    mi.setMaxMp(pet_mp);
		    mi.setNowHp(pet_hp);
		    mi.setNowMp(pet_mp);
		    mi.setX(pc.getX());
		    mi.setY(pc.getY());
		    mi.setMap(pc.getMap());
		    if (ii != null && ii.getCount() >= 1000) {
		        if (SummonController.toPet(pc, mi)) {
		            pc.getInventory().count(ii, ii.getCount() - 1000, true);
		            MonsterSpawnlistDatabase.setPool(mi);
		            // 창 닫기
		            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		        } else {
		            // 멘트
		            ChattingController.toChatting(pc, "구매하실려는 펫이 너무 많습니다.", Lineage.CHATTING_MODE_MESSAGE);
		        }
		    } else {
		      
		     ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		     // 창 닫기
		     pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		    }
		}
	}
}

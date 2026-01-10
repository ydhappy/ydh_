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
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Bankoo extends CraftInstance {

	List<Item> list;

	public Bankoo(Npc npc) {
		super(npc);
		list = new ArrayList<Item>();
		list.add(ItemDatabase.find("녹색 해츨링 알"));
		list.add(ItemDatabase.find("황색 해츨링 알"));
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bankoo1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

	    if (pc == null) {
	        System.println("Bankoo.toTalk(): pc가 null입니다.");
	        return;
	    }

	    if (action == null) {
	        System.println("Bankoo.toTalk(): action이 null입니다.");
	        return;
	    }

	    Item ii = null;
	    String pet_name = null;
	    int pet_level = 0;
	    int pet_hp = 0;
	    int pet_mp = 0;
	    String eggName = null;

	    if (Lineage.server_version > 144) {
	        switch (action) {
	            case "buy71":
	                pet_name = "해츨링 수컷";
	                pet_level = 6;
	                pet_hp = 40 + Util.random(4, 7);
	                pet_mp = 12 + Util.random(1, 2);
	                eggName = "녹색 해츨링 알";
	                break;
	            case "buy81":
	                pet_name = "해츨링 암컷";
	                pet_level = 6;
	                pet_hp = 45 + Util.random(4, 7);
	                pet_mp = 12 + Util.random(1, 2);
	                eggName = "황색 해츨링 알";
	                break;
	        }
	    }

	    if (pet_name == null) {
	        System.println("Bankoo.toTalk(): pet_name이 설정되지 않았습니다.");
	        return;
	    }

	    if (eggName == null) {
	        System.println("Bankoo.toTalk(): eggName이 설정되지 않았습니다.");
	        return;
	    }

	    if (pc.getInventory().find(eggName) == null) {
	        System.println("Bankoo.toTalk(): " + eggName + "을 찾을 수 없습니다.");
	        ChattingController.toChatting(pc, eggName + "이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	        return;
	    }

	    if (list.size() < 2) {
	        System.println("Bankoo.toTalk(): list에 필요한 아이템 정보가 없습니다.");
	        return;
	    }

	    switch (pet_name) {
	        case "해츨링 수컷":
	            ii = list.get(0);
	            break;
	        case "해츨링 암컷":
	            ii = list.get(1);
	            break;
	    }

	    if (ii == null) {
	        System.println("Bankoo.toTalk(): list에서 찾은 아이템이 null입니다.");
	        return;
	    }

	    ItemInstance iiInstance = pc.getInventory().find(ii);
	    if (iiInstance == null) {
	        System.println("Bankoo.toTalk(): iiInstance가 null입니다.");
	        return;
	    }

	    if (iiInstance.getCount() < 1) {
	        System.println("Bankoo.toTalk(): 아이템 개수가 부족합니다.");
	        return;
	    }

	    MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(pet_name));
	    if (mi == null) {
	        System.println("Bankoo.toTalk(): 몬스터 데이터 생성 실패");
	        return;
	    }

	    mi.setLevel(pet_level);
	    mi.setMaxHp(pet_hp);
	    mi.setMaxMp(pet_mp);
	    mi.setNowHp(pet_hp);
	    mi.setNowMp(pet_mp);
	    mi.setX(pc.getX());
	    mi.setY(pc.getY());
	    mi.setMap(pc.getMap());

	    if (SummonController.toPet(pc, mi)) {
	        pc.getInventory().count(iiInstance, iiInstance.getCount() - 1, true);
	        MonsterSpawnlistDatabase.setPool(mi);
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	    } else {
	        ChattingController.toChatting(pc, "구매하실려는 펫이 너무 많습니다.", Lineage.CHATTING_MODE_MESSAGE);
	    }
	}
}
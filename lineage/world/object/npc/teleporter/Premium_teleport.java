package lineage.world.object.npc.teleporter;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;


public class Premium_teleport extends TeleportInstance {

	List<Item> list;

	public Premium_teleport(Npc npc) {
		super(npc);
		list = new ArrayList<Item>();
		list.add(ItemDatabase.find("신비한 날개깃털"));
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
                 pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "suspicious1t"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("teleport premium-island")) {
			out_말섬(pc);
		} else if (action.equalsIgnoreCase("teleport premium-willow")) {
			out_화전민촌(pc);
		} else if (action.equalsIgnoreCase("teleport premium-woods")) {
			out_윈다우드(pc);
		} else if (action.equalsIgnoreCase("teleport premium-silver")) {
			out_은기사(pc);
		} else if (action.equalsIgnoreCase("teleport premium-gludin")) {
			out_글루디오(pc);
		} else if (action.equalsIgnoreCase("teleport premium-kent")) {
			out_켄트(pc);
		} else if (action.equalsIgnoreCase("teleport premium-heine")) {
			out_하이네(pc);
		} else if (action.equalsIgnoreCase("teleport premium-dragonvalley")) {
			out_인나드(pc);
		} else if (action.equalsIgnoreCase("teleport premium-giran")) {
			out_기란(pc);
		} else if (action.equalsIgnoreCase("teleport premium-werldern")) {
			out_웰던(pc);
		}
	}

	/**
	 * 각 마을
	 */
	private void out_말섬(PcInstance pc) {
		//수량
		int count = 11;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {

		pc.toTeleport(32581, 32928, 0, true);
		pc.getInventory().count(ii, ii.getCount() - 12, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_화전민촌(PcInstance pc) {
		//수량
		int count = 4;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(32750, 32441, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 5, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_윈다우드(PcInstance pc) {
		//수량
		int count = 4;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(32640, 33203, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 5, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_은기사(PcInstance pc) {
		//수량
		int count = 3;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(33080, 33386, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 4, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_글루디오(PcInstance pc) {
		//수량
		int count = 3;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(32612, 32734, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 4, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_켄트(PcInstance pc) {
		//수량
		int count = 2;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(33050, 32782, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 3, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_하이네(PcInstance pc) {
		//수량
		int count = 2;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(33612, 33257, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 3, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_인나드(PcInstance pc) {
		//수량
		int count = 2;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(32684, 32868, 58, true);
		pc.getInventory().count(ii, ii.getCount() - 3, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_기란(PcInstance pc) {
		//수량
		int count = 1;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(33438, 32796, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 2, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
	private void out_웰던(PcInstance pc) {
		//수량
		int count = 1;
		for (Item item : list) {
			ItemInstance ii = pc.getInventory().find(item);
		// 재료 확인.
		if(ii.getCount() > count) {
			
		pc.toTeleport(33709, 32500, 4, true);
		pc.getInventory().count(ii, ii.getCount() - 2, true);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	} else {
		ChattingController.toChatting(pc, "신비한 날개깃털이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "")); // 창제거
	     }
	   }
	}
}
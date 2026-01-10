package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Ludian extends QuestInstance {

	public Ludian(Npc npc) {
		super(npc);
		
		Item i = ItemDatabase.find("보물지도 조각");
		if (i != null) {
			craft_list.put(Lineage.QUEST_RUDIAN, i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("은제 플룻"), 1));
			l.add(new Craft(ItemDatabase.find("친구의 가방"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		Quest q = QuestController.find(pc, Lineage.QUEST_RUDIAN);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_RUDIAN);
		//
		switch(q.getQuestStep()) {
			case Lineage.QUEST_END:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian1c"));
				break;
			case 1:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian7"));
				break;
			default:
				Quest doil_q = QuestController.find(pc, Lineage.QUEST_DOIL);
				if(doil_q!=null && doil_q.getQuestStep()==Lineage.QUEST_END)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian1b"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian1a"));
				break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//
		Quest q = QuestController.find(pc, Lineage.QUEST_RUDIAN);
		if (q == null)
			return;
		//
		if(action.equalsIgnoreCase("A")) {
			q.setQuestStep(1);
			CraftController.toCraft(this, pc, ItemDatabase.find("은제 플룻"), 1, true);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian6"));
			
		}else if(action.equalsIgnoreCase("B")) {
			Item craft = craft_list.get(Lineage.QUEST_RUDIAN);
			if (craft != null) {
				List<Craft> l = list.get(craft);
				if (CraftController.isCraft(pc, l, true)) {
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep(Lineage.QUEST_END);
					//
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian8"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rudian9"));
				}
			}
			
		}
	}

}

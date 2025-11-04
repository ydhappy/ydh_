package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Doyle extends QuestInstance {

	public Doyle(Npc npc) {
		super(npc);
		
		Item i = ItemDatabase.find("보물지도 조각");
		if (i != null) {
			craft_list.put(Lineage.QUEST_DOIL, i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("푸른 도마뱀의 껍질"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		Quest q = QuestController.find(pc, Lineage.QUEST_DOIL);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_DOIL);

		switch(q.getQuestStep()) {
			case Lineage.QUEST_END:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "doil9"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "doil1"));
				break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		Quest q = QuestController.find(pc, Lineage.QUEST_DOIL);
		if (q == null)
			return;

		if(action.equalsIgnoreCase("3")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "doil4"));
			
		}else if(action.equalsIgnoreCase("6")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "doil6"));
			
		}else if(action.equalsIgnoreCase("1")) {
	    	Item craft = craft_list.get(Lineage.QUEST_DOIL);
	        if(q != null){
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep(Lineage.QUEST_END);
					//
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "doil8"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "doil7"));
				}
			}
			
		}
	}
	
}

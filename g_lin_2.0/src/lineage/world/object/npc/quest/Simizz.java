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

public class Simizz extends QuestInstance {

	public Simizz(Npc npc) {
		super(npc);
		
		Item i = ItemDatabase.find("푸른 해적 두건");
		if (i != null) {
			craft_list.put(Lineage.QUEST_SIMIZZ, i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("아들의 유골"), 1));
			l.add(new Craft(ItemDatabase.find("아들의 초상화"), 1));
			l.add(new Craft(ItemDatabase.find("아들의 편지"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		//
		Quest resta_q = QuestController.find(pc, Lineage.QUEST_RESTA);
		if (resta_q!=null && resta_q.getQuestStep()==4) {
			if(pc.getInventory().findDbNameId(3598) == null)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz0"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz11"));
			return;
		}
		//
		Quest q = QuestController.find(pc, Lineage.QUEST_SIMIZZ);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_SIMIZZ);
		//
		switch(q.getQuestStep()) {
			case Lineage.QUEST_END:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz15"));
				break;
			case 0:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz1"));
				break;
			case 1:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz6"));
				break;
			case 2:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz0"));
				break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//
		Quest q = QuestController.find(pc, Lineage.QUEST_SIMIZZ);
		if (q == null)
			return;
		//
		if(action.equalsIgnoreCase("a")) {
			q.setQuestStep(1);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz7"));
			
		}else if(action.equalsIgnoreCase("b")) {
			Item craft = craft_list.get(Lineage.QUEST_SIMIZZ);
			if (craft != null) {
				List<Craft> l = list.get(craft);
				if (CraftController.isCraft(pc, l, true)) {
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep(2);
					//
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz8"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz9"));
				}
			}
			
		}else if (action.equalsIgnoreCase("d")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "simizz12"));
			q.setQuestStep(Lineage.QUEST_END);
			
		}
	}

}

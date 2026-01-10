package lineage.world.object.npc.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
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

public class Uamulet extends QuestInstance {

	public Uamulet(Npc npc) {
		super(npc);
		
		Item i = ItemDatabase.find("노예의 목걸이");
		if(i != null)
			craft_list.put("1", i);
		i = ItemDatabase.find("약속의 목걸이");
		if(i != null)
			craft_list.put("2", i);
		i = ItemDatabase.find("해방의 목걸이");
		if(i != null)
			craft_list.put("3", i);
		i = ItemDatabase.find("사냥개의 목걸이");
		if(i != null)
			craft_list.put("4", i);
		i = ItemDatabase.find("마족의 목걸이");
		if(i != null)
			craft_list.put("5", i);
		i = ItemDatabase.find("용사의 목걸이");
		if(i != null)
			craft_list.put("6", i);
		i = ItemDatabase.find("장군의 목걸이");
		if(i != null)
			craft_list.put("7", i);
		/*i = ItemDatabase.find("대장군의 목걸이");
		if(i != null)
			craft_list.put("8", i);*/
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		//
		Quest q = QuestController.find(pc, Lineage.QUEST_UAMULET);
		if(q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_UAMULET);
		if(q.getQuestStep() == 100) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uamuletd"));
			return;
		}
		if(pc.isKarmaType() != -1) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uamulet0"));
			return;
		}
		//
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "uamulet"+pc.getKarmaLevel()));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		Item craft = craft_list.get(action);
		Quest q = QuestController.find(pc, Lineage.QUEST_UAMULET);
		if(craft!=null && q!=null && q.getQuestStep()!=pc.getKarmaLevel() && q.getQuestStep()!=100) {
			// 아이템 지급.
			CraftController.toCraft(this, pc, craft, 1, true);
			// 퀘스트 스탭 변경.
			q.setQuestStep(pc.getKarmaLevel());
			if(q.getQuestStep() == 8)
				q.setQuestStep(100);
		}
	}

}

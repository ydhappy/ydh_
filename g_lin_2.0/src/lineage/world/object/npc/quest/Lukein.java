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
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Lukein extends QuestInstance {

	public Lukein(Npc npc) {
		super(npc);

		Item i = ItemDatabase.find("해골 목걸이");
		if (i != null) {
			craft_list.put(Lineage.QUEST_LUKEIN, i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("할아버지의 보물"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("레스타의 반지[1]");
		if (i != null) {
			craft_list.put(Lineage.QUEST_RESTA, i);

			List<Craft> p = new ArrayList<Craft>();
			p.add(new Craft(ItemDatabase.find("레스타의 반지"), 1));
			list.put(i, p);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {

		ItemInstance Treasure = pc.getInventory().find(ItemDatabase.find("할아버지의 보물"));

		Quest resta_q = QuestController.find(pc, Lineage.QUEST_RESTA);
		if (resta_q != null && resta_q.getQuestStep() == 2) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein10"));
			return;
		}

		Quest q = QuestController.find(pc, Lineage.QUEST_LUKEIN);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_LUKEIN);

		switch (q.getQuestStep()) {
		case 0:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein1"));
			break;
		case 1:
			if (Treasure != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein9"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein8"));
			}
			break;
		default:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein0"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    Quest q = QuestController.find(pc, Lineage.QUEST_LUKEIN);
	    if (q == null)
	        return;

	    if (action.equalsIgnoreCase("0")) {
	        CraftController.tuCraft(this, pc, ItemDatabase.find("작은 보물지도[0]"), 1, true);
	        ChattingController.toChatting(pc, "루케인이 당신에게 힌트가 담긴 작은 보물지도를 건네주었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	        // 제안을 받아 들인다
	        q.setQuestStep(1);
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein8"));
	    } else if (action.equalsIgnoreCase("1")) {
	        Item craft = craft_list.get(Lineage.QUEST_LUKEIN);
	        if (q != null) {
	            List<Craft> l = list.get(craft);
	            // 재료 확인.
	            if (CraftController.isCraft(pc, l, true)) {
	                // 재료 제거
	                CraftController.toCraft(pc, l);
	                // 아이템 지급.
	                CraftController.tuCraft(this, pc, craft, 1, true);
	                ChattingController.toChatting(pc, "루케인이 당신에게 해골 목걸이를 주었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	                // 퀘스트 스탭 변경.
	                q.setQuestStep(2);
	                pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein0"));
	            }
	        }
	    } else if (action.equalsIgnoreCase("a")) {
	        Quest resta_q = QuestController.find(pc, Lineage.QUEST_RESTA);
	        if (resta_q != null) {
	            Item craft = craft_list.get(Lineage.QUEST_RESTA);
	            if (resta_q != null) {
	                List<Craft> p = list.get(craft);
	                if (CraftController.isCraft(pc, p, true)) {
	                    // 재료 제거
	                    CraftController.toCraft(pc, p);
	                    // 아이템 지급.
	                    CraftController.tuCraft(this, pc, craft, 1, true);
	                    ChattingController.toChatting(pc, "루케인이 당신에게 레스타의 반지를 주었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	                    // 퀘스트 스탭 변경.
	                    resta_q.setQuestStep(3);
	                    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein12"));
	                } else {
	                    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lukein13"));
	                }
	            }
	        }
	    }
	}
}
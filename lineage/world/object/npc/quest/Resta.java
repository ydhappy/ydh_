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

public class Resta extends QuestInstance {

	public Resta(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		//
		Quest q = QuestController.find(pc, Lineage.QUEST_RESTA);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_RESTA);
		//
		if (q!=null && q.getQuestStep()==Lineage.QUEST_END) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta1e"));
			return;
		}
		//
		Quest simizz_q = QuestController.find(pc, Lineage.QUEST_SIMIZZ);
		if (simizz_q!=null && simizz_q.getQuestStep()==Lineage.QUEST_END) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta14"));
			return;
		}
		//
		switch(q.getQuestStep()) {
			case 2:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta16"));
				break;
			case 3:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta11"));
				q.setQuestStep(4);
				break;
			case 4:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta13"));
				break;
			default:
				Quest cadmus_q = QuestController.find(pc, Lineage.QUEST_CADMUS);
				
				if (cadmus_q!=null && simizz_q!=null && simizz_q.getQuestStep()==2 && cadmus_q.getQuestStep()==1) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta1a"));
					break;
				}
				if (cadmus_q!=null && cadmus_q.getQuestStep()==1) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta1c"));
					break;
				}
				if (simizz_q!=null && simizz_q.getQuestStep()==2) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta1b"));
					break;
				}
				
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta1"));
				break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//
		ItemInstance Ring = pc.getInventory().find(ItemDatabase.find("레스타의 반지[1]"));
		
		Quest q = QuestController.find(pc, Lineage.QUEST_RESTA);
		if (q == null)
			return;
		//
		if(action.equalsIgnoreCase("A")) {
			Quest rudian_q = QuestController.find(pc, Lineage.QUEST_RUDIAN);
			if (rudian_q!=null && rudian_q.getQuestStep()==Lineage.QUEST_END)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta6"));
			else 
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta4"));
			
		}else if(action.equalsIgnoreCase("B")) {
			q.setQuestStep(2);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta10"));
			
		}else if(action.equalsIgnoreCase("D")) {
			if (Ring != null) {
				CraftController.toCraft(this, pc, ItemDatabase.find("보물지도 조각"), 1, true);
				pc.getInventory().remove(Ring, true);
				q.setQuestStep(Lineage.QUEST_END);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "resta15"));
			}
		}
	}
}

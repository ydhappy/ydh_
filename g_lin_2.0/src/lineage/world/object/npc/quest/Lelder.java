package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Lelder extends QuestInstance {

	public Lelder(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		
		ItemInstance Report = pc.getInventory().find(ItemDatabase.find("리자드맨의 보고서"));
		ItemInstance Relics = pc.getInventory().find(ItemDatabase.find("리자드맨의 보물"));

		if (pc.getLevel() < 39) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder14"));

		} else {
			Quest q = QuestController.find(pc, Lineage.QUEST_LELDER);
			if (q == null)
				q = QuestController.newQuest(pc, this, Lineage.QUEST_LELDER);

			switch (q.getQuestStep()) {
			case 0:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder1"));
				break;
			case 1:
				if (Report != null) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder7"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder7b"));
				}
				break;
			case 2:
				if (Relics != null) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder12"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder11"));
				}
				break;
			case Lineage.QUEST_END:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder0"));
				break;
			}
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		ItemInstance Report = pc.getInventory().find(ItemDatabase.find("리자드맨의 보고서"));
		ItemInstance Relics = pc.getInventory().find(ItemDatabase.find("리자드맨의 보물"));

		Quest q = QuestController.find(pc, Lineage.QUEST_LELDER);
		if (q == null)
			return;

		if (action.equalsIgnoreCase("a")) {
			q.setQuestStep(1);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder5"));
		} else if (action.equalsIgnoreCase("b")) {
			if (Report != null) {
				pc.getInventory().remove(Report, true);
				q.setQuestStep(2);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder10"));
			}
		} else if (action.equalsIgnoreCase("c")) {
			if (Relics != null) {
				CraftController.toCraft(this, pc, ItemDatabase.find("리자드맨 영웅의 장갑"), 1, true);
				pc.getInventory().remove(Relics, true);
				q.setQuestStep(Lineage.QUEST_END);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lelder13"));
			}
		}
	}
}
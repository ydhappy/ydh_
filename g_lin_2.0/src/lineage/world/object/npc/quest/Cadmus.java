package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;
import lineage.world.object.npc.Kamit;

public class Cadmus extends QuestInstance {

	public Cadmus(Npc npc) {
		super(npc);

	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		//
		ItemInstance Cadmus = pc.getInventory().find(ItemDatabase.find("카드무스의 목걸이"));

		Quest q = QuestController.find(pc, Lineage.QUEST_CADMUS);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_CADMUS);
		//
		switch (q.getQuestStep()) {

		case Lineage.QUEST_END:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus1c"));
			break;
		case 2:
			if (Cadmus != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus8"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus1a"));
			}
			break;
		case 3:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus8"));
			break;
		default:
			Quest doil_q = QuestController.find(pc, Lineage.QUEST_DOIL);
			if (doil_q != null && doil_q.getQuestStep() == Lineage.QUEST_END) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus1b"));
				return;
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus1"));
			}
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//
		ItemInstance Cadmus = pc.getInventory().find(ItemDatabase.find("카드무스의 목걸이"));

		Quest q = QuestController.find(pc, Lineage.QUEST_CADMUS);
		if (q == null)
			return;
		//
		if (action.equalsIgnoreCase("A")) {
			ItemInstance item = pc.getInventory().find(ItemDatabase.find("보물지도 조각"));
			if (item != null && item.getCount() >= 3) {
				pc.getInventory().count(item, item.getCount() - 3, true);
				q.setQuestStep(2);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus6"));
			} else {
				q.setQuestStep(1);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "cadmus5"));
			}
		} else if (action.equalsIgnoreCase("B")) {
			if (Cadmus != null) {
				CraftController.toCraft(this, pc, ItemDatabase.find("완성된 보물지도"), 1, true);
				pc.getInventory().remove(Cadmus, true);
				q.setQuestStep(Lineage.QUEST_END);
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			}
		}

	}
}

package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Lekman extends FirstQuest {

	private long lastSoundPlayTime = 0; // 마지막 사운드 재생 시간을 저장할 변수

	public Lekman(Npc npc) {
		super(npc);

		List<Craft> l = new ArrayList<Craft>();
		l.add(new Craft(ItemDatabase.find("말하는 두루마리"), 1));
		list.put(null, l);
	}

	public long getLastSoundPlayTime() {
		return lastSoundPlayTime;
	}

	public void setLastSoundPlayTime(long lastSoundPlayTime) {
		this.lastSoundPlayTime = lastSoundPlayTime;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
		if (q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_TALKINGSCROLL);

		switch (q.getQuestStep()) {
		case 0:
			long currentTime = System.currentTimeMillis(); // 현재 시간(밀리초)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena2"));
			if (currentTime - getLastSoundPlayTime() >= 2400) {

				pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27803));
				setLastSoundPlayTime(currentTime);
			}
			break;
		case 1:
			if (pc.getLevel() >= 5) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena4"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena14"));
			}
			break;
		case 2:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena6"));
			break;
		case 3:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena8"));
			break;
		case 4:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena10"));
			break;
		case Lineage.QUEST_END:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena11"));
			break;
		}

		switch (pc.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
		case Lineage.LINEAGE_CLASS_WIZARD:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orenb13"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		ItemInstance Talking_scroll = pc.getInventory().find(ItemDatabase.find("말하는 두루마리"));
		ItemInstance Secret_Book = pc.getInventory().find(ItemDatabase.find("초보 비법서"));
		ItemInstance Cuckoos = pc.getInventory().find(ItemDatabase.find("쿠커스의 증표"));
		ItemInstance Certificate = pc.getInventory().find(ItemDatabase.find("계곡의 증표"));

		if (action.equalsIgnoreCase("0")) {
			if (Talking_scroll == null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena0"));
				return;
			}
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if (q != null && q.getQuestStep() == 0) {
				List<Craft> l = list.get(null);
				// 재료 확인.
				if (CraftController.isCraft(pc, l, true)) {
					// 인벤토리에서 지도를 확인.
					if (pc.getInventory().find("지도 숨계") != null || pc.getInventory().find("지도 노섬") != null || pc.getInventory().find("상아탑의 단검") != null) {
						q.setQuestStep(1);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena9-2"));
					} else {
						// 지도가 없을 경우 아이템 지급 로직
						toStep0(pc);
						q.setQuestStep(1);
						// 창 띄우기.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena3"));
					}
				}
			}
		} else if (action.equalsIgnoreCase("1")) {
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if (q != null && q.getQuestStep() == 1) {
				List<Craft> l = list.get(null);
				// 재료 확인.
				if (CraftController.isCraft(pc, l, true)) {
					// 인벤토리에서 지도를 확인.
					if (pc.getInventory().find("상아탑의 가죽 투구") != null || pc.getInventory().find("상아탑의 가죽 장갑") != null) {
						q.setQuestStep(2);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena9-2"));
					} else {
						// 지도가 없을 경우 아이템 지급 로직
						toStep1(pc);
						q.setQuestStep(2);
						// 창 띄우기.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena5"));
					}
				}
			}
		} else if (action.equalsIgnoreCase("2")) {
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if (q != null && q.getQuestStep() == 2) {
				if (Secret_Book != null) {
					if (pc.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT) {
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 100, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 속도향상 물약"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 용기의 물약"), 10, true);
					} else if (pc.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 100, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 속도향상 물약"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 엘븐 와퍼"), 10, true);
					}
					pc.getInventory().remove(Secret_Book, true);
					q.setQuestStep(3);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena7"));
				} else {
					// 퀘스트 아이템이 없을 때 메시지 전송
					ChattingController.toChatting(pc, "초보 비법서 아이템이 부족합니다.", 20);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
				}
			}
		} else if (action.equalsIgnoreCase("3")) {
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if (q != null && q.getQuestStep() == 3) {
				if (Cuckoos != null) {
					if (pc.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT) {
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 샌달"), 1, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 100, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 50, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 변신 주문서"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("은기사 마을 귀환 주문서"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("지도 은기사"), 1, true);
						q.setQuestStep(4);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena9"));
					} else if (pc.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 샌달"), 1, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 200, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 순간이동 주문서"), 50, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 변신 주문서"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 귀환 주문서"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("요정숲 귀환 주문서"), 10, true);
						CraftController.toCraft(this, pc, ItemDatabase.find("지도 요숲"), 1, true);
						q.setQuestStep(Lineage.QUEST_END);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena9-1"));
					}
					pc.getInventory().remove(Cuckoos, true);
					pc.getInventory().remove(Talking_scroll, true);
				} else {
					// 퀘스트 아이템이 없을 때 메시지 전송
					ChattingController.toChatting(pc, "쿠커스의 증표 아이템이 부족합니다.", 20);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
				}
			}
		} else if (action.equalsIgnoreCase("4")) {
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if (q != null && q.getQuestStep() == 4) {
				if (Certificate != null) {
					CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 방패"), 1, true);
					CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 100, true);
					q.setQuestStep(Lineage.QUEST_END);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena11"));
				} else {
					// 퀘스트 아이템이 없을 때 메시지 전송
					ChattingController.toChatting(pc, "계곡의 증표 아이템이 부족합니다.", 20);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
				}
			}
		}
	}
}

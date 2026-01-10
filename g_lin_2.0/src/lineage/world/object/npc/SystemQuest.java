package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lineage.database.AccountDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class SystemQuest extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		showHtml(pc);
	}


	public void showHtml(PcInstance pc) {
		List<String> kqlist = new ArrayList<String>();
		kqlist.clear();
	    // 사용자 레벨이 15 이하일 때
	    if (pc.getLevel() <= 15) {
	        // kquest0 HTML 창을 띄우도록 설정
	        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kquest", null, kqlist));
	        return;
	    }
	    
		switch (pc.getRadomQuest()) {
		case 1:
			// 퀘스트설명
			kqlist.add(String.format("%s 소탕", Lineage.rqmonst1));
			// 퀘스트명
			kqlist.add(String.format("%s %d마리를 처치하세요.", Lineage.rqmonst1, Lineage.rqmonstkill1));
			// 퀘스트목표
			kqlist.add(String.format("%s", Lineage.rqmonstkill1));
			if (pc.getRandomQuestkill() > 0) {
				// 처치몬스터
				kqlist.add(String.format("%d", pc.getRandomQuestkill()));
			} else {
				kqlist.add(String.format("0"));
			}

			// 보상
			// 보상
			//아이템 #7
			kqlist.add(String.format("%s", Lineage.rq1));
			//수량 #8
			kqlist.add(String.format("%s", Lineage.rqc1));
			//경험치 #9
			kqlist.add(String.format("%s", Lineage.rqExp1));
			break;
		case 2:
			// 퀘스트설명
			kqlist.add(String.format("%s 소탕", Lineage.rqmonst2));
			// 퀘스트명
			kqlist.add(String.format("%s %d마리를 처치하세요.", Lineage.rqmonst2, Lineage.rqmonstkill2));
			// 퀘스트목표
			kqlist.add(String.format("%s", Lineage.rqmonstkill2));
			if (pc.getRandomQuestkill() > 0) {
				// 처치몬스터
				kqlist.add(String.format("%d", pc.getRandomQuestkill()));
			} else {
				kqlist.add(String.format("0"));
			}

			// 보상
			//아이템 #7
			kqlist.add(String.format("%s", Lineage.rq2));
			//수량 #8
			kqlist.add(String.format("%s", Lineage.rqc2));
			//경험치 #9
			kqlist.add(String.format("%s", Lineage.rqExp2));
			break;
		case 3:
			// 퀘스트설명
			kqlist.add(String.format("%s 소탕", Lineage.rqmonst3));
			// 퀘스트명
			kqlist.add(String.format("%s %d마리를 처치하세요.", Lineage.rqmonst3, Lineage.rqmonstkill3));
			// 퀘스트목표
			kqlist.add(String.format("%s", Lineage.rqmonstkill3));
			if (pc.getRandomQuestkill() > 0) {
				// 처치몬스터
				kqlist.add(String.format("%d", pc.getRandomQuestkill()));
			} else {
				kqlist.add(String.format("0"));
			}

			// 보상
			//아이템 #7
			kqlist.add(String.format("%s", Lineage.rq3));
			//수량 #8
			kqlist.add(String.format("%s", Lineage.rqc3));
			//경험치 #9
			kqlist.add(String.format("%s", Lineage.rqExp3));
			break;
		default:
			break;
		}
		if (pc.getRandomQuestPlay() > 0) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kquest2", null, kqlist));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kquest3", null, kqlist));
			return;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    int monstkill = 0;
	    
	    if (action.equalsIgnoreCase("kquest2-start")) {
	        if (pc.getRandomQuestCount() >= Lineage.dayquest) {
	            ChattingController.toChatting(pc, String.format("오늘 수행 가능한 퀘스트를 전부 완료 하셨습니다."), Lineage.CHATTING_MODE_MESSAGE);
	            return;
	        }
	        
	        // 랜덤으로 퀘스트 선택
	        Random random = new Random();
	        int selectedQuest = random.nextInt(3) + 1;
	        
	        pc.setRadomQuest(selectedQuest);
	        pc.setRandomQuestPlay(pc.getRandomQuestPlay() + 1);
	        pc.setRandomQuestCount(pc.getRandomQuestCount() + 1);
	    }
	    
	    switch (pc.getRadomQuest()) {
	    case 1:
	        monstkill = Lineage.rqmonstkill1;
	        break;
	    case 2:
	        monstkill = Lineage.rqmonstkill2;
	        break;
	    case 3:
	        monstkill = Lineage.rqmonstkill3;
	        break;
	    default:
	        break;
	    }
	    
	    if (action.equalsIgnoreCase("kquest2-finish")) {
	        if (pc.getRadomQuest() == 0) {
	            ChattingController.toChatting(pc, String.format("[일일 퀘스트] 완료 가능한 퀘스트가 없습니다."), Lineage.CHATTING_MODE_MESSAGE);
	            return;
	        }
	        
	        if (pc.getRandomQuestkill() >= monstkill) {
	            switch (pc.getRadomQuest()) {
	            case 1:
	                rewardQuest(pc, Lineage.rq1, Lineage.rqc1, Lineage.rqExp1);
	                break;
	            case 2:
	                rewardQuest(pc, Lineage.rq2, Lineage.rqc2, Lineage.rqExp2);
	                break;
	            case 3:
	                rewardQuest(pc, Lineage.rq3, Lineage.rqc3, Lineage.rqExp3);
	                break;
	            default:
	                break;
	            }
	            //완료후 처리 
	            pc.setRandomQuestkill(0);
	            pc.setRandomQuestCount(pc.getRandomQuestCount() + 1);
	            pc.setRadomQuest(0);
	            pc.setRandomQuestPlay(0);
	            AccountDatabase.updateuQuestKill((int) pc.getObjectId());
	            AccountDatabase.updatequestcount(pc.getRandomQuestCount(), (int) pc.getObjectId());
	            ChattingController.toChatting(pc, String.format("[일일 퀘스트] 보상이 지급되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
	        } else {
	            ChattingController.toChatting(pc, String.format("남은 몬스터 %d 마리 ", monstkill - pc.getRandomQuestkill()), Lineage.CHATTING_MODE_MESSAGE);
	        }
	    }
	    
	    showHtml(pc);
	}
	
	private void rewardQuest(PcInstance pc, String itemKey, int itemCount, int exp) {
	    ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(itemKey));
	    pc.setExp(pc.getExp() + exp);
	    ii.setCount(itemCount);
	    pc.toGiveItem(null, ii, ii.getCount());
	    ChattingController.toChatting(pc, "인벤토리에 퀘스트 보상 아이템이 지급되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	    pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 32006));
	}
}
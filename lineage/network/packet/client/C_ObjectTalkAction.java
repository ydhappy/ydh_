package lineage.network.packet.client;

import goldbitna.item.ItemChange;
import goldbitna.item.PetAdoptionDocument;
import goldbitna.item.RandomDollOption;
import goldbitna.item.RingOfTransform;
import lineage.database.BackgroundDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ExchangeController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.RobotClanController;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.all_night.EnchantRecovery;
import lineage.world.object.item.all_night.ClassChangeTicket;
import lineage.world.object.item.yadolan.HuntingZoneTeleportationBook;
import lineage.world.object.npc.RobotClan;

public class C_ObjectTalkAction extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_ObjectTalkAction(data, length);
		else
			((C_ObjectTalkAction) bp).clone(data, length);
		return bp;
	}

	public C_ObjectTalkAction(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
	    // 버그 방지: pc가 null이거나 월드에서 삭제되었거나 읽기 불가능한 경우 바로 반환
	    if (pc == null || pc.isWorldDelete() || !isRead(4))
	        return this;

	    try {
	        int objId = readD();    // 오브젝트 ID 읽기
	        String action = readS(); // 행동 읽기
	        String type = readS();   // 타입 읽기
	        object o = pc.findInsideList(objId); // 오브젝트 리스트에서 찾기
			
	        // 자동사냥 아이템
	        if (action.contains("autohunt-")) {
	            pc.toTalk(pc, action, type, this);
	            return this;
	        }

	        // 자동사냥 F1
	        if (action.contains("autoHunt-")) {
	            NpcSpawnlistDatabase.autoHunt.toTalk(pc, null);
	            pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 7788));
	            return this;
	        }
	        
	        // 자동물약
	        if (action.contains("autoPotion-")) {
	            NpcSpawnlistDatabase.autoPotion.toTalk(pc, null);
	            pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 7788));
	            return this;
	        }

	        // 자동판매
	        if (action.contains("autoSellItem-")) {
	            NpcSpawnlistDatabase.AutoSellItem.toTalk(pc, null);
	            pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 7788));
	            return this;
	        }

	        // 랭킹게시판
	        if (action.contains("rankcheck-")) {
	            BoardInstance b = BackgroundDatabase.getRankBoard();
	            b.toClick(pc, this);
	            return this;
	        }

	        // 출석체크
	        if (action.contains("playcheck-")) {
	            if (pc.getDaycount() > Lineage.lastday) {
	                ChattingController.toChatting(pc, "출석체크를 전부 완료하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
	            } else {
	                NpcSpawnlistDatabase.playcheck.toTalk(pc, action, type, this);
	                pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 7788));
	                return this;
	            }
	            return this;
	        }

	        // 보스 시간표
	        if (action.contains("bossList-")) {
	            NpcSpawnlistDatabase.bosstime.toTalk(pc, null);
	            pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 7788));
	            return this;
	        }

	        // 보스 시간표의 b00~b99 클릭 처리 먼저 수행
	        if (action != null && action.matches("^b\\d{2}$")) {
	        	NpcSpawnlistDatabase.bosstime.toTalk(pc, action, type, this);
	        	return this;
	        }

	        // 퀘스트
	        if (action.contains("kquest2-")) {
	            if (pc.getQuestChapter() >= Lineage.lastquest) {
	                ChattingController.toChatting(pc, "퀘스트를 전부 완료했습니다.", Lineage.CHATTING_MODE_MESSAGE);
	            } else {
	                NpcSpawnlistDatabase.quest.toTalk(pc, action, type, this);
	                return this;
	            }
	            return this;
	        }

	        // 아이템 관련
	        if (action.contains("ChangeOptions")) {
	            RandomDollOption doll = pc.getInventory().is부여주문서(pc, objId);
	            if (doll != null) {
	                doll.toTalk(pc, action, type, this);
	                return this;
	            } else {
	                ChattingController.toChatting(pc, "[알림] 인형 랜덤옵션 부여 주문서가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            return this;
	        }
	        
	        if (action.contains("kicheck-")) {
	            ItemChange kitemc = pc.getInventory().is아이템변경주문서(pc, objId);
	            if (kitemc != null) {
	                kitemc.toTalk(pc, action, type, this);
	                return this;
	            } else {
	                ChattingController.toChatting(pc, "[알림] 주문서가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            return this;
	        }
			
	        // 시세 검색
	        if (objId == PcMarketController.marketPriceNPC.getObjectId()) {
	            PcMarketController.marketPriceNPC.toTalk(pc, action, type, this);
	            return this;
	        }
	        
	        if (objId == NpcSpawnlistDatabase.marketNpc.getObjectId()) {
	            NpcSpawnlistDatabase.marketNpc.toTalk(pc, action, type, this);
	            return this;
	        }

	        // 장비 스왑
	        if (objId == NpcSpawnlistDatabase.itemSwap.getObjectId()) {
	            NpcSpawnlistDatabase.itemSwap.toTalk(pc, action, type, this);
	            return this;
	        }

	        // 자동 물약
	        if (objId == NpcSpawnlistDatabase.autoPotion.getObjectId()) {
	            NpcSpawnlistDatabase.autoPotion.toTalk(pc, action, type, this);
	            return this;
	        }
			
	        // 거래소
			if (objId == ExchangeController.ExchangeNpc.getObjectId()) {
				ExchangeController.ExchangeNpc.toTalk(pc, action, type, this);
				return this;
			} 
			
	        //
	        if (objId == NpcSpawnlistDatabase.gmteleporter.getObjectId()) {
	            NpcSpawnlistDatabase.gmteleporter.toTalk(pc, action, type, this);
	            return this;
	        }
	        if (objId == NpcSpawnlistDatabase.gmagit.getObjectId()) {
	            NpcSpawnlistDatabase.gmagit.toTalk(pc, action, type, this);
	            return this;
	        }
	        
	        // 무인혈맹
	        RobotClan ci = RobotClanController.find무인혈맹(objId);
	        if (ci != null) {
	            ci.toTalk(pc, action, type, this);
	            return this;
	        }

	        // 오브젝트가 null이 아니고, PC가 GM이거나 투명하지 않은 경우
	        if (o != null && (pc.getGm() > 0 || !pc.isTransparent())) {
	            o.toTalk(pc, action, type, this);
	            return this;
	        }

	        // 인첸트 복구 주문서
	        if (pc.getInventory() != null) {
	            EnchantRecovery enchant = pc.getInventory().is인첸트복구주문서(pc, objId);
	            if (enchant != null) {
	                pc.isAutoSellAdding = false;
	                pc.isAutoSellDeleting = false;
	                enchant.toTalk(pc, action, type, this);
	                return this;
	            }

	            // 클래스 변경 주문서
	            ClassChangeTicket classChange = pc.getInventory().is클래스변경주문서(pc, objId);
	            if (classChange != null) {
	                pc.isAutoSellAdding = false;
	                pc.isAutoSellDeleting = false;
	                classChange.toTalk(pc, action, type, this);
	                return this;
	            }

	            // 사냥터 텔레포트 책
	            HuntingZoneTeleportationBook book = pc.getInventory().istellbook(pc, objId);
	            if (book != null) {
	                pc.isAutoSellAdding = false;
	                pc.isAutoSellDeleting = false;
	                book.toTalk(pc, action, type, this);
	                return this;
	            }

				if (action.contains("polylist1-")) {
					RingOfTransform rot = pc.getInventory().isRingPoly(pc, objId);
					rot.toTalk(pc, action, type, this);
					return this;
				}

				PetAdoptionDocument pet = pc.getInventory().isPetAdoptionDocument(pc, objId);
				if (pet != null) {					
					pet.toTalk(pc, action, type, this);
					return this;
				}
				
	            try {
	                int selectedAction = Integer.parseInt(action);

	                // 자동 판매 리스트에서 선택된 액션 처리
	                if (selectedAction >= 0 && selectedAction <= 100) {
	                    if (pc.isAutoSellDeleting) {
	                        if (pc.isAutoSellList.get(selectedAction) != null) {
	                            String valueAtIndex = pc.isAutoSellList.get(selectedAction);
	                            pc.isAutoSellList.remove(valueAtIndex);
	                            NpcSpawnlistDatabase.AutoSellItem.toTalk(pc, null);

	                            ChattingController.toChatting(pc, String.format("[자동판매 알림] '%s' 목록에서 삭제", valueAtIndex), Lineage.CHATTING_MODE_MESSAGE);
	                            return this;
	                        } else {
	                            ChattingController.toChatting(pc, "[자동판매 알림] 존재하지 않는 아이템입니다", 20);
	                        }
	                        pc.isAutoSellDeleting = false;
	                        return this;
	                    }
	                }

	            } catch (NumberFormatException e) {
	                // 숫자 포맷 예외 처리
	            }

	            // 자동 판매 아이템 처리
	            if (objId == NpcSpawnlistDatabase.AutoSellItem.getObjectId()) {
	                NpcSpawnlistDatabase.AutoSellItem.toTalk(pc, action, type, this);
	                return this;
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        // 예외 처리
	    }

	    return this;
	}
}

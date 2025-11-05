package lineage.world.object.npc.craft;

import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.*;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Small_Box extends CraftInstance {

	public Small_Box(Npc npc) {
		super(npc);
	}
	
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		
		ItemInstance Treasure_Map = pc.getInventory().find(ItemDatabase.find("작은 보물지도[0]"));
		ItemInstance Treasure_Map1 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[1]"));
		ItemInstance Treasure_Map2 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[2]"));
		ItemInstance Treasure_Map3 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[3]"));
		ItemInstance Treasure_Map4 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[4]"));
		ItemInstance Treasure_Map5 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[5]"));
		ItemInstance Treasure_Map6 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[6]"));
		ItemInstance Treasure_Map7 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[7]"));
		ItemInstance Treasure_Map8 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[8]"));
		ItemInstance Treasure_Map9 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[9]"));
		
		switch (getNpc().getNameIdNumber()) {
		case 19590: // 등대
			if (Treasure_Map != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
			//2단계
		case 19591: // 선착장
			if (Treasure_Map1 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxa"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19592: // 불탄
			if (Treasure_Map2 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxb"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19593: // 참치
			if (Treasure_Map3 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxc"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
			//3단계
		case 19594: // 다리가 두 개인 섬의 한 가운데.
			if (Treasure_Map4 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxd"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19595: // 진흙이 가득 찬 웅덩이
			if (Treasure_Map5 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxe"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19596: // 엄지와 검지가 빠진 거인의 손가락
			if (Treasure_Map6 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxf"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19597: // 화단 안의 이상한 기둥 뒤
			if (Treasure_Map7 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxg"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19598: // 다리가 한 개인 섬의 구석
			if (Treasure_Map8 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxh"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		case 19599: // 물레방아 도는데…
			if (Treasure_Map9 != null) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptboxi"));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox0"));
			}
			break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		
		ItemInstance Treasure_Map = pc.getInventory().find(ItemDatabase.find("작은 보물지도[0]"));
		ItemInstance Treasure_Map1 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[1]"));
		ItemInstance Treasure_Map2 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[2]"));
		ItemInstance Treasure_Map3 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[3]"));
		ItemInstance Treasure_Map4 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[4]"));
		ItemInstance Treasure_Map5 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[5]"));
		ItemInstance Treasure_Map6 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[6]"));
		ItemInstance Treasure_Map7 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[7]"));
		ItemInstance Treasure_Map8 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[8]"));
		ItemInstance Treasure_Map9 = pc.getInventory().find(ItemDatabase.find("작은 보물지도[9]"));
	
	    if (action.equalsIgnoreCase("1")) {
	        if (Treasure_Map != null) {
	            // 작은 주머니 아이템 목록 생성
	            List<String> pouches = Arrays.asList("작은 주머니[1]", "작은 주머니[2]", "작은 주머니[3]");

	            int[] weights = {1, 1, 1};

	            int totalWeight = 0;
	            for (int weight : weights) {
	                totalWeight += weight;
	            }

	            // 가중치에 따라 아이템 선택
	            Random rand = new Random();
	            int randomValue = rand.nextInt(totalWeight);
	            int cumulativeWeight = 0;
	            String selectedPouch = null;
	            for (int i = 0; i < pouches.size(); i++) {
	                cumulativeWeight += weights[i];
	                if (randomValue < cumulativeWeight) {
	                    selectedPouch = pouches.get(i);
	                    break;
	                }
	            }

	            // 선택된 주머니 아이템을 지급
	            CraftController.tuCraft(this, pc, ItemDatabase.find(selectedPouch), 1, true);
	            ChattingController.toChatting(pc, String.format("작은 상자가 당신에게 작은 주머니를 주었습니다."), Lineage.CHATTING_MODE_MESSAGE);
	            pc.getInventory().remove(Treasure_Map, true);
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox1"));
	        }
	        //2단계
		}else if (action.equalsIgnoreCase("2")) {
	        if (Treasure_Map1 != null) {
	            // 작은 주머니 아이템 목록 생성
		        List<String> pouches1 = Arrays.asList("작은 주머니[4]", "작은 주머니[5]", "작은 주머니[6]", "작은 주머니[7]", "작은 주머니[8]", "작은 주머니[9]");

		        int[] weights = {1, 1, 1, 1, 1, 1}; 

		        int totalWeight = 0;
		        for (int weight : weights) {
		            totalWeight += weight;
		        }

	            // 가중치에 따라 아이템 선택
	            Random rand = new Random();
	            int randomValue = rand.nextInt(totalWeight);
	            int cumulativeWeight = 0;
	            String selectedPouch = null;
	            for (int i = 0; i < pouches1.size(); i++) {
	                cumulativeWeight += weights[i];
	                if (randomValue < cumulativeWeight) {
	                    selectedPouch = pouches1.get(i);
	                    break;
	                }
	            }

	            // 선택된 주머니 아이템을 지급
	            CraftController.tuCraft(this, pc, ItemDatabase.find(selectedPouch), 1, true);
	            ChattingController.toChatting(pc, String.format("작은 상자가 당신에게 작은 주머니를 주었습니다."), Lineage.CHATTING_MODE_MESSAGE);
	            pc.getInventory().remove(Treasure_Map1, true);
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox1"));
	        }
		}else if (action.equalsIgnoreCase("3")) {
		    if (Treasure_Map2 != null) {
		        // 작은 주머니 아이템 목록 생성
		        List<String> pouches2 = Arrays.asList("작은 주머니[4]", "작은 주머니[5]", "작은 주머니[6]", "작은 주머니[7]", "작은 주머니[8]", "작은 주머니[9]");

		        int[] weights = {1, 1, 1, 1, 1, 1}; 

		        int totalWeight = 0;
		        for (int weight : weights) {
		            totalWeight += weight;
		        }

		        // 가중치에 따라 아이템 선택
		        Random rand = new Random();
		        int randomValue = rand.nextInt(totalWeight);
		        int cumulativeWeight = 0;
		        String selectedPouch = null;
		        for (int i = 0; i < pouches2.size(); i++) {
		            cumulativeWeight += weights[i];
		            if (randomValue < cumulativeWeight) {
		                selectedPouch = pouches2.get(i);
		                break;
		            }
		        }

		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find(selectedPouch), 1, true);
		        ChattingController.toChatting(pc, String.format("작은 상자가 당신에게 작은 주머니를 주었습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map2, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox1"));
		    }
		}else if (action.equalsIgnoreCase("4")) {
	        if (Treasure_Map3 != null) {
	            // 작은 주머니 아이템 목록 생성
		        List<String> pouches3 = Arrays.asList("작은 주머니[4]", "작은 주머니[5]", "작은 주머니[6]", "작은 주머니[7]", "작은 주머니[8]", "작은 주머니[9]");

	            int[] weights = {1, 1, 1, 1, 1, 1};

	            int totalWeight = 0;
	            for (int weight : weights) {
	                totalWeight += weight;
	            }

	            // 가중치에 따라 아이템 선택
	            Random rand = new Random();
	            int randomValue = rand.nextInt(totalWeight);
	            int cumulativeWeight = 0;
	            String selectedPouch = null;
	            for (int i = 0; i < pouches3.size(); i++) {
	                cumulativeWeight += weights[i];
	                if (randomValue < cumulativeWeight) {
	                    selectedPouch = pouches3.get(i);
	                    break;
	                }
	            }

	            // 선택된 주머니 아이템을 지급
	            CraftController.tuCraft(this, pc, ItemDatabase.find(selectedPouch), 1, true);
	            ChattingController.toChatting(pc, String.format("작은 상자가 당신에게 작은 주머니를 주었습니다."), Lineage.CHATTING_MODE_MESSAGE);
	            pc.getInventory().remove(Treasure_Map3, true);
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox1"));
	        }
	        //3단계
		}else if (action.equalsIgnoreCase("d")) {
		    if (Treasure_Map4 != null) {
		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find("할아버지의 보물"), 1, true);
		        ChattingController.toChatting(pc, String.format("할아버지의 보물을 찾았습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map4, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox2"));
		    }
		}else if (action.equalsIgnoreCase("e")) {
		    if (Treasure_Map5 != null) {
		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find("할아버지의 보물"), 1, true);
		        ChattingController.toChatting(pc, String.format("할아버지의 보물을 찾았습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map5, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox2"));
		    }
		}else if (action.equalsIgnoreCase("f")) {
		    if (Treasure_Map6 != null) {
		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find("할아버지의 보물"), 1, true);
		        ChattingController.toChatting(pc, String.format("할아버지의 보물을 찾았습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map6, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox2"));
		    }
		}else if (action.equalsIgnoreCase("g")) {
		    if (Treasure_Map7 != null) {
		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find("할아버지의 보물"), 1, true);
		        ChattingController.toChatting(pc, String.format("할아버지의 보물을 찾았습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map7, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox2"));
		    }
		}else if (action.equalsIgnoreCase("h")) {
		    if (Treasure_Map8 != null) {
		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find("할아버지의 보물"), 1, true);
		        ChattingController.toChatting(pc, String.format("할아버지의 보물을 찾았습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map8, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox2"));
		    }
		}else if (action.equalsIgnoreCase("i")) {
		    if (Treasure_Map9 != null) {
		        // 선택된 주머니 아이템을 지급
		        CraftController.tuCraft(this, pc, ItemDatabase.find("할아버지의 보물"), 1, true);
		        ChattingController.toChatting(pc, String.format("할아버지의 보물을 찾았습니다."), Lineage.CHATTING_MODE_MESSAGE);
		        pc.getInventory().remove(Treasure_Map9, true);
		        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "maptbox2"));
		    } 
		}
	}
}
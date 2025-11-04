package lineage.world.controller;


import lineage.share.Lineage;
import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;


public class DonationController {

	static public void toGiveandTake(PcInstance pc, long amount) {
	    // 아이템 이름과 수량을 결정합니다.
	    String itemName = "후원코인"; // 지급할 아이템의 이름 (예시)
	    int itemCount = (int) amount; // 후원 금액에 따라 지급할 수량 결정
	    int enchantLevel = 0; // 아이템의 강화 레벨 (예시)
	    int bress = 0; // 아이템의 축복 값 (예시)

	    // 아이템을 생성하는 메서드 호출
	    toItem(pc, itemName, itemCount, enchantLevel, bress);
	}

	// 아이템 생성 메서드
	static private void toItem(PcInstance pc, String itemName, int count, int en, int bress) {
		Item item = ItemDatabase.find2(itemName);
		
		 if (item != null) {
		        ItemInstance temp = pc.getInventory().find(item.getItemCode(), item.getName(), bress, item.isPiles());
		        
		        if (temp != null && (temp.getBless() != bress || temp.getEnLevel() != en))
		            temp = null;

		        if (temp == null) {
		            // 겹칠 수 있는 아이템이 존재하지 않을 경우.
		            if (item.isPiles()) {
		                temp = ItemDatabase.newInstance(item);
		                temp.setObjectId(ServerDatabase.nextItemObjId());
		                temp.setBless(bress);
		                temp.setEnLevel(en);
		                temp.setCount(count);
		                temp.setDefinite(true);
		                pc.getInventory().append(temp, true);
		            } else {
		                for (int idx = 0; idx < count; idx++) {
		                    temp = ItemDatabase.newInstance(item);
		                    temp.setObjectId(ServerDatabase.nextItemObjId());
		                    temp.setBless(bress);
		                    temp.setEnLevel(en);
		                    temp.setDefinite(true);
		                    pc.getInventory().append(temp, true);
		                }
		            }
		        } else {
		            // 겹치는 아이템이 존재할 경우.
		            pc.getInventory().count(temp, temp.getCount() + count, true);
		        }
		            ChattingController.toChatting(pc, String.format("%s(%d)이 지급 되었습니다.", item.getName(), count),Lineage.CHATTING_MODE_MESSAGE);
		            
		            
		    } else {
		        ChattingController.toChatting(pc, String.format("%s 지급에 실패 하였습니다.", itemName), Lineage.CHATTING_MODE_MESSAGE);
		    }
		}
	}
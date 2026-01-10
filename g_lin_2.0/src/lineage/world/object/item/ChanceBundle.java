package lineage.world.object.item;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.ItemChanceBundle;
import lineage.database.ItemChanceBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcRobotInstance;

public class ChanceBundle extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ChanceBundle();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
//		ItemChanceBundleDatabase.reload();
		if (cha.getInventory() != null && cha.getInventory().getList().size() >= Lineage.inventory_max) {
			ChattingController.toChatting(cha, "인벤토리가 가득찼습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		// 아이템 지급.
		int random = 0;
		int randomCount = 0;
		//double probability = Math.random();
		List<ItemChanceBundle> list = new ArrayList<ItemChanceBundle>();
		ItemChanceBundleDatabase.find(list, getItem().getName());
		
		//야도란 찬스아이템 보정
//		if(list.get(random).getCount() > 0){
//			list.remove(list.get(random).getName());
//			ChattingController.toChatting(cha, String.format("나 많이 나와서 안나올거야"+list.size()), Lineage.CHATTING_MODE_MESSAGE);
//		}
		if (list.size() < 1)
			return;

		for (;;) {
			if (randomCount++ > 50)
				break;
			
//			if (randomCount++ > list.size())
//				probability = Math.random();
			
			random = Util.random(0, list.size() - 1);
			
		
			if (list.get(random).getItemCountMin() < 1)
				break;
			
			double probability = Math.random();
			if (probability < list.get(random).getItemChance()) {
				if (cha instanceof PcRobotInstance) {
					// 수량 하향.
					cha.getInventory().count(this, getCount() - 1, true);
					break;
				}
				
				ItemChanceBundle ib = list.get(random);
				Item i = ItemDatabase.find_ItemCode(ib.getItemCode());
				
			
				if (i != null) {
					ItemInstance temp = cha.getInventory().find(i.getItemCode(), i.getName(), ib.getItemBless(), i.isPiles());
					int count = Util.random(ib.getItemCountMin(), ib.getItemCountMax());

					if (temp != null && (temp.getBless() != list.get(random).getItemBless() || temp.getEnLevel() != ib.getItemEnchant()))
						temp = null;

					if (temp == null) {
						// 겹칠수 있는 아이템이 존재하지 않을경우.
						if (i.isPiles()) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(ib.getItemBless());
							temp.setEnLevel(ib.getItemEnchant());
							temp.setCount(count);
							temp.setDefinite(ib.isDefine());
					
				            // 🔽 기간제 아이템 처리 메서드 호출
				            applyItemDuration(cha, temp, i.getName());
				            
							cha.getInventory().append(temp, true);
						} else {
							for (int idx = 0; idx < count; idx++) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setBless(ib.getItemBless());
								temp.setEnLevel(ib.getItemEnchant());
								temp.setDefinite(ib.isDefine());

					            // 🔽 기간제 아이템 처리 메서드 호출
					            applyItemDuration(cha, temp, i.getName());
					            
								cha.getInventory().append(temp, true);
							}
						}
					} else
						// 겹치는 아이템이 존재할 경우.

					cha.getInventory().count(temp, temp.getCount() + count, true);
					
					if (Lineage.is_item_drop_msg_item && i != null && this != null && getItem() != null) {
						ItemDropMessageDatabase.sendMessage(cha, i.getName(), getItem().getName());
					}
		
					
					// 알림.
					ChattingController.toChatting(cha, String.format("%s에서 %s 획득하였습니다.",  getItem().getName(), Util.getStringWord(temp.getItem().getName(), "을", "를")), Lineage.CHATTING_MODE_MESSAGE);
					
				
						 cha.getInventory().count(this, getCount() - 1, true);
					 // 수량 하향.
					
				}
				break;
			}
		}
	}
	
	/**
	 * 기간제 아이템 설정 (아이템 이름 기반)
	 * - "1일", "3일", "7일", "30일", 또는 특정 마법인형 문자열이 들어있는지 검사
	 * - KST 기준으로 현재 시각 + daysToAdd 일 -> epoch millis -> itemTimek 저장
	 */
	private static void applyItemDuration(object o, ItemInstance temp, String itemName) {
	    int daysToAdd = 0;

	    // 아이템 이름에 "1일", "3일", "7일", "30일"이 포함되어 있으면 해당 일수
	    if (itemName.contains("1일")) {
	        daysToAdd = 1;
	    } else if (itemName.contains("3일")) {
	        daysToAdd = 3;
	    } else if (itemName.contains("7일")) {
	        daysToAdd = 7;
	    } else if (itemName.contains("30일")) {
	        daysToAdd = 30;
	    } 

	    if (daysToAdd > 0) {
	        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
	        ZonedDateTime futureKST = nowKST.plusDays(daysToAdd);

	        long epochMillis = futureKST.toInstant().toEpochMilli();
	        temp.setItemTimek(Long.toString(epochMillis));

	        // 안내 메시지
	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
	        String dateString = futureKST.format(fmt);

	        // o가 PcInstance 라면 캐스팅 필요
	        // if (o instanceof PcInstance) { ... }
	        // 여기서는 단순화하여 o를 그대로 사용
	        String chatMsg = String.format("%s 아이템은 %s까지 사용 가능합니다.",
	            temp.getItem().getName(), dateString);
	        ChattingController.toChatting(o, chatMsg, Lineage.CHATTING_MODE_MESSAGE);
	    }
	}
}

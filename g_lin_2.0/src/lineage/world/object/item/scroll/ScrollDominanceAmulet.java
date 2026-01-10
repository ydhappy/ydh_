package lineage.world.object.item.scroll;

import lineage.bean.database.ItemTeleport;
import lineage.database.ItemTeleportDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

/**
 * 오만의 탑 지배 부적 클래스
 */

public class ScrollDominanceAmulet extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollDominanceAmulet();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (!isLvCheck(cha) || !isClick(cha))
			return;

		try {
			String itemName = getItem().getName();
			String searchKey = extractOmanFloorName(itemName);

			if (searchKey != null) {
				ItemTeleport it = findTeleportData(searchKey);

				if (it != null) {
					ChattingController.toChatting(cha, searchKey + " 이동 했습니다.", Lineage.CHATTING_MODE_MESSAGE);
					ItemTeleportDatabase.toTeleport(it, cha, true);
				} else {
					//ChattingController.toChatting(cha, "알림: DB에서 [" + searchKey + "] 좌표를 찾지 못했습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("오만의탑지배부적 에러 : %s\r\n", e.toString());
		}
	}

	/**
	 * 아이템 이름에서 "오만의 탑 *층" 또는 "오만의 탑 정상" 까지만 추출
	 */
	private String extractOmanFloorName(String name) {
		if (name == null || !name.contains("오만의 탑")) return null;

		int start = name.indexOf("오만의 탑");
		int end = -1;

		if (name.contains("층")) {
			end = name.indexOf("층") + 1;
		} else if (name.contains("정상")) {
			end = name.indexOf("정상") + 2;
		}

		if (end != -1) {
			return name.substring(start, end).trim();
		}
		return null;
	}

	/**
	 * 공백을 제거하고 DB의 name 컬럼과 비교하여 일치하는 데이터 탐색
	 */
	private ItemTeleport findTeleportData(String searchKey) {
		String cleanSearchKey = searchKey.replace(" ", "");

		for (ItemTeleport it : ItemTeleportDatabase.getList()) {
			if (it != null && it.getName() != null) {
				String cleanDbName = it.getName().replace(" ", "");
				
				if (cleanDbName.contains(cleanSearchKey) || cleanSearchKey.contains(cleanDbName)) {
					return it;
				}
			}
		}
		return null;
	}
}
package lineage.world.object.item.scroll;

import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.LocationController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollLabeledVerrYedHorae extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollLabeledVerrYedHorae();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {

		// "마을 이동 부적" 확인
		if (getItem() != null && getItem().getName().equalsIgnoreCase("마을 이동 부적")) {
			if (LocationController.isTeleportVerrYedHoraeZone(cha, true)) {
				// 이동 처리
				TeleportHomeDatabase.toLocation(cha);
				cha.toPotal(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap());
			}
		} else {
			// 일반 아이템 사용 시 처리
			if (LocationController.isTeleportVerrYedHoraeZone(cha, true)) {
				// 이동 처리
				TeleportHomeDatabase.toLocation(cha);
				cha.toPotal(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap());

				// 아이템 개수 감소
				cha.getInventory().count(this, getCount() - 1, true);
			}
		}

	}
}

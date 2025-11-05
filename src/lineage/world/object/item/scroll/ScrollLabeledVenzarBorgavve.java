package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Teleport;

public class ScrollLabeledVenzarBorgavve extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollLabeledVenzarBorgavve();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		// 메모리 제거 구문으로 인해 bless 값을 임시 저장
		int bress = this.bless;

		if (getItem() != null && getItem().getName().equalsIgnoreCase("무한 순간이동 룬") || getItem().getName().equalsIgnoreCase("무한 순간이동 룬(3일)")) {
			// 이동 가능 여부 확인
			if (Teleport.onBuff(cha, cbp, bress, true, true)) {
				// 이동 실행
				cha.toPotal(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap());
			}
		} else {
			// 이동 가능 여부 확인
			if (Teleport.onBuff(cha, cbp, bress, true, true)) {
				// 이동 실행
				cha.toPotal(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap());

				// 아이템 개수 감소
				cha.getInventory().count(this, getCount() - 1, true);
			}
		}

	}
}
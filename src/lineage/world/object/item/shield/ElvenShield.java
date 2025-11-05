package lineage.world.object.item.shield;

import lineage.bean.lineage.Inventory;
import lineage.database.SkillDatabase;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Haste;

public class ElvenShield extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ElvenShield();
		return item;
	}

	@Override
	public void toEquipped(Character cha, Inventory inv) {
		super.toEquipped(cha, inv);

		if (cha.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
			if (equipped) {
				// 적용
				cha.setDynamicMr(cha.getDynamicMr() + 5);
			} else {
				// 해제
				cha.setDynamicMr(cha.getDynamicMr() - 5);
			}
		}
	}
}

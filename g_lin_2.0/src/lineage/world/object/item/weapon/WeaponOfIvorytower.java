package lineage.world.object.item.weapon;

import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class WeaponOfIvorytower extends ItemWeaponInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if(item == null)
			item = new WeaponOfIvorytower();
		return item;
	}
}
package lineage.world.object.item.armor;

import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class ArmorOfIvorytower extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if(item == null)
			item = new ArmorOfIvorytower();
		return item;
	}
}
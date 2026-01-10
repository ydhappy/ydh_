package lineage.bean.lineage;

import lineage.world.object.instance.ItemInstance;

public class PcTradeShopAdd {
	ItemInstance item;
	long count;
	long price;
	
	public ItemInstance getItem() {
		return item;
	}
	public void setItem(ItemInstance item) {
		this.item = item;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
}

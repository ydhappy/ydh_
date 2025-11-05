package lineage.bean.database;

public class PcTradeShopSell {
	private long uid;
	private long pcObjId;
	private String pcName;
	private String itemName;
	private String adenType;
	private long price;
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public long getPcObjId() {
		return pcObjId;
	}
	public void setPcObjId(long pcObjId) {
		this.pcObjId = pcObjId;
	}
	public String getPcName() {
		return pcName;
	}
	public void setPcName(String pcName) {
		this.pcName = pcName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getAdenType() {
		return adenType;
	}
	public void setAdenType(String adenType) {
		this.adenType = adenType;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
}

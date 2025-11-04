package lineage.bean.database;

public class PcTradeShop {
	private long pcObjectId;
	private String pcName;
	private long itemObjectId;
	private String itemName;
	private long count;
	private int enLevel;
	private int bless;
	private boolean definite;
	private String adenType; // 화폐타입
	private long price;		// 판매할 가격
	private long petObjId;
	private String petName;
	private int petLevel;
	private int petClassId;
	private int petHp;
	private long sellTime;
	private long delay;
	private int 무기속성;
	
	public long getPcObjectId() {
		return pcObjectId;
	}
	public void setPcObjectId(long pcObjectId) {
		this.pcObjectId = pcObjectId;
	}
	public String getPcName() {
		return pcName;
	}
	public void setPcName(String pcName) {
		this.pcName = pcName;
	}
	public long getItemObjectId() {
		return itemObjectId;
	}
	public void setItemObjectId(long itemObjectId) {
		this.itemObjectId = itemObjectId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public int getEnLevel() {
		return enLevel;
	}
	public void setEnLevel(int enLevel) {
		this.enLevel = enLevel;
	}
	public int getBless() {
		return bless;
	}
	public void setBless(int bless) {
		this.bless = bless;
	}
	public boolean isDefinite() {
		return definite;
	}
	public void setDefinite(boolean definite) {
		this.definite = definite;
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
	public long getPetObjId() {
		return petObjId;
	}
	public void setPetObjId(long petObjId) {
		this.petObjId = petObjId;
	}
	public String getPetName() {
		return petName;
	}
	public void setPetName(String petName) {
		this.petName = petName;
	}
	public int getPetLevel() {
		return petLevel;
	}
	public void setPetLevel(int petLevel) {
		this.petLevel = petLevel;
	}
	public int getPetClassId() {
		return petClassId;
	}
	public void setPetClassId(int petClassId) {
		this.petClassId = petClassId;
	}
	public int getPetHp() {
		return petHp;
	}
	public void setPetHp(int petHp) {
		this.petHp = petHp;
	}
	public long getSellTime() {
		return sellTime;
	}
	public void setSellTime(long sellTime) {
		this.sellTime = sellTime;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public int get무기속성() {
		return 무기속성;
	}
	public void set무기속성(int 무기속성) {
		this.무기속성 = 무기속성;
	}
}

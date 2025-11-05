package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class RobotDrop {
	
	private String className;
	private int ItemCode;
	private String ItemName;
	private int ItemBress;
	private int CountMin;
	private int CountMax;
	private int Chance;
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public int getItemCode() {
		return ItemCode;
	}
	
	public void setItemCode(int itemCode) {
		ItemCode = itemCode;
	}
	
	public String getItemName() {
		return ItemName;
	}
	
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
	
	public int getItemBress() {
		return ItemBress;
	}
	
	public void setItemBress(int itemBress) {
		ItemBress = itemBress;
	}
	
	public int getCountMin() {
		return CountMin;
	}
	
	public void setCountMin(int countMin) {
		CountMin = countMin;
	}
	
	public int getCountMax() {
		return CountMax;
	}
	
	public void setCountMax(int countMax) {
		CountMax = countMax;
	}
	
	public int getChance() {
		return Chance;
	}
	
	public void setChance(int chance) {
		Chance = chance;
	}
}

package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class NpcTeleport {
	public class Location {
		public int x;
		public int y;
		public int map;
	}
	
	private String name;
	private String action;
	private int idx;
	private int num;
	private int level;
	private int checkMap;
	private int x;
	private int y;
	private int map;
	private int price;
	
	private boolean israndom;

	private List<Location> list_goto = new ArrayList<Location>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCheckMap() {
		return checkMap;
	}
	public void setCheckMap(int checkMap) {
		this.checkMap = checkMap;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getMap() {
		return map;
	}
	public void setMap(int map) {
		this.map = map;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public boolean isRandomLoc(){
		return israndom;
	}
	public void setRandomLoc(boolean flag){
		israndom = flag;
	}
	public List<Location> getListGoto() {
		return list_goto;
	}
	public void appendLocation(String loc){
		try {
			Location l = new Location();
			StringTokenizer tok = new StringTokenizer(loc);
			l.x = Integer.valueOf(tok.nextToken());
			l.y = Integer.valueOf(tok.nextToken());
			l.map = Integer.valueOf(tok.nextToken());
			
			list_goto.add(l);
		} catch (Exception e) { }
	}
	
}

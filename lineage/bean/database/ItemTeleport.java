package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ItemTeleport {
	public class Location {
		public int x;
		public int y;
		public int map;
	}
	private int Uid;
	private String Name;
	private int X;
	private int Y;
	private int Map;
	private int range;
	private int Heading;
	private int Level;
	private int ClassType;
	private boolean remove;
	private boolean israndom;
	
	private List<Location> list_goto = new ArrayList<Location>();
	
	public int getUid() {
		return Uid;
	}
	public void setUid(int uid) {
		Uid = uid;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public int getMap() {
		return Map;
	}
	public void setMap(int map) {
		Map = map;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getHeading() {
		return Heading;
	}
	public void setHeading(int heading) {
		Heading = heading;
	}
	public int getLevel() {
		return Level;
	}
	public void setLevel(int level) {
		Level = level;
	}
	public int getClassType() {
		return ClassType;
	}
	public void setClassType(int classType) {
		ClassType = classType;
	}
	public boolean isRemove() {
		return remove;
	}
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	
	public List<Location> getListGoto() {
		return list_goto;
	}
	
	public boolean isRandomLoc(){
		return israndom;
	}
	public void setRandomLoc(boolean flag){
		israndom = flag;
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

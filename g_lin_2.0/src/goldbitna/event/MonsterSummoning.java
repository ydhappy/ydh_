package goldbitna.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.world.controller.MonsterSummonController;
import lineage.world.object.instance.MonsterInstance;

public class MonsterSummoning {


	
	private String type;									// 이벤트 위치
	private String name;									// 이름.
	private int x;											// 몬스터 소환이 진행되는 좌표.
	private int y;											// 몬스터 소환이 진행되는 좌표.
	private int map;										// 몬스터 소환이 진행되는 맵아이디.
	private int maxStage;									// 최대 군 갯수.
	private int lastTime;								    // 최근에 시작한 시간값. 중복동작 방지하기 위해.
	private int[] timeStart;								// 이벤트 시작시간 목록.
	private int[] timeEnd;									// 각 스테이지에 따른 종료 시간 목록.
	private int[] timeCool;									// 각 스테이지에 따른 쿨타임 목록.
	private int[][] stageCount;								// 각 스테이지에 나오는 몬스터 개체수 갯수 목록.
	private int[][] stageItemCount;							// 각 군에 나오는 아이템 갯수 목록.
	private int[] bossCount;								// 각 스테이지 보스 개체수 갯수 목록.
	private Map<Integer, List<String>> list;				// 각 스테이지에 따라 스폰되는 몬스터 목록.
	private Map<Integer, List<String>> listItem;			// 각 스테이지에 따라 드랍할 아이템 목록.
	private List<String> list_boss;							// 각 스테이지  보스 스폰 목록.
	private MonsterSummonController.EVENT_STATUS status;	// 현재 진행된 상태 확인용 변수.
	private List<MonsterInstance> list_spawn;				// 스폰된 몬스터 들.
	public int timer_ment_cnt;								// 멘트 처리에 사용되는 카운팅 변수. 어떤 멘트를 날릴지 파악용.
	public int nowStage;									// 현재 진행중인 스테이지 값
	public long timer_time;									// 소환 이벤트 진행 처리에 사용되는 시간값.
	public long timer_cool_time;							// 휴식 시간값.
	public int list_spawn_idx;								// 스폰된 몬스터 위치값.

	public MonsterSummoning(){
		
		list_spawn = new ArrayList<MonsterInstance>();
		listItem = new HashMap<Integer, List<String>>();
		list = new HashMap<Integer, List<String>>();
		list_boss = new ArrayList<String>();
		status = MonsterSummonController.EVENT_STATUS.휴식;
		timer_cool_time = timer_time = timer_ment_cnt = nowStage = maxStage = list_spawn_idx = map = x = y = 0;
		lastTime = -1;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getMaxStage() {
		return maxStage;
	}

	public void setMaxStage(int maxStage) {
		this.maxStage = maxStage;
	}
	
	public MonsterSummonController.EVENT_STATUS getStatus() {
		return status;
	}

	public void setStatus(MonsterSummonController.EVENT_STATUS status) {
		this.status = status;
	}

	public int getLastTime() {
		return lastTime;
	}

	public void setLastTime(int lastTime) {
		this.lastTime = lastTime;
	}
	
	public int[] getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(int[] timeStart) {
		this.timeStart = timeStart;
	}

	public int[] getTimeCool() {
		return timeCool;
	}

	public void setTimeCool(int[] timeCool) {
		this.timeCool = timeCool;
	}
	
	public int[] getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(int[] timeEnd) {
		this.timeEnd = timeEnd;
	}

	public int[][] getStageItemCount() {
		return stageItemCount;
	}

	public void setStageItemCount(int[][] stageItemCount) {
		this.stageItemCount = stageItemCount;
	}

	public int[] getBossCount() {
		return bossCount;
	}

	public void setBossCount(int[] bossCount) {
		this.bossCount = bossCount;
	}
	
	public int[][] getStageCount() {
		return stageCount;
	}
	
	public void setStageCount(int[][] stageCount) {
		this.stageCount = stageCount;
	}
	
	public Map<Integer, List<String>> getList(){
		return list;
	}

	public Map<Integer, List<String>> getListItem(){
		return listItem;
	}	
	
	public List<String> getListBoss(){
		return list_boss;
	}
	
	public List<MonsterInstance> getListSpawn(){
		return list_spawn;
	}	
	
	public String toString(int h){
		StringBuffer sb = new StringBuffer();
		sb.append(h);
		sb.append(name);
		return sb.toString();
	}
}
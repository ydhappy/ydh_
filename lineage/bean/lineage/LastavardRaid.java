package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.monster.LastavardBoss;
import lineage.world.object.npc.background.door.LastavardDoor;

public class LastavardRaid extends Lastavard {

	private Map<LastavardBoss, List<LastavardBoss>> group_list;
	private int step;
	private int step_boss_idx;
	private long step_time;
	private final int BOSS_DELAY = 20; // 보스당 제한시간. 분단위
	private List<Integer> home_map_list;

	public LastavardRaid(int mapId) {
		super(mapId);

		group_list = new HashMap<LastavardBoss, List<LastavardBoss>>();
		home_map_list = new ArrayList<Integer>();
	}

	@Override
	public void init() {
		super.init();

		step = step_boss_idx = 0;
		step_time = 0;
	}

	@Override
	public void close(boolean clear) {
		super.close(clear);

		synchronized (group_list) {
			for (LastavardBoss key : group_list.keySet()) {
				for (LastavardBoss lb : group_list.get(key)) {
					World.remove(lb);
					lb.clearList(true);
					if (clear)
						lb.setAiStatus(Lineage.AI_STATUS_DELETE);
				}
				if (clear)
					group_list.get(key).clear();
			}
			if (clear)
				group_list.clear();
		}
		step = step_boss_idx = 0;
		step_time = 0;
	}

	@Override
	protected void toSpawnDoorMan() {
		//
	}

	@Override
	protected void toSpawnBoss() {
		//
	}

	@Override
	public void appendBoss(LastavardBoss lb, int x, int y, int map, int heading) {
		super.appendBoss(lb, x, y, map, heading);
		//
		if (!home_map_list.contains(map))
			home_map_list.add(map);
	}

	@Override
	public void appendDoor(LastavardDoor door, int x, int y, int map, int heading, int gfx) {
		super.appendDoor(door, x, y, map, heading, gfx);
		//
		if (!home_map_list.contains(map))
			home_map_list.add(map);
	}

	/**
	 * 등록된 보스와 함께 스폰할 그룹 목록에 등록 처리 메서드.
	 * 
	 * @param key
	 * @param lb
	 * @param x
	 * @param y
	 * @param heading
	 */
	public void appendGroupList(LastavardBoss key, LastavardBoss lb, int x, int y, int map, int heading) {
		lb.setHomeX(x);
		lb.setHomeY(y);
		lb.setHomeMap(map);
		lb.setHomeHeading(heading);
		lb.setLastavard(this);
		//
		synchronized (group_list) {
			List<LastavardBoss> list = group_list.get(key);
			if (list == null) {
				list = new ArrayList<LastavardBoss>();
				group_list.put(key, list);
			}
			list.add(lb);
		}
		//
		if (!home_map_list.contains(map))
			home_map_list.add(map);
	}

	@Override
	public void toTimer(long time) {
		//
		
		switch (step) {
			case 0: // 초기처리 부분.
				// 스폰.
				toSpawnBoss(step_boss_idx);
				step = 1;
				break;
			case 1:
				// 죽엇는지 확인.
				if (boss_list.get(step_boss_idx).isDead() && isGroupDead(boss_list.get(step_boss_idx))) {
					// 문 열기.
					door_list.get(step_boss_idx).toOpen();
					door_list.get(step_boss_idx).toSend();
					// 스탭 변경
					step = 2;
					// 딜레이
					step_time = time + (1000 * 60 * 5);
				}
				break;
			case 2:
				// 대기
				if (time >= step_time)
					step = 3;
				break;
			case 3:
				// 문 닫기.
				door_list.get(step_boss_idx).toClose();
				door_list.get(step_boss_idx).toSend();
				step = 4;
				break;
			case 4:
				// 마지막 보스가 죽엇다면
				if (boss_list.size() <= step_boss_idx + 1) {
					step = 6;
					step_time = time + (1000 * 60 * 1);
					break;
				}

				// 다음 스탭.
				step_boss_idx += 1;
				// 딜레이 주기
				step_time = time + (1000 * 60 * BOSS_DELAY);
				//
				step = 5;
				// 다음보스 스폰.
				toSpawnBoss(step_boss_idx);
				break;
			case 5:
				// 시간안에 보스를 못잡을경우 종료처리.
				if (time >= step_time) {
					step = 6;
					step_time = time + (1000 * 60 * 1);
				}
				// 보스가 죽고 문이 닫힌상태일경우.
				if (boss_list.get(step_boss_idx).isDead() && isGroupDead(boss_list.get(step_boss_idx)) && door_list.get(step_boss_idx).isDoorClose()) {
					// 문 열기.
					door_list.get(step_boss_idx).toOpen();
					door_list.get(step_boss_idx).toSend();
					// 스탭 변경
					step = 4;
				}
				break;
			case 6:
				// 대기
				if (time >= step_time)
					step = 7;
				break;
			case 7:
				// 유저를 강제 귀환.
				for (PcInstance pc : World.getPcList()) {
					if (home_map_list.contains(pc.getMap())) {
						TeleportHomeDatabase.toLocation(pc);
						pc.toPotal(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap());
					}
				}
				close(false);
				init();
				break;
		}
		// 더이상관리목록 스폰할 객체가없을경우
		// - 3층 결계로 강제 텔레포트.
		//
	}

	private void toSpawnBoss(int idx) {
		synchronized (boss_list) {
			//
			LastavardBoss boss = boss_list.get(idx);
			MonsterSpawnlistDatabase.toSpawnMonster(boss, World.get_map(boss.getHomeMap()), false, boss.getHomeX(), boss.getHomeY(), boss.getHomeMap(), 0, 0, 0, false, true);
			synchronized (group_list) {
				//
				List<LastavardBoss> list = group_list.get(boss);
				if (list == null)
					return;
				for (LastavardBoss lb : list) {
					MonsterSpawnlistDatabase.toSpawnMonster(lb, World.get_map(lb.getHomeMap()), false, lb.getHomeX(), lb.getHomeY(), lb.getHomeMap(), 0, 0, 0, false, true);
					boss.getGroupList().add(lb);
					lb.setGroupMaster(boss);
				}
			}
		}
	}

	/**
	 * 보스와 연결된 그룹몬스터들이 전부 죽엇는지 확인해주는 함수.
	 * 
	 * @param key
	 * @return
	 */
	private boolean isGroupDead(LastavardBoss key) {
		boolean is = true;
		return is;
	}

}

package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.monster.IceDungeonBoss;
import lineage.world.object.monster.IceDungeonDoorMan;
import lineage.world.object.npc.background.door.Door;
import lineage.world.object.npc.background.door.IceDungeonDoor;

public class IceDungeon {

	protected int mapId;
	protected List<IceDungeonBoss> boss_list; // 보스 목록
	protected List<IceDungeonDoorMan> doorman_list; // 문지기 목록
	protected List<IceDungeonDoor> door_list; // 문 목록
	private long door_close_time; // 문이 다시 닫히는 시간 기록.
	private long boss_spawn_time; // 문지기 및 보스가 재스폰할 시간 기록.
	private int boosDeadCount; // 문지기 및 보스가 죽을때 해당값을 카운팅함.
	private int doormanDeadCount; // 문지기 및 보스가 죽을때 해당값을 카운팅함.

	public IceDungeon(int mapId) {
		boss_list = new ArrayList<IceDungeonBoss>();
		doorman_list = new ArrayList<IceDungeonDoorMan>();
		door_list = new ArrayList<IceDungeonDoor>();
		this.mapId = mapId;
		door_close_time = boss_spawn_time = boosDeadCount = doormanDeadCount = 0;
	}

	public void init() {
		toSpawnDoor();
		toSpawnDoorMan();
		toSpawnBoss();
	}

	public void close(boolean clear) {
		synchronized (boss_list) {
			for (IceDungeonBoss lb : boss_list) {
				World.remove(lb);
				lb.clearList(true);
				if (clear)
					lb.setAiStatus(Lineage.AI_STATUS_DELETE);
			}
			if (clear)
				boss_list.clear();
		}
		synchronized (doorman_list) {
			for (IceDungeonDoorMan ldm : doorman_list) {
				World.remove(ldm);
				ldm.clearList(true);
				if (clear)
					ldm.setAiStatus(Lineage.AI_STATUS_DELETE);
			}
			if (clear)
				doorman_list.clear();
		}
		synchronized (door_list) {
			for (IceDungeonDoor door : door_list) {
				World.remove(door);
				door.clearList(true);
			}
			if (clear)
				door_list.clear();
		}
		door_close_time = boss_spawn_time = boosDeadCount = doormanDeadCount = 0;
	}

	/**
	 * 해당층에 관리할 보스 등록.
	 * 
	 * @param ldm
	 */
	public void appendBoss(IceDungeonBoss lb, int x, int y, int map, int heading) {
		lb.setHomeX(x);
		lb.setHomeY(y);
		lb.setHomeMap(map);
		lb.setHomeHeading(heading);
		lb.setIceDungeon(this);
		synchronized (boss_list) {
			boss_list.add(lb);
		}
	}

	/**
	 * 해당층에 관리할 문지기 등록.
	 * 
	 * @param ldm
	 */
	public void appendDoorMan(IceDungeonDoorMan ldm, int x, int y, int heading) {
		ldm.setHomeX(x);
		ldm.setHomeY(y);
		ldm.setHomeHeading(heading);
		ldm.setIceDungeon(this);
		synchronized (doorman_list) {
			doorman_list.add(ldm);
		}
	}

	/**
	 * 해당층에 관리할 문을 등록.
	 * 
	 * @param door
	 */
	public void appendDoor(IceDungeonDoor door, int x, int y, int map, int heading, int gfx) {
		door.setHomeX(x);
		door.setHomeY(y);
		door.setHomeMap(map);
		door.setHomeHeading(heading);
		door.setClassGfx(gfx);
		door.setClassGfxMode(29);
		door.setGfx(door.getClassGfx());
		door.setGfxMode(door.getClassGfxMode());
		door.setHeading(door.getHomeHeading());
		synchronized (door_list) {
			door_list.add(door);
		}
	}

	public int getMap() {
		return mapId;
	}

	public void updateBossDead() {
		synchronized (boss_list) {
			if (boss_list.size() > boosDeadCount)
				boosDeadCount += 1;
		}
	}

	public void updateDoormanDead() {
		synchronized (doorman_list) {
			if (doorman_list.size() > doormanDeadCount)
				doormanDeadCount += 1;
		}
	}

	/**
	 * LastavardController.toTimer 에서 지속적으로 호출함.
	 * 
	 * @param time
	 */
	public void toTimer(long time) {
		toDoorCheck(time);
		toBossCheck(time);
	}

	protected void toSpawnDoor() {
		synchronized (door_list) {
			for (Door door : door_list) {
				if (door.getObjectId() == 0)
					door.setObjectId(ServerDatabase.nextNpcObjId());
				door.toClose();
				door.toTeleport(door.getHomeX(), door.getHomeY(), door.getHomeMap(), false);
			}
		}
	}

	protected void toSpawnDoorMan() {
		synchronized (doorman_list) {
			for (MonsterInstance mi : doorman_list)
				MonsterSpawnlistDatabase.toSpawnMonster(mi,  World.get_map(mapId), false, mi.getHomeX(), mi.getHomeY(), mapId, 0, 0, 0, false, true);
		}
	}

	protected void toSpawnBoss() {
		synchronized (boss_list) {
			for (MonsterInstance mi : boss_list)
				MonsterSpawnlistDatabase.toSpawnMonster(mi,  World.get_map(mi.getHomeMap()), false, mi.getHomeX(), mi.getHomeY(), mi.getHomeMap(), 0, 0, 0, false, true);
		}
	}

	/**
	 * 관리중인 보스 및 문지기를 재스폰 처리함.
	 * 
	 * @param time
	 */
		private void toBossCheck(long time) {
		if (boss_spawn_time == 0) {
			// 문지기 및 보스가 스폰되있는 상태.
		} else {
			// 문지기 및 보스가 재스폰 대기중인 상태.
			if (time >= boss_spawn_time) {
				toSpawnDoorMan();
				toSpawnBoss();
				boss_spawn_time = 0;
			}
		}
	} 

	/**
	 * 관리중인 문을 열거나 닫는걸 처리함.
	 * 
	 * @param time
	 */
	private void toDoorCheck(long time) {
		// 아직 보스나 문지기가 죽은적이 없으면 무시.
		if (boosDeadCount + doormanDeadCount == 0)
			return;
		// 아직 문이 오픈안됫을때
		if (door_close_time == 0) {
			// 문지기 & 보스 죽엇는지 확인.
			// 죽엇다면 관리중인 문 열기.
			if (boosDeadCount + doormanDeadCount >= doorman_list.size() + boss_list.size()) {
				for (IceDungeonDoor ld : door_list) {
					ld.toOpen();
					ld.toSend();
				}
				// 25분 후 문 닫히게 하기.
				door_close_time = time + (1000 * 60 * 5);
			}
		} else {
			// 문이 오픈된시간에서 25분이 지낫다면 다시 닫기.
			if (time >= door_close_time) {
			//	System.out.println("라바문 닫힘! 몹렉이 생겼는지 봐주세요");
				for (IceDungeonDoor ld : door_list) {
					ld.toClose();
					ld.toSend();

				door_close_time = boosDeadCount = doormanDeadCount = 0;
				// 문지기 및 보스 25분 후 재 스폰하기. (문대기 5분지난 후임.)
				boss_spawn_time = time + (1000 * 60 * 1);
				}
			}
		}
	}
}

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
import lineage.world.object.monster.LastavardBoss;
import lineage.world.object.monster.LastavardDoorMan;
import lineage.world.object.npc.background.door.Door;
import lineage.world.object.npc.background.door.LastavardDoor;

public class Lastavard {

	protected int mapId;
	protected List<LastavardBoss> boss_list; // 보스 목록
	protected List<LastavardDoorMan> doorman_list; // 문지기 목록
	protected List<LastavardDoor> door_list; // 문 목록
	private long door_close_time; // 문이 다시 닫히는 시간 기록.
	private long boss_spawn_time; // 문지기 및 보스가 재스폰할 시간 기록.
	private int boosDeadCount; // 문지기 및 보스가 죽을때 해당값을 카운팅함.
	private int doormanDeadCount; // 문지기 및 보스가 죽을때 해당값을 카운팅함.

	public Lastavard(int mapId) {
		boss_list = new ArrayList<LastavardBoss>();
		doorman_list = new ArrayList<LastavardDoorMan>();
		door_list = new ArrayList<LastavardDoor>();
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
			for (LastavardBoss lb : boss_list) {
				World.remove(lb);
				lb.clearList(true);
				if (clear)
					lb.setAiStatus(Lineage.AI_STATUS_DELETE);
			}
			if (clear)
				boss_list.clear();
		}
		synchronized (doorman_list) {
			for (LastavardDoorMan ldm : doorman_list) {
				World.remove(ldm);
				ldm.clearList(true);
				if (clear)
					ldm.setAiStatus(Lineage.AI_STATUS_DELETE);
			}
			if (clear)
				doorman_list.clear();
		}
		synchronized (door_list) {
			for (LastavardDoor door : door_list) {
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
	public void appendBoss(LastavardBoss lb, int x, int y, int map, int heading) {
		lb.setHomeX(x);
		lb.setHomeY(y);
		lb.setHomeMap(map);
		lb.setHomeHeading(heading);
		lb.setLastavard(this);
		synchronized (boss_list) {
			boss_list.add(lb);
		}
	}

	/**
	 * 해당층에 관리할 문지기 등록.
	 * 
	 * @param ldm
	 */
	public void appendDoorMan(LastavardDoorMan ldm, int x, int y, int heading) {
		ldm.setHomeX(x);
		ldm.setHomeY(y);
		ldm.setHomeHeading(heading);
		ldm.setLastavard(this);
		synchronized (doorman_list) {
			doorman_list.add(ldm);
		}
	}

	/**
	 * 해당층에 관리할 문을 등록.
	 * 
	 * @param door
	 */
	public void appendDoor(LastavardDoor door, int x, int y, int map, int heading, int gfx) {
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
				for (LastavardDoor ld : door_list) {
					ld.toOpen();
					ld.toSend();
				}
				// 25분 후 문 닫히게 하기.
				door_close_time = time + (1000 * 60 * 25);
			}
		} else {
			// 문이 오픈된시간에서 25분이 지낫다면 다시 닫기.
			if (time >= door_close_time) {
			//	System.out.println("라바문 닫힘! 몹렉이 생겼는지 봐주세요");
				for (LastavardDoor ld : door_list) {
					ld.toClose();
					ld.toSend();

					// 라스타바드 문이 닫겼을때 유저들 특정좌표로 텔레포트되는 공식 구현요청 (문 위치에 따른 설정된맵)
					if (ld.getX() == 32813 && ld.getY() == 32833 && ld.getMap() == 451) { // 집회장 1시방향
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 452) { // 돌격대 훈련장
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32785 && ld.getY() == 32809 && ld.getMap() == 451) { // 집회장 11시방향
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 491) { // 지하 통로
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32811 && ld.getY() == 32836 && ld.getMap() == 452) { // 1층 돌격대 훈련장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 453) { // 마수군왕의 집무실
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32768 && ld.getY() == 32830 && ld.getMap() == 453) { // 1층 마수군왕의 집무실 11시방향
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 454) { // 야수조련실
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32813 && ld.getY() == 32822 && ld.getMap() == 454) { // 1층 야수 조련실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 455) { // 야수 훈련장
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32793 && ld.getY() == 32852 && ld.getMap() == 453) { // 1층 마수군왕의 집무실 11시방향
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 456) { // 마수 소환실
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32733 && ld.getY() == 32860 && ld.getMap() == 491) { // 지하통로
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 495) { // 지하 결투장
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32815 && ld.getY() == 32817 && ld.getMap() == 495) { // 지하 결투장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 496) { // 지하 감옥
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32773 && ld.getY() == 32785 && ld.getMap() == 495) { // 지하 결투장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 493) { // 지하 통제실
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32806 && ld.getY() == 32770 && ld.getMap() == 493) { // 지하 통제실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 494) { // 지하 처형장
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32746 && ld.getY() == 32723 && ld.getMap() == 493) { // 지하 통제실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 490) { // 지하 훈련장
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32731 && ld.getY() == 32810 && ld.getMap() == 490) { // 지하 훈련장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 492) { // 암살군왕의 집무실
								switch (Util.random(0, 3)) {
								case 0:
									use.toPotal(32746, 32814, 450);
									break;
								case 1:
									use.toPotal(32739, 32815, 450);
									break;
								case 2:
									use.toPotal(32742, 32822, 450);
									break;
								case 3:
									use.toPotal(32749, 32820, 450);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32814 && ld.getY() == 32819 && ld.getMap() == 460) { // 흑마법 수련장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 461) { // 흑마법 연구실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32680, 32870, 457);
									break;
								case 1:
									use.toPotal(32666, 32860, 457);
									break;
								case 2:
									use.toPotal(32671, 32865, 457);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32695 && ld.getY() == 32797 && ld.getMap() == 461) { // 흑마법 연구실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 462) { // 마령군왕의 집무실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32680, 32870, 457);
									break;
								case 1:
									use.toPotal(32666, 32860, 457);
									break;
								case 2:
									use.toPotal(32671, 32865, 457);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32813 && ld.getY() == 32865 && ld.getMap() == 462) { // 마령군왕의 집무실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 463) { // 마령군왕의 서재
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32680, 32870, 457);
									break;
								case 1:
									use.toPotal(32666, 32860, 457);
									break;
								case 2:
									use.toPotal(32671, 32865, 457);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32806 && ld.getY() == 32829 && ld.getMap() == 464) { // 정령 소환실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 465) { // 정령 서식지
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32680, 32870, 457);
									break;
								case 1:
									use.toPotal(32666, 32860, 457);
									break;
								case 2:
									use.toPotal(32671, 32865, 457);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32811 && ld.getY() == 32808 && ld.getMap() == 465) { // 정령 서식지
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 466) { // 암흑정령 연구실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32680, 32870, 457);
									break;
								case 1:
									use.toPotal(32666, 32860, 457);
									break;
								case 2:
									use.toPotal(32671, 32865, 457);
									break;
								}
							}
						}
					}
					// 3층
					if (ld.getX() == 32817 && ld.getY() == 32806 && ld.getMap() == 470) { // 악령제단
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 473) { // 명법군의 훈련장
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32660, 32872, 468);
									break;
								case 1:
									use.toPotal(32678, 32874, 468);
									break;
								case 2:
									use.toPotal(32668, 32875, 468);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32833 && ld.getY() == 32792 && ld.getMap() == 473) { // 명법군의 훈련장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 478) { // 통제구역
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32660, 32872, 468);
									break;
								case 1:
									use.toPotal(32678, 32874, 468);
									break;
								case 2:
									use.toPotal(32668, 32875, 468);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32750 && ld.getY() == 32812 && ld.getMap() == 478) { // 통제구역
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 474) { // 오움 실험실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32660, 32872, 468);
									break;
								case 1:
									use.toPotal(32678, 32874, 468);
									break;
								case 2:
									use.toPotal(32668, 32875, 468);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32735 && ld.getY() == 32786 && ld.getMap() == 478) { // 통제구역
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 476) { // 중앙 통제실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32660, 32872, 468);
									break;
								case 1:
									use.toPotal(32678, 32874, 468);
									break;
								case 2:
									use.toPotal(32668, 32875, 468);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32805 && ld.getY() == 32800 && ld.getMap() == 476) { // 중앙 통제실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 475) { // 명법군왕의 집무실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32660, 32872, 468);
									break;
								case 1:
									use.toPotal(32678, 32874, 468);
									break;
								case 2:
									use.toPotal(32668, 32875, 468);
									break;
								}
							}
						}
					}
					// 3층 반대편
					if (ld.getX() == 32808 && ld.getY() == 32799 && ld.getMap() == 472) { // 용병 훈련장
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 477) { // 데빌로드 용병실
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32677, 32855, 467);
									break;
								case 1:
									use.toPotal(32668, 32856, 467);
									break;
								case 2:
									use.toPotal(32665, 32854, 467);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32746 && ld.getY() == 32788 && ld.getMap() == 477) { // 데빌로드 용병실
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 471) { // 데빌로드 제단
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32677, 32855, 467);
									break;
								case 1:
									use.toPotal(32668, 32856, 467);
									break;
								case 2:
									use.toPotal(32665, 32854, 467);
									break;
								}
							}
						}
					}
					
					if (ld.getX() == 32737 && ld.getY() == 32862 && ld.getMap() == 541) { // 개미 알창고 (A)
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 541) { // 개미 알창고 (A)
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32744, 32790, 541);
									break;
								case 1:
									use.toPotal(32743, 32787, 541);
									break;
								case 2:
									use.toPotal(32743, 32792, 541);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32737 && ld.getY() == 32862 && ld.getMap() == 542) { // 개미 알창고 (B)
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 542) { // 개미 알창고 (B)
								switch (Util.random(0, 2)) {
								case 0:
									use.toPotal(32744, 32790, 542);
									break;
								case 1:
									use.toPotal(32743, 32787, 542);
									break;
								case 2:
									use.toPotal(32743, 32792, 542);
									break;
								}
							}
						}
					}
					if (ld.getX() == 32739 && ld.getY() == 32834 && ld.getMap() == 543) { // 거대 여왕 개미
						// 맵 단위 전체유저 강제 텔레포트 시키기.
						for (PcInstance use : World.getPcList()) {
							if (use.getMap() == 543) { // 거대 여왕 개미
								switch (Util.random(0, 4)) {
								case 0:
									use.toPotal(32864, 32748, 543);
									break;
								case 1:
									use.toPotal(32869, 32748, 543);
									break;
								case 2:
									use.toPotal(32869, 32755, 543);
									break;
								case 3:
									use.toPotal(32863, 32755, 543);
									break;
								case 4:
									use.toPotal(32867, 32751, 543);
									break;
								}
							}
						}
					}
					
				}
				door_close_time = boosDeadCount = doormanDeadCount = 0;
				// 문지기 및 보스 25분 후 재 스폰하기. (문대기 5분지난 후임.)
				boss_spawn_time = time + (1000 * 60 * 5);
			}
		}
	}
}

package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import all_night.Lineage_Balance;
import lineage.bean.database.Monster;
import lineage.bean.database.MonsterGroup;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.Map;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectGfx;
import lineage.network.packet.server.S_ObjectMode;
import lineage.network.packet.server.S_ObjectName;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BossController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.monster.Antharas;
import lineage.world.object.monster.ArachnevilElder;
import lineage.world.object.monster.Balthazar;
import lineage.world.object.monster.Baphomet;
import lineage.world.object.monster.Black_Knight;
import lineage.world.object.monster.Blob;
import lineage.world.object.monster.BombFlower;
import lineage.world.object.monster.Chaospriest;
import lineage.world.object.monster.Cuckoos;
import lineage.world.object.monster.Deer;
import lineage.world.object.monster.Demon;
import lineage.world.object.monster.Doppelganger;
import lineage.world.object.monster.Duck;
import lineage.world.object.monster.Elder;
import lineage.world.object.monster.Ettin;
import lineage.world.object.monster.Faust_Ghost;
import lineage.world.object.monster.FloatingEye;
import lineage.world.object.monster.Ghoul;
import lineage.world.object.monster.Gremlin;
import lineage.world.object.monster.Grimreaper;
import lineage.world.object.monster.Gryphon;
import lineage.world.object.monster.Harphy;
import lineage.world.object.monster.Hen;
import lineage.world.object.monster.IceDungeonDoorMan;
import lineage.world.object.monster.IceMonster;
import lineage.world.object.monster.IceQueenHandmaiden;
import lineage.world.object.monster.Ifrit;
import lineage.world.object.monster.Kaspar;
import lineage.world.object.monster.Knight;
import lineage.world.object.monster.Kouts;
import lineage.world.object.monster.Kuman;
import lineage.world.object.monster.LastavardBoss;
import lineage.world.object.monster.LastavardDoorMan;
import lineage.world.object.monster.Mermaid;
import lineage.world.object.monster.Merman;
import lineage.world.object.monster.Milkcow;
import lineage.world.object.monster.Monster_trap;
import lineage.world.object.monster.Nancy;
import lineage.world.object.monster.Necromancer;
import lineage.world.object.monster.NotSelectedPerson;
import lineage.world.object.monster.Oman_Monster;
import lineage.world.object.monster.Perez;
import lineage.world.object.monster.Phoenix;
import lineage.world.object.monster.Pig;
import lineage.world.object.monster.Secret_Book;
import lineage.world.object.monster.Sema;
import lineage.world.object.monster.Slime;
import lineage.world.object.monster.Spartoi;
import lineage.world.object.monster.Stabilizer;
import lineage.world.object.monster.StoneGolem;
import lineage.world.object.monster.Succubus;
import lineage.world.object.monster.Troll;
import lineage.world.object.monster.Unicorn;
import lineage.world.object.monster.Valakas;
import lineage.world.object.monster.Wolf;
import lineage.world.object.monster.Wolf2;
import lineage.world.object.monster.YongahBlue;
import lineage.world.object.monster.YongahYellow;
import lineage.world.object.monster.event.JackLantern;
import lineage.world.object.monster.quest.AtubaOrc;
import lineage.world.object.monster.quest.BetrayedOrcChief;
import lineage.world.object.monster.quest.BetrayerOfUndead;
import lineage.world.object.monster.quest.Bugbear;
import lineage.world.object.monster.quest.Cursed_Ettin;
import lineage.world.object.monster.quest.DarkElf;
import lineage.world.object.monster.quest.Darkmar;
import lineage.world.object.monster.quest.DudaMaraOrc;
import lineage.world.object.monster.quest.GandiOrc;
import lineage.world.object.monster.quest.Labourbon;
import lineage.world.object.monster.quest.MutantGiantQueenAnt;
import lineage.world.object.monster.quest.NerugaOrc;
import lineage.world.object.monster.quest.OrcZombie;
import lineage.world.object.monster.quest.Ramia;
import lineage.world.object.monster.quest.RovaOrc;
import lineage.world.object.monster.quest.Sealed_Succubus;
import lineage.world.object.monster.quest.Skeleton;
import lineage.world.object.monster.quest.Zombie;

public final class MonsterSpawnlistDatabase {

	static private List<MonsterInstance> pool;
	static public List<MonsterInstance> list;
	static private List<Monster> temp;

	static public void init(Connection con){
		TimeLine.start("MonsterSpawnlistDatabase..");
		
		// 몬스터 스폰 처리.
		pool = new ArrayList<MonsterInstance>();
		list = new ArrayList<MonsterInstance>();
		temp = new ArrayList<Monster>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();
			while(rs.next()){
				Monster m = MonsterDatabase.find(rs.getString("monster"));

				if (m != null) {

					if (temp.size() < 1) {
						temp.add(m);
					} else {
						boolean result = true;

						for (int i = 0; i < temp.size(); i++) {
							if (temp.get(i).getName().equalsIgnoreCase(m.getName())) {
								result = false;
								break;
							}
						}

						if (result)
							temp.add(m);
					}
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setSentry(Boolean.valueOf(rs.getString("sentry")));
					ms.setHeading(rs.getInt("heading"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while(stt.hasMoreTokens()){
						String map = stt.nextToken();
						if(map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}
					ms.setReSpawn(rs.getInt("re_spawn_min") * 1000);
					
					if (rs.getInt("re_spawn_max") < rs.getInt("re_spawn_min"))
						ms.setReSpawnMax(rs.getInt("re_spawn_min") * 1000);
					else
						ms.setReSpawnMax(rs.getInt("re_spawn_max") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if(ms.isGroup()){
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						Monster g5 = MonsterDatabase.find(rs.getString("monster_5"));
						Monster g6 = MonsterDatabase.find(rs.getString("monster_6"));
						Monster g7 = MonsterDatabase.find(rs.getString("monster_7"));
						Monster g8 = MonsterDatabase.find(rs.getString("monster_8"));
						if(g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if(g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if(g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if(g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
						if(g5 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_5_count")));
						if(g6 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_6_count")));
						if(g7 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_7_count")));
						if(g8 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_8_count")));
					}
					toSpawnMonster(ms, null);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}

	/**
	 * 몬스터 리스폰
	 */
	static public void close() {
		synchronized (list) {
			for (MonsterInstance mi : World.getMonsterList()) {
				mi.setDead(true);
				World.remove(mi);
				mi.clearList(true);
				mi.setAiStatus(-2);
			}
		}
	}

	/**
	 * 중복코드 방지용 : gui 기능에서 사용중 lineage.gui.dialog.MonsterSpawn.step4()
	 */
	static public void toSpawnMonster(MonsterSpawnlist ms, Map map) {
		// 버그 방지
		if (ms == null || ms.getMap().size() <= 0)
			return;
		// 맵 확인.
		if (map == null) {
			if (ms.getMap().size() > 1)
				map = World.get_map(ms.getMap().get(Util.random(0, ms.getMap().size() - 1)));
			else
				map = World.get_map(ms.getMap().get(0));
		}
		if (map == null)
			return;
		// 스폰처리.
		for (int i = 0; i < ms.getCount(); ++i) {
			MonsterInstance mi = newInstance(ms.getMonster());
			if (mi == null)
				return;

			if (i == 0)
				mi.setMonsterSpawnlist(ms);
			mi.setHomeHeading(ms.getHeading());
			mi.setDatabaseKey(Integer.valueOf(ms.getUid()));
			if (ms.isGroup()) {
				mi.setGroupMaster(mi);
				// 마스터 스폰.
				toSpawnMonster(mi, map, false, ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
				// 관리객체 생성.
				for (MonsterGroup mg : ms.getListGroup()) {
					for (int j = mg.getCount(); j > 0; --j) {
						MonsterInstance a = newInstance(mg.getMonster());
						if (a != null) {
							// 스폰
							toSpawnMonster(a, map, false, ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
							// 마스터관리 목록에 등록.
							mi.getGroupList().add(a);
							// 관리하고있는 마스터가 누군지 지정.
							a.setGroupMaster(mi);
						}
					}
				}
			} else {
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
			}
		}
	}

	/**
	 * 파우스트의 악령 및 기타 몬스터 이벤트성 소환시 사용 2017-10-07 by all-night
	 */
	static public boolean toSpawnMonster(Monster monster, int x, int y, int map, int heading, boolean boss, MonsterInstance mon) {
		// 버그 방지
		if (monster == null || map < 0)
			return false;

		// 스폰처리.
		MonsterInstance mi = newInstance(monster);

		if (mi == null)
			return false;

		if (mon instanceof Oman_Monster || mon instanceof Grimreaper)
			mon.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon, 4784), true);
		else if (mon instanceof Faust_Ghost)
			mon.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mon, 5634), true);

		// 기존 몬스터 제거후 스폰대기
		mon.toAiThreadRespawn();

		if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
			if (mi.getMonster().isHaste())
				mi.setSpeed(1);
			if (mi.getMonster().isBravery())
				mi.setBrave(true);
		}

		mi.setHeading(heading);
		mi.setReSpawnTime(0);
		mi.setHomeX(x);
		mi.setHomeY(y);
		mi.setHomeMap(map);
		mi.toTeleport(x, y, map, false);
		// mi.readDrop(map);
		AiThread.append(mi);

		if (boss) {
			mi.setBoss(true);
			BossController.appendBossList(mi);

			if (mon instanceof Faust_Ghost && Lineage_Balance.faust_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Oman_Monster && Lineage_Balance.grimreaper_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Grimreaper && Lineage_Balance.oman_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			}
		}

		return true;
	}

	static public boolean toSpawnMonster2(Monster monster, int x, int y, int map, int heading, boolean boss, MonsterInstance mon) {
		// 버그 방지
		if (monster == null || map < 0)
			return false;

		// 스폰처리.
		MonsterInstance mi = newInstance(monster);

		if (mi == null)
			return false;

		if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
			if (mi.getMonster().isHaste())
				mi.setSpeed(1);
			if (mi.getMonster().isBravery())
				mi.setBrave(true);
		}

		mi.setHeading(heading);
		mi.setReSpawnTime(0);
		mi.setHomeX(x);
		mi.setHomeY(y);
		mi.setHomeMap(map);
		mi.toTeleport(x, y, map, false);
		// mi.readDrop(map);
		AiThread.append(mi);

		if (boss) {
			mi.setBoss(true);
			BossController.appendBossList(mi);

			if (mon instanceof Faust_Ghost && Lineage_Balance.faust_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Oman_Monster && Lineage_Balance.grimreaper_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			} else if (mon instanceof Grimreaper && Lineage_Balance.oman_spawn_msg) {
				World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
			}
		}

		return true;
	}

	/**
	 * 중복코드 방지용 : gui 기능에서 사용중 lineage.gui.dialog.MonsterSpawn.step4()
	 */
	static public void toSpawnMonster(MonsterSpawnlist ms, Map map, boolean isBoss) {
		// 버그 방지
		if (ms == null || ms.getMap().size() <= 0)
			return;
		// 맵 확인.
		if (map == null) {
			if (ms.getMap().size() > 1)
				map = World.get_map(ms.getMap().get(Util.random(0, ms.getMap().size() - 1)));
			else
				map = World.get_map(ms.getMap().get(0));
		}
		if (map == null)
			return;
		// 스폰처리.
		for (int i = 0; i < ms.getCount(); ++i) {
			MonsterInstance mi = newInstance(ms.getMonster());
			if (mi == null)
				return;
			if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
				if (mi.getMonster().isHaste())
					mi.setSpeed(1);
				if (mi.getMonster().isBravery())
					mi.setBrave(true);
			}
			if (i == 0)
				mi.setMonsterSpawnlist(ms);
			mi.setHomeHeading(ms.getHeading());
			mi.setDatabaseKey(Integer.valueOf(ms.getUid()));
			if (ms.isGroup()) {
				mi.setGroupMaster(mi);
				// 마스터 스폰.
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
				// 관리객체 생성.
				for (MonsterGroup mg : ms.getListGroup()) {
					for (int j = mg.getCount(); j > 0; --j) {
						MonsterInstance a = newInstance(mg.getMonster());
						if (a != null) {
							// 스폰
							toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
							// 마스터관리 목록에 등록.
							mi.getGroupList().add(a);
							// 관리하고있는 마스터가 누군지 지정.
							a.setGroupMaster(mi);
						}
					}
				}
			} else {
				toSpawnMonster(mi, map, ms.isRandom(), ms.getX(), ms.getY(), map.mapid, ms.getLocSize(), ms.getReSpawn(), ms.getReSpawnMax(), true, true);
			}
		}
	}

	/**
	 * 중복 코드를 막기위해 함수로 따로 뺌.
	 */
	static public void toSpawnMonster(MonsterInstance mi, Map m, boolean random, int x, int y, int map, int loc, int respawn, int respawnMax, boolean drop, boolean ai) {
		if (mi == null)
			return;

		int roop_cnt = 0;
		int lx = x;
		int ly = y;
		if (random) {
			// 랜덤 좌표 스폰
			do {
				if (x > 0) {
					lx = Util.random(x - loc, x + loc);
					ly = Util.random(y - loc, y + loc);
				} else {
					lx = Util.random(m.locX1, m.locX2);
					ly = Util.random(m.locY1, m.locY2);
				}
				if (roop_cnt++ > 500) {
					lx = x;
					ly = y;
					break;
				}
			} while (!World.isThroughObject(lx, ly + 1, map, 0) || !World.isThroughObject(lx, ly - 1, map, 4) || !World.isThroughObject(lx - 1, ly, map, 2) || !World.isThroughObject(lx + 1, ly, map, 6)
					|| !World.isThroughObject(lx - 1, ly + 1, map, 1) || !World.isThroughObject(lx + 1, ly - 1, map, 5) || !World.isThroughObject(lx + 1, ly + 1, map, 7) || !World.isThroughObject(lx - 1, ly - 1, map, 3)
					|| World.isNotMovingTile(lx, ly, map));
		} else {
			// 좌표 스폰
			lx = x;
			ly = y;
			if (loc > 1 && x > 0) {
				while (!World.isThroughObject(lx, ly + 1, map, 0) || !World.isThroughObject(lx, ly - 1, map, 4) || !World.isThroughObject(lx - 1, ly, map, 2) || !World.isThroughObject(lx + 1, ly, map, 6)
						|| !World.isThroughObject(lx - 1, ly + 1, map, 1) || !World.isThroughObject(lx + 1, ly - 1, map, 5) || !World.isThroughObject(lx + 1, ly + 1, map, 7)
						|| !World.isThroughObject(lx - 1, ly - 1, map, 3) || World.isNotMovingTile(lx, ly, map)) {
					lx = Util.random(x - loc, x + loc);
					ly = Util.random(y - loc, y + loc);
					if (roop_cnt++ > 500) {
						lx = x;
						ly = y;
						break;
					}
				}
			}
		}

		if (mi.getMonster().isHaste() || mi.getMonster().isBravery()) {
			if (mi.getMonster().isHaste())
				mi.setSpeed(1);
			if (mi.getMonster().isBravery())
				mi.setBrave(true);
		}

		mi.setReSpawnTime(Util.random(respawn, respawnMax));
		mi.setHomeX(lx);
		mi.setHomeY(ly);
		mi.setHomeLoc(loc);
		mi.setHomeMap(map);
		mi.toTeleport(lx, ly, map, false);
		mi.setHomeHeading(mi.getHeading());
		if (mi.getInventory() != null) {
			for (ItemInstance ii : mi.getInventory().getList()) {
				ItemDatabase.setPool(ii);
			}
			mi.getInventory().clearList();
		}

		if (ai)
			AiThread.append(mi);

		if (respawn > 0)
			World.appendMonster(mi);
	}

	static public MonsterInstance newInstance(Monster m) {
		MonsterInstance mi = null;

		if (m != null) {
			switch (m.getNameIdNumber()) {
			case 936: // 토끼
			case 256: // 개구리
			case 930: // 사슴
				mi = Deer.clone(getPool(Deer.class), m);
				break;
			case 952: // 오리
				mi = Duck.clone(getPool(Duck.class), m);
				break;
			case 927: // 돼지
				mi = Pig.clone(getPool(Pig.class), m);
				break;
			case 928: // 암닭
				mi = Hen.clone(getPool(Hen.class), m);
				break;
			case 929: // 젖소
				mi = Milkcow.clone(getPool(Milkcow.class), m);
				break;
			case 1405: // 인어
				mi = Mermaid.clone(getPool(Mermaid.class), m);
				break;
			case 331: // 네크로맨서[완]
				mi = Necromancer.clone(getPool(Necromancer.class), m);
				break;
			case 335: // 발터자르
				mi = Balthazar.clone(getPool(Balthazar.class), m);
				break;
			case 336: // 카스파
				mi = Kaspar.clone(getPool(Kaspar.class), m);
				break;
			case 337: // 메르키오르
				mi = Nancy.clone(getPool(Nancy.class), m);
				break;
			case 338: // 세마
				mi = Sema.clone(getPool(Sema.class), m);
				break;
			case 371: // 데스나이트[완]
				mi = Knight.clone(getPool(Knight.class), m);
				break;
			case 306: // 바포메트[완]
				mi = Baphomet.clone(getPool(Baphomet.class), m);
				break;
			case 945: // 베레스[완]
				mi = Perez.clone(getPool(Perez.class), m);
				break;
			case 274: // 커츠[완]
				mi = Kouts.clone(getPool(Kouts.class), m);
				break;
			case 1175: // 데몬[완]
				mi = Demon.clone(getPool(Demon.class), m);
				break;
			case 992: // 흑장로[완]
				mi = Elder.clone(getPool(Elder.class), m);
				break;
			case 1569: // 피닉스[완]
				mi = Phoenix.clone(getPool(Phoenix.class), m);
				break;
			case 1567: // 이프리트[완]
				mi = Ifrit.clone(getPool(Ifrit.class), m);
				break;
			case 1116: // 안타라스
				mi = Antharas.clone(getPool(Antharas.class), m);
				break;
//			case 1605: // 발라카스
//				mi = Valakas.clone(getPool(Valakas.class), m);
//				break;
			case 3726: // 쿠만
				mi = Kuman.clone(getPool(Kuman.class), m);
				break;
			case 6: // 괴물 눈
				mi = FloatingEye.clone(getPool(FloatingEye.class), m);
				break;
			case 2081: // 배신자의 해골근위병
				mi = Skeleton.clone(getPool(Skeleton.class), m);
				break;
			case 8: // 슬라임
				mi = Slime.clone(getPool(Slime.class), m);
				break;
			case 56: // 돌골렘
				mi = StoneGolem.clone(getPool(StoneGolem.class), m);
				break;
			case 57: // 좀비
				mi = Zombie.clone(getPool(Zombie.class), m);
				break;
			case 272: // 흑기사
			case 273:
				mi = Black_Knight.clone(getPool(Black_Knight.class), m);
				break;
			case 268: // 늑대
			case 904: // 세인트버나드
			case 4072: // 아기진돗개
			case 4073: // 진돗개
			case 4079: // 아기 캥거루
			case 4078: // 공포의판다곰
			case 4080: // 불꽃의 캥거루
			case 4077: // 아기 판다곰
			case 906: // 콜리
			case 907: // 세퍼드
			case 908: // 비글
			case 1397: // 여우
			case 1495: // 곰
			case 1788: // 허스키
			case 2563: // 열혈토끼
			case 2701: // 고양이
			case 3041: // 호랑이
			case 3508: // 라쿤
				mi = Wolf.clone(getPool(Wolf.class), m);
				break;
			case 905: // 도베르만
				mi = Wolf2.clone(getPool(Wolf2.class), m);
				break;
			case 318: // 스파토이
				mi = Spartoi.clone(getPool(Spartoi.class), m);
				break;
			case 319: // 웅골리언트
				mi = ArachnevilElder.clone(getPool(ArachnevilElder.class), m);
				break;
			case 325: // 버그베어
				mi = Bugbear.clone(getPool(Bugbear.class), m);
				break;
			case 494: // 아투바 오크
				mi = AtubaOrc.clone(getPool(AtubaOrc.class), m);
				break;
			case 495: // 네루가 오크
				mi = NerugaOrc.clone(getPool(NerugaOrc.class), m);
				break;
			case 496: // 간디 오크
				mi = GandiOrc.clone(getPool(GandiOrc.class), m);
				break;
			case 497: // 로바 오크
				mi = RovaOrc.clone(getPool(RovaOrc.class), m);
				break;
			case 498: // 두다-마라 오크
				mi = DudaMaraOrc.clone(getPool(DudaMaraOrc.class), m);
				break;
			case 758: // 브롭
				mi = Blob.clone(getPool(Blob.class), m);
				break;
			case 989: // 트롤
			case 1716: // 굶주린 트롤
				mi = Troll.clone(getPool(Troll.class), m);
				break;
//			case 1041: // 오크좀비
//				mi = OrcZombie.clone(getPool(OrcZombie.class), m);
//				break;
			case 1042: // 다크엘프
				mi = DarkElf.clone(getPool(DarkElf.class), m);
				break;
			case 1046: // 그렘린
				mi = Gremlin.clone(getPool(Gremlin.class), m);
				break;
			case 16148: // 라미아
				mi = Ramia.clone(getPool(Ramia.class), m);
				break;
			case 1490:	// 머맨
				mi = Merman.clone(getPool(Merman.class), m);
				break; 
			case 1554: // 도펠갱어
				mi = Doppelganger.clone(getPool(Doppelganger.class), m);
				break;
			case 1571: // 폭탄꽃
				mi = BombFlower.clone(getPool(BombFlower.class), m);
				break;
			case 959: // 하피
				mi = Harphy.clone(getPool(Harphy.class), m);
				break;
			case 1176: // 그리폰
				mi = Gryphon.clone(getPool(Gryphon.class), m);
				break;
			case 2017: // 다크마르
				mi = Darkmar.clone(getPool(Darkmar.class), m);
				break;
			case 2020: // 언데드의 배신자
				mi = BetrayerOfUndead.clone(getPool(BetrayerOfUndead.class), m);
				break;
			case 2063: // 잭-O-랜턴
			case 2064: // 잭-0-랜턴
				mi = JackLantern.clone(getPool(JackLantern.class), m);
				break;
			case 2073: // 변종 거대 여왕 개미
				mi = MutantGiantQueenAnt.clone(getPool(MutantGiantQueenAnt.class), m);
				break;
			case 2219: // 배신당한 오크대장
				mi = BetrayedOrcChief.clone(getPool(BetrayedOrcChief.class), m);
				break;
			case 2488:
				mi = Unicorn.clone(getPool(Unicorn.class), m);
				break;
			case 7444: // 파우스트의 악령
				mi = Faust_Ghost.clone(getPool(Faust_Ghost.class), m);
				break;
			case 28954: // 라버본 해골
				mi = Labourbon.clone(getPool(Labourbon.class), m);
				break;
			case 1711: // 계곡 라이칸스로프
			case 1700: // 계곡 곰
			case 1704: // 계곡 하피
			case 1714: // 계곡 오우거
			case 1696: // 섬 가스트
			case 1697: // 섬 가스트 로드
			case 1709: // 섬 코카트리스
			case 1701: // 섬 크로코다일
				mi = Secret_Book.clone(getPool(Secret_Book.class), m);
				break;
			case 17865: // 쿠커스
				mi = Cuckoos.clone(getPool(Cuckoos.class), m);
				break;
			case 1705: // 스타이져
				mi = Stabilizer.clone(getPool(Stabilizer.class), m);
				break;
			case 1739: // 저주받은 에틴
				mi = Cursed_Ettin.clone(getPool(Cursed_Ettin.class), m);
				break;
			case 1745: // 봉인된 서큐버스 퀸
				mi = Sealed_Succubus.clone(getPool(Sealed_Succubus.class), m);
				break;
			case 14593: // 몬스터 트랩
				mi = Monster_trap.clone(getPool(Monster_trap.class), m);
				break;
			case 17667: // 파란 색 용아병의 혼령
				mi = YongahBlue.clone(getPool(YongahBlue.class), m);
				break;
			case 17661: // 노란 색 용아병의 혼령
				mi = YongahYellow.clone(getPool(YongahYellow.class), m);
				break;

			case 19884: // 1층 메두사
			case 19889: // 2층 불신의 다이어울프
			case 19896: // 3층 공포의 켈베로스
			case 19906: // 4층 죽음의 해골 근위병
			case 19908: // 5층 지옥의 고스트(녹색)
			case 19918: // 6층 불사의 좀비 장군
			case 19921: // 7층 잔혹한 샤벨타이거
			case 19926: // 8층 어둠의 불타는 전사
			case 19933: // 9층 불멸의 해골 기사
			case 19938: // 10층 오만한 아스모데우스
				mi = Oman_Monster.clone(getPool(Oman_Monster.class), m);
				break;
			case 12410: // 감시자 리퍼
				mi = Grimreaper.clone(getPool(Grimreaper.class), m);
				break;
			case 3139: // 라이아
			case 3798: // 암살군왕 슬레이브
			case 3799: // 암살단장 블레이즈
			case 3800: // 친위대장 카이트
			case 3801: // 피의 암살단
			case 3803: // 라스타바드 친위대
			case 3804: // 마수군왕 바란카
			case 3805: // 마수단장 카이바르
			case 3806: // 사단장 싱클레어
			case 3807: // 여단장 다크펜서
			case 3814: // 팬텀 나이트
			case 3816: // 신관장 바운티
			case 3817: // 마법단장 카르미엘
			case 3822: // 어둠의 수호자
			case 3824: // 암흑의마법사
			case 3825: // 명법군왕 헬바인
			case 3826: // 명법단장 크리퍼스
			case 3827: // 용병대장 메파이스토
			case 3828: // 어둠의 복수자
			case 3829: // 핏빛 기사
			case 4296: // 대법관 바로드
			case 4297: // 대법관 바로메스
			case 4298: // 대법관 라미아스
			case 4299: // 대법관 이데아
			case 4300: // 대법관 케이나
			case 4301: // 대법관 티아메스
			case 4302: // 대법관 비아타스
			case 4303: // 대법관 엔디아스
			case 4304: // 장로 수행원
			case 4383: // 라스타바드 근위대
			case 4465: // 부제사장 카산드라
				mi = LastavardBoss.clone(getPool(LastavardBoss.class), m);
				break;
			case 3887: // 라스타바드 문지기
				mi = LastavardDoorMan.clone(getPool(LastavardDoorMan.class), m);
				break;
			case 13229966: // 얼음성 문지기
			case 132291395: 
			case 132295502: 
			case 132291791: 
			case 132291786: 
			case 1771: // 얼음여왕
			case 5525: // 아이스데몬
				mi = IceDungeonDoorMan.clone(getPool(IceDungeonDoorMan.class), m);
				break;
			case 24231: // 레서 데몬
			case 2656: // 혼돈의 사제
				mi = Chaospriest.clone(getPool(Chaospriest.class), m);
				break;
			case 4986: // 선택받지 못한 자
				mi = NotSelectedPerson.clone(getPool(NotSelectedPerson.class), m);
				break;
			case 2218: // 얼음여왕의시녀
				mi = IceQueenHandmaiden.clone(getPool(IceQueenHandmaiden.class), m);
				break;
			case 19893: // 불신의 서큐버스
			case 19928: // 암흑의 서큐버스 퀸
			case 19936: // 오만한 서큐버스 퀸
			case 1000: // 서큐버스
			case 1019: // 서큐버스 퀸
				mi = Succubus.clone(getPool(Succubus.class), m);
				break;
			case 19870: // 좀비
				mi = Ghoul.clone(getPool(Ghoul.class), m);
				break;
			case 19869: // 에틴
				mi = Ettin.clone(getPool(Ettin.class), m);
				break;
			default:
				switch (m.getGfx()) {
				case 6632:
				case 6634:
				case 6636:
				case 6638:
					mi = IceMonster.clone(getPool(IceMonster.class), m);
					break;
				default:
					mi = MonsterInstance.clone(getPool(MonsterInstance.class), m);
					break;
				}
			}
			mi.setObjectId(ServerDatabase.nextEtcObjId());
			mi.setGfx(m.getGfx());
			mi.setGfxMode(m.getGfxMode());
			mi.setClassGfx(m.getGfx());
			mi.setClassGfxMode(m.getGfxMode());
			mi.setName(m.getNameId());
			mi.setLevel(m.getLevel());
			mi.setExp(m.getExp());
			mi.setMaxHp(m.getHp());
			mi.setMaxMp(m.getMp());
			mi.setNowHp(m.getHp());
			mi.setNowMp(m.getMp());
			mi.setLawful(m.getLawful());
			mi.setStr(mi.getLevel() / 2 < 25 ? 25 : mi.getLevel() / 2);
			mi.setDex(mi.getLevel() / 2 < 25 ? 25 : mi.getLevel() / 2);
			mi.setInt(mi.getLevel() / 2 < 25 ? 25 : mi.getLevel() / 2);
			mi.setCon(m.getCon());
			mi.setWis(m.getWis());
			mi.setCha(m.getCha());
			mi.setEarthress(m.getResistanceEarth());
			mi.setFireress(m.getResistanceFire());
			mi.setWindress(m.getResistanceWind());
			mi.setWaterress(m.getResistanceWater());
			mi.setAiStatus(Lineage.AI_STATUS_WALK);
		}
		return mi;
	}

	
	
	
	static public void CancellationMonsterRenew(MonsterInstance mi, Monster m) {
	    newInstance(mi, m);
	    mi.setMonster(m);
	    mi.clearExpList();
	    mi.setGfxMode(28);
	    mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
	    mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	    if (m.getGfx() == 30) {
	        performActionAfterChange(mi);
	    }
	}

	private static void performActionAfterChange(MonsterInstance mi) {
	    if (mi.getGfxMode() == Lineage.GFX_MODE_OPEN) {
	        mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, Lineage.GFX_MODE_ALT_ATTACK), false);
	        mi.setGfxMode(Lineage.GFX_MODE_WALK);
	        mi.toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), mi), false);
	    }
	}

	static public void changeMonsterRenew(MonsterInstance mi, Monster m) {

		newInstance(mi, m);
		mi.setMonster(m);
		mi.clearExpList();
		mi.setGfxMode(0);
		mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
		mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	}
	
	static public void changeMonster(MonsterInstance mi, Monster m) {

		newInstance(mi, m);
		mi.setMonster(m);
		mi.readDrop();
		mi.clearExpList();
		mi.setGfxMode(0);
		mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
		mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	}

	/**
	 * 파우스트
	 * 
	 * @param mi
	 * @param m
	 */
	static public void changeMonsterBoss(MonsterInstance mi, Monster m) {

		newInstance(mi, m);
		mi.setMonster(m);
		mi.readDrop();
		mi.clearExpList();
		mi.setGfxMode(0);
		mi.setBoss(true);
		BossController.appendBossList(mi);
		if (Lineage_Balance.faust_spawn_msg) {
			World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 780, "\\fY" + Util.getMapName(mi) + " " + mi.getMonster().getName()));
		}
		mi.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), mi), false);
		mi.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), mi), false);
	}

	static private MonsterInstance newInstance(MonsterInstance mi, Monster m) {
		mi.setObjectId(mi.getObjectId() == 0 ? ServerDatabase.nextNpcObjId() : mi.getObjectId());
		mi.setGfx(m.getGfx());
		mi.setGfxMode(m.getGfxMode());
		mi.setClassGfx(m.getGfx());
		mi.setClassGfxMode(m.getGfxMode());
		mi.setName(m.getNameId());
		mi.setLevel(m.getLevel());
		mi.setExp(m.getExp());
		mi.setMaxHp(m.getHp());
		mi.setMaxMp(m.getMp());
		mi.setNowHp(m.getHp());
		mi.setNowMp(m.getMp());
		mi.setLawful(m.getLawful());
		mi.setEarthress(m.getResistanceEarth());
		mi.setFireress(m.getResistanceFire());
		mi.setWindress(m.getResistanceWind());
		mi.setWaterress(m.getResistanceWater());

		return mi;
	}

	static private MonsterInstance getPool(Class<?> c) {
		synchronized (pool) {
			MonsterInstance mon = null;
			for (MonsterInstance mi : pool) {
				if (mi.getClass().equals(c)) {
					mon = mi;
					break;
				}
			}
			if (mon != null)
				pool.remove(mon);
			return mon;
		}
	}

	static public void setPool(MonsterInstance mi) {
		mi.close();
		synchronized (pool) {
			if (!pool.contains(mi))
				pool.add(mi);
		}
	}

	static public int getPoolSize() {
		return pool.size();
	}

	static public void insert(Connection con, String name, String monster, boolean random, int count, int loc_size, int x, int y, int map, int re_spawn, boolean groups, String monster_1, int monster_1_count,
			String monster_2, int monster_2_count, String monster_3, int monster_3_count, String monster_4, int monster_4_count, String monster_5, int monster_5_count, String monster_6, int monster_6_count, String monster_7, int monster_7_count, String monster_8, int monster_8_count) {
		PreparedStatement st = null;
		int uid = getUid(con);

		try {
			st = con.prepareStatement(
					"INSERT INTO monster_spawnlist SET uid=?, name=?, monster=?, random=?, count=?, loc_size=?, spawn_x=?, spawn_y=?, spawn_map=?, re_spawn_min=?, re_spawn_max=?, groups=?, monster_1=?, monster_1_count=?, monster_2=?, monster_2_count=?, monster_3=?, monster_3_count=?, monster_4=?, monster_4_count=?");
			st.setInt(1, uid);
			st.setString(2, name);
			st.setString(3, monster);
			st.setString(4, String.valueOf(random));
			st.setInt(5, count);
			st.setInt(6, loc_size);
			st.setInt(7, x);
			st.setInt(8, y);
			st.setInt(9, map);
			st.setInt(10, re_spawn);
			st.setInt(11, re_spawn);
			st.setString(12, String.valueOf(groups));
			st.setString(13, monster_1);
			st.setInt(14, monster_1_count);
			st.setString(15, monster_2);
			st.setInt(16, monster_2_count);
			st.setString(17, monster_3);
			st.setInt(18, monster_3_count);
			st.setString(19, monster_4);
			st.setInt(20, monster_4_count);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert()\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	static public int getUid(Connection con) {
		PreparedStatement st = null;
		ResultSet rs = null;
		int uid = 0;

		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();
			while (rs.next()) {
				int temp = rs.getInt("uid");

				if (uid < temp)
					uid = temp;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getUid(Connection con)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		return uid + 1;
	}

	/**
	 * 
	 * 어느 맵에 스폰되어있는지 확인해주는 함수
	 * 
	 * @param map
	 */

	static public MonsterInstance find(String name) {
		synchronized (list) {
			for (MonsterInstance m : list) {
				if (m.getMonster().getName().equalsIgnoreCase(name))
					return m;
			}
			return null;
		}
	}

	public static void reload(int mapId) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		for (MonsterInstance mon : World.getMonsterList()) {
			if (mon.getMap() == mapId && !mon.isBoss()) {
				World.removeMonster(mon);
				mon.toAiThreadDelete();
				mon.close();
			}
		}

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM monster_spawnlist WHERE spawn_map=?");
			st.setInt(1, mapId);
			rs = st.executeQuery();

			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));

				if (m != null) {

					if (temp.size() < 1) {
						temp.add(m);
					} else {
						boolean result = true;

						for (int i = 0; i < temp.size(); i++) {
							if (temp.get(i).getName().equalsIgnoreCase(m.getName())) {
								result = false;
								break;
							}
						}

						if (result)
							temp.add(m);
					}
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setSentry(Boolean.valueOf(rs.getString("sentry")));
					ms.setHeading(rs.getInt("heading"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while (stt.hasMoreTokens()) {
						String map = stt.nextToken();
						if (map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}
					ms.setReSpawn(rs.getInt("re_spawn_min") * 1000);
					if (rs.getInt("re_spawn_max") < rs.getInt("re_spawn_min"))
						ms.setReSpawnMax(rs.getInt("re_spawn_min") * 1000);
					else
						ms.setReSpawnMax(rs.getInt("re_spawn_max") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if(ms.isGroup()){
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						Monster g5 = MonsterDatabase.find(rs.getString("monster_5"));
						Monster g6 = MonsterDatabase.find(rs.getString("monster_6"));
						Monster g7 = MonsterDatabase.find(rs.getString("monster_7"));
						Monster g8 = MonsterDatabase.find(rs.getString("monster_8"));
						if(g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if(g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if(g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if(g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
						if(g5 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_5_count")));
						if(g6 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_6_count")));
						if(g7 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_7_count")));
						if(g8 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_8_count")));
					}
					toSpawnMonster(ms, null);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload(int map)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	public static void reload() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		for (MonsterInstance mon : World.getMonsterList()) {
			if (!mon.isBoss()) {
				World.removeMonster(mon);
				mon.toAiThreadDelete();
				mon.close();
			}
		}

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM monster_spawnlist");
			rs = st.executeQuery();

			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));

				if (m != null) {

					if (temp.size() < 1) {
						temp.add(m);
					} else {
						boolean result = true;

						for (int i = 0; i < temp.size(); i++) {
							if (temp.get(i).getName().equalsIgnoreCase(m.getName())) {
								result = false;
								break;
							}
						}

						if (result)
							temp.add(m);
					}
					MonsterSpawnlist ms = new MonsterSpawnlist();
					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(m);
					ms.setRandom(rs.getString("random").equalsIgnoreCase("true"));
					ms.setCount(rs.getInt("count"));
					ms.setLocSize(rs.getInt("loc_size"));
					ms.setSentry(Boolean.valueOf(rs.getString("sentry")));
					ms.setHeading(rs.getInt("heading"));
					ms.setX(rs.getInt("spawn_x"));
					ms.setY(rs.getInt("spawn_y"));
					StringTokenizer stt = new StringTokenizer(rs.getString("spawn_map"), "|");
					while (stt.hasMoreTokens()) {
						String map = stt.nextToken();
						if (map.length() > 0)
							ms.getMap().add(Integer.valueOf(map));
					}

					ms.setReSpawn(rs.getInt("re_spawn_min") * 1000);

					if (rs.getInt("re_spawn_max") < rs.getInt("re_spawn_min"))
						ms.setReSpawnMax(rs.getInt("re_spawn_min") * 1000);
					else
						ms.setReSpawnMax(rs.getInt("re_spawn_max") * 1000);
					ms.setGroup(rs.getString("groups").equalsIgnoreCase("true"));
					if(ms.isGroup()){
						Monster g1 = MonsterDatabase.find(rs.getString("monster_1"));
						Monster g2 = MonsterDatabase.find(rs.getString("monster_2"));
						Monster g3 = MonsterDatabase.find(rs.getString("monster_3"));
						Monster g4 = MonsterDatabase.find(rs.getString("monster_4"));
						Monster g5 = MonsterDatabase.find(rs.getString("monster_5"));
						Monster g6 = MonsterDatabase.find(rs.getString("monster_6"));
						Monster g7 = MonsterDatabase.find(rs.getString("monster_7"));
						Monster g8 = MonsterDatabase.find(rs.getString("monster_8"));
						if(g1 != null)
							ms.getListGroup().add(new MonsterGroup(g1, rs.getInt("monster_1_count")));
						if(g2 != null)
							ms.getListGroup().add(new MonsterGroup(g2, rs.getInt("monster_2_count")));
						if(g3 != null)
							ms.getListGroup().add(new MonsterGroup(g3, rs.getInt("monster_3_count")));
						if(g4 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_4_count")));
						if(g5 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_5_count")));
						if(g6 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_6_count")));
						if(g7 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_7_count")));
						if(g8 != null)
							ms.getListGroup().add(new MonsterGroup(g4, rs.getInt("monster_8_count")));
					}
					toSpawnMonster(ms, null);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload(int map)\r\n", MonsterSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
}
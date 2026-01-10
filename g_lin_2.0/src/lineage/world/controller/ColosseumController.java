package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Item;
import lineage.bean.lineage.Colosseum;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_SoundEffect;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public final class ColosseumController {

	// 콜롯세움 목록
	static private List<Colosseum> list;

	static private Calendar calendar;

	// 콜롯세움 상태 목록
	static public enum COLOSSEUM_STATUS {
		휴식, 대기, 준비, 시작, 최종전, 종료,
	};

	static public void init() {
		TimeLine.start("ColosseumController..");

		list = new ArrayList<Colosseum>();
		calendar = Calendar.getInstance();

		if (Lineage.colosseum_giran)
			toGiran();

		TimeLine.end();
	}

	/**
	 * 무한대전에 참가 처리 함수.
	 * 
	 * @param pc
	 * @param c
	 */
	static public void toJoin(PcInstance pc, Colosseum c) {
		// 레벨 확인.
		if (c.getJoinMinLevel() > pc.getLevel() || c.getJoinMaxLevel() < pc.getLevel()) {
			ChattingController.toChatting(pc, String.format("참가 가능한 레벨은 %d~%d 입니다.", c.getJoinMinLevel(), c.getJoinMaxLevel()), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if (pc.getInventory().isAden(1000, true)) {
			// 이동.
			pc.toPotal(c.getX(), c.getY(), c.getMap());
		} else {
			// \f1아데나가 충분치 않습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189, "아데나"));
		}
	}

	/**
	 * 구분자로 해당 콜롯세움 객체 찾기.
	 * 
	 * @param type
	 * @return
	 */
	static public Colosseum find(String title) {
		for (Colosseum c : list) {
			if (c.getType().equalsIgnoreCase(title))
				return c;
		}
		return null;
	}

	/**
	 * 기란 콜롯세움 세팅.
	 */
	static private void toGiran() {
		Colosseum c = new Colosseum();
		c.setType("giran");
		c.setName("시 20분부터");
		c.setX(33505);
		c.setY(32734);
		c.setMap(88);
		c.setMaxStage(2); // 최대 2군.
		//c.setTimeStart(new int[] { Lineage.colosseum_time }); // 콜롯세움이 활성화될 리니지월드 시간. 시단위
		c.setTimeStart(Lineage.colosseum_time);
		c.setTimeEnd(new int[] { 274, 278 }); // 각군 마다 진행되는 대기시간. 초단위
		c.setTimeCool(new int[] { 120, 160 }); // 각군이 완료된후 휴식시간. 초단위
		// 각 군에 따른 아이템 갯수
		c.setStageItemCount(new int[][] {
				// 1군
				{ 200, 30, 20, 10, 5, 5 },
				// 2군
				{ 200, 30, 20, 10, 5, 5, 10, 10, 10 }, });
		// 1군 아이템
		List<String> l = new ArrayList<String>();
		l.add("아데나");
		l.add("체력 회복제");
		l.add("고급 체력 회복제");
		l.add("강력 체력 회복제");
		l.add("속도향상 물약");
		l.add("엔트의 줄기");
		c.getListItem().put(0, l);
		// 2군 아이템
		l = new ArrayList<String>();
		l.add("아데나");
		l.add("체력 회복제");
		l.add("고급 체력 회복제");
		l.add("강력 체력 회복제");
		l.add("속도향상 물약");
		l.add("엔트의 줄기");
		l.add("용기의 물약");
		l.add("악마의 피");
		l.add("엘븐 와퍼");
		c.getListItem().put(1, l);
		c.setStageCount(new int[][] {
				// 1군
				{ 20, 10, 20, 10, 10, 20, 20, 15, 10, 20, 10, 18, 10, 12, 11, 18, 15, 8, 8, 6 },
				// 2군
				{ 20, 10, 10, 20, 15, 20, 10, 15, 10, 10, 20, 10, 15, 10, 15, 10, 3 }, });
		// 1군 몬스터
		l = new ArrayList<String>();
		l.add("[무한대전1군]늑대");
		l.add("[무한대전1군]멧돼지");
		l.add("[무한대전1군]곰");
		l.add("[무한대전1군]호랑이");
		l.add("[무한대전1군]아울베어");
		l.add("[무한대전1군]임프");
		l.add("[무한대전1군]좀비");
		l.add("[무한대전1군]괴물눈");
		l.add("[무한대전1군]오크전사");
		l.add("[무한대전1군]놀");
		l.add("[무한대전1군]늑대인간");
		l.add("[무한대전1군]크로울링크로");
		l.add("[무한대전1군]난쟁이족전사");
		l.add("[무한대전1군]해골");
		l.add("[무한대전1군]오크좀비");
		l.add("[무한대전1군]악어");
		l.add("[무한대전1군]거대개미");
		l.add("[무한대전1군]해골궁수");
		l.add("[무한대전1군]바쿡");
		l.add("[무한대전1군]사이클롭스");
		c.getList().put(0, l);
		// 2군 몬스터
		l = new ArrayList<String>();
		l.add("[무한대전2군]아이언골렘");
		l.add("[무한대전2군]해골근위병");
		l.add("[무한대전2군]해골돌격병");
		l.add("[무한대전2군]혈거인간");
		l.add("[무한대전2군]오크스카우트");
		l.add("[무한대전2군]홉고블린");
		l.add("[무한대전2군]리자드맨");
		l.add("[무한대전2군]구울");
		l.add("[무한대전2군]크랩맨");
		l.add("[무한대전2군]유령(레드)");
		l.add("[무한대전2군]유령(그린)");
		l.add("[무한대전2군]라이칸스로프");
		l.add("[무한대전2군]웅골리언트");
		l.add("[무한대전2군]거대병정개미");
		l.add("[무한대전2군]거대쥐");
		l.add("[무한대전2군]가스트로드");
		l.add("[무한대전2군]드레이크");
		c.getList().put(1, l);
		// 보스 개체수
		c.setBossCount(new int[] { 2 });
		// 최종 보스
		c.getListBoss().add("[무한대전]데스나이트");
		// 기본 정보
		c.setJoinClass(1 + 2 + 4 + 8); // 참가 가능 클래스
		c.setJoinSex(0); // 참가 가능 성별
		c.setJoinMinLevel(10); // 참가 가능 최저 레벨
		c.setJoinMaxLevel(70); // 참가 가능 최고 레벨
		c.setJoinTeleport(false); // 텔레포트
		c.setJoinResurrection(true); // 부활
		c.setJoinPotion(true); // 포션 사용
		c.setJoinHp(true); // 자연 HP 변화
		c.setJoinMp(true); // 자연 MP 변화
		c.setJoinSummon(false); // 서먼/테이밍 몬스터 및 개 사용
		c.setJoinPvP(true); // PvP 방식
		list.add(c);
	}

	/**
	 * 타이머에서 주기적으로 호출됨.
	 * 
	 * @param time
	 */
	static public void toTimer(long time) {

		//if (PluginController.init(ColosseumController.class, "toTimer", time, list) != null)
		//	return;
		
		// 각 마을별 시간 확인.
		int h = Util.getHours(time);
		int m = Util.getMinutes(time);
		int s = Util.getSeconds(time);

		for (Colosseum c : list) {
			for (int c_h : c.getTimeStart()) {
				if (c.getLastTime() == c_h)
					continue;
				if ((c_h == h && m == 5)
						|| c.getStatus() != COLOSSEUM_STATUS.휴식) {
					switch (c.getStatus()) {
					case 휴식:
						// 안내 멘트 날리기.
						switch (++c.timer_ment_cnt) {
						case 1:
							// 전체채팅 잠시 닫기.
							ChattingController.setGlobal(false);
							// 안내 멘트.
							ChattingController.toChatting(null, "안녕하세요. 리니지입니다.", Lineage.CHATTING_MODE_GLOBAL);
							break;
						case 6:
							// 안내 멘트.
							ChattingController.toChatting(null, String.format("잠시후 %s 기란 콜롯세움에서", c.toString(h)), Lineage.CHATTING_MODE_GLOBAL);
							break;
						case 9:
							// 안내 멘트.
							ChattingController.toChatting(null, "무한대전이 진행되오니 많은 참여 바랍니다.", Lineage.CHATTING_MODE_GLOBAL);
							// 상태 변경.
							c.setStatus(COLOSSEUM_STATUS.대기);
							// 초기화.
							c.timer_ment_cnt = 0;
							// 전체채팅 다시 활성화.
							ChattingController.setGlobal(true);
							break;
						}
						break;
					case 대기:
						// 20분 이라면 상태 변경.
						if (m == 20)
							c.setStatus(COLOSSEUM_STATUS.준비);
						break;
					case 준비:
						switch (++c.timer_ment_cnt) {
						case 1:
							// 이제 곧 몬스터들이 등장할 것입니다. 건투를 빕니다.
							World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "콜롯세움 관리자: 이제 곧 몬스터들이 등장할 것입니다. 건투를 빕니다."), c.getMap());
							break;
						case 11:
							// "10초뒤에 경기를 시작 합니다."
							World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "콜롯세움 관리자: 10초뒤에 경기를 시작 합니다."), c.getMap());
							break;
						default:
							if (c.timer_ment_cnt > 14) {
								// 5 !!
								int cnt = 20 - c.timer_ment_cnt;
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("콜롯세움 관리자: %d !!", cnt)), c.getMap());
								if (cnt <= 1) {
									// 상태 변경.
									c.setStatus(COLOSSEUM_STATUS.시작);
									// 초기화.
									c.timer_ment_cnt = 0;
								}
							}
							break;
						}
						break;
					case 시작:
						if (c.getMaxStage() <= c.nowStage) {
							// 보스전
							if (++c.timer_ment_cnt % 60 == 0) {
								int cnt = 6 - (c.timer_ment_cnt / 60);
								if (cnt > 0) {
									// 5분 후에 최종전이 시작됩니다.
									World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("콜롯세움 관리자: %d분 후에 최종전이 시작됩니다.", cnt)),
											c.getMap());
								} else {
									// 상태 변경.
									c.setStatus(COLOSSEUM_STATUS.최종전);
									// 초기화.
									c.timer_ment_cnt = 0;
								}
							}

						} else {
							// 각 군별 대기시간값
							int cool_sleep = c.getTimeCool()[c.nowStage] * 1000; // 현재
																					// 스테이지가
																					// 끝나고
																					// 휴식하는
																					// 대기시간값
							int nowStage = c.nowStage + 1; // 현재 진행중인 스테이지 값.
							int list_spawn_max = c.getList().get(c.nowStage).size(); // 현재
																						// 스테이지에
																						// 스폰될
																						// 몬스터
																						// 최대
																						// 갯수.
							int spawn_sleep = c.getTimeEnd()[c.nowStage] / list_spawn_max; // 현재
																							// 스테이지에
																							// 스폰될
																							// 몬스터들이
																							// 스폰될
																							// 주기값.

							// 시간 설정이 안되엇다면 시간설정과 함께 안내 멘트.
							if (c.timer_time == 0) {
								// 제 1 군 투입!
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("콜롯세움 관리자: 제 %d 군 투입!", nowStage)), c.getMap());
								c.timer_time = time;
							}

							// 몬스터 투입.
							if (c.list_spawn_idx < list_spawn_max && c.timer_ment_cnt++ % spawn_sleep == 0) {
								// 스폰
								String name = c.getList().get(c.nowStage).get(c.list_spawn_idx);
								int count = c.getStageCount()[c.nowStage][c.list_spawn_idx];
								for (int i = 0; i < count; ++i) {
									MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find3(name));
									if (mi != null) {
										c.getListSpawn().add(mi);
										MonsterSpawnlistDatabase.toSpawnMonster(mi, World.get_map(c.getMap()), true, c.getX(), c.getY(), c.getMap(), Lineage.SEARCH_MONSTER_TARGET_LOCATION, 0, 0, false, true);
									}
								}
								// 값 갱신.
								c.list_spawn_idx += 1;
							}

							// 모든 몬스터가 스폰됫을경우.
							if (c.timer_cool_time == 0 && c.list_spawn_idx >= list_spawn_max) {
								// 제 1 군의 투입이 완료되었습니다.
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("콜롯세움 관리자: 제 %d 군의 투입이 완료되었습니다.", nowStage)),
										c.getMap());
								// 아이템 드랍.
								List<String> list = c.getListItem().get(c.nowStage);
								for (int i = 0; i < list.size(); ++i)
									for (String data : list) {
										StringTokenizer st = new StringTokenizer(data, "|");
										String name = st.nextToken();
										int count = c.getStageItemCount()[c.nowStage][i];

										for (int j = 0; j < 10; ++j) {
											ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(name));
											if (ii != null) {
												ii.setObjectId(ServerDatabase.nextItemObjId());
												ii.setCount(count);
												ii.toDrop(null);
												ii.toTeleport(Util.random(33492, 33516), Util.random(32723, 32747), 88, true);
											}
										}
									}
								c.timer_cool_time = time;
							}

							// 쿨타임(휴식) 오버됫을경우 다음스테이지로 이동.
							if (c.timer_cool_time != 0 && c.timer_cool_time + cool_sleep <= time) {
								// 맵에 드랍된 아이템 제거.
								World.clearWorldItem(c.getMap());
								// 스테이지 변경.
								c.nowStage += 1;
								// 초기화.
								c.timer_cool_time = c.timer_time = c.timer_ment_cnt = c.list_spawn_idx = 0;
							}
						}
						break;
					case 최종전:
						if (c.timer_ment_cnt == 0) {
							// 최종전 개시! 제한 시간은 5분입니다.
							World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "콜롯세움 관리자: 최종전 개시! 제한 시간은 5분입니다."), c.getMap());
							// 몬스터 스폰.
							for (int i = 0; i < c.getListBoss().size(); ++i) {
								String name = c.getListBoss().get(i);
								int count = c.getBossCount()[i];
								for (int j = 0; j < count; ++j) {
									MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find3(name));
									if (mi != null) {
										c.getListSpawn().add(mi);
										MonsterSpawnlistDatabase.toSpawnMonster(mi, World.get_map(c.getMap()), true, c.getX(), c.getY(), c.getMap(), Lineage.SEARCH_MONSTER_TARGET_LOCATION, 0, 0, true, true);
									}
								}
							}
						}

						if (++c.timer_ment_cnt % 60 == 0) {
							int cnt = 5 - (c.timer_ment_cnt / 60);
							if (cnt > 0) {
								// 경기 종료까지 4분 남았습니다.
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("콜롯세움 관리자: 경기 종료까지 %d분 남았습니다.", cnt)),
										c.getMap());
							} else {
								// 상태 변경.
								c.setStatus(COLOSSEUM_STATUS.종료);
								// 초기화.
								c.timer_ment_cnt = 0;
							}
						}

						break;
					case 종료:
						// 해당 맵에 있는 사용자 마을로 귀환.
						for (PcInstance pc : World.getPcList()) {
							if (pc.getMap() == c.getMap()) {
								TeleportHomeDatabase.toLocation(pc);
								pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), false);
							}
						}
						// 맵에 드랍된 아이템 제거.
						World.clearWorldItem(c.getMap());
						// 맵에 스폰된 몬스터 제거.
						for (MonsterInstance mi : c.getListSpawn())
							mi.toAiThreadDelete();
						c.getListSpawn().clear();
						// 초기화.
						c.setStatus(COLOSSEUM_STATUS.휴식);
						c.timer_cool_time = c.timer_time = c.timer_ment_cnt = c.list_spawn_idx = c.nowStage = 0;
						break;
					}
					break;
				}
			}
		}
	}

}

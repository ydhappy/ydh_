package lineage.world.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import goldbitna.event.MonsterSummoning;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public final class MonsterSummonController {

	// 이벤트 위치 목록
	static private List<MonsterSummoning> list;
	
	static private Calendar calendar;	
	
	// 이벤트 시작, 종료 운영자 명령어에서 사용중 
	public static boolean isEventReady; // 이벤트 시작
	public static boolean isEventStart; // 이벤트 시작
	public static boolean isEventEnd;   // 이벤트 종료
	
	// 이벤트 상태 목록
	static public enum EVENT_STATUS {
		휴식, 대기, 준비, 시작, 최종전, 종료,
	};	
	
	static public void init() {
		TimeLine.start("MonsterSummonController..");
		
		list = new ArrayList<MonsterSummoning>();
		calendar = Calendar.getInstance();
		
		if (Lineage.mon_event)
			toEvent();

		isEventStart = isEventEnd  = false;
		
		TimeLine.end();
	}	

	/**
	 * 구분자로 해당 이벤트 객체 찾기.
	 * 
	 * @param type
	 * @return
	 */
	static public MonsterSummoning find(String title) {
		for (MonsterSummoning e : list) {
			if (e.getType().equalsIgnoreCase(title))
				return e;
		}
		return null;
	}
	

	// 몬스터 소환 이벤트에 참여하시겠습니까? (y/n)
	static public void toAskSummonEvent(String time) {
		for (PcInstance pc : World.getPcList()) {
			if (!pc.isWorldDelete())
				pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 1415, time));
		}
	}

	// YES / NO 대답을 했을시
	static public void toAsk(PcInstance pc, boolean yes) {
		if (yes && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && !pc.isFishing()) {
			pc.toPotal(Util.random(Lineage.event_map_min_x, Lineage.event_map_max_x), Util.random(Lineage.event_map_min_y, Lineage.event_map_max_y), Lineage.mon_event_map);
		} else {
			if (yes) {
				if (pc.isDead() || pc.isLock())
					ChattingController.toChatting(pc, "[알림] 현재 상태에서는 입장이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
				if (pc.isFishing())
					ChattingController.toChatting(pc, "[알림] 낚시중엔 입장이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	/**
	 * 몬스터 이벤트 세팅.
	 */
	static private void toEvent() {
		MonsterSummoning e = new MonsterSummoning();
	    // 현재 시간 가져오기
	    LocalDateTime now = LocalDateTime.now();
	    int currentHour = now.getHour();
	    // currentHour를 int[] 배열에 담아서 전달
	    int[] timeArray = { currentHour };
	    
		e.setType("Event");
		e.setName("시 10분 부터 이벤트");
		e.setX(Lineage.mon_event_x);
		e.setY(Lineage.mon_event_y);
		e.setMap(Lineage.mon_event_map);
		e.setMaxStage(4); // 최대 스테이지.
		if (isEventReady && e.getStatus() == EVENT_STATUS.휴식){
			e.setTimeStart(timeArray);
		} else {	
		e.setTimeStart(Lineage.mon_event_time);
		}
		e.setStatus(EVENT_STATUS.휴식);
		e.setTimeEnd(new int[] { 300, 300, 300, 300 }); // 각 스테이지 마다 진행되는 대기시간. 초단위
		e.setTimeCool(new int[] { 120, 120, 120, 120 }); // 각 스테이지 완료된후 휴식시간. 초단위
		// 각 군에 따른 아이템 갯수
		e.setStageItemCount(new int[][] {
			// 스테이지 1
			{ 30000, 2, 2 },
			// 스테이지 2
			{ 50000, 3, 3 },
			// 스테이지 3
			{ 70000, 5, 5 },
			// 스테이지 4
			{ 100000, 10, 10 }, });
		// 스테이지1 아이템
		List<String> l = new ArrayList<String>();
		l.add("아데나");
		l.add("무기 마법 주문서");
		l.add("갑옷 마법 주문서");
		e.getListItem().put(0, l);
		// 스테이지2 아이템
		l = new ArrayList<String>();
		l.add("아데나");
		l.add("무기 마법 주문서");
		l.add("갑옷 마법 주문서");
		e.getListItem().put(1, l);
		// 스테이지3 아이템
		l = new ArrayList<String>();
		l.add("아데나");
		l.add("무기 마법 주문서");
		l.add("갑옷 마법 주문서");
		e.getListItem().put(2, l);		
		// 스테이지4 아이템
		l = new ArrayList<String>();
		l.add("아데나");
		l.add("무기 마법 주문서");
		l.add("갑옷 마법 주문서");
		e.getListItem().put(3, l);								
		e.setStageCount(new int[][] {		
			// 스테이지1 보스 수량
			{ 2, 2, 2, 2 },
			// 스테이지2 보스 수량
			{ 2, 2, 2, 2 },
			// 스테이지3 보스 수량
			{ 2, 2, 2, 2 },
			// 스테이지4 보스 수량
			{ 2, 2, 2, 2 }, });		
		// 1군 몬스터
		l = new ArrayList<String>();
		l.add("카스파");
		l.add("발터자르");
		l.add("메르키오르");
		l.add("세마");
		e.getList().put(0, l);	
		// 2군 몬스터
		l = new ArrayList<String>();
		l.add("이프리트");
		l.add("바포메트");
		l.add("베레스");
		l.add("커츠");
		e.getList().put(1, l);		
		// 3군 몬스터
		l = new ArrayList<String>();
		l.add("흑장로");
		l.add("아이스데몬");
		l.add("얼음여왕");
		l.add("데몬");
		e.getList().put(2, l);	
		// 4군 몬스터
		l = new ArrayList<String>();
		l.add("네크로맨서");
		l.add("데스나이트");
		l.add("아리오크");
		l.add("피닉스");
		e.getList().put(3, l);		
		// 보스 개체수
		e.setBossCount(new int[] { 1, 1, 1, 1 });
		// 최종 보스
		e.getListBoss().add("안타라스");
		e.getListBoss().add("발라카스");
		e.getListBoss().add("린드비오르");
		e.getListBoss().add("파푸리온");
		// 기본 정보	
		list.add(e);
	}

	/**
	 * 타이머에서 주기적으로 호출됨.
	 * 
	 * @param time
	 */
	static public void toTimer(long time) {		
	    // 현재 요일 구하기
	    int toDay = calendar.get(Calendar.DAY_OF_WEEK);
	    
	    // 각 마을별 시간 확인.
	    int h = Util.getHours(time);
	    int m = Util.getMinutes(time);
	    // 이벤트 시작 요일 리스트 가져오기
	    List<Integer> eventDays = Lineage.getMonsterSummonDayList();
	    
	    // 이벤트가 시작할 요일인지 확인
	    if (!eventDays.contains(toDay) && !isEventReady) {
	        return; // 오늘이 이벤트 시작일이 아니면 종료
	    }
	    
			for (MonsterSummoning e : list) {						
				for (int e_h : e.getTimeStart()) {
					if (e.getLastTime() == e_h)
						continue;
					if ((e_h == h && m == 5 || isEventReady || isEventStart || isEventEnd)
							|| e.getStatus() != EVENT_STATUS.휴식) {
						switch (e.getStatus()) {
					   
						case 휴식:
							// 안내 멘트 날리기.
							switch (++e.timer_ment_cnt) {
							case 1:
								// 전체채팅 잠시 닫기.
								ChattingController.setGlobal(false);
								// 안내 멘트.
								ChattingController.toChatting(null, "안녕하세요. 리니지입니다.", Lineage.CHATTING_MODE_GLOBAL);
								break;
							case 6:
								// 안내 멘트.
								if(!isEventReady){
								ChattingController.toChatting(null, String.format("잠시후 %s 콜로세움에서 몬스터", e.toString(h)), Lineage.CHATTING_MODE_GLOBAL);
								toAskSummonEvent("5분");
								} else {
									ChattingController.toChatting(null, "잠시 후 이벤트 콜로세움에서 보스 몬스터", Lineage.CHATTING_MODE_GLOBAL);
									toAskSummonEvent("잠시");
								}
								break;
							case 9:
								// 안내 멘트.
								ChattingController.toChatting(null, "소환 이벤트가 진행됩니다. 많은 참여 부탁드립니다.", Lineage.CHATTING_MODE_GLOBAL);
								// 상태 변경.
								e.setStatus(EVENT_STATUS.대기);
								// 초기화.
								e.timer_ment_cnt = 0;
								// 전체채팅 다시 활성화.
								ChattingController.setGlobal(true);
								break;
							}
							break;
						case 대기:
							// n시 10분 이라면 상태 변경.
							if (m == 10 || isEventStart)
								e.setStatus(EVENT_STATUS.준비);
							break;
						case 준비:
							switch (++e.timer_ment_cnt) {
							case 1:
								// 이제 곧 몬스터들이 등장할 것입니다. 건투를 빕니다.
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "이벤트 관리자: 이제 곧 몬스터가 소환 됩니다. Good Luck!"), e.getMap());
								break;
							case 11:
								// "10초뒤에 경기를 시작 합니다."
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "이벤트 관리자: 10초 뒤에 소환을 시작 합니다."), e.getMap());
								toAskSummonEvent("10초");
								break;
							default:
								if (e.timer_ment_cnt > 14) {
									// 5 !!
									int cnt = 20 - e.timer_ment_cnt;
									World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("이벤트 관리자: %d !!", cnt)), e.getMap());
									if (cnt <= 1 || isEventEnd) {
										// 상태 변경.
										e.setStatus(EVENT_STATUS.시작);
										// 초기화.
										e.timer_ment_cnt = 0;
									}
								}
								break;
							}
							break;
						case 시작:
							if (e.getMaxStage() <= e.nowStage || isEventEnd) {
								// 보스전
								if (++e.timer_ment_cnt % 60 == 0 || isEventEnd) {
									int cnt = 6 - (e.timer_ment_cnt / 60);
									if (cnt > 0 && !isEventEnd) {
										// 5분 후에 최종전이 시작됩니다.
										World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("이벤트 관리자: %d분 후에 마지막 소환이 시작됩니다.", cnt)),
												e.getMap());
									} else {
										// 상태 변경.
										e.setStatus(EVENT_STATUS.최종전);
										// 초기화.
										e.timer_ment_cnt = 0;
									}
								}

							} else {
							// 각 군별 대기시간값
							int cool_sleep = e.getTimeCool()[e.nowStage] * 1000; // 현재 스테이지가 끝나고 휴식하는 대기시간값
							int nowStage = e.nowStage + 1; // 현재 진행중인 스테이지 값.
							int list_spawn_max = e.getList().get(e.nowStage).size(); // 현재 스테이지에 스폰될 몬스터 최대 갯수.
							int spawn_sleep = e.getTimeEnd()[e.nowStage] / list_spawn_max; // 현재 스테이지에 스폰될 몬스터들이 스폰될
							// 주기값.							
							// 시간 설정이 안되엇다면 시간설정과 함께 안내 멘트.
							if (e.timer_time == 0) {
								// 제 1 군 소환!
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("이벤트 관리자: 제 %d 군 소환!", nowStage)), e.getMap());
								e.timer_time = time;
							}

							// 몬스터 투입.
							if (e.list_spawn_idx < list_spawn_max && e.timer_ment_cnt++ % spawn_sleep == 0 && !isEventEnd) {
								// 스폰
								String name = e.getList().get(e.nowStage).get(e.list_spawn_idx);
								int count = e.getStageCount()[e.nowStage][e.list_spawn_idx];							
								for (int i = 0; i < count; ++i) {
									MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find3(name));
									if (mi != null) {
										e.getListSpawn().add(mi);
										MonsterSpawnlistDatabase.toSpawnMonster(mi, World.get_map(e.getMap()), true, e.getX(), e.getY(), e.getMap(), Lineage.SEARCH_MONSTER_TARGET_LOCATION, 0, 0, false, true);
									}
								}
								// 값 갱신.
								e.list_spawn_idx += 1;
							}

							// 모든 몬스터가 스폰됫을경우.
							if (e.timer_cool_time == 0 && e.list_spawn_idx >= list_spawn_max & !isEventEnd) {
								// 제 1 군의 투입이 완료되었습니다.
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("이벤트 관리자: 제 %d 군의 소환이 완료되었습니다.", nowStage)),
										e.getMap());
								// 아이템 드랍.
								List<String> list = e.getListItem().get(e.nowStage);
								int[] stageItemCount = e.getStageItemCount()[e.nowStage];
								for (int i = 0; i < list.size(); ++i) {
							        String data = list.get(i);  // i번째 항목을 사용합니다.
							        StringTokenizer st = new StringTokenizer(data, "|");
							        String name = st.nextToken();
							        int count = stageItemCount[i];  // i번째 아이템 수량

							        for (int j = 0; j < 3; ++j) {  // 아이템을 3번 드랍
							            ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(name));
							            if (ii != null) {
							                ii.setObjectId(ServerDatabase.nextItemObjId());
							                ii.setCount(count);
							                ii.toDrop(null);
							                ii.toTeleport(Util.random(Lineage.event_map_min_x, Lineage.event_map_max_x), 
							                               Util.random(Lineage.event_map_min_y, Lineage.event_map_max_y), 
							                               Lineage.mon_event_map, true);
							            }
							        }
							    }							
								e.timer_cool_time = time;
							}

							// 쿨타임(휴식)을 초과한 경우 다음스테이지로 이동.
							if (e.timer_cool_time != 0 && e.timer_cool_time + cool_sleep <= time || isEventEnd) {
								// 맵에 드랍된 아이템 제거.
								World.clearWorldItem(e.getMap());
								// 스테이지 변경.
								e.nowStage += 1;
								// 초기화.
								e.timer_cool_time = e.timer_time = e.timer_ment_cnt = e.list_spawn_idx = 0;
							}
						}
						break;
					case 최종전:
						if (e.timer_ment_cnt == 0 && !isEventEnd) {
							// 최종전 개시! 제한 시간은 10분입니다.							
							World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, "이벤트 관리자: 마지막 소환! 제한 시간은 10분입니다."), e.getMap());
					        // 몬스터 스폰.
					        int minSize = Math.min(e.getListBoss().size(), e.getBossCount().length); // 배열 크기 동기화
					        for (int i = 0; i < minSize; ++i) { // 크기가 맞는 만큼만 반복
					            String name = e.getListBoss().get(i);
					            int count = e.getBossCount()[i];
					            for (int j = 0; j < count; ++j) {
					                MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find3(name));
					                if (mi != null) {
					                    e.getListSpawn().add(mi);
					                    MonsterSpawnlistDatabase.toSpawnMonster(mi, World.get_map(e.getMap()), true, e.getX(), e.getY(), e.getMap(), Lineage.SEARCH_MONSTER_TARGET_LOCATION, 0, 0, true, true);
					                }
					            }
					        }
					    }

						if (++e.timer_ment_cnt % 60 == 0 || isEventEnd) {
							int cnt = 10 - (e.timer_ment_cnt / 60);
							if (cnt > 0 && !isEventEnd) {
								// 경기 종료까지 9분 남았습니다.
								World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("이벤트 관리자: 이벤트 종료까지 %d분 남았습니다.", cnt)),
										e.getMap());
							} else {
								// 상태 변경.
								e.setStatus(EVENT_STATUS.종료);															
								// 초기화.
								e.timer_ment_cnt = 0;
							}
						}
						break;
					case 종료:
						// 해당 맵에 있는 사용자 마을로 귀환.
						for (PcInstance pc : World.getPcList()) {
							if (pc.getMap() == e.getMap()) {
								int[] home = null;
								home = Lineage.getHomeXY();
								pc.setHomeX(home[0]);
								pc.setHomeY(home[1]);
								pc.setHomeMap(home[2]);
								pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), false);
							}
						}
						ChattingController.toChatting(null, "몬스터 소환 이벤트가 종료되었습니다.", Lineage.CHATTING_MODE_GLOBAL);	
						// 맵에 드랍된 아이템 제거.
						World.clearWorldItem(e.getMap());
						// 맵에 스폰된 몬스터 제거.
						for (MonsterInstance mi : e.getListSpawn())
							mi.toAiThreadDelete();
						e.getListSpawn().clear();
						// 초기화.
						e.setStatus(EVENT_STATUS.휴식);
						e.timer_cool_time = e.timer_time = e.timer_ment_cnt = e.list_spawn_idx = e.nowStage = 0;
						isEventReady= isEventStart = isEventEnd  = false;
						break;
					}
					break;
				}
			}
		}
	}
}
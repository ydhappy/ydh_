package lineage.world.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import goldbitna.ConversationSession;
import goldbitna.RobotTalkDAO;
import goldbitna.robot.PartyRobotInstance;
import goldbitna.robot.controller.RobotConversationController;
import lineage.bean.database.PcShop;
import lineage.bean.database.RobotTalk;
import lineage.bean.database.marketPrice;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Party;
import lineage.database.ServerReloadDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_SoundEffect;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.TimeLine;
import lineage.thread.RobotMentQueueThread;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcShopInstance;
import lineage.world.object.instance.QuestInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.magic.ChattingClose;

public final class ChattingController {

	static private boolean global;
	static private List<String> warr;
	
	private static final String[] DEFAULT_NAME_MENT = {
		    "네?",
		    "네 &님?",
		    "&님 무슨일이세요?",
		    "나 부름?",
		    "누가 나부름?"
		};

	
	static public void init() {
		TimeLine.start("ChattingController..");
		warr = new ArrayList<String>();

		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("goldbitna_word/fword_list.txt"));

			String words;

			while ((words = lnrr.readLine()) != null) {

				words.trim();
				warr.add(words);

			}
			lnrr.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		global = true;

		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("fword_list.txt 파일 리로드 완료 - ");
		warr.clear();

		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("goldbitna_word/fword_list.txt"));

			String words;

			while ((words = lnrr.readLine()) != null) {

				words.trim();
				warr.add(words);

			}
			lnrr.close();

		} catch (Exception e) {
			lineage.share.System.printf("%s : fword_list txt init()\r\n", ChattingController.class.toString());
			lineage.share.System.println(e);
		}

		TimeLine.end();
	}

	static public void toWorldJoin(PcInstance pc) {

	}

	static public void toWorldOut(PcInstance pc) {

	}

	static public void setGlobal(boolean g) {
		global = g;
	}

	static public boolean isGlobal() {
		return global;
	}

	private static void processRobotTalkResponse(object sender, String msg) {
		if (!(sender instanceof PcInstance) || (sender instanceof RobotInstance))
			return;

		PcInstance pc = (PcInstance) sender;
		long now = System.currentTimeMillis();

		List<RobotInstance> nearby = World.getRobotList().stream().filter(robot -> Util.getDistance(pc, robot) <= 14).sorted(Comparator.comparingInt(robot -> Util.getDistance(pc, robot))).collect(Collectors.toList());

		for (RobotInstance robot : nearby) {
			boolean sameUser = RobotConversationController.isActiveConversation(robot, pc);
			boolean otherUser = RobotConversationController.isActiveWithOtherUser(robot, pc);

			String name = robot.getName();
			String last2 = name.length() >= 2 ? name.substring(name.length() - 2) : name;
			boolean called = msg.contains(name) || msg.contains(last2);

			// 1. 키워드 포함 여부 검사 (matchedTalk 변수 없이)
			boolean anyKeyword = false;
			for (RobotTalk talk : RobotTalkDAO.getList()) {
				for (String keyword : talk.getKeywordList()) {
					if (msg.contains(keyword)) {
						anyKeyword = true;
						break;
					}
				}
				if (anyKeyword)
					break;
			}

			// 2. 이름만 불렸고, 키워드는 없음
			if (called && !anyKeyword) {
				String ment = DEFAULT_NAME_MENT[Util.random(0, DEFAULT_NAME_MENT.length - 1)];
				RobotTalk tempTalk = new RobotTalk(0, "", ment);
				respondWithMent(robot, pc, now, tempTalk);
				RobotConversationController.terminateOthers(pc, robot);
				return;
			}

			// 3. exact/loose 키워드 매칭
			for (RobotTalk talk : RobotTalkDAO.getList()) {
				for (String keyword : talk.getKeywordList()) {
					boolean exact = called && msg.contains(keyword);
					boolean loose = msg.contains(keyword);

					if (exact || (sameUser && loose)) {
						if (otherUser)
							RobotConversationController.endConversationLater(robot);
						RobotConversationController.terminateOthers(pc, robot);
						respondWithMent(robot, pc, now, talk);
						return;
					}
				}
			}

			// 4. 대화 중이면 세션만 갱신
			if (sameUser) {
				RobotConversationController.updateConversation(robot);
				return;
			}
		}

		// 5. fallback 랜덤 반응 (키워드 30% 확률)
		for (RobotInstance robot : nearby) {
			for (RobotTalk talk : RobotTalkDAO.getList()) {
				for (String keyword : talk.getKeywordList()) {
					if (msg.contains(keyword) && Util.random(1, 100) <= 30) {
						RobotConversationController.terminateOthers(pc, robot);
						respondWithMent(robot, pc, now, talk);
						return;
					}
				}
			}
		}
	}

	/**
	 * 멘트 랜덤 추출 및 & 치환 후 큐에 등록, 대화 세션 등록까지 수행
	 */
	private static void respondWithMent(RobotInstance robot, PcInstance pc, long now, RobotTalk talk) {
		String response = getRandomMent(talk.getMent(), pc.getName(), robot.getName());
		if (response != null && !response.isEmpty()) {
			RobotMentQueueThread.thread.addMent(Lineage.AI_TALK_MENT, robot, pc, Lineage.CHATTING_MODE_NORMAL, Lineage.AI_TALK_MENT_DELAY, response);
			RobotConversationController.registerConversation(robot, pc, now);
		}
	}

	/**
	 * 멘트 필드에서 랜덤하게 문장을 선택하고, & → 플레이어 이름, $ → 로봇 이름으로 치환
	 * 
	 * @param mentField
	 *            DB에서 불러온 멘트 필드 ("&안녕하세요]$가 반갑다고 하네요")
	 * @param playerName
	 *            호출한 유저 이름
	 * @param robotName
	 *            응답하는 로봇 이름
	 * @return 이름이 반영된 랜덤 멘트
	 */
	private static String getRandomMent(String mentField, String playerName, String robotName) {
		if (mentField == null || mentField.trim().isEmpty())
			return null;

		String[] candidates = mentField.split("]");
		if (candidates.length == 0)
			return null;

		int index = Util.random(0, candidates.length - 1);
		String selected = candidates[index].trim();

		// 치환 처리
		return selected.replace("&", playerName != null ? playerName : "").replace("$", robotName != null ? robotName : "");
	}

	/**
	 * 채팅 처리 함수.
	 * 
	 * @param o
	 *            채팅 객체 (PcInstance, RobotInstance 등)
	 * @param msg
	 *            채팅 메시지
	 * @param mode
	 *            채팅 모드 (일반, 외침, 글로벌 등)
	 */
	static public void toChatting(object o, String msg, int mode) {
		if (o != null && o.isBuffChattingClose() && mode != Lineage.CHATTING_MODE_MESSAGE && mode != Lineage.CHATTING_MODE_NORMAL) {
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}

		if (o != null && o.getMap() == Lineage.teamBattleMap && mode != Lineage.CHATTING_MODE_MESSAGE && mode != Lineage.CHATTING_MODE_NORMAL && o.getGm() == 0) {
			return;
		}
		if (o != null && o.getMap() == Lineage.BattleRoyalMap && mode != Lineage.CHATTING_MODE_MESSAGE && mode != Lineage.CHATTING_MODE_NORMAL && o.getGm() == 0) {
			return;
		}
		// 사일런스 상태는 무시.
		if (o != null && o.isBuffSilence() && o.getGm() == 0)
			return;

		// 금지어 체크 (PcRobotInstance는 무시)
		if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
			switch (mode) {
	            case 0:
	            case 2:
	            case 3:
	            case 4:
	            case 12:
	            case 11:
	                if (containsForbiddenWord(msg) && o.getGm() == 0) {
	                    PcInstance pc = (PcInstance) o;
	                    msg = "";
	                    toMessage(o, "서버 금칙어를 사용하였습니다.");
	                    ChattingClose.init(pc, Lineage.ChatTime);
	                    return;
	                }
	        }
	    }
	    
	    // 모든채팅락 무시.
	    if (o != null && o.getGm() == 0 && Lineage.chatting_all_lock)
	        return;
	    
	    // 채팅 처리
	    switch (mode) {
	        case Lineage.CHATTING_MODE_NORMAL:
	            // 일반 채팅 모드에서는, 채팅 객체(o)가 PcInstance일 경우
	            // 파티에 속해 있으며 파티 마스터라면, 채팅 메시지를 커맨드로 파싱합니다.
	            if (o instanceof PcInstance) {
	                PcInstance pc = (PcInstance) o;
	                Party party = PartyController.find(pc);
	                if (party != null && pc.equals(party.getMaster())) {
	                    // 파티 마스터의 채팅 명령 처리
	                    parsePartyMasterGeneralCommand(pc, msg);
	                }
	            }
	            // 실제 채팅 메시지는 toNormal()을 통해 전파됩니다.
	            toNormal(o, msg);
	            break;
	            
	        case Lineage.CHATTING_MODE_SHOUT:
	            toShout(o, msg);
	            break;
	        case Lineage.CHATTING_MODE_GLOBAL:
	            if (o != null && o.getGm() == 0 && Lineage.chatting_global_lock)
	                return;
	            toGlobal(o, msg);
	            break;
	        case Lineage.CHATTING_MODE_CLAN:
	            toClan(o, msg);
	            break;
	        case Lineage.CHATTING_MODE_PARTY:
	        case Lineage.CHATTING_MODE_PARTY_MESSAGE:
	            toParty(o, msg, mode);
	            break;
	        case Lineage.CHATTING_MODE_TRADE:
	            toTrade(o, msg);
	            break;
	        case Lineage.CHATTING_MODE_MESSAGE:
	            toMessage(o, msg);
	            break;
	    }
	    
	    // 일반 플레이어의 일반 채팅일 경우 로봇 반응 처리 (명령어 제외)
	    if (
	        mode == Lineage.CHATTING_MODE_NORMAL &&     // 일반 채팅 모드일 때
	        o instanceof PcInstance &&                  // 플레이어 객체일 때
	        !(o instanceof RobotInstance) &&            // 로봇이 아닐 때
	        !CommandController.isCommand(msg)           // 명령어가 아닐 때
	    ) {
	        processRobotTalkResponse(o, msg);           // 로봇 대화 반응 실행
	    }

	    // 로그 기록 (시스템메세지는 기록 안함.)
	    if (mode != Lineage.CHATTING_MODE_MESSAGE && (o == null || o instanceof PcInstance)) {
	        if (Log.isLog(o))
	            Log.appendChatting(o == null ? null : (PcInstance) o, msg, mode);
	    }
	}

	private static boolean parsePartyMasterGeneralCommand(PcInstance partyMaster, String msg) {
	    if (msg == null) return false;

	    String lowerMsg = msg.toLowerCase();
	    Party party = PartyController.find(partyMaster);
	    if (party == null) return false;

	    boolean processed = false;

	    // 클래스별 로봇 리스트 맵: 클래스 ID → 로봇 목록
	    Map<Integer, List<PartyRobotInstance>> classRobotMap = new HashMap<>();

	    // 1. 파티원 중 PartyRobotInstance를 클래스별로 분류
	    for (PcInstance member : party.getList()) {
	        if (member instanceof PartyRobotInstance) {
	            PartyRobotInstance robot = (PartyRobotInstance) member;
	            int classType = robot.getClassType();

	            classRobotMap.computeIfAbsent(classType, k -> new ArrayList<>()).add(robot);
	        }
	    }

	    // 2. 클래스별로 랜덤 1명 선택해서 명령 전달
	    for (Map.Entry<Integer, List<PartyRobotInstance>> entry : classRobotMap.entrySet()) {
	        List<PartyRobotInstance> robots = entry.getValue();
	        if (!robots.isEmpty()) {
	            // 랜덤으로 1명 선택
	            PartyRobotInstance selected = robots.get(Util.random(0, robots.size() - 1));
	            selected.processChatCommand(lowerMsg);
	            processed = true;

	            // 디버그 로그
//	            lineage.share.System.printf("[디버그] 클래스 %d → %s 에게 명령 전달됨\n", entry.getKey(), selected.getName());
	        }
	    }

	    return processed;
	}


	/**
	 * 귓속말 처리 함수.
	 * 
	 * @param o
	 * @param name
	 * @param msg
	 */
	static public void toWhisper(final object o, final String name, final String msg) {
		if (o != null && ((o.getMap() == Lineage.teamBattleMap || o.getMap() == Lineage.BattleRoyalMap) && o.getGm() == 0)) {
			return;
		}

		if (o != null && o.isBuffChattingClose()) {
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		if (o != null && o.isBuffChattingClosetwo()) {
			// 현재 채팅 금지중입니다.
			toMessage(o, "상대방을 타격 하였을 경우 " + Lineage.ChatTimetwo + "(초)의 채팅 금지");
			return;
		}
		// 사일런스 상태는 무시. 단, 로봇은 예외.
		if (o != null && !(o instanceof RobotInstance) && o.isBuffSilence() && o.getGm() == 0)
		    return;

		if (Lineage.server_version <= 144 || o == null || o.getLevel() >= Lineage.chatting_level_whisper) {
			boolean gui_admin = name.equalsIgnoreCase(ServerReloadDatabase.manager_character_id);

			PcInstance user = World.findPc(name);

			if (user != null && o.getGm() == 0 && user.getGm() > 0 && user.isGmWhisper) {
				ChattingController.toChatting(o, "운영자는 현재 다른 용무중 입니다. 편지로 남겨주시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (user != null && user.getGm() > 0)
				user.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 5374));

			if (user != null || gui_admin) {
				if (gui_admin || o == null || (user.isChattingWhisper() && !user.getListBlockName().contains(o.getName()))) {
					if (o != null)
						o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), user, 0x09, msg));
					if (user != null)
						user.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, 0x08, msg));
					// gui 처리.
					if (Common.system_config_console == false) {
						GuiMain.display.asyncExec(new Runnable() {
							@Override
							public void run() {
								GuiMain.getViewComposite().getChattingComposite().toWhisper(o, name, msg);
							}
						});
					}
					// 로그 기록
					if (Log.isLog(user))
						Log.appendChatting(user, msg, Lineage.CHATTING_MODE_WHISPER);
				} else {
					if (o != null)
						// \f1%0%d 현재 귓말을 듣고 있지 않습니다.
						o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 205, name));
				}
			} else {
				if (o != null) {
					// \f1%0%d 게임을 하고 있지 않습니다.
					o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 73, name));
				} else {
					// gui 처리.
					if (Common.system_config_console == false) {
						GuiMain.display.asyncExec(new Runnable() {
							@Override
							public void run() {
								GuiMain.getViewComposite().getChattingComposite().toMessage(String.format("%s 게임을 하고 있지 않습니다.", name));
							}
						});
					}
				}
			}
		} else {
			if (o != null)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 404, String.valueOf(Lineage.chatting_level_whisper)));
		}
	}

	/**
	 * 일반 채팅 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toNormal(final object o, final String msg) {
		// 이름변경 확인 처리.
		if (o.getInventory() != null && o.getInventory().changeName != null) {
			o.getInventory().changeName.toClickFinal((Character) o, msg);
			o.getInventory().changeName = null;
			return;
		}
		if (o instanceof PcInstance && !(o instanceof RobotInstance) && ((PcInstance) o).PcMarket_Step > 0) {
			PcShopInstance pc_shop = PcMarketController.shop_list.get(((PcInstance) o).getObjectId());
			if (pc_shop == null) {
				pc_shop = new PcShopInstance(((PcInstance) o).getObjectId(), ((PcInstance) o).getName(), ((PcInstance) o).getClassType(), ((PcInstance) o).getClassSex());
				PcMarketController.shop_list.put(((PcInstance) o).getObjectId(), pc_shop);
			}
			switch (((PcInstance) o).PcMarket_Step) {
			case 1:
				try {

					StringTokenizer st = new StringTokenizer(msg, " ");
					while (st.hasMoreTokens()) {
						int price = Integer.valueOf(st.nextToken());
						int count = Integer.valueOf(st.nextToken());

						if (pc_shop.list.get(0L) == null) {
							String aden = "아데나";

							if (price > 2000000000 || price < 0) {
								ChattingController.toChatting(o, "\\fR가격이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
							} else {
								pc_shop.list.put(0L, new PcShop(pc_shop, price, aden, count));
								ChattingController.toChatting(o, "\\fR판매할 물품을 더블 클릭 해주세요.", Lineage.CHATTING_MODE_MESSAGE);
								((PcInstance) o).PcMarket_Step = 2;
								((PcInstance) o).PcMarket_Count = count;

							}
						} else {
							ChattingController.toChatting(o, "\\fR판매할 물품을 더블 클릭 해주세요.", Lineage.CHATTING_MODE_MESSAGE);
							((PcInstance) o).PcMarket_Step = 2;
							((PcInstance) o).PcMarket_Count = count;
						}

					}
				} catch (Exception e) {
					((PcInstance) o).PcMarket_Step = 0;
					((PcInstance) o).PcMarket_Count = 0;
					ChattingController.toChatting(o, "[알림] 입력이 잘못되었습니다. 다시 진행해주세요.", 20);
				}
				return;

			case 3:
				pc_shop.shop_comment = msg;
				PcMarketController.updateShopRobot(pc_shop);
				ChattingController.toChatting(o, "\\fR\"" + msg + "\" 홍보멘트 설정", Lineage.CHATTING_MODE_MESSAGE);
				((PcInstance) o).PcMarket_Step = 0;
				return;
			case 4:
				((PcInstance) o).marketPrice.clear();
				String itemName = msg;
				int index = 1;
				List<String> list = new ArrayList<String>();
				List<String> tempMsg = new ArrayList<String>();
				int en = 0;
				int bless = 1;
				boolean isEn = false;
				boolean isBless = false;

				tempMsg.add(msg);
				list.add((bless == 0 ? "(축) " : bless == 2 ? "(저주) " : "") + (en > 0 ? "+" + en + " " : en < 0 ? en + " " : "") + itemName);

				for (PcShopInstance psi : PcMarketController.shop_list.values()) {
					if (psi.getX() > 0 && psi.getY() > 0 && psi.getPc_objectId() != psi.getObjectId()) {
						for (PcShop s : psi.list.values()) {
							if (s.getItem() == null)
								continue;
							if (index > 60) { // 보낼 아이템 개수가 10개를 초과하면 더 이상 추가하지
												// 않음
								break;
							}

							if (s.getItem().getName().contains(itemName)) {
								marketPrice mp = new marketPrice();
								StringBuffer sb = new StringBuffer();

								sb.append(String.format("%d. %s", index++, s.getInvItemBress() == 0 ? "(축) " : s.getInvItemBress() == 1 ? "" : "(저주) "));

								if (s.getInvItemEn() > 0)
									sb.append(String.format("+%d ", s.getInvItemEn()));

								sb.append(String.format("%s", s.getItem().getName()));

								if (s.getInvItemCount() > 1)
									sb.append(String.format("(%d)", s.getInvItemCount()));

								sb.append(String.format(" [판매 금액]: %s 아데나", Util.changePrice(s.getPrice())));

								list.add(sb.toString());

								mp.setShopNpc(psi);
								mp.setX(psi.getX());
								mp.setY(psi.getY());
								mp.setMap(psi.getMap());
								mp.setObjId(psi.getObjectId());
								((PcInstance) o).marketPrice.add(mp);
							}
						}
					} else {
						continue;
					}
				}

				((PcInstance) o).PcMarket_Step = 0;
				if (list.size() < 2 || ((PcInstance) o).marketPrice.size() < 1) {

					((PcInstance) o).toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), PcMarketController.marketPriceNPC, "marketprice1", null, list));
					((PcInstance) o).PcMarket_Step = 0;
					return;
				} else {
					int count = 60 - (list.size() - 1);
					for (int i = 0; i < count; i++)
						list.add(" ");
					// 시세 검색 결과 html 패킷 보냄.
					((PcInstance) o).PcMarket_Step = 0;
					((PcInstance) o).toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), PcMarketController.marketPriceNPC, "marketprice", null, list));
				}

				return;
			}
		}

		// 인벤확인주문서 처리.
		if (o.getInventory() != null && o.getInventory().characterInventory != null) {
			o.getInventory().characterInventory.toClickFinal((Character) o, msg);
			o.getInventory().characterInventory = null;
			return;
		}

		// 장비 스왑 등록 확인.
		if (o != null && o instanceof PcInstance && PluginController.init(ChattingController.class, "swap", o, msg) != null)
			return;

		// 자동사냥 방지 답변 확인.
		if (o != null && o instanceof PcInstance && PluginController.init(ChattingController.class, "toAutoHuntAnswer", o, msg) != null)
			return;

		if (o != null && o.isBuffChattingClosetwo()) {
			// 현재 채팅 금지중입니다.
			toMessage(o, "상대방을 타격 하였을 경우 " + Lineage.ChatTimetwo + "(초)의 채팅 금지");
			return;
		}

		// 명령어 확인 처리.
		if (!CommandController.toCommand(o, msg)) {
			if (o != null && o.isBuffChattingClose()) {
				// 현재 채팅 금지중입니다.
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
				return;
			}

			if (o instanceof PcInstance) {
				// 나에게 표현.
				o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
				// 주변 객체에게 알리기. npc, monster, robot만.
				for (object oo : o.getInsideList()) {
					if (oo instanceof NpcInstance || oo instanceof MonsterInstance || oo instanceof RobotInstance)
						oo.toChatting(o, msg);
				}
			}
			// 주변사용자에게 표현.
			for (object oo : o.getInsideList()) {
				if (oo instanceof PcInstance) {
					PcInstance use = (PcInstance) oo;
					// 블럭 안된 이름만 표현하기.
					if (use.getListBlockName().contains(o.getName()) == false) {
						if (o.getMap() == Lineage.teamBattleMap && Lineage.is_teamBattle_chatting) {
							PcInstance pc = (PcInstance) o;

							if (pc.getBattleTeam() > 0 && pc.getBattleTeam() == use.getBattleTeam()) {
								use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
							}
						} else {
							if (o instanceof PcShopInstance) {
								if (oo.isShopMent)
									use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
							} else {
								if (o.getMap() != Lineage.teamBattleMap) {
									if (Lineage.is_chatting_clan) {
										use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
									} else {
										// 자신이 운영자일 경우
										// 상대방이 무혈일 경우
										// 상대방이 중립혈맹일 경우
										// 상대방이 자신과 같은 혈맹일 경우
										if ((o.getGm() > 0 || o.getClanId() == 0 || o.getClanName().equalsIgnoreCase(Lineage.new_clan_name) || o.getClanId() == use.getClanId() || use.getGm() > 0 || use.getClanId() == 0
												|| use.getClanName().equalsIgnoreCase(Lineage.new_clan_name)))
											use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
										else
											use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, "!@#$%^&*"));
									}
								}
							}
						}
					}
				}
			}
			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance) && o instanceof PcInstance) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toNormal(o, msg);
					}
				});
			}
		}
	}

	/**
	 * 외치기 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toShout(object o, String msg) {

		if (o != null && o.isBuffChattingClosetwo()) {
			// 현재 채팅 금지중입니다.
			toMessage(o, "상대방을 타격 하였을 경우 " + Lineage.ChatTimetwo + "(초)의 채팅 금지");
			return;
		}
		if (o != null && o.isBuffChattingClose()) {
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		// 나에게 표현.
		if (o instanceof PcInstance)
			o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_SHOUT, msg));
		// 주변사용자에게 표현.
		for (object oo : o.getAllList()) {
			if (oo instanceof PcInstance) {
				PcInstance use = (PcInstance) oo;
				// 블럭 안된 이름만 표현하기.
				if (o instanceof QuestInstance || !use.getListBlockName().contains(o.getName()))
					use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_SHOUT, msg));
			}
		}
	}

	public static String filtering(PcInstance pc, String msg) {
		try {
			int size = warr.size();
			String filterword;

			for (int i = 0; i < size; i++) {
				filterword = warr.get(i).trim().toLowerCase();

				if (msg.toLowerCase().contains(filterword)) {
					String hider = createHider(filterword.length());
					msg = msg.replaceAll("(?i)" + Pattern.quote(filterword), hider);

				}

				msg = filterSimilarWords(msg, filterword);
			}

		} catch (Exception e) {
			// 예외 처리 코드를 추가하거나 로그를 작성할 수 있습니다.
		}

		return msg;
	}

	private static String filterSimilarWords(String msg, String filterword) {
		String[] words = msg.split("\\s+");

		for (int i = 0; i < words.length; i++) {
			String word = words[i].toLowerCase();
			if (word.length() > 2 && word.charAt(0) == filterword.charAt(0) && word.charAt(word.length() - 1) == filterword.charAt(filterword.length() - 1)) {
				String hider = createHider(words[i].length());
				msg = msg.replaceFirst("(?i)" + Pattern.quote(words[i]), hider);
			}
		}

		return msg;
	}

	private static String createHider(int length) {
		StringBuilder hider = new StringBuilder();

		for (int i = 0; i < length; i++) {
			hider.append("*");
		}

		return hider.toString();
	}

	public static int calculateLevenshteinDistance(String a, String b) {

		a = Normalizer.normalize(a.toLowerCase(), Normalizer.Form.NFD);
		b = Normalizer.normalize(b.toLowerCase(), Normalizer.Form.NFD);
		int[] costs = new int[b.length() + 1];
		for (int j = 0; j < costs.length; j++) {
			costs[j] = j;
		}
		for (int i = 1; i <= a.length(); i++) {
			costs[0] = i;
			int corner = i - 1;
			for (int j = 1; j <= b.length(); j++) {
				int upper = costs[j];
				int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
				costs[j] = Math.min(Math.min(costs[j - 1], costs[j]), corner + cost);
				corner = upper;
			}
		}
		return costs[b.length()];
	}

	/**
	 * 금칙어를 포함하는지 확인하는 함수.
	 * 
	 * @param msg
	 * @return 금칙어를 포함하면 true
	 */
	static private boolean containsForbiddenWord(String msg) {
		for (String forbiddenWord : warr) {
			if (msg.contains(forbiddenWord)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 전체채팅 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toGlobal(final object o, final String msg) {

		if (Lineage.is_gm_global_chat && (o == null || o.getGm() > 0)) {
			for (PcInstance pc : World.getPcList()) {
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_MESSAGE, String.format("[******] %s", msg)));
			}

			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance)) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toGlobal(o, msg);
					}
				});
			}
			return;
		}

		// 처리해도되는지 확인.
		if (!global && o instanceof PcInstance)
			return;

		if (o == null || o.getGm() > 0 || Lineage.chatting_level_global <= o.getLevel()) {
			for (PcInstance use : World.getPcList()) {
				if (o == null || use.isChattingGlobal() && !use.getListBlockName().contains(o.getName())) {
					if (Lineage.is_chatting_clan) {
						use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, filtering(use, msg)));
					} else {
						// 자신이 운영자일 경우
						// 상대방이 무혈일 경우
						// 상대방이 중립혈맹일 경우
						// 상대방이 자신과 같은 혈맹일 경우
						if (o == null || ((o.getGm() > 0 || o.getClanId() == 0 || o.getClanName().equalsIgnoreCase(Lineage.new_clan_name) || o.getClanId() == use.getClanId() || use.getGm() > 0 || use.getClanId() == 0
								|| use.getClanName().equalsIgnoreCase(Lineage.new_clan_name))))
							use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, filtering(use, msg)));
						else
							use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, "!@#$%^&*"));
					}
				}
			}
			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance)) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toGlobal(o, msg);
					}
				});
			}
		} else {
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 195, String.valueOf(Lineage.chatting_level_global)));
		}
	}

	/**
	 * 혈맹채팅 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toClan(final object o, final String msg) {
		Clan c = ClanController.find(o.getClanId());
		if (c != null) {
			c.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_CLAN, msg));
			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance)) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toClan(o, msg);
					}
				});
			}
		}
	}

	/**
	 * 파티채팅 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toParty(final object o, final String msg, final int mode) {
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			Party p = PartyController.find(pc);

			if (p != null) {
				for (PcInstance party : p.getList()) {
					if (mode == Lineage.CHATTING_MODE_PARTY_MESSAGE) {
						if (party.isPartyMent()) {
							if (!Lineage.party_autopickup_item_print_on_screen || (Lineage.party_autopickup_item_print_on_screen && Util.isDistance(pc, party, Lineage.SEARCH_LOCATIONRANGE)))
								;
							party.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_PARTY, msg));
						}
					} else {
					}

				}

				// gui 처리.
				if (Common.system_config_console == false && !(o instanceof RobotInstance) && mode == Lineage.CHATTING_MODE_PARTY) {
					GuiMain.display.asyncExec(new Runnable() {
						@Override
						public void run() {
							GuiMain.getViewComposite().getChattingComposite().toParty(o, msg);
						}
					});
				}
			}
		}
	}

	/**
	 * 장사채팅 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toTrade(final object o, final String msg) {
		// 처리해도되는지 확인.
		if (msg.startsWith("\\d")) {
			return;
		}
		if (o != null && o.isBuffChattingClose()) {
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}

		if (o.getGm() > 0 || Lineage.chatting_level_global <= o.getLevel()) {
			for (PcInstance use : World.getPcList()) {

				use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_TRADE, msg));

			}
			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance)) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toTrade(o, msg);
					}
				});
			}
		} else {
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 195, String.valueOf(Lineage.chatting_level_global)));
		}
	}

	/**
	 * 일반 메세지 표현용.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toMessage(object o, final String msg) {
		if (o == null) {
			if (Common.system_config_console == false) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toMessage(msg);
					}
				});
			}
		} else {
			o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_MESSAGE, msg));
		}
	}
}

package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.MagicdollList;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

public class MagicdollListDatabase {

	static private List<MagicdollList> list;

	static public void init(Connection con) {
		TimeLine.start("MagicdollListDatabase..");

		list = new ArrayList<MagicdollList>();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM magicdoll_list");
			rs = st.executeQuery();
			while (rs.next()) {
				MagicdollList mdl = new MagicdollList();
				mdl.setItemName(rs.getString("item_name"));
				mdl.setMaterialName(rs.getString("material_name"));
				mdl.setMaterialCount(rs.getInt("material_count"));
				mdl.setDollName(rs.getString("doll_name"));
				mdl.setDollGfx(rs.getInt("doll_gfx"));
				mdl.setDollBuffType(rs.getString("doll_buff_type"));
				mdl.setDollBuffEffect(rs.getInt("doll_buff_effect"));
				mdl.setDollContinuous(rs.getInt("doll_continuous"));

				list.add(mdl);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MagicdollListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public MagicdollList find(String name) {
		for (MagicdollList mdl : list) {
			if (mdl.getItemName().equalsIgnoreCase(name))
				return mdl;
		}
		return null;
	}

	/**
	 * 버프 타입별 버프 이팩트값 찾아서 리턴함.
	 * 
	 * @param type
	 * @return
	 */
	static public int getBuffEffect(String type) {
		for (MagicdollList mdl : list) {
			if (mdl.getDollBuffType().equalsIgnoreCase(type))
				return mdl.getDollBuffEffect();
		}
		return 0;
	}

	/**
	 * 매직인형 착용 및 해제 시 옵션 처리 함수.
	 * 인형의 등급(단계)에 따라 해당 효과와 BUFF 아이콘만 전송합니다.
	 * BUFF 시간 값: -1이면 착용, 0이면 해제.
	 */
	static public void toOption(PcInstance pc, MagicdollList mdl, boolean enabled) {
	    if (mdl == null) {
	        return;
	    }
	    
	    int buffTimeValue = enabled ? -1 : 0;
	    int factor = enabled ? 1 : -1;
	    String type = mdl.getDollBuffType().toLowerCase();
	    
	    // 인형 타입에 따라 해당 단계만 처리합니다.
	    if (isStage1(type)) {
	        handleStage1(pc, mdl, factor);
	        sendBuffTime(pc, S_Ext_BuffTime.BUFFID_9974, buffTimeValue);
	    } else if (isStage2(type)) {
	        handleStage2(pc, mdl, factor);
	        sendBuffTime(pc, S_Ext_BuffTime.BUFFID_9975, buffTimeValue);
	    } else if (isStage3(type)) {
	        handleStage3(pc, mdl, factor);
	        sendBuffTime(pc, S_Ext_BuffTime.BUFFID_9976, buffTimeValue);
	    } else if (isStage4(type)) {
	        handleStage4(pc, mdl, factor);
	        sendBuffTime(pc, S_Ext_BuffTime.BUFFID_9977, buffTimeValue);
	    } else if (isStage5(type)) {
	        handleStage5(pc, mdl, factor);
	        sendBuffTime(pc, S_Ext_BuffTime.BUFFID_9978, buffTimeValue);
	    }
	    
	    // 최종 상태 갱신 패킷 전송
	    pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
	    pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
	}

	/* 헬퍼 메소드: 인형 타입이 각 단계에 해당하는지 여부 */
	private static boolean isStage1(String type) {
	    return type.equals("돌 골렘") || type.equals("늑대인간") ||
	           type.equals("버그베어") || type.equals("크러스트시안") ||
	           type.equals("에티") || type.equals("목각");
	}

	private static boolean isStage2(String type) {
	    return type.equals("서큐버스") || type.equals("장로") ||
	           type.equals("코카트리스") || type.equals("눈사람") ||
	           type.equals("인어") || type.equals("라바 골렘");
	}

	private static boolean isStage3(String type) {
	    return type.equals("자이언트") || type.equals("흑장로") ||
	           type.equals("서큐버스 퀸") || type.equals("드레이크") ||
	           type.equals("킹 버그베어") || type.equals("다이아몬드 골렘");
	}

	private static boolean isStage4(String type) {
	    return type.equals("리치") || type.equals("사이클롭스") ||
	           type.equals("나이트발드") || type.equals("시어") ||
	           type.equals("아이리스") || type.equals("뱀파이어") ||
	           type.equals("머미로드");
	}

	private static boolean isStage5(String type) {
	    // 아래는 예시로 주요 5단계 인형들만 포함합니다.
	    return type.equals("데몬") || type.equals("데스나이트") ||
	           type.equals("바란카") || type.equals("타락") ||
	           type.equals("바포메트") || type.equals("얼음여왕") ||
	           type.equals("커츠") || type.equals("안타라스") ||
	           type.equals("파푸리온") || type.equals("린드비오르") ||
	           type.equals("발라카스") || type.equals("각성 발라카스") ||
	           type.equals("각성 린드비오르") || type.equals("각성 파푸리온") ||
	           type.equals("각성 안타라스") || type.equals("왕자 인형") ||
	           type.equals("공주 인형") || type.equals("남기사 인형") ||
	           type.equals("여기사 인형") || type.equals("남마법사 인형") ||
	           type.equals("여마법사 인형") || type.equals("남요정 인형") ||
	           type.equals("여요정 인형");
	}

	/* -------------------------
	   각 단계별 효과 처리 메소드
	   factor: +1이면 효과 적용, -1이면 효과 해제
	--------------------------*/

	// 1단계: 돌 골렘, 늑대인간, 버그베어, 크러스트시안, 에티, 목각
	private static void handleStage1(PcInstance pc, MagicdollList mdl, int factor) {
	    String type = mdl.getDollBuffType().toLowerCase();
	    switch (type) {
	        case "돌 골렘":
	            pc.setMagicdollStoneGolem(factor > 0);
	            pc.setDynamicReduction(pc.getDynamicReduction() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "돌 골렘: 대미지 감소+1 ", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "늑대인간":
	            pc.setMagicdollWerewolf(factor > 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "늑대인간: 근거리 공격 시 일정 확률로 추가 대미지+15 ", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "버그베어":
	            pc.setMagicdollBugBear(factor > 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "버그베어: 소지 무게 증가+500 ", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "크러스트시안":
	            pc.setMagicdollHermitCrab(factor > 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "크러스트시안: 원거리 공격 시 일정 확률로 추가 대미지+15 ", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "에티":
	            pc.setMagicdollYeti(factor > 0);
	            pc.setDynamicAc(pc.getDynamicAc() + (3 * factor));
	            pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "에티: AC-3, 마법 치명타+1% ", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "목각":
	            pc.setMagicdollBasicWood(factor > 0);
	            pc.setDynamicHp(pc.getDynamicHp() + (50 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "목각: 최대 HP+50 ", Lineage.CHATTING_MODE_MESSAGE);
	                pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
	            }
	            break;
	        default:
	            break;
	    }
	}

	// 2단계: 서큐버스, 장로, 코카트리스, 눈사람, 인어, 라바 골렘
	private static void handleStage2(PcInstance pc, MagicdollList mdl, int factor) {
	    String type = mdl.getDollBuffType().toLowerCase();
	    switch (type) {
	        case "서큐버스":
	            pc.setMagicdollsuccubus(factor > 0);
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "서큐버스: 64초당 MP 회복+15 ", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "장로":
	            pc.setMagicdollElder(factor > 0);
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "장로: 64초당 MP 회복+15", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "코카트리스":
	            pc.setMagicdollCockatrice(factor > 0);
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + factor);
	            pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "코카트리스: 원거리 대미지+1, 원거리 명중+1", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "눈사람":
	            pc.setMagicdollSnowMan(factor > 0);
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + factor);
	            pc.setDynamicAddHit(pc.getDynamicAddHit() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "눈사람: 근거리 대미지+1, 근거리 명중+1", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "인어":
	            pc.setMagicdollMermaid(factor > 0);
	            pc.setDynamicExp(pc.getDynamicExp() + (0.05 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "인어: 경험치 보너스+5%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "라바 골렘":
	            pc.setMagicdollLavaGolem(factor > 0);
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + factor);
	            pc.setDynamicReduction(pc.getDynamicReduction() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "라바 골렘: 근거리 대미지+1, 대미지 감소+1", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        default:
	            break;
	    }
	}

	// 3단계: 자이언트, 흑장로, 서큐버스 퀸, 드레이크, 킹 버그베어, 다이아몬드 골렘
	private static void handleStage3(PcInstance pc, MagicdollList mdl, int factor) {
	    String type = mdl.getDollBuffType().toLowerCase();
	    switch (type) {
	        case "자이언트":
	            pc.setMagicdollGiant(factor > 0);
	            pc.setDynamicExp(pc.getDynamicExp() + (0.1 * factor));
	            pc.setDynamicReduction(pc.getDynamicReduction() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "자이언트: 경험치 보너스+10%, 대미지 감소+1", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "흑장로":
	            pc.setMagicdollBlackElder(factor > 0);
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "흑장로: 64초당 MP 회복+15, 일정확률 콜 라이트닝 발동", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "서큐버스 퀸":
	            pc.setMagicdollsuccubusQueen(factor > 0);
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            pc.setDynamicSp(pc.getDynamicSp() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "서큐버스 퀸: 64초당 MP 회복+15, SP+1", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "드레이크":
	            pc.setMagicdollDrake(factor > 0);
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + (2 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 6 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "드레이크: 원거리 대미지+2, 64초당 MP 회복+6", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "킹 버그베어":
	            pc.setMagicdollKingBugBear(factor > 0);
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.08 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 10 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "킹 버그베어: 스턴 내성+8, 64초당 MP 회복+10", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "다이아몬드 골렘":
	            pc.setMagicdollDiamondGolem(factor > 0);
	            pc.setDynamicReduction(pc.getDynamicReduction() + (2 * factor));
	            pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() + factor);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "다이아몬드 골렘: 대미지 감소+2, PvP 대미지 감소+1", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        default:
	            break;
	    }
	}

	// 4단계: 리치, 사이클롭스, 나이트발드, 시어, 아이리스, 뱀파이어, 머미로드
	private static void handleStage4(PcInstance pc, MagicdollList mdl, int factor) {
	    String type = mdl.getDollBuffType().toLowerCase();
	    switch (type) {
	        case "리치":
	            pc.setMagicdollRich(factor > 0);
	            pc.setDynamicSp(pc.getDynamicSp() + (2 * factor));
	            pc.setDynamicHp(pc.getDynamicHp() + (80 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "리치: SP+2, 최대 HP+80", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "사이클롭스":
	            pc.setMagicdollCyclops(factor > 0);
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + (2 * factor));
	            pc.setDynamicAddHit(pc.getDynamicAddHit() + (2 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.12 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "사이클롭스: 근거리 대미지+2, 근거리 명중+2, 스턴 내성+12", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "나이트발드":
	            pc.setMagicdollKnightVald(factor > 0);
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + (2 * factor));
	            pc.setDynamicAddHit(pc.getDynamicAddHit() + (2 * factor));
	            pc.setDynamicStunHit(pc.getDynamicStunHit() + (0.05 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "나이트발드: 근거리 대미지+2, 근거리 명중+2, 스턴 명중+5", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "시어":
	            pc.setMagicdollSeer(factor > 0);
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + (5 * factor));
	            pc.setMagicdollTimeHpTic(factor > 0 ? 32 : 0);
	            pc.setMagicdollHpTic(factor > 0 ? 30 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "시어: 원거리 대미지+5, 32초당 HP 회복+30", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "아이리스":
	            pc.setMagicdollIris(factor > 0);
	            pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + (5 * factor));
	            pc.setDynamicReduction(pc.getDynamicReduction() + (3 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "아이리스: PvP 대미지+5, 대미지 감소+3", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "뱀파이어":
	            pc.setMagicdollVampire(factor > 0);
	            pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + (3 * factor));
	            pc.setMagicdollTimeHpTic(factor > 0 ? 32 : 0);
	            pc.setMagicdollHpTic(factor > 0 ? 30 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "뱀파이어: PvP 대미지+3, 32초당 HP 회복+30", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "머미로드":
	            pc.setMagicdollMummylord(factor > 0);
	            pc.setDynamicSp(pc.getDynamicSp() + factor);
	            pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + factor);
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "머미로드: SP+1, 마법 치명타+1%, 64초당 MP 회복+15", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        default:
	            break;
	    }
	}

	// 5단계: 데몬, 데스나이트, 바란카, 타락, 바포메트, 얼음여왕, 커츠, 안타라스,
//	         파푸리온, 린드비오르, 발라카스, 각성 발라카스, 각성 린드비오르, 
//	         각성 파푸리온, 각성 안타라스, 왕자 인형, 공주 인형, 남기사 인형, 
//	         여기사 인형, 남마법사 인형, 여마법사 인형, 남요정 인형, 여요정 인형 등
	private static void handleStage5(PcInstance pc, MagicdollList mdl, int factor) {
	    String type = mdl.getDollBuffType().toLowerCase();
	    switch (type) {
	        case "데몬":
	            pc.setMagicdollDemon(factor > 0);
	            pc.setDynamicStunHit(pc.getDynamicStunHit() + (0.1 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.12 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.15 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "데몬: 스턴 명중+10, 스턴 내성+12 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "데스나이트":
	            pc.setMagicdollDeathKnight(factor > 0);
	            pc.setDynamicReduction(pc.getDynamicReduction() + (7 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.25 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "데스나이트: 대미지 감소+7, 경험치 보너스+25%, 일정확률 헬파이어 발동", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "바란카":
	            pc.setMagicdollBaranka(factor > 0);
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.10 * factor));
	            pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + (5 * factor));
	            pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() + (3 * factor));
	            pc.setDynamicCritical(pc.getDynamicCritical() + (10 * factor));
	            pc.setDynamicBowCritical(pc.getDynamicBowCritical() + (10 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.15 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "바란카: 스턴 내성+10, 근/원거리 치명타+10% , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "타락":
	            pc.setMagicdollTarak(factor > 0);
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicSp(pc.getDynamicSp() + (3 * factor));
	            pc.setDynamicMagicHit(pc.getDynamicMagicHit() + (5 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.15 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "타락: 스턴 내성+10, SP+3, 마법 명중+5 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "바포메트":
	            pc.setMagicdollBaphomet(factor > 0);
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + (5 * factor));
	            pc.setDynamicMagicDmg(pc.getDynamicMagicDmg() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.15 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "바포메트: 스턴 내성+10, 마법 치명타+5%, 마법 대미지+2 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "얼음여왕":
	            pc.setMagicdollIceQueen(factor > 0);
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + (5 * factor));
	            pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + (5 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.15 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "얼음여왕: 원거리 대미지+5, 원거리 명중+5, 스턴 내성+10 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "커츠":
	            pc.setMagicdollKouts(factor > 0);
	            pc.setDynamicReduction(pc.getDynamicReduction() + (3 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + (5 * factor));
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + (5 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.15 * factor));
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "커츠: 추가 대미지+5, 대미지 감소+3, 스턴 내성+10 , 경험치 보너스+15%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "안타라스":
	            pc.setMagicdollAntaras(factor > 0);
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicReduction(pc.getDynamicReduction() + (10 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.50 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 20 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "각성 안타라스: 스턴 내성+10, 대미지 감소+10, 경험치 보너스+50%, 64초당 MP 회복+20", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "파푸리온":
	            pc.setMagicdollPapoorion(factor > 0);
	            pc.setDynamicSp(pc.getDynamicSp() + (8 * factor));
	            pc.setDynamicMagicHit(pc.getDynamicMagicHit() + (8 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.08 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.20 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 20 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "파푸리온: SP+8, 마법 명중+8, 스턴 내성+8, 64초당 MP 회복+20 , 경험치 보너스+20%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "린드비오르":
	            pc.setMagicdollLindvior(factor > 0);
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + (8 * factor));
	            pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + (8 * factor));
	            pc.setDynamicBowCritical(pc.getDynamicBowCritical() + (5 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.08 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.20 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "린드비오르: 원거리 대미지+8, 원거리 명중+8, 원거리 치명타+5%, 스턴 내성+8, 64초당 MP 회복+15 , 경험치 보너스+20%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "발라카스":
	            pc.setMagicdollValakas(factor > 0);
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + (8 * factor));
	            pc.setDynamicAddHit(pc.getDynamicAddHit() + (8 * factor));
	            pc.setDynamicCritical(pc.getDynamicCritical() + (5 * factor));
	            pc.setDynamicStunHit(pc.getDynamicStunHit() + (0.1 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.20 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "발라카스: 근거리 대미지+8, 근거리 명중+8, 근거리 치명타+5%, 스턴 명중+10, 64초당 MP 회복+15 , 경험치 보너스+20%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "각성 발라카스":
	            pc.setMagicdollValakas(factor > 0);
	            pc.setDynamicAddDmg(pc.getDynamicAddDmg() + (10 * factor));
	            pc.setDynamicAddHit(pc.getDynamicAddHit() + (10 * factor));
	            pc.setDynamicCritical(pc.getDynamicCritical() + (7 * factor));
	            pc.setDynamicStunHit(pc.getDynamicStunHit() + (0.15 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.30 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "각성 발라카스: 근거리 대미지+10, 근거리 명중+10, 근거리 치명타+7%, 스턴 명중+15, 64초당 MP 회복+15 , 경험치 보너스+30%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "각성 린드비오르":
	            pc.setMagicdollLindvior(factor > 0);
	            pc.setDynamicAddDmgBow(pc.getDynamicAddDmgBow() + (10 * factor));
	            pc.setDynamicAddHitBow(pc.getDynamicAddHitBow() + (10 * factor));
	            pc.setDynamicBowCritical(pc.getDynamicBowCritical() + (10 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.30 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "각성 린드비오르: 원거리 대미지+10, 원거리 명중+10, 원거리 치명타+10%, 스턴 내성+10, 64초당 MP 회복+15 , 경험치 보너스+30%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "각성 파푸리온":
	            pc.setMagicdollPapoorion(factor > 0);
	            pc.setDynamicSp(pc.getDynamicSp() + (10 * factor));
	            pc.setDynamicMagicHit(pc.getDynamicMagicHit() + (10 * factor));
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.30 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 30 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "각성 파푸리온: SP+10, 마법 명중+10, 스턴 내성+10, 64초당 MP 회복+30 , 경험치 보너스+30%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "각성 안타라스":
	            pc.setMagicdollAntaras(factor > 0);
	            pc.setDynamicStunResist(pc.getDynamicStunResist() + (0.1 * factor));
	            pc.setDynamicReduction(pc.getDynamicReduction() + (10 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.50 * factor));
	            pc.setMagicdollTimeMpTic(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic(factor > 0 ? 20 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "각성 안타라스: 스턴 내성+10, 대미지 감소+10, 경험치 보너스+50%, 64초당 MP 회복+20", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "왕자 인형":
	            // 초보자 인형: 왕자 인형은 체력/콘 보너스 효과
	            pc.setMagicdollAntaras(factor > 0);
	            pc.setLvStr(pc.getLvStr() + (1 * factor));
	            pc.setLvCon(pc.getLvCon() + (1 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeHpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollHpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "왕자 마법인형: STR+1, CON+1, 64초당 HP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "공주 인형":
	            // 초보자 인형: 공주 인형은 MP/콘 보너스 효과
	            pc.setLvStr(pc.getLvStr() + (1 * factor));
	            pc.setLvCon(pc.getLvCon() + (1 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeMpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "공주 마법인형: STR+1, CON+1, 64초당 MP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "남기사 인형":
	            pc.setLvCon(pc.getLvCon() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeHpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollHpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "남기사 마법인형: CON+2, 64초당 HP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "여기사 인형":
	            pc.setLvCon(pc.getLvCon() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeMpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "여기사 마법인형: CON+2, 64초당 MP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "남마법사 인형":
	            pc.setLvInt(pc.getLvInt() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeHpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollHpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "남마법사 마법인형: INT+2, 64초당 HP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "여마법사 인형":
	            pc.setLvInt(pc.getLvInt() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeMpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "여마법사 마법인형: INT+2, 64초당 MP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "남요정 인형":
	            pc.setLvDex(pc.getLvDex() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeHpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollHpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "남요정 마법인형: DEX+2, 64초당 HP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        case "여요정 인형":
	            pc.setLvDex(pc.getLvDex() + (2 * factor));
	            pc.setDynamicExp(pc.getDynamicExp() + (0.10 * factor));
	            pc.setMagicdollTimeMpTic1(factor > 0 ? 64 : 0);
	            pc.setMagicdollMpTic1(factor > 0 ? 15 : 0);
	            if (factor > 0) {
	                ChattingController.toChatting(pc, "여요정 마법인형: DEX+2, 64초당 MP 회복+15, 경험치 보너스+10%", Lineage.CHATTING_MODE_MESSAGE);
	            }
	            break;
	        default:
	            break;
	    }
	}

	/**
	 * 지정된 BUFFID와 시간 값을 이용해 버프 시간 패킷 전송.
	 * BUFFID는 short 타입이어야 하므로 캐스팅합니다.
	 */
	private static void sendBuffTime(PcInstance pc, int buffId, int timeValue) {
	    pc.toSender(S_Ext_BuffTime.clone(BasePacketPooling.getPool(S_Ext_BuffTime.class), (short)buffId, timeValue));
	}
}

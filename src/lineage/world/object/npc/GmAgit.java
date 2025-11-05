package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class GmAgit extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "GmAgit"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		if (action.equalsIgnoreCase("1")) {
			pc.toTeleport(33372, 32653, 4, true);
			ChattingController.toChatting(pc, "1번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("2")) {
			pc.toTeleport(33384, 32654, 4, true);
			ChattingController.toChatting(pc, "2번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("3")) {
			pc.toTeleport(33396, 32654, 4, true);
			ChattingController.toChatting(pc, "3번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("4")) {
			pc.toTeleport(33429, 32659, 4, true);
			ChattingController.toChatting(pc, "4번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("5")) {
			pc.toTeleport(33443, 32667, 4, true);
			ChattingController.toChatting(pc, "5번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("6")) {
			pc.toTeleport(33457, 32652, 4, true);
			ChattingController.toChatting(pc, "6번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("7")) {
			pc.toTeleport(33478, 32668, 4, true);
			ChattingController.toChatting(pc, "7번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("8")) {
			pc.toTeleport(33475, 32680, 4, true);
			ChattingController.toChatting(pc, "8번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("9")) {
			pc.toTeleport(33458, 32696, 4, true);
			ChattingController.toChatting(pc, "9번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("10")) {
			pc.toTeleport(33424, 32689, 4, true);
			ChattingController.toChatting(pc, "10번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("11")) {
			pc.toTeleport(33413, 32675, 4, true);
			ChattingController.toChatting(pc, "11번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("12")) {
			pc.toTeleport(33419, 32705, 4, true);
			ChattingController.toChatting(pc, "12번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("13")) {
			pc.toTeleport(33375, 32696, 4, true);
			ChattingController.toChatting(pc, "13번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("14")) {
			pc.toTeleport(33364, 32684, 4, true);
			ChattingController.toChatting(pc, "14번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("15")) {
			pc.toTeleport(33364, 32671, 4, true);
			ChattingController.toChatting(pc, "15번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("16")) {
			pc.toTeleport(33345, 32662, 4, true);
			ChattingController.toChatting(pc, "16번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("17")) {
			pc.toTeleport(33347, 32675, 4, true);
			ChattingController.toChatting(pc, "17번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("18")) {
			pc.toTeleport(33341, 32708, 4, true);
			ChattingController.toChatting(pc, "18번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("19")) {
			pc.toTeleport(33354, 32730, 4, true);
			ChattingController.toChatting(pc, "19번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("20")) {
			pc.toTeleport(33370, 32715, 4, true);
			ChattingController.toChatting(pc, "20번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("21")) {
			pc.toTeleport(33382, 32715, 4, true);
			ChattingController.toChatting(pc, "21번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("22")) {
			pc.toTeleport(33404, 32737, 4, true);
			ChattingController.toChatting(pc, "22번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("23")) {
			pc.toTeleport(33428, 32719, 4, true);
			ChattingController.toChatting(pc, "23번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("24")) {
			pc.toTeleport(33450, 32732, 4, true);
			ChattingController.toChatting(pc, "24번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("25")) {
			pc.toTeleport(33406, 32757, 4, true);
			ChattingController.toChatting(pc, "25번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("26")) {
			pc.toTeleport(33366, 32759, 4, true);
			ChattingController.toChatting(pc, "26번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("27")) {
			pc.toTeleport(33355, 32776, 4, true);
			ChattingController.toChatting(pc, "27번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("28")) {
			pc.toTeleport(33358, 32788, 4, true);
			ChattingController.toChatting(pc, "28번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("29")) {
			pc.toTeleport(33371, 32788, 4, true);
			ChattingController.toChatting(pc, "29번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("30")) {
			pc.toTeleport(33385, 32776, 4, true);
			ChattingController.toChatting(pc, "30번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("31")) {
			pc.toTeleport(33402, 32790, 4, true);
			ChattingController.toChatting(pc, "31번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("32")) {
			pc.toTeleport(33484, 32790, 4, true);
			ChattingController.toChatting(pc, "32번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("33")) {
			pc.toTeleport(33500, 32804, 4, true);
			ChattingController.toChatting(pc, "33번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("34")) {
			pc.toTeleport(33382, 32803, 4, true);
			ChattingController.toChatting(pc, "34번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("35")) {
			pc.toTeleport(33376, 32826, 4, true);
			ChattingController.toChatting(pc, "35번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("36")) {
			pc.toTeleport(33400, 32813, 4, true);
			ChattingController.toChatting(pc, "36번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("37")) {
			pc.toTeleport(33401, 32823, 4, true);
			ChattingController.toChatting(pc, "37번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("38")) {
			pc.toTeleport(33436, 32840, 4, true);
			ChattingController.toChatting(pc, "38번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("39")) {
			pc.toTeleport(33460, 32833, 4, true);
			ChattingController.toChatting(pc, "39번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("40")) {
			pc.toTeleport(33390, 32847, 4, true);
			ChattingController.toChatting(pc, "40번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("41")) {
			pc.toTeleport(33403, 32861, 4, true);
			ChattingController.toChatting(pc, "41번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("42")) {
			pc.toTeleport(33416, 32853, 4, true);
			ChattingController.toChatting(pc, "42번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("43")) {
			pc.toTeleport(33376, 32871, 4, true);
			ChattingController.toChatting(pc, "43번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("44")) {
			pc.toTeleport(33428, 32869, 4, true);
			ChattingController.toChatting(pc, "44번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("45")) {
			pc.toTeleport(33447, 32871, 4, true);
			ChattingController.toChatting(pc, "45번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			
		//하이네
		} else if (action.equalsIgnoreCase("46")) {
			pc.toTeleport(33606, 33215, 4, true);
			ChattingController.toChatting(pc, "1번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("47")) {
			pc.toTeleport(33630, 33208, 4, true);
			ChattingController.toChatting(pc, "2번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("48")) {
			pc.toTeleport(33630, 33226, 4, true);
			ChattingController.toChatting(pc, "3번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("49")) {
			pc.toTeleport(33632, 33243, 4, true);
			ChattingController.toChatting(pc, "4번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("50")) {
			pc.toTeleport(33619, 33264, 4, true);
			ChattingController.toChatting(pc, "5번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("51")) {
			pc.toTeleport(33576, 33231, 4, true);
			ChattingController.toChatting(pc, "6번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("52")) {
			pc.toTeleport(33586, 33310, 4, true);
			ChattingController.toChatting(pc, "7번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("53")) {
			pc.toTeleport(33581, 33337, 4, true);
			ChattingController.toChatting(pc, "8번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("54")) {
			pc.toTeleport(33619, 33375, 4, true);
			ChattingController.toChatting(pc, "9번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("55")) {
			pc.toTeleport(33627, 33398, 4, true);
			ChattingController.toChatting(pc, "10번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if  (action.equalsIgnoreCase("56")) {
			pc.toTeleport(33625, 33444, 4, true);
			ChattingController.toChatting(pc, "11번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		
		// 아덴
		} else if (action.equalsIgnoreCase("57")) {
			pc.toTeleport(34158, 33400, 4, true);
			ChattingController.toChatting(pc, "1번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("58")) {
			pc.toTeleport(34145, 33396, 4, true);
			ChattingController.toChatting(pc, "2번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("59")) {
			pc.toTeleport(34135, 33418, 4, true);
			ChattingController.toChatting(pc, "3번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("60")) {
			pc.toTeleport(34128, 33391, 4, true);
			ChattingController.toChatting(pc, "4번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("61")) {
			pc.toTeleport(34128, 33401, 4, true);
			ChattingController.toChatting(pc, "5번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("62")) {
			pc.toTeleport(34107, 33392, 4, true);
			ChattingController.toChatting(pc, "6번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("63")) {
			pc.toTeleport(34106, 33406, 4, true);
			ChattingController.toChatting(pc, "7번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("64")) {
			pc.toTeleport(34095, 33391, 4, true);
			ChattingController.toChatting(pc, "8번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("65")) {
			pc.toTeleport(34078, 33396, 4, true);
			ChattingController.toChatting(pc, "9번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("66")) {
			pc.toTeleport(34064, 33397, 4, true);
			ChattingController.toChatting(pc, "10번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("67")) {
			pc.toTeleport(34147, 33370, 4, true);
			ChattingController.toChatting(pc, "11번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("68")) {
			pc.toTeleport(34074, 33356, 4, true);
			ChattingController.toChatting(pc, "12번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("69")) {
			pc.toTeleport(34128, 33354, 4, true);
			ChattingController.toChatting(pc, "13번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("70")) {
			pc.toTeleport(34127, 33368, 4, true);
			ChattingController.toChatting(pc, "14번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("71")) {
			pc.toTeleport(34091, 33350, 4, true);
			ChattingController.toChatting(pc, "15번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("72")) {
			pc.toTeleport(34098, 33370, 4, true);
			ChattingController.toChatting(pc, "16번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("73")) {
			pc.toTeleport(34061, 33376, 4, true);
			ChattingController.toChatting(pc, "17번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("74")) {
			pc.toTeleport(34075, 33382, 4, true);
			ChattingController.toChatting(pc, "18번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("75")) {
			pc.toTeleport(34078, 33370, 4, true);
			ChattingController.toChatting(pc, "19번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("76")) {
			pc.toTeleport(34027, 33382, 4, true);
			ChattingController.toChatting(pc, "20번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("77")) {
			pc.toTeleport(34015, 33374, 4, true);
			ChattingController.toChatting(pc, "21번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("78")) {
			pc.toTeleport(33955, 33404, 4, true);
			ChattingController.toChatting(pc, "22번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("79")) {
			pc.toTeleport(34006, 33403, 4, true);
			ChattingController.toChatting(pc, "23번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("80")) {
			pc.toTeleport(33988, 33404, 4, true);
			ChattingController.toChatting(pc, "24번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("81")) {
			pc.toTeleport(33976, 33274, 4, true);
			ChattingController.toChatting(pc, "25번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("82")) {
			pc.toTeleport(33983, 33299, 4, true);
			ChattingController.toChatting(pc, "26번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("83")) {
			pc.toTeleport(33967, 33417, 4, true);
			ChattingController.toChatting(pc, "27번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("84")) {
			pc.toTeleport(33941, 33405, 4, true);
			ChattingController.toChatting(pc, "28번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("85")) {
			pc.toTeleport(33932, 33406, 4, true);
			ChattingController.toChatting(pc, "29번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("86")) {
			pc.toTeleport(33916, 33389, 4, true);
			ChattingController.toChatting(pc, "30번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("87")) {
			pc.toTeleport(33917, 33399, 4, true);
			ChattingController.toChatting(pc, "31번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("88")) {
			pc.toTeleport(33920, 33411, 4, true);
			ChattingController.toChatting(pc, "32번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("89")) {
			pc.toTeleport(33916, 33365, 4, true);
			ChattingController.toChatting(pc, "33번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("90")) {
			pc.toTeleport(33917, 33372, 4, true);
			ChattingController.toChatting(pc, "34번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("91")) {
			pc.toTeleport(34001, 33324, 4, true);
			ChattingController.toChatting(pc, "35번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("92")) {
			pc.toTeleport(33898, 33396, 4, true);
			ChattingController.toChatting(pc, "36번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("93")) {
			pc.toTeleport(33887, 33381, 4, true);
			ChattingController.toChatting(pc, "37번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("94")) {
			pc.toTeleport(33921, 33302, 4, true);
			ChattingController.toChatting(pc, "38번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("95")) {
			pc.toTeleport(33957, 33324, 4, true);
			ChattingController.toChatting(pc, "39번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("96")) {
			pc.toTeleport(33898, 33218, 4, true);
			ChattingController.toChatting(pc, "40번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("97")) {
			pc.toTeleport(33947, 33306, 4, true);
			ChattingController.toChatting(pc, "41번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("98")) {
			pc.toTeleport(33935, 33291, 4, true);
			ChattingController.toChatting(pc, "42번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("99")) {
			pc.toTeleport(33921, 33293, 4, true);
			ChattingController.toChatting(pc, "43번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("100")) {
			pc.toTeleport(33912, 33291, 4, true);
			ChattingController.toChatting(pc, "44번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("101")) {
			pc.toTeleport(33918, 33315, 4, true);
			ChattingController.toChatting(pc, "45번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("102")) {
			pc.toTeleport(33921, 33326, 4, true);
			ChattingController.toChatting(pc, "46번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("103")) {
			pc.toTeleport(33900, 33318, 4, true);
			ChattingController.toChatting(pc, "47번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("104")) {
			pc.toTeleport(33905, 33329, 4, true);
			ChattingController.toChatting(pc, "48번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("105")) {
			pc.toTeleport(33907, 33350, 4, true);
			ChattingController.toChatting(pc, "49번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("106")) {
			pc.toTeleport(33892, 33354, 4, true);
			ChattingController.toChatting(pc, "50번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("107")) {
			pc.toTeleport(33976, 33204, 4, true);
			ChattingController.toChatting(pc, "51번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("108")) {
			pc.toTeleport(33919, 33230, 4, true);
			ChattingController.toChatting(pc, "52번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("109")) {
			pc.toTeleport(33933, 33210, 4, true);
			ChattingController.toChatting(pc, "53번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("110")) {
			pc.toTeleport(33916, 33193, 4, true);
			ChattingController.toChatting(pc, "54번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("111")) {
			pc.toTeleport(34157, 33141, 4, true);
			ChattingController.toChatting(pc, "55번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("112")) {
			pc.toTeleport(34144, 33143, 4, true);
			ChattingController.toChatting(pc, "56번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("113")) {
			pc.toTeleport(34129, 33114, 4, true);
			ChattingController.toChatting(pc, "57번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("114")) {
			pc.toTeleport(34122, 33163, 4, true);
			ChattingController.toChatting(pc, "58번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("115")) {
			pc.toTeleport(34127, 33129, 4, true);
			ChattingController.toChatting(pc, "59번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("116")) {
			pc.toTeleport(34099, 33110, 4, true);
			ChattingController.toChatting(pc, "60번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("117")) {
			pc.toTeleport(34108, 33121, 4, true);
			ChattingController.toChatting(pc, "61번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("118")) {
			pc.toTeleport(34144, 33167, 4, true);
			ChattingController.toChatting(pc, "62번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("119")) {
			pc.toTeleport(34055, 33109, 4, true);
			ChattingController.toChatting(pc, "63번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("120")) {
			pc.toTeleport(34044, 33107, 4, true);
			ChattingController.toChatting(pc, "64번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("121")) {
			pc.toTeleport(34029, 33109, 4, true);
			ChattingController.toChatting(pc, "65번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("122")) {
			pc.toTeleport(34041, 33130, 4, true);
			ChattingController.toChatting(pc, "66번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else if (action.equalsIgnoreCase("123")) {
			pc.toTeleport(34030, 33130, 4, true);
			ChattingController.toChatting(pc, "67번째 아지트로 이동하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}

		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	}
}
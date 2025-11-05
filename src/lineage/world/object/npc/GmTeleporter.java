package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.controller.PcMarketController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcShopInstance;

public class GmTeleporter extends object {

    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "GmTeleporter"));
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        
		if (action.equalsIgnoreCase("말섬")) {
			pc.toTeleport(32582, 32931, 0, true); // 말섬
			ChattingController.toChatting(pc, String.format("말하는 섬으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("글말")) {
			pc.toTeleport(32613, 32796, 4, true); // 글루딘
			ChattingController.toChatting(pc, String.format("글루딘 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("켄말")) {
			pc.toTeleport(33050, 32780, 4, true); // 켄트
			ChattingController.toChatting(pc, String.format("켄트성 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("윈말")) {
			pc.toTeleport(32610, 33188, 4, true); // 윈다우드
			ChattingController.toChatting(pc, String.format("윈다우드 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("은말")) {
			pc.toTeleport(33080, 33392, 4, true); // 은기사
			ChattingController.toChatting(pc, String.format("은기사 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("화말")) {
			pc.toTeleport(32750, 32442, 4, true); // 화전민
			ChattingController.toChatting(pc, String.format("화전민 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("요말")) {
			pc.toTeleport(33051, 32340, 4, true); // 요정숲
			ChattingController.toChatting(pc, String.format("요정의숲 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("기란")) {
			pc.toTeleport(33442, 32797, 4, true); // 기란
			ChattingController.toChatting(pc, String.format("기란성 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("노래하는섬")) {
			pc.toTeleport(32779, 32760, 68, false); // 노래하는 섬
			ChattingController.toChatting(pc, String.format("노래하는 섬으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("숨겨진계곡")) {
			pc.toTeleport(32670, 32841, 69, false); // 숨겨진 계곡
			ChattingController.toChatting(pc, String.format("숨겨진 계곡으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("웰던")) {
			pc.toTeleport(33702, 32500, 4, false); // 웰던
			ChattingController.toChatting(pc, String.format("웰던 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("오렌")) {
			pc.toTeleport(34055, 32277, 4, false); //오렌
			ChattingController.toChatting(pc, String.format("오렌 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("하이네")) {
			pc.toTeleport(33611, 33244, 4, false); // 하이네
			ChattingController.toChatting(pc, String.format("하이네 마을로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("영자")) {
			pc.toTeleport(32740, 32865, 99, false); // 영자방
			ChattingController.toChatting(pc, String.format("영자방으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("잊섬")) {
			pc.toTeleport(32821, 32850, 70, false); // 잊섬
			ChattingController.toChatting(pc, String.format("잊혀진 섬으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("지옥")) {
			pc.toTeleport(32740, 32797, 666, true); // 지옥
			ChattingController.toChatting(pc, String.format("지옥으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		
		if (action.equalsIgnoreCase("VIP")) {
			pc.toTeleport(32799, 32800, 620, true); // VIP룸
			ChattingController.toChatting(pc, String.format("VIP 파티룸으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		
		//성
		if (action.equalsIgnoreCase("켄성")) {
			pc.toTeleport(32736, 32786, 15, true); // 켄트성
			ChattingController.toChatting(pc, String.format("켄트 성으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("윈성")) {
			pc.toTeleport(32735, 32786, 29, true); // 윈다우드성
			ChattingController.toChatting(pc, String.format("윈다우드 성으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("기란성")) {
			pc.toTeleport(32729, 32790, 52, true); // 윈다우드성
			ChattingController.toChatting(pc, String.format("기란 성으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("하이네성")) {
			pc.toTeleport(32572, 32814, 64, true); // 윈다우드성
			ChattingController.toChatting(pc, String.format("하이네 성으로 이동하였습니다."), Lineage.CHATTING_MODE_MESSAGE);
		}
		
	}
}
package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Auction;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.AgitController;
import lineage.world.controller.AuctionController;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Maid extends object {

	private Agit agit;
	private List<String> list_html;
	
	public Maid(){
		list_html = new ArrayList<String>();
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		
		// 아지트쪽에 영향을 주는 하녀인지 확인.
		agit = AgitController.find("npc", x, y);
		if(agit != null)
			agit.setMaid(this);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(agit != null){
			list_html.clear();
			if(agit.getClanId()>0 && agit.getClanId()==pc.getClanId() || (pc.getGm()>0)) {
				list_html.add(getName());
				list_html.add(agit.getAgitName());
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agit", null, list_html));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "agdeny"));
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	    if (agit != null) {
	        if (agit.getClanId() > 0 && agit.getClanId() != pc.getClanId())
	            return;

	        // 문열기 
	        if (action.equalsIgnoreCase("open")) {
	            AgitController.toDoor(agit, true);

	        // 문닫기 
	        } else if (action.equalsIgnoreCase("close")) {
	            AgitController.toDoor(agit, false);

	        // 외부인 내보내기 
	        } else if (action.equalsIgnoreCase("expel")) {
	            AgitController.toExpel(this, agit);

	        // 세금 낸다 
	        } else if (action.equalsIgnoreCase("pay")) {

	        // 집을 판다 
	        } else if (action.equalsIgnoreCase("sell")) {
	            Auction a = AuctionController.find(agit.getUid());

	            if (!a.isSell()) {
	                a.setSell(true);
	                a.setPrice(a.getDefaultPrice());
	                AuctionController.update(a);
	                ChattingController.toChatting(pc, "아지트를 경매에 등록하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
	            } else {
	                ChattingController.toChatting(pc, "아지트가 이미 경매에 판매중입니다.", Lineage.CHATTING_MODE_MESSAGE);
	            }

	        // 집이름 정한다. 
	        } else if (action.equalsIgnoreCase("name")) {
	            pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 512));

	        // 집안의 가구 모두 제거. 
	        } else if (action.equalsIgnoreCase("rem")) {

	        // 텔레포트 
	        } else if (action.startsWith("tel")) {
	            // 0:창고 1:펫보관소 2:속죄의신녀 3:시장
	            switch (action) {
	                case "tel0": // 창고
	                    if (pc.getInventory().isAden(100, true)) {
	                        switch (Util.random(0, 3)) {
	                            case 0:
	                                pc.toTeleport(33421, 32812, 4, true);
	                                break;
	                            case 1:
	                                pc.toTeleport(33424, 32813, 4, true);
	                                break;
	                            case 2:
	                                pc.toTeleport(33430, 32816, 4, true);
	                                break;
	                            case 3:
	                                pc.toTeleport(33433, 32816, 4, true);
	                                break;
	                        }
	                    } else {
	                        pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
	                    }
	                    break;
	                case "tel1": // 펫 보관소
	                    if (pc.getInventory().isAden(100, true)) {
	                        switch (Util.random(0, 1)) {
	                            case 0:
	                                pc.toTeleport(33342, 32722, 4, true);
	                                break;
	                            case 1:
	                                pc.toTeleport(33489, 32688, 4, true);
	                                break;
	                        }
	                    } else {
	                        pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
	                    }
	                    break;
	                case "tel2": // 아덴 성당
	                    if (pc.getInventory().isAden(100, true)) {
	                        pc.toTeleport(33960, 33364, 4, true);
	                    } else {
	                        pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
	                    }
	                    break;
	                case "tel3": // 시장
	                    if (pc.getInventory().isAden(100, true)) {
	                        switch (Util.random(0, 4)) {
	                            case 0:
	                                pc.toTeleport(32683, 32853, 350, true);
	                                break;
	                            case 1:
	                                pc.toTeleport(32691, 32845, 350, true);
	                                break;
	                            case 2:
	                                pc.toTeleport(32698, 32838, 350, true);
	                                break;
	                            case 3:
	                                pc.toTeleport(32707, 32829, 350, true);
	                                break;
	                            case 4:
	                                pc.toTeleport(32715, 32821, 350, true);
	                                break;
	                        }
	                    } else {
	                        pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
	                    }
	                    break;
	            }
	        }
	    }
	}
}
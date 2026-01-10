package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterKarma;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Meet extends CraftInstance {

	public Meet(Npc npc) {
		super(npc);

		// 발록
		Item i = ItemDatabase.find("아데나");
		if (i != null) {
			craft_list.put("b_a", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 1));
			list.put(i, l);
		}
		i = ItemDatabase.find("보호 망토");
		if (i != null) {
			craft_list.put("b_b", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 10));
			list.put(i, l);
		}
		i = ItemDatabase.find("투명 망토");
		if (i != null) {
			craft_list.put("b_c", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("영혼석 파편"), 100));
			list.put(i, l);
		}

		// 야히
		i = ItemDatabase.find("마법 망토");
		if (i != null) {
			craft_list.put("y_a", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 1));
			list.put(i, l);
		}
		i = ItemDatabase.find("치환 망토");
		if (i != null) {
			craft_list.put("y_b", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 10));
			list.put(i, l);
		}
		i = ItemDatabase.find("작은 방패");
		if (i != null) {
			craft_list.put("y_c", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("혈석 파편"), 100));
			list.put(i, l);
		}

	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (getMap() == 601) {
			// 발록
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet001"));
		} else {
			// 야히
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet101"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (getMap() == 601) {
			// 발록
			if(action.equalsIgnoreCase("1")) {
				if(pc.isKarmaType() == 1)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet002"));
				else{
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet003"));
					pc.toChatting(pc, "욕망의 동굴로 가서 몹을 잡고 오너라");
				}
			} else if(action.equalsIgnoreCase("2")) {
				if(pc.isKarmaType() == 1) {
					if(pc.getKarmaLevel() == 8)
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet005"));
					else
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet006"));
				} else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet004"));
			} else if(action.equalsIgnoreCase("a") || action.equalsIgnoreCase("b") || action.equalsIgnoreCase("c")) {
				Item craft = craft_list.get("b_"+action);
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 안내창 띄우기.
					if(action.equalsIgnoreCase("a"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet007"));
					if(action.equalsIgnoreCase("b"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet008"));
					if(action.equalsIgnoreCase("c"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet009"));
					if(action.equalsIgnoreCase("aa"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet007"));
					if(action.equalsIgnoreCase("bb"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet008"));
					if(action.equalsIgnoreCase("cc"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet009"));
					// 우호도 상승.
					if(action.equalsIgnoreCase("a")){
						pc.setKarma(pc.getKarma() + (100*1));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("b")){
						pc.setKarma(pc.getKarma() + (100*10));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("c")){
						pc.setKarma(pc.getKarma() + (100*100));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					// 우호도 상승.
					if(action.equalsIgnoreCase("aa")){
						pc.setKarma(pc.getKarma() + (100*1000));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("bb")){
						pc.setKarma(pc.getKarma() + (100*10000));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("cc")){
						pc.setKarma(pc.getKarma() + (100*100000));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(Lineage.server_version >= 200)
						pc.toSender(S_CharacterKarma.clone(BasePacketPooling.getPool(S_CharacterKarma.class), (int)pc.getKarma()));
				}
			}
		} else {

			// 야히
			if(action.equalsIgnoreCase("1")) {
				if(pc.isKarmaType() == -1)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet102"));
				else{
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet103"));
					pc.toChatting(pc, "그림자 신전으로 가서 몹을 잡고 오너라");
				}
			} else if(action.equalsIgnoreCase("2")) {
				if(pc.isKarmaType() == -1) {
					if(pc.getKarmaLevel() == 8)
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet105"));
					else
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet106"));
				} else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet104"));
			} else if(action.equalsIgnoreCase("a") || action.equalsIgnoreCase("b") || action.equalsIgnoreCase("c")) {
				Item craft = craft_list.get("y_"+action);
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 안내창 띄우기.
					if(action.equalsIgnoreCase("a"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet107"));
					if(action.equalsIgnoreCase("b"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet108"));
					if(action.equalsIgnoreCase("c"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet109"));
					if(action.equalsIgnoreCase("aa"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet107"));
					if(action.equalsIgnoreCase("bb"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet108"));
					if(action.equalsIgnoreCase("cc"))
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meet109"));
					// 우호도 상승.
					if(action.equalsIgnoreCase("a")){
						pc.setKarma(pc.getKarma() + ~(100*1));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
						
					}
					if(action.equalsIgnoreCase("b")){
						pc.setKarma(pc.getKarma() + ~(100*10));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("c")){
						pc.setKarma(pc.getKarma() + ~(100*100));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("aa")){
						pc.setKarma(pc.getKarma() + ~(100*1000));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("bb")){
						pc.setKarma(pc.getKarma() + ~(100*10000));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(action.equalsIgnoreCase("cc")){
						pc.setKarma(pc.getKarma() + ~(100*100000));
						ChattingController.toChatting(pc, String.format("\\fR당신의 우호도는 %,d입니다.", (int)pc.getKarma()), 20);
					}
					if(Lineage.server_version >= 200)
						pc.toSender(S_CharacterKarma.clone(BasePacketPooling.getPool(S_CharacterKarma.class), (int)pc.getKarma()));
				}
			} else if(action.equalsIgnoreCase("d")) {
				pc.toPotal(32758, 32794, 600);
			}
		}
	}
}

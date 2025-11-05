package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Moria extends CraftInstance {
	
	private long actionTime;
	private int lastAction;

	public Moria(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("마법사 옷");
		if(i != null){
			craft_list.put("request magician dress", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 1) );
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 25) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 2) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 4) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("마법사 모자");
		if(i != null){
			craft_list.put("request magician cap", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급 에메랄드"), 2) );
			l.add( new Craft(ItemDatabase.find("마력의 돌"), 20) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 1) );
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 1) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toAi(long time) {
	    // 10초마다 동작을 실행하도록 설정 (10초 = 10,000밀리초)
	    if (actionTime + 15000 < System.currentTimeMillis()) {
	        // S_ObjectAction 17번 행동을 수행
	        lastAction = 17;
	        actionTime = System.currentTimeMillis();
	        toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 17), true);
	    }
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "moria1"));
	}
	
	
	/**
	 * 제작처리 마지막 부분. : 중복코드 방지용
	 * 
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toLeather(PcInstance pc, String action, long count) {
		Item craft = craft_list.get(action);

		if (craft != null) {
			int max = CraftController.getMax(pc, list.get(craft));
			if (count > 0 && max > 0 && count <= max) {
				// 재료 제거
				for (int i = 0; i < count; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action) == null ? 0 : craft.getListCraft().get(action);
				if (jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count * jegop, true);
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		if (pc.getInventory() != null) {
			Item craft = craft_list.get(action);
			// 재료가 부족 할 시 창 닫기
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));

			if (craft != null) {
				// 마법사 옷
				if (action.equalsIgnoreCase("request magician dress")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "moria4", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 마법사 모자
				if (action.equalsIgnoreCase("request magician cap")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "moria4", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}

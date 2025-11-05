package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.AccountDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.network.packet.server.S_SoundEffect;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Point extends ShopInstance {

	public Point(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
	    List<String> msg = new ArrayList<>();
	    String account = pc.getName();

	    // 계정과 포인트 정보를 가져오기
	    String pccheck = AccountDatabase.getid(account);
	    int point = (int) AccountDatabase.userpointcheck(pccheck);
	    long pendingCash = AccountDatabase.getPendingCash(pccheck); 

	    // 계정 이름과 포인트 정보를 메시지에 추가
	    msg.add(String.format("%s", account));
	    msg.add(String.format("%s", pendingCash));
	    msg.add(String.format("%d", point));

	    // S_Html 객체 생성 및 클라이언트에 전송
	    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "point", null, msg));
	   // pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27824)); 
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (action.equalsIgnoreCase("sell")) {
			List<ItemInstance> sell_list = new ArrayList<ItemInstance>();
			for (Shop s : npc.getShop_list()) {
				// 판매할 수 있도록 설정된 목록만 처리.
				if (s.isItemSell()) {
					List<ItemInstance> search_list = new ArrayList<ItemInstance>();
					pc.getInventory().findDbName(s.getItemName(), search_list);
					for (ItemInstance item : search_list) {
						if (!item.isEquipped() && item.getItem().isSell() && (s.getItemEnLevel() == 0 || s.getItemEnLevel() == item.getEnLevel())) {
							//
							if (isSellAdd(item) && !sell_list.contains(item) && item.getEnLevel() == s.getItemEnLevel())
								sell_list.add(item);
						}
					}
				}
			}
			if (sell_list.size() > 0) {
				pc.toSender(S_ShopSell.clone(BasePacketPooling.getPool(S_ShopSell.class), this, sell_list));
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mayer4"));
			}
		} else if (action.indexOf("3") > 0 || action.indexOf("6") > 0 || action.indexOf("7") > 0) {
			List<String> list_html = new ArrayList<String>();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}
}

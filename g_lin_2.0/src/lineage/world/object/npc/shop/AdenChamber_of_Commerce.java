package lineage.world.object.npc.shop;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopSell;
import lineage.network.packet.server.S_SoundEffect;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class AdenChamber_of_Commerce extends ShopInstance {
	
	 private long lastSoundPlayTime = 0; // 마지막 사운드 재생 시간을 저장할 변수
	 
	public AdenChamber_of_Commerce(Npc npc){
		super(npc);
	}

    // 기존 PcInstance의 다른 코드...
    // lastSoundPlayTime의 getter 메서드
    public long getLastSoundPlayTime() {
        return lastSoundPlayTime;
    }


    public void setLastSoundPlayTime(long lastSoundPlayTime) {
        this.lastSoundPlayTime = lastSoundPlayTime;
    }

    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
        long currentTime = System.currentTimeMillis(); // 현재 시간(밀리초)

        // HTML 출력
        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tzmerchant"));
        

        if (currentTime - getLastSoundPlayTime() >= 2700) {

            pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27806));

            // 현재 시간을 마지막 사운드 재생 시간으로 업데이트
            setLastSoundPlayTime(currentTime);
        }
    }
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("sell")) {
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
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tzmerchant3"));
			}
		} else if (action.indexOf("3") > 0 || action.indexOf("6") > 0 || action.indexOf("7") > 0) {
			List<String> list_html = new ArrayList<String>();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}
}


package lineage.world.object.item.bundle;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Bundle;

public class Supplies extends Bundle {

	static synchronized public ItemInstance clone(ItemInstance item) {
	    if (item == null)
	        item = new Supplies();
	    return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    // 1간마다 한번씩 사용 가능.
	    long value = ((PcInstance) cha).getSupplyTime();
	    long time = 0;
	    time = Long.valueOf(value);
	    // 시간확인
	    long remain = time - System.currentTimeMillis();
	    //long hour = remain / (1000 * 3600);
	    long minute = (remain % (3600 * 1000)) / 60000;
	    if (time > 0 && time > System.currentTimeMillis()) {
	        // 시간체크추가
	        ChattingController.toChatting(cha, "이 아이템은 " + minute + "분 후에 다시 사용이 가능합니다. ", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }
	    int[][] db = {
	            // 보급품주문서 (nameid, min, max, chance)
	            {8427, 1, 1, 1},
	    };
	    toBundle(cha, db, TYPE.LOOP_1);
	    
	    // 시간 갱신
	    ((PcInstance) cha).setSupplyTime(System.currentTimeMillis() + (1000 * 60 * 60 * 1));
	}
}
package lineage.world.object.item.bundle;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Bundle;
import lineage.world.object.item.Bundle.TYPE;

public class AlchemistStone extends Bundle {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new AlchemistStone();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    PcInstance pc = (PcInstance) cha;
	    long lastUsedTime = pc.getStoneTime();
	    long currentTime = System.currentTimeMillis();

	    // 사용 횟수를 100으로 초기화 (처음 사용 시)
	    if (pc.getStoneCount() <= 0) {
	        pc.setStoneCount(100);  // 사용 횟수 초기화
	    }

        // 남은 시간 계산
        long remain = lastUsedTime - currentTime; // 남은 시간 계산
        long hour = (remain > 0) ? (remain / (1000 * 3600)) : 0; // 시간 계산
        long minute = (remain > 0) ? ((remain % (1000 * 3600)) / 60000) : 0; // 분 계산

	    if (lastUsedTime > 0 && lastUsedTime > currentTime) {
	        // 사용 제한 시간에 도달하지 않았을 경우, 남은 시간 안내 메시지 전송
	    	ChattingController.toChatting(cha, +hour+"시간 "+minute+"분후 사용이 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }

	    // 랜덤 아이템 추출
	    int[][] db = {
	        {944, 1, 10, 20},      // 지혜의 물약
	        {110, 1, 10, 20},      // 엘븐 와퍼
	        {1652264, 1, 10, 20},  // 강화 속도향상 물약
	        {2575, 1, 100, 10},    // 고대의 체력 회복제
	        {2576, 1, 200, 10},    // 고대의 고급 체력 회복제
	        {2577, 1, 300, 10},    // 고대의 강력 체력 회복제
	        {3370, 1, 3, 10},      // 정신력의 물약
	    };
	    toBundle(cha, db, TYPE.LOOP_1);

	    // 사용 횟수 확인 및 감소
	    int count = pc.getStoneCount();
	    count -= 1;  // 사용 후 횟수 감소

	    // 남은 횟수 안내
	    ChattingController.toChatting(cha, "남은 사용 횟수: " + count + "회", Lineage.CHATTING_MODE_MESSAGE);
	    
	    if (count <= 0) {
	        // 사용 횟수가 0이면 아이템을 인벤토리에서 제거
	        ChattingController.toChatting(cha, "이 아이템은 사용 횟수를 모두 소진하여 제거됩니다.", Lineage.CHATTING_MODE_MESSAGE);
	        cha.getInventory().count(this, getCount() - 1, true); // 인벤토리에서 제거
	        return;
	    }

	    // 사용 제한 시간 및 남은 횟수 갱신
	    pc.setStoneTime(currentTime + (1000 * 60 * 60 * 22));  // 22시간 후 사용 가능
	    pc.setStoneCount(count);  // 남은 사용 횟수 갱신
	}
}
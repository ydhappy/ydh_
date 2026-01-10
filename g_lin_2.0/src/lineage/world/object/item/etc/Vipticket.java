package lineage.world.object.item.etc;

import lineage.database.AccountDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Vipticket extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Vipticket();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    String account = cha.getName();

	    // 계정과 포인트 정보를 가져오기
	    String pccheck = AccountDatabase.getid(account);
	    int point = (int) AccountDatabase.userpointcheck(pccheck);

        if (cha.getMap() == 620) {
            ChattingController.toChatting(cha, "VIP룸에서는 사용이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
            return;
        }
        
	    // 포인트가 있는 사람만 포탈 이동
	    if (point > 0) {
	    	cha.toPotal(32799, 32800, 620);
	    	ChattingController.toChatting(cha, String.format("%s님께서 VIP룸에 입장하였습니다.", cha.getName()), Lineage.CHATTING_MODE_MESSAGE);
	    } else {
	        // 포인트가 0인 경우 이동 불가 메시지 출력
	    	 ChattingController.toChatting(cha, String.format("%s님께서 포인트가 부족하여 VIP룸에 입장할 수 없습니다.", cha.getName()), Lineage.CHATTING_MODE_MESSAGE);
	    }
	}
}

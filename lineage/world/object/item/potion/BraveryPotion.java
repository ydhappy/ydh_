package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.Wafer;

public class BraveryPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new BraveryPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;
		
		if(cha.getMap() == 5143){
			ChattingController.toChatting(cha, "[알림] 인형경주중엔 사용 하실 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if(isClassCheck(cha)){
			// 이팩트 표현
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
			if (cha.getClassType() == Lineage.LINEAGE_CLASS_WIZARD) {
                HolyWalk.init(cha, getItem().getDuration());  // HolyWalk 대체	                
                
            } else if (cha.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
            	Wafer.init(cha, getItem().getDuration(), false); // Wafer 대체
            	
            } else {
                // 버프 처리
                Bravery.init(cha, getItem().getDuration(), false);
            }
					
		}else{
			// 아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
		
		// 아이템 수량 갱신
		if (getItem() != null && !getItem().getName().equalsIgnoreCase("무한 가속 룬") && !getItem().getName().equalsIgnoreCase("무한 가속 룬(3일)"))
		cha.getInventory().count(this, getCount()-1, true);
	}

}

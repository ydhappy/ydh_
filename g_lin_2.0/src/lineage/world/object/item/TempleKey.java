package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class TempleKey extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new TempleKey();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    if (!isClick(cha)) {
	        return;
	    }
	    
	    switch (item.getNameIdNumber()) {
	        case 3945:
	            // 좌표 확인
	            if (cha.getMap() == 522 && cha.getX() >= 32701 && cha.getX() <= 32706 && cha.getY() >= 32893 && cha.getY() <= 32898) {
	                cha.toPotal(32700, 32896, 523);
	                return;
	            }
	            // \f1아무일도 일어나지 않았습니다.
	            cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
	            break;
	            
	        case 3946:
	            // 좌표 확인
	            if (cha.getMap() == 523 && cha.getX() >= 32698 && cha.getX() <= 32703 && cha.getY() >= 32893 && cha.getY() <= 32898) {
	                cha.toPotal(32690, 32895, 524);
	                return;
	            }
	            // \f1아무일도 일어나지 않았습니다.
	            cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
	            break;
	    }
	}
}
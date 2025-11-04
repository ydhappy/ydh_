package lineage.world.object.item.cloak;

import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.item.CookCommon;

public class Ivorytowerelixir extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Ivorytowerelixir();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		//  food체크 100%일때만 복용됨.
		if(getItem().getNameIdNumber()==8428 && cha.getFood()!=Lineage.MAX_FOOD) {
			ChattingController.toChatting(cha, "배부른 상태가 100%일 때만 섭취할 수 있습니다.", 20);
			return;
		}
		// food상태 만땅
		cha.setFood(Lineage.MAX_FOOD);
		// 버프 적용.
		CookCommon.onBuff(cha, SkillDatabase.find(705)); 
		// 메세지
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 76, getName()));
		// 수량제거
		cha.getInventory().count(this, getCount()-1, true);
		}
	}



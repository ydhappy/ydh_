package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;
import lineage.world.object.item.armor.ArmorOfIvorytower;

public class IvorytowerEnchantmentArmor extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new IvorytowerEnchantmentArmor();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
		 ItemInstance armor = cha.getInventory().value(cbp.readD());
			if (armor instanceof ArmorOfIvorytower) 
				if (armor.getEnLevel() == 4) {
					ChattingController.toChatting(cha, "더이상 인챈트가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 32003));
					return;
				}
			if(armor instanceof ArmorOfIvorytower && armor.getEnLevel() < 4 && armor.getItem().isEnchant()){
				if(cha instanceof PcInstance)
					armor.toEnchant((PcInstance)cha, toEnchant(cha, armor, this));

				cha.getInventory().count(this, getCount()-1, true);
		
		} else {
			if(armor instanceof ItemArmorInstance)
				ChattingController.toChatting(cha, "상아탑 방어구에만 사용이 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		    	cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 32004));

		}
	}
}


	
	/**
	 * 인첸트가 가능한지 확인해주는 함수.
	 * 
	 * @param armor
	 * @return
	 */
	@Override
	protected boolean isEnchant(ItemInstance armor) {
		//
		if(armor instanceof ArmorOfIvorytower) {
			// 봉인 확인.
			if (armor.getBless() < 0)
				return false;
			// 오픈대기상태 확인.
			if (Lineage.open_wait)
				return false;

			return true;
		}
		return false;
	}

}
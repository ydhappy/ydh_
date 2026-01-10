package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;
import lineage.world.object.item.weapon.WeaponOfIvorytower;

public class IvorytowerEnchantmentWeapon extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new IvorytowerEnchantmentWeapon();
		return item;
	}

	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance weapon = cha.getInventory().value(cbp.readD());
			if (weapon instanceof WeaponOfIvorytower) 
				if (weapon.getEnLevel() == 6) {
					ChattingController.toChatting(cha, "더이상 인챈트가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 32003));
					return;
				}
			if(weapon instanceof WeaponOfIvorytower && weapon.getEnLevel() < 6 && weapon.getItem().isEnchant()){
				if(cha instanceof PcInstance)
					weapon.toEnchant((PcInstance)cha, toEnchant(cha, weapon, this));
				
				cha.getInventory().count(this, getCount()-1, true);
				
			} else {
				if(weapon instanceof ItemWeaponInstance)
					ChattingController.toChatting(cha, "상아탑 무기에만 사용이 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			    	cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 32005));

			}
		}
	}

	
	/**
	 * 인첸트가 가능한지 확인해주는 함수.
	 * 
	 * @param weapon
	 * @return
	 */
	@Override
	protected boolean isEnchant(ItemInstance weapon) {
		//
		if(weapon instanceof WeaponOfIvorytower) {
			// 봉인 확인.
			if (weapon.getBless() < 0)
				return false;
			// 오픈대기상태 확인.
			if (Lineage.open_wait)
				return false;
			//
			return true;
		}

		return false;
	}
}
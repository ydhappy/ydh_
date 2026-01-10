package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;
import lineage.world.object.item.weapon.WeaponOfchangcheon;

public class ChangcheonEnchantWeaponIllusion extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ChangcheonEnchantWeaponIllusion();
		return item;
	}

	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance weapon = cha.getInventory().value(cbp.readD());
			if(weapon!=null && weapon.getItem().isEnchant() && weapon instanceof WeaponOfchangcheon){
				if(cha instanceof PcInstance)
					weapon.toEnchant((PcInstance)cha, toEnchant(cha, weapon, this));
				cha.getInventory().count(this, getCount()-1, true);
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
	protected boolean isEnchant(ItemInstance weapon) {
		//
		if(weapon instanceof WeaponOfchangcheon) {
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

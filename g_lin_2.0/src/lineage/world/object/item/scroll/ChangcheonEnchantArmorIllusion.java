package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;
import lineage.world.object.item.armor.ArmorOfchangcheon;

public class ChangcheonEnchantArmorIllusion extends Enchant {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ChangcheonEnchantArmorIllusion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance armor = cha.getInventory().value(cbp.readD());
			if(armor!=null && armor.getItem().isEnchant() && armor instanceof ArmorOfchangcheon){
				if(cha instanceof PcInstance)
					armor.toEnchant((PcInstance)cha, toEnchant(cha, armor, this));
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
	protected boolean isEnchant(ItemInstance armor) {
		//
		if(armor instanceof ArmorOfchangcheon) {
			// 봉인 확인.
			if (armor.getBless() < 0)
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

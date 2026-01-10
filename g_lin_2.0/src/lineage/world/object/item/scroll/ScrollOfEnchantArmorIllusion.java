package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.EventController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemIllusionInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;
import lineage.bean.lineage.IllusionItem; // ✅ 추가된 인터페이스 import

public class ScrollOfEnchantArmorIllusion extends Enchant implements IllusionItem {

    private final ItemIllusionInstance illusionInstance;

    public ScrollOfEnchantArmorIllusion() {
        this.illusionInstance = new ItemIllusionInstance();
    }

    @Override
    public void registerIllusion() {
        EventController.appendIllusion(illusionInstance);
    }

    static synchronized public ItemInstance clone(ItemInstance item){
        if(item == null)
            item = new ScrollOfEnchantArmorIllusion();
        
        if (item instanceof IllusionItem) {
            ((IllusionItem) item).registerIllusion();
        }

        return item;
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp){
        if(cha.getInventory() != null){
            ItemInstance armor = cha.getInventory().value(cbp.readD());
            if(armor!=null && armor.getItem().isEnchant() && armor instanceof ItemArmorInstance && EventController.containsIllusion((ItemArmorInstance)armor)){
                if(cha instanceof PcInstance)
                    armor.toEnchant((PcInstance)cha, toEnchant(cha, armor, this));
                cha.getInventory().count(this, getCount()-1, true);
            }
        }
    }
}

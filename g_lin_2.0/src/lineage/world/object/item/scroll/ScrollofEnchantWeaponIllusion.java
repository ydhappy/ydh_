package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.EventController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemIllusionInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;
import lineage.bean.lineage.IllusionItem; // ✅ 기존에 만든 인터페이스 import

public class ScrollofEnchantWeaponIllusion extends Enchant implements IllusionItem {

    // 내부적으로 ItemIllusionInstance를 포함하여 형변환 문제 해결
    private final ItemIllusionInstance illusionInstance;

    public ScrollofEnchantWeaponIllusion() {
        this.illusionInstance = new ItemIllusionInstance();
    }

    // IllusionItem 인터페이스 구현
    @Override
    public void registerIllusion() {
        EventController.appendIllusion(illusionInstance);
    }

    static synchronized public ItemInstance clone(ItemInstance item){
        if(item == null)
            item = new ScrollofEnchantWeaponIllusion();
        
        // 내부적으로 관리하는 illusionInstance를 등록
        if (item instanceof IllusionItem) {
            ((IllusionItem) item).registerIllusion();
        }

        return item;
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp){
        if(cha.getInventory() != null){
            ItemInstance weapon = cha.getInventory().value(cbp.readD());
            if(weapon!=null && weapon.getItem().isEnchant() && weapon instanceof ItemWeaponInstance && EventController.containsIllusion((ItemWeaponInstance)weapon)){
                if(cha instanceof PcInstance)
                    weapon.toEnchant((PcInstance)cha, toEnchant(cha, weapon, this));
                cha.getInventory().count(this, getCount()-1, true);
            }
        }
    }
}

package system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.server.S_Inventory;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.bean.database.Shop;
import lineage.share.Lineage;

public class ItemSearchSystem extends S_Inventory {

    static synchronized public BasePacket clone(BasePacket bp, PcInstance pc, List<ItemInstance> items, int blessStatus, int enchantLevel) {
        if (bp == null) {
            bp = new ItemSearchSystem(pc, items, blessStatus, enchantLevel);
        } else {
            ((ItemSearchSystem) bp).toClone(pc, items, blessStatus, enchantLevel);
        }
        return bp;
    }

    static synchronized public BasePacket clone(BasePacket bp, List<ItemInstance> items) {
        return clone(bp, null, items, 1, 0);
    }

    public ItemSearchSystem(PcInstance pc, List<ItemInstance> items, int blessStatus, int enchantLevel) {
        toClone(pc, items, blessStatus, enchantLevel);
    }

    public void toClone(PcInstance pc, List<ItemInstance> items, int blessStatus, int enchantLevel) {
        clear();
        writeC(Opcodes.S_OPCODE_SHOPBUY);

        if (pc != null) {
            // ✅ 새로운 가상 상점(TempGmShop) 생성
            ShopInstance tempGmShop = new ShopInstance();
            tempGmShop.setObjectId((int) ServerDatabase.nextNpcObjId());

            List<Shop> shopItems = new ArrayList<>();

            for (ItemInstance item : items) {
                Item i = ItemDatabase.find_ItemCode(item.getItem().getItemCode());
                if (i != null) {
                    Shop s = new Shop();
                    s.setItemName(i.getName());
                    s.setItemCode(i.getItemCode());
                    s.setPrice(1); // 기본 가격 설정
                    s.setAdenType("아데나");
                    s.setItemCount(1);
                    s.setItemEnLevel(enchantLevel);
                    s.setItemBress(blessStatus);
                    shopItems.add(s);
                }
            }

            // ✅ `setTempGmShop()`을 사용해 가상 상점 저장
            if (!shopItems.isEmpty()) {
                tempGmShop.setShopItems(shopItems);
                pc.setTempGmShop(tempGmShop);
            }

            writeD(tempGmShop.getObjectId()); // ✅ 가상 상점 ID 전송
        } else {
            writeD(0);
        }

        toSearch(items, blessStatus, enchantLevel);
    }

    private void toSearch(List<ItemInstance> items, int blessStatus, int enchantLevel) {
        writeH(items.size());

        for (ItemInstance item : items) {
            Item i = ItemDatabase.find_ItemCode(item.getItem().getItemCode());
            if (i != null) {
                int itemId = i.getItemCode();  // ✅ 아이템 ID 가져오기
                writeD(itemId);  // ✅ 아이템 ID를 패킷에 작성
                writeH(i.getInvGfx());
                writeD(1); // ✅ 가격 설정

                // ✅ 올바른 축복/저주 태그 적용
                String statusTag = (blessStatus == 0) ? "(축) " : (blessStatus == 2) ? "(저주) " : "";

                StringBuilder sb = new StringBuilder();
                sb.append(statusTag);

                if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor")) && enchantLevel != 0) {
                    sb.append(enchantLevel > 0 ? "+" : "-").append(Math.abs(enchantLevel)).append(" ");
                }

                sb.append(i.getName());
                writeS(sb.toString());

                // ✅ 클라이언트 튕김 방지: 아이템 타입별 write 처리
                if (Lineage.server_version > 144) {
                    if (i.getType1().equalsIgnoreCase("armor")) {
                        toArmor(i, null, 0, enchantLevel, (int) i.getWeight(),
                                enchantLevel > 4 ? (enchantLevel - 4) * i.getEnchantMr() : 0,
                                enchantLevel, enchantLevel * i.getEnchantStunDefense());
                    } else if (i.getType1().equalsIgnoreCase("weapon")) {
                        toWeapon(i, null, 0, enchantLevel, (int) i.getWeight(), blessStatus);
                    } else {
                        toEtc(i, (int) i.getWeight());
                    }
                }
            }
        }
    }
}
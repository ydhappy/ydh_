package goldbitna.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ItemChange2 extends ItemInstance {

    private static final List<String> ITEM_LIST_1 = Arrays.asList("황금 지휘봉", "대검", "장궁", "마나의 지팡이", "미스릴 단검");
    private static final List<String> ITEM_LIST_2 = Arrays.asList("군터의 백드코빈", "무관의 양손검", "살천의 활", "강철 마나의 지팡이", "수정 단검");
    private static final List<String> ITEM_LIST_3 = Arrays.asList("해신의 삼지창", "진 싸울아비 대검", "달의 장궁", "얼음 여왕의 지팡이", "오리하루콘 단검");
    private static final List<String> ITEM_LIST_4 = Arrays.asList("커츠의 검", "나이트발드의 양손검", "제로스의 지팡이", "데스나이트의 불검", "악몽의 장궁");
    private static final List<String> ITEM_LIST_5 = Arrays.asList("진명황의 집행검", "가이아의 격노", "바람칼날의 단검", "수정 결정체 지팡이", "기르타스의 검");

    static synchronized public ItemInstance clone(ItemInstance item) {
        if (item == null)
            item = new ItemChange2();
        return item;
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp) {
        if (cha.getInventory() != null) {
            ItemInstance item = cha.getInventory().value(cbp.readD());

            if (item != null && item.getItem() != null) {
                List<String> tempList = getMatchingItemList(item.getItem().getName());

                if (tempList != null) {
                    tempList.remove(item.getItem().getName());
                    String itemName = tempList.get(Util.random(0, tempList.size() - 1));

                    Item tempItem = ItemDatabase.find(itemName);
                    if (tempItem != null) {
                        ItemInstance temp = ItemDatabase.newInstance(tempItem);
                        temp.setObjectId(ServerDatabase.nextItemObjId());
                        temp.setBless(item.getBless());
                        temp.setEnLevel(item.getEnLevel());
                        temp.setDefinite(true);
                        cha.getInventory().append(temp, true);
                        cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 13533), true);
                        cha.getInventory().count(item, item.getCount() - 1, true);
                        cha.getInventory().count(this, getCount() - 1, true);

                        ChattingController.toChatting(cha, String.format("\\fY%s \\fR을(를) 획득하였습니다.", temp.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
                    }
                } else {
                    ChattingController.toChatting(cha, "해당 아이템에 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
                }
            }
        }
    }

    private List<String> getMatchingItemList(String itemName) {
        List<List<String>> itemLists = Arrays.asList(ITEM_LIST_1, ITEM_LIST_2, ITEM_LIST_3, ITEM_LIST_4, ITEM_LIST_5);
        for (List<String> itemList : itemLists) {
            if (itemList.contains(itemName)) {
                return new ArrayList<>(itemList);
            }
        }
        return null;
    }
}
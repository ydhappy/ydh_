package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class Skeleton extends MonsterInstance {

    private Item quest_item;
    
    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Skeleton();
        return MonsterInstance.clone(mi, m);
    }
    
    public Skeleton() {
        quest_item = ItemDatabase.find("언데드의 열쇠");
    }
    
    @Override
    protected void toAiDead(long time) {
        // 공격자 검색하여 공격자가 1명이며, 퀘스트 진행 중이고 퀘스트 스텝이 맞을 경우
        // 퀘스트 아이템을 자동으로 인벤토리에 추가하는 대신 바닥에 드롭하도록 처리
        if (quest_item != null && getAttackListSize() == 1) {
            Object o = getAttackList(0);
            if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
                PcInstance pc = (PcInstance) o;
                Quest q1 = QuestController.find(pc, Lineage.QUEST_WIZARD_LV15);
                Quest q2 = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);

                // 두 퀘스트 중 하나라도 조건을 만족할 경우
                if ((q1 != null && q1.getQuestStep() >= 0) || (q2 != null && q2.getQuestStep() >= 0)) {
                    if (pc.getInventory().findDbNameId(quest_item.getNameIdNumber()) == null) {
                        dropMultipleItemsOnGround("언데드의 열쇠", 1, this);
                    }
                }
            }
        }
        super.toAiDead(time);
    }
    
    private static void dropMultipleItemsOnGround(String itemName, int count, MonsterInstance mon) {
        for (int i = 0; i < count; i++) {
            ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(itemName));
            ii.setCount(1);
            dropItemOnGround(ii, mon);
        }
    }

    private static void dropItemOnGround(ItemInstance ii, MonsterInstance mon) {
        if (ii.getObjectId() == 0) {
            ii.setObjectId(ServerDatabase.nextItemObjId());
        }

        int x = Util.random(mon.getX() - 1, mon.getX() + 1);
        int y = Util.random(mon.getY() - 1, mon.getY() + 1);

        if (World.isThroughObject(x, y + 1, mon.getMap(), 0)) {
            ii.toTeleport(x, y, mon.getMap(), false);
        } else {
            ii.toTeleport(mon.getX(), mon.getY(), mon.getMap(), false);
        }
        ii.toDrop(mon);
    }
}
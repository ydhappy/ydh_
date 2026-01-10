package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class Bugbear extends MonsterInstance {

    private Item quest_item;
    
    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Bugbear();
        return MonsterInstance.clone(mi, m);
    }

    public Bugbear() {
        quest_item = ItemDatabase.find("비밀방 열쇠");
    }
    
    @Override
    protected void toAiDead(long time) {
        if (getAttackListSize() == 1) {
            Object o = getAttackList(0);
            if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
                PcInstance pc = (PcInstance) o;
                Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);

                // 특정 맵에서 비밀방 열쇠 드랍 로직
                if (getMap() == 13 && q != null && q.getQuestStep() == 5) {
                    if (quest_item != null) {
                        // 아이템의 드랍 확률 계산
                        double dropChance = 0.4 + quest_item.getDropChance();
                        if (Math.random() < dropChance * Lineage.rate_drop) {
                            // Item을 ItemInstance로 변환
                            ItemInstance ii = ItemDatabase.newInstance(quest_item);
                            ii.setObjectId(ServerDatabase.nextItemObjId());
                            // 비밀방 열쇠를 땅에 드랍하도록 처리
                            dropItemOnGround(ii, this);
                        }
                    }
                }
            }
        }
        super.toAiDead(time);
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
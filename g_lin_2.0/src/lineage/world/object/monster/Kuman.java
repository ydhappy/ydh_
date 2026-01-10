package lineage.world.object.monster;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.QuestController;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Kuman extends MonsterInstance {

    private Item quest_item;

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Kuman();
        return MonsterInstance.clone(mi, m);
    }

    public Kuman() {
        quest_item = ItemDatabase.find("리자드맨의 보물");
    }
  
    @Override
    protected void toAiDead(long time) {
		// 공격자 검색 하여 공격자가 1명이며, 퀘스트진행중이고 퀘스트스탭이 맞을경우 퀘아이템 오토루팅 지급 처리. 1개이상 지급못하도록 하기위해 인벤 검색도 함.
		if(quest_item!=null && getAttackListSize()==1){
			object o = getAttackList(0);
			if(o instanceof PcInstance && !(o instanceof RobotInstance)) {
				PcInstance pc = (PcInstance)o;
				Quest q = QuestController.find(pc, Lineage.QUEST_LELDER);
	            if (q != null && q.getQuestStep() == 2) {
	                ItemInstance ii = ItemDatabase.newInstance(quest_item);
					ii.setObjectId(ServerDatabase.nextItemObjId());
					pc.getInventory().append(ii, true);
					// \f1%0%s 당신에게 %1%o 주었습니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), ii.getName()));
				}
			}
		}
        super.toAiDead(time);
        setDead(true);
        setAiStatus(Lineage.AI_STATUS_DEAD);
        ChattingController.toChatting(this, "쿠우오.. 보물.. 그 보물만은...", Lineage.CHATTING_MODE_NORMAL);
    }
}
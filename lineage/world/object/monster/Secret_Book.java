package lineage.world.object.monster;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class Secret_Book extends MonsterInstance {

	private Item quest_item;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Secret_Book();
		return MonsterInstance.clone(mi, m);
	}

	public Secret_Book() {
		quest_item = ItemDatabase.find("초보 비법서");
	}

	@Override
	protected void toAiDead(long time) {
	    // 공격자 검색하여 공격자가 1명이며, 퀘스트 진행 중이고 퀘스트 스텝이 맞을 경우 퀘스트 아이템 처리
	    if (quest_item != null && getAttackListSize() == 1) {
	        Object o = getAttackList(0);
	        if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
	            PcInstance pc = (PcInstance) o;
	            Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
	            if (q != null && q.getQuestStep() == 2) {
	                ItemInstance Secret_Book = pc.getInventory().find(ItemDatabase.find("초보 비법서"));
	                // 플레이어가 이미 퀘스트 아이템을 가지고 있지 않은지 확인
	                if (Secret_Book == null) {
	                    // 드랍율을 100%로 변경 (항상 드랍)
	                    double dropRate = 0.60; // 1.0 = 100%
	                    if (Math.random() < dropRate) {
	                        // 아이템을 자동 지급
	                        ItemInstance ii = ItemDatabase.newInstance(quest_item);
	                        ii.setObjectId(ServerDatabase.nextItemObjId());
	                        pc.getInventory().append(ii, true);
	                        // \f1%0%s 당신에게 %1%o 주었습니다.
	                        pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), ii.getName()));
	                    }
	                }
	            }
	        }
	    }
	    super.toAiDead(time);
	}
}
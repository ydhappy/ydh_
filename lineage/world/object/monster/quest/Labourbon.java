package lineage.world.object.monster.quest;

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
import lineage.world.object.item.SilverFlute;

public class Labourbon extends MonsterInstance {

	private Item quest_item;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Labourbon();
		return MonsterInstance.clone(mi, m);
	}

	public Labourbon() {
		quest_item = ItemDatabase.find("친구의 가방");
	}

	@Override
	protected void toAiDead(long time) {
	    // 공격자 검색하여 공격자가 1명이며, 퀘스트 진행 중이고 퀘스트 스텝이 맞을 경우 퀘스트 아이템 오토루팅 지급 처리
	    if (quest_item != null && getAttackListSize() == 1) {
	        Object o = getAttackList(0);
	        if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
	            PcInstance pc = (PcInstance) o;
	            Quest q = QuestController.find(pc, Lineage.QUEST_RUDIAN);
	            if (q != null && q.getQuestStep() == 1) {
	                ItemInstance ii = ItemDatabase.newInstance(quest_item);
	                ii.setObjectId(ServerDatabase.nextItemObjId());
	                pc.getInventory().append(ii, true);
	                // \f1%0%s 당신에게 %1%o 주었습니다.
	                pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), ii.getName()));
	            }
	        }
	    }
	    super.toAiDead(time);
	    SilverFlute.onMonsterDisappear(this);
	}
}
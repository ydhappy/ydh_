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
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class ArachnevilElder extends MonsterInstance {

	private Item quest_item;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new ArachnevilElder();
		return MonsterInstance.clone(mi, m);
	}

	public ArachnevilElder() {
		quest_item = ItemDatabase.find("웅골리언트의 발톱");
	}

	@Override
	protected void toAiDead(long time) {
		if (getAttackListSize() == 1) {
			Object o = getAttackList(0);
			if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
				PcInstance pc = (PcInstance) o;
				Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);

				if (q != null && q.getQuestStep() == 1) {
					if (quest_item != null) {
						// 아이템의 드랍 확률 계산
						double dropChance = 0.4 + quest_item.getDropChance();
						if (Math.random() < dropChance * Lineage.rate_drop) {
							// Item을 ItemInstance로 변환
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

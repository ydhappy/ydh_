package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class Sealed_Succubus extends MonsterInstance {

	private Item quest_item;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Sealed_Succubus();
		return MonsterInstance.clone(mi, m);
	}

	/**
	 * 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman(){
		boolean find = false;
		for(object o : getInsideList(true)){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(isAttack(pc, true)){
					addAttackList(pc);
					find = true;
				}
			}
		}
		return find;
	}
	
	public Sealed_Succubus() {
		quest_item = ItemDatabase.find("검은 열쇠");
	}

	@Override
	protected void toAiWalk(long time){
	    super.toAiWalk(time);

	    if(toSearchHuman()) {
	        ChattingController.toChatting(this, "우리들을 봉인 하려는 자! 반드시 죽는다!", Lineage.CHATTING_MODE_SHOUT);
	        new Thread(() -> {
	            try {
	                Thread.sleep(10000); // 10초 대기
	                ChattingController.toChatting(this, "어리석은 놈! 너 따위가 우리를 봉인 할 수 있을 거 같아?!", Lineage.CHATTING_MODE_SHOUT);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }).start();
	    } else {
	    }
	    return;
	}
	
	@Override
	protected void toAiDead(long time) {
	    // 공격자 검색하여 공격자가 1명이며, 퀘스트 진행 중이고 퀘스트 스텝이 맞을 경우 퀘스트 아이템 처리
	    if (quest_item != null && getAttackListSize() == 1) {
	        Object o = getAttackList(0);
	        if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
	            PcInstance pc = (PcInstance) o;
	            Quest q = QuestController.find(pc, Lineage.QUEST_RUBA);
	            if (q != null && q.getQuestStep() == 0) {
	                ItemInstance Secret_Book = pc.getInventory().find(ItemDatabase.find("검은 열쇠"));
	                // 플레이어가 이미 퀘스트 아이템을 가지고 있지 않은지 확인
	                if (Secret_Book == null) {
	                    // 낮은 확률로 퀘스트 아이템 자동 지급
	                    double dropRate = 0.60; // 예: 5% 확률
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
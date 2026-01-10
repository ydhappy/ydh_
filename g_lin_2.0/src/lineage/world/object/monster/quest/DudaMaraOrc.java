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
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class DudaMaraOrc extends MonsterInstance {

	private Item quest_item;
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new DudaMaraOrc();
		return MonsterInstance.clone(mi, m);
	}
	
	public DudaMaraOrc(){
		quest_item = ItemDatabase.find("두다-마라의 토템");
	}
	
	@Override
	protected void toAiDead(long time) {
	    // 공격자 검색: 공격자가 1명이며, 퀘스트 진행 중인 상태에서 퀘스트 스탭이 맞을 경우
	    if(quest_item != null && getAttackListSize() == 1){
	        object o = getAttackList(0);
	        if(o instanceof PcInstance && !(o instanceof RobotInstance)){
	            PcInstance pc = (PcInstance)o;
	            Quest q = QuestController.find(pc, Lineage.QUEST_LYRA);
	            if(q != null && q.getQuestStep() == 1 && Util.random(0, 100) <= Lineage.quest_lyra_drop_rate){
	                // 새로운 아이템 인스턴스 생성
	                ItemInstance ii = ItemDatabase.newInstance(quest_item);
	                ii.setObjectId(ServerDatabase.nextItemObjId());
	                
	                // 인벤토리에서 이미 같은 아이템이 있는지 확인
	                ItemInstance existing = pc.getInventory().find(ii);
	                if(existing == null){
	                    // 인벤토리에 없는 경우 새로 추가
	                    pc.getInventory().append(ii, true);
	                    pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), ii.getName()));
	                } else {
	                    // 이미 있을 경우 기존 아이템과 합치기 (수량 증가)
	                    int newCount = (int)( existing.getCount() + ii.getCount() );
	                    pc.getInventory().count(existing, newCount, true);
	                    pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), existing.getName()));
	                }
	            }
	        }
	    }
	    super.toAiDead(time);
	}
}

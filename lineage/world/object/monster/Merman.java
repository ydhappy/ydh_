package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Merman extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Merman();
		return MonsterInstance.clone(mi, m);
	}
	
	/**
	 * hp에 따른 멘트 출력
	 */
	private boolean HP70 = false;
	
	/**
	 * 타 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman(){
		boolean find = false;
		for(object o : getInsideList()){
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

	@Override
	public void toAiAttack(long time) {
	    if (!HP70 && getNowHp() <= getTotalHp() * 0.7) {
	    	ChattingController.toChatting(this, "또 우리의 비늘을 뺐으러 온 것이냐. 인간! 어서 꺼져!", Lineage.CHATTING_MODE_NORMAL);
	        HP70 = true;
	        return;
	    }
	    super.toAiAttack(time);
	}
	
	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		if(toSearchHuman())
		return;
	}
}
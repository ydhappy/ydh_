package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class Troll extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Troll();
		return MonsterInstance.clone(mi, m);
	}
	
	private long ai_time_temp_1;
	private boolean revival;
	private Object sync = new Object();
	
	@Override
	public void close(){
		super.close();
		revival = false;
	}
		
	@Override
	public void setAiStatus(int ai_status) {
		super.setAiStatus(ai_status);
		if(Lineage.AI_STATUS_SPAWN == ai_status) {
			revival = false;
		}
	}
	
	@Override
	protected void toAiCorpse(long time) {

		synchronized (sync) {
			// 지정된 맵은 부활허용하지 않는다 
			if (revival || getMap()==70 || getMap()==88 || !isDead()) {
				super.toAiCorpse(time);
				return;
			}
		
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+8);

		if(ai_time_temp_1 == 0)
			ai_time_temp_1 = time;

		if(this instanceof MonsterInstance && ai_time_temp_1+Lineage.ai_corpse_time > time)
			return;
		
		ai_time_temp_1 = 0;
		}
		toRevival(this);
		setNowHp(getMaxHp());	
	}

	@Override
	public void toRevival(object o) {
		synchronized (sync) {
			// 맵체크 부활 하지 않는다.
			if (revival || getMap()==70 || getMap()==88 || !isDead())
				return;
			revival = true;
			super.toRevival(o);
			setNowHp(getMaxHp());
		}
	}
	
}
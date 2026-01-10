package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSpawnlist;
import lineage.share.Lineage;
import lineage.world.object.instance.MonsterInstance;

public class Chaospriest extends MonsterInstance {

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Chaospriest();
        return MonsterInstance.clone(mi, m);
    }

	@Override
	public void close() {
		super.close();
	}

	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		//
		setDead(false);
		setNowHp(getMaxHp());
		setNowMp(getMaxMp());
		setGfx(getClassGfx());
		setGfxMode(getClassGfxMode());
		setAiStatus(Lineage.AI_STATUS_WALK);
		setHeading(getHomeHeading());
		//
		super.toTeleport(x, y, map, effect);
	}
	
    @Override
    protected void toAiDead(long time){
        super.toAiDead(time);
        // 상태 변환
        setAiStatus(Lineage.AI_STATUS_CORPSE);
    }
}
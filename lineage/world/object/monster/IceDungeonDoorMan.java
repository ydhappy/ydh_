package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.IceDungeon;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class IceDungeonDoorMan extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new IceDungeonDoorMan();
		return MonsterInstance.clone(mi, m);
	}

	private IceDungeon iceDungeon;

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
		setHeading(getHomeHeading());
		setAiStatus(Lineage.AI_STATUS_WALK);
		//
		super.toTeleport(x, y, map, effect);
		//
		setMonsterSpawnlist(new MonsterSpawnlist());
		getMonsterSpawnlist().setSentry(true);
		getMonsterSpawnlist().setHeading(getHomeHeading());
	}

	@Override
	protected void toAiDead(long time) {
	    super.toAiDead(time);
	    
	    IceDungeon iceDungeon = getIceDungeon();
	    if (iceDungeon == null) {
	        // 로그를 기록하거나 예외를 던지는 등의 처리를 추가
	        return; // 또는 throw new IllegalStateException("IceDungeon is null");
	    }

	    if (this instanceof IceDungeonBoss)
	        iceDungeon.updateBossDead();
	    else
	        iceDungeon.updateDoormanDead();
	}
	@Override
	protected void toAiSpawn(long time) {
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode + Lineage.GFX_MODE_DEAD);
		// 스폰 대기.
	}

	public IceDungeon getIceDungeon() {
		return iceDungeon;
	}

	public void setIceDungeon(IceDungeon iceDungeon) {
		this.iceDungeon = iceDungeon;
	
	}

}

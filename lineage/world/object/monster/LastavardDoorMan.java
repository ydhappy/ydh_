package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.Lastavard;
import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class LastavardDoorMan extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new LastavardDoorMan();
		return MonsterInstance.clone(mi, m);
	}

	private Lastavard lastavard;

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

		Lastavard l = getLastavard();
		if (l == null) {
//			System.err.println("[ERROR] Lastavard is null in " + getName() + " during toAiDead.");
			return;
		}

		if(this instanceof LastavardBoss)
			l.updateBossDead();
		else
			l.updateDoormanDead();
	}

	@Override
	protected void toAiSpawn(long time) {
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode + Lineage.GFX_MODE_DEAD);
		// 스폰 대기.
	}

	public void toMoving(object o, int x, int y, int h, boolean astar) {
		if ((this instanceof LastavardBoss))
			super.toMoving(o, x, y, h, astar);
	}

	public Lastavard getLastavard() {
		return lastavard;
	}

	public void setLastavard(Lastavard lastavard) {
		this.lastavard = lastavard;
	}

}

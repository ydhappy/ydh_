package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.lineage.IceDungeon;
import lineage.share.Lineage;
import lineage.world.object.instance.MonsterInstance;

public class IceDungeonBoss extends IceDungeonDoorMan {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new LastavardBoss();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void close() {
		super.close();
	}

	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
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
}

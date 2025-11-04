package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Cancellation;

public class Ettin extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Ettin();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		super.toDamage(cha, dmg, type, opt);

		if (type == Lineage.ATTACK_TYPE_MAGIC && opt != null && opt.length > 0) {
			Class<?> c = (Class<?>) opt[0];
	        if (c.toString().equalsIgnoreCase(Cancellation.class.toString())) {
	            //
	            String find_name = getMonster().getGfx() == 1128 ? "해골(퀘)" : "에틴(퀘)";
	            Monster mon = MonsterDatabase.find(find_name);
	            if (mon != null) {
	                // 몬스터 교체 로직 실행
	                MonsterSpawnlistDatabase.CancellationMonsterRenew(this, mon);
	               
	            }
	        }
	    }
	}
}

package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.object.instance.MonsterInstance;

public class NotSelectedPerson extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new NotSelectedPerson();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void toAiAttack(long time) {
		// hp가 30%미만이면 도망모드로 변환.
		if (getNowHp() <= getTotalHp() * 0.1) {
			setAiStatus(Lineage.AI_STATUS_ESCAPE);
			return;
		}
		super.toAiAttack(time);
	}

	@Override
	public void toAiEscape(long time) {
		// hp가 30%이상이라면 공격모드로 전환.
		if (getNowHp() > getTotalHp() * 0.15) {
			setAiStatus(Lineage.AI_STATUS_ATTACK);
			return;
		}
		super.toAiEscape(time);
	}

}

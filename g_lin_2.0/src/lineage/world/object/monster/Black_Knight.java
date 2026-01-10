package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;

public class Black_Knight extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Black_Knight();
		return MonsterInstance.clone(mi, m);
	}

	/**
	 * 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman() {
		boolean find = false;

		for (Object o : getInsideList(true)) {
			if (o instanceof PcInstance) {
				PcInstance pc = (PcInstance) o;
				if (pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
					addAttackList(pc);
					find = true;
				} else {
					if (isAttack(pc, true) && pc.getPkTime() > 0 && !(pc instanceof RobotInstance)) {
						addAttackList(pc);
						find = true;
					}
				}
			}
		}
		return find;
	}

	@Override
	protected void toAiWalk(long time) {
		super.toAiWalk(time);
		if (toSearchHuman())
			return;
	}

	@Override
	public boolean isAttack(Character cha, boolean magic) {
		if (getGfxMode() != getClassGfxMode())
			return false;
		return super.isAttack(cha, magic);
	}
}
package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

import java.util.Random;

public class IceQueenHandmaiden extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new IceQueenHandmaiden();
		return MonsterInstance.clone(mi, m);
	}

	/**
	 * hp에 따른 멘트 출력
	 */
	private boolean HP40 = false;
	
	/**
	 * 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman() {
		boolean find = false;
		for (object o : getInsideList(true)) {
			if (o instanceof PcInstance) {
				PcInstance pc = (PcInstance) o;
				toTeleport(pc);
				if (isAttack(pc, true)) {
					addAttackList(pc);
					hasShouted = false;
					find = true;
				}
			}
		}
		return find;
	}

	@Override
	public void toAiAttack(long time) {
	    if (!HP40 && getNowHp() <= getTotalHp() * 0.9) {
	        ChattingController.toChatting(this, "인간주제에 혹한의 힘을 견딜 수 있겠느냐", Lineage.CHATTING_MODE_NORMAL);
	        HP40 = true;
	        return;
	    }
		super.toAiAttack(time);
		if (getAttackList().size() > 0 && !hasShouted) {
			ChattingController.toChatting(this, "여왕님의 혼란을 방해할 수는 없다", Lineage.CHATTING_MODE_NORMAL);
			hasShouted = true;
		}
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

	/**
	 * 몬스터를 플레이어 캐릭터 주변으로 텔레포트하는 함수
	 * 
	 * @param pc
	 */
	private void toTeleport(PcInstance pc) {
		Random random = new Random();

		int offsetX = random.nextInt(3) - 1;
		int offsetY = random.nextInt(3) - 1;

		int newX = pc.getX() + offsetX;
		int newY = pc.getY() + offsetY;

		this.toTeleportRange(newX, newY, pc.getMap(), false, 0);
	}

	private boolean hasShouted = false;
}
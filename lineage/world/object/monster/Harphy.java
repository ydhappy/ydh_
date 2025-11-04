package lineage.world.object.monster;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;

import lineage.bean.database.Monster;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.potion.WisdomPotion;
import lineage.world.object.magic.Detection;
import lineage.world.controller.ChattingController;

public class Harphy extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Harphy();
		return MonsterInstance.clone(mi, m);
	}

	// 휴식도중 반응하는 아이템 목록들
	private static final List<Integer> item_list = new ArrayList<Integer>();

	static {
		item_list.add(23); // 고기
		item_list.add(26); // 체력 회복제
		item_list.add(255); // 고급 체력 회복제
		item_list.add(328); // 강력 체력 회복제
		item_list.add(264); // 속도향상 물약
		item_list.add(16115); // 강화 속도향상 물약
		item_list.add(507); // 마력 회복 물약
		item_list.add(239); // 불투명 물약
		item_list.add(764); // 버섯포자의 즙
		item_list.add(1507);// 에바의 축복
	}

	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		super.toTeleport(x, y, map, effect);

		if (Util.random(0, 1) == 0)
			toStay(true);
	}

	@Override
	public void toAi(long time) {

		if (getGfxMode() == getClassGfxMode()) {
			if (getNowHp() <= (getTotalHp() * 0.4) && Util.random(0, 99) <= 10)
				toStay(true);
		}

		super.toAi(time);
	}

	@Override
	protected void toAiWalk(long time) {
		if (getGfxMode() == getClassGfxMode()) {
			super.toAiWalk(time);
			return;
		}

		ai_time = SpriteFrameDatabase.find(gfx, gfxMode + Lineage.GFX_MODE_WALK) + 1500;

		if (isItemInside() || isPlayerInside())
			toStay(false);
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		super.toDamage(cha, dmg, type);
		if (getGfxMode() == getClassGfxMode())
			return;

		toStay(false);
		addAttackList(cha);
	}

	@Override
	public void toMagic(Character cha, Class<?> c) {
		if (getGfxMode() == getClassGfxMode())
			return;

		toStay(false);
		addAttackList(cha);
	}

	@Override
	public void toAiAttack(long time) {
		if (getGfxMode() == getClassGfxMode()) {
			super.toAiAttack(time);
			return;
		}

		if (isItemInside()) {
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode + 8);
			toStay(false);
		}
	}

	// 몹이 죽은 상태인지 확인하는 메서드
	private boolean toAiDead() {
		return getNowHp() <= 0;
	}

	@Override
	protected boolean isPickupItem(object o) {
		if (o instanceof HealingPotion || o instanceof HastePotion || o instanceof WisdomPotion || o instanceof BraveryPotion)
			return true;
		if (o instanceof ItemInstance && !containsAstarList(o))
			return item_list.contains(((ItemInstance) o).getItem().getNameIdNumber());
		else
			return super.isPickupItem(o);
	}

	@Override
	public void setNowHp(int nowHp) {
		if (getGfxMode() == getClassGfxMode())
			super.setNowHp(nowHp);
	}

	/**
	 * 주변에사용자가존재하는지 확인.
	 * 
	 * @return
	 */
	private boolean isPlayerInside() {
		for (object o : getInsideList()) {
			if (o instanceof PcInstance && Util.isDistance(this, o, 2)) {
				// toDamage((PcInstance)o, 0, 3);
				return true;
			}
		}
		return false;
	}

	/**
	 * 주변에 반응하는 아이템이 드랍되잇는지 확인.
	 * 
	 * @return
	 */
	private boolean isItemInside() {
		for (object o : getInsideList(true)) {
			if (isPickupItem(o))
				return true;
		}
		return false;
	}

	private void toStay(boolean recess) {
		if (!recess) {
			ai_time = SpriteFrameDatabase.find(gfx, 45);
			setGfxMode(getClassGfxMode());
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 45), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
			Executors.newSingleThreadScheduledExecutor().schedule(() -> {
				if (!toAiDead()) { // 몹이 죽지 않았는지 확인
					ChattingController.toChatting(Harphy.this, "먹이다! 먹이!", Lineage.CHATTING_MODE_SHOUT);
				}
			}, 1, TimeUnit.SECONDS);

		} else {
			ai_time = SpriteFrameDatabase.find(gfx, 44);
			setGfxMode(4);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 44), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		}
	}

}

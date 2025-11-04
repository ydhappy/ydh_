package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;

public class IceMonster extends MonsterInstance {
	
	public static synchronized MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new IceMonster();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void toTeleport(int x, int y, int map, boolean effect) {
		super.toTeleport(x, y, map, effect);
		toStay(true);
	}

	@Override
	protected void toAiWalk(long time) {
		if (getGfxMode() == getClassGfxMode()) {
			super.toAiWalk(time);
			return;
		}

		ai_time = SpriteFrameDatabase.find(gfx, gfxMode + Lineage.GFX_MODE_WALK);
	}
	
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		super.toDamage(cha, dmg, type, opt);

		if (getGfxMode() != getClassGfxMode())
			toStay(false);
	}

	private void toStay(boolean recess) {
		if (!recess) {
			ai_time = SpriteFrameDatabase.find(gfx, 11);
			setGfxMode(getClassGfxMode());
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 11), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		} else {
			ai_time = SpriteFrameDatabase.find(gfx, 4);
			setGfxMode(4);
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this), false);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
		}
	}

}


package lineage.world.object.monster;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class TrapArrow extends Character {

	static synchronized public TrapArrow clone(TrapArrow ta, int[] loc, int gfx){
		if(ta == null)
			ta = new TrapArrow();
		ta.loc = loc;
		ta.gfx = gfx;
		ta.weapon = ItemDatabase.newInstance(ItemDatabase.find("장궁"));
		ta.arrow = ItemDatabase.newInstance(ItemDatabase.find("은화살"));
		ta.setStr(18);
		ta.setDex(18);
		ta.setCon(18);
		ta.setLevel(12);
		ta.invis = true;
		return ta;
	}

	private int[] loc;
	private ItemInstance weapon;
	private ItemInstance arrow;
	
	@Override
	public void close() {
		super.close();
		
		loc = null;
	}
	
	@Override
	public void setInvis(boolean invis) {}
	
	@Override
	protected void toAiWalk(long time) {
		ai_time = Util.random(500, 1000) * Util.random(1, 3);

		object target = null;
		int distance = 0;
		int dmg = 50;
		for(object o : getInsideList(true)) {
			boolean is = false;
			switch(Util.calcheading(loc[0], loc[1], loc[2], loc[3])) {
				case 4:
					is = loc[0]==o.getX() && loc[3]>=o.getY() && loc[1]<=o.getY();
					break;
				case 6:
					is = loc[1]==o.getY() && loc[0]>=o.getX() && loc[2]<=o.getX();
					break;
			}
			if(is && !o.isDead()) {
				int dt = Util.getDistance(loc[0], loc[1], o.getX(), o.getY());
				if(distance==0 || distance>dt) {
					distance = dt;
					target = o;
				}
			}
		}

		if(target != null) {
			DamageController.toDamage(this, target, dmg, Lineage.ATTACK_TYPE_WEAPON);
		}
		toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, target, getGfxMode()+1, dmg, getGfx(), true, true, loc[2], loc[3]), false);
	}

}

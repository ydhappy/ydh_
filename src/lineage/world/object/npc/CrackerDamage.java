package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class CrackerDamage extends GuardInstance {
			
	public CrackerDamage(Npc npc) {
	    super(npc); 
	    
	    switch (getNpc().getGfx()) {
	        case 12548: // 약한 NPC
	            setDynamicMr(3);
	            setAc(0);
	            setLevel(15);
	            setDex(18);
	            setStr(18);
	            setInt(18);
	            setWis(18);
	            break;
	            
	        case 12553: // 보통 NPC
	            setDynamicMr(4);
	            setAc(50);
	            setLevel(52);
	            setDex(25);
	            setStr(25);
	            setInt(25);
	            setWis(25);
	            break;
	            
	        case 12550: // 강한 NPC
	            setDynamicMr(19);
	            setAc(90);
	            setLevel(75);
	            setDex(35);
	            setStr(35);
	            setInt(35);
	            setWis(35);
	            break;
	    }

	    CharacterController.toWorldJoin(this);
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);
			if ((weapon != null) && (type == Lineage.ATTACK_TYPE_WEAPON || type == Lineage.ATTACK_TYPE_BOW)) {
				if (cha.getWeapon() == null) {
					cha.setWeapon(weapon);
					cha.setMinDmg(dmg);
					cha.setMaxDmg(dmg);
				} else if (cha.getWeapon().getObjectId() != weapon.getObjectId() || cha.getLastHitTime() + (1000 * 3) < System.currentTimeMillis()) {
					cha.setWeapon(weapon);
					cha.setDmg(0);
					cha.setMinDmg(dmg);
					cha.setMaxDmg(dmg);
					cha.setHitCount(0);
				}
				cha.setLastHitTime(System.currentTimeMillis());
				cha.setDmg(cha.getDmg() + dmg);
				cha.setHitCount(cha.getHitCount() + 1);
				// 최소, 최대 데미지 저장
				if (cha.getMinDmg() > dmg)
					cha.setMinDmg(dmg);
				if (cha.getMaxDmg() < dmg)
					cha.setMaxDmg(dmg);
				// 평균 데미지
				int avgDmg = cha.getDmg() / cha.getHitCount();
				
				// DPS
				long time = System.currentTimeMillis();
				long dpsTime = time - cha.dps_attack_time;
				double attackSpeed = dpsTime * 0.001;
				int dps = (int) Math.round(avgDmg / attackSpeed);
				cha.dps_attack_time = time;
				
				if (Lineage.view_cracker_damage && !pc.isDamageMassage()) {
						ChattingController.toChatting(cha, String.format("대미지: [%d]", dmg), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				
				if (Lineage.view_cracker_damage && !pc.isDamageMassage()) 
					ChattingController.toChatting(cha, String.format("대미지: [%d]", dmg), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		
		setHeading(++heading);
	}
	
	@Override
	protected void toAiWalk(long time){
	    setHeading(6); 
	}
	
	@Override
	public void toAiAttack(long time) {
	}
	
	@Override
	protected void toAiDead(long time) {
	    super.toAiDead(time);
	    toAiSpawn(System.currentTimeMillis());
	}

	@Override
	protected void toAiSpawn(long time) {
	    super.toAiSpawn(time);
	    setDead(false);  
	    setNowHp(getMaxHp());  
	    setHeading(6);  
	    toTeleport(homeX, homeY, homeMap, false);  
	    setAiStatus(Lineage.AI_STATUS_WALK);  
	}
}


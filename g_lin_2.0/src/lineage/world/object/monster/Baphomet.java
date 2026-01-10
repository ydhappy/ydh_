package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.TeleportResetDatabase;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Baphomet extends MonsterInstance { // 바포메트
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Baphomet();
		return MonsterInstance.clone(mi, m);
	}
	
	/**
	 * hp에 따른 멘트 출력
	 */
	private boolean HP90 = false;
	private boolean HP70 = false;
	private boolean HP10 = false;

	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		// 보스몬스터 일경우 말섬던전2층에 사용자들 리스폰위치로 강제 이동 시키기.
		if(boss){
			for(PcInstance pc : World.getPcList()){
				if(pc.getMap() == 2){
					TeleportResetDatabase.toLocation(pc);
					pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), false);
				}
			}
		}
	}

	/**
	 * 클레스 찾아서 공격목록에 넣는 함수.
	 */
	private boolean toSearchHuman(){
		boolean find = false;
		for(object o : getInsideList(true)){
			if(o instanceof PcInstance){
				PcInstance pc = (PcInstance)o;
				if(isAttack(pc, true)){
					addAttackList(pc);
					find = true;
				}
			}
		}
		return find;
	}
	
	@Override
	public void toAiAttack(long time) {
	    if (!HP90 && getNowHp() <= getTotalHp() * 0.9) {
	        ChattingController.toChatting(this, "이런 곳에 가두었다고 나를 완전히 제어할 수 있다 생각하지 말라, 인간들이여!", Lineage.CHATTING_MODE_SHOUT);
	        HP90 = true;
	        return;
	    }

	    if (!HP70 && getNowHp() <= getTotalHp() * 0.8) {
	        ChattingController.toChatting(this, "이런 곳에 가두었다고 나를 완전히 제어할 수 있다 생각하지 말라, 인간들이여!", Lineage.CHATTING_MODE_SHOUT);
	        HP70 = true;
	        return;
	    }

	    if (!HP10 && getNowHp() <= getTotalHp() * 0.1) {
	        ChattingController.toChatting(this, "꺼져라!", Lineage.CHATTING_MODE_SHOUT);
	        HP10 = true;
	        return;
	    }

	    super.toAiAttack(time);
	}

	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		if(toSearchHuman())
		return;
	}

	@Override
	public boolean isAttack(Character cha, boolean magic) {
		if(getGfxMode() != getClassGfxMode())	
			return false;
		return super.isAttack(cha, magic);
	}
}

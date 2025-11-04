package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.bean.database.Monster;
import lineage.bean.database.Npc;
import lineage.bean.database.SummonList;
import lineage.bean.lineage.Summon;
import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.NpcDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SummonListDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_HtmlSummon;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectName;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.SummonController.TYPE;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SoldierInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.instance.SummonInstance.SUMMON_MODE;
import lineage.world.object.item.DogCollar;

public final class SummonController {

	static private List<Summon> list;
	static private List<Summon> pool;
	static private List<SummonInstance> summon_pool;
	static private List<PetInstance> pet_pool;
	static private List<SoldierInstance> soldier_pool;
	static public enum TYPE {
		PET,
		TAME,
		ELEMENTAL,
		MONSTER,
		SOLDIER
	};

	static public void init(){
		TimeLine.start("SummonController..");

		soldier_pool = new ArrayList<SoldierInstance>();
		pet_pool = new ArrayList<PetInstance>();
		summon_pool = new ArrayList<SummonInstance>();
		pool = new ArrayList<Summon>();
		list = new ArrayList<Summon>();

		TimeLine.end();
	}

	/**
	 * 소환객체 관리 필요할때 호출해서 사용.
	 * @param o
	 */
	static public void toWorldJoin(object o){
		Summon s = find(o);
		if(s == null){
			s = getPool();
			s.setMasterObjectId(o.getObjectId());
			synchronized (list) {
				if(!list.contains(s))
					list.add(s);
			}
		}
		// 서먼에 알리기.
		s.toWorldJoin(o);
	}

	/**
	 * 소환객체 에게 마스터가 월드를 나갓다는걸 알리는 용도로 사용.
	 * @param o
	 */
	static public void toWorldOut(object o){
		Summon s = find(o);
		if(s != null){
			if(s.getSize() == 0){
				synchronized (list) {
					list.remove(s);
				}
				setPool(s);
			}else{
				s.toWorldOut();
			}
		}
	}

	/**
	 * 타이머에서 주기적으로 호출.
	 */
	static public void toTimer(){
		// 서먼몬스터 시간체크
		for(Summon s : getList())
			s.toTimer();
	}

	/**
	 * 찾기.
	 * @param o
	 * @return
	 */
	static public Summon find(object o){
		// 이미 지정되잇을경우.
		if(o.getSummon() != null)
			return o.getSummon();
		// 목록에서 찾기.
		for(Summon s : getList()){
			if(s.getMasterObjectId() == o.getObjectId())
				return s;
		}
		return null;
	}
	
	public static List<Summon> getList() {
		synchronized (list) {
			return new ArrayList<Summon>( list );
		}
	}

	/**
	 * 풀에서 하나 꺼내기
	 * @return
	 */
	static private Summon getPool(){
		Summon s = null;
		synchronized (pool) {
			if(pool.size()>0){
				s = pool.get(0);
				pool.remove(0);
			}else{
				s = new Summon();
			}
		}
		return s;
	}

	public static SummonInstance getSummonPoolPublic() {
	    return getSummonPool();
	}

	/**
	 * 풀에 다시 넣기.
	 * @param s
	 */
	static private void setPool(Summon s){
		s.close();
		synchronized (pool) {
			if(!pool.contains(s))
				pool.add(s);
		}
	}

	static private SummonInstance getSummonPool(){
		SummonInstance si = null;
		synchronized (summon_pool) {
			if(summon_pool.size()>0){
				si = summon_pool.get(0);
				summon_pool.remove(0);
			}
		}
		return si;
	}

	static public void setSummonPool(SummonInstance si){
		si.close();
		synchronized (summon_pool) {
			if(!summon_pool.contains(si))
				summon_pool.add(si);
		}
//		lineage.share.System.println("append : "+summon_pool.size());
	}

	public static PetInstance getPetPool(){
		PetInstance pi = null;
		synchronized (pet_pool) {
			if(pet_pool.size()>0){
				pi = pet_pool.get(0);
				pet_pool.remove(0);
			}
		}
//		lineage.share.System.println("remove : "+pet_pool.size());
		return pi;
	}

	static public void setPetPool(PetInstance pi){
		pi.close();
		synchronized (pet_pool) {
			if(!pet_pool.contains(pi))
				pet_pool.add(pi);
		}
//		lineage.share.System.println("append : "+pet_pool.size());
	}
	
	static private SoldierInstance getSoldierPool() {
		SoldierInstance si = null;
		synchronized (soldier_pool) {
			if(soldier_pool.size()>0){
				si = soldier_pool.get(0);
				soldier_pool.remove(0);
			}
		}
		return si;
	}
	
	static public void setSoldierPool(SoldierInstance si) {
		si.close();
		synchronized (soldier_pool) {
			if(!soldier_pool.contains(si))
				soldier_pool.add(si);
		}
	}

	public static Summon createSummonForGM(PcInstance master) {
	    Summon s = getPool();
	    s.setMasterObjectId(master.getObjectId());
	    synchronized (list) {
	        if (!list.contains(s)) {
	            list.add(s);
	        }
	    }
	    return s;
	}

	/**
	 * 몬스터를 길들이는 확률을 계산하는 메서드.
	 *
	 * @param o     길들이기를 시도하는 객체 (ex: 플레이어)
	 * @param mi    길들여질 몬스터 객체
	 * @param meat  길들이기 시도가 고기를 사용한 것인지 여부 (현재 사용되지 않음)
	 * @return      길들이기에 성공하면 true, 실패하면 false
	 */
	static public boolean isTame(object o, MonsterInstance mi, boolean meat) {
	    // 몬스터가 null이거나 이미 죽었거나, 보스 몬스터이거나, 길들이기가 불가능한 몬스터라면 실패
	    if (mi == null || mi.isDead() || mi.getMonster().isBoss() || !mi.getMonster().isTaming())
	        return false;

	    // GM(운영자) 체크: GM 레벨이 1 이상이면 무조건 성공
	    if (o != null && o.getGm() > 0)
	        return true;
	    
	    // 현재 몬스터의 체력 비율(%) 계산
	    int hp = (int) (((double) mi.getNowHp() / (double) mi.getTotalHp()) * 100.0);

	    // 체력 비율에 따른 길들이기 확률 적용
	    if (hp >= 80) // 체력이 80% 이상일 경우, 길들이기 확률 25%
	        return Math.random() < 0.25;
	    else if (hp >= 60) // 체력이 60% ~ 79%일 경우, 길들이기 확률 35%
	        return Math.random() < 0.35;
	    else if (hp >= 50) // 체력이 50% ~ 59%일 경우, 길들이기 확률 40%
	        return Math.random() < 0.40;
	    else if (hp >= 30) // 체력이 30% ~ 49%일 경우, 길들이기 확률 50%
	        return Math.random() < 0.50;
	    else // 체력이 30% 미만일 경우, 길들이기 확률 90%
	        return Math.random() < 0.90;
	}

	/**
	 * 관리중인 객체 전체 마스터에 좌표로 텔레포트.
	 * @param cha
	 */
	static public void toTeleport(Character cha){
		Summon s = find(cha);
		if(s != null){
			boolean is_pet_teleport = true;
			boolean is_summon_teleport = true;
			// 펫객체가 이동불가능한 맵일경우 정보 변경.
			for(int map : Lineage.PetTeleportImpossibleMap){
				if(cha.getMap() == map){
					is_pet_teleport = false;
					break;
				}
			}
			// 서먼이 이동불가능한 맵일경우 정보 변경.
			for(int map : Lineage.SummonTeleportImpossibleMap){
				if(cha.getMap() == map){
					is_summon_teleport = false;
					break;
				}
			}
			// 이동 처리.
			for(object o : s.getList()){
				// 소환된 객체별 구분해서 이동가능여부 확인.
				if(o instanceof PetInstance){
					if(!is_pet_teleport)
						continue;
				}else if(o instanceof SummonInstance){
					if(!is_summon_teleport)
						continue;
				}
				
				int roop_cnt = 0;
				int x = cha.getX();
				int y = cha.getY();
				int map = cha.getMap();
				int lx = x;
				int ly = y;
				int loc = 4;
				
				// 랜덤 좌표 스폰
				do {
					lx = Util.random(x - loc, x + loc);
					ly = Util.random(y - loc, y + loc);
					if (roop_cnt++ > 100) {
						lx = x;
						ly = y;
						break;
					}
				}while(
						!World.isThroughObject(lx, ly+1, map, 0) || 
						!World.isThroughObject(lx, ly-1, map, 4) || 
						!World.isThroughObject(lx-1, ly, map, 2) || 
						!World.isThroughObject(lx+1, ly, map, 6) ||
						!World.isThroughObject(lx-1, ly+1, map, 1) ||
						!World.isThroughObject(lx+1, ly-1, map, 5) || 
						!World.isThroughObject(lx+1, ly+1, map, 7) || 
						!World.isThroughObject(lx-1, ly-1, map, 3) ||
						World.isNotMovingTile(lx, ly, map) ||
						(cha.getX() == lx && cha.getY() == ly)
					);
				
				o.toTeleport(lx, ly, cha.getMap(), false);
				//o.toTeleport(cha.getX(), cha.getY(), cha.getMap(), true);
			}
		}
	}

	/**
	 * 소환이 가능한 필드 및 맵인지 확인하는 함수.
	 * @param o
	 * @return
	 */
	static private boolean isSummon(object o){
		for(int map : Lineage.SummonTeleportImpossibleMap){
			if(map == o.getMap()){
				// 이 근처에서는 몬스터를 소환할 수 없습니다.
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 353));
				return false;
			}
		}
		// 이미 소환수가 존재하면 무시.
		Summon s = find(o);
		if(s != null){
			
		}
		return true;
	}

	/**
	 * 서먼 그레이터 엘리멘탈 처리 함수.
	 * @param cha
	 * @param time
	 */
	static public void toSummonGreaterElemental(Character cha, int time){
		// 소환 불가능한 상태는 무시.
		if(!isSummon(cha))
			return;

		// 속성별 처리.
		String LE_name = "[바람] 서먼그레이터엘리멘탈";
		switch(cha.getAttribute()){
			case Lineage.ELEMENT_EARTH:
				LE_name = "[땅] 서먼그레이터엘리멘탈";
				break;
			case Lineage.ELEMENT_FIRE:
				LE_name = "[불] 서먼그레이터엘리멘탈";
				break;
			case Lineage.ELEMENT_WIND:
				LE_name = "[바람] 서먼그레이터엘리멘탈";
				break;
			case Lineage.ELEMENT_WATER:
				LE_name = "[물] 서먼그레이터엘리멘탈";
				break;
		}

		Monster m = MonsterDatabase.find(LE_name);
		Summon s = find(cha);
		if( s!=null && m!=null && isAppend(null, cha, TYPE.ELEMENTAL) ){
			SummonInstance si = SummonInstance.clone(getSummonPool(), m, time);
			s.append(si);
			si.setElemental(true);
			si.setGfx(m.getGfx());
			si.setGfxMode(m.getGfxMode());
			si.setClassGfx(m.getGfx());
			si.setClassGfxMode(m.getGfxMode());
			si.setName(m.getNameId());
			si.setLevel(m.getLevel());
			si.setExp(m.getExp());
			si.setObjectId(ServerDatabase.nextEtcObjId());
			si.setMaxHp(m.getHp());
			si.setMaxMp(m.getMp());
			si.setNowHp(m.getHp());
			si.setNowMp(m.getMp());
			si.setLawful(m.getLawful());
			si.toTeleport(cha.getX(), cha.getY(), cha.getMap(), false);
			
			AiThread.append(si);
		}
	}

	/**
	 * 서먼 레서 엘리멘탈 처리 함수.
	 * @param cha
	 * @param time
	 */
	static public void toSummonLesserElemental(Character cha, int time){
		// 소환 불가능한 상태는 무시.
		if(!isSummon(cha))
			return;

		// 속성별 처리.
		String LE_name = "[바람] 서먼레서엘리멘탈";
		switch(cha.getAttribute()){
			case Lineage.ELEMENT_EARTH:
				LE_name = "[땅] 서먼레서엘리멘탈";
				break;
			case Lineage.ELEMENT_FIRE:
				LE_name = "[불] 서먼레서엘리멘탈";
				break;
			case Lineage.ELEMENT_WIND:
				LE_name = "[바람] 서먼레서엘리멘탈";
				break;
			case Lineage.ELEMENT_WATER:
				LE_name = "[물] 서먼레서엘리멘탈";
				break;
		}

		Monster m = MonsterDatabase.find(LE_name);
		Summon s = find(cha);
		if( s!=null && m!=null && isAppend(null, cha, TYPE.ELEMENTAL) ){
			SummonInstance si = SummonInstance.clone(getSummonPool(), m, time);
			s.append(si);
			si.setElemental(true);
			si.setGfx(m.getGfx());
			si.setGfxMode(m.getGfxMode());
			si.setClassGfx(m.getGfx());
			si.setClassGfxMode(m.getGfxMode());
			si.setName(m.getNameId());
			si.setLevel(m.getLevel());
			si.setExp(m.getExp());
			si.setObjectId(ServerDatabase.nextEtcObjId());
			si.setMaxHp(m.getHp());
			si.setMaxMp(m.getMp());
			si.setNowHp(m.getHp());
			si.setNowMp(m.getMp());
			si.setLawful(m.getLawful());
			si.toTeleport(cha.getX(), cha.getY(), cha.getMap(), false);

			AiThread.append(si);
		}
	}

	/**
	 * 크리에이트좀비 뒷처리 함수.
	 * @param cha
	 * @param mi
	 * @param time
	 */
	static public void toCreateZombie(Character cha, MonsterInstance mi, int time){
		// 소환 불가능한 상태는 무시.
		if(!isSummon(cha))
			return;

		Summon s = find(cha);
		Monster m = MonsterDatabase.findNameid("$57");
		if( s!=null && mi!=null && m!=null && isAppend(null, cha, TYPE.TAME) ){
			// mi객체 스폰상태로 변경.
			mi.toAiClean(true);
			// 좀비 객체 생성.
			SummonInstance si = SummonInstance.clone(getSummonPool(), m, time);
			s.append(si);
			si.setGfx(m.getGfx());
			si.setGfxMode(m.getGfxMode());
			si.setClassGfx(m.getGfx());
			si.setClassGfxMode(m.getGfxMode());
			si.setName(m.getNameId());
			si.setLevel(m.getLevel());
			si.setExp(m.getExp());
			si.setObjectId(ServerDatabase.nextEtcObjId());
			si.setMaxHp(m.getHp());
			si.setMaxMp(m.getMp());
			si.setNowHp(m.getHp());
			si.setNowMp(m.getMp());
			si.setLawful(m.getLawful());
			si.toTeleport(mi.getX(), mi.getY(), mi.getMap(), false);
			// 인공지능 처리에 등록.
			AiThread.append(si);
		}
	}

	/**
	 * 테이밍 몬스터 뒷처리 함수.
	 * @param cha
	 * @param mi
	 * @param time
	 */
	static public void toTameMonster(Character cha, MonsterInstance mi, int time){
		// 소환 불가능한 상태는 무시.
		if(!isSummon(cha))
			return;

		Summon s = find(cha);
		if( s!=null && mi!=null && isAppend(null, cha, TYPE.TAME) ){
			// mi객체 스폰상태로 변경.
			mi.toAiClean(true);
			// 테이밍 객체 생성.
			SummonInstance si = SummonInstance.clone(getSummonPool(), mi.getMonster(), time);
			s.append(si);
			si.setGfx(mi.getGfx());
			si.setGfxMode(mi.getGfxMode());
			si.setClassGfx(mi.getClassGfx());
			si.setClassGfxMode(mi.getClassGfxMode());
			si.setName(mi.getName());
			si.setLevel(mi.getLevel());
			si.setExp(mi.getMonster().getExp());
			si.setObjectId(ServerDatabase.nextEtcObjId());
			si.setMaxHp(mi.getMaxHp());
			si.setMaxMp(mi.getMaxMp());
			si.setNowHp(mi.getNowHp());
			si.setNowMp(mi.getNowMp());
			si.setLawful(mi.getLawful());
			si.toTeleport(mi.getX(), mi.getY(), mi.getMap(), false);
			// 인공지능에 추가.
			AiThread.append(si);
		}
	}
	
	/**
	 * 용병고용 뒷처리 함수.
	 * @param cha
	 * @param time
	 * @param name
	 */
	static public void toSoldier(Character cha, int time, String name) {
		// 소환 불가능한 상태는 무시.
		if(!isSummon(cha))
			return;

		// 초기화.
		Summon s = find(cha);

		Monster m = MonsterDatabase.find("켄트성 경비병 창");

		//
		if( s!=null && m!=null ){
			// 이미 거느리고 있는 객체가 1개 이상이라면 무시.
			if(s.getSize() > 0)
				return;
			// 소환
			if( s!=null && m!=null && isAppend(null, cha, TYPE.SOLDIER) ){
				SoldierInstance si = SoldierInstance.clone(getSoldierPool(), m, time);
				s.append(si);
				// 기본 정보 처리.
				si.setGfx(m.getGfx());
				si.setGfxMode(m.getGfxMode());
				si.setClassGfx(m.getGfx());
				si.setClassGfxMode(m.getGfxMode());
				si.setName(name);
				si.setLevel(m.getLevel());
				si.setExp(m.getExp());
				si.setObjectId(ServerDatabase.nextEtcObjId());
				si.setMaxHp(m.getHp());
				si.setMaxMp(m.getMp());
				si.setNowHp(m.getHp());
				si.setNowMp(m.getMp());
				si.setLawful(m.getLawful());
				// 스폰 처리.
				si.toTeleport(cha.getX(), cha.getY(), cha.getMap(), false);
				// 인공지능 등록.
				AiThread.append(si);
			}
		}
	}

	/**
	 * ORC 용병고용 뒷처리 함수.
	 * 
	 * @param cha
	 * @param time
	 * @param name
	 */
	static public void toOrcsoldier(Character cha, int time, String name) {
		// 소환 불가능한 상태는 무시.
		if (!isSummon(cha))
			return;

		// 초기화.
		Summon s = find(cha);
		// 버그베어
		Monster m = MonsterDatabase.findNameid("$29009");
		// 근위병
		Npc n = NpcDatabase.find("[용병] 오크");
		//
		if (s != null && m != null && n != null) {
			// 이미 거느리고 있는 객체가 1개 이상이라면 무시.
			if (s.getSize() > 0)
				return;
			// 소환
			if (s != null && isAppend(null, cha, TYPE.SOLDIER)) {
				SoldierInstance si = SoldierInstance.clone(getSoldierPool(), m, time);
				s.append(si);
				// 기본 정보 처리.
				si.setGfx(n.getGfx());
				si.setGfxMode(n.getGfxMode());
				si.setClassGfx(n.getGfx());
				si.setClassGfxMode(n.getGfxMode());
				si.setName(name);
				si.setLevel(m.getLevel());
				si.setExp(m.getExp());
				si.setObjectId(ServerDatabase.nextEtcObjId());
				si.setMaxHp(m.getHp());
				si.setMaxMp(m.getMp());
				si.setNowHp(m.getHp());
				si.setNowMp(m.getMp());
				si.setLawful(m.getLawful());
				// 스폰 처리.
				si.toTeleport(cha.getX(), cha.getY(), cha.getMap(), false);
				// 인공지능 등록.
				AiThread.append(si);
			}
		}
	}
	
	/**
	 * HEINE 용병고용 뒷처리 함수.
	 * 
	 * @param cha
	 * @param time
	 * @param name
	 */
	static public void toHeinesoldier(Character cha, int time, String name) {
		// 소환 불가능한 상태는 무시.
		if (!isSummon(cha))
			return;

		// 초기화.
		Summon s = find(cha);
		// 버그베어
		Monster m = MonsterDatabase.findNameid("$29009");
		// 근위병
		Npc n = NpcDatabase.find("[용병] 하이네");
		//
		if (s != null && m != null && n != null) {
			// 이미 거느리고 있는 객체가 1개 이상이라면 무시.
			if (s.getSize() > 0)
				return;
			// 소환
			if (s != null && isAppend(null, cha, TYPE.SOLDIER)) {
				SoldierInstance si = SoldierInstance.clone(getSoldierPool(), m, time);
				s.append(si);
				// 기본 정보 처리.
				si.setGfx(n.getGfx());
				si.setGfxMode(n.getGfxMode());
				si.setClassGfx(n.getGfx());
				si.setClassGfxMode(n.getGfxMode());
				si.setName(name);
				si.setLevel(m.getLevel());
				si.setExp(m.getExp());
				si.setObjectId(ServerDatabase.nextEtcObjId());
				si.setMaxHp(m.getHp());
				si.setMaxMp(m.getMp());
				si.setNowHp(m.getHp());
				si.setNowMp(m.getMp());
				si.setLawful(m.getLawful());
				// 스폰 처리.
				si.toTeleport(cha.getX(), cha.getY(), cha.getMap(), false);
				// 인공지능 등록.
				AiThread.append(si);
			}
		}
	}
	
	/**
	 * 서먼몬스터 뒷처리 함수.
	 * @param cha
	 */
	static public void toSummonMonster(Character cha, int time, int summon_auction){
		// 소환 불가능한 상태는 무시.
		if(!isSummon(cha))
			return;
		
		int count = 0;
		// 초기화.
		Monster m = null;
		// 레벨별 소환할 몬스터 추출.
		SummonList sl = SummonListDatabase.summon(cha, summon_auction);
		
		if (sl != null)
			m = sl.getMonster();	

		Summon s = find(cha);

		if(s!=null && m!=null){
			// 이미 거느리고 있는 객체가 1개 이상이라면 무시.
			if(s.getSize() > 0)
				return;			
			//
			while( isAppend(sl, cha, TYPE.MONSTER) ){
				int loc = 1;
				int lx = Util.random(cha.getX() - loc, cha.getX() + loc);
				int ly = Util.random(cha.getY() - loc, cha.getY() + loc);
				int roop_cnt = 0;
				count++;
				
				if (sl != null && sl.getMaxCount() > 0 && sl.getMaxCount() < count)
					break;
				
				SummonInstance si = SummonInstance.clone(getSummonPool(), m, time);
				s.append(si);
				// 기본 정보 처리.
				si.setGfx(m.getGfx());
				si.setGfxMode(m.getGfxMode());
				si.setClassGfx(m.getGfx());
				si.setClassGfxMode(m.getGfxMode());
				si.setName(m.getNameId());
				si.setLevel(m.getLevel());
				si.setExp(m.getExp());
				si.setObjectId(ServerDatabase.nextEtcObjId());
				si.setMaxHp(m.getHp());
				si.setMaxMp(m.getMp());
				si.setNowHp(m.getHp());
				si.setNowMp(m.getMp());
				si.setLawful(m.getLawful());
				
				// 인위적으로 변경할 정보 처리.
				if(sl != null){
					if (sl.getSummonLv() > 0)
						si.setLevel(sl.getSummonLv());
						
					if(sl.getSummonHp() > 0){
						si.setMaxHp(sl.getSummonHp());
						si.setNowHp(sl.getSummonHp());
					}
					
					if(sl.getSummonMp() > 0){
						si.setMaxMp(sl.getSummonMp());
						si.setNowMp(sl.getSummonMp());
					}
					
					if(sl.getSummonLv() > 0)
						si.setLevel(sl.getSummonLv());
					if(sl.getSummonStr() > 0)
						si.setStr(sl.getSummonStr());
					if(sl.getSummonDex() > 0)
						si.setDex(sl.getSummonDex());
					if(sl.getSummonCon() > 0)
						si.setCon(sl.getSummonCon());
					if(sl.getSummonWis() > 0)
						si.setWis(sl.getSummonWis());
					if(sl.getSummonCha() > 0)
						si.setCha(sl.getSummonCha());
					if(sl.getSummonInt() > 0)
						si.setInt(sl.getSummonInt());
				}
				
				si.setStr(si.getLevel() / 2 < 25 ? 25 : si.getLevel() / 2);
				si.setDex(si.getLevel() / 2 < 25 ? 25 : si.getLevel() / 2);
				si.setInt(si.getLevel() / 2 < 25 ? 25 : si.getLevel() / 2);
				
				// 좌표 스폰
					while(
							!World.isThroughObject(lx, ly+1, cha.getMap(), 0) || 
							!World.isThroughObject(lx, ly-1, cha.getMap(), 4) || 
							!World.isThroughObject(lx-1, ly, cha.getMap(), 2) || 
							!World.isThroughObject(lx+1, ly, cha.getMap(), 6) ||
							!World.isThroughObject(lx-1, ly+1, cha.getMap(), 1) || 
							!World.isThroughObject(lx+1, ly-1, cha.getMap(), 5) || 
							!World.isThroughObject(lx+1, ly+1, cha.getMap(), 7) || 
							!World.isThroughObject(lx-1, ly-1, cha.getMap(), 3)
						){
						lx = Util.random(lx - loc, lx + loc);
						ly = Util.random(ly - loc, ly + loc);

						if (roop_cnt++ > 100) {
							lx = cha.getX();
							ly = cha.getY();
							break;
						}
					}

				// 스폰 처리.
				si.toTeleport(lx, ly, cha.getMap(), false);
				// 이팩트
				si.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), si, 6132), true);
				// 인공지능 등록.
				AiThread.append(si);
			}
		}
	}

	/**
	 * 펫길들이기 뒷처리 함수.
	 * @param o
	 * @param mi
	 */
	static public boolean toPet(object o, MonsterInstance mi) {
		Summon s = find(o);
		if(s!=null && mi!=null && o instanceof Character && isAppend(null, (Character)o, TYPE.PET)){
			// mi객체 스폰상태로 변경.
			mi.toAiClean(true);
			// 펫객체 생성
			PetInstance pet = PetInstance.clone(getPetPool(), mi.getMonster());
			pet.setGfx(mi.getMonster().getGfx());
			pet.setGfxMode(mi.getMonster().getGfxMode());
			pet.setClassGfx(mi.getMonster().getGfx());
			pet.setClassGfxMode(mi.getMonster().getGfxMode());
			pet.setName(mi.getMonster().getNameId());
			pet.setObjectId(ServerDatabase.nextItemObjId());
			pet.setHeading(mi.getHeading());
			pet.setMaxHp(mi.getMaxHp());
			pet.setMaxMp(mi.getMaxMp());
			pet.setNowHp(mi.getNowHp());
			pet.setNowMp(mi.getNowMp());
			pet.setLawful(mi.getLawful());
			// 레벨과 경험치 설정
			Exp exp = ExpDatabase.find(mi.getLevel());
			pet.setLevel(exp.getLevel());
			pet.setExp( (exp.getBonus()-exp.getExp())+1 );
			// 서먼관리 목록에 등록.
			s.append(pet);
			// 스폰
			pet.toTeleport(mi.getX(), mi.getY(), mi.getMap(), false);
			// 펫목걸이 생성
			DogCollar dc = (DogCollar)ItemDatabase.newInstance( ItemDatabase.find("펫 목걸이") );
			dc.setObjectId(ServerDatabase.nextItemObjId());
			dc.setDefinite(true);
			dc.setPetSpawn(true);
			dc.toUpdate(pet);
			o.getInventory().append(dc, true);
			// 디비 등록.
			insertPet(pet);

			AiThread.append(pet);
			return true;
		}
		return false;
	}

	/**
	 * 펫 목걸이를통해 펫을 생성하는 함수.
	 *  : PetMasterInstance 에 toGetFinal 함수가 호출해서 사용중.
	 * @param o
	 * @param dc
	 * @return
	 */
	static public boolean toPet(object o, DogCollar dc) {
		Summon s = find(o);
		boolean isGM = (o instanceof PcInstance) && ((PcInstance)o).getGm() > 0;

		if (s == null || dc == null || dc.isPetSpawn() || !(o instanceof Character)) {
			return false;
		}

		boolean canAppend = isAppend(null, (Character)o, TYPE.PET);
		if (!isGM && !canAppend) {
			return false;
		}

		PetInstance pet = PetInstance.clone(getPetPool(), MonsterDatabase.find(dc.getPetClassId()));
		if (pet == null) {
			return false;
		}

		pet.setObjectId(dc.getPetObjectId());
		pet.setGfx(pet.getMonster().getGfx());
		pet.setGfxMode(pet.getMonster().getGfxMode());
		pet.setClassGfx(pet.getMonster().getGfx());
		pet.setClassGfxMode(pet.getMonster().getGfxMode());
		pet.setPetWeapon(pet.getPetWeapon());
		pet.setPetArmor(pet.getPetArmor());

		selectPet(pet);
		if (pet.getName() == null) {
			setPetPool(pet);
			return false;
		}

		s.append(pet);
		pet.toTeleport(o.getX(), o.getY(), o.getMap(), false);
		dc.setPetSpawn(true);
		pet.setCollar(dc);
		AiThread.append(pet);

		return true;
	}


	/**
	 * 펫을 다시 몬스터로 되돌리는 함수.
	 * @param pet 변환할 펫 인스턴스
	 * @return 변환 성공 여부
	 */
	static public boolean toMonster(PetInstance pet) {
	    if (pet == null) {
	        return false; // 잘못된 펫 객체라면 변환 불가
	    }

	    // 기존 펫 정보 기반으로 몬스터 데이터 가져오기
	    Monster monsterData = MonsterDatabase.find(pet.getMonster().getNameIdNumber());

	    if (monsterData == null) {
	        return false; // 몬스터 데이터가 없으면 변환 불가
	    }

	    // MonsterInstance 직접 생성
	    MonsterInstance monster = new MonsterInstance();
	    monster.setMonster(monsterData); // 몬스터 기본 데이터 설정
	    monster.setObjectId(ServerDatabase.nextItemObjId()); // 새로운 오브젝트 ID 할당
	    monster.setGfx(monsterData.getGfx());
	    monster.setGfxMode(monsterData.getGfxMode());
	    monster.setClassGfx(monsterData.getGfx());
	    monster.setClassGfxMode(monsterData.getGfxMode());
	    monster.setName(monsterData.getNameId());
	    monster.setHeading(pet.getHeading());
	    monster.setMaxHp(pet.getMaxHp());
	    monster.setMaxMp(pet.getMaxMp());
	    monster.setNowHp(pet.getNowHp());
	    monster.setNowMp(pet.getNowMp());
	    monster.setLawful(pet.getLawful());
	    monster.setLevel(pet.getLevel());

	    // 몬스터를 펫이 있던 위치에 소환
	    monster.toTeleport(pet.getX(), pet.getY(), pet.getMap(), false);

	    // 기존 펫 제거
	    World.remove(pet); // 월드에서 제거
	    pet.clearList(true);

	    return true;
	}
	
	/**
	 * 펫 및 소환수를 더 거느릴수 있는지 확인해주는 메서드.
	 *  : 카리 계산.
	 */
	static public boolean isAppend(SummonList sl, Character cha, TYPE type){
		//
		Object o = PluginController.init(SummonController.class, "isAppend", sl, cha, type);
		if(o != null)
			return (Boolean)o;
		//
		int max = 0;
		int count = 0;

		// 엘리멘탈 소환일경우 1마리만 소환할 수 있음.
		if(type == TYPE.ELEMENTAL){
			Summon s = find(cha);
			return s!=null && s.getElementalSize()<1;	
		}

		if(type == TYPE.SOLDIER) {
			if (sl != null && sl.getNeedCha() > 0)
				max =Util.random(2, 8);
			
		}
		// 1.70버전 이하에서는 2~8마리 랜덤으로 나옴 (펫이 아닐대만.)
		if( Lineage.server_version<170 && (type!=TYPE.PET && type!=TYPE.SOLDIER) ){
			if(sl!=null && sl.getNeedCha()>0)
				max = Math.round( cha.getTotalCha()/sl.getNeedCha() );
			else
				max = type==TYPE.TAME ? Math.round( cha.getTotalCha()/6 ) : Util.random(2, 8);

		}else{
			// 카리수치에따른 최대값.
	
				if(sl!=null && sl.getNeedCha()>0)
					max = Math.round( cha.getTotalCha()/sl.getNeedCha() );
				else {
					if (sl != null && sl.getNeedCha() < 1 && sl.getMaxCount() > 0) {
						max = sl.getMaxCount();
					} else {
						max = Math.round( cha.getTotalCha()/6 );
						
						// 클래스별 최소 펫 가능 수.
						switch (cha.getClassType()) {
						case Lineage.LINEAGE_CLASS_ROYAL:
						case Lineage.LINEAGE_CLASS_KNIGHT:
							if (max < 2)
								max = 2;
							break;
						case Lineage.LINEAGE_CLASS_ELF:
						case Lineage.LINEAGE_CLASS_WIZARD:
							if (max < 3)
								max = 3;
							break;
						}
					}			
				}
			
		}

		Summon s = find(cha);
		
		if(s!=null)
			count = max - s.getSize();
		else
			count = max;

		return count > 0;
	}

	/**
	 * o 객체가 target 으로부터 데미지를 입으면 호출되는 함수.
	 *  : DamageController.toDamage에서 호출 함.
	 * @param o
	 * @param target
	 * @param dmg
	 */
	static public void toDamage(object o, object target, int dmg){
		if(o==null || dmg<=0)
			return;

		Summon s = find(o);
		
		if(s!=null && dmg>0)
			s.toDamage(target, dmg);
	}

	/**
	 * 강제로 특정 타켓을 공격하도록 하게할때 요청됨.
	 * @param o
	 * @param pet
	 * @param target
	 * @param type
	 */
	static public void toTargetSelect(object o, int pet_id, int target_id, int type){
		Summon s = find(o);
		if(s==null)
			return;

		object pet = s.find(pet_id);
		object target = o.findInsideList(target_id);
		if(pet==null || target==null)
			return;

		// 타켓 변경 상태로 변경. (그래야 공격목록 제거됨.)
		((SummonInstance)pet).setSummonMode(SUMMON_MODE.TargetSelect);
		// 공격태세로 변경.
		((SummonInstance)pet).setSummonMode(SUMMON_MODE.AggressiveMode);
		// 공격할수 있도록 공격목록에 추가.
		((SummonInstance)pet).addAttackList(target);
	}

	/**
	 * 펫 정보 디비에 등록하는 함수.
	 * @param pet
	 */
	static public void insertPet(PetInstance pet){
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO characters_pet SET objid=?, name=?, classId=?, level=?, nowHp=?, maxHp=?, nowMp=?, maxMp=?, exp=?, lawful=?, gfx=?, food_mode=?, del='false',weapon=?,armor=?");
			st.setLong(1, pet.getObjectId());
			st.setString(2, pet.getName());
			st.setInt(3, pet.getMonster().getNameIdNumber());
			st.setInt(4, pet.getLevel());
			st.setInt(5, pet.getNowHp());
			st.setInt(6, pet.getMaxHp());
			st.setInt(7, pet.getNowMp());
			st.setInt(8, pet.getMaxMp());
			st.setDouble(9, pet.getExp());
			st.setInt(10, pet.getLawful());
			st.setInt(11, pet.getGfx());
			switch(pet.getFoodMode()){
				case Veryhungry:
					st.setInt(12, 1);
					break;
				case Littlehungry:
					st.setInt(12, 2);
					break;
				case NeitherHungryNorFull:
					st.setInt(12, 3);
					break;
				case LittleFull:
					st.setInt(12, 4);
					break;
				case VeryFull:
					st.setInt(12, 5);
					break;
			}
			//쿠베라 펫장비
			st.setString(13, "");
			st.setString(14, "");
			st.executeUpdate();
		} catch(Exception e) {
			lineage.share.System.println(SummonController.class.toString()+" : void insertPet(PetInstance pet)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	/**
	 * 펫이 제거되었음을 알리기 위한 함수.
	 * DB의 del 칼럼을 true로 변경하고, 이후 toWorldJoin을 호출하여
	 * 목걸이 등의 상태를 업데이트 합니다.
	 * @param pet 펫 인스턴스
	 */
	static public void deletePet(PetInstance pet){
	    Connection con = null;
	    PreparedStatement st = null;
	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("UPDATE characters_pet SET del='true' WHERE objid=?");
	        st.setLong(1, pet.getObjectId());
	        st.executeUpdate();
	    } catch(Exception e) {
	        lineage.share.System.println(SummonController.class.toString()+" : void deletePet(PetInstance pet)");
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st);
	    }
	    
	    // DB 업데이트 후, 펫의 주인(PcInstance)을 가져와 해당 인벤토리에서 연결된 목걸이를 찾아 toWorldJoin() 호출
	    // getMaster() 메서드를 사용하여 주인 객체를 얻습니다.
	    object masterObj = pet.getSummon().getMaster();// pet 객체에 getMaster()가 구현되어 있어야 합니다.
	    if(masterObj != null && masterObj instanceof PcInstance) {
	        PcInstance pc = (PcInstance) masterObj;
	        // 인벤토리에서 해당 펫과 연결된 DogCollar를 찾습니다.
	        for(ItemInstance ii : pc.getInventory().getList()){
	            if(ii instanceof DogCollar){
	                DogCollar dc = (DogCollar) ii;
	                if(dc.getPetObjectId() == pet.getObjectId()){
	                    Connection con2 = null;
	                    try {
	                        con2 = DatabaseConnection.getLineage();
	                        // toWorldJoin 내부에서 DB 정보를 재로딩하여, del 칼럼이 true이면 목걸이를 제거합니다.
	                        dc.toWorldJoin(con2, pc);
	                    } catch(Exception e) {
	                        lineage.share.System.println("deletePet: toWorldJoin 호출 중 에러 발생");
	                        lineage.share.System.println(e);
	                    } finally {
	                        DatabaseConnection.close(con2);
	                    }
	                }
	            }
	        }
	    }
	    

	}

	/**
	 * 관리목록에서 펫만 추출하여 디비에 업데이트하는 함수.
	 * @param o
	 */
	static public void toSave(Connection con, object o){
		Summon s = find(o);
		if(s != null){
			for(object oo : s.getList()){
				if(oo instanceof PetInstance)
					updatePet(con, (PetInstance)oo);
			}
		}
	}

	/**
	 * 소환객체 이름 변경처리 함수.
	 * @param si
	 * @param name
	 */
	static public void updateName(SummonInstance si, String name){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_pet WHERE name=?");
			st.setString(1, name);
			rs = st.executeQuery();
			if(!rs.next()){
				// 이름 없을경우 처리.
				si.setName(name);
				if(si instanceof PetInstance)
					SummonController.updatePet(con, (PetInstance)si);
				si.getSummon().getMaster().toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), si));
				si.getSummon().getMaster().toSender(S_HtmlSummon.clone(BasePacketPooling.getPool(S_HtmlSummon.class), si));

			}else{
				// 이름이 존재한다면
				si.getSummon().getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 327));
			}
		}catch (Exception e){
		}finally{
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 펫정보 디비에 갱신하는 함수.
	 * @param con 데이터베이스 연결 객체
	 * @param pet 갱신할 펫 정보
	 */
	static public void updatePet(Connection con, PetInstance pet) {
	    PreparedStatement st = null;
	    try {
	        st = con.prepareStatement("UPDATE characters_pet SET level=?, nowHp=?, maxHp=?, nowMp=?, maxMp=?, exp=?, lawful=?, food_mode=?, name=?, classId=?, gfx=? WHERE objid=?");

	        st.setInt(1, pet.getLevel());

	        // 펫이 사망한 경우 nowHp를 maxHp로 설정
	        st.setInt(2, pet.isDead() ? pet.getMaxHp() : pet.getNowHp());

	        st.setInt(3, pet.getMaxHp());
	        st.setInt(4, pet.getNowMp());
	        st.setInt(5, pet.getMaxMp());
	        st.setDouble(6, pet.getExp());
	        st.setInt(7, pet.getLawful());

	        int foodModeValue = 3; // 기본값 (NeitherHungryNorFull)
	        switch (pet.getFoodMode()) {
	            case Veryhungry:
	                foodModeValue = 1;
	                break;
	            case Littlehungry:
	                foodModeValue = 2;
	                break;
	            case NeitherHungryNorFull:
	                foodModeValue = 3;
	                break;
	            case LittleFull:
	                foodModeValue = 4;
	                break;
	            case VeryFull:
	                foodModeValue = 5;
	                break;
	        }
	        st.setInt(8, foodModeValue);

	        st.setString(9, pet.getName());
	        st.setInt(10, pet.getMonster().getNameIdNumber());
	        st.setInt(11, pet.getGfx());
	        st.setLong(12, pet.getObjectId());

	        st.executeUpdate();
	    } catch (Exception e) {
	        lineage.share.System.println("updatePet() 오류 발생: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        DatabaseConnection.close(st);
	    }
	}

	
	//쿠베라 펫 장비 작업
	static public void updatePetWeapon(Connection con, PetInstance pet){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE characters_pet SET weapon=? WHERE objid=?");
			
			st.setString(1, pet.getPetWeapon());

			st.setLong(2, pet.getObjectId());
			st.executeUpdate();
		} catch(Exception e) {
			lineage.share.System.println(SummonController.class.toString()+" : void toSave(object o)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	static public void updateArmor(Connection con, PetInstance pet){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE characters_pet SET armor=? WHERE objid=?");
			
			st.setString(1, pet.getPetArmor());

			st.setLong(2, pet.getObjectId());
			st.executeUpdate();
		} catch(Exception e) {
			lineage.share.System.println(SummonController.class.toString()+" : void toSave(object o)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public  String selectPetWeapon(PetInstance pet) {
		Connection con = null;
		PreparedStatement st = null;

		ResultSet rs = null;
		String weapon = null;
		try {
			con = DatabaseConnection.getLineage();
	
			st = con.prepareStatement("select weapon from characters_pet where objid=? ");
			st.setLong(1, pet.getObjectId());
			rs = st.executeQuery();
			if(rs.next())
				weapon = rs.getString(1);
		} catch (Exception e) {
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st,rs);
		}
		return weapon;
	}
	static public  String selectPetArmor(PetInstance pet) {
		Connection con = null;
		PreparedStatement st = null;

		ResultSet rs = null;
		String weapon = null;
		try {
			con = DatabaseConnection.getLineage();
	
			st = con.prepareStatement("select armor from characters_pet where objid=? ");
			st.setLong(1, pet.getObjectId());
			rs = st.executeQuery();
			if(rs.next())
				weapon = rs.getString(1);
		} catch (Exception e) {
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st,rs);
		}
		return weapon;
	}
	static public int getNoticeUid(int uid){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT notice_uid FROM accounts WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getNoticeUid(int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
	/**
	 * pet과 연결된 디비 정보 추출해서 갱신.
	 * @param pet
	 */
	//쿠베라 펫장비
	static private void selectPet(PetInstance pet){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_pet WHERE objid=?");
			st.setLong(1, pet.getObjectId());
			rs = st.executeQuery();
			if(rs.next()){
				pet.setName(rs.getString("name"));
				pet.setLevel(rs.getInt("level"));
				pet.setMaxHp(rs.getInt("maxHp"));
				pet.setNowHp(rs.getInt("nowHp"));
				pet.setMaxMp(rs.getInt("maxMp"));
				pet.setNowMp(rs.getInt("nowMp"));
				pet.setExp(rs.getDouble("exp"));
				pet.setLawful(rs.getInt("lawful"));
				pet.setGfx(rs.getInt("gfx"));
				switch(rs.getInt("food_mode")){
					case 1:
						pet.setFoodMode(PetInstance.PET_FOOD_MODE.Veryhungry);
						break;
					case 2:
						pet.setFoodMode(PetInstance.PET_FOOD_MODE.Littlehungry);
						break;
					case 3:
						pet.setFoodMode(PetInstance.PET_FOOD_MODE.NeitherHungryNorFull);
						break;
					case 4:
						pet.setFoodMode(PetInstance.PET_FOOD_MODE.LittleFull);
						break;
					case 5:
						pet.setFoodMode(PetInstance.PET_FOOD_MODE.VeryFull);
						break;
				}
				pet.setPetWeapon(rs.getString("weapon"));
				pet.setPetArmor(rs.getString("armor"));
			}
		} catch(Exception e) {
			lineage.share.System.println(SummonController.class.toString()+" : void deletePet(PetInstance pet)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	public static void toPush(PcInstance pc, long petObjId){
		Summon s = SummonController.find(pc);
		if(s != null){
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				// 모든 펫 저장.
				toSave(con, pc, petObjId);
				// 모든 펫 제거 하면서 펫목걸이도 갱신.
				s.removePet(petObjId);
			} catch (Exception e) {
				lineage.share.System.println(SummonController.class.toString()+" : toPush(PcInstance pc, long petObjId)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}
	static public void toSave(Connection con, object o, long petObjId){
		Summon s = find(o);
		if(s != null){
			for(object oo : s.getList()){
				if(oo instanceof PetInstance && oo.getObjectId() == petObjId) {
					updatePet(con, (PetInstance)oo);
					break;
				}
			}
		}
	}
	/**
	 * 해당 펫객체가 디비에서 제거된 상태인지 확인해주는 함수.
	 * @param object_id
	 * @return
	 */
	static public boolean isDeletePet(long object_id){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_pet WHERE objid=? AND del='true'");
			st.setLong(1, object_id);
			rs = st.executeQuery();
			return rs.next();
		} catch(Exception e) {
			lineage.share.System.println(SummonController.class.toString()+" : void deletePet(PetInstance pet)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}
	
	static public int getPoolSize(){
		return pool.size();
	}

	static public int getSummonPoolSize(){
		return summon_pool.size();
	}

	static public int getPetPoolSize(){
		return pet_pool.size();
	}

	static public int getSoldierPoolSize(){
		return soldier_pool.size();
	}
}

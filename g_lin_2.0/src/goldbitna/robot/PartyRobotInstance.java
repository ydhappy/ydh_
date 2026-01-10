package goldbitna.robot;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import goldbitna.AttackController;
import goldbitna.RobotSpawnLocation;
import goldbitna.robot.PartyRobotInstance.PCROBOT_MODE;
import lineage.bean.database.Item;
import lineage.bean.database.Poly;
import lineage.bean.database.RobotPoly;
import lineage.bean.database.Skill;
import lineage.bean.database.SkillRobot;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Party;
import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.network.packet.server.S_ObjectMoving;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.LocationController;
import lineage.world.controller.PartyController;
import lineage.world.controller.RobotController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.EventInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MagicDollInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.PetMasterInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.instance.TeleportInstance;
import lineage.world.object.item.all_night.Buff_potion;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.scroll.ScrollPolymorph;
import lineage.world.object.item.weapon.Arrow;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.DecreaseWeight;
import lineage.world.object.magic.Detection;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.HastePotionMagic;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Wafer;
import lineage.world.object.monster.Doppelganger;
import lineage.world.object.monster.Harphy;
import lineage.world.object.monster.Spartoi;
import lineage.world.object.monster.StoneGolem;
import lineage.world.object.npc.guard.PatrolGuard;
import lineage.world.object.npc.kingdom.KingdomCrown;
import lineage.world.object.npc.kingdom.KingdomDoor;

public class PartyRobotInstance extends RobotInstance {

    protected static final int ADEN_LIMIT          = 5000000;    // 아데나 체크할 최소값 및 추가될 아데나 갯수.

    protected static final int HEALING_PERCENT     = 95;            // 체력 회복제를 복용할 시점 백분율
    protected static final int GOTOHOME_PERCENT    = 30;            // 체력이 해당퍼센트값보다 작으면 귀환함.
    protected static final int USABLE_MP_PERCENT   = 10;            // 해당 마나량이 해당 값보다 클때만 마법 사용
    // ✅ 예약 유지 시간
    private static final long RESERVE_TIMEOUT = 60_000; // 60초
    // ✅ 예약 좌표 맵: "x_y_map" → 예약 시간 (ms)
    private static final Map<String, Long> RESERVED_COORDS = new ConcurrentHashMap<>();
    // ✅ 좌표 등록 맵: 지역 이름 → 좌표 리스트
    private static final Map<String, List<RobotSpawnLocation>> SPAWN_LOCATIONS = new HashMap<>();
    static {
        SPAWN_LOCATIONS.put("말하는섬", Arrays.asList(
            new RobotSpawnLocation(32578, 32945, 0),
            new RobotSpawnLocation(32587, 32931, 0),
            new RobotSpawnLocation(32600, 32919, 0),
            new RobotSpawnLocation(32581, 32919, 0),
            new RobotSpawnLocation(32563, 32949, 0),
            new RobotSpawnLocation(32585, 32947, 0)
        ));

        SPAWN_LOCATIONS.put("글루딘", Arrays.asList(
            new RobotSpawnLocation(32612, 32808, 4),
            new RobotSpawnLocation(32618, 32799, 4),
            new RobotSpawnLocation(32633, 32810, 4),
            new RobotSpawnLocation(32635, 32823, 4),
            new RobotSpawnLocation(32620, 32767, 4),
            new RobotSpawnLocation(32603, 32756, 4)
        ));

        SPAWN_LOCATIONS.put("켄트", Arrays.asList(
            new RobotSpawnLocation(33076, 32792, 4),
            new RobotSpawnLocation(33051, 32786, 4),
            new RobotSpawnLocation(33069, 32800, 4),
            new RobotSpawnLocation(33053, 32810, 4),
            new RobotSpawnLocation(33068, 32762, 4),
            new RobotSpawnLocation(33051, 32754, 4)
        ));

        SPAWN_LOCATIONS.put("우드벡", Arrays.asList(
            new RobotSpawnLocation(32613, 33187, 4),
            new RobotSpawnLocation(32608, 33169, 4),
            new RobotSpawnLocation(32635, 33173, 4),
            new RobotSpawnLocation(32622, 33193, 4),
            new RobotSpawnLocation(32613, 33229, 4),
            new RobotSpawnLocation(32650, 33203, 4)
        ));

        SPAWN_LOCATIONS.put("은기사", Arrays.asList(
            new RobotSpawnLocation(33073, 33400, 4),
            new RobotSpawnLocation(33077, 33381, 4),
            new RobotSpawnLocation(33101, 33371, 4),
            new RobotSpawnLocation(33093, 33401, 4),
            new RobotSpawnLocation(33112, 33385, 4),
            new RobotSpawnLocation(33117, 33364, 4)
        ));

        SPAWN_LOCATIONS.put("화전민", Arrays.asList(
            new RobotSpawnLocation(32753, 32444, 4),
            new RobotSpawnLocation(32742, 32430, 4),
            new RobotSpawnLocation(32741, 32441, 4),
            new RobotSpawnLocation(32736, 32459, 4),
            new RobotSpawnLocation(32745, 32474, 4),
            new RobotSpawnLocation(32764, 32455, 4)
        ));

        SPAWN_LOCATIONS.put("기란", Arrays.asList(
            new RobotSpawnLocation(33449, 32817, 4),
            new RobotSpawnLocation(33419, 32820, 4),
            new RobotSpawnLocation(33449, 32801, 4),
            new RobotSpawnLocation(33427, 32793, 4),
            new RobotSpawnLocation(33412, 32793, 4),
            new RobotSpawnLocation(33473, 32777, 4)
        ));

        SPAWN_LOCATIONS.put("하이네", Arrays.asList(
            new RobotSpawnLocation(33595, 33244, 4),
            new RobotSpawnLocation(33624, 33245, 4),
            new RobotSpawnLocation(33610, 33223, 4),
            new RobotSpawnLocation(33575, 33238, 4),
            new RobotSpawnLocation(33584, 33261, 4),
            new RobotSpawnLocation(33592, 33279, 4)
        ));

        SPAWN_LOCATIONS.put("오렌", Arrays.asList(
            new RobotSpawnLocation(34062, 32276, 4),
            new RobotSpawnLocation(34063, 32295, 4),
            new RobotSpawnLocation(34071, 32303, 4),
            new RobotSpawnLocation(34073, 32266, 4),
            new RobotSpawnLocation(34063, 32261, 4),
            new RobotSpawnLocation(34033, 32251, 4)
        ));

        SPAWN_LOCATIONS.put("웰던", Arrays.asList(
            new RobotSpawnLocation(33712, 32488, 4),
            new RobotSpawnLocation(33704, 32511, 4),
            new RobotSpawnLocation(33745, 32496, 4),
            new RobotSpawnLocation(33733, 32479, 4),
            new RobotSpawnLocation(33702, 32494, 4),
            new RobotSpawnLocation(33714, 32496, 4)
        ));
    }
    
    protected static enum PCROBOT_MODE {
        None,           // 기본값
        HealingPotion,  // 물약상점 이동.
        HastePotion,    // 초록물약 상점 이동.
        BraveryPotion,  // 용기물약 상점 이동.
        ScrollPolymorph,// 변신주문서 상점 이동.
        Arrow,          // 화살 상점 이동.
        InventoryHeavy, // 마을로 이동.
        ElvenWafer,     // 엘븐와퍼 상점 이동.
        Polymorph,      // 변신하기위해 마을로 이동.
        Stay,           // 휴식 모드.
        Cracker,        // 허수아비 모드.
    }
    	
    private AStar aStar;           	 	 // 길찾기 변수
    private Node tail;             	 	 // 길찾기 변수
    private int[] iPath;           	 	 // 길찾기 변수
    private List<object> astarList;  	 // A* 경로 무시할 객체 목록.
    private List<object> temp_list;  	 // 주변 셀 검색 임시 저장용
    protected Item weapon;
    protected int weaponEn;         	 // 무기 인첸
    private String weapon_name;
    private String place_name;
    public PCROBOT_MODE pcrobot_mode; 	 // 처리 모드.
    private int step;               	 // 일렬 동작 처리 시 스탭 변수.
	public volatile object target;  	 // 공격 대상  
	public volatile object tempTarget;   // 임시 대상
	public volatile object attacker;  	 // 공격자  
	private object currentAttackTarget;  // 현재 전투 중인 타겟 저장
	
    // 동기화를 위한 객체
    private Object sync_ai = new Object();
        
    // 시체 유지 및 재스폰 관련 변수
    private long ai_time_temp_1;
	private long polyTime;
    private long delayTime;
    public long teleportTime;
    // ✅ 타겟이 설정된 시점을 저장하는 변수
    private long targetSetTime = 0;
   
    // 로봇 행동 상태 변수
    public String action;    
    
    // ✅ 파티 상태를 저장하는 변수 추가
    private boolean inParty;  // true: 파티 중, false: 파티 중이 아님
    
	protected boolean mythicPoly;
	protected boolean randomPoly;
	private int adena = 0;
	
    // 리로드 확인용.
    public boolean isReload;     
    
    public PartyRobotInstance() {
        aStar = new AStar();
        iPath = new int[2];
        astarList = new ArrayList<object>();
        temp_list = new ArrayList<object>();
        target = tempTarget = attacker = currentAttackTarget = null;
    }
    
    @Override
    public void close() {
        super.close();
        if (getInventory() != null) {
            for (ItemInstance ii : getInventory().getList())
                ItemDatabase.setPool(ii);
            getInventory().clearList();
        }
        weapon_name = place_name = null;
        weapon = null;
        action = null;
        target = tempTarget = attacker = currentAttackTarget = null;
        teleportTime = delayTime = polyTime = ai_time_temp_1 = targetSetTime = weaponEn = step = 0;
        inParty = isReload = randomPoly = mythicPoly = false;
        
        if (Util.random(0, 99) < 10)
            pcrobot_mode = PCROBOT_MODE.Stay;
        else
            pcrobot_mode = PCROBOT_MODE.None;
        
        if (aStar != null)
            aStar.cleanTail();
        if (astarList != null)
            clearAstarList();
        if (temp_list != null)
            temp_list.clear();    
    }
    
    @Override
    public void toSave(Connection con) {
    }
	
    public boolean containsAstarList(object o) {
        synchronized (astarList) {
            return astarList.contains(o);
        }
    }
    
    private void appendAstarList(object o) {
        synchronized (astarList) {
            if (!astarList.contains(o))
                astarList.add(o);
        }
    }
    
    private void removeAstarList(object o) {
        synchronized (astarList) {
            astarList.remove(o);
        }
    }
    
    private void clearAstarList() {
        synchronized (astarList) {
            astarList.clear();
        }
    }
    
    public int getWeaponEn() {
        return weaponEn;
    }
    
    public void setWeaponEn(int weaponEn) {
        this.weaponEn = weaponEn;
    }
    
    public String getWeapon_name() {
        return weapon_name;
    }
    
    public void setWeapon_name(String weapon_name) {
        this.weapon_name = weapon_name;
    }
    
    public String getPlaceName() {
        return place_name ;
    }
    
    public void setPlaceName (String place_name ) {
        this.place_name  = place_name ;
    }

    /**
     * ✅ 사용 가능한 스폰 좌표 중 랜덤으로 하나 반환 (중복/점유 좌표 제외)
     */
    public static RobotSpawnLocation getSpawnLocation(String placeName) {
        List<RobotSpawnLocation> list = SPAWN_LOCATIONS.get(placeName);
        if (list == null || list.isEmpty()) return null;

        List<RobotSpawnLocation> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled); // 랜덤화

        for (RobotSpawnLocation loc : shuffled) {
            String key = loc.getX() + "_" + loc.getY() + "_" + loc.getMapId();

            // ✅ 동기화로 안전하게 예약 확인 및 추가
            synchronized (RESERVED_COORDS) {
                if (RESERVED_COORDS.containsKey(key)) continue; // 이미 예약된 좌표

                if (!World.isMapdynamic(loc.getX(), loc.getY(), loc.getMapId())) {
                    RESERVED_COORDS.put(key, System.currentTimeMillis()); // 예약 시간 기록
                    return loc;
                }
            }
        }
        return null; // 사용 가능한 좌표 없음
    }

    /**
     * ✅ 특정 지역의 전체 스폰 좌표 리스트 반환
     */
    public static List<RobotSpawnLocation> getSpawnLocationList(String placeName) {
        return SPAWN_LOCATIONS.getOrDefault(placeName, Collections.emptyList());
    }

    /**
     * ✅ 특정 좌표의 예약을 수동 해제
     */
    public static void releaseSpawnLocation(RobotSpawnLocation loc) {
        if (loc == null) return;
        String key = loc.getX() + "_" + loc.getY() + "_" + loc.getMapId();
        RESERVED_COORDS.remove(key);
    }

    /**
     * ✅ 예약된 좌표 중 일정 시간이 지난 좌표를 자동 해제
     * → 주기적으로 호출할 것
     */
    public static void cleanupExpiredReservations() {
        long now = System.currentTimeMillis();
        RESERVED_COORDS.entrySet().removeIf(entry -> (now - entry.getValue()) > RESERVE_TIMEOUT);
    }
    
    public synchronized object getTarget() {
        return attacker;
    }
  
    // ✅ 로봇 객체가 파티 중인지 확인하는 메서드
    public boolean isRobotInParty() {
        return inParty; // 파티 상태를 반환
    }

    // ✅ 파티 상태를 변경할 때마다 호출하여 업데이트
    public void updatePartyStatus(boolean status) {
        this.inParty = status; // 파티 상태를 업데이트
    }

    // ✅ 파티 상태 확인 메서드 (호출 용도)
    public boolean isInParty() {
        return inParty;
    }

    // ✅ 파티 상태 설정 메서드 (호출 용도)
    public void setInParty(boolean inParty) {
        this.inParty = inParty;
    }
    
	public boolean getMythicPoly() {
	    return mythicPoly;
	}

	public void setMythicPoly(boolean mythicPoly) {
	    this.mythicPoly = mythicPoly;
	}

	public boolean getRandomPoly() {
	    return randomPoly;
	}

	public void setRandomPoly(boolean randomPoly) {
	    this.randomPoly = randomPoly;
	}
	
	public int getAdena() {
	    return adena;
	}

	public void setAdena(int adena) {
	    this.adena = adena;
	}
	
    /**
     * ✅ 타겟 설정 (설정된 시간 기록 추가)
     */
    public synchronized void setTarget(object newTarget) {
        if (newTarget != null && newTarget != this.target) {
            this.target = newTarget;
            this.targetSetTime = System.currentTimeMillis(); // ✅ 타겟이 설정된 시간 기록
        }
    }

	public void toWorldJoin(Connection con) {
		super.toWorldJoin();

		// 인공지능 상태 변경
		setAiStatus(Lineage.AI_STATUS_WALK);

		// 메모리 세팅
		World.appendRobot(this);

		// 컨트롤러 호출
		BookController.toWorldJoin(this);
		CharacterController.toWorldJoin(this);
		BuffController.toWorldJoin(this);
		SkillController.toWorldJoin(this);
		InventoryController.toWorldJoin(this);
	    RobotController.readSkill(con, this); 
		
		// 인벤토리 셋팅 (자동 무기 장착)
		setInventory();

		// AI 활성화
		AiThread.append(this);
	}

    
    @Override
    public void toWorldOut() {
        super.toWorldOut();
        setAiStatus(Lineage.AI_STATUS_DELETE);
        toReset(true);
        World.removeRobot(this);        
        BookController.toWorldOut(this);
        CharacterController.toWorldOut(this);
        BuffController.toWorldOut(this);
        SkillController.toWorldOut(this);
		InventoryController.toWorldOut(this);
        close();
    }
    
    @Override
    public void toRevival(object o) {
        if (isDead()) {
            super.toReset(false);            
            target = tempTarget = attacker = currentAttackTarget = null;
            clearAstarList();
            
            // ✅ 무조건 파티 해제 (로봇만 해당)
            if (this instanceof PartyRobotInstance) {
                PartyRobotInstance robot = (PartyRobotInstance) this;
                if (robot.getPartyId() > 0) {
                    PartyController.close(robot); // ➤ robot이 속한 파티 강제 해산
                }
            }
            
            int[] home = Lineage.getHomeXY();
            setHomeX(home[0]);
            setHomeY(home[1]);
            setHomeMap(home[2]);
                		
            toTeleport(getHomeX(), getHomeY(), getHomeMap(), isDead() == false);
            setDead(false);
            setNowHp(level);
            toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), o, this), false);
            ai_time_temp_1 = 0;
            setAiStatus(Lineage.AI_STATUS_WALK);
        }
    }
    
    @Override
    public void setDead(boolean dead) {
        super.setDead(dead);
        if (dead) {
            ai_time = 0;
            setAiStatus(Lineage.AI_STATUS_DEAD);
        }
    }
    
    /**
     * 공격에 따른 대미지 처리를 수행하는 메서드.
     *
     * @param cha  공격을 가한 캐릭터
     * @param dmg  입힌 대미지
     * @param type 대미지 타입
     * @param opt  추가 옵션
     */
	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		super.toDamage(cha, dmg, type); // 기본 대미지 처리

		if (!isExcludedMap(getMap()) && World.isSafetyZone(getX(), getY(), getMap())) {
			setHeading(Util.calcheading(this, cha.getX(), cha.getY()));
			toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		}
			
		// 🔒 유효성 검사
		if (cha == null || cha.getObjectId() == getObjectId() || dmg <= 0 || cha.getGm() > 0)
			return;
		
        // ✅ 타겟이 없을 경우에만 설정
        if (currentAttackTarget == null) {
            setTarget(cha);
        }
        
		removeAstarList(cha); // 경로 최적화
	}

    @Override
    public void toAiThreadDelete() {
        super.toAiThreadDelete();
        World.removeRobot(this);        
        BookController.toWorldOut(this);
        CharacterController.toWorldOut(this);
        BuffController.toWorldOut(this);
        SkillController.toWorldOut(this);
    }   
 
    @Override
    public void toAi(long time) {
        synchronized (sync_ai) {
            if (isReload) return;

            // 사망 처리
            if (isDead()) {
                if (ai_time_temp_1 == 0) ai_time_temp_1 = time;
                if (ai_time_temp_1 + Lineage.ai_robot_corpse_time > time) return;

                goToHome(false);
                toRevival(this);
            }

            // 마을 대기
            if ("마을 대기".equalsIgnoreCase(action)) {
                if (!World.isSafetyZone(getX(), getY(), getMap())) goToHome(false);
                return;
            }

            if (getInventory() == null) return;
        }

        // ✅ 무기 및 화살 장착, 아이템 지급, 무게 체크 등은 하나의 synchronized 블록으로 병합
        synchronized (this) {
            // 무기 착용 처리
            ItemInstance currentWeapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

            if (currentWeapon == null || !currentWeapon.getItem().getName().equalsIgnoreCase(this.getWeapon_name())) {
                // 인벤에서 찾기
                ItemInstance foundWeapon = getInventory().find(weapon);

                if (foundWeapon != null) {
                    foundWeapon.toClick(this, null);
                } else {
                    weapon = RobotController.getWeapon(getClassType());
                    if (weapon != null) {
                        ItemInstance item = ItemDatabase.newInstance(weapon);
                        if (item != null) {
                            item.setObjectId(ServerDatabase.nextEtcObjId());
                            item.setEnLevel(weaponEn);
                            getInventory().append(item, false);
                            item.toClick(this, null);
                        }
                    }
                }
            }

            // 화살 장착
            if (weapon != null && "bow".equalsIgnoreCase(weapon.getType2()))
                setArrow();

         // ✅ Java 8 호환 방식 + count() 호출 오류 수정
            Map<String, Integer> itemMap = new HashMap<>();
            itemMap.put("무한 체력 회복 룬", 1);
            itemMap.put("무한 신속 룬", 1);
            itemMap.put("무한 가속 룬", 1);
            itemMap.put("무한 변신 주문서", 1);
            itemMap.put("무한 신화 변신 북", 1);
            itemMap.put("무한의 화살통", 1);
            itemMap.put("무한 버프 물약", 1);

            for (Map.Entry<String, Integer> entry : itemMap.entrySet()) {
                String itemName = entry.getKey();
                int amount = entry.getValue();

                ItemInstance item = getInventory().find(itemName);
                if (item == null || item.getCount() <= 0) {
                    RobotController.giveItem(this, itemName, amount);
                }
            }

            // 무게 초과 처리
            if (pcrobot_mode == PCROBOT_MODE.None && !getInventory().isWeightPercent(82)) {
                pcrobot_mode = PCROBOT_MODE.InventoryHeavy;
            }

            // 변신 처리
            if (pcrobot_mode == PCROBOT_MODE.None && getGfx() == getClassGfx() && RobotController.isPoly(this) && isRobotInParty()) {
                pcrobot_mode = PCROBOT_MODE.Polymorph;
            }

            // 모드별 기본 재화 지급
            if (pcrobot_mode != PCROBOT_MODE.None && pcrobot_mode != PCROBOT_MODE.Cracker) {
                setAiStatus(Lineage.AI_STATUS_WALK);

                ItemInstance aden = getInventory().findAden();
                if (aden == null || aden.getCount() < ADEN_LIMIT) {
                    Item adenItem = ItemDatabase.find("아데나");
                    if (adenItem != null) {
                        aden = aden == null ? ItemDatabase.newInstance(adenItem) : aden;
                        aden.setObjectId(ServerDatabase.nextEtcObjId());
                        getInventory().append(aden, false);
                        aden.setCount(aden.getCount() + ADEN_LIMIT);
                    }
                }
            }
        }

        // ✅ 체력 회복
        if (getHpPercent() <= HEALING_PERCENT)
            toHealingPotion();

        // ✅ 귀환 조건
        if (!World.isSafetyZone(getX(), getY(), getMap()) && getHpPercent() <= GOTOHOME_PERCENT) {
            if ((getMap() == 4 && Util.random(0, 99) <= 60) || Util.random(0, 99) <= 10) {
                synchronized (this) {
                    pcrobot_mode = PCROBOT_MODE.Stay;
                }
                goToHome(false);
                ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
                return;
            } else if (Util.random(0, 99) <= 20) {
                synchronized (this) {
                    pcrobot_mode = PCROBOT_MODE.None;
                }
                return;
            }
        }

        // ✅ 파티 및 AI 상태 관리
        PartyController.checkParty(this);
        if (!isRobotInParty()) {
            moveToOriginalSpawnLocation();
        }

        synchronized (this) {
            switch (getAiStatus()) {
                case Lineage.AI_STATUS_WALK:
                    if (target != null) {
                        setAiStatus(Lineage.AI_STATUS_ATTACK);
                        currentAttackTarget = target;
                        target = null;
                    } 
                    // ✅ target은 null이지만 currentAttackTarget이 있을 경우도 공격 상태 진입
                    else if (currentAttackTarget != null) {
                        setAiStatus(Lineage.AI_STATUS_ATTACK);
                    }
                    break;

                case Lineage.AI_STATUS_ATTACK:
                    if (pcrobot_mode != PCROBOT_MODE.Cracker)
                        currentAttackTarget = checkTargetValidity(currentAttackTarget);

                    if (currentAttackTarget == null && pcrobot_mode != PCROBOT_MODE.Cracker)
                        setAiStatus(Lineage.AI_STATUS_WALK);
                    break;
            }
        }

        super.toAi(time); // ✅ 부모 로직 실행
    }

	@Override
	protected void toAiWalk(long time) {
		super.toAiWalk(time);

		// ✅ 현재 로봇 모드에 따라 처리
		PCROBOT_MODE mode;
		synchronized (this) {
			mode = pcrobot_mode;
		}

		switch (mode) {
		case InventoryHeavy:
			toInventoryHeavy();
			return;
		case Polymorph:
				toPolymorph();
			return;
		case Stay:
			toStay(time);
			return;
		}

		// ✅ 물약 복용: 크래커 및 대기 모드 제외
		if (mode != PCROBOT_MODE.Cracker && mode != PCROBOT_MODE.Stay && isRobotInParty()) {
			toHealingPotion();
			toBuffPotion();
			
			checkPartyBuffAndHeal();
		}

	    // ✅ 타겟이 없으면 탐색 시도
	    if (currentAttackTarget == null) {
	        findTarget();
	    }	           
        
	 // ✅ 파티 상태인 경우, 파티 마스터를 추적하여 이동 또는 텔레포트 실행
	    if (isRobotInParty()) {
	        Party p = PartyController.find(this); // 현재 로봇의 파티 찾기
	        if (p != null) {
	            PcInstance master = p.getMaster(); // 파티의 마스터 찾기
	            if (master != null) {
	                // 마스터를 따라 이동 시도를 함
	                if (moveToMaster(master)) { 
	                    return; 
	                }
	            }
	        }
	    }
        
        // ✅ 경로 초기화 (가끔 A* 경로 초기화)
        if (Util.random(0, 1) == 0) {
            clearAstarList();
        }
    }

	/**
	 * ✅ 파티 마스터를 따라 이동하는 메서드 
	 * @param master - 파티의 마스터 (PcInstance)
	 * @return boolean - 이동 성공 여부
	 */
	private boolean moveToMaster(PcInstance master) {
	    if (master == null || master.isDead()) {
	        return false; // 마스터가 존재하지 않거나 사망 상태면 이동하지 않음
	    }

	    int masterX = master.getX();
	    int masterY = master.getY();
	    int masterMap = master.getMap();

	    // 최대 이동 실패 횟수 제한
	    int maxMoveAttempts = 1;
	    int moveAttempts = 0;

	    // 파티 마스터와의 거리가 멀면 이동 시작
	    if (!Util.isDistance(this, master, Lineage.robot_auto_party_location)) {
	        while (moveAttempts < maxMoveAttempts) {
	            // ✅ 헤딩 계산
	            int heading = Util.calcheading(this.x, this.y, masterX, masterY);
	            setHeading(heading);

	            // ✅ 1. 직선 이동 시도
	            if (isMovableTile(masterX, masterY, heading, masterMap, false)) {
	                if (toMoving(this, masterX, masterY, heading, true)) return true;
	            }

	            // ✅ 2. 현재 헤딩 방향으로 한 칸 전진 시도
	            int nextX = Util.getXY(heading, true) + this.x;
	            int nextY = Util.getXY(heading, false) + this.y;
	            if (isMovableTile(nextX, nextY, heading, masterMap, false)) {
	                if (toMoving(this, nextX, nextY, heading, true)) return true;
	            }

	            // ✅ 3. 주변 8방향 탐색하여 최적 경로 탐색
	            List<int[]> candidates = new ArrayList<>();
	            for (int h = 0; h < 8; h++) {
	                int tx = Util.getXY(h, true) + this.x;
	                int ty = Util.getXY(h, false) + this.y;
	                if (isMovableTile(tx, ty, h, masterMap, false)) {
	                    int dist = Math.abs(tx - masterX) + Math.abs(ty - masterY);
	                    candidates.add(new int[]{tx, ty, h, dist});
	                }
	            }

	            // 거리 기준으로 정렬
	            candidates.sort(Comparator.comparingInt(a -> a[3]));
	            for (int[] tile : candidates) {
	                if (toMoving(this, tile[0], tile[1], tile[2], true)) return true;
	            }

	            moveAttempts++;
	        }
	    }

	    return false; // 이동 실패 또는 이동 필요 없음
	}

    /**
     * ✅ 해당 좌표가 이동 가능한 타일인지 확인
     * @param ignorePC true면 PC가 있어도 이동 가능
     */
    private boolean isMovableTile(int x, int y, int heading, int mapId, boolean ignorePC) {
        if (!World.isThroughObject(this.x, this.y, mapId, heading)) return false;
        if (World.getMapdynamic(x, y, mapId) != 0) return false;
        if (World.isNotMovingTile(x, y, mapId)) return false;
        
        if (!ignorePC && isPlayerAt(x, y, mapId)) return false; // ✅ PC 무시 여부 분기
        if (isOccupiedByRobot(x, y)) return false;

        return true;
    }

    /**
     * ✅ 해당 좌표에 로봇 또는 사람(PC)이 있는지 확인
     */
    private boolean isOccupiedByRobot(int x, int y) {
        for (object obj : getInsideList()) {
            if (obj == this) continue;
            if (!(obj instanceof Character)) continue; // Character, PcInstance 등 전체 캐릭터 상위 클래스
            if (obj.getX() == x && obj.getY() == y && obj.getMap() == this.map) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * ✅ 해당 좌표에 사람이 조종하는 PC가 있는지 확인
     */
    private boolean isPlayerAt(int x, int y, int mapId) {
        for (object obj : getInsideList()) {
            if (obj == this) continue;
            if (!(obj instanceof PcInstance)) continue; // 실제 사람 유저 클래스 (확인 필요)
            if (obj.getX() == x && obj.getY() == y && obj.getMap() == mapId) {
                return true;
            }
        }
        return false;
    }   

    public void moveToOriginalSpawnLocation() {
        List<RobotSpawnLocation> spawnList = getSpawnLocationList(getPlaceName());

        // ✅ 현재 좌표가 등록된 스폰 리스트 중 하나와 일치하는지 확인
        for (RobotSpawnLocation loc : spawnList) {
            if (getX() == loc.getX() && getY() == loc.getY() && getMap() == loc.getMapId()) {
                // ✅ HP/MP 회복
                setNowHp(getTotalHp());
                setNowMp(getTotalMp());

                toPolyRemove();
                return; // ✅ 이미 스폰 위치 중 하나에 있음 → 이동 안 함
            }
        }

        // ✅ 일치하지 않으면 스폰 좌표 요청
        RobotSpawnLocation spawn = getSpawnLocation(getPlaceName());
        if (spawn != null) {
            String key = spawn.getX() + "_" + spawn.getY() + "_" + spawn.getMapId();

            // ✅ 현재 위치가 스폰 좌표와 같은 경우 → 이동 필요 없음, 예약 해제
            if (getX() == spawn.getX() && getY() == spawn.getY() && getMap() == spawn.getMapId()) {
                RESERVED_COORDS.remove(key);

                // ✅ HP/MP 회복
                setNowHp(getTotalHp());
                setNowMp(getTotalMp());

                toPolyRemove();
                return;
            }

            // ✅ 실제 이동
            toTeleport(spawn.getX(), spawn.getY(), spawn.getMapId(), true);

            // ✅ HP/MP 회복
            setNowHp(getTotalHp());
            setNowMp(getTotalMp());

            toPolyRemove();
            RobotController.unregister(this);
        }
    }
    
    /**
     * 파티 상태인 경우 파티장의 체력 및 상태(디버프/버프 누락 등)를 검사하여 
     * 적절한 스킬(힐, 버프, 디버프 해제)을 시전하고, 
     * 스킬이 적용되면 ai_time을 갱신한 후 true를 반환합니다.
     * 파티 상태가 아니라면 원래 위치로 이동시키고 false를 반환합니다.
     *
     * @return 스킬이 적용되어 AI 처리가 종료되면 true, 그렇지 않으면 false.
     */
    private boolean checkPartyBuffAndHeal() {
        if (!isRobotInParty()) {
            moveToOriginalSpawnLocation();            
            return false;
        }
        
        Party party = PartyController.find(this);
        if (party == null) {
            moveToOriginalSpawnLocation(); 
            return false;
        }
        
        Character master = party.getMaster();
        if (master == null) {
            moveToOriginalSpawnLocation(); 
            return false;
        }
        
        // 파티 로직 처리 (예: 파티원 위치 조정 등)
        PartyController.checkParty(this);
        List<Skill> skillList = SkillController.find(this);
        
        boolean applied = false;
        // 각각 독립적으로 조건을 검사해서 필요한 스킬을 시전합니다.
        if (checkDebuffRemoval(master, skillList)) {
            applied = true;
        }
        if (checkHeal(master, skillList)) {
            applied = true;
        }
        if (checkBuffs(master, skillList)) {
            applied = true;
        }
        
        if (applied) {
            ai_time = SpriteFrameDatabase.getGfxFrameTime(
                          this,
                          getGfx(),
                          getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION
                      );
            return true;
        }
        return false;
    }

    /**
     * 마스터의 현재 버프 목록을 순회하여 독 및 저주 디버프 적용 여부를 판단합니다.
     * 독 디버프: uid 11, 301, 304  
     * 저주 디버프: uid 20, 33, 47
     *
     * @param masterBuff 마스터의 버프 객체
     * @return BuffStatus 객체 (hasPoisonDebuff, hasCurseDebuff)
     */
    private BuffStatus getBuffStatus(Buff masterBuff) {
        BuffStatus status = new BuffStatus();
        if (masterBuff != null) {
            for (BuffInterface b : masterBuff.getList()) {
                if (b.getSkill() != null) {
                    int uid = b.getSkill().getUid();
                    if (uid == 11 || uid == 301 || uid == 304) {
                        status.hasPoisonDebuff = true;
                    }
                    if (uid == 20 || uid == 33 || uid == 47) {
                        status.hasCurseDebuff = true;
                    }
                }
            }
        }
        return status;
    }

    /**
     * 독/저주 디버프를 독립적으로 검사하고, 필요 시 해제 스킬을 적용합니다.
     * - 독 디버프가 있으면 uid 9번(큐어 포이즌) 스킬을 시전
     * - 저주 디버프가 있으면 uid 37번(리므브커스) 스킬을 시전
     *
     * @param master    스킬 적용 대상
     * @param skillList 로봇의 스킬 목록
     * @return 적용 시 true, 아니면 false.
     */
    private boolean checkDebuffRemoval(Character master, List<Skill> skillList) {
        Buff masterBuff = BuffController.find(master);
        BuffStatus status = getBuffStatus(masterBuff);
        
        // 독 디버프 체크 (큐어 포이즌: uid 9)
        if (status.hasPoisonDebuff) {
            if (applySkill(9, master, skillList, true, false)) {
                return true;
            }
        }
        // 저주 디버프 체크 (리므브커스: uid 37)
        if (status.hasCurseDebuff) {
            if (applySkill(37, master, skillList, true, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 마스터의 체력 상태에 따라 힐 스킬을 적용합니다.
     * - 체력 ≤ 30%: UID 128 (네이쳐스 터치)
     * - 체력 ≤ 50%: UID 57 (풀힐)
     * - 체력 ≤ 60%: UID 35 (일반 힐)
     *
     * @param master    스킬 적용 대상
     * @param skillList 로봇의 스킬 목록
     * @return 적용 시 true, 아니면 false.
     */
    private boolean checkHeal(Character master, List<Skill> skillList) {
        int hpPercent = master.getHpPercent();

        if (hpPercent <= 30) {
            return applySkill(128, master, skillList, false, false);  // 네이쳐스 터치
        } else if (hpPercent <= 50) {
            return applySkill(57, master, skillList, false, false);   // 풀힐
        } else if (hpPercent <= 60) {
            return applySkill(35, master, skillList, false, false);   // 일반 힐
        }

        return false;
    }

    private boolean checkBuffs(Character master, List<Skill> skillList) {
        boolean applied = false;

        // 마스터 무기 정보
        ItemInstance weapon = master.getInventory().getSlot(Lineage.SLOT_WEAPON);
        boolean isBow = weapon != null && "bow".equalsIgnoreCase(weapon.getItem().getType2());

        // 1. 무기와 상관없이 항상 적용할 버프
        int[] generalBuffs = {
            26, // 피지컬 인챈트: DEX
            42, // 피지컬 인챈트: STR
            67, // 어드밴스 스피릿 
            23, // 버서커스
        };

        for (int uid : generalBuffs) {
            if (!hasBuff(master, uid)) {
                if (applySkill(uid, master, skillList, true, false)) {
                    applied = true;
                }
            }
        }

        // 2. 활 전용 버프: 135 (스톰 샷)
        if (isBow && !hasBuff(master, 135)) {
            if (applySkill(135, master, skillList, true, false)) {
                applied = true;
            }
        }

        // 3. 활이 아닐 때만 적용할 버프: 48, 124, 136
        if (!isBow) {
            int[] meleeOnlyBuffs = {48, 124, 136};
            for (int uid : meleeOnlyBuffs) {
                if (!hasBuff(master, uid)) {
                    if (applySkill(uid, master, skillList, true, false)) {
                        applied = true;
                    }
                }
            }
        }

        return applied;
    }


    /**
     * 버프 상태 정보를 담는 클래스
     */
    private static class BuffStatus {
        boolean hasPoisonDebuff;
        boolean hasCurseDebuff;
    }

    /**
     * 로봇의 스킬 목록에서 지정된 uid에 해당하는 스킬을 찾아서 적용하는 메서드입니다.
     *
     * @param uid         찾을 스킬 uid
     * @param target      스킬 적용 대상
     * @param skillList   로봇의 스킬 목록
     * @param isBuffSkill true이면 toSkillBuff, false이면 toSkillHealHp 사용
     * @param forceCast   (isBuffSkill가 false인 경우) true이면 HP 체크를 무시하고 힐 스킬을 시전함
     *                    (isBuffSkill가 true인 경우 이 값은 forceCast로 사용되어 대상의 버프 상태 무시 여부를 결정합니다.)
     * @return 스킬 적용 성공 여부
     */
    private boolean applySkill(int uid, Character target, List<Skill> skillList, boolean isBuffSkill, boolean forceCast) {
        Skill skill = getSkillByUid(skillList, uid);
        if (skill != null) {
            if (isBuffSkill) {
                // forceHeal를 버프 스킬의 forceCast로 재사용
                return toSkillBuff(Collections.singletonList(skill), target, forceCast);
            } else {
                return toSkillHealHp(Collections.singletonList(skill), target, forceCast);
            }
        }
        return false;
    }


    /**
     * 스킬 목록에서 지정된 uid에 해당하는 스킬을 검색합니다.
     *
     * @param skillList 로봇의 스킬 목록
     * @param uid       찾을 스킬 uid
     * @return 해당 스킬이 있으면 반환, 없으면 null
     */
    private Skill getSkillByUid(List<Skill> skillList, int uid) {
        for (Skill s : skillList) {
            SkillRobot sr = (SkillRobot) s;
            if (sr.getUid() == uid) {
                return s;
            }
        }
        return null;
    }

    /**
     * 대상에게 이미 특정 uid의 버프가 적용되어 있는지 확인합니다.
     *
     * @param target 대상 캐릭터
     * @param uid    확인할 버프 스킬 uid
     * @return 적용되어 있으면 true, 아니면 false
     */
    private boolean hasBuff(Character target, int uid) {
        // 일반적인 버프 체크
        Buff buff = BuffController.find(target);
        if (buff != null) {
            for (BuffInterface b : buff.getList()) {
                if (b.getSkill() != null && b.getSkill().getUid() == uid) {
                    return true;
                }
            }
        }

        // 블레스 웨폰은 무기에 적용되므로 별도 처리
        if (uid == 48) {
            ItemInstance weapon = target.getInventory().getSlot(Lineage.SLOT_WEAPON);
            if (weapon != null && weapon.isBuffBlessWeapon()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 채팅 명령어를 파싱하여 해당 키워드가 포함되었을 경우, 
     * 파티 마스터에게 지정된 스킬들을 (현재 마스터의 버프 상태와 상관없이) 시전합니다.
     *
     * @param msg 채팅 메시지 (파티 마스터가 입력한 내용)
     * @return 커맨드가 실행되었으면 true, 아니면 false
     */
    public boolean processChatCommand(String msg) {
        if (msg == null) return false;

        String lowerMsg = msg.trim().toLowerCase();
        // System.println("[디버그] 로봇 명령 수신: " + lowerMsg);

        Party party = PartyController.find(this);
        if (party == null) {
            // System.println("[디버그] 파티 없음.");
            return false;
        }

        PcInstance master = party.getMaster();
        if (master == null) {
            // System.println("[디버그] 파티 마스터 없음.");
            return false;
        }

        List<Skill> skillList = SkillController.find(this);
        boolean executed = false;

        // 이뮨
        if (lowerMsg.contains("ㅁ") || lowerMsg.contains("이뮨") || lowerMsg.contains("뮨")) {
            boolean result = applySkill(68, master, skillList, true, true);
            // System.println("[디버그] 이뮨 시도: " + result);
            executed |= result;
        }

        // 힐
        if (lowerMsg.contains("ㅎ") || lowerMsg.contains("힐") || lowerMsg.contains("힐좀")) {
            ScheduledExecutorService healScheduler = Executors.newSingleThreadScheduledExecutor();
            boolean result = applySkill(57, master, skillList, false, true);
            // System.println("[디버그] 힐 시도: " + result);

            if (result) {
                healScheduler.schedule(() -> {
                    boolean nbResult = applySkill(133, master, skillList, false, true);
                    // System.println("[디버그] 네이쳐스 블레싱 시도(딜레이): " + nbResult);
                }, 1000, TimeUnit.MILLISECONDS);
            } else {
                boolean nbResult = applySkill(133, master, skillList, false, true);
                // System.println("[디버그] 네이쳐스 블레싱 시도(즉시): " + nbResult);
            }

            // 🔥 shutdown 제거: 스케줄러는 작업이 끝나면 자동 종료됨
            // healScheduler.shutdown();
            executed = true;
        }

        // 버프
        if (lowerMsg.contains("ㅇ") || lowerMsg.contains("업") || lowerMsg.contains("버프")) {
            final List<Integer> buffList = Arrays.asList(23, 26, 42, 48, 67, 68, 137);
            final ScheduledExecutorService buffScheduler = Executors.newSingleThreadScheduledExecutor();

            final PcInstance finalMaster = master;
            final List<Skill> finalSkillList = new ArrayList<>(skillList);
            final long[] delay = {0};
            final long stepDelay = 1000;

            for (final int uid : buffList) {
                final long currentDelay = delay[0];

                buffScheduler.schedule(() -> {
                    boolean buffResult = applySkill(uid, finalMaster, finalSkillList, true, true);
                    // System.println("[디버그] 버프 스킬 시도 uid=" + uid + " : " + buffResult);
                }, currentDelay, TimeUnit.MILLISECONDS);

                delay[0] += stepDelay;
            }

            // 🔥 shutdown 제거: 모든 작업이 끝나면 스케줄러는 자동 종료
            // buffScheduler.schedule(() -> {
            //     buffScheduler.shutdown();
            // }, delay[0], TimeUnit.MILLISECONDS);

            executed = true;
        }

        return executed;
    }



	/**
	 * ✅ 줍기 대상 아이템 여부 판단
	 * - 아이템 객체인지 확인
	 * - 이미 경로 리스트에 포함되지 않았는지 확인
	 * - 이름이 "아데나"가 아닌 경우만 대상
	 */
	protected boolean isPickupItem(object o) {
	    if (!(o instanceof ItemInstance)) {
	        return false;
	    }

	    if (containsAstarList(o)) {
	        return false;
	    }

	    ItemInstance item = (ItemInstance) o;
	    return !item.getItem().getName().equalsIgnoreCase("아데나");
	}

	/**
	 * ✅ 비동기로 실행되는 타겟 탐색 메서드
	 */
	private void findTarget() {
	    synchronized (this) {
	        // ✅ 기존 타겟의 상태 확인 후 초기화
	        if (target != null && shouldResetTarget(target)) {
	            target = null; // 타겟 초기화
	        }
	    }
	    processInsideList(); // 새로운 타겟 탐색
	}

	private void processInsideList() {
	    try {
	        List<object> insideList;

	        synchronized (this) {
	            insideList = getInsideList();
	        }

	        if (insideList == null || insideList.isEmpty()) return;

	        // ✅ 1. 마스터가 공격 중인 타겟 확인 (단, 몬스터 또는 로봇만 허용)
	        object masterTarget = findMasterTarget(insideList);
	        if (masterTarget != null &&
	            (masterTarget instanceof MonsterInstance || masterTarget instanceof PartyRobotInstance || masterTarget instanceof PcInstance) &&
	            isAttack(masterTarget, true)) {

	            assignSharedTarget(masterTarget, insideList);
	            return;
	        }

	        // ✅ 2. 같은 파티 소속의 다른 PartyRobotInstance로부터 타겟 공유
	        object sharedTarget = insideList.stream()
	            .filter(o -> o instanceof PartyRobotInstance)
	            .map(o -> (PartyRobotInstance) o)
	            .filter(pt ->
	                pt != this &&
	                pt.getTarget() != null &&
	                isAttack(pt.getTarget(), true) &&
	                isSameParty(pt)
	            )
	            .map(PartyRobotInstance::getTarget)
	            .filter(t -> t instanceof MonsterInstance || t instanceof RobotInstance)
	            .findFirst()
	            .orElse(null);

	        if (sharedTarget != null) {
	            assignSharedTarget(sharedTarget, insideList);
	            return;
	        }
/*
	        // ✅ 3. 일반적인 대상 탐색 (가장 가까운 몬스터만)
	        object closestTarget = insideList.stream()
	            .filter(o -> o instanceof MonsterInstance) // ← 일반 탐색은 몬스터만
	            .filter(o -> isValidInstance(o))
	            .filter(o -> Util.isAreaAttack(this, o) && isAttack(o, true))
	            .min(Comparator.comparingInt(o -> Util.getDistance(this, o)))
	            .orElse(null);

	        if (closestTarget != null) {
	            assignSharedTarget(closestTarget, insideList);
	        }
*/
	    } catch (Exception e) {
	        lineage.share.System.printf(
	            "[PartyRobotInstance 오류] %s (partyId=%d) - processInsideList 예외: %s\r\n",
	            getName(), getPartyId(), e
	        );
	        e.printStackTrace();
	    }
	}

	/**
	 * ✅ 마스터가 공격 중인 타겟을 찾는 메서드
	 */
	private object findMasterTarget(List<object> insideList) {
	    Party party = PartyController.find(this);  // 현재 로봇이 속한 파티 찾기

	    if (party != null) {
	        PcInstance master = party.getMaster();  // 파티의 마스터를 찾기

	        if (master != null && master.getTarget() != null) {  // 마스터가 타겟을 지정하고 있는 경우
	            object masterTarget = master.getTarget();  // 마스터의 타겟
	            if (isAttack(masterTarget, true)) {  // 마스터의 타겟이 공격 가능한 경우
	                return masterTarget;
	            }
	        }
	    }
	    return null;  // 마스터의 타겟이 없거나 공격할 수 없는 경우
	}

	/**
	 * ✅ 특정 인스턴스 타입만 유효한 타겟으로 판단
	 */
	private boolean isValidInstance(object o) {
	    return (o instanceof PcInstance || o instanceof PcRobotInstance || o instanceof MonsterInstance);
	}

	/**
	 * ✅ 공유된 타겟을 배정하는 헬퍼 메서드
	 */
	private void assignSharedTarget(object newTarget, List<object> insideList) {
	    synchronized (this) {
	        setTarget(newTarget);  // 자신에게 타겟 설정
	    }

	    insideList.stream()
	        .filter(o -> o instanceof PartyRobotInstance)
	        .map(o -> (PartyRobotInstance) o)
	        .filter(pt ->
	            pt.getTarget() == null &&
	            isAttack(newTarget, true) &&
	            isSameParty(pt)   // 🔐 같은 파티인 경우에만 공유
	        )
	        .forEach(pt -> pt.setTarget(newTarget));
	}
	
	/**
	 * ✅ 특정 조건에서 target을 초기화하는 메서드
	 * - target이 Spartoi 타입이면서 GfxMode가 28일 경우 초기화
	 * - target이 StoneGolem 타입이면서 GfxMode가 4일 경우 초기화
	 */
	private boolean shouldResetTarget(object o) {
	    if (o instanceof Spartoi && o.getGfxMode() == 28) {
	        return true; // Spartoi가 특정 GfxMode일 때 초기화
	    }
	    if (o instanceof StoneGolem && o.getGfxMode() == 4) {
	        return true; // StoneGolem이 특정 GfxMode일 때 초기화
	    }
	    if (o instanceof Harphy && o.getGfxMode() == 4) {
	        return true; // Harphy가 특정 GfxMode일 때 초기화
	    }
	    return false;
	}

	/**
	 * 🔹 AI의 공격 동작 처리
	 */
    /**
     * 🔹 AI의 공격 동작 처리
     */
    @Override
    protected void toAiAttack(long time) {
        try {
            // 🔹 대기 상태에서는 행동하지 않음 (오픈 대기 중)
            if (Lineage.open_wait && pcrobot_mode != PCROBOT_MODE.Cracker && isWait())
                return;
            
			toHealingPotion();
			toBuffPotion();		
            checkPartyBuffAndHeal();
            
			// 🔹 currentAttackTarget 유효성 검사
			object o = checkTargetValidity(currentAttackTarget);

			if (o == null) {
				clearTarget();
				return;
			}

			// 🔹 같은 혈맹이면 공격하지 않음
			if (getClanId() > 0 && getClanId() == o.getClanId() && !(o instanceof Doppelganger)) {
				clearTarget();
				return;
			}

			// ✅ 같은 파티원은 공격 불가
			if (this instanceof PcInstance && o instanceof PcInstance) {
			    PcInstance attacker = (PcInstance) this;
			    PcInstance target = (PcInstance) o;
			    Party attackerParty = PartyController.find(attacker);
			    Party targetParty = PartyController.find(target);
			    if (attackerParty != null && targetParty != null && attackerParty == targetParty) {
			        clearTarget();
			        return;
			    }
			}
	        
            // 🔹 타겟 상태가 비정상이라면 리셋
            if (shouldResetTarget(o)) {
                clearTarget();
                return;
            }

            // 🔹 인비저 상태 감지 시 디텍션 마법 사용
            if (o.isInvis() && Util.random(0, 100) <= 30) {
                toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this,
                            Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
                Detection.onBuff(this, SkillDatabase.find(2, 4));
                ai_time = SpriteFrameDatabase.getGfxFrameTime(
                    this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION
                );
                return;
            }
            
            // 🔹 스킬 공격 시도
            boolean magicUsed = toSkillAttack(o);

            // 🔹 공격 사거리 확인 (활 착용 여부)
            boolean bow = getInventory().활장착여부();
            int atkRange = bow ? 8 : 1;

            // 🔹 공격 조건 만족 시
            if (Util.isDistance(this, o, atkRange) && Util.isAreaAttack(this, o) && Util.isAreaAttack(o, this)) {

                // 🔹 물리 공격 타이밍일 때만 수행
                if (!magicUsed && (AttackController.isAttackTime(this, getGfxMode() + Lineage.GFX_MODE_ATTACK, false)
                    || AttackController.isMagicTime(this, getCurrentSkillMotion()))) {

                    ai_time = (int) (SpriteFrameDatabase.getSpeedCheckGfxFrameTime(
                            this, getGfx(), getGfxMode() + Lineage.GFX_MODE_ATTACK
                        ) + 40);

                    // 🔥 공격 실행
                    toAttack(o, o.getX(), o.getY(), bow, getGfxMode() + Lineage.GFX_MODE_ATTACK, 0, false);
                }

            } else {
                // 🔹 이동 실패 시 타겟 초기화
                if (!moveToTarget(o)) {
                    clearTarget();
                }

                // 크래커 모드에서 타겟 없으면 귀환
                if (pcrobot_mode == PCROBOT_MODE.Cracker && currentAttackTarget == null) {
                	setAiStatus(Lineage.AI_STATUS_WALK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            lineage.share.System.printf("[처리 오류] toAiAttack(long time)\r\n : %s\r\n", e.toString());
        }
    }
	
    /**
     * ✅ 특정 대상(o)까지 이동 가능 여부 체크 및 이동
     * 단순히 o의 좌표로 이동만 시도합니다.
     */
    private boolean moveToTarget(object o) {
        if (o == null) return false;

        int targetX = o.getX();
        int targetY = o.getY();

        // 해당 좌표로 이동 시도 
        return toMoving(this, targetX, targetY, 0, true);
    }

    
    public void clearTarget() {
        target = null;
        currentAttackTarget = null;
        ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
        setAiStatus(Lineage.AI_STATUS_WALK);
    }
    
    private boolean isSameParty(PartyRobotInstance other) {
        if (!(this instanceof PartyRobotInstance)) return false;

        PartyRobotInstance self = (PartyRobotInstance) this;
        return self.getPartyId() > 0 &&
               other.getPartyId() > 0 &&
               self.getPartyId() == other.getPartyId();
    }

    
	/**
	 * ✅ 특정 맵 ID들이 로직 제외 대상인지 검사
	 */
	private boolean isExcludedMap(int map) {
	    return map == 70 || map == 68 || map == 69 || map == 85 || map == 86;
	}

	/**
	 * ✅ 딜레이 시간 관리
	 * - 랜덤한 지연시간 설정 및 초기화
	 */
	private void manageDelayTime() {
	    if (delayTime == 0)
	        delayTime = System.currentTimeMillis() + (1000 * (Util.random(3, 10)));

	    if (delayTime > 0 && delayTime <= System.currentTimeMillis())
	        delayTime = 0;
	}

	// 🔹 타겟 유효성 검사 후 유효하면 그대로 반환, 아니면 null 반환
	private object checkTargetValidity(object o) {
	    if (o == null) {
	        return null;
	    }
	    if (o.isDead()) {
	        return null;
	    }
	    if (o.isWorldDelete()) {
	        return null;
	    }
	    if (!isAttack(o, false)) {
	        return null;
	    }
	    return o;  // 모든 조건을 통과하면 유효한 타겟 반환
	}
	
	/**
	 * 공격 마법.
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	protected boolean toSkillAttack(object o) {
	    if (this == null || o == null)
	        return false;

	    // 🔸 스킬 사용 전체에 대한 확률 (예: 70%)
	    final double OVERALL_SKILL_USE_PROBABILITY = 0.5;
	    if (Math.random() > OVERALL_SKILL_USE_PROBABILITY) {
	        return false;  // 일정 확률로 전체 스킬 사용 자체를 스킵
	    }

	    List<Skill> list = SkillController.find(this);
	    ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

	    if (list == null)
	        return false;

	    if (System.currentTimeMillis() < delay_magic)
	        return false;

	    // 🔸 마나 부족 + 30% 확률로 스킵
	    if (getMpPercent() < USABLE_MP_PERCENT && Util.random(0, 100) <= 50)
	        return false;

	    if (o.isDead())
	        return false;

	    if (o instanceof KingdomDoor || o instanceof KingdomCrown)
	        return false;

	    for (Skill s : list) {
	        SkillRobot sr = (SkillRobot) s;
	        if (sr == null)
	            continue;

	        String type = sr.getType();
	        if (!type.equalsIgnoreCase("단일공격마법") &&
	            !type.equalsIgnoreCase("범위공격마법") &&
	            !type.equalsIgnoreCase("디버프"))
	            continue;

	        if (sr.getLevel() > getLevel())
	            continue;

	        // 🔸 무기 타입 체크
	        if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
	            if (weapon == null)
	                continue;

	            String wType = weapon.getItem().getType2();
	            boolean isTwoHand = weapon.getItem().isTohand();

	            switch (sr.getWeaponType()) {
	                case "한손검":
	                    if (!wType.equalsIgnoreCase("sword") || isTwoHand)
	                        continue;
	                    break;
	                case "양손검":
	                    if (!wType.equalsIgnoreCase("tohandsword") || !isTwoHand)
	                        continue;
	                    break;
	                case "한손검&양손검":
	                    if (!wType.equalsIgnoreCase("sword") && !wType.equalsIgnoreCase("tohandsword"))
	                        continue;
	                    break;
	                case "활":
	                    if (!wType.equalsIgnoreCase("bow"))
	                        continue;
	                    break;
	            }
	        }

	        // 🔸 대상 유형 체크
	        if (!sr.getTarget().equalsIgnoreCase("유저&몬스터")) {
	            switch (sr.getTarget()) {
	                case "유저":
	                    if (o instanceof MonsterInstance)
	                        continue;
	                    break;
	                case "몬스터":
	                    if (o instanceof PcInstance)
	                        continue;
	                    break;
	            }
	        }

	        // 🔸 속성 조건 체크
	        if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
	            continue;

	        // 🔸 MP 부족
	        if (sr.getMpConsume() > getNowMp())
	            continue;

	        // 🔸 개별 스킬 확률 적용
	        if (Math.random() < sr.getProbability()) {
	            toSkill(s, o);
	            return true;
	        }
	    }

	    return false;
	}

	
	@Override
	public void toAiEscape(long time) {
	    super.toAiEscape(time);

	    // 🔹 1. 공격자 유무 확인 및 유효성 검사
	    Character attackerSnapshot;
	    synchronized (this) {
	        if (attacker == null || !(attacker instanceof Character)) {
	            attacker = null;
	            setAiStatus(Lineage.AI_STATUS_WALK);
	            return;
	        }
	        attackerSnapshot = (Character) attacker;
	    }


	    // 🔹 2. 확률적으로 도망 멘트 출력
	    if (Util.random(1, 100) <= Lineage.robot_ment_probability) {
	        RobotController.getRandomMentAndChat(
	            Lineage.AI_THIEF_MENT,
	            this,
	            attackerSnapshot,
	            Lineage.CHATTING_MODE_NORMAL,
	            Lineage.AI_THIEF_MENT_DELAY
	        );
	    }
	    
	    // 🔹3. 공격자와 일정 거리 이상일 경우 → 도망 종료
	    if (Util.getDistance(this, attackerSnapshot) >= Lineage.robot_escape_clear_distance) {
	        synchronized (this) {
	            attacker = null;
	        }
	        setAiStatus(Lineage.AI_STATUS_WALK);
	        return;
	    }

	    // 🔹 4. 도망 방향 설정 (공격자의 반대 방향)
	    synchronized (this) {
	        heading = Util.oppositionHeading(this, attackerSnapshot);
	    }

	    int startHeading = heading;
	    int steps = 0;
	    int maxSteps = Lineage.robot_escape_step; // 설정된 도망 최대 칸 수
	    boolean escaped = false;

	    // 🔹 5. 도망 시도 루프
	    while (steps < maxSteps) {
	        int x = Util.getXY(heading, true) + this.x;
	        int y = Util.getXY(heading, false) + this.y;

	        boolean canMove = World.isThroughObject(this.x, this.y, this.map, heading);

	        synchronized (temp_list) {
	            temp_list.clear();
	            findInsideList(x, y, temp_list);

	            boolean hasObstacle = false;
	            for (object obj : temp_list) {
	                if (obj instanceof Character) {
	                    hasObstacle = true;
	                    break;
	                }
	            }

	            if (canMove && !hasObstacle) {
	                super.toMoving(x, y, heading); // 도망 이동
	                steps++;
	                escaped = true;
	                continue;
	            }
	        }

	        // 🔄 이동 실패 시 방향 변경 후 재시도
	        heading = (heading + 1) % 8;
	        if (heading == startHeading)
	            break; // 모든 방향을 한 번 돌았으면 탈출
	    }

	    // 🔹 6. 도망에 성공한 경우 → 상태 초기화
	    if (escaped) {
	        synchronized (this) {
	            attacker = null;
	        }
	        setAiStatus(Lineage.AI_STATUS_WALK);
	    }
	}
	
    @Override
    protected void toAiDead(long time) {
        super.toAiDead(time);
    
        ai_time_temp_1 = 0;
        target = tempTarget = attacker = currentAttackTarget = null;
        clearAstarList();
        setAiStatus(Lineage.AI_STATUS_CORPSE);
    }
    
    @Override
    protected void toAiCorpse(long time) {
        super.toAiCorpse(time);
    
        if (ai_time_temp_1 == 0)
            ai_time_temp_1 = time;
    
        if (ai_time_temp_1 + Lineage.ai_robot_corpse_time > time)
            return;
    
        ai_time_temp_1 = 0;
        toReset(true);
        clearList(true);
        World.remove(this);
        setAiStatus(Lineage.AI_STATUS_SPAWN);
    }
    
    @Override
    protected void toAiSpawn(long time) {
        super.toAiSpawn(time);
        goToHome(false);
        toRevival(this);
        setAiStatus(Lineage.AI_STATUS_WALK);
    }
    
    /**
     * ✅ AI 줍기 루틴
     * - 현재 타겟(target)으로 지정된 아이템이 있을 경우 해당 위치로 이동하거나 즉시 줍기 수행
     * - 아이템을 획득하면 타겟을 초기화하고 걷기 상태로 전환, 원래 장소로 이동
     */
	@Override
	protected void toAiPickup(long time) {
		object o = target; // ✅ 현재 설정된 줍기 대상 (타겟 아이템)

		// ✅ 타겟이 없으면 줍기 상태 종료 → 걷기 상태로 복귀
		if (o == null) {
			setAiStatus(Lineage.AI_STATUS_WALK);
			return;
		}

		// ✅ 현재 위치가 아이템 위치와 동일할 경우 → 아이템 줍기 시도
		if (Util.isDistance(this, o, 0)) {
			super.toAiPickup(time); // 부모 클래스 로직 실행

			synchronized (o.sync_pickup) {
				if (!o.isWorldDelete()) {
					Inventory inv = getInventory();
					if (inv != null) {
						inv.toPickup(o, o.getCount()); // ✅ 인벤토리에 아이템 추가
					}
				}
			}

			// ✅ 아이템 획득 후: 타겟 초기화 & 걷기 상태로 전환
			target = null;
			setAiStatus(Lineage.AI_STATUS_WALK);

		} else {
			// ✅ 아이템과 거리가 있을 경우 → 이동 명령 수행
			ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
			toMoving(o, o.getX(), o.getY(), 0, true); // 지정 위치로 이동
		}
	}
 
	protected void toInventoryHeavy() {
		switch (step++) {
		case 0:
			// 마을로 이동.
			goToHome(false);
			break;
		case 1:
			// 인벤에 아이템 삭제.
			for (ItemInstance ii : getInventory().getList()) {
				// 아데나는 무시.
				if (ii.getItem().getNameIdNumber() == 4)
					continue;
				// 착용중인 아이템 무시.
				if (ii.isEquipped())
					continue;
				// 그 외엔 다 제거.
				getInventory().remove(ii, false);
			}
			break;
		case 2:
			// 초기화.
			step = 0;
			// 기본 모드로 변경.
			pcrobot_mode = PCROBOT_MODE.None;
			break;
		}
	}

	private void toPolymorph() {
	    switch (step) {
	        case 0:
	            if (polyTime == 0) {
	                polyTime = System.currentTimeMillis() + (1000 * Util.random(1, 5));
	            }
	            if (polyTime > 0 && polyTime <= System.currentTimeMillis()) {
	                step = 1;
	            }
	            break;
	        
	        case 1:
	            ItemInstance polyScroll = getInventory().find(ScrollPolymorph.class);
	            ItemInstance mythicBook = getInventory().findDbNameId(6492); // 무한 신화 변신 북
	            boolean hasPolyScroll = polyScroll != null && polyScroll.getCount() > 0;
	            boolean hasMythicBook = mythicBook != null && mythicBook.getCount() > 0;

	            // 변신 방식을 랜덤으로 선택 (50:50 확률)
	            boolean useMythicPoly = false;
	            boolean usePolyScroll = false;

	            if (hasPolyScroll && hasMythicBook) {
	                // 두 아이템이 모두 있을 때 랜덤으로 선택 (50:50)
	                if (Util.random(0, 1) == 0) {
	                    useMythicPoly = true;
	                } else {
	                    usePolyScroll = true;
	                }
	            } else if (hasPolyScroll) {
	                usePolyScroll = true;
	            } else if (hasMythicBook) {
	                useMythicPoly = true;
	            }

	            if (!useMythicPoly && !usePolyScroll) {
	                // 변신할 수 있는 아이템이 없음
	                step = 0;
	                polyTime = 0;
	                pcrobot_mode = PCROBOT_MODE.None;
	                return;
	            }
	            
	            if (usePolyScroll) {
	                // 변신 주문서 이용
	                Poly p = PolyDatabase.getName(getPolymorph());
	                if (p != null && p.getMinLevel() <= getLevel()) {
	                    PolyDatabase.toEquipped(this, p);
	                    setGfx(p.getGfxId());
	                    
	                    // 무기 속도 조정 (applyWeaponSpeed 메서드 호출)
	                    applyWeaponSpeed(p);
	                    
	                    // 버프 등록
	                    BuffController.append(this, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), 7200));
	                    toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), this), true);
	                    
	                    // 주문서 사용량 감소 ("무한"이 포함되지 않은 경우만)
	                    if (!polyScroll.getItem().getName().contains("무한")) {
	                        getInventory().count(polyScroll, polyScroll.getCount() - 1, false);
	                    }
	                }
	            } else if (useMythicPoly) {
	                // 무한 신화 변신 북 이용
	                Poly p = PolyDatabase.getName(getRankPolyName());
	                if (p != null && getGfx() != p.getGfxId()) {
	                    mythicBook.toClick(this, null);
	                }
	            }
	            
	            // 초기화
	            step = 0;
	            polyTime = 0;
	            pcrobot_mode = PCROBOT_MODE.None;
	            break;
	    }
	}

	public void toPolyRemove() {
		BuffController.remove(this, ShapeChange.class);

		this.setGfx(this.getClassGfx());
		if (this.getInventory() != null && this.getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
			this.setGfxMode(this.getClassGfxMode() + this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
		else
			this.setGfxMode(this.getClassGfxMode());

		this.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), this), true);
	}
	
	/**
	 * ✅ Stay 루틴 (집으로 귀환 후 일정 시간 대기)
	 * - 대기 시간: 최소 5초 ~ 최대 30초
	 */
	private void toStay(long time) {
	    switch (step) {
	        case 0:
	            // ✅ 1단계: 집으로 이동
	            goToHome(false);
	            step = 1;
	            break;

	        case 1:
	            // ✅ 2단계: 랜덤 방향 설정 후 대기 진입
	            setHeading(Util.random(0, 7));
	            step = 2;
	            break;

	        case 2:
	            // ✅ 3단계: 5초 ~ 30초 동안 대기
	            if (ai_time_temp_1 == 0)
	                ai_time_temp_1 = time;

	            if (ai_time_temp_1 + Util.random(1000 * 5, 1000 * 30) > time)
	                return;

	            // 대기 완료 → 초기화 및 모드 전환
	            ai_time_temp_1 = 0;
	            step = 0;

	            // 3% 확률로 Stay 유지, 나머지는 모드 해제
	            if (Util.random(1, 100) < 3)
	                pcrobot_mode = PCROBOT_MODE.Stay;
	            else
	                pcrobot_mode = PCROBOT_MODE.None;
	            break;
	    }
	}

	   private boolean isAttack(object o, boolean walk) {
	        if (o == null || o.getGm() > 0 || o.isDead() || o.isTransparent()) {
	            return false;
	        }
	        
	        // ✅ 같은 파티원은 공격 불가
	        if (this instanceof PcInstance && o instanceof PcInstance) {
	            PcInstance attacker = (PcInstance) this;
	            PcInstance target = (PcInstance) o;
	            Party attackerParty = PartyController.find(attacker);
	            Party targetParty = PartyController.find(target);
	            if (attackerParty != null && targetParty != null && attackerParty == targetParty) {
	                return false;
	            }
	        }
	        
	        if (!Util.isDistance(this, o, Lineage.SEARCH_WORLD_LOCATION)) {
	            return false;
	        }
	    
	        if (World.isSafetyZone(getX(), getY(), getMap()) && !(o instanceof MonsterInstance)) {
	            return false;
	        }
	    
	        if (o instanceof TeleportInstance || o instanceof EventInstance || o instanceof InnInstance ||
	            o instanceof ShopInstance || o instanceof DwarfInstance || o instanceof PetMasterInstance) {
	            return false;
	        }
	    
	        if (o instanceof GuardInstance || o instanceof PatrolGuard) {
	            return true;
	        }
	        
	        if (o instanceof SummonInstance || (o instanceof NpcInstance && !(o instanceof GuardInstance) && !(o instanceof PatrolGuard))) {
	            return false;
	        }

	        if (o instanceof ItemInstance || o instanceof BackgroundInstance || o instanceof MagicDollInstance) {
	            return false;
	        }
	    
	        if (!(o instanceof MonsterInstance) && getX() == o.getX() && getY() == o.getY() && getMap() == o.getMap()) {
	            return false;
	        }
	    
		    // ✅ 특정 몬스터가 특정 GfxMode일 경우 공격 불가 처리
		    if (shouldResetTarget(o)) {
		        return false;
		    }
		    
	        return true;
	    }
	   
	public boolean toMoving(object o, final int x, final int y, final int h, final boolean astar) {
	    try {
	        if (o == null)
	            return false;

	        if (astar) {  // ✅ A* 경로 탐색을 사용할 경우
	            aStar.cleanTail();
	            tail = aStar.searchTail(this, x, y, true);

	            if (tail != null) {
	                while (tail != null) {
	                    if (tail.x == getX() && tail.y == getY())
	                        break;
	                    iPath[0] = tail.x;
	                    iPath[1] = tail.y;
	                    tail = tail.prev;
	                }

	                toMoving(iPath[0], iPath[1], Util.calcheading(this.x, this.y, iPath[0], iPath[1]));
	                toSender(S_ObjectMoving.clone(BasePacketPooling.getPool(S_ObjectMoving.class), this));
	                return true;  // ✅ 이동 성공 시 true 반환
	            } else {
	                if (o != null)
	                    appendAstarList(o);  // 경로 탐색 실패 시 경로 목록에 추가
	                return false;  // ✅ 경로 탐색 실패
	            }
	        } else {  // ✅ A* 경로 탐색을 사용하지 않을 경우
	            toMoving(x, y, h);
	            return true;  // ✅ 이동 성공 시 true 반환
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        lineage.share.System.printf("[처리 오류] toMoving(object o, final int x, final int y, final int h, final boolean astar)\r\n : %s\r\n", e.toString());
	    }

	    return false;  // 예외 발생 시 false 반환
	}
	
	/**
	 * 버프 물약 복용
	 * 
	 * @return
	 */
	private boolean toBuffPotion() {
		//
		Buff b = BuffController.find(this);
		if (b == null)
			return false;
		// 촐기 복용.
		if (b.find(HastePotionMagic.class) == null) {
			ItemInstance item = getInventory().find(HastePotion.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}
		// 용기 복용.
		if ((getClassType() == Lineage.LINEAGE_CLASS_KNIGHT || getClassType() == Lineage.LINEAGE_CLASS_ROYAL) && b.find(Bravery.class) == null) {
			ItemInstance item = getInventory().find(BraveryPotion.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}
		// 엘븐와퍼 복용.
		if (getClassType() == Lineage.LINEAGE_CLASS_ELF && b.find(Wafer.class) == null) {
			ItemInstance item = getInventory().find(BraveryPotion.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}
		// 홀리워크 사용
		if (getClassType() == Lineage.LINEAGE_CLASS_WIZARD && b.find(HolyWalk.class) == null) {
			ItemInstance item = getInventory().find(BraveryPotion.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}

		// 버프 물약 사용
		if (getInventory() != null && getInventory().getSlot(Lineage.SLOT_ARMOR) != null && getInventory().getSlot(Lineage.SLOT_WEAPON) != null) {
			if (b.find(DecreaseWeight.class) == null || b.find(EnchantDexterity.class) == null || b.find(EnchantMighty.class) == null || b.find(BlessWeapon.class) == null) {
				ItemInstance item = getInventory().find(Buff_potion.class);
				if (item != null && item.isClick(this)) {
					item.toClick(this, null);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 소울 스킬 시전
	 * 
	 * @return
	 */
	private boolean toSkillHealMp(List<Skill> list) {
		//
		if (getNowMp() == getTotalMp())
			return false;
		//
		if (list == null)
			return false;
		
		ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);
		
		for (Skill s : list) {
			SkillRobot sr = (SkillRobot) s;
			if (sr.getType().equalsIgnoreCase("mp회복마법") == false)
				continue;
			
			if (sr.getLevel() > getLevel())
				continue;
			
			if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
				if (weapon == null)
					continue;
				
				switch (sr.getWeaponType()) {
				case "한손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
						continue;
					break;
				case "양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
						continue;
					break;
				case "한손검&양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") && !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
						continue;
					break;
				case "활":
					if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
						continue;
					break;
				}
			}
			
			if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
				continue;
			
			if (Math.random() < sr.getProbability())
				toSkill(s, this);
			return true;
		}

		return false;
	}

	/**
	 * 지정한 대상(target)의 HP가 지정된 회복 임계치 이하인 경우 (혹은 force 파라미터가 true이면 체크하지 않음)
	 * 사용 가능한 힐 스킬 중 하나를 선택하여 대상에게 스킬을 시전합니다.
	 *
	 * @param list       사용 가능한 스킬 목록
	 * @param o          힐 스킬 사용 대상
	 * @param forceHeal  true이면 HP 임계치 체크를 무시하고 힐 스킬을 시전함
	 * @return 힐 스킬을 성공적으로 사용했으면 true, 아니면 false
	 */
	protected boolean toSkillHealHp(List<Skill> list, object o, boolean forceHeal) {
	    if (!(o instanceof Character)) {
	        return false;
	    }
	    Character target = (Character) o;

	    if (!forceHeal && target.getHpPercent() > HEALING_PERCENT)
	        return false;

	    if (list == null)
	        return false;

	    ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

	    for (Skill s : list) {
	        SkillRobot sr = (SkillRobot) s;

	        if (!sr.getType().equalsIgnoreCase("힐"))
	            continue;

	        if (sr.getLevel() > getLevel())
	            continue;

	        // ✅ MP 부족하면 무조건 제외
	        if (sr.getMpConsume() > getNowMp())
	            continue;

	        if (!forceHeal) {
	            if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
	                if (weapon == null)
	                    continue;
	                switch (sr.getWeaponType()) {
	                    case "한손검":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
	                            continue;
	                        break;
	                    case "양손검":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
	                            continue;
	                        break;
	                    case "한손검&양손검":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("sword") &&
	                            !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
	                            continue;
	                        break;
	                    case "활":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
	                            continue;
	                        break;
	                }
	            }

	            if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
	                continue;
	        }

	        toSkill(s, target);
	        return true;
	    }
	    return false;
	}


	/**
	 * 지정한 대상(target)이 버프 스킬을 적용받도록 시전합니다.
	 * 
	 * @param list      사용 가능한 버프 스킬 목록
	 * @param o         버프 스킬 사용 대상 (Character 타입)
	 * @param forceCast true이면 대상의 현재 버프 상태를 무시하고 스킬을 시전함
	 * @return 스킬을 성공적으로 시전했으면 true, 아니면 false
	 */
	protected boolean toSkillBuff(List<Skill> list, object o, boolean forceCast) {
	    if (!(o instanceof Character)) {
	        return false;
	    }
	    Character target = (Character) o;
	    if (list == null)
	        return false;

	    ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

	    for (Skill s : list) {
	        SkillRobot sr = (SkillRobot) s;

	        if (!sr.getType().equalsIgnoreCase("버프"))
	            continue;

	        if (sr.getLevel() > getLevel())
	            continue;

	        // ✅ MP 부족하면 무조건 제외 (forceCast 여부와 상관없이)
	        if (sr.getMpConsume() > getNowMp())
	            continue;

	        if (!forceCast) {
	            if (sr.getUid() == 43 && BuffController.find(target, SkillDatabase.find(311)) != null)
	                continue;

	            if (BuffController.find(target, s) != null)
	                continue;

	            if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
	                if (weapon == null)
	                    continue;
	                switch (sr.getWeaponType()) {
	                    case "한손검":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
	                            continue;
	                        break;
	                    case "양손검":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
	                            continue;
	                        break;
	                    case "한손검&양손검":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("sword") &&
	                            !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
	                            continue;
	                        break;
	                    case "활":
	                        if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
	                            continue;
	                        break;
	                }
	            }

	            if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
	                continue;
	        }

	        toSkill(s, target);
	        return true;
	    }
	    return false;
	}
	
	/**
	 * 중복코드 방지용.
	 * 
	 * @param s
	 */
	private void toSkill(Skill s, object o) {
		ServerBasePacket sbp = (ServerBasePacket) ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), null);
		sbp.writeC(0); // opcode
		sbp.writeC(s.getSkillLevel() - 1); // level
		sbp.writeC(s.getSkillNumber()); // number
		sbp.writeD(o.getObjectId()); // objId
		sbp.writeH(o.getX()); // x좌표
		sbp.writeH(o.getY()); // y좌표
		byte[] data = sbp.getBytes();
		BasePacketPooling.setPool(sbp);
		BasePacket bp = ClientBasePacket.clone(BasePacketPooling.getPool(ClientBasePacket.class), data, data.length);
		SkillController.toSkill(this, (ClientBasePacket) bp);
		// 메모리 재사용.
		BasePacketPooling.setPool(bp);
	}

	/**
	 * 체력 물약 복용.
	 */
	private boolean toHealingPotion() {
		//
		if (getHpPercent() > HEALING_PERCENT)
			return false;
		//
		ItemInstance item = getInventory().find(HealingPotion.class);
		if (item != null && item.isClick(this))
			item.toClick(this, null);
		return true;
	}
	
    protected void goToHome(boolean isCracker) {
        if (!LocationController.isTeleportVerrYedHoraeZone(this, true))
            return;
        
        if (!isCracker && World.isGiranHome(getX(), getY(), getMap()))
            return;
    
        target = tempTarget = attacker = currentAttackTarget = null;
        clearAstarList();
        
        int[] home = Lineage.getHomeXY();
        setHomeX(home[0]);
        setHomeY(home[1]);
        setHomeMap(home[2]);		
        
        toTeleport(getHomeX(), getHomeY(), getHomeMap(), isDead() == false);
    }
 
	/**
	 * 클레스별로 변신할 이름 리턴.
	 * 
	 * @return
	 */
	private String getPolymorph() {
		RobotPoly rp = null;

		if (RobotController.getPolyList().size() < 1)
			return "";
		
		for (int i = 0; i < 200; i++) {
			rp = RobotController.getPolyList().get(Util.random(0, RobotController.getPolyList().size() - 1));

			if (rp != null && rp.getPoly().getMinLevel() <= getLevel() && SpriteFrameDatabase.findGfxMode(rp.getPoly().getGfxId(), getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
				switch (rp.getPolyClass()) {
				case "모든클래스":
					return rp.getPoly().getName();
				case "군주":
					if (getClassType() == Lineage.LINEAGE_CLASS_ROYAL)
						return rp.getPoly().getName();
					else
						continue;
				case "기사":
					if (getClassType() == Lineage.LINEAGE_CLASS_KNIGHT)
						return rp.getPoly().getName();
					else
						continue;
				case "요정":
					if (getClassType() == Lineage.LINEAGE_CLASS_ELF)
						return rp.getPoly().getName();
					else
						continue;
				case "마법사":
					if (getClassType() == Lineage.LINEAGE_CLASS_WIZARD)
						return rp.getPoly().getName();
					else
						continue;
				case "군주&기사&마법사":
					if (getClassType() == Lineage.LINEAGE_CLASS_ROYAL || getClassType() == Lineage.LINEAGE_CLASS_KNIGHT || getClassType() == Lineage.LINEAGE_CLASS_WIZARD)
						return rp.getPoly().getName();
					else
						continue;
				}
			}
			continue;
		}
		
		return "";
	}

	/**
	 * 현재 변신상태가 최적화되지 않은 변신인지 확인하는 메서드.
	 * 	: 양호하다면 false를 리턴.
	 */
	protected boolean isBadPolymorph() {
		Poly p = PolyDatabase.getPolyName( getPolymorph() );
		return p!=null && getGfx()!=p.getGfxId() && getGfx()!=getClassGfx();
	}
	
	/**
	 * 화살 장착 메소드.
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	private void setArrow() {
	    if (getInventory() != null) {
	        ItemInstance arrow = getInventory().find(Arrow.class);
	        if (arrow != null && !arrow.isEquipped()) {
	            arrow.toClick(this, null);
	        }
	    }
	}
	
    /**
     * 인벤토리 셋팅: 로봇 전투 시 자동 무기 장착.
     */
    public void setInventory() {
        if (!Lineage.robot_auto_party) {
            return;
        }
        if (getInventory() == null) {
            return;
        }
        
        // 무기 이름이 지정되어 있지 않다면 무기를 장착하지 않음.
        if (getWeapon_name() == null) {
            return;
        }
        
        // 무기 이름이 지정되어 있으면 해당 이름으로 무기를 검색.
        weapon = ItemDatabase.find(getWeapon_name());
        
        if (weapon == null) {
            return;
        }
        
        ItemInstance item = ItemDatabase.newInstance(weapon);
        item.setObjectId(ServerDatabase.nextEtcObjId());
        item.setEnLevel(weaponEn);
        getInventory().append(item, false);
        
        // 자동 무기 장착: 클릭 액션을 호출하여 장비 처리.
        item.toClick(this, null);
    }
    
	/**
	 * 서버 오픈대기일 경우 처리.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	private boolean isWait() {		
		goToHome(false);

		if (Util.random(0, 99) < 50) {
			pcrobot_mode = PCROBOT_MODE.Stay;
		} else {
			do {
				// 이동 좌표 추출.
				int x = Util.getXY(getHeading(), true) + getX();
				int y = Util.getXY(getHeading(), false) + getY();

				// 해당 좌표 이동가능한지 체크.
				boolean tail = World.isThroughObject(getX(), getY(), getMap(), getHeading()) && World.isMapdynamic(x, y, map) == false;
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				if (tail && Util.random(0, 99) < 5) {
					toMoving(null, x, y, getHeading(), false);
				} else {
					if (Util.random(0, 99) < 10)
						setHeading(Util.random(0, 7));

					continue;
				}
			} while (false);
		}
		return true;
	}
	
    /**
     * 무기 속도 조정 (변신 상태에 따라 GFX 설정)
     */
    private void applyWeaponSpeed(Poly p) {
        if (getInventory() == null) {
            return;
        }

        if (Lineage.is_weapon_speed) {
            try {
                if (getInventory().getSlot(Lineage.SLOT_WEAPON) != null &&
                    SpriteFrameDatabase.findGfxMode(getGfx(), getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
                    setGfxMode(getGfxMode()); // 현재 GfxMode 유지
                } else {
                    setGfxMode(getGfxMode()); // 기본 GfxMode 유지
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.println("❌ [applyWeaponSpeed] 예외 발생: " + e.getMessage());
            }
        } else {
            setGfxMode(getGfxMode());
        }
    }
    
    /**
     * PartyRobotInstance 객체가 공격받았을 때,
     * 타겟이 없으면 공격자를 타겟으로 지정합니다.
     *
     * @param cha 공격한 객체
     */
    public void toDamage(Character cha) {
        // ✅ 유효성 검사
        if (cha == null || cha.getObjectId() == this.getObjectId() || cha.getGm() > 0) {
            return;
        }

        // ✅ 타겟이 없을 경우에만 설정
        if (currentAttackTarget == null) {
            setTarget(cha);
        }
    }
    
	/**
	 * 현재 위치가 기란 마을인지 확인.
	 * 
	 */
    public boolean isInVillage() { 
        return (this.getX() == this.getHomeX() && 
                this.getY() == this.getHomeY() && 
                this.getMap() == this.getHomeMap()) 
                || World.isGiranHome(getX(), getY(), getMap());        
    }
}
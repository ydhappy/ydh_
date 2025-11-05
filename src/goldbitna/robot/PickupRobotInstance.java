package goldbitna.robot;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.Poly;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectMoving;
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
import lineage.world.controller.RobotController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.item.all_night.Buff_potion;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.Criminal;
import lineage.world.object.magic.DecreaseWeight;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.HastePotionMagic;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.Wafer;

public class PickupRobotInstance extends RobotInstance {

    protected static final int ADEN_LIMIT          = 5000000;    // 아데나 체크할 최소값 및 추가될 아데나 갯수.
 // ✅ 아이템별 지급 수량 상수
    private static final int SUPPLY_COUNT_HEALING_POTION     = 500; // 체력 회복제
    private static final int SUPPLY_COUNT_HASTE_POTION       = 30;  // 속도향상 물약
    private static final int SUPPLY_COUNT_BRAVERY_POTION     = 30;  // 용기의 물약
    protected static final int HEALING_PERCENT     = 95;            // 체력 회복제를 복용할 시점 백분율
    protected static final int GOTOHOME_PERCENT    = 30;            // 체력이 해당퍼센트값보다 작으면 귀환함.
    protected static final int USABLE_MP_PERCENT   = 10;            // 해당 마나량이 해당 값보다 클때만 마법 사용
    // ✅ 타겟이 유지될 최대 시간 (예: 30초)
    private static final long MAX_TARGET_DURATION = 30_000; 
 
    private static final Map<String, int[]> ITEM_SEARCH_LOCATIONS = new HashMap<>();
    static {
        ITEM_SEARCH_LOCATIONS.put("말섬", new int[] {32611, 32840, 0});
        ITEM_SEARCH_LOCATIONS.put("골밭", new int[] {32896, 32656, 4});
        // ...
    }

    // 장소별 범위: 시작X, 끝X, 시작Y, 끝Y, 맵ID
    private static final Map<String, int[]> PLACE_AREA_MAP = new HashMap<>();

    static {
        PLACE_AREA_MAP.put("말섬", new int[]{32530, 32700, 32800, 32900, 0});
        PLACE_AREA_MAP.put("골밭", new int[]{32680, 32930, 32580, 32770, 4});
        // 필요 시 추가
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
    private String polyName;
    public PCROBOT_MODE pcrobot_mode; 	 // 처리 모드.
    private int step;               	 // 일렬 동작 처리 시 스탭 변수.
	public volatile object target;  	 // 공격 대상  
	public volatile object tempTarget;   // 임시 대상
	public volatile object attacker;  	 // 공격자  
	// 인벤토리
	protected Inventory inv;
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
    
    
    // 리로드 확인용.
    public boolean isReload;     
    
    public PickupRobotInstance() {
        aStar = new AStar();
        iPath = new int[2];
        astarList = new ArrayList<object>();
        temp_list = new ArrayList<object>();
        target = tempTarget = attacker = null;
    }
    
    @Override
    public void close() {
        super.close();
        if (getInventory() != null) {
            for (ItemInstance ii : getInventory().getList())
                ItemDatabase.setPool(ii);
            getInventory().clearList();
        }
        weapon_name = place_name = polyName = null;
        weapon = null;
        action = null;
        target = tempTarget = attacker = null;
        teleportTime = delayTime = polyTime = ai_time_temp_1 = targetSetTime = weaponEn = step = 0;
        isReload = false;
        
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
		if (inv != null) {
			for (ItemInstance ii : inv.getList()) {
				ItemDatabase.setPool(ii);
			}
			inv.clearList();
		}
    }
    
    @Override
    public void toSave(Connection con) {
    }
    
	@Override
	public Inventory getInventory() {
		return inv;
	}

	@Override
	public void setInventory(Inventory inv) {
		this.inv = inv;
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
  
    public String getPolymorph() {
        return polyName ;
    }
    
    public void setPolymorph (String polyName ) {
        this.polyName  = polyName ;
    }
    
    public synchronized object getTarget() {
        return attacker;
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
            target = tempTarget = attacker = null;
            clearAstarList();
            
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

        // 🔒 유효성 검사
        if (cha == null || cha.getObjectId() == getObjectId() || dmg <= 0 || cha.getGm() > 0)
            return;

        removeAstarList(cha); // 경로 최적화

        // 🔁 무조건 도망 모드로 진입
        if (attacker == null) {
            attacker = cha;
        }
        setAiStatus(Lineage.AI_STATUS_ESCAPE);

        // 📣 주변 동료(PkRobotInstance_1)에게 도움 요청
        for (object obj : getInsideList()) {
            if (obj instanceof Pk1RobotInstance) {
                Pk1RobotInstance ally = (Pk1RobotInstance) obj;
                ally.toDamage(cha); 
            }
        }
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
        // ✅ 1. 사망 처리 로직
        if (isDead()) {
            if (ai_time_temp_1 == 0)
                ai_time_temp_1 = time;

            if (ai_time_temp_1 + Lineage.ai_robot_corpse_time > time)
                return; // 아직 부활 시간 도달 전

            synchronized (this) {
                goToHome(false);     // 귀환 처리
                toRevival(this);     // 부활 처리
            }
            return;
        }

        // ✅ 2. 인벤토리 유효성 검사
        if (getInventory() == null)
            return;
    	 }
        // ✅ 3. 체력 부족 시 즉시 회복
        if (getHpPercent() <= HEALING_PERCENT)
            toHealingPotion();

        // ✅ 4. 체력 낮고 위험지역일 경우 → 30% 확률로 귀환 및 Stay 모드 진입
        if (!World.isSafetyZone(getX(), getY(), getMap()) && getHpPercent() <= GOTOHOME_PERCENT) {
            if (Util.random(0, 99) < 30) {
                synchronized (this) {
                    pcrobot_mode = PCROBOT_MODE.Stay;
                }
                goToHome(false);
                ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
                return;
            }
        }

        // ✅ 5. 소모품 자동 지급 (없을 경우만)
        Map<String, Integer> itemMap = new HashMap<>();         // 지급할 아이템명과 수량 매핑
        itemMap.put("농축 체력 회복제", SUPPLY_COUNT_HEALING_POTION);
        itemMap.put("속도향상 물약", SUPPLY_COUNT_HASTE_POTION);
        itemMap.put("용기의 물약", SUPPLY_COUNT_BRAVERY_POTION);

        // ✅ 아이템 지급
        synchronized (this) {
            for (Map.Entry<String, Integer> entry : itemMap.entrySet()) {
                String itemName = entry.getKey();
                int amount = entry.getValue();

                if (getInventory().find(itemName) == null) {
                    RobotController.giveItem(this, itemName, amount);
                }
            }
        }

        // ✅ 6. 타겟 유지 시간 검사
        checkTargetDuration();

        // ✅ 7. 타겟이 유효하지 않을 경우 → 걷기 상태로 복귀
        if (getAiStatus() == Lineage.AI_STATUS_PICKUP && pcrobot_mode != PCROBOT_MODE.Cracker) {
            if (target == null) {
                setAiStatus(Lineage.AI_STATUS_WALK);
            }
        }

        // ✅ 8. 인벤토리 무게 과다 시 모드 전환
        synchronized (this) {
            if (pcrobot_mode == PCROBOT_MODE.None && !getInventory().isWeightPercent(82)) {
                pcrobot_mode = PCROBOT_MODE.InventoryHeavy;
            }
        }

        // ✅ 9. 기본 외형일 경우 → 변신 모드 진입
        synchronized (this) {
            if (pcrobot_mode == PCROBOT_MODE.None && getGfx() == getClassGfx()) {
                pcrobot_mode = PCROBOT_MODE.Polymorph;
            }
        }
        
        // ✅ 10. 로봇 모드 진입 시 아데나 보충 및 걷기 상태 유지
        synchronized (this) {
            if (pcrobot_mode != PCROBOT_MODE.None && pcrobot_mode != PCROBOT_MODE.Cracker && getAiStatus() != Lineage.AI_STATUS_PICKUP) {
                setAiStatus(Lineage.AI_STATUS_WALK);

                // ✅ 아데나 부족 시 지급
                ItemInstance aden = getInventory().findAden();
                if (aden == null || aden.getCount() < ADEN_LIMIT) {
                    Item adenItem = ItemDatabase.find("아데나");
                    if (adenItem != null) {
                        if (aden == null) {
                            aden = ItemDatabase.newInstance(adenItem);
                            aden.setObjectId(ServerDatabase.nextEtcObjId());
                            getInventory().append(aden, false);
                        }
                        aden.setCount(aden.getCount() + ADEN_LIMIT);
                    }
                }
            }
        }

        // ✅ 11. 상위 AI 루틴 실행 (경로 탐색, 이동 등)
        super.toAi(time);
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
        if (mode != PCROBOT_MODE.Cracker && mode != PCROBOT_MODE.Stay) {
            toHealingPotion();
            toBuffPotion();
        }

        // ✅ 세이프존 + 특정 맵 제외 체크 후 처리
        if (!isExcludedMap(getMap()) && World.isSafetyZone(getX(), getY(), getMap())) {
            manageDelayTime();

            // ✅ 보라돌이 해제
            if (isBuffCriminal()) {
                BuffController.remove(this, Criminal.class);
            }

            // ✅ 체력이 일정 이상일 경우만 텔레포트
            if (getHpPercent() > HEALING_PERCENT) {
                String placeName = getPlaceName();

                // ✅ 말섬 또는 골밭 이벤트가 활성화 상태이면 텔레포트
                if ("말섬".equals(placeName) && RobotController.triggered1) {
                    teleportToItemSearchLocation(placeName);
                } else if ("골밭".equals(placeName) && RobotController.triggered2) {
                    teleportToItemSearchLocation(placeName);
                }
            }
        }

        // ✅ 타겟 탐색: 피격 대상 없고, 루팅/도주 상태가 아닐 경우만
        synchronized (this) {
            if (attacker == null && getAiStatus() != Lineage.AI_STATUS_PICKUP && getAiStatus() != Lineage.AI_STATUS_ESCAPE) {
                findTarget();
            }
        }

        // ✅ 장소 범위를 벗어난 경우 복귀 처리
        String placeName = getPlaceName(); // 위에서와 중복되지만 구조상 여기서도 필요
        if (placeName != null && isOutsidePlaceArea(placeName)) {
            // ✅ 마을에 있는 경우는 복귀하지 않음
            if (!isInVillage()) {
                moveToPlaceArea(placeName);
            }
        }

        // ✅ 랜덤 이동 조건: 타겟과 공격자가 없고, 루팅/도주 상태도 아닐 때
        if (target == null && attacker == null && getAiStatus() != Lineage.AI_STATUS_PICKUP && getAiStatus() != Lineage.AI_STATUS_ESCAPE) {
            // ✅ 마을에 있는 경우는 움직이지 않음
            if (!isInVillage()) {
                toRandomMovement();
            }
        }

        // ✅ 경로 초기화 (가끔 A* 경로 초기화)
        if (Util.random(0, 1) == 0) {
            clearAstarList();
        }
    }

	/**
	 * ✅ 타겟 아이템 탐색 및 설정
	 * - 기존 타겟이 유효하지 않으면 제거
	 * - 주변에서 가장 가까운 아이템을 새 타겟으로 지정
	 */
	private void findTarget() {
	    synchronized (this) {
	        if (target != null && !isPickupItem(target)) {
	            target = null;
	        }
	    }
	    processPickupItem();
	}

	/**
	 * ✅ 주변에 있는 줍을 수 있는 아이템 중 가장 가까운 것을 타겟으로 설정
	 */
	private void processPickupItem() {
	    try {
	        List<object> insideList = getInsideList();
	        if (insideList == null || insideList.isEmpty())
	            return;

	        object closestItem = insideList.stream()
	            .filter(this::isPickupItem)
	            .min(Comparator.comparingInt(o -> Util.getDistance(this, o)))
	            .orElse(null);

	        if (closestItem != null) {
	            synchronized (this) {
	                setTarget(closestItem);
	            }
	            setAiStatus(Lineage.AI_STATUS_PICKUP); // 줍기 상태 진입
	            return;
	            
	        }

	    } catch (Exception e) {
	        lineage.share.System.printf("[처리 오류] processPickupItem() - %s\r\n", e.toString());
	        e.printStackTrace();
	    }
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
	 * ✅ 타겟 유지 시간이 초과됐는지 검사하고, 초과 시 타겟 초기화
	 */
	private void checkTargetDuration() {
	    if (target != null) {
	        long elapsedTime = System.currentTimeMillis() - targetSetTime;
	        if (elapsedTime >= MAX_TARGET_DURATION) {
	            target = null;
	            targetSetTime = 0;
	        }
	    }
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

	/**
	 * ✅ 지정된 장소로 텔레포트 (즉시 이동)
	 * - ITEM_SEARCH_LOCATIONS 기반 좌표 사용
	 */
	private void teleportToItemSearchLocation(String placeName) {
	    if (placeName == null) return;

	    int[] coord = ITEM_SEARCH_LOCATIONS.get(placeName);
	    if (coord == null) {
	        System.println("⚠️ 등록되지 않은 장소: " + placeName);
	        return;
	    }

	    int x = coord[0];
	    int y = coord[1];
	    int mapId = coord[2];

	    setHomeX(x);
	    setHomeY(y);
	    setHomeMap(mapId);
	    toTeleport(x, y, mapId, true);
	    target = null;
	}

	/**
	 * ✅ 로봇이 지정된 장소 중심 좌표(ITEM_SEARCH_LOCATIONS) 기준으로 배회 이동
	 * - Lineage.SEARCH_LOCATIONRANGE 범위 내에서만 이동 허용
	 * - 허용되지 않은 경우 방향 보정 후 이동 시도
	 */
	protected void toRandomMovement() {
		// ✅ 랜덤 방향 설정 (0~7)
		setHeading(Util.random(0, 7));

		// 현재 방향 기준 이동 좌표 계산
		int x = Util.getXY(heading, true) + this.x;
		int y = Util.getXY(heading, false) + this.y;

		// 이동 기준이 될 장소명
		String placeName = getPlaceName();
		if (placeName == null || !ITEM_SEARCH_LOCATIONS.containsKey(placeName))
			return;

		int[] coord = ITEM_SEARCH_LOCATIONS.get(placeName);
		int px = coord[0];     // 기준 X
		int py = coord[1];     // 기준 Y
		int pmap = coord[2];   // 기준 맵 ID

		// ➤ 기준 위치에서 설정된 거리(Lineage.SEARCH_LOCATIONRANGE) 이상 벗어날 경우 → 방향 보정
		if (!Util.isDistance(x, y, map, px, py, pmap, Lineage.SEARCH_LOCATIONRANGE)) {
			heading = Util.calcheading(this, px, py); // 방향 보정
			x = Util.getXY(heading, true) + this.x;
			y = Util.getXY(heading, false) + this.y;
		}

		// ✅ 이동 가능 여부 확인
		boolean canMove = World.isThroughObject(this.x, this.y, this.map, heading)
				&& !World.isMapdynamic(x, y, map)
				&& !World.isNotAttackTile(x, y, map);

		// ✅ 이동 처리
		if (canMove) {
			toMoving(null, x, y, heading, false);
		}
	}
	
	/**
	 * ✅ 현재 위치가 해당 장소 기준으로 너무 멀리 벗어났는지 판단
	 * - 맵이 다르거나 x/y 거리 10칸 초과
	 */
	private boolean isOutsidePlaceArea(String placeName) {
	    int[] coord = ITEM_SEARCH_LOCATIONS.get(placeName);
	    if (coord == null) return false;

	    int targetX = coord[0];
	    int targetY = coord[1];
	    int targetMap = coord[2];

	    int dx = Math.abs(getX() - targetX);
	    int dy = Math.abs(getY() - targetY);

	    return (getMap() != targetMap || dx > 10 || dy > 10);
	}

	/**
	 * ✅ 사냥 장소 범위를 벗어났을 때 지정 좌표로 복귀 시도
	 * - 기본 이동 실패 시 8방향 탐색 및 주변 타일로 이동
	 * - 모든 시도 실패 시 홈으로 귀환
	 */
	private void moveToPlaceArea(String placeName) {
	    if (placeName == null) return;

	    int[] coord = ITEM_SEARCH_LOCATIONS.get(placeName);
	    if (coord == null) {
	        System.println("⚠️ 등록되지 않은 장소: " + placeName);
	        return;
	    }

	    int targetX = coord[0];
	    int targetY = coord[1];
	    int targetMap = coord[2];

	    // 충분히 가까우면 이동 생략
	    int dx = Math.abs(getX() - targetX);
	    int dy = Math.abs(getY() - targetY);
	    if (getMap() == targetMap && dx <= 10 && dy <= 10) return;

	    // 기본 이동 시도
	    int heading = Util.calcheading(getX(), getY(), targetX, targetY);
	    setHeading(heading);

	    if (World.isThroughObject(getX(), getY(), targetMap, heading)
	            && !World.isMapdynamic(targetX, targetY, targetMap)
	            && !World.isNotMovingTile(targetX, targetY, targetMap)) {
	        if (toMoving(this, targetX, targetY, 0, true)) return;
	    }

	    // 이동 실패 시 방향 변경 및 주변 좌표 탐색 (최대 10회)
	    for (int retry = 0; retry < 10; retry++) {
	        setHeading(Util.random(0, 7));

	        int newX = getX() + Util.getXY(getHeading(), true);
	        int newY = getY() + Util.getXY(getHeading(), false);
	        if (toMoving(this, newX, newY, 0, true)) return;

	        for (int dx2 = -1; dx2 <= 1; dx2++) {
	            for (int dy2 = -1; dy2 <= 1; dy2++) {
	                if (dx2 == 0 && dy2 == 0) continue;

	                int nearbyX = getX() + dx2;
	                int nearbyY = getY() + dy2;

	                if (!World.isMapdynamic(nearbyX, nearbyY, targetMap)
	                        && !World.isNotMovingTile(nearbyX, nearbyY, targetMap)
	                        && toMoving(this, nearbyX, nearbyY, 0, true)) {
	                    return;
	                }
	            }
	        }
	    }

	    // ✅ 최종 이동 실패 → 홈 복귀 시도
	    goToHome(false); // 비정상 상황 대응용 귀환
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
        target = tempTarget = attacker = null;
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

                        // ✅ 멘트 출력 (확률)
                        if (Util.random(1, 100) <= Lineage.robot_ment_probability && o instanceof ItemInstance) {
                            ItemInstance item = (ItemInstance) o;
                            RobotController.getRandomMentAndChat(
                                Lineage.AI_PICKUP_MENT,
                                this,
                                item,
                                item.getEnLevel(),
                                Lineage.CHATTING_MODE_NORMAL
                            );
                        }
                    }
                }
            }

            // ✅ 아이템 획득 후: 타겟 초기화 & 걷기 상태로 전환
            target = null;
            setAiStatus(Lineage.AI_STATUS_WALK);

            // ✅ 원래 지역으로 복귀 이동
            String placeName = getPlaceName();
            if (placeName != null) {
                moveToPlaceArea(placeName);
            }

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

	/**
	 * ✅ 간소화된 변신 처리
	 * - polyName 기반으로 변신 실행
	 * - 무기 속도 자동 적용
	 */
	private void toPolymorph() {
	    switch (step) {
	        case 0:
	            // ✅ 1~5초 사이 랜덤 시간 대기
	            if (polyTime == 0)
	                polyTime = System.currentTimeMillis() + (1000 * Util.random(1, 5));

	            // 시간 도달 시 다음 단계로
	            if (polyTime > 0 && polyTime <= System.currentTimeMillis())
	                step = 1;
	            break;

	        case 1:
	            // ✅ 변신 정보 설정 및 무기 속도 적용
	            String polyName = getPolymorph(); // 또는 getRankPolyName(), 상황에 따라 조정
	            if (polyName != null) {
	                Poly p = PolyDatabase.getName(polyName);
	                if (p != null) {
	                    PolyDatabase.toEquipped(this, p);
	                    setGfx(p.getGfxId());
	                    applyWeaponSpeed(p);
	                }
	            }

	            // ✅ 초기화
	            step = 0;
	            polyTime = 0;
	            pcrobot_mode = PCROBOT_MODE.None;
	            break;
	    }
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

    
    public boolean toMoving(object o, final int x, final int y, final int h, final boolean astar) {
        try {
            if (o == null)
                return false;
    
            if (astar) {
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
                    return true;
                } else {
                    if (o != null)
                        appendAstarList(o);
                    return false;
                }
            } else {
                toMoving(x, y, h);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            lineage.share.System.printf("[처리 오류] toMoving(object o, final int x, final int y, final int h, final boolean astar)\r\n : %s\r\n", e.toString());
        }
    
        return false;
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

	// ✅ 특정 장소 범위 내 PuRobotInstance 들 귀환 처리
	public static void sendRobotsHomeInArea(String placeName) {
		int[] area = PLACE_AREA_MAP.get(placeName);
		if (area == null) return;

		int startX = area[0];
		int endX = area[1];
		int startY = area[2];
		int endY = area[3];
		int mapId = area[4];

		for (object obj : World.getRobotList()) {
			if (obj instanceof PickupRobotInstance) {
				PickupRobotInstance robot = (PickupRobotInstance) obj;

				if (robot.getMap() == mapId) {
					int x = robot.getX();
					int y = robot.getY();

					if (x >= startX && x <= endX && y >= startY && y <= endY) {
						robot.goToHome(false);
					}
				}
			}
		}
	}
	
    protected void goToHome(boolean isCracker) {
        if (!LocationController.isTeleportVerrYedHoraeZone(this, true))
            return;
        
        if (!isCracker && World.isGiranHome(getX(), getY(), getMap()))
            return;
    
        target = tempTarget = attacker = null;
        clearAstarList();
        
        int[] home = Lineage.getHomeXY();
        setHomeX(home[0]);
        setHomeY(home[1]);
        setHomeMap(home[2]);		
        
        toTeleport(getHomeX(), getHomeY(), getHomeMap(), isDead() == false);
    }
    
    /**
     * 인벤토리 셋팅: 로봇 전투 시 자동 무기 장착.
     */
    public void setInventory() {
        if (!Lineage.robot_auto_pu) {
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
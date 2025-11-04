package goldbitna.robot;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_ObjectAction;
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
import lineage.world.controller.LocationController;
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
import lineage.world.object.item.Aden;
import lineage.world.object.magic.Detection;
import lineage.world.object.magic.EnergyBolt;
import lineage.world.object.monster.Harphy;
import lineage.world.object.monster.Spartoi;
import lineage.world.object.monster.StoneGolem;
import lineage.world.object.npc.guard.PatrolGuard;

public class Pk1RobotInstance extends RobotInstance {

    protected static final int ADEN_LIMIT          = 1000000000;    // 아데나 체크할 최소값 및 추가될 아데나 갯수.
    protected static final int HEALING_PERCENT     = 95;            // 체력 회복제를 복용할 시점 백분율
    protected static final int GOTOHOME_PERCENT    = 40;            // 체력이 해당퍼센트값보다 작으면 귀환함.
    protected static final int USABLE_MP_PERCENT   = 10;             // 해당 마나량이 해당 값보다 클때만 마법 사용
 // ✅ 타겟이 유지될 최대 시간 (예: 30초)
    private static final long MAX_TARGET_DURATION = 20_000; 
    
    // 보조 캐릭터의 수 (각 줄 10개)
    private static final int ASSISTANT_COUNT     = 10;
    
    // ✅ 단장 좌표 (X, Y, MAP)
    private static final int[][] MASTER_COORDS = {	{32613, 32836, 0}, {32897, 32652, 4} };
    
    // ✅ 각 줄의 시작 좌표 (X, Y, MAP)
    private static final int[][] TALKISLAND_ROW_COORDS = {
        {32610, 32830, 0}, // 1번째 줄
        {32610, 32831, 0}, // 2번째 줄
        {32609, 32832, 0}, // 3번째 줄
        {32609, 32833, 0}, // 4번째 줄
        {32608, 32834, 0}, // 5번째 줄
        {32608, 32835, 0}  // 6번째 줄
    };
 
    // ✅ 각 줄의 시작 좌표 (X, Y, MAP)
    private static final int[][] GLUDIN_ROW_COORDS = {
        {32900, 32650, 4}, // 1번째 줄
        {32900, 32651, 4}, // 2번째 줄
        {32899, 32652, 4}, // 3번째 줄
        {32899, 32653, 4}, // 4번째 줄
        {32898, 32654, 4}, // 5번째 줄
        {32898, 32655, 4}  // 6번째 줄
    };
    
    // PK 이동 경로
    private static final List<int[]> PK_ZONE_LOCATIONS = Arrays.asList(
        new int[]{32580, 32630, 32820, 32850, 0}, // PK 시작 영역 1
        new int[]{32835, 32910, 32680, 32690, 0}  // PK 종료 영역 2
    );
    
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
    public PCROBOT_MODE pcrobot_mode; 	 // 처리 모드.
    private int step;               	 // 일렬 동작 처리 시 스탭 변수.
	public volatile object target;  	 // 공격 대상  
	public volatile object tempTarget;   // 임시 대상
	private object currentAttackTarget;  // 현재 전투 중인 타겟 저장
    
    // 동기화를 위한 객체
    private Object sync_ai = new Object();
        
    // 시체 유지 및 재스폰 관련 변수
    private long ai_time_temp_1;
    private long delayTime;
    public long teleportTime;
 // ✅ 타겟이 설정된 시점을 저장하는 변수
    private long targetSetTime = 0;
     
    // 로봇 행동 상태 변수
    public String action;
    
    private static boolean allEntitiesInitialized = false;  // 모든 개체가 배치 완료되었는지 여부
    
    // 리로드 확인용.
    public boolean isReload;     
    
    public Pk1RobotInstance() {
        aStar = new AStar();
        iPath = new int[2];
        astarList = new ArrayList<object>();
        temp_list = new ArrayList<object>();
        target = tempTarget = currentAttackTarget = null;
    }
    
    @Override
    public void close() {
        super.close();
        if (getInventory() != null) {
            for (ItemInstance ii : getInventory().getList())
                ItemDatabase.setPool(ii);
            getInventory().clearList();
        }
        weapon_name = null;
        weapon = null;
        action = null;
        target = tempTarget = currentAttackTarget = null;
        teleportTime = delayTime = ai_time_temp_1 = targetSetTime = weaponEn = step = 0;
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
    
    public synchronized object getTarget() {
        return target;
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
        // ✅ 모든 개체 초기화 상태 리셋
        allEntitiesInitialized = false;
        // 메모리 세팅
        World.appendRobot(this);
        // 컨트롤러 호출
        BookController.toWorldJoin(this);
        CharacterController.toWorldJoin(this);
        BuffController.toWorldJoin(this);
        SkillController.toWorldJoin(this);
    
        // 인벤토리 셋팅 (자동 무기 장착)
        setInventory();   
        
        // 인공지능 활성화를 위해 AiThread에 등록
        AiThread.append(this);
    }
    
    @Override
    public void toWorldOut() {
        super.toWorldOut();
        setAiStatus(Lineage.AI_STATUS_DELETE);
        // ✅ 모든 개체 초기화 상태 리셋
        allEntitiesInitialized = false;
        toReset(true);
        World.removeRobot(this);        
        BookController.toWorldOut(this);
        CharacterController.toWorldOut(this);
        BuffController.toWorldOut(this);
        SkillController.toWorldOut(this);
        close();
    }
    
    @Override
    public void toRevival(object o) {
        if (isDead()) {
            super.toReset(false);            
            target = tempTarget = currentAttackTarget = null;
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
        // 1. 기본 대미지 처리
        super.toDamage(cha, dmg, type);

        // 2. 유효성 검사
        if (cha == null || cha.getObjectId() == getObjectId() || dmg <= 0 || cha.getGm() > 0) {
            return; // 자기 자신이거나 GM이거나 무효한 공격일 경우 무시
        }

        // 3. A* 경로 최적화를 위해 공격자 제거
        removeAstarList(cha);

        // 4. object 타입으로 안전하게 변환
        if (!(cha instanceof object)) {
            return;
        }
        object o = (object) cha;

        // 5. 타겟이 없고 공격 가능한 대상일 경우 타겟으로 설정
        if (currentAttackTarget != o && isAttack(o, true)) {
            setTarget(o);
        }

        // 6. 근처 동료(PkRobotInstance_1)들에게도 타겟 공유 요청
        for (object obj : getInsideList()) {
            if (obj instanceof Pk1RobotInstance) {
                Pk1RobotInstance ally = (Pk1RobotInstance) obj;
                ally.toDamage(cha); // 해당 동료의 타겟이 비어있다면 설정됨
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
			if (isReload)
				return;

			if (isDead()) {
				if (ai_time_temp_1 == 0)
					ai_time_temp_1 = time;
				if (ai_time_temp_1 + Lineage.ai_robot_corpse_time > time)
					return;

				RobotController.toWorldOut(this);
			}

			if (getInventory() == null)
				return;
		}
        
        if (getMpPercent() < USABLE_MP_PERCENT) {
            if (Util.random(1, 100) <= Lineage.robot_ment_probability) {
                // ✅ 마나가 부족할 시 멘트 출력 (target이 null인 경우 안전한 값 전달)
                object safeTarget = (target != null) ? target : this; 
                RobotController.getRandomMentAndChat(
                    Lineage.AI_LOW_MANA_MENT, this, safeTarget, Lineage.CHATTING_MODE_NORMAL, Lineage.AI_LOW_MANA_MENT_DELAY
                );
            }
        }
        
		// ✅ 3. 타겟 유지 시간 검사 (초과 시 초기화)
		checkTargetDuration();

		synchronized (this) {
		    switch (getAiStatus()) {
		        case Lineage.AI_STATUS_WALK:
		            // 👉 상태가 WALK일 때 타겟이 존재하면 ATTACK 상태로 전환하고 currentAttackTarget에 복사
		            if (target != null) {
		                setAiStatus(Lineage.AI_STATUS_ATTACK);
		                currentAttackTarget = target;
		                target = null; // 이후는 currentAttackTarget만 사용
		            }
		            // ✅ target이 null인데 currentAttackTarget은 유효한 경우 → ATTACK으로 진입
		            else if (currentAttackTarget != null) {
		                setAiStatus(Lineage.AI_STATUS_ATTACK);
		            }
		            break;

		        case Lineage.AI_STATUS_ATTACK:
		            // 👉 공격 상태일 땐 currentAttackTarget 기준으로 유효성 검사
		            if (currentAttackTarget != null) {
		                currentAttackTarget = checkTargetValidity(currentAttackTarget); // 유효하지 않으면 null 반환됨
		            }

		            // 👉 currentAttackTarget이 없으면 다시 걷기 상태로 전환
		            if (currentAttackTarget == null) {
		                setAiStatus(Lineage.AI_STATUS_WALK);
		            }
		            break;
		    }
		}

		synchronized (this) {
			if (pcrobot_mode == PCROBOT_MODE.None && !getInventory().isWeightPercent(82)) {
				pcrobot_mode = PCROBOT_MODE.InventoryHeavy;
			}
		}

		synchronized (this) {
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

		super.toAi(time);
	}
    
	@Override
	protected void toAiWalk(long time) {
	    super.toAiWalk(time);

	    // ✅ 초기 배치가 필요한 경우만 수행
	    boolean shouldInitialize = !allEntitiesInitialized &&
	        ((RobotController.isTalkIsland && !isTalkIsland()) ||
	         (!RobotController.isTalkIsland && !isSkeletonField()));

	    if (shouldInitialize) {
	        initializeEntities();
	    }

	    // ✅ 잘못된 타겟 제거 및 동료 타겟 해제
	    clearSharedTargetIfInvalid();

	    // ✅ 타겟이 없으면 탐색 시도
	    if (currentAttackTarget == null) {
	        findTarget();
	    }

	    // ✅ 랜덤 멘트 출력 (확률 기반)
	    if (target != null && Util.random(1, 100) <= Lineage.robot_ment_probability) {
	        sendRandomChatMessageForTarget();
	    }

	    // ✅ 타겟이 없고, PK 구역에 없다면 → 복귀 이동
	    if (target == null && !isPkZoneLocations(RobotController.isTalkIsland ? 0 : 1)) {
//	        moveToPkLocation();
	    }

	    // ✅ A* 경로 캐시 정리 (50% 확률)
	    if (Util.random(0, 1) == 0) {
	        clearAstarList();
	    }
	}

    /**
     * ✅ 랜덤 채팅 메시지를 전송하는 메서드. (PcInstance 및 PcRobotInstance 대상)
     */
    private void sendRandomChatMessageForTarget() {
        // ✅ 타겟이 없거나, 객체 이름이 없는 경우 실행할 필요 없음
        if (target == null || getName() == null) {
            return;
        }

        // ✅ 타겟이 PcInstance 또는 PcRobotInstance가 아니면 실행하지 않음
        if (!(target instanceof PcInstance) && !(target instanceof PcRobotInstance)) {
            return;
        }

        // ✅ "단장" 여부 판단 (한 번만 검사하여 효율성 향상)
        boolean isMaster = getName().contains("단장");

        // ✅ 적절한 멘트 전송
        RobotController.getRandomMentAndChat(
            isMaster ? Lineage.AI_EPK_MASTER_MENT : Lineage.AI_EPK_MEMBER_MENT,
            this,
            target,
            Lineage.CHATTING_MODE_NORMAL,
            isMaster ? Lineage.AI_EPK_MASTER_MENT_DELAY : Lineage.AI_EPK_MEMBER_MENT_DELAY
        );
    }

    /**
     * ✅ 개체 초기화 및 배치 실행
     */
	private void initializeEntities() {
		synchronized (this) {
			// ✅ 이미 모든 개체가 배치되었다면 더 이상 실행하지 않음
			if (allEntitiesInitialized) {
				return;
			}
		}

		// ✅ 기존 타겟 정보 초기화
		target = null;
		tempTarget = null;

		// ✅ "PK단"이 포함된 경우, 특정 위치로 즉시 이동 후 종료
		if (getName() != null && getName().contains("PK단")) {
			// ✅ MASTER_COORDS 선택
			int[] masterCoords = (RobotController.isTalkIsland ? MASTER_COORDS[0] : MASTER_COORDS[1]);
			toTeleport(masterCoords[0], masterCoords[1], masterCoords[2], false);
			checkEntitiesInitialization(); // ✅ 전체 개체 배치 여부 확인
			return;
		}

		// ✅ 전체 객체 배치 (각 줄의 좌표를 순회하면서 개체 생성)
		// isTalkIsland가 true이면 TALKISLAND_ROW_COORDS, false이면 GLUDIN_ROW_COORDS
		// 사용
		int[][] rowCoords = (RobotController.isTalkIsland ? TALKISLAND_ROW_COORDS : GLUDIN_ROW_COORDS);
		for (int[] coords : rowCoords) {
			spawnEntityRow(coords[0], coords[1], coords[2], ASSISTANT_COUNT);
		}
		// ✅ 개체 배치 완료 여부 확인
		checkEntitiesInitialization();
	}

    /**
     * ✅ 한 줄에 10개씩 개체 생성 (X 좌표를 각각 +1씩 증가)
     *
     * - 한 줄에 `count` 개체를 배치 (기본적으로 10개)
     * - 개체가 이미 존재하는 경우 배치를 건너뜀
     * - 이동할 수 없는 경우 좌표를 조정
     *
     * @param startX 시작 X 좌표
     * @param startY 시작 Y 좌표
     * @param map 맵 ID
     * @param count 생성할 개체 수 (한 줄에 배치할 개체 수)
     */
    private void spawnEntityRow(int startX, int startY, int map, int count) {
        for (int i = 0; i < count; i++) {
            int locX = startX + i; // X 좌표 증가
            int locY = startY; // Y 좌표 고정 (대각선 이동 방지)

            // ✅ 해당 좌표가 비어 있는지 확인
            if (World.getMapdynamic(locX, locY, map) != 0) {
                continue; // 이동할 수 없는 경우 건너뛰기
            }

            // ✅ 개체를 해당 위치로 이동
            toTeleport(locX, locY, map, false);
        }
    }

    /**
     * ✅ 모든 개체가 정상적으로 배치되었는지 확인하고 `allEntitiesInitialized`를 설정
     */
    private synchronized void checkEntitiesInitialization() {
        int currentUserCount = RobotController.isTalkIsland ? getUserCountInisTalkIsland() : getUserCountInisSkeletonField();
        // 전체 개체 수와 현재 PK 구역 내 개체 수가 동일하면 초기화 완료
        if (currentUserCount >= RobotController.list_pk1.size()) {
            allEntitiesInitialized = true;
        }
    }


    /**
     * ✅ 비동기로 실행되는 타겟 탐색 메서드
     */
    private void findTarget() {
        synchronized (this) {
            // ✅ 기존 타겟이 상태 확인 후 초기화
            if (target != null && shouldResetTarget(target)) {
                target = null; // 타겟 초기화
            }
        }
        processInsideList(); // 새로운 타겟 탐색
    }
    
    /**
     * ✅ 일반적인 대상 탐색 로직 (공유된 타겟 우선)
     */
    private void processInsideList() {
        try {
            List<object> insideList = getInsideList();
            if (insideList == null || insideList.isEmpty()) return;

            // ✅ 1. 공유된 타겟 확인 (이미 타겟을 설정한 객체가 있으면 공유)
            object sharedTarget = insideList.stream()
                .filter(o -> o instanceof Pk1RobotInstance)
                .map(o -> (Pk1RobotInstance) o)
                .filter(pk -> pk.getTarget() != null && isAttack(pk.getTarget(), true))
                .map(Pk1RobotInstance::getTarget)
                .findFirst()
                .orElse(null);

            if (sharedTarget != null) {
                assignSharedTarget(sharedTarget, insideList);
                return;
            }

            // ✅ 2. 특정 객체 인스턴스만 필터링하여 가장 가까운 적 탐색
            object closestTarget = insideList.stream()
                .filter(o -> isValidInstance(o)) // 특정 인스턴스 확인
                .filter(o -> Util.isAreaAttack(this, o) && isAttack(o, true))
                .min(Comparator.comparingInt(o -> Util.getDistance(this, o)))
                .orElse(null);

            if (closestTarget != null) {
                assignSharedTarget(closestTarget, insideList);
            }
        } catch (Exception e) {
            lineage.share.System.printf("[처리 오류] processInsideList() - %s\r\n", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * ✅ 특정 인스턴스 타입만 유효한 타겟으로 판단
     */
    private boolean isValidInstance(object o) {
        return (o instanceof PcInstance || o instanceof PcRobotInstance || o instanceof MonsterInstance) && !(o instanceof PickupRobotInstance);
    }

	/**
	 * ✅ 공유된 타겟을 배정하는 헬퍼 메서드
	 */
	private void assignSharedTarget(object newTarget, List<object> insideList) {
	    synchronized (this) {
	        setTarget(newTarget);
	    }
	    insideList.stream()
	        .filter(o -> o instanceof Pk1RobotInstance)
	        .map(o -> (Pk1RobotInstance) o)
	        .filter(pk -> pk.getTarget() == null && isAttack(newTarget, true))
	        .forEach(pk -> pk.setTarget(newTarget));
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
	 * ✅ 타겟 유지 시간을 검사하고, 일정 시간이 지나면 초기화
	 */
	private void checkTargetDuration() {
	    if (currentAttackTarget != null) {
	        long elapsedTime = System.currentTimeMillis() - targetSetTime;
	        if (elapsedTime >= MAX_TARGET_DURATION) {
	            target = currentAttackTarget = null;
	            targetSetTime = 0; // ✅ 초기화
	        }
	    }
	}

	/**
	 * ✅ 공격자가 사라졌을 때 주변 동료도 함께 타겟 해제 (협동 전투 종료)
	 */
	private void clearSharedTargetIfInvalid() {
	    // 대상이 없거나 삭제되었거나 죽었을 경우
	    if (target == null || target.isWorldDelete() || target.isDead()) {
	        // 본인 타겟 해제
	        setTarget(null);

	        // 주변에 있는 같은 타입(PkRobotInstance_1)의 동료들에게도 타겟 해제 요청
	        for (object obj : getInsideList()) {
	            if (obj instanceof Pk1RobotInstance) {
	                Pk1RobotInstance ally = (Pk1RobotInstance) obj;

	                // 같은 타겟을 공유하고 있다면 해제
	                if (ally.getTarget() == target) {
	                    ally.setTarget(null);
	                }
	            }
	        }
	    }
	}

	
	private synchronized void moveToPkLocation() {
	    int zoneIndex = RobotController.isTalkIsland ? 0 : 1;
	    if (!isPkZoneLocations(zoneIndex)) {
	        moveToPkZone(zoneIndex);
	    }
	}

    
    private synchronized void moveToPkZone(int pkZoneIndex) {
        if (pkZoneIndex < 0 || pkZoneIndex >= PK_ZONE_LOCATIONS.size()) {
            return;
        }
    
        int[] pkZone = PK_ZONE_LOCATIONS.get(pkZoneIndex);
        int x1 = pkZone[0];
        int x2 = pkZone[1];
        int y1 = pkZone[2];
        int y2 = pkZone[3];
        int map = pkZone[4];
    
        int randomX = x1 + (int) (Math.random() * (x2 - x1 + 1));
        int randomY = y1 + (int) (Math.random() * (y2 - y1 + 1));
    
        int heading = Util.calcheading(this.x, this.y, randomX, randomY);
        setHeading(heading);
    
        boolean isMovableTile = World.isThroughObject(this.x, this.y, map, heading)
                && !World.isMapdynamic(randomX, randomY, map)
                && !World.isNotMovingTile(randomX, randomY, map);
    
        if (isMovableTile) {
            if (toMoving(this, randomX, randomY, 0, true)) {
                return;
            }
        }
    
        int maxRetry = 10;
        int retryCount = 0;
    
        do {
            switch (Util.random(0, 7)) {
                case 2:
                    setHeading(getHeading() + 1);
                    break;
                case 6:
                    setHeading(getHeading() - 1);
                    break;
                case 7:
                    setHeading(Util.random(0, 7));
                    break;
                default:
                    break;
            }
    
            int newX = Util.getXY(getHeading(), true) + this.x;
            int newY = Util.getXY(getHeading(), false) + this.y;
    
            if (toMoving(this, newX, newY, 0, true)) {
                return;
            }
    
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    if (offsetX == 0 && offsetY == 0) {
                        continue;
                    }
    
                    int nearbyX = this.x + offsetX;
                    int nearbyY = this.y + offsetY;
    
                    if (World.isThroughObject(this.x, this.y, map, heading)
                            && !World.isMapdynamic(nearbyX, nearbyY, map)
                            && !World.isNotMovingTile(nearbyX, nearbyY, map)
                            && toMoving(this, nearbyX, nearbyY, 0, true)) {
                        return;
                    }
                }
            }
    
            retryCount++;
        } while (retryCount < maxRetry);
    
        if (!isPkZoneLocations(pkZoneIndex)) {
//          System.println("PK 구역 이동 실패: " + pkZoneIndex);
        }
    }
    
    public boolean isPkZoneLocations(int idx) {
        if (idx < 0 || idx >= PK_ZONE_LOCATIONS.size()) {
            return false;
        }
    
        int x = this.getX();
        int y = this.getY();
        int map = this.getMap();
    
        int[] loc = PK_ZONE_LOCATIONS.get(idx);
    
        return (loc[0] <= x && x <= loc[1] &&
                loc[2] <= y && y <= loc[3] &&
                loc[4] == map);
    }
    
    public boolean isTalkIsland() {    
        return this.getX() >= 32256 && this.getX() <= 32767 &&
               this.getY() >= 32768 && this.getY() <= 33279 &&
               this.getMap() == 0;
    }
    
    public boolean isSkeletonField() {    
        return this.getX() >= 32708 && this.getX() <= 32930 &&
               this.getY() >= 32590 && this.getY() <= 32740 &&
               this.getMap() == 4;
    }   

    @Override
    protected void toAiAttack(long time) {
        try {
            // 🔹 현재 전투 중인 타겟 기준
            object o = checkTargetValidity(currentAttackTarget);
            if (o == null) {
                clearTarget();
                return;
            }

            // 🔹 타겟 리셋 조건 검사
            if (shouldResetTarget(o)) {
                clearTarget();
                return;
            }

            // 🔹 투명 상태 감지
            if (o.isInvis() && Util.random(0, 100) <= 90) {
                toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this,
                        Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
                Detection.onBuff(this, SkillDatabase.find(2, 4));
                ai_time = SpriteFrameDatabase.getGfxFrameTime(
                        this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION
                );
                return;
            }

            // 🔹 기본 공격 스킬 설정
            Skill skill = SkillDatabase.find(1, 3); // 기본: 에너지 볼트

            if (getName().contains("PK단") && Util.random(1, 100) <= 1) {
                Skill disintegrate = SkillDatabase.find(10, 4);
                if (disintegrate != null)
                    skill = disintegrate;
            } else if (Util.random(1, 100) <= 10) {
                Skill eruption = SkillDatabase.find(6, 4);
                if (eruption != null)
                    skill = eruption;
            }

            // 🔹 스킬 사거리 확인
            int skillRange = (skill != null) ? skill.getDistance() : 15;
            boolean canCast = Util.isDistance(this, o, skillRange) && isAttack(o, true);

            // 🔹 스킬 사용 시도
            if (canCast) {
                boolean magicUsed = toSkillAttack(o, skill);
                if (!magicUsed) {
                    clearTarget(); // 실패 시 타겟 리셋
                }
            } else {
                clearTarget(); // 사거리 밖이면 타겟 리셋
            }

        } catch (Exception e) {
            e.printStackTrace();
            lineage.share.System.printf("[처리 오류] toAiAttack(long time)\r\n : %s\r\n", e.toString());
        }
    }
   
    private void moveToTargetForMagic(object o, int skillRange) {
        if(o == null) {
            // o가 null인 경우 처리 로직 (예: target 초기화, 오류 로그 기록 등)
            target = null;
            return;
        }
        
        if (!Util.isDistance(this, o, skillRange)) {
            int heading = Util.calcheading(this.getX(), this.getY(), o.getX(), o.getY());
            setHeading(heading);
        
            if (!toMoving(this, o.getX(), o.getY(), 0, true)) {
                target = null;
            }
        }
    }

    
    private object checkTargetValidity(object o) {
        if (o == null || o.isDead() || o.isWorldDelete()
            || !isAttack(o, false)
            || !Util.isAreaAttack(this, o)
            || !Util.isAreaAttack(o, this)) {
            return null;
        }
        return o;
    }

    private void clearTarget() {
        target = null;
        currentAttackTarget = null;
    }

    @Override
    protected void toAiDead(long time) {
        super.toAiDead(time);
    
        ai_time_temp_1 = 0;
        target = tempTarget = currentAttackTarget = null;
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
    
    @Override
    protected void toAiPickup(long time) {
        object o = null;
    
        for (object oo : getInsideList()) {
            if (oo instanceof Aden) { 
                if (o == null)
                    o = oo;
                else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
                    o = oo;
            }
        }
    
        if (o == null) {
            setAiStatus(Lineage.AI_STATUS_WALK);
            return;
        }
    
        if (Util.isDistance(this, o, 1)) {
            super.toAiPickup(time);
            synchronized (o.sync_pickup) {
                if (!o.isWorldDelete()) {
                    getInventory().toPickup(o, o.getCount());
                }
            }
        } else {
            ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
            toMoving(o, o.getX(), o.getY(), 0, true);
        }
    }
    
    private boolean isAttack(object o, boolean walk) {
        if (o == null || o.getGm() > 0 || o.isDead() || o.isTransparent()) {
            return false;
        }
    
        if (o instanceof Pk1RobotInstance || o instanceof PickupRobotInstance)
            return false;
        
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
     * 로봇 또는 몬스터의 스킬 공격 처리
     * 
     * @param o      공격 대상 객체
     * @param skill  사용할 스킬 객체
     * @return       스킬 시전 성공 여부
     */
    protected boolean toSkillAttack(object o, Skill skill) {
        // ✅ 유효성 검사
        if (o == null || o.isDead() || skill == null) {
            return false;
        }

        // ✅ 마나 부족 시 실패
        if (getMpPercent() < USABLE_MP_PERCENT) {
            return false;
        }

        // ✅ 마법 쿨타임 확인
        if (System.currentTimeMillis() < delay_magic) {
            return false;
        }

        // ✅ 사거리 밖이면 실패
        if (!Util.isDistance(this, o, skill.getDistance())) {
            return false;
        }

        // ✅ 시전 액션 패킷 전송 (시전 애니메이션)
        toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, Lineage.GFX_MODE_SPELL_DIRECTION), true);

        // ✅ 스킬 UID 기준으로 분기 처리
        // ※ EnergyBolt 클래스에서 디스/이럽션도 함께 처리하므로 Disintegrate는 필요 없음
        switch (skill.getUid()) {
            case 77: // 디스인티그레이트
            case 45: // 이럽션
            case 4:  // 에너지 볼트 (기본)
            default:
                EnergyBolt.init(this, skill, (int) (o.getObjectId() & 0xFFFFFFFF));
                break;
        }

        // ✅ AI 행동 지연 시간 설정
        ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_DIRECTION);

        // ✅ 다음 마법 사용까지의 딜레이 적용
        delay_magic = System.currentTimeMillis() + skill.getDelay();

        return true;
    }
    
    protected void goToHome(boolean isCracker) {
        if (!LocationController.isTeleportVerrYedHoraeZone(this, true))
            return;
        
        if (!isCracker && World.isGiranHome(getX(), getY(), getMap()))
            return;
    
        target = tempTarget = currentAttackTarget = null;
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
    private void setInventory() {
        if (!Lineage.robot_auto_pk) {
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
            System.println("No valid weapon found for robot: " + getName());
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
     * Pk1RobotInstance 객체가 공격받았을 때,
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
    
    /**
     * Talk Island 영역 내에 존재하는 PcRobotInstance 객체의 수를 반환합니다.
     * Talk Island 좌표: x: 32256 ~ 32767, y: 32768 ~ 33279, map: 0
     *
     * @return Talk Island 내의 객체 수
     */
    public static synchronized int getUserCountInisTalkIsland() {
        // Talk Island 영역의 좌표 설정
        int x1 = 32256;
        int x2 = 32767;
        int y1 = 32768;
        int y2 = 33279;
        int map = 0;
        int count = 0;
        
        // PkRobotInstance_1 리스트 순회: 해당 영역에 속하는 객체 수를 카운트
        for (Pk1RobotInstance robot : RobotController.getPkRobotList()) {
            if (robot == null) {
                continue;
            }
            
            if (robot.getX() >= x1 && robot.getX() <= x2 &&
                robot.getY() >= y1 && robot.getY() <= y2 &&
                robot.getMap() == map) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Talk Island 영역 내에 존재하는 PcRobotInstance 객체의 수를 반환합니다.
     * Talk Island 좌표: x: 32256 ~ 32767, y: 32768 ~ 33279, map: 0
     *
     * @return Talk Island 내의 객체 수
     */
    public static synchronized int getUserCountInisSkeletonField() {
        // SkeletonField 영역의 좌표 설정
        int x1 = 32708;
        int x2 = 32930;
        int y1 = 32590;
        int y2 = 32740;
        int map = 4;
        int count = 0;
        
        // PkRobotInstance_1 리스트 순회: 해당 영역에 속하는 객체 수를 카운트
        for (Pk1RobotInstance robot : RobotController.getPkRobotList()) {
            if (robot == null) {
                continue;
            }
            
            if (robot.getX() >= x1 && robot.getX() <= x2 &&
                robot.getY() >= y1 && robot.getY() <= y2 &&
                robot.getMap() == map) {
                count++;
            }
        }
        
        return count;
    }
}
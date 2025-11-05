package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import goldbitna.RobotSpawnLocation;
import goldbitna.robot.PartyRobotInstance;
import goldbitna.robot.Pk1RobotInstance;
import goldbitna.robot.controller.RobotConversationController;
import goldbitna.robot.PickupRobotInstance;
import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.bean.database.Poly;
import lineage.bean.database.RobotDrop;
import lineage.bean.database.RobotMent;
import lineage.bean.database.RobotPoly;
import lineage.bean.database.Shop;
import lineage.bean.database.Skill;
import lineage.bean.database.SkillRobot;
import lineage.bean.lineage.Book;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.bean.lineage.Party;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.thread.RobotMentQueueThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.robot.buff.BuffRobotInstance;
import lineage.world.object.robot.buff.BuffRobotInstance1;
import lineage.world.object.robot.buff.BuffRobotInstance2;
import lineage.world.object.robot.buff.BuffRobotInstance3;
import lineage.world.object.robot.buff.BuffRobotInstance4;

public class RobotController {
	// 기란
	static private List<BuffRobotInstance> list_buff;
	// 화말
	static private List<BuffRobotInstance1> list_buff1;
	// 글말
	static private List<BuffRobotInstance2> list_buff2;
	// 은말
	static private List<BuffRobotInstance3> list_buff3;
	// 말섬
	static private List<BuffRobotInstance4> list_buff4;
	
	// 에볼 피케이단 케릭 관리 목록.
	static public List<Pk1RobotInstance> list_pk1;
	private static List<Pk1RobotInstance> pool_pk1;
	
	// 먹자 케릭 관리 목록.
	static public List<PickupRobotInstance> list_pickup;
	private static List<PickupRobotInstance> pool_pickup;

	// 파티 케릭 관리 목록.
	static public List<PartyRobotInstance> list_party;
	private static List<PartyRobotInstance> pool_party;
	
	// 무인케릭 관리 목록.
	static public List<PcRobotInstance> list_pc;
	private static List<PcRobotInstance> pool_pc;
	// 변신 리스트.
	private static List<RobotPoly> list_poly;
	private static Map<Long, Clan> list_clan;
	// 로봇이 드랍하게될 아이템 목록.
	private static Map<String, List<RobotDrop>> list_drop;
	// 로봇 멘트
	static public final Map<Integer, List<String>> list_ment = new HashMap<>();	
	
	// 등록된 로봇들 관리 맵
	private static final Map<PcInstance, PartyRobotInstance> partyRobotMap = new ConcurrentHashMap<>();
	private static final Map<PartyRobotInstance, PcInstance> robotToPcMap = new ConcurrentHashMap<>();
    
	// 스폰된 로봇 개체수
	static public int count;
	private static int lastRandomCase = -1; // 마지막 선택된 케이스를 저장하는 변수
    // PK 장소 확인용.
    public static boolean isTalkIsland;  
    
	// 하루 내 랜덤 트리거 시각(밀리초 단위)와 실행 여부 플래그
	private static long triggerTime1 = 0;
	private static long triggerTime2 = 0;
	public static boolean triggered1 = false;
	public static boolean triggered2 = false;
	public static long trigger1ActivationTime = 0;
	public static long trigger2ActivationTime = 0;
	public static long lastGlobalTriggerTime = -1;
	private static int lastDay = -1; // 마지막으로 트리거를 초기화한 날짜
	// 마지막으로 실행된 이벤트 시간을 저장
	private static int lastTriggerHour1 = -1; // 이벤트1 마지막 실행 시간
	private static int lastTriggerHour2 = -1; // 이벤트2 마지막 실행 시간
	private static long lastCleanup = 0;
	
	// ======= KENT 공통 =======
	private static final int KENT_MAP_ID = 4;
	private static final int KENT_HEADING = 2;
	
	// ======= ORCISH 공통 =======
	private static final int ORCISH_MAP_ID = 4;
	private static final int ORCISH_HEADING = 2;
	
	// ======= GIRAN 공통 =======
	private static final int GIRAN_MAP_ID = 4;
	private static final int GIRAN_HEADING = 0;
	
	// ======= HEINE 공통 =======
	private static final int HEINE_MAP_ID = 4;
	private static final int HEINE_HEADING = 0;
	
	public enum Shape {
	    DIAMOND,
	    SQUARE
	}

	// ======== 켄트 좌표 설정 ========
	private static final int[] KENT_ROYAL_CENTER = { 33169, 32773, 4 };
	private static final int[] KENT_KNIGHT_CENTER = { 33169, 32773, 4 };
	private static final int[] KENT_ELF_CENTER = { 33169, 32773, 4 };
	private static final int[] KENT_WIZARD_CENTER = { 33169, 32773, 4 };

	private static final Shape KENT_ROYAL_SHAPE = Shape.SQUARE;
	private static final Shape KENT_KNIGHT_SHAPE = Shape.SQUARE;
	private static final Shape KENT_ELF_SHAPE = Shape.SQUARE;
	private static final Shape KENT_WIZARD_SHAPE = Shape.SQUARE;

	private static final int KENT_ROYAL_DISTANCE = 1;
	private static final int KENT_ROYAL_DEPTH = 1;
	private static final int KENT_KNIGHT_DISTANCE = 4;
	private static final int KENT_KNIGHT_DEPTH = 1;
	private static final int KENT_ELF_DISTANCE = 3;
	private static final int KENT_ELF_DEPTH = 1;
	private static final int KENT_WIZARD_DISTANCE = 2;
	private static final int KENT_WIZARD_DEPTH = 1;
	
	private static final int KENT_ROYAL_HEADING = 5;
	private static final int KENT_OTHER_HEADING = 5;
	
	// ======== 오크 좌표 설정 ========
	private static final int[] ORCISH_ROYAL_CENTER = { 32800, 32290, 4 };
	private static final int[] ORCISH_KNIGHT_CENTER = { 32800, 32290, 4 };
	private static final int[] ORCISH_ELF_CENTER = { 32800, 32290, 4 };
	private static final int[] ORCISH_WIZARD_CENTER = { 32800, 32290, 4 };

	private static final Shape ORCISH_ROYAL_SHAPE = Shape.SQUARE;
	private static final Shape ORCISH_KNIGHT_SHAPE = Shape.SQUARE;
	private static final Shape ORCISH_ELF_SHAPE = Shape.SQUARE;
	private static final Shape ORCISH_WIZARD_SHAPE = Shape.SQUARE;

	private static final int ORCISH_ROYAL_DISTANCE = 1;
	private static final int ORCISH_ROYAL_DEPTH = 1;
	private static final int ORCISH_KNIGHT_DISTANCE = 4;
	private static final int ORCISH_KNIGHT_DEPTH = 1;
	private static final int ORCISH_ELF_DISTANCE = 3;
	private static final int ORCISH_ELF_DEPTH = 1;
	private static final int ORCISH_WIZARD_DISTANCE = 2;
	private static final int ORCISH_WIZARD_DEPTH = 1;
	
	private static final int ORCISH_ROYAL_HEADING = 5;
	private static final int ORCISH_OTHER_HEADING = 5;
	
	// ======== 기란 좌표 설정 ========
	// 중심 좌표
	private static final int[] GIRAN_ROYAL_CENTER = { 33631, 32678, 4 };
	private static final int[] GIRAN_KNIGHT_CENTER = { 33631, 32678, 4 };
	private static final int[] GIRAN_ELF_CENTER = { 33631, 32678, 4 };
	private static final int[] GIRAN_WIZARD_CENTER = { 33631, 32678, 4 };

	// Shape
	private static final Shape GIRAN_ROYAL_SHAPE = Shape.SQUARE;
	private static final Shape GIRAN_KNIGHT_SHAPE = Shape.DIAMOND;
	private static final Shape GIRAN_ELF_SHAPE = Shape.SQUARE;
	private static final Shape GIRAN_WIZARD_SHAPE = Shape.SQUARE;

	// 거리 및 겹수
	private static final int GIRAN_ROYAL_DISTANCE = 1; // 수호탑 기준 거리
	private static final int GIRAN_ROYAL_DEPTH = 1;    // 배치 겹수
	private static final int GIRAN_KNIGHT_DISTANCE = 5;
	private static final int GIRAN_KNIGHT_DEPTH = 1;
	private static final int GIRAN_ELF_DISTANCE = 2;
	private static final int GIRAN_ELF_DEPTH = 2;
	private static final int GIRAN_WIZARD_DISTANCE = 2;
	private static final int GIRAN_WIZARD_DEPTH = 1;

	// heading
	private static final int GIRAN_ROYAL_HEADING = 5;
	private static final int GIRAN_OTHER_HEADING = 5;

	// ======== 하이네 좌표 설정 ========
	// 중심 좌표
	private static final int[] HEINE_ROYAL_CENTER = { 33524, 33396, 4 };
	private static final int[] HEINE_KNIGHT_CENTER = { 33524, 33396, 4 };
	private static final int[] HEINE_ELF_CENTER = { 33524, 33396, 4 };
	private static final int[] HEINE_WIZARD_CENTER = { 33524, 33396, 4 };

	// Shape
	private static final Shape HEINE_ROYAL_SHAPE = Shape.DIAMOND;
	private static final Shape HEINE_KNIGHT_SHAPE = Shape.DIAMOND;
	private static final Shape HEINE_ELF_SHAPE = Shape.DIAMOND;
	private static final Shape HEINE_WIZARD_SHAPE = Shape.DIAMOND;

	// 거리 및 겹수
	private static final int HEINE_ROYAL_DISTANCE = 1; // 수호탑 기준 거리
	private static final int HEINE_ROYAL_DEPTH = 1;    // 배치 겹수
	private static final int HEINE_KNIGHT_DISTANCE = 5;
	private static final int HEINE_KNIGHT_DEPTH = 1;
	private static final int HEINE_ELF_DISTANCE = 2;
	private static final int HEINE_ELF_DEPTH = 2;
	private static final int HEINE_WIZARD_DISTANCE = 2;
	private static final int HEINE_WIZARD_DEPTH = 1;

	// heading
	private static final int HEINE_ROYAL_HEADING = 5;
	private static final int HEINE_OTHER_HEADING = 5;
	
	// 켄트성	
	private static int KINGDOM_KENT_ROYAL_DEF_LOCATION[][];
	private static int KINGDOM_KENT_KNIGHT_DEF_LOCATION[][];
	private static int KINGDOM_KENT_ELF_DEF_LOCATION[][];
	private static int KINGDOM_KENT_WIZARD_DEF_LOCATION[][];	
	static private int KINGDOM_KENT_RANDOM_DEF_LOCATION[][];
	// 오크성	
	private static int KINGDOM_ORCISH_ROYAL_DEF_LOCATION[][];
	private static int KINGDOM_ORCISH_KNIGHT_DEF_LOCATION[][];
	private static int KINGDOM_ORCISH_ELF_DEF_LOCATION[][];
	private static int KINGDOM_ORCISH_WIZARD_DEF_LOCATION[][];	
	static private int KINGDOM_ORCISH_RANDOM_DEF_LOCATION[][];
	// 기란성
	static private int KINGDOM_GIRAN_ROYAL_DEF_LOCATION[][];
	static private int KINGDOM_GIRAN_KNIGHT_DEF_LOCATION[][];
	static private int KINGDOM_GIRAN_ELF_DEF_LOCATION[][];
	static private int KINGDOM_GIRAN_WIZARD_DEF_LOCATION[][];
	static private int KINGDOM_GIRAN_RANDOM_DEF_LOCATION[][];	
	// 하이네성	
	static private int KINGDOM_HEINE_ROYAL_DEF_LOCATION[][];
	static private int KINGDOM_HEINE_KNIGHT_DEF_LOCATION[][];
	static private int KINGDOM_HEINE_ELF_DEF_LOCATION[][];
	static private int KINGDOM_HEINE_WIZARD_DEF_LOCATION[][];
	static private int KINGDOM_HEINE_RANDOM_DEF_LOCATION[][];
	
	// 켄트 수성 위치 맵
	private static final Map<Integer, int[][]> KENT_DEF_LOCATIONS = new HashMap<Integer, int[][]>();
	// 오쿠 수성 위치 맵
	private static final Map<Integer, int[][]> ORCISH_DEF_LOCATIONS = new HashMap<Integer, int[][]>();
	// 기란 수성 위치 맵
	private static final Map<Integer, int[][]> GIRAN_DEF_LOCATIONS = new HashMap<Integer, int[][]>();
	// 하이네 수성 위치 맵
	private static final Map<Integer, int[][]> HEINE_DEF_LOCATIONS = new HashMap<Integer, int[][]>();
	
	// 켄트성 공성 case 1
	private static final Map<Integer, int[][]> KINGDOM_KENT_POSITION_CASE1 = new HashMap<>();
	// 켄트성 공성 case 2
	private static final Map<Integer, int[][]> KINGDOM_KENT_POSITION_CASE2 = new HashMap<>();
	
	// 오크요새 공성 case 1
	private static final Map<Integer, int[][]> KINGDOM_ORCISH_POSITION_CASE1 = new HashMap<>();
	// 오크요새 공성 case 2
	private static final Map<Integer, int[][]> KINGDOM_ORCISH_POSITION_CASE2 = new HashMap<>();
	
	// 기란 CASE 1 (성문 4시)
	private static final Map<Integer, int[][]> KINGDOM_GIRAN_POSITION_CASE1 = new HashMap<>();
	// 기란 CASE 2 (성문 8시)
	private static final Map<Integer, int[][]> KINGDOM_GIRAN_POSITION_CASE2 = new HashMap<>();

	// 하이네 CASE 1 (성문 11시)
	private static final Map<Integer, int[][]> KINGDOM_HEINE_POSITION_CASE1 = new HashMap<>();
	// 하이네 CASE 2 (성문 5시)
	private static final Map<Integer, int[][]> KINGDOM_HEINE_POSITION_CASE2 = new HashMap<>();
	
	static {
	    // === 먼저 위치 좌표 생성 ===
	    // 켄트
	    KINGDOM_KENT_ROYAL_DEF_LOCATION = generatePositions(
	        KENT_ROYAL_CENTER[0], KENT_ROYAL_CENTER[1], KENT_ROYAL_CENTER[2],
	        KENT_ROYAL_SHAPE, KENT_ROYAL_DISTANCE, KENT_ROYAL_DEPTH, KENT_ROYAL_HEADING
	    );
	    KINGDOM_KENT_KNIGHT_DEF_LOCATION = generatePositions(
	        KENT_KNIGHT_CENTER[0], KENT_KNIGHT_CENTER[1], KENT_KNIGHT_CENTER[2],
	        KENT_KNIGHT_SHAPE, KENT_KNIGHT_DISTANCE, KENT_KNIGHT_DEPTH, KENT_OTHER_HEADING
	    );
	    KINGDOM_KENT_ELF_DEF_LOCATION = generatePositions(
	        KENT_ELF_CENTER[0], KENT_ELF_CENTER[1], KENT_ELF_CENTER[2],
	        KENT_ELF_SHAPE, KENT_ELF_DISTANCE, KENT_ELF_DEPTH, KENT_OTHER_HEADING
	    );
	    KINGDOM_KENT_WIZARD_DEF_LOCATION = generatePositions(
	        KENT_WIZARD_CENTER[0], KENT_WIZARD_CENTER[1], KENT_WIZARD_CENTER[2],
	        KENT_WIZARD_SHAPE, KENT_WIZARD_DISTANCE, KENT_WIZARD_DEPTH, KENT_OTHER_HEADING
	    );

	    // 오크
	    KINGDOM_ORCISH_ROYAL_DEF_LOCATION = generatePositions(
	    	ORCISH_ROYAL_CENTER[0], ORCISH_ROYAL_CENTER[1], ORCISH_ROYAL_CENTER[2],
	    	ORCISH_ROYAL_SHAPE, ORCISH_ROYAL_DISTANCE, ORCISH_ROYAL_DEPTH, ORCISH_ROYAL_HEADING
	    );
	    KINGDOM_ORCISH_KNIGHT_DEF_LOCATION = generatePositions(
	    	ORCISH_KNIGHT_CENTER[0], ORCISH_KNIGHT_CENTER[1], ORCISH_KNIGHT_CENTER[2],
	    	ORCISH_KNIGHT_SHAPE, ORCISH_KNIGHT_DISTANCE, ORCISH_KNIGHT_DEPTH, ORCISH_OTHER_HEADING
	    );
	    KINGDOM_ORCISH_ELF_DEF_LOCATION = generatePositions(
	    	ORCISH_ELF_CENTER[0], ORCISH_ELF_CENTER[1], ORCISH_ELF_CENTER[2],
	    	ORCISH_ELF_SHAPE, ORCISH_ELF_DISTANCE, ORCISH_ELF_DEPTH, ORCISH_OTHER_HEADING
	    );
	    KINGDOM_ORCISH_WIZARD_DEF_LOCATION = generatePositions(
	    	ORCISH_WIZARD_CENTER[0], ORCISH_WIZARD_CENTER[1], ORCISH_WIZARD_CENTER[2],
	    	ORCISH_WIZARD_SHAPE, ORCISH_WIZARD_DISTANCE, ORCISH_WIZARD_DEPTH, ORCISH_OTHER_HEADING
	    );
	    
	    // 기란
	    KINGDOM_GIRAN_ROYAL_DEF_LOCATION = generatePositions(
	        GIRAN_ROYAL_CENTER[0], GIRAN_ROYAL_CENTER[1], GIRAN_ROYAL_CENTER[2],
	        GIRAN_ROYAL_SHAPE, GIRAN_ROYAL_DISTANCE, GIRAN_ROYAL_DEPTH, GIRAN_ROYAL_HEADING
	    );
	    KINGDOM_GIRAN_KNIGHT_DEF_LOCATION = generatePositions(
	        GIRAN_KNIGHT_CENTER[0], GIRAN_KNIGHT_CENTER[1], GIRAN_KNIGHT_CENTER[2],
	        GIRAN_KNIGHT_SHAPE, GIRAN_KNIGHT_DISTANCE, GIRAN_KNIGHT_DEPTH, GIRAN_OTHER_HEADING
	    );
	    KINGDOM_GIRAN_ELF_DEF_LOCATION = generatePositions(
	        GIRAN_ELF_CENTER[0], GIRAN_ELF_CENTER[1], GIRAN_ELF_CENTER[2],
	        GIRAN_ELF_SHAPE, GIRAN_ELF_DISTANCE, GIRAN_ELF_DEPTH, GIRAN_OTHER_HEADING
	    );
	    KINGDOM_GIRAN_WIZARD_DEF_LOCATION = generatePositions(
	        GIRAN_WIZARD_CENTER[0], GIRAN_WIZARD_CENTER[1], GIRAN_WIZARD_CENTER[2],
	        GIRAN_WIZARD_SHAPE, GIRAN_WIZARD_DISTANCE, GIRAN_WIZARD_DEPTH, GIRAN_OTHER_HEADING
	    );
	    
	    // 하이네
	    KINGDOM_HEINE_ROYAL_DEF_LOCATION = generatePositions(
	        HEINE_ROYAL_CENTER[0], HEINE_ROYAL_CENTER[1], HEINE_ROYAL_CENTER[2],
	        HEINE_ROYAL_SHAPE, HEINE_ROYAL_DISTANCE, HEINE_ROYAL_DEPTH, HEINE_ROYAL_HEADING
	    );
	    KINGDOM_HEINE_KNIGHT_DEF_LOCATION = generatePositions(
	    	HEINE_KNIGHT_CENTER[0], HEINE_KNIGHT_CENTER[1], HEINE_KNIGHT_CENTER[2],
	    	HEINE_KNIGHT_SHAPE, HEINE_KNIGHT_DISTANCE, HEINE_KNIGHT_DEPTH, HEINE_OTHER_HEADING
	    );
	    KINGDOM_HEINE_ELF_DEF_LOCATION = generatePositions(
	    	HEINE_ELF_CENTER[0], HEINE_ELF_CENTER[1], HEINE_ELF_CENTER[2],
	    	HEINE_ELF_SHAPE, HEINE_ELF_DISTANCE, HEINE_ELF_DEPTH, HEINE_OTHER_HEADING
	    );
	    KINGDOM_HEINE_WIZARD_DEF_LOCATION = generatePositions(
	        HEINE_WIZARD_CENTER[0], HEINE_WIZARD_CENTER[1], HEINE_WIZARD_CENTER[2],
	        HEINE_WIZARD_SHAPE, HEINE_WIZARD_DISTANCE, HEINE_WIZARD_DEPTH, HEINE_OTHER_HEADING
	    );
	    
	    // === 그 다음 Map에 좌표 등록 ===
	    KENT_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ROYAL,  KINGDOM_KENT_ROYAL_DEF_LOCATION);
	    KENT_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_KNIGHT, KINGDOM_KENT_KNIGHT_DEF_LOCATION);
	    KENT_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ELF,    KINGDOM_KENT_ELF_DEF_LOCATION);
	    KENT_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_WIZARD, KINGDOM_KENT_WIZARD_DEF_LOCATION);

	    ORCISH_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ROYAL,  KINGDOM_ORCISH_ROYAL_DEF_LOCATION);
	    ORCISH_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_KNIGHT, KINGDOM_ORCISH_KNIGHT_DEF_LOCATION);
	    ORCISH_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ELF,    KINGDOM_ORCISH_ELF_DEF_LOCATION);
	    ORCISH_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_WIZARD, KINGDOM_ORCISH_WIZARD_DEF_LOCATION);
	    	    
	    GIRAN_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ROYAL,  KINGDOM_GIRAN_ROYAL_DEF_LOCATION);
	    GIRAN_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_KNIGHT, KINGDOM_GIRAN_KNIGHT_DEF_LOCATION);
	    GIRAN_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ELF,    KINGDOM_GIRAN_ELF_DEF_LOCATION);
	    GIRAN_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_WIZARD, KINGDOM_GIRAN_WIZARD_DEF_LOCATION);
	    
	    HEINE_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ROYAL,  KINGDOM_HEINE_ROYAL_DEF_LOCATION);
	    HEINE_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_KNIGHT, KINGDOM_HEINE_KNIGHT_DEF_LOCATION);
	    HEINE_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_ELF,    KINGDOM_HEINE_ELF_DEF_LOCATION);
	    HEINE_DEF_LOCATIONS.put(Lineage.LINEAGE_CLASS_WIZARD, KINGDOM_HEINE_WIZARD_DEF_LOCATION);
	    
	    // ---- KENT CASE 1 ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_KENT_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(33093, 32765, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));
	    KINGDOM_KENT_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(33102, 32765, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));
	    KINGDOM_KENT_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(33099, 32765, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));
	    KINGDOM_KENT_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(33096, 32765, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));

	    // ---- KENT CASE 2 ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_KENT_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(33093, 32775, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));
	    KINGDOM_KENT_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(33102, 32775, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));
	    KINGDOM_KENT_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(33099, 32775, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));
	    KINGDOM_KENT_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(33096, 32775, KENT_MAP_ID, KENT_HEADING, 3, 10, 0, 1));

	    // ---- ORCISH CASE 1 ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_ORCISH_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(32794, 32329, ORCISH_MAP_ID, ORCISH_HEADING, 1, 10, 1, 0));
	    KINGDOM_ORCISH_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(32794, 32326, ORCISH_MAP_ID, ORCISH_HEADING, 1, 10, 1, 0));
	    KINGDOM_ORCISH_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(32794, 32327, ORCISH_MAP_ID, ORCISH_HEADING, 1, 10, 1, 0));
	    KINGDOM_ORCISH_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(32794, 32328, ORCISH_MAP_ID, ORCISH_HEADING, 1, 10, 1, 0));

	    // ---- ORCISH CASE 2 ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_ORCISH_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(32784, 32329, ORCISH_MAP_ID, ORCISH_HEADING, 3, 10, 0, 1));
	    KINGDOM_ORCISH_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(32784, 32326, ORCISH_MAP_ID, ORCISH_HEADING, 3, 10, 0, 1));
	    KINGDOM_ORCISH_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(32784, 32327, ORCISH_MAP_ID, ORCISH_HEADING, 3, 10, 0, 1));
	    KINGDOM_ORCISH_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(32784, 32328, ORCISH_MAP_ID, ORCISH_HEADING, 3, 10, 0, 1));
	    
	    // ---- GIRAN CASE 1 (4시 방향) ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_GIRAN_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(33626, 32750, GIRAN_MAP_ID, GIRAN_HEADING, 3, 10, 1, 0));
	    KINGDOM_GIRAN_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(33626, 32741, GIRAN_MAP_ID, GIRAN_HEADING, 3, 10, 1, 0));
	    KINGDOM_GIRAN_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(33626, 32747, GIRAN_MAP_ID, GIRAN_HEADING, 3, 10, 1, 0));
	    KINGDOM_GIRAN_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(33626, 32744, GIRAN_MAP_ID, GIRAN_HEADING, 3, 10, 0, 1));

	    // ---- GIRAN CASE 2 (8시 방향) ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_GIRAN_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(33566, 32673, GIRAN_MAP_ID, 2, 3, 10, 0, 1));
	    KINGDOM_GIRAN_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(33575, 32673, GIRAN_MAP_ID, 2, 3, 10, 0, 1));
	    KINGDOM_GIRAN_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(33572, 32673, GIRAN_MAP_ID, 2, 3, 10, 0, 1));
	    KINGDOM_GIRAN_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(33569, 32673, GIRAN_MAP_ID, 2, 3, 10, 0, 1));
	    
	    // ---- HEINE CASE 1 (5시 방향) ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_HEINE_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(33517, 33484, HEINE_MAP_ID, HEINE_HEADING, 1, 10, 1, 0));
	    KINGDOM_HEINE_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(33517, 33487, HEINE_MAP_ID, HEINE_HEADING, 1, 10, 1, 0));
	    KINGDOM_HEINE_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(33517, 33486, HEINE_MAP_ID, HEINE_HEADING, 1, 10, 1, 0));
	    KINGDOM_HEINE_POSITION_CASE1.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(33517, 33485, HEINE_MAP_ID, HEINE_HEADING, 1, 10, 1, 0));

	    // ---- HEINE CASE 2 (11시 방향) ---- startX, startY, map, heading, rows, cols (줄 수, 칸 수), xStep, yStep
	    KINGDOM_HEINE_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ROYAL, generateLocationGrid(33515, 33323, HEINE_MAP_ID, 4, 1, 20, 1, 0));
	    KINGDOM_HEINE_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_KNIGHT, generateLocationGrid(33515, 33324, HEINE_MAP_ID, 4, 1, 20, 1, 0));
	    KINGDOM_HEINE_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_WIZARD, generateLocationGrid(33515, 33325, HEINE_MAP_ID, 4, 1, 20, 1, 0));
	    KINGDOM_HEINE_POSITION_CASE2.put(Lineage.LINEAGE_CLASS_ELF, generateLocationGrid(33515, 33326, HEINE_MAP_ID, 4, 1, 20, 0, 1));
	}
	
	// 외성문 4시 1차 좌표값
	static private final int KINGDOM_OUT_DOOR04_LOCATION[][] = {
		    { 0, 0, 0, 0, 0, 0 },	
		    { 33089, 33111, 32750, 32790, 4, Lineage.KINGDOM_KENT }, 
		    { 32750, 32850, 32250, 32350, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33620, 33645, 32705, 32755, 4, Lineage.KINGDOM_GIRAN },
		    { 33524, 33524, 33468, 33468, 4, Lineage.KINGDOM_HEINE }
			};

	// 외성문 8시 1차 좌표값
	static private final int KINGDOM_OUT_DOOR08_LOCATION[][] = {
		    { 0, 0, 0, 0, 0, 0 },	
		    { 33106, 33110, 32764, 32777, 4, Lineage.KINGDOM_KENT }, 
		    { 32750, 32850, 32250, 32350, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33560, 33600, 32670, 32690, 4, Lineage.KINGDOM_GIRAN },
		    { 33524, 33525, 33344, 33344, 4, Lineage.KINGDOM_HEINE }
			};

	static public final int CASTLE_OUTSIDE_04_COORDS[][] = {
		    { 0, 0, 0, 0, 0, 0, 0 }, 	
		    { 33115, 33130, 32770, 32780, 4, Lineage.KINGDOM_KENT }, 
		    { 32780, 32796, 32310, 32323, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33620, 33650, 32700, 32705, 4, Lineage.KINGDOM_GIRAN },
		    { 33494, 33525, 33428, 33345, 4, Lineage.KINGDOM_HEINE }
			};
	
	static public final int CASTLE_OUTSIDE_08_COORDS[][] = {
		    { 0, 0, 0, 0, 0, 0 }, 	
		    { 33106, 33110, 32764, 32777, 4, Lineage.KINGDOM_KENT }, 
		    { 32750, 32850, 32250, 32350, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33606, 33608, 32670, 32685, 4, Lineage.KINGDOM_GIRAN },
		    { 33524, 33525, 33345, 33345, 4, Lineage.KINGDOM_HEINE }
			};

	static public final int CASTLE_INSIDE_COORDS[][] = { 
		    { 0, 0, 0, 0, 0, 0 }, 	
		    { 33113, 33188, 32734, 32804, 4, Lineage.KINGDOM_KENT },
		    { 32770, 32815, 32255, 32290, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33612, 33652, 32655, 32699, 4, Lineage.KINGDOM_GIRAN },
		    { 33505, 33538, 33381, 33447, 4, Lineage.KINGDOM_HEINE }
			};

	static public final int CASTLE_TOP_OUTSIDE_COORDS[][] = { 
		    { 0, 0, 0, 0, 0, 0 }, 	
		    { 33150, 33180, 32760, 32790, 4, Lineage.KINGDOM_KENT },
		    { 32795, 32827, 32280, 32295, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33628, 33635, 32674, 32681, 4, Lineage.KINGDOM_GIRAN },
		    { 33514, 33534, 33389, 33401, 4, Lineage.KINGDOM_HEINE }
			};
	
	static public final int CASTLE_TOP_INSIDE_COORDS[][] = { 
		    { 0, 0, 0, 0, 0, 0 }, 	
		    { 33161, 33175, 32766, 32782, 4, Lineage.KINGDOM_KENT },
		    { 32798, 32800, 32288, 32290, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33628, 33635, 32674, 32681, 4, Lineage.KINGDOM_GIRAN },
		    { 33521, 33527, 33393, 33398, 4, Lineage.KINGDOM_HEINE }
			};
	
	static public final int CASTLE_TOP_COORDS[][] = { 
		    { 0, 0, 0, 0, 0, 0 }, 	
		    { 33166, 33172, 32770, 32777, 4, Lineage.KINGDOM_KENT },
		    { 32798, 32802, 32286, 32290, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33628, 33636, 32674, 32681, 4, Lineage.KINGDOM_GIRAN },
		    { 33522, 33526, 33394, 33398, 4, Lineage.KINGDOM_HEINE }
			};

	static public final int KINGDOM_CROWN_COORDS[][] = { 
		    { 0, 0, 0, 0, 0, 0 }, 	
		    { 33168, 33170, 32772, 32774, 4, Lineage.KINGDOM_KENT },
		    { 32782, 32802, 32286, 32290, 4, Lineage.KINGDOM_ORCISH }, 
		    { 32571, 32721, 33350, 33460, 4, Lineage.KINGDOM_WINDAWOOD }, 
		    { 33630, 33632, 32677, 32679, 4, Lineage.KINGDOM_GIRAN },
		    { 33523, 33525, 33395, 33397, 4, Lineage.KINGDOM_HEINE }
			};

	// Entry 존 정보 { x1, x2, y1, y2, mapid }
	static public final int[][] KINGDOM_HEINE_ENTRY_ZONES = { 
	    {33509, 33538, 33450, 33465, 4},  // 5시 방향
	    {33495, 33549, 33354, 33357, 4}   // 11시 방향
	};

	// 탈출 좌표 { x1, x2, y1, y2, mapid }
	static public final int[][] KINGDOM_HEINE_ESCAPE_TARGETS = {
	    {33514, 33529, 33428, 33445, 4}
	};
	
	static public void init() {
		TimeLine.start("RobotController..");
		
		pool_pc = new ArrayList<PcRobotInstance>();		
		list_clan = new HashMap<Long, Clan>();
		list_drop = new HashMap<String, List<RobotDrop>>();
	
	    list_ment.clear();
	    
		// 무인케릭 객체 초기화.
		list_pc = new ArrayList<PcRobotInstance>();
		list_poly = new ArrayList<RobotPoly>();

		// 헤이스트샵 객체 초기화.
		list_buff = new ArrayList<BuffRobotInstance>();
		list_buff1 = new ArrayList<BuffRobotInstance1>();
		list_buff2 = new ArrayList<BuffRobotInstance2>();
		list_buff3 = new ArrayList<BuffRobotInstance3>();
		list_buff4 = new ArrayList<BuffRobotInstance4>();
		
		// 에볼 피케이단 로봇 객체 초기화
		pool_pk1 = new ArrayList<Pk1RobotInstance>();		
		list_pk1 = new ArrayList<Pk1RobotInstance>();

		// 먹자 로봇 객체 초기화
		pool_pickup = new ArrayList<PickupRobotInstance>();		
		list_pickup = new ArrayList<PickupRobotInstance>();
		
		// 파티 로봇 객체 초기화
		pool_party = new ArrayList<PartyRobotInstance>();		
		list_party = new ArrayList<PartyRobotInstance>();		

		// 켄트성 수성 나머지 포지션
		KINGDOM_KENT_RANDOM_DEF_LOCATION = new int[][] { { 33124, 32764, 4 }, { 33136, 32771, 4 }, { 33136, 32775, 4 }, { 33135, 32763, 4 }, { 33151, 32765, 4 }, { 33154, 32780, 4 }, { 33155, 32765, 4 },
				{ 33154, 32775, 4 }, { 33163, 32786, 4 } };
		// 오크요새 수성 나머지 포지션
        KINGDOM_ORCISH_RANDOM_DEF_LOCATION = new int[][] { { 32787, 32319, 4 }, { 32784, 32308, 4 }, { 32795, 32308, 4 }, { 32801, 32301, 4 }, { 32789, 32293, 4 }, { 32808, 32295, 4 } };						
		// 기란성 수성 나머지 포지션
		KINGDOM_GIRAN_RANDOM_DEF_LOCATION = new int[][] { { 33619, 32666, 4 }, { 33619, 32690, 4 }, { 33644, 32665, 4 }, { 33643, 32689, 4 }, { 33644, 32677, 4 }, { 33632, 32690, 4 }, { 33619, 32677, 4 },
				{ 33603, 32676, 4 }, { 33632, 32707, 4 }, { 33666, 32677, 4 } };		
		// 하이네성 수성 나머지 포지션
		KINGDOM_HEINE_RANDOM_DEF_LOCATION = new int[][] { { 33513, 33395, 4 }, { 33532, 33396, 4 }, { 33521, 33405, 4 }, { 33526, 33405, 4 }, { 33524, 33409, 4 }, { 33517, 33415, 4 }, { 33530, 33418, 4 },
				{ 33517, 33419, 4 }, { 33524, 33352, 4 }, { 33524, 33355, 4 }, { 33523, 33462, 4 }, { 33527, 33462, 4 }, { 33530, 33462, 4 } };
				
		if (Lineage.robot_auto_pc) {
			readPcRobot();
			readPoly();
			readMent();
			readDrop();
		}
		if (Lineage.robot_auto_buff) {
			readBuffRobot();
			toStartBuff();
		}
		if (Lineage.robot_auto_pu) {
			readPuRobot();
		}
		if (Lineage.robot_auto_party) {
			readPartyRobot();
		}		
		isTalkIsland = triggered1 = triggered2 = false;
//		reloadPkRobot(true);
		
		TimeLine.end();
	}

	static public void close() {
	    // 플러그인 초기화
	    PluginController.init(RobotController.class, "close");

	    // 자동 버프 중지
	    if (Lineage.robot_auto_buff) {
	        toStopBuff();
	    }

	    // 자동 PK 관련 리스트 정리
	    if (Lineage.robot_auto_pk) {
	        synchronized (list_pk1) {
	            list_pk1.clear();
	        }
	    }

	    // 자동 PU 관련 리스트 정리
	    if (Lineage.robot_auto_pu) {
	        synchronized (list_pickup) {
	            list_pickup.clear();
	        }
	    }

	    // 자동 Party 관련 리스트 정리
	    if (Lineage.robot_auto_party) {
	        synchronized (list_party) {
	        	list_party.clear();
	        }
	    }
	    
	    // 자동 PC 관련 리스트 정리
	    if (Lineage.robot_auto_pc) {
	        synchronized (list_pc) {
	            list_pc.clear();
	        }
	        synchronized (list_clan) {
	            list_clan.clear();
	        }
	        synchronized (list_drop) {
	            list_drop.clear();
	        }
	        synchronized (list_ment) {
	            list_ment.clear();
	        }
	    }
	}

	
	public static List<PcRobotInstance> getPcRobotList() {
		synchronized (list_pc) {
			return new ArrayList<PcRobotInstance>(list_pc);
		}
	}

	public static int getPcRobotListSize() {
		return list_pc.size();
	}

	private static PcRobotInstance getPoolPc() {
		PcRobotInstance pri = null;

		synchronized (pool_pc) {
			if (pool_pc.size() > 0) {
				pri = pool_pc.get(0);
				pool_pc.remove(0);
			} else {
				pri = new PcRobotInstance();
			}
		}
		return pri;
	}

	public static void setPool(PcRobotInstance pri) {
		synchronized (pool_pc) {
			pool_pc.add(pri);
		}
	}

	public static List<Pk1RobotInstance> getPkRobotList() {
		synchronized (list_pk1) {
			return new ArrayList<Pk1RobotInstance>(list_pk1);
		}
	}

	public static int getPkRobotListSize() {
		return list_pk1.size();
	}
	
	private static Pk1RobotInstance getPoolPk1() {
		Pk1RobotInstance pki = null;

		synchronized (pool_pk1) {
			if (pool_pk1.size() > 0) {
				pki = pool_pk1.get(0);
				pool_pk1.remove(0);
			} else {
				pki = new Pk1RobotInstance();
			}
		}
		return pki;
	}

	public static void setPoolPk1(Pk1RobotInstance pki) {
		synchronized (pool_pk1) {
			pool_pk1.add(pki);
		}
	}

	public static List<PickupRobotInstance> getPuRobotList() {
		synchronized (list_pickup) {
			return new ArrayList<PickupRobotInstance>(list_pickup);
		}
	}

	public static int getPuRobotListSize() {
		return list_pickup.size();
	}
	
	private static PickupRobotInstance getPoolPu() {
		PickupRobotInstance pui = null;

		synchronized (pool_pickup) {
			if (pool_pickup.size() > 0) {
				pui = pool_pickup.get(0);
				pool_pickup.remove(0);
			} else {
				pui = new PickupRobotInstance();
			}
		}
		return pui;
	}

	public static void setPoolPu(PickupRobotInstance pui) {
		synchronized (pool_pickup) {
			pool_pickup.add(pui);
		}
	}

	public static List<PartyRobotInstance> getPartyRobotList() {
		synchronized (list_party) {
			return new ArrayList<PartyRobotInstance>(list_party);
		}
	}

	public static int getPartyRobotListSize() {
		return list_party.size();
	}
	
	private static PartyRobotInstance getPoolParty() {
		PartyRobotInstance pti = null;

		synchronized (pool_party) {
			if (pool_party.size() > 0) {
				pti = pool_party.get(0);
				pool_party.remove(0);
			} else {
				pti = new PartyRobotInstance();
			}
		}
		return pti;
	}

	public static void setPoolParty(PartyRobotInstance pti) {
		synchronized (pool_party) {
			pool_party.add(pti);
		}
	}

    // pc(또는 파티에 속한 pc)로부터 PartyRobotInstance 객체를 검색하는 메소드
    public static PartyRobotInstance find(PcInstance pc) {
        synchronized (partyRobotMap) {
            return partyRobotMap.get(pc);
        }
    }

    /**
     *  파티 로봇을 등록하는 메서드
     * - PC와 로봇을 양방향 맵에 등록하여 추후 로봇 또는 PC 기준 조회 가능
     * @param pc    파티 마스터 혹은 로봇을 초대한 PC
     * @param robot 해당 PC와 연결된 PartyRobotInstance 객체
     */
    public static void register(PcInstance pc, PartyRobotInstance robot) {
        partyRobotMap.put(pc, robot);       // PC → 로봇
        robotToPcMap.put(robot, pc);        // 로봇 → PC
    }

    /**
     *  PC 기준 파티 로봇 등록 해제
     * - PC가 월드 아웃되거나 파티를 떠날 경우 호출됨
     * @param pc 로봇을 등록했던 PC 객체
     */
    public static void unregister(PcInstance pc) {
        PartyRobotInstance robot = partyRobotMap.remove(pc);  // PC → 로봇 제거
        if (robot != null) {
            robotToPcMap.remove(robot);                        // 로봇 → PC 제거
        }
    }

    /**
     *  로봇 기준 파티 등록 해제
     * - 로봇이 사망하거나 파티에서 제거될 경우 호출됨
     * - 연결된 PC가 무엇이든 상관없이 로봇 중심으로 맵에서 완전히 삭제
     * @param robot 제거할 PartyRobotInstance 객체
     */
    public static void unregister(PartyRobotInstance robot) {
        PcInstance pc = robotToPcMap.remove(robot);  // 로봇 → PC 제거
        if (pc != null) {
            partyRobotMap.remove(pc);                // PC → 로봇 제거
        }
    }

    // PC가 파티에 속해 있는 경우에도 로봇을 검색할 수 있도록 추가 메소드  
    // 예시로 PC와 연결된 로봇이 실제로 파티 상태인지(isInParty == true) 확인
    public static PartyRobotInstance findPartyRobot(PcInstance pc) {
        PartyRobotInstance robot = partyRobotMap.get(pc);
        if (robot != null && robot.isInParty()) {
            return robot;
        }
        return null;
    }
    
	static public void toTimer(long time) {
	    synchronized (list_buff) {
	        for (RobotInstance bi : list_buff)
	            bi.toTimer(time);
	    }
	    synchronized (list_buff1) {
	        for (RobotInstance bi : list_buff1)
	            bi.toTimer(time);
	    }
	    synchronized (list_buff2) {
	        for (RobotInstance bi : list_buff2)
	            bi.toTimer(time);
	    }
	    synchronized (list_buff3) {
	        for (RobotInstance bi : list_buff3)
	            bi.toTimer(time);
	    }
	    synchronized (list_buff4) {
	        for (RobotInstance bi : list_buff4)
	            bi.toTimer(time);
	    }
	    synchronized (list_pc) {
	        for (RobotInstance pc : list_pc)
	            pc.toTimer(time);
	    }
	    synchronized (list_pk1) {
	        for (RobotInstance pk1 : list_pk1)
	            pk1.toTimer(time);
	    }
	    synchronized (list_pickup) {
	        for (RobotInstance pu : list_pickup)
	            pu.toTimer(time);
	    }

	    synchronized (list_party) {
	        for (RobotInstance pt : list_party)
	            pt.toTimer(time);
	    }
	    
	    if (!Lineage.robot_auto_pk) return;

	    Calendar cal = Calendar.getInstance();
	    cal.setTimeInMillis(time);
	    int currentDay = cal.get(Calendar.DAY_OF_YEAR);

	    if (currentDay != lastDay) {
	        lastDay = currentDay;
	        Random rand = new Random();

	        triggerTime1 = Lineage.eventPk1Hour1 * 3600000L + (rand.nextInt(60) * 60000L);
	        triggerTime2 = Lineage.eventPk1Hour2 * 3600000L + (rand.nextInt(60) * 60000L);

	        triggered1 = false;
	        triggered2 = false;
	        lastGlobalTriggerTime = -1;
	    }

	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    int second = cal.get(Calendar.SECOND);
	    int ms = cal.get(Calendar.MILLISECOND);
	    long msOfDay = hour * 3600000L + minute * 60000L + second * 1000L + ms;

	    // 이벤트 1 실행 - 하루 한 번만 랜덤한 분 시점에 실행
	    if (!triggered1 && msOfDay >= triggerTime1 && msOfDay < triggerTime1 + 1000) {
	        triggered1 = true;
	        trigger1ActivationTime = msOfDay;
	        lastGlobalTriggerTime = msOfDay;
	        triggerEvent1();
	        World.toSender(S_ObjectChatting.clone(
	                BasePacketPooling.getPool(S_ObjectChatting.class),
	                "\\fY말하는 섬 북쪽에 에너지볼트 PK단이 출현하였습니다."
	        ));
	    }

	    // 이벤트 2 실행 - 하루 한 번만 랜덤한 분 시점에 실행
	    if (!triggered2 && msOfDay >= triggerTime2 && msOfDay < triggerTime2 + 1000) {
	        triggered2 = true;
	        trigger2ActivationTime = msOfDay;
	        lastGlobalTriggerTime = msOfDay;
	        triggerEvent2();
	        World.toSender(S_ObjectChatting.clone(
	                BasePacketPooling.getPool(S_ObjectChatting.class),
	                " \\fY본토 해골밭에 에너지볼트 PK단이 출현하였습니다."
	        ));
	    }

	    // 트리거 자동 종료 (30분 후)
	    if (triggered1 && msOfDay >= trigger1ActivationTime + 1800000) {
	        triggerEvent1Off();
	        World.toSender(S_ObjectChatting.clone(
	                BasePacketPooling.getPool(S_ObjectChatting.class),
	                "\\fY말하는 섬에 출현한 에볼 PK단 전원 퇴치에 실패하였습니다."
	        ));
	    }

	    if (triggered2 && msOfDay >= trigger2ActivationTime + 1800000) {
	        triggerEvent2Off();
	        World.toSender(S_ObjectChatting.clone(
	                BasePacketPooling.getPool(S_ObjectChatting.class),
	                "\\fY본토 해골 밭에 출현한 에볼 PK단 전원 퇴치에 실패하였습니다."
	        ));
	    }

	    // 트리거 실행 후 3분 동안 list_pk1이 비어 있으면 자동 종료
	    if (triggered1 && msOfDay >= trigger1ActivationTime + 180000 && getPkRobotListSize() == 0) {
	        triggerEvent1Off();
	        World.toSender(S_ObjectChatting.clone(
	                BasePacketPooling.getPool(S_ObjectChatting.class),
	                "\\fR말하는 섬 북쪽에 출현한 에볼 PK단이 모두 퇴치되었습니다."
	        ));
	    }

	    if (triggered2 && msOfDay >= trigger2ActivationTime + 180000 && getPkRobotListSize() == 0) {
	        triggerEvent2Off();
	        World.toSender(S_ObjectChatting.clone(
	                BasePacketPooling.getPool(S_ObjectChatting.class),
	                "\\fR본토 해골 밭에 출현한 에볼 PK단이 모두 퇴치되었습니다."
	        ));
	    }
	    // 좌표 예약 자동 해제 (60초마다 실행)
	    if (time - lastCleanup > 60_000) {
	        lastCleanup = time;
	        PartyRobotInstance.cleanupExpiredReservations();
	    }
	    
	    // 로봇 대화 유지시간 초과 검사 추가
	    try {
	        RobotConversationController.checkTimeout(time);
	    } catch (Exception e) {
	        lineage.share.System.println("로봇 대화 유지시간 검사 실패");
	        lineage.share.System.println(e);
	    }
	}

	public static void triggerEvent1() {
	    isTalkIsland = true;
	    reloadPkRobot(false);
	}

	public static void triggerEvent2() {
	    isTalkIsland = false;
	    reloadPkRobot(false);
	}

	public static void triggerEvent1Off() {
	    isTalkIsland = false;
	    reloadPkRobot(true);
	    triggered1 = false;
	    PickupRobotInstance.sendRobotsHomeInArea("말섬");
	}

	public static void triggerEvent2Off() {
	    isTalkIsland = false;
	    reloadPkRobot(true);
	    triggered2 = false;
	    PickupRobotInstance.sendRobotsHomeInArea("골밭");
	}
	
	/**
	 * 자동 버프사 시작 처리 함수.
	 */
	static public void toStartBuff() {
		synchronized (list_buff) {
			for (RobotInstance bi : list_buff)
				bi.toWorldJoin();
			count += list_buff.size();

		}
		synchronized (list_buff1) {
			for (RobotInstance bi : list_buff1)
				bi.toWorldJoin();
			count += list_buff1.size();

		}
		synchronized (list_buff2) {
			for (RobotInstance bi : list_buff2)
				bi.toWorldJoin();
			count += list_buff2.size();

		}
		synchronized (list_buff3) {
			for (RobotInstance bi : list_buff3)
				bi.toWorldJoin();
			count += list_buff3.size();

		}
		synchronized (list_buff4) {
			for (RobotInstance bi : list_buff4)
				bi.toWorldJoin();
			count += list_buff4.size();
		}
	}

	/**
	 * 자동 버프사 종료 처리 함수.
	 */
	static public void toStopBuff() {
		synchronized (list_buff) {
			for (RobotInstance bi : list_buff)
				bi.toWorldOut();
			count -= list_buff.size();
		}
		synchronized (list_buff1) {
			for (RobotInstance bi : list_buff1)
				bi.toWorldOut();
			count -= list_buff1.size();
		}
		synchronized (list_buff2) {
			for (RobotInstance bi : list_buff2)
				bi.toWorldOut();
			count -= list_buff2.size();
		}
		synchronized (list_buff3) {
			for (RobotInstance bi : list_buff3)
				bi.toWorldOut();
			count -= list_buff3.size();
		}
		synchronized (list_buff4) {
			for (RobotInstance bi : list_buff4)
				bi.toWorldOut();
			count -= list_buff4.size();
		}
	}
	
	/**
	 * 월드 아웃 처리 메서드.
	 * 
	 * @param pri
	 */
	static public void toWorldOut(PcRobotInstance pri) {
		pri.toWorldOut();
		synchronized (list_pc) {
			list_pc.remove(pri);
			count -= 1;
		}
	}

	/**
	 * 월드 아웃 처리 메서드.
	 * 
	 * @param pki
	 */
	static public void toWorldOut(Pk1RobotInstance pki) {
		pki.toWorldOut();
		synchronized (list_pk1) {
			list_pk1.remove(pki);
			count -= 1;
		}
	}

	/**
	 * 월드 아웃 처리 메서드.
	 * 
	 * @param pui
	 */
	static public void toWorldOut(PickupRobotInstance pui) {
		pui.toWorldOut();
		synchronized (list_pickup) {
			list_pickup.remove(pui);
			count -= 1;
		}
	}

	/**
	 * 월드 아웃 처리 메서드.
	 * 
	 * @param pti
	 */
	static public void toWorldOut(PartyRobotInstance pti) {
		pti.toWorldOut();
		synchronized (list_party) {
			list_party.remove(pti);
			count -= 1;
		}
	}
	
	/**
	 * 구매하려는 아이템을 판매하는 상점 찾기.
	 * 
	 * @param item_name
	 * @return
	 */
	public static ShopInstance findShop(PcRobotInstance pi, String item_name) {
	    ShopInstance nearestShop = null;
	    int minDistance = Integer.MAX_VALUE;  // 최소 거리를 최대값으로 설정하여 비교 시작

	    for (ShopInstance si : NpcSpawnlistDatabase.getShopList()) {
	        for (Shop s : si.getNpc().getShop_list()) {
	            // 본토의 상점만 검색.
	            if (si.getMap() == 4 
	                && Util.isDistance(pi.getX(), pi.getY(), pi.getMap(), si.getX(), si.getY(), si.getMap(), Lineage.SEARCH_WORLD_LOCATION_SHOP) 
	                && s.getItemName().equalsIgnoreCase(item_name) 
	                && s.getItemBress() == 1
	                && s.getAdenType().equalsIgnoreCase("아데나")) {

	                // 거리 계산
	                int distance = Util.getDistance(pi.getX(), pi.getY(), si.getX(), si.getY());

	                // 최소 거리를 갱신
	                if (distance < minDistance) {
	                    minDistance = distance;
	                    nearestShop = si;  // 가장 가까운 상점으로 갱신
	                }
	            }
	        }
	    }

	    // 가장 가까운 상점을 반환 (없으면 null)
	    return nearestShop;
	}

	/**
	 * 로봇 이름 존재여부리턴.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isName(String name) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot WHERE LOWER(name)=?");
			st.setString(1, name.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isName(String name)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return false;
	}

	/**
	 * 스킬정보 추출.
	 * 
	 * @param pr
	 */
	static public void readSkill(Connection con, RobotInstance pr) {
		List<Skill> list = SkillController.find(pr);

		if (list == null)
			return;

		list.clear();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM _robot_skill WHERE class=?");
			st.setString(1, getClassNameHangl(pr.getClassType()));
			rs = st.executeQuery();
			while (rs.next()) {
				Skill s = SkillDatabase.find(rs.getInt("skill_uid"));
				if (s != null) {
					SkillRobot sr = new SkillRobot(s);
					sr.setType(rs.getString("skill_type"));
					sr.setProbability(rs.getDouble("시전확률") * 0.01);
					sr.setWeaponType(rs.getString("시전무기"));
					sr.setTarget(rs.getString("공격대상"));
					sr.setLevel(rs.getInt("사용레벨"));

					switch (rs.getString("정령속성")) {
					case "일반":
						sr.setAttribute(Lineage.ELEMENT_NONE);
						break;
					case "물":
						sr.setAttribute(Lineage.ELEMENT_WATER);
						break;
					case "바람":
						sr.setAttribute(Lineage.ELEMENT_WIND);
						break;
					case "땅":
						sr.setAttribute(Lineage.ELEMENT_EARTH);
						break;
					case "불":
						sr.setAttribute(Lineage.ELEMENT_FIRE);
						break;
					}

					list.add(sr);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readSkill(RobotInstance pr)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}

	static public void reloadRobotSkill() {
		TimeLine.start("_robot_skill 테이블 리로드 완료 - ");

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();

			synchronized (list_pc) {
				if (list_pc.size() > 0) {
					for (RobotInstance pr : list_pc) {
						List<Skill> list = SkillController.find(pr);

						if (list == null)
							return;

						list.clear();

						st = con.prepareStatement("SELECT * FROM _robot_skill WHERE class=?");
						st.setString(1, getClassNameHangl(pr.getClassType()));
						rs = st.executeQuery();
						while (rs.next()) {
							Skill s = SkillDatabase.find(rs.getInt("skill_uid"));
							if (s != null) {
								SkillRobot sr = new SkillRobot(s);
								sr.setType(rs.getString("skill_type"));
								sr.setProbability(rs.getDouble("시전확률") * 0.01);
								sr.setWeaponType(rs.getString("시전무기"));
								sr.setTarget(rs.getString("공격대상"));
								sr.setLevel(rs.getInt("사용레벨"));

								switch (rs.getString("정령속성")) {
								case "일반":
									sr.setAttribute(Lineage.ELEMENT_NONE);
									break;
								case "물":
									sr.setAttribute(Lineage.ELEMENT_WATER);
									break;
								case "바람":
									sr.setAttribute(Lineage.ELEMENT_WIND);
									break;
								case "땅":
									sr.setAttribute(Lineage.ELEMENT_EARTH);
									break;
								case "불":
									sr.setAttribute(Lineage.ELEMENT_FIRE);
									break;
								}

								list.add(sr);
							}
						}
					}
					st.close();
					rs.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reRoadRobotSkill()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}

	/**
	 * 기억정보 추출.
	 * 
	 * @param pr
	 */
	static public void readBook(Connection con, PcRobotInstance pr) {
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    try {
	        st = con.prepareStatement("SELECT * FROM _robot_book");
	        rs = st.executeQuery();
	        while (rs.next()) {
	            Book b = BookController.getPool();
	            b.setLocation(rs.getString("location"));
	            b.setX(rs.getInt("locX"));
	            b.setY(rs.getInt("locY"));
	            b.setMap(rs.getInt("locMAP"));
	            b.setMinLevel(rs.getInt("입장레벨"));

	            // `enable` 컬럼 값 가져오기 (ENUM 타입)
	            String enable = rs.getString("enable").trim(); // 공백 제거 및 대소문자 무관 처리
	            b.setEnable(enable.equalsIgnoreCase("true")); // "true"면 true, 아니면 false

	            BookController.append(pr, b);
	        }
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : readBook(PcRobotInstance pr)\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(st, rs);
	    }
	}


	/**
	 * 리로드 북 2018-08-11 by connector12@nate.com
	 */
	static public void reloadRobotBook() {
		TimeLine.start("_robot_book 테이블 리로드 완료 - ");

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();

			synchronized (list_pc) {
				for (PcRobotInstance pr : list_pc) {
					BookController.find(pr).clear();

					st = con.prepareStatement("SELECT * FROM _robot_book");
					rs = st.executeQuery();
					while (rs.next()) {
						Book b = BookController.getPool();
						b.setLocation(rs.getString("location"));
						b.setX(rs.getInt("locX"));
						b.setY(rs.getInt("locY"));
						b.setMap(rs.getInt("locMAP"));
						b.setMinLevel(rs.getInt("입장레벨"));
						
			            // `enable` 컬럼 값 가져오기 (ENUM 타입)
			            String enable = rs.getString("enable").trim(); // 공백 제거 및 대소문자 무관 처리
			            b.setEnable(enable.equalsIgnoreCase("true")); // "true"면 true, 아니면 false
			            
						BookController.append(pr, b);
					}
					st.close();
					rs.close();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reRoadRobotBook()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}

	/**
	 * _robot_drop 테이블에서 읽은 정보를 토대로 로봇 인벤토리에 드랍할 아이템 등록.
	 *  : 로딩 후 로봇이 스폰될때 로딩됨.
	 *  : 또는 로봇이 죽은 후 다시 스폰될때 로딩됨.
	 * @param pr
	 */
	static public void robotItemDrop(PcRobotInstance pr) {
	    List<RobotDrop> list = null;
	    switch (pr.getClassType()) {
	        case Lineage.LINEAGE_CLASS_ROYAL:
	            synchronized (list_drop) {
	                list = list_drop.get("군주");
	            }
	            break;
	        case Lineage.LINEAGE_CLASS_ELF:
	            synchronized (list_drop) {
	                list = list_drop.get("요정");
	            }
	            break;
	        case Lineage.LINEAGE_CLASS_KNIGHT:
	            synchronized (list_drop) {
	                list = list_drop.get("기사");
	            }
	            break;
	        case Lineage.LINEAGE_CLASS_WIZARD:
	            synchronized (list_drop) {
	                list = list_drop.get("마법사");
	            }
	            break;
//	      case Lineage.LINEAGE_CLASS_DARKELF:
//	          synchronized (list_drop) {
//	              list = list_drop.get("다크엘프");
//	          }
//	          break;
	    }
	    if (list == null)
	        return;

	    boolean isFirstItemDropped = false; // 첫 번째 아이템 드랍 여부 확인

	    for (RobotDrop rd : list) {
	        Item item = ItemDatabase.find_ItemCode(rd.getItemCode());

	        // 아이템이 null이 아닐 경우에만 드랍하도록 수정
	        if (item != null) {
	            if (Util.random(0, Lineage.robot_auto_pc_chance_max_drop) <= Util.random(0, rd.getChance())) {
	                ItemInstance ii = ItemDatabase.newInstance(item);
	                if (ii != null) {
	                    ii.setObjectId(ServerDatabase.nextItemObjId());
	                    ii.setDefinite(true);
	                    ii.setBless(rd.getItemBress());

	                    // 확률 분포 설정
	                    int maxLevel = 9;
	                    double randomValue = Util.random(1, 100); // 1부터 100까지의 랜덤 수 생성
	                    int itemEnLevel = 0;

	                    // 각 레벨에 대한 확률 설정
	                    double[] probabilities = { 18.18, 16.36, 14.54, 12.72, 10.90, 9.09, 7.27, 5.45, 3.63, 1.81 };

	                    // 확률을 누적해서 비교하는 방식으로 변경
	                    double cumulativeProbability = 0.0;
	                    for (int level = 0; level <= maxLevel; level++) {
	                        cumulativeProbability += probabilities[level];
	                        if (randomValue <= cumulativeProbability) { // 누적 확률 비교
	                            itemEnLevel = level;
	                            break;
	                        }
	                    }

	                    ii.setEnLevel(itemEnLevel);
	                    ii.setCount(Util.random(rd.getCountMin() == 0 ? 1 : rd.getCountMin(), rd.getCountMax() == 0 ? 1 : rd.getCountMax()));

	                    // 아이템을 바닥에 드랍하도록 변경
	                    ii.toTeleport(pr.getX(), pr.getY(), pr.getMap(), false);

	                    // 🔥 첫 번째 아이템에 대해서만 멘트 실행
	                    if (!isFirstItemDropped) {
	                        getRandomMentAndChat(Lineage.AI_DROP_MENT, pr, ii, itemEnLevel, Lineage.CHATTING_MODE_NORMAL);
	                        isFirstItemDropped = true; // 첫 번째 아이템 드랍됨
	                    }
	                }
	            }
	        }
	    }
	}
  
	/**
	 * 멘트 정보 추출 (DB에서 데이터를 가져와 list_ment에 저장)
	 */
	static private void readMent() {
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT uid, type, ment FROM _robot_ment");
	        rs = st.executeQuery();

	        synchronized (list_ment) {
	            list_ment.clear(); // 기존 데이터 초기화

	            while (rs.next()) {
	                int uid = rs.getInt("uid");
	                String typeString = rs.getString("type").trim(); // 공백 제거
	                String ment = rs.getString("ment");

	                int type = RobotMent.getTypeMapping().getOrDefault(typeString, -1);

	                if (type == -1) {
	                    continue; // 매핑되지 않은 타입이면 무시
	                }

	                RobotMent rm = new RobotMent(uid, type, ment);
	                list_ment.computeIfAbsent(type, k -> new ArrayList<>()).add(rm.getMent()); //  type 사용
	            }
	        }
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : readMent()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	}

	/**
	 * _robot_ment 테이블 리로드.
	 */
	static public void reloadMent() {
	    TimeLine.start("_robot_ment 테이블 리로드 완료 - ");

	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT uid, type, ment FROM _robot_ment");
	        rs = st.executeQuery();

	        synchronized (list_ment) {
	            list_ment.clear(); // 기존 데이터를 초기화

	            while (rs.next()) {
	                int uid = rs.getInt("uid");
	                String typeString = rs.getString("type").trim(); // 공백 제거
	                String ment = rs.getString("ment");

	                int type = RobotMent.getTypeMapping().getOrDefault(typeString, -1);

	                if (type == -1) {
	                    continue; // 매핑되지 않은 타입이면 무시
	                }

	                RobotMent rm = new RobotMent(uid, type, ment);
	                list_ment.computeIfAbsent(type, k -> new ArrayList<>()).add(rm.getMent());
	            }
	        }
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : reloadMent()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	    TimeLine.end();
	}
	
	/**
	 * 드랍 정보 추출.
	 * 
	 * 
	 */
	static private void readDrop() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot_drop");
			rs = st.executeQuery();
			while(rs.next()) {
				RobotDrop rd = new RobotDrop();
				rd.setClassName(rs.getString("class"));
				rd.setItemCode(rs.getInt("item_code"));
				rd.setItemName(rs.getString("item_name"));
				rd.setItemBress(rs.getInt("item_bress"));
				rd.setCountMin(rs.getInt("count_min"));
				rd.setCountMax(rs.getInt("count_max"));
				rd.setChance(rs.getInt("chance"));
				
				synchronized (list_drop) {
					List<RobotDrop> list = list_drop.get(rd.getClassName());
					if(list == null) {
						list = new ArrayList<RobotDrop>();
						list_drop.put(rd.getClassName(), list);
					}
					list.add( rd );
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readDrop()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * _robot_drop 테이블 리로드.
	 */
	static public void reloadDrop() {
	    TimeLine.start("_robot_drop 테이블 리로드 완료 - ");

	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT * FROM _robot_drop");
	        rs = st.executeQuery();
	        
	        synchronized (list_drop) {
	            list_drop.clear();
	            
	            while (rs.next()) {
	                RobotDrop rd = new RobotDrop();
	                rd.setClassName(rs.getString("class"));
	                rd.setItemCode(rs.getInt("item_code"));
	                rd.setItemName(rs.getString("item_name"));
	                rd.setItemBress(rs.getInt("item_bress"));
	                rd.setCountMin(rs.getInt("count_min"));
	                rd.setCountMax(rs.getInt("count_max"));
	                rd.setChance(rs.getInt("chance"));
	                
	                list_drop.computeIfAbsent(rd.getClassName(), k -> new ArrayList<>()).add(rd);
	            }
	        }
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : reloadRobotDrop()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	    TimeLine.end();
	}

	
	/**
	 * 무인 버프 pc 정보 추출.
	 */
	private static void readBuffRobot() {
		// 기란
		for (int i = 0; i < 1; ++i) {
			list_buff.add(new BuffRobotInstance(33446, 32828, 4, 6, "행복헤이", "결제도와드림", 10000, 16234, 40));
		}
		// 화말
		for (int t = 0; t < 1; ++t) {
			list_buff1.add(new BuffRobotInstance1(32733, 32449, 4, 4, "화말헤이", "결제도와드림", 10000, 16232, 4));
		}
		// 켄말
		for (int e = 0; e < 1; ++e) {
			list_buff2.add(new BuffRobotInstance2(33037, 32762, 4, 4, "헤이걸", "결제도와드림", 30000, 16233, 20));
		}
		// 은말
		for (int r = 0; r < 1; ++r) {
			list_buff3.add(new BuffRobotInstance3(33064, 33391, 4, 2, "은말헤이", "결제도와드림", 30000, 16235, 40));
		}
		// 말섬
		for (int s = 0; s < 1; ++s) {
			list_buff4.add(new BuffRobotInstance4(32589, 32941, 0, 6, "말섬헤이걸", "결제도와드림", 30000, 16235, 40));
		}
	}

	private static void readPkRobot() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int[] home = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot_pk1");
			rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getString("스폰_여부").equalsIgnoreCase("false") || checkPk1Name(rs.getString("name")) != null || rs.getInt("objId") < 1910000)
					continue;

				Pk1RobotInstance pk = getPoolPk1();
				
				if (pk == null) {
					pk = new Pk1RobotInstance();
				}

				pk.setObjectId(rs.getInt("objId"));
				pk.setName(rs.getString("name"));
				pk.setStr(rs.getInt("str"));
				pk.setDex(rs.getInt("dex"));
				pk.setCon(rs.getInt("con"));
				pk.setWis(rs.getInt("wis"));
				pk.setInt(rs.getInt("inter"));
				pk.setCha(rs.getInt("cha"));

				home = Lineage.getHomeXY();
				pk.setX(home[0]);
				pk.setY(home[1]);
				pk.setMap(home[2]);
				pk.setTitle(rs.getString("title"));
				pk.setLawful(rs.getInt("lawful"));

				if (rs.getString("class").equalsIgnoreCase("마법사")) {
					pk.setClassType(Lineage.LINEAGE_CLASS_WIZARD);
					pk.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자") ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
					pk.setGfx(pk.getClassGfx());
					pk.setMaxHp(Lineage.wizard_hp);
					pk.setMaxMp(Lineage.wizard_mp);
				} 
				//
				Exp e = ExpDatabase.find(rs.getInt("level"));
				int hp = 0;
				int mp = 0;
				for (int i = 0; i < e.getLevel(); ++i) {
					hp += CharacterController.toStatusUP(pk, true);
					mp += CharacterController.toStatusUP(pk, false);
				}

				pk.setMaxHp(pk.getMaxHp() + hp);
				pk.setMaxMp(pk.getMaxMp() + mp);
				pk.setNowHp(pk.getMaxHp());
				pk.setNowMp(pk.getMaxMp());
				pk.setLevel(e.getLevel());
				pk.setDynamicMr(rs.getInt("mr"));
				pk.setDynamicSp(rs.getInt("sp"));
				pk.setFood(Lineage.MAX_FOOD);
				pk.setAttribute(pk.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
				pk.setWeaponEn(rs.getInt("무기인챈트"));
				pk.setAc(rs.getInt("ac"));
				pk.setHeading(Util.random(0, 7));
				pk.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
				
				pk.toWorldJoin(con);
				//
				synchronized (list_pk1) {
					list_pk1.add(pk);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			lineage.share.System.printf("%s : readPkRobot()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}


	/**
	 * _robot_pk1 테이블의 데이터를 읽어와 PK 로봇 인스턴스를 리로드합니다.
	 * 스폰 여부가 'true'이고, objId가 1910000 이상인 경우에만 로봇을 생성하거나 업데이트합니다.
	 */
	public static void reloadPkRobot() {
	    // 타임라인 로그 시작
	    TimeLine.start("_robot_pk1 테이블 리로드 완료 - ");
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    int[] home = null;
	    
	    try {
	        // 데이터베이스 연결 획득
	        con = DatabaseConnection.getLineage();
	        // _robot_pk1 테이블의 모든 로봇 데이터를 조회
	        st = con.prepareStatement("SELECT * FROM _robot_pk1");
	        rs = st.executeQuery();
	        while (rs.next()) {
	            // objId가 1910000 이상인 경우에만 처리
	            if (rs.getInt("objId") >= 1910000) {
	                // 스폰 여부가 "false"인 경우:
	                // - 이미 동일한 objId의 로봇 인스턴스가 존재하면 월드에서 삭제 처리
	                if (rs.getString("스폰_여부").equalsIgnoreCase("false")) {
	                    if (checkPk1ObjId(rs.getInt("objId")) != null) {
	                        Pk1RobotInstance pk = checkPk1ObjId(rs.getInt("objId"));
	                        toWorldOut(pk);
	                    }
	                } else {
	                    // 스폰 여부가 "true"인 경우:
	                    // 해당 objId의 로봇 인스턴스가 없으면 새로 생성하고,
	                    // 이미 존재하면 DB의 최신 값으로 업데이트(예: 능력치, 이름, 위치 갱신)만 수행
	                    Pk1RobotInstance pk;
	                    if (checkPk1ObjId(rs.getInt("objId")) == null) {
	                        pk = getPoolPk1();
	                        if (pk == null) {
	                            pk = new Pk1RobotInstance();
	                        }
	                    } else {
	                        pk = checkPk1ObjId(rs.getInt("objId"));
	                    }
	                    
	                    // DB에서 읽은 값으로 로봇 인스턴스의 기본 속성을 업데이트
	                    pk.setObjectId(rs.getInt("objId"));
	                    pk.setName(rs.getString("name"));
	                    pk.setStr(rs.getInt("str"));
	                    pk.setDex(rs.getInt("dex"));
	                    pk.setCon(rs.getInt("con"));
	                    pk.setWis(rs.getInt("wis"));
	                    pk.setInt(rs.getInt("inter"));
	                    pk.setCha(rs.getInt("cha"));
	                    
	                    // 기본 귀환 좌표(홈 좌표)를 가져와 설정
	                    home = Lineage.getHomeXY();
	                    pk.setX(home[0]);
	                    pk.setY(home[1]);
	                    pk.setMap(home[2]);
	                    pk.setTitle(rs.getString("title"));
	                    pk.setLawful(rs.getInt("lawful"));
	                    
	                    // 클래스가 "마법사"인 경우에 대한 처리 (필요 시 다른 클래스 처리 추가)
	                    if (rs.getString("class").equalsIgnoreCase("마법사")) {
	                        pk.setClassType(Lineage.LINEAGE_CLASS_WIZARD);
	                        pk.setClassGfx(rs.getString("sex").equalsIgnoreCase("남자")
	                                ? Lineage.wizard_male_gfx
	                                : Lineage.wizard_female_gfx);
	                        pk.setGfx(pk.getClassGfx());
	                        pk.setMaxHp(Lineage.wizard_hp);
	                        pk.setMaxMp(Lineage.wizard_mp);
	                    }
	                    
	                    // 경험치 정보를 기반으로 레벨별 상태 상승량 계산
	                    Exp e = ExpDatabase.find(rs.getInt("level"));
	                    int hp = 0;
	                    int mp = 200;
	                    for (int i = 0; i < e.getLevel(); ++i) {
	                        hp += CharacterController.toStatusUP(pk, true);
	                        mp += CharacterController.toStatusUP(pk, false);
	                    }
	                    
	                    // 최대 HP/MP에 추가 상승분을 더하고, 현재 HP/MP와 레벨을 설정
	                    pk.setMaxHp(pk.getMaxHp() + hp);
	                    pk.setMaxMp(pk.getMaxMp() + mp);
	                    pk.setNowHp(pk.getMaxHp());
	                    pk.setNowMp(pk.getMaxMp());
	                    pk.setLevel(e.getLevel());
	                    pk.setDynamicMr(rs.getInt("mr"));
	                    pk.setDynamicSp(rs.getInt("sp"));
	                    pk.setFood(Lineage.MAX_FOOD);
	                    pk.setAttribute(pk.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
	                    pk.setWeaponEn(rs.getInt("무기인챈트"));
	                    pk.setAc(rs.getInt("ac"));
	                    pk.setHeading(Util.random(0, 7));
	                    // 무기 이름이 길이가 0보다 크면 해당 문자열, 아니면 null
	                    pk.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
	                    
	                    // 로봇 리로드 플래그를 true로 설정
	                    pk.isReload = true;
	                    
	                    // 인벤토리가 존재하면, 인벤토리 내 장착된 무기의 인첸트 레벨을 업데이트
	                    if (pk.getInventory() != null) {
	                        for (ItemInstance weapon : pk.getInventory().getList()) {
	                            if (weapon instanceof ItemWeaponInstance && weapon.isEquipped()) {
	                                weapon.setEnLevel(pk.getWeaponEn());
	                                break;
	                            }
	                        }
	                    }
	                    
	                    // 현재 좌표와 맵 정보를 기준으로 로봇을 해당 위치로 순간이동 처리
//	                    pk.toTeleport(pk.getX(), pk.getY(), pk.getMap(), false);
	                    
	                    // 로봇을 월드에 스폰 (새로운 인스턴스 생성 시에도, 이미 스폰되어 있다면 업데이트)
	                    pk.toWorldJoin(con);
	                    
	                    // 전역 PK 로봇 리스트에 추가 (동기화 처리)
	                    synchronized (list_pk1) {
	                        if (!list_pk1.contains(pk)) {
	                            list_pk1.add(pk);
	                        }
	                    }
	                }
	            }
	        }
	        
	        // 나비켓에서 로봇 삭제 후 리로드할 경우,
	        // 이미 스폰되어 있는 로봇들 중 isReload 플래그가 false인 로봇을 삭제 처리
	        synchronized (list_pk1) {
	            for (Pk1RobotInstance pk : getPkRobotList()) {
	                if (!pk.isReload) {
	                    list_pk1.remove(pk);
	                    pk.toWorldOut();
	                } else {
	                    // 리로드 완료 후 플래그 초기화
	                    pk.isReload = false;
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        lineage.share.System.printf("%s : reloadPkRobot()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	    // 타임라인 로그 종료
	    TimeLine.end();
	}

	/**
	 * PK 로봇 스폰 여부를 업데이트한 후 전체 PK 로봇 데이터를 리로드합니다.
	 * 
	 * @param isDelete true이면 모든 로봇의 스폰 여부를 'false'로 업데이트하여 삭제 처리,
	 *                 false이면 스폰 여부를 'true'로 업데이트합니다.
	 */
	public static void reloadPkRobot(boolean isDelete) {
	    Connection con = null;
	    PreparedStatement st = null;
	    
	    try {
	        con = DatabaseConnection.getLineage();
	        if (isDelete) {
	            st = con.prepareStatement("UPDATE _robot_pk1 SET 스폰_여부='false'");
	            st.executeUpdate();
	        } else {
	            st = con.prepareStatement("UPDATE _robot_pk1 SET 스폰_여부='true'");
	            st.executeUpdate();
	        }
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : reloadPkRobot(boolean isDelete)\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st);
	    }
	    
	    // 업데이트 후 전체 PK 로봇 데이터를 다시 리로드합니다.
	    reloadPkRobot();
	}

	private static void readPuRobot() {
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;

	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT * FROM _robot_pu");
	        rs = st.executeQuery();

	        while (rs.next()) {
	            //  데이터 무결성 검사
	            if (rs.getString("스폰_여부") == null || rs.getString("스폰_여부").equalsIgnoreCase("false")) continue;
	            if (rs.getString("name") == null || checkPickupName(rs.getString("name")) != null) continue;
	            if (rs.getInt("objId") < 1920000) continue;

	            String spawnLocation = rs.getString("스폰_위치");
	            if (spawnLocation == null) continue;
	            //  polyName 값 추출
	            String polyName = rs.getString("poly_name");
	            if (polyName == null) continue;
	            
	            PickupRobotInstance pu = getPoolPu();
	            if (pu == null) {
	                pu = new PickupRobotInstance();
	            }

	            //  기본 정보 설정
	            pu.setObjectId(rs.getInt("objId"));
	            pu.setName(rs.getString("name"));
	            //  polyName 저장
	            pu.setPolymorph(polyName);	            
	            //  스폰 위치 저장
	            pu.setPlaceName(spawnLocation);
	            pu.setStr(rs.getInt("str"));
	            pu.setDex(rs.getInt("dex"));
	            pu.setCon(rs.getInt("con"));
	            pu.setWis(rs.getInt("wis"));
	            pu.setInt(rs.getInt("inter"));
	            pu.setCha(rs.getInt("cha"));

	            int[] home = Lineage.getHomeXY();
	            pu.setX(home[0]);
	            pu.setY(home[1]);
	            pu.setMap(home[2]);
	            pu.setTitle(rs.getString("title"));
	            pu.setLawful(rs.getInt("lawful"));

	            //  클래스 정보 설정
	            applyClassInfo(pu, rs.getString("class"), rs.getString("sex"));

	            //  무기 정보 설정
	            String weaponName = rs.getString("무기 이름");
	            pu.setWeapon_name((weaponName != null && weaponName.length() > 0) ? weaponName : null);
	            pu.setWeaponEn(rs.getInt("무기인챈트"));

	            //  레벨 & HP/MP 설정
	            Exp e = ExpDatabase.find(rs.getInt("level"));
	            applyStatusGrowth(pu, e);

	            pu.setDynamicMr(rs.getInt("mr"));
	            pu.setDynamicSp(rs.getInt("sp"));
	            pu.setFood(Lineage.MAX_FOOD);
	            pu.setAttribute(pu.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
	            pu.setAc(rs.getInt("ac"));
	            pu.setHeading(Util.random(0, 7));

	            //  월드 등록 + 변신/무기속도 적용까지 내부에서 처리
	            pu.toWorldJoin(con);

	            //  리스트에 추가
	            synchronized (list_pickup) {
	                list_pickup.add(pu);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        lineage.share.System.printf("%s : readPuRobot()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	}

	private static void readPartyRobot() {
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;

	    try {
	        //  데이터베이스 연결 설정
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT * FROM _robot_party");
	        rs = st.executeQuery();

	        while (rs.next()) {
	            //  데이터 무결성 검사
	            if (rs.getString("스폰_여부") == null || rs.getString("스폰_여부").equalsIgnoreCase("false")) continue;
	            if (rs.getString("name") == null || checkPartyName(rs.getString("name")) != null) continue;
	            if (rs.getInt("objId") < 1930000) continue;

	            //  스폰 위치 확인
	            String spawnLocation = rs.getString("스폰_위치");
	            if (spawnLocation == null) continue;

	            //  풀에서 PartyRobotInstance 객체 가져오기
	            PartyRobotInstance pt = getPoolParty();
	            if (pt == null) {
	                pt = new PartyRobotInstance();
	            }

	            //  기본 정보 설정
	            pt.setObjectId(rs.getInt("objId"));
	            pt.setName(rs.getString("name"));
	            pt.setPlaceName(spawnLocation);
	            pt.setStr(rs.getInt("str"));
	            pt.setDex(rs.getInt("dex"));
	            pt.setCon(rs.getInt("con"));
	            pt.setWis(rs.getInt("wis"));
	            pt.setInt(rs.getInt("inter"));
	            pt.setCha(rs.getInt("cha"));

	            //  스폰 좌표 설정
                RobotSpawnLocation spawn = PartyRobotInstance.getSpawnLocation(pt.getPlaceName());
                if (spawn != null) {
                    // 예: 해당 좌표에 이미 다른 객체가 있을 경우 예약 해제
                    if (World.isMapdynamic(spawn.getX(), spawn.getY(), spawn.getMapId())) {
                        PartyRobotInstance.releaseSpawnLocation(spawn);
                    } else {
                        pt.setX(spawn.getX());
                        pt.setY(spawn.getY());
                        pt.setMap(spawn.getMapId());
                    }
                } else {
                    int[] home = Lineage.getHomeXY();
                    pt.setX(home[0]);
                    pt.setY(home[1]);
                    pt.setMap(home[2]);
                }

	            pt.setTitle(rs.getString("title"));
	            pt.setLawful(rs.getInt("lawful"));

	            //  클래스 정보 설정
	            applyClassInfo(pt, rs.getString("class"), rs.getString("sex"));

	            //  무기 정보 설정
	            String weaponName = rs.getString("무기 이름");
	            pt.setWeapon_name((weaponName != null && weaponName.length() > 0) ? weaponName : null);
	            pt.setWeaponEn(rs.getInt("무기인챈트"));

	            //  레벨 정보 설정
	            Exp e = ExpDatabase.find(rs.getInt("level"));
	            applyStatusGrowth(pt, e);

	            pt.setDynamicMr(rs.getInt("mr"));
	            pt.setDynamicSp(rs.getInt("sp"));
	            pt.setFood(Lineage.MAX_FOOD);

	            //  엘프 클래스일 경우 속성을 무작위로 설정
	            pt.setAttribute(pt.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);

	            pt.setAc(rs.getInt("ac"));
	            pt.setHeading(Util.random(0, 7));

	            //  아데나(재화) 설정
	            pt.setAdena(rs.getInt("adena"));
	            
	            //  월드 등록 및 로봇 초기화 처리
	            pt.toWorldJoin(con);

	            //  로봇 리스트에 추가 (동기화 처리)
	            synchronized (list_party) {
	                list_party.add(pt);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        lineage.share.System.printf("%s : readPartyRobot()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	}

    public static void reloadPartyRobot(boolean isDelete) {
        Connection con = null;
        PreparedStatement st = null;

        try {
            con = DatabaseConnection.getLineage();

            if (isDelete) {
                st = con.prepareStatement("UPDATE _robot_party SET 스폰_여부='false'");
                st.executeUpdate();
            } else {
                st = con.prepareStatement("UPDATE _robot_party SET 스폰_여부='true'");
                st.executeUpdate();
            }
        } catch (Exception e) {
            lineage.share.System.printf("%s : reloadPartyRobot(boolean isDelete)\r\n", RobotController.class.toString());
            lineage.share.System.println(e);
        } finally {
            DatabaseConnection.close(con, st);
        }

        reloadPartyRobot();
    }

    private static void reloadPartyRobot() {
        // 타임라인 로그 시작
        TimeLine.start("_robot_party 테이블 리로드 완료 - ");
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getLineage();
            st = con.prepareStatement("SELECT * FROM _robot_party");
            rs = st.executeQuery();

            while (rs.next()) {
                int objId = rs.getInt("objId");
                String name = rs.getString("name");
                String spawnFlag = rs.getString("스폰_여부");

                if (objId < 1930000) continue;
                if (name == null) continue;

                //  스폰 해제 상태면 파티 탈퇴만 하고 넘어감
                if (spawnFlag == null || spawnFlag.equalsIgnoreCase("false")) {
                    PartyRobotInstance existing = checkPartyName(name);
                    if (existing != null && existing.getPartyId() > 0) {
                        PartyController.close(existing); //  파티에서만 탈퇴
                    }
                    continue;
                }

                //  중복 등록 방지
                if (checkPartyName(name) != null) continue;

                String spawnLocation = rs.getString("스폰_위치");
                if (spawnLocation == null) continue;

                PartyRobotInstance pt = checkPartyObjId(objId);
                if (pt == null) {
                    pt = getPoolParty();
                    if (pt == null) {
                        pt = new PartyRobotInstance();
                    }
                }

                pt.setObjectId(objId);
                pt.setName(name);
                pt.setPlaceName(spawnLocation);
                pt.setStr(rs.getInt("str"));
                pt.setDex(rs.getInt("dex"));
                pt.setCon(rs.getInt("con"));
                pt.setWis(rs.getInt("wis"));
                pt.setInt(rs.getInt("inter"));
                pt.setCha(rs.getInt("cha"));

                //  스폰 좌표 설정
                RobotSpawnLocation spawn = PartyRobotInstance.getSpawnLocation(pt.getPlaceName());
                if (spawn != null) {
                    if (World.isMapdynamic(spawn.getX(), spawn.getY(), spawn.getMapId())) {
                        PartyRobotInstance.releaseSpawnLocation(spawn);
                    } else {
                        pt.setX(spawn.getX());
                        pt.setY(spawn.getY());
                        pt.setMap(spawn.getMapId());
                    }
                } else {
                    int[] home = Lineage.getHomeXY();
                    pt.setX(home[0]);
                    pt.setY(home[1]);
                    pt.setMap(home[2]);
                }

                pt.setTitle(rs.getString("title"));
                pt.setLawful(rs.getInt("lawful"));
                applyClassInfo(pt, rs.getString("class"), rs.getString("sex"));

                String weaponName = rs.getString("무기 이름");
                pt.setWeapon_name((weaponName != null && weaponName.length() > 0) ? weaponName : null);
                pt.setWeaponEn(rs.getInt("무기인챈트"));

                Exp e = ExpDatabase.find(rs.getInt("level"));
                applyStatusGrowth(pt, e);

                pt.setDynamicMr(rs.getInt("mr"));
                pt.setDynamicSp(rs.getInt("sp"));
                pt.setFood(Lineage.MAX_FOOD);
                pt.setAttribute(pt.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
                pt.setAc(rs.getInt("ac"));
                pt.setHeading(Util.random(0, 7));

	            //  아데나(재화) 설정
	            pt.setAdena(rs.getInt("adena"));
	            
                pt.isReload = true;

                pt.toWorldJoin(con);

                synchronized (list_party) {
                    if (!list_party.contains(pt)) {
                        list_party.add(pt);
                    }
                }
            }

            //  리로드 대상이 아닌 기존 로봇은 제거
            synchronized (list_party) {
                Iterator<PartyRobotInstance> iterator = list_party.iterator();
                while (iterator.hasNext()) {
                    PartyRobotInstance pt = iterator.next();
                    if (!pt.isReload) {
                        iterator.remove();
                        pt.toWorldOut();
                    } else {
                        pt.isReload = false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            lineage.share.System.printf("%s : reloadPartyRobot()\r\n", RobotController.class.toString());
            lineage.share.System.println(e);
        } finally {
            DatabaseConnection.close(con, st, rs);
        }

        // 타임라인 로그 종료
        TimeLine.end();
    }

	/**
	 * 로봇 이름으로 검색. 2019-04-28 by connector12@nate.com
	 */
	public static PcRobotInstance checkName(String name) {
		synchronized (list_pc) {
			for (PcRobotInstance pi : list_pc) {
				if (pi.getName().equalsIgnoreCase(name))
					return pi;
			}
		}

		return null;
	}

	public static Pk1RobotInstance checkPk1Name(String name) {
		synchronized (list_pk1) {
			for (Pk1RobotInstance pk : list_pk1) {
				if (pk.getName().equalsIgnoreCase(name))
					return pk;
			}
		}

		return null;
	}

	public static PickupRobotInstance checkPickupName(String name) {
		synchronized (list_pickup) {
			for (PickupRobotInstance pu : list_pickup) {
				if (pu.getName().equalsIgnoreCase(name))
					return pu;
			}
		}

		return null;
	}
	
	public static PartyRobotInstance checkPartyName(String name) {
	    if (list_party == null) {
	        lineage.share.System.println("❌ [checkPartyName] list_party is null.");
	        return null;
	    }

	    synchronized (list_party) {
	        for (PartyRobotInstance pti : list_party) {
	            if (pti.getName().equalsIgnoreCase(name))
	                return pti;
	        }
	    }

	    return null;
	}

	
	/**
	 * 로봇 uid로 검색. 2019-04-28 by connector12@nate.com
	 */
	public static PcRobotInstance checkObjId(long objId) {
		synchronized (list_pc) {
			for (PcRobotInstance pi : list_pc) {
				if (pi.getObjectId() == objId)
					return pi;
			}
		}

		return null;
	}

	public static Pk1RobotInstance checkPk1ObjId(long objId) {
		synchronized (list_pk1) {
			for (Pk1RobotInstance pk : list_pk1) {
				if (pk.getObjectId() == objId)
					return pk;
			}
		}

		return null;
	}

	public static PickupRobotInstance checkPickupObjId(long objId) {
		synchronized (list_pickup) {
			for (PickupRobotInstance pu : list_pickup) {
				if (pu.getObjectId() == objId)
					return pu;
			}
		}

		return null;
	}

	public static PartyRobotInstance checkPartyObjId(long objId) {
		synchronized (list_party) {
			for (PartyRobotInstance pti : list_party) {
				if (pti.getObjectId() == objId)
					return pti;
			}
		}

		return null;
	}
	
	/**
	 *  무인PC 정보 추출 및 초기화
	 */
	private static void readPcRobot() {
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    int[] home = null;

	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT * FROM _robot");
	        rs = st.executeQuery();

	        while (rs.next()) {
	            //  스폰 여부 확인 및 유효성 체크
	            if (rs.getString("스폰_여부").equalsIgnoreCase("false") || 
	                checkName(rs.getString("name")) != null || 
	                rs.getInt("objId") < 1900000) {
	                continue;
	            }

	            //  기존 객체 풀에서 가져오기 (없으면 새로 생성)
	            PcRobotInstance pr = getPoolPc();
	            if (pr == null) {
	                pr = new PcRobotInstance();
	            }

	            //  기본 정보 설정
	            pr.setObjectId(rs.getInt("objId"));
	            pr.setName(rs.getString("name"));
	            pr.action = rs.getString("행동");
	            pr.setStr(rs.getInt("str"));
	            pr.setDex(rs.getInt("dex"));
	            pr.setCon(rs.getInt("con"));
	            pr.setWis(rs.getInt("wis"));
	            pr.setInt(rs.getInt("inter"));
	            pr.setCha(rs.getInt("cha"));

	            //  초기 위치 설정
	            home = Lineage.getHomeXY();
	            pr.setX(home[0]);
	            pr.setY(home[1]);
	            pr.setMap(home[2]);

	            //  칭호, 법률, 클랜 정보 설정
	            pr.setTitle(rs.getString("title"));
	            pr.setLawful(rs.getInt("lawful"));
	            pr.setClanId(rs.getInt("clanID"));
	            pr.setClanName(rs.getString("clan_name"));

	            //  클랜 정보 업데이트
	            processClanInfo(pr);

	            //  클래스 정보 설정 (최적화 적용)
	            applyClassInfo(pr, rs.getString("class"), rs.getString("sex"));

	            //  레벨 기반 HP/MP 증가량 적용
	            Exp e = ExpDatabase.find(rs.getInt("level"));
	            applyStatusGrowth(pr, e);

	            //  기타 능력치 설정
	            pr.setDynamicMr(rs.getInt("mr"));
	            pr.setDynamicSp(rs.getInt("sp"));
	            pr.setFood(Lineage.MAX_FOOD);
	            pr.setAttribute(pr.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
	            pr.setWeaponEn(rs.getInt("무기인챈트"));
	            pr.setAc(rs.getInt("ac"));
	            pr.setHeading(Util.random(0, 7));
	            pr.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
	            pr.setDoll_name(rs.getString("마법 인형").length() > 0 ? rs.getString("마법 인형") : null);
	            String mythicPoly = rs.getString("mythic_poly");
	            pr.setMythicPoly("true".equalsIgnoreCase(mythicPoly)); 
	            pr.setRandomPoly("random".equalsIgnoreCase(mythicPoly));

	            //  월드에 추가
	            pr.toWorldJoin(con);

	            //  리스트에 추가
	            synchronized (list_pc) {
	                list_pc.add(pr);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        lineage.share.System.printf("%s : readPcRobot()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	}
	
	public static void reloadPcRobot() {
	    TimeLine.start("_robot 테이블 리로드 완료 - ");
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    int[] home = null;

	    try {
	        con = DatabaseConnection.getLineage();
	        st = con.prepareStatement("SELECT * FROM _robot");
	        rs = st.executeQuery();
	        while (rs.next()) {
	            int objId = rs.getInt("objId");

	            if (objId >= 1900000) {
	                // ❌ 스폰 상태에서 삭제 처리
	                if (rs.getString("스폰_여부").equalsIgnoreCase("false")) {
	                    PcRobotInstance pi = checkObjId(objId);
	                    if (pi != null) {
	                        toWorldOut(pi);
	                    }
	                    continue;
	                }

	                //  스폰이 안되어있으면 새로 생성
	                PcRobotInstance pr = checkObjId(objId);
	                if (pr == null) {
	                    pr = getPoolPc();
	                    if (pr == null) {
	                        pr = new PcRobotInstance();
	                    }
	                }

	                //  기본 정보 설정
	                pr.setObjectId(objId);
	                pr.setName(rs.getString("name"));
	                pr.action = rs.getString("행동");
	                pr.setStr(rs.getInt("str"));
	                pr.setDex(rs.getInt("dex"));
	                pr.setCon(rs.getInt("con"));
	                pr.setWis(rs.getInt("wis"));
	                pr.setInt(rs.getInt("inter"));
	                pr.setCha(rs.getInt("cha"));

	                home = Lineage.getHomeXY();
	                pr.setX(home[0]);
	                pr.setY(home[1]);
	                pr.setMap(home[2]);

	                pr.setTitle(rs.getString("title"));
	                pr.setLawful(rs.getInt("lawful"));
	                pr.setClanId(rs.getInt("clanID"));
	                pr.setClanName(rs.getString("clan_name"));

	                //  클랜 정보 처리 (중복 방지)
	                processClanInfo(pr);

	                //  클래스 정보 적용 (최적화)
	                applyClassInfo(pr, rs.getString("class"), rs.getString("sex"));

	                //  레벨 및 HP/MP 적용 (최적화)
	                Exp e = ExpDatabase.find(rs.getInt("level"));
	                applyStatusGrowth(pr, e);

	                //  추가적인 정보 적용
	                pr.setDynamicMr(rs.getInt("mr"));
	                pr.setDynamicSp(rs.getInt("sp"));
	                pr.setFood(Lineage.MAX_FOOD);
	                pr.setAttribute(pr.getClassType() == Lineage.LINEAGE_CLASS_ELF ? Util.random(1, 4) : 0);
	                pr.setWeaponEn(rs.getInt("무기인챈트"));
	                pr.setAc(rs.getInt("ac"));
	                pr.setHeading(Util.random(0, 7));
	                pr.setWeapon_name(rs.getString("무기 이름").length() > 0 ? rs.getString("무기 이름") : null);
	                pr.setDoll_name(rs.getString("마법 인형").length() > 0 ? rs.getString("마법 인형") : null);

	                String mythicPoly = rs.getString("mythic_poly");
	                pr.setMythicPoly("true".equalsIgnoreCase(mythicPoly));
	                pr.setRandomPoly("random".equalsIgnoreCase(mythicPoly));

	                pr.isReload = true;

	                pr.toWorldJoin(con);

	                //  리스트 추가 (중복 방지)
	                synchronized (list_pc) {
	                    if (!list_pc.contains(pr)) {
	                        list_pc.add(pr);
	                    }
	                }
	            }
	        }

	        //  로봇 삭제 후 리로드 시 처리
	        synchronized (list_pc) {
	            Iterator<PcRobotInstance> iterator = list_pc.iterator();
	            while (iterator.hasNext()) {
	                PcRobotInstance pi = iterator.next();
	                if (!pi.isReload) {
	                    iterator.remove();
	                    pi.toWorldOut();
	                } else {
	                    pi.isReload = false;
	                }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        lineage.share.System.printf("%s : reloadPcRobot()\r\n", RobotController.class.toString());
	        lineage.share.System.println(e);
	    } finally {
	        DatabaseConnection.close(con, st, rs);
	    }
	    TimeLine.end();
	}

	/**
	 * 로봇 사용 여부 처리 메소드. 2019-07-03 by connector12@nate.com
	 */
	public static void reloadPcRobot(boolean isDelete) {
		Connection con = null;
		PreparedStatement st = null;

		try {
			con = DatabaseConnection.getLineage();

			if (isDelete) {
				st = con.prepareStatement("UPDATE _robot SET 스폰_여부='false'");
				st.executeUpdate();
			} else {
				st = con.prepareStatement("UPDATE _robot SET 스폰_여부='true'");
				st.executeUpdate();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reloadPcRobot(boolean isDelete)\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}

		reloadPcRobot();
	}

	private static void processClanInfo(PcRobotInstance pr) {
	    if (pr.getClanName() == null) return;

	    Clan c = ClanController.find(pr.getClanName());
	    synchronized (c.getMemberList()) {
	        if (!c.getMemberList().contains(pr.getName().toLowerCase())) {
	            c.appendMemberList(pr.getName());
	        }
	    }

	    Map<Integer, Clan> clanList = ClanController.getClanList();
	    if (clanList != null) {
	        synchronized (clanList) {
	            if (!clanList.containsKey(c.getUid())) {
	                clanList.put(c.getUid(), c);
	                ClanController.toSaveClan(c);
	            }
	        }
	    }
	}
	
	private static void applyClassInfo(RobotInstance ri, String className, String sex) {
	    boolean isMale = sex.equalsIgnoreCase("남자");
	    
	    //  성별 코드 세팅 (0: 남자, 1: 여자)
	    ri.setClassSex(isMale ? 0 : 1);

	    switch (className) {
	        case "군주":
	            ri.setClassType(Lineage.LINEAGE_CLASS_ROYAL);
	            ri.setClassGfx(isMale ? Lineage.royal_male_gfx : Lineage.royal_female_gfx);
	            ri.setMaxHp(Lineage.royal_hp);
	            ri.setMaxMp(Lineage.royal_mp);
	            break;
	        case "기사":
	            ri.setClassType(Lineage.LINEAGE_CLASS_KNIGHT);
	            ri.setClassGfx(isMale ? Lineage.knight_male_gfx : Lineage.knight_female_gfx);
	            ri.setMaxHp(Lineage.knight_hp);
	            ri.setMaxMp(Lineage.knight_mp);
	            break;
	        case "요정":
	            ri.setClassType(Lineage.LINEAGE_CLASS_ELF);
	            ri.setClassGfx(isMale ? Lineage.elf_male_gfx : Lineage.elf_female_gfx);
	            ri.setMaxHp(Lineage.elf_hp);
	            ri.setMaxMp(Lineage.elf_mp);
	            break;
	        case "마법사":
	            ri.setClassType(Lineage.LINEAGE_CLASS_WIZARD);
	            ri.setClassGfx(isMale ? Lineage.wizard_male_gfx : Lineage.wizard_female_gfx);
	            ri.setMaxHp(Lineage.wizard_hp);
	            ri.setMaxMp(Lineage.wizard_mp);
	            break;
	        case "다크엘프":
	            ri.setClassType(Lineage.LINEAGE_CLASS_DARKELF);
	            ri.setClassGfx(isMale ? Lineage.darkelf_male_gfx : Lineage.darkelf_female_gfx);
	            ri.setMaxHp(Lineage.darkelf_hp);
	            ri.setMaxMp(Lineage.darkelf_mp);
	            break;
	    }

	    ri.setGfx(ri.getClassGfx());
	}

	/**
	 *  레벨업 시 HP/MP 증가량을 적용
	 */
	private static void applyStatusGrowth(RobotInstance ri, Exp e) {
	    if (e == null) return; //  레벨 정보가 없으면 실행하지 않음

	    int hp = 0;
	    int mp = 0;
	    
	    //  현재 레벨까지 HP/MP 증가량 계산
	    for (int i = 0; i < e.getLevel(); ++i) {
	        hp += CharacterController.toStatusUP(ri, true);  // HP 증가
	        mp += CharacterController.toStatusUP(ri, false); // MP 증가
	    }

	    //  HP/MP 적용
	    ri.setMaxHp(ri.getMaxHp() + hp);
	    ri.setMaxMp(ri.getMaxMp() + mp);
	    ri.setNowHp(ri.getMaxHp());
	    ri.setNowMp(ri.getMaxMp());
	    ri.setLevel(e.getLevel());
	}
	
	private static String getClassNameHangl(int classType) {
		String className = "군주";
		if (classType == Lineage.LINEAGE_CLASS_KNIGHT)
			className = "기사";
		if (classType == Lineage.LINEAGE_CLASS_ELF)
			className = "요정";
		if (classType == Lineage.LINEAGE_CLASS_WIZARD)
			className = "마법사";
		if (classType == Lineage.LINEAGE_CLASS_DARKELF)
			className = "다크엘프";
		if (classType == Lineage.LINEAGE_CLASS_DRAGONKNIGHT)
			className = "용기사";
		if (classType == Lineage.LINEAGE_CLASS_BLACKWIZARD)
			className = "환술사";
		return className;
	}

	/**
	 * 로봇과 연결된 클랜 찾아서 리턴.
	 * @param robot
	 * @return
	 */
	static public Clan findClan(RobotInstance robot) {
	    synchronized (list_clan) {
	        return robot.getClanId() == 0 ? null : list_clan.get((long) robot.getClanId());
	    }
	}
	
	static public Clan findClan(long uid) {
		synchronized (list_clan) {
			return uid==0 ? null : list_clan.get(uid);
		}
	}
	
	static public Clan findClan(String name) {
		synchronized (list_clan) {
			for(Clan c : list_clan.values()) {
				if(c.getName().equalsIgnoreCase(name))
					return c;
			}
			return null;
		}
	}
	
	public static int[][] generatePositions(int centerX, int centerY, int mapId, Shape shape, int distance, int depth, int heading) {
	    List<int[]> positions = new ArrayList<>();

	    int maxRadius = distance * depth;

	    if (shape == Shape.SQUARE) {
	        for (int x = centerX - maxRadius; x <= centerX + maxRadius; x++) {
	            for (int y = centerY - maxRadius; y <= centerY + maxRadius; y++) {
	                if (isPassable(x, y, mapId, heading)) {
	                    positions.add(new int[]{x, y, mapId, heading});
//	                    System.println("[디버그] 추가된 좌표: (" + x + ", " + y + ")");
	                }
	            }
	        }
	    } else if (shape == Shape.DIAMOND) {
	        for (int x = centerX - maxRadius; x <= centerX + maxRadius; x++) {
	            for (int y = centerY - maxRadius; y <= centerY + maxRadius; y++) {
	                int dx = Math.abs(centerX - x);
	                int dy = Math.abs(centerY - y);
	                if (dx + dy <= maxRadius) { // 다이아몬드 형태 조건
	                    if (isPassable(x, y, mapId, heading)) {
	                        positions.add(new int[]{x, y, mapId, heading});
//	                        System.println("[디버그] 추가된 좌표: (" + x + ", " + y + ")");
	                    }
	                }
	            }
	        }
	    }

	    return positions.toArray(new int[0][]);
	}


	private static boolean isPassable(int x, int y, int mapId, int heading) {
	    return World.getMapdynamic(x, y, mapId) == 0 &&
	           World.isThroughObject(x, y, mapId, heading) &&
	           !World.isNotMovingTile(x, y, mapId);
	}
	    
	// RobotController 클래스 안에 추가
	public static int[][] generateLocationGrid(int startX, int startY, int map, int heading,
	                                           int rows, int cols, int xStep, int yStep) {
	    int[][] locations = new int[rows * cols][4];
	    int index = 0;
	    for (int row = 0; row < rows; row++) {
	        for (int col = 0; col < cols; col++) {
	            int locX = startX + (col * xStep);
	            int locY = startY + (row * yStep);
	            locations[index++] = new int[]{locX, locY, map, heading};
	        }
	    }
	    return locations;
	}
	
	/**
	 * 공성전이 진행중일때 호출되며, 로봇이 스폰될 위치가 존재하는지 확인해줌.
	 * @param ri
	 * @return
	 */
	public static boolean isKingdomLocation(RobotInstance ri, boolean teleport, int idx) {
	    if (ri == null) {
//	        System.println("[디버그] 로봇 인스턴스가 null입니다.");
	        return false;
	    }

	    Clan c = ClanController.find(ri);
	    int[][] locationArray = null;

	    // 클래스별 위치 배열 가져오기
	    Map<Integer, int[][]> locationMap = 
	    	    (idx == 1) ? KENT_DEF_LOCATIONS :
	    	    (idx == 2) ? ORCISH_DEF_LOCATIONS : 
	    	    (idx == 4) ? GIRAN_DEF_LOCATIONS : 
	    	    (idx == 5) ? HEINE_DEF_LOCATIONS : 
	    	    null;

	    if (locationMap == null) {
//	        System.println("[디버그] locationMap이 null입니다. idx: " + idx);
	        return false;
	    }

	    // ROYAL일 경우 군주인지 체크
	    if (ri.getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
	        if (c == null) {
//	            System.println("[디버그] ROYAL인데 소속 클랜이 없습니다.");
	            return false;
	        }
	        if (c.getLord() == null) {
//	            System.println("[디버그] ROYAL인데 클랜 군주 정보가 null입니다.");
	            return false;
	        }
	        if (!c.getLord().equalsIgnoreCase(ri.getName())) {
//	            System.println("[디버그] ROYAL인데 로봇 이름(" + ri.getName() + ")과 군주 이름(" + c.getLord() + ")이 다릅니다.");
	            return false; // 군주가 아니면 위치 제공 안 함
	        }
	    }

	    locationArray = locationMap.get(ri.getClassType());

	    if (locationArray == null) {
//	        System.println("[디버그] 클래스 타입(" + ri.getClassType() + ")에 대한 위치 배열이 없습니다.");
	        return false;
	    }

	    synchronized (locationArray) {
	        for (int[] location : locationArray) {
	            int locX = location[0];
	            int locY = location[1];
	            int locMap = location[2];
	            int locHead = location[3];

//	            System.println("[디버그] 좌표 검사중: (" + locX + ", " + locY + ", " + locMap + ") Head: " + locHead);

	            // 이동 가능한 좌표인지 체크
	            if (World.getMapdynamic(locX, locY, locMap) == 0 &&
	                World.isThroughObject(locX, locY, locMap, locHead) &&
	                !World.isNotMovingTile(locX, locY, locMap)) {

//	                System.println("[디버그] 이동 가능한 좌표 발견: (" + locX + ", " + locY + ", " + locMap + ")");
	                ri.setHeading(locHead);

	                if (teleport) {
//	                    System.println("[디버그] 로봇 텔레포트 수행: (" + locX + ", " + locY + ", " + locMap + ")");
	                    ri.toTeleport(locX, locY, locMap, true);
	                }
	                return true;
	            } else {
//	                System.println("[디버그] 이동 불가: (" + locX + ", " + locY + ", " + locMap + ")");
	            }
	        }
	    }

//	    System.println("[디버그] 조건에 맞는 스폰 좌표를 찾지 못했습니다.");
	    return false;
	}

	
	/**
	 * 공성전 중이라면 호출되며, 랜덤워킹 가능한 좌표로 랜덤 텔레포트 함.
	 * @param pi   로봇 객체
	 * @param idx  성 구분 (1=켄트, 4=기란)
	 */
	public static void toKingdomRandomLocationTeleport(PcRobotInstance pi, int idx) {
	    if (pi == null)
	        return;

	    Clan c = ClanController.find(pi);
	    // 클랜 정보가 없거나, 군주이면 텔레포트하지 않음
	    if (c == null || c.getLord().equalsIgnoreCase(pi.getName()))
	        return;

	    int[] baseLocation = null;
	    switch (idx) {
	        case 1:
	            baseLocation = KINGDOM_KENT_RANDOM_DEF_LOCATION[
	                    Util.random(0, KINGDOM_KENT_RANDOM_DEF_LOCATION.length - 1)];
	            break;
	        case 2:
	            baseLocation = KINGDOM_ORCISH_RANDOM_DEF_LOCATION[
	                    Util.random(0, KINGDOM_ORCISH_RANDOM_DEF_LOCATION.length - 1)];
	            break;    
	        case 4:
	            baseLocation = KINGDOM_GIRAN_RANDOM_DEF_LOCATION[
	                    Util.random(0, KINGDOM_GIRAN_RANDOM_DEF_LOCATION.length - 1)];
	            break;
	        case 5:
	            baseLocation = KINGDOM_HEINE_RANDOM_DEF_LOCATION[
	                    Util.random(0, KINGDOM_HEINE_RANDOM_DEF_LOCATION.length - 1)];
	            break;    
	        default:
	            return; // idx가 1, 4가 아니면 처리 안 함
	    }

	    int baseX = baseLocation[0];
	    int baseY = baseLocation[1];
	    int mapId = baseLocation[2];

	    // 최대 10번 좌표 찾기 시도
	    int maxAttempts = 10;
	    boolean found = false;
	    int finalX = baseX;
	    int finalY = baseY;
	    int finalHeading = 0; // 이동 방향은 0으로 기본 설정

	    for (int attempt = 0; attempt < maxAttempts; attempt++) {
	        // ±3 범위 랜덤 좌표
	        int offsetX = Util.random(-3, 3);
	        int offsetY = Util.random(-3, 3);

	        int candidateX = baseX + offsetX;
	        int candidateY = baseY + offsetY;

	        //  1) 방향으로 통과 가능한지 확인 (heading 0 = 북쪽, 필요하면 heading 랜덤 가능)
	        if (!World.isThroughObject(candidateX, candidateY, mapId, finalHeading))
	            continue;

	        //  2) 해당 위치에 객체가 없는지 확인
	        if (World.getMapdynamic(candidateX, candidateY, mapId) != 0)
	            continue;

	        //  3) 이동 불가능한 타일인지 확인
	        if (World.isNotMovingTile(candidateX, candidateY, mapId))
	            continue;

	        // 조건을 모두 통과하면 선택
	        finalX = candidateX;
	        finalY = candidateY;
	        found = true;
	        break;
	    }

	    if (found) {
	        // 최종 좌표로 텔레포트
	        pi.toTeleport(finalX, finalY, mapId, true);
	    } else {
	        // 조건을 만족하는 위치를 찾지 못함
	        // System.println("[DEBUG] 이동 가능한 좌표를 찾지 못했습니다.");
	    }
	}

	/**
	 * 로봇(RobotInstance)의 현재 위치가 켄트 또는 기란 성 공격 위치에 해당하는지 확인하고,
	 * 해당 위치로 이동(텔레포트) 시도.
	 *
	 * @param ri        로봇 인스턴스
	 * @param teleport  true면 위치로 텔레포트
	 * @param idx       성 타입 (1=켄트, 4=기란)
	 * @return 위치 이동 성공 여부
	 */
	public static boolean isKingdomAttLocation(RobotInstance ri, boolean teleport, int idx) {
	    if (ri == null || ri.getClanId() <= 0) {
	        return false;
	    }

	    Kingdom k = KingdomController.find(getWarCastleUid());
	    int castleId = (k != null) ? k.getClanId() : 0;

	    int[][] locations = null;

	    // castleId가 4 or 5면 항상 랜덤케이스 사용
	    if (castleId == 4 || castleId == 5) {
	        int randomCase = getRandomCase();
	        locations = getLocationsByCastle(ri, idx, randomCase);
	    }
	    // 그 외에는 클랜 소속에 따라 case1 or case2 선택
	    else {
	        locations = getLocationsByClan(ri, idx);
	    }

	    if (locations == null) {
	        return false;
	    }

	    synchronized (locations) {
	        for (int[] loc : locations) {
	            int locX = loc[0];
	            int locY = loc[1];
	            int locMap = loc[2];
	            int locHeading = loc[3];
	            if (World.getMapdynamic(locX, locY, locMap) == 0) {
	                ri.setHeading(locHeading);
	                if (teleport) {
	                    ri.toTeleport(locX, locY, locMap, true);
	                }
	                return true;
	            }
	        }
	    }

	    return false;
	}

	/**
	 * 랜덤 케이스를 반환. (0 또는 1, 직전과 다르게)
	 */
	private static int getRandomCase() {
	    int currentCase;
	    do {
	        currentCase = (int) (Math.random() * 2);
	    } while (currentCase == lastRandomCase);
	    lastRandomCase = currentCase;
	    return currentCase;
	}

	/**
	 * 켄트/기란/하이네 idx와 랜덤 케이스에 따라 위치 반환 (castleId가 1~7일 때)
	 */
	private static int[][] getLocationsByCastle(RobotInstance ri, int idx, int randomCase) {
	    Map<Integer, int[][]> map = null;
	    if (idx == 1) { // Kent
	        map = (randomCase == 0) ? KINGDOM_KENT_POSITION_CASE1 : KINGDOM_KENT_POSITION_CASE2;
	    } else if (idx == 2) { // Orcish
	        map = (randomCase == 0) ? KINGDOM_ORCISH_POSITION_CASE1 : KINGDOM_ORCISH_POSITION_CASE2;
	    } else if (idx == 4) { // Giran
	        map = (randomCase == 0) ? KINGDOM_GIRAN_POSITION_CASE1 : KINGDOM_GIRAN_POSITION_CASE2;
	    } else if (idx == 5) { // Heine 추가
	        map = (randomCase == 0) ? KINGDOM_HEINE_POSITION_CASE1 : KINGDOM_HEINE_POSITION_CASE2;
	    }
	    return (map != null) ? map.get(ri.getClassType()) : null;
	}

	/**
	 * 클랜 ID에 따라 기본 위치 반환  (castleId가 1~7이 아닐 때)
	 */
	private static int[][] getLocationsByClan(RobotInstance ri, int idx) {
	    int clanId = ri.getClanId();
	    Map<Integer, int[][]> map = null;

	    if (idx == 1) { // Kent
	        map = (clanId == 4) ? KINGDOM_KENT_POSITION_CASE1 :
	              (clanId == 5) ? KINGDOM_KENT_POSITION_CASE2 : null;
	    } else if (idx == 2) { // Giran
	        map = (clanId == 4) ? KINGDOM_ORCISH_POSITION_CASE1 :
	              (clanId == 5) ? KINGDOM_ORCISH_POSITION_CASE2 : null;	        
	    } else if (idx == 4) { // Giran
	        map = (clanId == 4) ? KINGDOM_GIRAN_POSITION_CASE1 :
	              (clanId == 5) ? KINGDOM_GIRAN_POSITION_CASE2 : null;
	    } else if (idx == 5) { // Heine 추가
	        map = (clanId == 4) ? KINGDOM_HEINE_POSITION_CASE1 :
	              (clanId == 5) ? KINGDOM_HEINE_POSITION_CASE2 : null;
	    }

	    return (map != null) ? map.get(ri.getClassType()) : null;
	}

	/**
	 * 외성 내부에 잇는지 체크
	 * @return
	 */
	
	static public boolean isKingdomOutDoor04Location(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    // 배열 범위 체크
	    if (idx < 0 || idx >= KINGDOM_OUT_DOOR04_LOCATION.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (KINGDOM_OUT_DOOR04_LOCATION[idx][0] <= x && KINGDOM_OUT_DOOR04_LOCATION[idx][1] >= x &&
	            KINGDOM_OUT_DOOR04_LOCATION[idx][2] <= y && KINGDOM_OUT_DOOR04_LOCATION[idx][3] >= y && 
	            KINGDOM_OUT_DOOR04_LOCATION[idx][4] == map);
	}

	static public boolean isKingdomOutDoor08Location(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= KINGDOM_OUT_DOOR08_LOCATION.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (KINGDOM_OUT_DOOR08_LOCATION[idx][0] <= x && KINGDOM_OUT_DOOR08_LOCATION[idx][1] >= x &&
	            KINGDOM_OUT_DOOR08_LOCATION[idx][2] <= y && KINGDOM_OUT_DOOR08_LOCATION[idx][3] >= y && 
	            KINGDOM_OUT_DOOR08_LOCATION[idx][4] == map);
	}
	
	static public boolean isCastleOutside04Coords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= CASTLE_OUTSIDE_04_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (CASTLE_OUTSIDE_04_COORDS[idx][0] <= x && CASTLE_OUTSIDE_04_COORDS[idx][1] >= x &&
	            CASTLE_OUTSIDE_04_COORDS[idx][2] <= y && CASTLE_OUTSIDE_04_COORDS[idx][3] >= y && 
	            CASTLE_OUTSIDE_04_COORDS[idx][4] == map);
	}

	static public boolean isCastleOutside08Coords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= CASTLE_OUTSIDE_08_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (CASTLE_OUTSIDE_08_COORDS[idx][0] <= x && CASTLE_OUTSIDE_08_COORDS[idx][1] >= x &&
	            CASTLE_OUTSIDE_08_COORDS[idx][2] <= y && CASTLE_OUTSIDE_08_COORDS[idx][3] >= y && 
	            CASTLE_OUTSIDE_08_COORDS[idx][4] == map);
	}

	static public boolean isCastleInsideCoords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= CASTLE_INSIDE_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (CASTLE_INSIDE_COORDS[idx][0] <= x && CASTLE_INSIDE_COORDS[idx][1] >= x &&
	            CASTLE_INSIDE_COORDS[idx][2] <= y && CASTLE_INSIDE_COORDS[idx][3] >= y && 
	            CASTLE_INSIDE_COORDS[idx][4] == map);
	}

	static public boolean isCastleTopOutsideCoords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= CASTLE_TOP_OUTSIDE_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (CASTLE_TOP_OUTSIDE_COORDS[idx][0] <= x && CASTLE_TOP_OUTSIDE_COORDS[idx][1] >= x &&
	    		CASTLE_TOP_OUTSIDE_COORDS[idx][2] <= y && CASTLE_TOP_OUTSIDE_COORDS[idx][3] >= y && 
	    		CASTLE_TOP_OUTSIDE_COORDS[idx][4] == map);
	}
	
	static public boolean isCastleTopInsideCoords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= CASTLE_TOP_INSIDE_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (CASTLE_TOP_INSIDE_COORDS[idx][0] <= x && CASTLE_TOP_INSIDE_COORDS[idx][1] >= x &&
	            CASTLE_TOP_INSIDE_COORDS[idx][2] <= y && CASTLE_TOP_INSIDE_COORDS[idx][3] >= y && 
	            CASTLE_TOP_INSIDE_COORDS[idx][4] == map);
	}

	static public boolean isCastleTopCoords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= CASTLE_TOP_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (CASTLE_TOP_COORDS[idx][0] <= x && CASTLE_TOP_COORDS[idx][1] >= x &&
	            CASTLE_TOP_COORDS[idx][2] <= y && CASTLE_TOP_COORDS[idx][3] >= y && 
	            CASTLE_TOP_COORDS[idx][4] == map);
	}

	static public boolean isKingdomCrownCoords(object o, int idx) {
	    if (o == null) {
	        return false; // 객체가 null인 경우 false 반환
	    }
	    if (idx < 0 || idx >= KINGDOM_CROWN_COORDS.length) {
	        return false;
	    }
	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (KINGDOM_CROWN_COORDS[idx][0] <= x && KINGDOM_CROWN_COORDS[idx][1] >= x &&
	            KINGDOM_CROWN_COORDS[idx][2] <= y && KINGDOM_CROWN_COORDS[idx][3] >= y && 
	            KINGDOM_CROWN_COORDS[idx][4] == map);
	}

	static public boolean isKingdomCrownCoords(object o) {
	    if(o == null){
	        return false;
	    }
	    for (int[] i : KINGDOM_CROWN_COORDS) {
	        if (i[0] == 0)
	            continue;
	        if (i[0] <= o.getX() && i[1] >= o.getX() && i[2] <= o.getY() && i[3] >= o.getY() && i[4] == o.getMap())
	            return true;
	    }
	    return false;
	}

	// Entry Zone 안에 있는지 확인
	public static boolean isHeineEntryZone(object o, int zoneIdx) {
	    if (o == null) return false;
	    if (zoneIdx < 0 || zoneIdx >= KINGDOM_HEINE_ENTRY_ZONES.length) return false;

	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (KINGDOM_HEINE_ENTRY_ZONES[zoneIdx][0] <= x && x <= KINGDOM_HEINE_ENTRY_ZONES[zoneIdx][1] &&
	            KINGDOM_HEINE_ENTRY_ZONES[zoneIdx][2] <= y && y <= KINGDOM_HEINE_ENTRY_ZONES[zoneIdx][3] &&
	            KINGDOM_HEINE_ENTRY_ZONES[zoneIdx][4] == map);
	}

	// Escape Target 범위 안에 있는지 확인
	public static boolean isHeineEscapeTarget(object o, int targetIdx) {
	    if (o == null) return false;
	    if (targetIdx < 0 || targetIdx >= KINGDOM_HEINE_ESCAPE_TARGETS.length) return false;

	    int x = o.getX();
	    int y = o.getY();
	    int map = o.getMap();

	    return (KINGDOM_HEINE_ESCAPE_TARGETS[targetIdx][0] <= x && x <= KINGDOM_HEINE_ESCAPE_TARGETS[targetIdx][1] &&
	            KINGDOM_HEINE_ESCAPE_TARGETS[targetIdx][2] <= y && y <= KINGDOM_HEINE_ESCAPE_TARGETS[targetIdx][3] &&
	            KINGDOM_HEINE_ESCAPE_TARGETS[targetIdx][4] == map);
	}
	
	/**
	 * 마스터 주변으로 파티에 속한 모든 로봇을 텔레포트 (등록된 로봇만 대상)
	 * @param master - 파티 마스터
	 */
	public static void teleportToMaster(PcInstance master) {
	    if (master == null) return;

	    Party party = PartyController.find(master);
	    if (party == null) return;

	    int masterX = master.getX();
	    int masterY = master.getY();
	    int masterMap = master.getMap();

	    // 마스터 주변 8방향 좌표 미리 확보
	    List<int[]> baseLocations = new ArrayList<>();
	    for (int dx = -1; dx <= 1; dx++) {
	        for (int dy = -1; dy <= 1; dy++) {
	            if (dx == 0 && dy == 0) continue;
	            int tx = masterX + dx;
	            int ty = masterY + dy;
	            if (!World.isMapdynamic(tx, ty, masterMap)) {
	                baseLocations.add(new int[]{tx, ty});
	            }
	        }
	    }

	    // 텔레포트 대상이 될 모든 파티 로봇 순회
	    List<PcInstance> partyMembers = party.getList();
	    int idx = 0;

	    for (PcInstance member : partyMembers) {
	        if (!(member instanceof PartyRobotInstance)) continue;

	        PartyRobotInstance robot = (PartyRobotInstance) member;

	        if (robot.isDead() || robot.isWorldDelete()) continue;

	        // 위치 분배: baseLocations 리스트를 순서대로 사용 (충돌 최소화)
	        if (!baseLocations.isEmpty()) {
	            int[] loc = baseLocations.get(idx % baseLocations.size());
	            robot.toTeleport(loc[0], loc[1], masterMap, true);
	            robot.clearTarget();
	            idx++;
	        }
	    }
	}

	
	/**
	 * 공성전이 시작되면 호출됨.
	 */
	public static void toStartWar() {
	}
	
	/**
	 * 공성전이 종료되면 호출됨.
	 */
	public static void toStopWar() {
	}

	/**
	 * 무한 체력 회복 룬. 2025-01-18 by goldbitna
	 */
	static public void getHealingPotion(PcRobotInstance pi) {
		String item = "무한 체력 회복 룬";
		int count = 1;
		
		giveItem(pi, item, count);
	}

	
	/**
	 * 무한 신속 룬. 2025-01-18 by goldbitna
	 */
	static public void getHastePotion(PcRobotInstance pi) {
		String item = "무한 신속 룬";
		int count = 1;
		
		giveItem(pi, item, count);
	}

	/**
	 * 무한 가속 룬. 2025-01-18 by goldbitna
	 */
	static public void getBraveryPotion(PcRobotInstance pi) {
		String item = "무한 가속 룬";
		int count = 1;
		
		giveItem(pi, item, count);
	}

	/**
	 * 무한 와퍼. 2025-01-18 by goldbitna
	 */
	static public void getElvenWafer(PcRobotInstance pi) {
		String item = "무한 가속 룬";
		int count = 1;
		
		giveItem(pi, item, count);
	}

	/**
	 * 무한 변신 주문서. 2025-01-18 by goldbitna
	 */
	static public void getScrollPolymorph(PcRobotInstance pi) {
		String item = "무한 변신 주문서";
		int count = 1;
		
		giveItem(pi, item, count);
	}

	/**
	 * 무한의 화살통. 2025-01-18 by goldbitna
	 */
	static public void getArrow(PcRobotInstance pi) {
		String item = "무한의 화살통";
		int count = 1;
		
		giveItem(pi, item, count);
	}

	/**
	 * 무기. 2018-08-12 by connector12@nate.com
	 */
	static public Item getWeapon(int classType) {
		String weapon = null;
		String[] royal = { "일본도", "레이피어" };
		String[] knight = { "양손검", "대검" };
		String[] elf = { "크로스 보우", "장궁" };
		String[] wizard = { "힘의 지팡이", "마나의 지팡이" };

		switch (classType) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			weapon = royal[Util.random(0, royal.length - 1)];
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			weapon = knight[Util.random(0, knight.length - 1)];
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			weapon = elf[Util.random(0, elf.length - 1)];
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			weapon = wizard[Util.random(0, wizard.length - 1)];
			break;
		}

		return ItemDatabase.find(weapon);
	}

	static public void readPoly() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		list_poly.clear();

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot_poly");
			rs = st.executeQuery();
			while (rs.next()) {
				Poly p = PolyDatabase.getName(rs.getString("poly_name"));

				if (p == null)
					continue;

				RobotPoly rp = new RobotPoly();

				rp.setPoly(p);
				rp.setPolyClass(rs.getString("변신클래스"));

				synchronized (list_poly) {
					if (rs.getString("사용여부").equalsIgnoreCase("true"))
						list_poly.add(rp);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPoly()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}

	static public void reloadPoly() {
		TimeLine.start("_robot_poly 테이블 리로드 완료 - ");
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		list_poly.clear();

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM _robot_poly");
			rs = st.executeQuery();
			while (rs.next()) {
				Poly p = PolyDatabase.getName(rs.getString("poly_name"));

				if (p == null)
					continue;

				RobotPoly rp = new RobotPoly();

				rp.setPoly(p);
				rp.setPolyClass(rs.getString("변신클래스"));

				synchronized (list_poly) {
					if (rs.getString("사용여부").equalsIgnoreCase("true"))
						list_poly.add(rp);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPoly()\r\n", RobotController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		TimeLine.end();
	}

	static public List<RobotPoly> getPolyList() {
		return new ArrayList<RobotPoly>(list_poly);
	}

	/**
	 * 변신 가능한 목록이 있는지 확인. 2018-08-13 by connector12@nate.com
	 */
	static public boolean isPoly(RobotInstance pr) {
		for (RobotPoly rp : getPolyList()) {
			if (rp.getPoly().getMinLevel() <= pr.getLevel() && SpriteFrameDatabase.findGfxMode(rp.getPoly().getGfxId(), pr.getGfxMode() + Lineage.GFX_MODE_ATTACK))
				return true;
		}
		return false;
	}
	
	// 아이템 지급 메서드
	static public void giveItem(RobotInstance ri, String itemName, int count) {
	    // 인벤토리가 null이 아니고, 해당 아이템이 인벤토리에 없다면
	    if (ri.getInventory() != null && ri.getInventory().find(itemName) == null) {
	        // 아이템 이름으로 아이템 인스턴스를 찾고, 없으면 반환
	        ItemInstance i = ItemDatabase.newInstance(ItemDatabase.find(itemName));  
	        if (i == null) {
	            return;
	        }  
	        
	        // 아이템에 오브젝트 아이디 할당
	        long item_objectid = ServerDatabase.nextItemObjId(); // 새로운 오브젝트 아이디 생성
	        i.setObjectId(item_objectid); // 오브젝트 아이디 설정
	        
	        // 아이템 설정
	        i.setCount(count); // 아이템 수량 설정
	        i.setBless(1); // 축복 상태 설정
	        i.setDefinite(true); // 아이템 상태 확정

	        // 인벤토리에 아이템 추가
	        ri.getInventory().append(i, true);
	    }
	}

	
	public static int getWarCastleUid() {
	    for (Kingdom k : KingdomController.getList()) {
	        if (k.isWar()) {
	            return k.getUid();  // 전쟁 중인 성의 이름 반환
	        }
	    }
	    return -1;  // 전쟁 중인 성이 없으면 -1 반환
	}

	/**
	 * 현재 위치가 기란 마을인지 확인하는 메서드.
	 * 
	 * @param pr 확인 대상 RobotInstance
	 * @return pr이 기란 마을에 있으면 true, 아니면 false
	 */
	public static boolean isInVillage(RobotInstance pr) { 
	    return (pr.getX() == pr.getHomeX() && 
	            pr.getY() == pr.getHomeY() && 
	            pr.getMap() == pr.getHomeMap()) 
	            || World.isGiranHome(pr.getX(), pr.getY(), pr.getMap());        
	}
    
    /**
     * 특정 타입(int)의 멘트 리스트에서 랜덤으로 하나 선택
     * 
     * @param type 멘트 타입 (0~11)
     *             0  : 공격하다
     *             1  : 공격받다
     *             2  : 스킬사용
     *             3  : 스킬피격
     *             4  : 도망
     *             5  : 죽다
     *             6  : 죽임
     *             7  : 아이템
     *             8  : 공성
     *             9  : 수성
     *             10 : 마을
     *             11 : 조우
     *             12 : 앱솔
     *             13 : 캔슬
     *             14 : 이뮨
     *             15 : 디케이
     *             16 : 힐
     *             17 : 투망
     *             18 : 워터
     *             19 : 어바
     *             20 : 웨폰
     *             21 : 외성문
     *             22 : 에볼단장
     *             23 : 에볼단원
     *             24 : 마나부족
     *             25 : 먹자
     *             26 : 픽업
     * @return 해당 타입의 랜덤 멘트 문자열 (멘트가 없으면 null 반환)
     */
	public static String getRandomMent(int type) {
	    List<String> mentList = list_ment.get(type);

	    if (mentList == null || mentList.isEmpty()) {
	        return null; //  멘트가 없으면 null 반환
	    }

	    Random random = new Random();
	    return mentList.get(random.nextInt(mentList.size()));
	}

	/**
	 *  랜덤 채팅 메시지를 가져오고 즉시 출력
	 *
	 * @param type 멘트 타입 (0~99)
	 * @param sender 메시지를 보내는 객체
	 * @param target 메시지 대상
	 * @param mode 채팅 모드
	 * @param delay 출력 지연 시간
	 */
	public static void getRandomMentAndChat(int type, object sender, object target, int mode, long delay) {    
	    if (!list_ment.containsKey(type)) {
	        return;
	    }
	    
	    List<String> mentList = list_ment.get(type);
	    if (mentList == null || mentList.isEmpty()) {
	        return;
	    }

	    //  디버깅 로그 추가
	    if (sender instanceof Pk1RobotInstance) {
//	        lineage.share.System.println("[DEBUG]  getRandomMentAndChat 호출됨 - Type: " + type);
	    }

	    //  랜덤 멘트 선택
	    Random random = new Random();
	    String ment = mentList.get(random.nextInt(mentList.size()));

	    //  target이 null인데 & 포함된 멘트 재시도
	    int retryCount = 0;
	    while (target == null && ment.contains("&") && retryCount < 5) {
	        ment = mentList.get(random.nextInt(mentList.size()));
	        retryCount++;
	    }

	    //  target이 있을 때 & 치환
	    if (target != null && ment.contains("&")) {
	        ment = ment.replace("&", target.getName());
	    }

	    //  target의 클랜명을 %로 치환
	    if (target != null && ment.contains("%")) {
	        String clanName = target.getClanName();
	        retryCount = 0;

	        while ((clanName == null || clanName.isEmpty()) && ment.contains("%") && retryCount < 5) {
	            ment = mentList.get(random.nextInt(mentList.size()));
	            retryCount++;
	        }

	        if (clanName != null && !clanName.isEmpty()) {
	            ment = ment.replace("%", clanName);
	        }
	    }

	    //  채팅 가능 여부 확인 후 실행
	    if (sender != null && sender.isBuffChattingClose()) {
	        return;
	    }

	    //  PcInstance 또는 PcRobotInstance 대상에게만 멘트 전송
	    if (!(target instanceof PcInstance) && !(target instanceof Pk1RobotInstance)) {
//	        lineage.share.System.println("[DEBUG] ❌ 유효하지 않은 대상 - 멘트 출력 안 함: " + target.getClass().getSimpleName());
	        return;
	    }

	    //  멘트 큐 추가 여부 확인
	    if (RobotMentQueueThread.thread == null) {
//	        lineage.share.System.println("[DEBUG] ❌ RobotMentQueueThread가 null -> 초기화 시도");
	        RobotMentQueueThread.init();
	    } else {
//	        lineage.share.System.println("[DEBUG]  RobotMentQueueThread 정상 실행 중");
	    }

	    //  멘트 출력 로그 추가
//	    lineage.share.System.println("[DEBUG]  멘트 큐 추가됨 - " + ment);
	    RobotMentQueueThread.thread.addMent(type, sender, target, mode, delay, ment);
	}

	
	/**
	 * 특정 타입의 멘트를 가져와 채팅으로 출력 (아이템 포함)
	 *
	 * @param type 멘트 타입 (0~99)
	 * @param sender 메시지를 보내는 객체 (보통 this)
	 * @param item 드랍된 아이템 객체
	 * @param enchantLevel 아이템 인챈트 수치
	 * @param mode 채팅 모드 (Lineage.CHATTING_MODE_NORMAL)
	 */
	public static void getRandomMentAndChat(int type, object sender, ItemInstance item, int enchantLevel, int mode) {
	    String ment = getRandomMent(type);
	    if (ment == null || item == null || item.getItem() == null) return; //  멘트 or 아이템 정보가 없으면 종료

	    String itemName = item.getItem().getName();

	    // 인챈트 레벨이 0보다 크고 일반 아이템일 경우
	    if (enchantLevel > 0 && !item.getItem().getType1().equalsIgnoreCase("item")) {
	        itemName = enchantLevel + " " + itemName;
	    }

	    // 멘트 내용 중 & 를 아이템 이름으로 치환
	    if (ment.contains("&")) {
	        ment = ment.replace("&", itemName);
	    }

	    ChattingController.toChatting(sender, ment, mode);
	}
	
	public static final class RobotMoving {
	    private RobotMoving() {}

	    private static final long MIN_INTERVAL_MS = 120L;
	    private static final long MAX_INTERVAL_MS = 420L;

	    /**
	     * 이동 가능 여부
	     * @param pc              로봇(=PcInstance) 객체
	     * @param lastMovingTime  마지막 “성공 이동” 시각(ms)
	     * @param targetX         목적지 X (시그니처 호환용)
	     * @param targetY         목적지 Y (시그니처 호환용)
	     */
	    public static boolean isMoveValid(RobotInstance pc, long lastMovingTime, int targetX, int targetY) {
	        if (pc.isLock()) return false;
	        if (lastMovingTime == 0) return true; // 첫 이동은 허용

	        final long now = System.currentTimeMillis();
	        final long need = getWalkInterval(pc);
	        return (now - lastMovingTime) >= need;
	    }

	    /**
	     * “걷기 1스텝” 최소 간격(ms)
	     * - 엔진 프레임타임 × 전역배율을 기반으로 브레이브/스피드에 소폭 보정
	     * - 최종적으로 하한/상한 범위로 클램프
	     */
	    public static long getWalkInterval(RobotInstance pc) {
	        final int action = pc.getGfxMode() + Lineage.GFX_MODE_WALK;
	        int base = SpriteFrameDatabase.getGfxFrameTime(pc, pc.getGfx(), action);
	        if (base <= 0) base = 100;

	        long interval = (long) (base * Lineage.speed_check_walk_frame_rate);

	        // 체감 보정 (필요 시 미세 조정)
	        double braveFactor = pc.isBrave() ? 0.90 : 1.00;
	        double speedFactor = (pc.getSpeed() >= 1) ? 0.95 : 1.00;
	        interval = (long) (interval * braveFactor * speedFactor);

	        if (interval < MIN_INTERVAL_MS) interval = MIN_INTERVAL_MS;
	        else if (interval > MAX_INTERVAL_MS) interval = MAX_INTERVAL_MS;

	        return interval;
	    }
	}
}

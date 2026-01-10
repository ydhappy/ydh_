package lineage.world.object.instance;

import java.util.List;
import java.util.Random;

import lineage.bean.database.MagicdollList;
import lineage.bean.lineage.Doll;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectRemove;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;

public class MagicDollInstance extends object {

    private static final String[] PRINCE_MESSAGES_66 = {"다그닥~ 다그닥~", "으랴~~ 으랴~~"};
    private static final String[] PRINCE_MESSAGES_67 = {"여긴 앞으로 내 영지다!", "진격~!! 앞으로!!", "먼가 재밌는 일이 없을까?"};
    private static final String[] PRINCESS_MESSAGES_66 = {"어디든 함께 해요~", "토각 토각"};
    private static final String[] PRINCESS_MESSAGES_67 = {"응?~", "신난다아~", "헤에~"};
    
    private static final String[] KNIGHT_MESSAGES_66 = {"당당하게!", "씩씩하게!", "이 몸은 준비 됐다고!"};
    private static final String[] KNIGHT_MESSAGES_67 = {"후후후.", "받아라! 우왓! 에이!"};
    private static final String[] KNIGHTSS_MESSAGES_66 = {"벌컥벌컥~ 흐하하~", "용기의 물약이 최고야~ 흐아아~", "후후훗."};
    private static final String[] KNIGHTSS_MESSAGES_67 = {"난 준비되었어.", "어디든 가자구~", "늠름하게~"};
    
    private static final String[] SORCERE_MESSAGES_66 = {"우움?~", "다박~ 다박~"};
    private static final String[] SORCERE_MESSAGES_67 = {"두리번.. 두리번..", "작아지니 발걸음이 가볍네!~"};
    private static final String[] SORCERESS_MESSAGES_66 = {"랄라라~ 랄라~", "리듬을 타고~"};
    private static final String[] SORCERESS_MESSAGES_67 = {"또각 또각", "우아~ 하게~~"};
    
    private static final String[] ELF_MESSAGES_66 = {"착착착~"};
    private static final String[] ELF_MESSAGES_67 = {"??", "같이가요~"};
    private static final String[] ELFSS_MESSAGES_66 = {"헤에~~", "정말 아름다운 세상이에요~"};
    private static final String[] ELFSS_MESSAGES_67 = {"나비네?~ 어맛~ 벌레가 싫엇!", "사뿐 사뿐~"};

    private Doll doll;
    private MagicdollList mdl;
    private long time_start; // 소환된 시간
    private long time_end; // 종료될 시간.
    private long actionTime; // 액션딜레이
    private int lastAction; // 연속으로 같은 액션 막기위한 변수
    private AStar aStar; // 길찾기 변수
    private Node tail; // 길찾기 변수
    private int[] iPath; // 길찾기 변수

    public MagicDollInstance() {
        aStar = new AStar();
        iPath = new int[2];
    }

    static synchronized public MagicDollInstance clone(MagicDollInstance mdi, Doll doll, MagicdollList mdl) {
        if (mdi == null)
            mdi = new MagicDollInstance();
        // 걷기모드로 변경.
        mdi.setAiStatus(Lineage.AI_STATUS_WALK);
        // 소환된 시간과 종료될 시간 처리하기.
        mdi.time_start = System.currentTimeMillis();
        mdi.time_end = mdi.time_start + (1000 * mdl.getDollContinuous());
        // 액션 취할 딜레이
        mdi.actionTime = System.currentTimeMillis();
        mdi.lastAction = 0;
        mdi.doll = doll;
        mdi.mdl = mdl;
        return mdi;
    }

    public void setTime(int time) {
        if (time > 0)
            time_end = System.currentTimeMillis() + (time * 1000);
        else
            time_end = time;
    }

    public int getTime() {
        return time_end > 0 ? (int) ((time_end - System.currentTimeMillis()) * 0.001) : (int) time_end;
    }

    public long getTimeEnd() {
        return time_end;
    }

    public long getTimeStart() {
        return time_start;
    }

    public MagicdollList getMDL() {
        return mdl;
    }

    @Override
    public void close() {
        super.close();
        doll = null;
        time_end = actionTime = 0L;
        lastAction = 0;
    }

    @Override
    public void setInvis(boolean invis) {
        if (isInvis() == invis)
            return;
        super.setInvis(invis);
        if (!worldDelete) {
            if (isInvis())
                toSender(S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), this), false);
            else
                toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this), false);
        }
    }

	@Override
	public void toMoving(final int x, final int y, final int h) {
		if (isInvis() == false) {
			super.toMoving(x, y, h);
			return;
		}
		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(this.x, this.y, this.map, false);
		// 좌표 변경.
		this.x = x;
		this.y = y;
		this.heading = h;
		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(x, y, map, true);
		// 주변객체 갱신
		if (!Util.isDistance(tempX, tempY, map, x, y, map, Lineage.SEARCH_LOCATIONRANGE)) {
			tempX = x;
			tempY = y;
			// 이전에 관리중이던 목록 갱신
			List<object> temp = getAllList();
			clearAllList();
			for (object o : temp)
				o.removeAllList(this);
			// 객체 갱신
			temp.clear();
			World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp);
			for (object o : temp) {
				if (isList(o)) {
					// 전체 관리목록에 등록.
					appendAllList(o);
					o.appendAllList(this);
				}
			}
		}
	}


    @Override
    protected void toAiWalk(long time) {
        super.toAiWalk(time);

        if (!Util.isDistance(this, doll.getMaster(), Lineage.magicdoll_location)) {
            setSpeed(1);
            setBrave(true);
            aStar.cleanTail();
            tail = aStar.searchTail(this, doll.getMaster().getX(), doll.getMaster().getY(), false);
            if (tail != null) {
                while (tail != null) {
                    if (tail.x == getX() && tail.y == getY())
                        break;
                    iPath[0] = tail.x;
                    iPath[1] = tail.y;
                    tail = tail.prev;
                }
                toMoving(iPath[0], iPath[1], Util.calcheading(this.x, this.y, iPath[0], iPath[1]));
            }
        } else {
            if (actionTime + (1000 * (Util.random(15, 20))) < System.currentTimeMillis()) {
                int count = 0;

                while (true) {
                    if (count++ > 50)
                        break;

                    int tempGfxMode = Lineage.magicDollAction[Util.random(0, Lineage.magicDollAction.length - 1)];
                    if (SpriteFrameDatabase.findGfxMode(getGfx(), tempGfxMode) && lastAction != tempGfxMode) {
                        lastAction = tempGfxMode;
                        actionTime = System.currentTimeMillis();
                        super.toAiMagicDollAction(tempGfxMode);
                        toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, tempGfxMode), true);
                        
                        // 액션에 따른 멘트 출력
                        String message = getActionMessage(tempGfxMode);
                        if (message != null) {
                            ChattingController.toChatting(this, message, Lineage.CHATTING_MODE_NORMAL);
                        }
                        break;
                    }
                }
            }
        }
    }

    // 액션에 따라 멘트를 반환하는 메서드toAiPickup(time);
    private String getActionMessage(int action) {
        String[] selectedMessages = null;

        if (mdl != null && mdl.getDollBuffType() != null) {
            if (mdl.getDollBuffType().equalsIgnoreCase("왕자 인형")) {
                if (action == 66) {
                    selectedMessages = PRINCE_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = PRINCE_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("공주 인형")) {
                if (action == 66) {
                    selectedMessages = PRINCESS_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = PRINCESS_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("남기사 인형")) {
                if (action == 66) {
                    selectedMessages = KNIGHT_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = KNIGHT_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("여기사 인형")) {
                if (action == 66) {
                    selectedMessages = KNIGHTSS_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = KNIGHTSS_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("남마법사 인형")) {
                if (action == 66) {
                    selectedMessages = SORCERE_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = SORCERE_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("여마법사 인형")) {
                if (action == 66) {
                    selectedMessages = SORCERESS_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = SORCERESS_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("남요정 인형")) {
                if (action == 66) {
                    selectedMessages = ELF_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = ELF_MESSAGES_67;
                }
            } else if (mdl.getDollBuffType().equalsIgnoreCase("여요정 인형")) {
                if (action == 66) {
                    selectedMessages = ELFSS_MESSAGES_66;
                } else if (action == 67) {
                    selectedMessages = ELFSS_MESSAGES_67;
                }
            }
        }

        if (selectedMessages != null) {
            Random random = new Random();
            return selectedMessages[random.nextInt(selectedMessages.length)];
        }
        return null;
    }
}
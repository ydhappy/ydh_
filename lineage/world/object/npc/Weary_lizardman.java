package lineage.world.object.npc;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lineage.util.Util;
import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Weary_lizardman extends QuestInstance {

    private AStar aStar;
    private Node tail;
    private int[] iPath = new int[2];
    private static final int DISTANCE_THRESHOLD = 2;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;

    public Weary_lizardman(Npc npc) {
        super(npc);
        this.aStar = new AStar();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    protected void toAiWalk(long time) {
        super.toAiWalk(time);
    }

    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
    	
		if (pc.getLevel() < 39) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "llizard1a"));

		} else {
			
		}
        Quest q = QuestController.find(pc, Lineage.QUEST_LELDER);
        if (q == null)
            q = QuestController.newQuest(pc, this, Lineage.QUEST_LELDER);

        switch (q.getQuestStep()) {
        case 0:
            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "llizard1a"));
            break;
        case 1:
            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "llizard1b"));
            break;
		case Lineage.QUEST_END:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "llizard1a"));
			break;
        }
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        if (action.equalsIgnoreCase("start")) {
            for (object o : getInsideList()) {
                if (o instanceof PcInstance && !Util.isDistance(this, o, DISTANCE_THRESHOLD)) {
                    toStay();
                }
                startAutoStayForDuration((PcInstance) o);
                pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
            }
        }
    }

    private void toStay() {
        Random rand = new Random();
        List<object> insideList = getInsideList();
        if (insideList != null) {
            for (object o : insideList) {
                if (o instanceof PcInstance) {
                    PcInstance pc = (PcInstance) o;

                    int offsetX = rand.nextInt(3) - 1;
                    int offsetY = rand.nextInt(3) - 1;
                    int targetX = pc.getX() + offsetX;
                    int targetY = pc.getY() + offsetY;

                    aStar.cleanTail();
                    tail = aStar.searchTail(this, targetX, targetY, false);
                    if (tail != null) {
                        while (tail != null) {
                            if (tail.x == this.getX() && tail.y == this.getY())
                                break;
                            iPath[0] = tail.x;
                            iPath[1] = tail.y;
                            tail = tail.prev;
                        }
                        toMoving(iPath[0], iPath[1], Util.calcheading(this.getX(), this.getY(), iPath[0], iPath[1]));
                    }
                }
            }
        }
    }

    private void startAutoStayForDuration(PcInstance targetPc) {
        scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                toStay();
                count++;
                if (count >= 18) {
                    scheduledFuture.cancel(false);

                    giveItemToPC(targetPc);
                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            moveToSpawn();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void giveItemToPC(PcInstance pc) {
        if (pc == null)
        	return;
        Item i = ItemDatabase.find("리자드맨의 보고서");
		if (i != null) {
			ItemInstance crown = ItemDatabase.newInstance(i);
			if (pc != null && Util.isDistance(this, pc, 4)) {
				// 인벤에 등록처리.
				pc.getInventory().append(crown, true);
				ChattingController.toChatting(pc, String.format("지친 리자드맨 전사가 당신에게 %s를 주었습니다.", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
			}
        }
    }

    private void stopAutoStay() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    private void moveToSpawn() {
        if (x != homeX || y != homeY) {
            toMoving(homeX, homeY, homeHeading);
            return;
        }
    }

    @Override
    public void finalize() {
        stopAutoStay();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
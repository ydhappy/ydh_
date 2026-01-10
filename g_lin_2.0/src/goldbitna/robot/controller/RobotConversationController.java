package goldbitna.robot.controller;

import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.PcInstance;

import java.util.*;
import java.util.concurrent.*;

import goldbitna.ConversationSession;

public class RobotConversationController {

    public static final int ROBOT_STATE_IDLE = 0x00;
    public static final int ROBOT_STATE_CHATTING = 0x01;
    private static final long CONVERSATION_TIMEOUT = 10_000L;

    private static final Map<RobotInstance, ConversationSession> activeConversations = new ConcurrentHashMap<>();

    public static void registerConversation(RobotInstance robot, PcInstance user, long now) {
        activeConversations.put(robot, new ConversationSession(user, now));
        robot.setRobotStatus(robot.getRobotStatus() | ROBOT_STATE_CHATTING);
    }

    public static void updateConversation(RobotInstance robot) {
        ConversationSession session = activeConversations.get(robot);
        if (session != null) {
            session.updateActivity();
        }
    }

    public static boolean isActiveConversation(RobotInstance robot, PcInstance user) {
        ConversationSession session = activeConversations.get(robot);
        return session != null && session.getUser().equals(user)
                && System.currentTimeMillis() - session.getLastActiveTime() < CONVERSATION_TIMEOUT;
    }

    public static boolean isActiveWithOtherUser(RobotInstance robot, PcInstance user) {
        ConversationSession session = activeConversations.get(robot);
        return session != null && !session.getUser().equals(user)
                && System.currentTimeMillis() - session.getLastActiveTime() < CONVERSATION_TIMEOUT;
    }

    public static void endConversationLater(RobotInstance robot) {
        ConversationSession session = activeConversations.get(robot);
        if (session != null) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                endConversation(robot);
            }, 1, TimeUnit.SECONDS);
        }
    }

    public static void terminateOthers(PcInstance user, RobotInstance exclude) {
        for (Map.Entry<RobotInstance, ConversationSession> entry : activeConversations.entrySet()) {
            if (!entry.getKey().equals(exclude) && entry.getValue().getUser().equals(user)) {
                endConversationLater(entry.getKey());
            }
        }
    }

    public static void checkTimeout(long now) {
        Iterator<Map.Entry<RobotInstance, ConversationSession>> it = activeConversations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<RobotInstance, ConversationSession> entry = it.next();
            if (now - entry.getValue().getLastActiveTime() >= CONVERSATION_TIMEOUT) {
                endConversation(entry.getKey());
                it.remove();
            }
        }
    }

    private static void endConversation(RobotInstance robot) {
        long before = robot.getRobotStatus();
        robot.setRobotStatus(before & ~ROBOT_STATE_CHATTING);
        activeConversations.remove(robot);
    }
}

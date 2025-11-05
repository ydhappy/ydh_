package lineage.thread;

import java.util.concurrent.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

import lineage.world.controller.ChattingController;
import lineage.world.controller.RobotController;
import lineage.world.object.object;
import lineage.world.object.instance.RobotInstance;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public class RobotMentQueueThread implements Runnable {
    static public RobotMentQueueThread thread;
    static private volatile boolean running;
    private final ScheduledExecutorService scheduler;
    private final Queue<MentTask> messageQueue;
    private final ConcurrentHashMap<object, Long> chattedMeetTargets = new ConcurrentHashMap<>();

    public RobotMentQueueThread() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    static public void init() {
        TimeLine.start("RobotMentQueueThread..");
        start();
        TimeLine.end();
    }

    static public void start() {
        if (thread == null || !running) {
            thread = new RobotMentQueueThread();
            running = true;
            Thread t = new Thread(thread);
            t.setName("RobotMentQueueThread");
            t.start();
        }
    }

    static public void close() {
        running = false;
        if (thread != null) {
            try {
                thread.scheduler.shutdown();
                if (!thread.scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    thread.scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                thread.scheduler.shutdownNow();
            }
            thread = null;
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                MentTask task = messageQueue.peek();

                if (task != null) {
                    boolean isAITalk = task.mentType == Lineage.AI_TALK_MENT;

                    if (task.sender instanceof RobotInstance) {
                        RobotInstance pr = (RobotInstance) task.sender;
                        if (RobotController.isInVillage(pr) && !isAITalk) {
                            messageQueue.poll();
                            continue;
                        }
                    }

                    messageQueue.poll();

                    try {
                        scheduler.schedule(() -> {
                            try {
                                processTask(task);
                            } catch (Exception innerEx) {
                                // Silent fail
                            }
                        }, task.delay, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        // Silent fail
                    }
                }

                Thread.sleep(Common.THREAD_SLEEP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addMent(int mentType, object sender, object target, int chatMode, long delay, String ment) {
        if (sender == null || target == null || ment == null || ment.trim().isEmpty()) return;

        if (sender instanceof RobotInstance) {
            RobotInstance pr = (RobotInstance) sender;
            if (RobotController.isInVillage(pr) && mentType != Lineage.AI_TALK_MENT) return;
        }

        if (mentType == Lineage.AI_MEET_MENT) {
            long lastChatTime = chattedMeetTargets.getOrDefault(target, 0L);
            if (System.currentTimeMillis() - lastChatTime < 30000) {
                return;
            }
        }

        if (messageQueue.stream().anyMatch(task -> task.mentType == mentType && task.sender == sender && task.target == target)) {
            return;
        }

        messageQueue.offer(new MentTask(mentType, sender, target, chatMode, delay, ment));
    }

    private void processTask(MentTask task) {
        try {
            if (task.sender == null || task.target == null) return;

            if (task.sender instanceof RobotInstance) {
                RobotInstance pr = (RobotInstance) task.sender;
                if (RobotController.isInVillage(pr) && task.mentType != Lineage.AI_TALK_MENT) return;
            }

            String ment = task.ment;
            if (ment == null || ment.trim().isEmpty()) return;

            if (task.target != null) {
                if (ment.contains("&")) {
                    String targetName = (task.target.getName() != null) ? task.target.getName() : "";
                    ment = ment.replace("&", targetName);
                }
                if (ment.contains("%")) {
                    String clanName = (task.target.getClanName() != null) ? task.target.getClanName() : "";
                    ment = ment.replace("%", clanName);
                }
            }

            ChattingController.toChatting(task.sender, ment, task.chatMode);

            if (task.mentType == Lineage.AI_MEET_MENT) {
                chattedMeetTargets.put(task.target, System.currentTimeMillis());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MentTask {
        int mentType;
        object sender;
        object target;
        int chatMode;
        long delay;
        String ment;

        public MentTask(int mentType, object sender, object target, int chatMode, long delay, String ment) {
            this.mentType = mentType;
            this.sender = sender;
            this.target = target;
            this.chatMode = chatMode;
            this.delay = delay;
            this.ment = ment;
        }
    }
}
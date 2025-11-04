package goldbitna;

import lineage.world.object.instance.PcInstance;

public class ConversationSession {
    private final PcInstance user;
    private long lastActiveTime;

    public ConversationSession(PcInstance user, long timestamp) {
        this.user = user;
        this.lastActiveTime = timestamp;
    }

    public PcInstance getUser() {
        return user;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void updateActivity() {
        this.lastActiveTime = System.currentTimeMillis();
    }
}

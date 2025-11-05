package lineage.world.object.item;

import java.util.Calendar;

import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ThebeKey extends ItemInstance {

    private long endTime = 0L;
    private static final int DURATION = 10800 * 1000; // 3시간(밀리초 단위)

    public long getEndTime() {
        return getLimitTime();
    }

    public void setEndTime(long time) {
        endTime = time;
    }

    @Override
    public void toPickup(Character cha) {
        if (getLimitTime() == 0) {
            setLimitTime(System.currentTimeMillis() + DURATION);
        }
        super.toPickup(cha);
    }
}

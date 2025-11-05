package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.object.instance.MonsterInstance;

public class Ghoul extends MonsterInstance {

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Ghoul();
        return MonsterInstance.clone(mi, m);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void toTeleport(int x, int y, int map, boolean effect) {
        if (map == 201) { // 201번 맵인지 체크
            // 맵 201번에서만 적용되는 특수 로직
            setDead(false);
            setNowHp(getMaxHp());
            setNowMp(getMaxMp());
            setGfx(getClassGfx());
            setGfxMode(getClassGfxMode());
            setAiStatus(Lineage.AI_STATUS_WALK);
            setHeading(getHomeHeading());
        }
        // 기본 동작 수행
        super.toTeleport(x, y, map, effect);
    }

    @Override
    protected void toAiDead(long time) {
        super.toAiDead(time);
        // 상태 변환
        setAiStatus(Lineage.AI_STATUS_CORPSE);
    }
}
package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

import java.util.Random;

public class Succubus extends MonsterInstance {

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Succubus();
        return MonsterInstance.clone(mi, m);
    }

    /**
     * 플레이어 캐릭터를 찾아서 공격목록에 넣는 함수.
     */
    private boolean toSearchHuman() {
        boolean find = false;
        for (object o : getInsideList(true)) {
            if (o instanceof PcInstance) {
                PcInstance pc = (PcInstance) o;
                // 죽은 캐릭터는 무시
                if (pc.isDead()) {
                    continue;
                }
                if (!Util.isDistance(this, o, 5)) {
                    toTeleport(pc);
                }
                if (isAttack(pc, true)) {
                    addAttackList(pc);
                    hasShouted = false;
                    find = true;
                }
            }
        }
        return find;
    }
    
    @Override
    protected void toAiWalk(long time) {
        super.toAiWalk(time);
        if (toSearchHuman())
            return;
    }

    @Override
    public boolean isAttack(Character cha, boolean magic) {
        if (getGfxMode() != getClassGfxMode())
            return false;
        return super.isAttack(cha, magic);
    }

    /**
     * 몬스터를 플레이어 캐릭터 주변 2셀에서 3셀 범위로 랜덤하게 텔레포트하는 함수
     * 
     * @param pc
     */
    private void toTeleport(PcInstance pc) {
        Random random = new Random();
        boolean validPositionFound = false;
        int attempts = 0;

        while (!validPositionFound && attempts < 10) {
            int offset = 1 + random.nextInt(2);
            int offsetX = (random.nextBoolean() ? offset : -offset);
            int offsetY = (random.nextBoolean() ? offset : -offset);

            int newX = pc.getX() + offsetX;
            int newY = pc.getY() + offsetY;
            int map = pc.getMap();

            if (!isNotMovingTile(newX, newY, map)) {
                this.toTeleportRange(newX, newY, map, true, 0);
                validPositionFound = true;
            }
            attempts++;
        }

        if (!validPositionFound) {
        }
    }

    /**
     * 이동 불가능한 타일 여부.
     * 
     */
    static public boolean isNotMovingTile(int x, int y, int map) {
        int tile = World.get_map(x, y, map);
        return tile == 0 || tile == 4 || tile == 8 || tile == 12 || tile == 16 || tile == 127;
    }
    private boolean hasShouted = false;
}
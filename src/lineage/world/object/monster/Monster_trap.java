package lineage.world.object.monster;

import java.util.Random;

import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Monster_trap extends MonsterInstance {

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Monster_trap();
        return MonsterInstance.clone(mi, m);
    }

    @Override
    protected void toAiWalk(long time) {
        if (isPlayerInside()) {
            super.toAiWalk(time);
        }
    }

    @Override
    protected void toAiDead(long time){
        super.toAiDead(time);
        // 상태 변환
        setAiStatus(Lineage.AI_STATUS_CORPSE);
    }

    private boolean isPlayerInside() {
        for (object o : getInsideList()) {
            if (o instanceof PcInstance && Util.isDistance(this, o, 1)) {
                spawnRaverbonMonsters();
                toAiThreadDelete();
                World.removeMonster(this);
                World.remove(this);
                setAiStatus(Lineage.AI_STATUS_SPAWN);
                return true;
            }
        }
        return false;
    }

    private void spawnRaverbonMonsters() {
        String[][] monsterGroups = null;

        switch (map) {
            case 810: // 4층
                monsterGroups = new String[][]{{"사악한 셸로브", "사악한 버그베어"}};
                break;
            case 811: // 5층
                monsterGroups = new String[][]{{"사악한 버그베어", "사악한 셸로브"}};
                break;
            case 812: // 6층
                monsterGroups = new String[][]{{"칠흑의 구울", "칠흑의 켈베로스"}};
                break;
            case 813: // 7층
                monsterGroups = new String[][]{{"칠흑의 켈베로스", "칠흑의 구울"}};
                break;
            // 상아탑
            case 285: // 4층
                monsterGroups = new String[][]{{"[상아탑 6층] 상아탑 유령", ""}};
                break;
            case 286: // 5층
                monsterGroups = new String[][]{{"[상아탑 6층] 상아탑 유령", ""}};
                break;
            case 287: // 6층
                monsterGroups = new String[][]{{"", "[상아탑 6층] 상아탑 유령"}};
                break;
            case 288: // 7층
                monsterGroups = new String[][]{{"[상아탑 8층] 상아탑 레서 데몬", "[상아탑 6층] 상아탑 유령"}};
                break;
            case 289: // 8층
                monsterGroups = new String[][]{{"[상아탑 6층] 상아탑 유령", "[상아탑 8층] 상아탑 레서 데몬"}};
                break;
            default:
                break;
        }

        if (monsterGroups != null) {
            Random random = new Random();
            String[] monsters = monsterGroups[0];
            if (monsters.length == 2) {
                String chosenMonster = monsters[random.nextInt(2)];
                if (!chosenMonster.isEmpty()) {
                    spawnRaverbonMonster(chosenMonster, 3);
                }
            }
        }
    }

    private void spawnRaverbonMonster(String monsterName, int count) {
        for (int i = 0; i < count; i++) {
            MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(monsterName));

            if (mi == null) {
                continue;
            }

            int offsetX = Util.random(-5, 0); // 스폰 위치 반경 확대
            int offsetY = Util.random(0, +5);
            int newX = x + offsetX;
            int newY = y + offsetY;

            if (!World.isThroughObject(newX, newY, map, 0) || World.isNotMovingTile(newX, newY, map)) {
                newX = x;
                newY = y;
            }

            // 랜덤 방향 설정
            mi.setDirection(randomDirection());

            mi.toTeleport(newX, newY, map, false);
            AiThread.append(mi);
            World.appendMonster(mi);
        }
    }

    private int randomDirection() {
        Random random = new Random();
        return random.nextInt(8); // 8개의 가능한 방향 (0~7)을 가정
    }
}
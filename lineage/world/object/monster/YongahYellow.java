package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.util.Util;
import lineage.world.controller.LocationController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.magic.Teleport;

public class YongahYellow extends MonsterInstance {

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new YongahYellow();
        return MonsterInstance.clone(mi, m);
    }

    @Override
    public void toDamage(Character cha, int dmg, int type, Object... cbp) {
        super.toDamage(cha, dmg, type);

        int currentMap = cha.getMap();
        switch (currentMap) {
            case 807:
            case 808:
            case 809:
            case 810:
            case 811:
            case 812:
                cha.toTeleport(cha.getHomeX(), cha.getHomeY(), currentMap + 1, true);
                return;
        }

        if (SkillController.isMagic(cha, null, false)) {
            if (LocationController.isTeleportVerrYedHoraeZone(cha, true)) {
                Util.toRndLocation(cha);
            }
            cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
            return;
        }

        Teleport.unLock(cha, true);
    }
}
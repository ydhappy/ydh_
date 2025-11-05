package lineage.world.object.magic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;

import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class PoisonTornado {

	private static final int MAX_DIRECTIONS = 4;
    /**
     * 몬스터가 사용하는 함수.
     * @param mi
     * @param skill
     * @param action
     */
    static public void init(MonsterInstance mi, MonsterSkill ms, int action, int effect){
    	if (SkillController.isMagic(mi, ms, true))
        toBuff(mi, ms.getSkill(), action, ms.getRange(), effect, Util.random(ms.getMindmg(), ms.getMaxdmg()));
    }

    /**
     * 중복코드 방지 함수.
     * 
     * @param cha
     * @param skill
     * @param action
     */
    static private void toBuff(Character cha, Skill skill, int action, int area, int effect2, double alpha_dmg) {
        List<object> list = new ArrayList<object>();

        // 시전자 좌표
        int cx = cha.getX();
        int cy = cha.getY();
        
        int[][] directions = {
                {cx + 2, cy - 2},
                {cx + 2, cy + 2},
                {cx - 2, cy + 2},
                {cx - 2, cy - 2}
         };

        // 주변 객체 추출 및 데미지 처리.
        for (object o : cha.getInsideList()) {
            if (Util.isDistance(cha, o, area) && o instanceof Character) {
                // 데미지 처리
                int dmg = SkillController.getDamage(cha, cha, o, skill, alpha_dmg, skill.getElement());
                DamageController.toDamage(cha, o, dmg, Lineage.ATTACK_TYPE_MAGIC);
                if (dmg > 0) {
                    list.add(o);
                }
            }
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(MAX_DIRECTIONS);
        
        CompletableFuture<Void> motionFuture = CompletableFuture.runAsync(() -> {
            cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_ALT_ATTACK), true);
        }, executor);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int[] dir : directions) {
            int x = dir[0];
            int y = dir[1];
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                cha.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), cha, null, list, true, action, 0, 196, x, y), cha instanceof PcInstance);
            }, executor);
            futures.add(future);
        }
        
        CompletableFuture<Void> allOf = motionFuture.thenCombine(CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])), (m, e) -> null);
        try {
            allOf.get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
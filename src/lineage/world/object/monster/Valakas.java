package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;

public class Valakas extends MonsterInstance {

    static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
        if (mi == null)
            mi = new Valakas();
        return MonsterInstance.clone(mi, m);
    }

    @Override
    public void toAiAttack(long time) {
        // HP가 30% 이하일 때 도망 모드로 전환하지 않고 계속 공격을 유지하도록 수정.
        super.toAiAttack(time);
    }

    @Override
    public boolean isAttack(Character cha, boolean magic) {
        // GfxMode와 상관없이 공격하도록 수정.
        return super.isAttack(cha, magic);
    }

    @Override
    public void toDamage(Character cha, int dmg, int type, Object... opt) {
        super.toDamage(cha, dmg, type);
        
        // 데미지를 받은 후 이펙트를 표시
        showDamageEffect();
       // toAiAttack(System.currentTimeMillis());
    }

    private void showDamageEffect() {
        // 이펙트를 표시하는 코드
        toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 1248), false);
    }
}
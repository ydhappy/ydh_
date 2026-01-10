package lineage.world.object.magic;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.Magic;

public class Thebanmagic extends Magic {

    public Thebanmagic(Character cha) {
        super(cha, null);
    }

    private int effect;
    private int damage;
    private int alpha;

    public void setEffectNumber(int num, int damage, int alpha) {
        this.effect = num;
        this.damage = damage;
        this.alpha = alpha;
    }

    @Override
    public void toBuff(object o) {
        if (o.isDead()) {
            System.out.println("Target is dead");
            return;
        }
        if (o.isBuffAbsoluteBarrier()) {
            System.out.println("Target has an absolute barrier");
            return;
        }
        if (o.isLockHigh()) {
            System.out.println("Target is locked high");
            return;
        }

        // 디버깅 로그 추가
        System.out.println("Applying effect: " + effect + " with damage: " + damage + " and alpha: " + alpha);

        o.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), o, Lineage.GFX_MODE_DAMAGE), true);
        o.toSender(new S_ObjectEffect(o, effect), true);

        // 로그 추가
        System.out.println("Sending effect to target");
        
        o.setNowHp(o.getNowHp() - (damage + Util.random(-alpha, alpha)));
        
        // 로그 추가
        System.out.println("New HP: " + o.getNowHp());
    }
}
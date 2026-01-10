package lineage.world.object.item.weapon;

import lineage.network.packet.server.S_ObjectEffect;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Theban extends ItemWeaponInstance {

    static synchronized public ItemInstance clone(ItemInstance item){
        if(item == null)
            item = new Theban();
        return item;
    }

    @Override
    public boolean toDamage(Character cha, object o){
        // 대상이 존재하고, 몬스터 또는 플레이어일 경우에만 데미지 적용
        if(o != null && (o instanceof MonsterInstance || o instanceof PcInstance)){
            // 무기별 처리 분리
            switch (getItem().getNameIdNumber()) {
                case 5718: // 테베 오시리스의 이도류
                    applyAdditionalDamage(cha, o, 10, 1, 10);
                    break;
                case 5719: // 테베 오시리스의 양손검
                    applyAdditionalDamage(cha, o, 10, 5, 15);
                    break;
                case 5720: // 테베 오시리스의 활
                    applyAdditionalDamage(cha, o, 10, 2, 12);
                    break;
                case 5721: // 테베 오시리스의 지팡이
                    applyAdditionalDamage(cha, o, 10, 3, 8); 
                    break;
                case 14135: // 테베 오시리스의 한손검
                    applyAdditionalDamage(cha, o, 10, 4, 10);
                    break;
                case 6428: // 쿠쿨칸의 창
                	AdditionalDamage(cha, o, 10, 4, 10);
                    break;
                default:
                    // 기본 확률 및 데미지 (예: 10% 확률로 1~5 데미지)
                    applyAdditionalDamage(cha, o, 10, 1, 5);
                    break;
            }
        }
        return false;
    }

    private void applyAdditionalDamage(Character cha, object o, int chance, int minDamage, int maxDamage) {
        // 추가 데미지를 적용할 확률 계산
        if(Util.random(0, 100) < chance){
            // minDamage ~ maxDamage 랜덤 데미지 추출
            int damage = Util.random(minDamage, maxDamage);
            // 인첸트 수치만큼 +@
            if(getEnLevel() > 0){
                damage += getEnLevel();
                if(getEnLevel() > 6)
                    damage += getEnLevel() - 6;
            }
            // 이펙트 발동
            o.toSender(new S_ObjectEffect(o, 7025), true);
            // 대상의 현재 HP에서 데미지만큼 감소
            o.setNowHp(o.getNowHp() - damage);
        }
    }
    
    private void AdditionalDamage(Character cha, object o, int chance, int minDamage, int maxDamage) {
        // 추가 데미지를 적용할 확률 계산
        if(Util.random(0, 100) < chance){
            // minDamage ~ maxDamage 랜덤 데미지 추출
            int damage = Util.random(minDamage, maxDamage);
            // 인첸트 수치만큼 +@
            if(getEnLevel() > 0){
                damage += getEnLevel();
                if(getEnLevel() > 6)
                    damage += getEnLevel() - 6;
            }
            // 이펙트 발동
            o.toSender(new S_ObjectEffect(o, 7179), true);
            // 대상의 현재 HP에서 데미지만큼 감소
            o.setNowHp(o.getNowHp() - damage);
        }
    }
}
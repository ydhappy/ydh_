package lineage.network.packet.server;

import java.util.HashMap;
import java.util.Map;

import lineage.bean.lineage.Useshop;
import lineage.database.CharacterMarbleDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.UserShopController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.yadolan.HuntingZoneTeleportationBook;

public class S_ObjectAdd extends S_Inventory {

	static public BasePacket clone(BasePacket bp, object o, object oo) {
		if (bp == null)
			bp = new S_ObjectAdd(o, oo);
		else
			((S_ObjectAdd) bp).toClone(o, oo);
		return bp;
	}

	public S_ObjectAdd(object o, object oo) {
		toClone(o, oo);
	}

	public void toClone(object o, object oo) {
	    clear();

	    String name = o.isNameHidden() ? "" : o.getName();

	    // 수배중 체크
	    if (o instanceof PcInstance && WantedController.checkWantedPc(o)) {
	        name = Lineage.wanted_name + name;
	    }

	    if (o.getOwnObjectId() > 0 && o.getOwnName() != null) {
	        boolean wanted = WantedController.checkWantedPc(o.getOwnObjectId());

	        if (wanted) {
	            if (!o.getOwnName().contains(Lineage.wanted_name)) {
	                o.setOwnName(Lineage.wanted_name + o.getOwnName());
	            }
	        } else {
	            if (o.getOwnName().contains(Lineage.wanted_name) && o.getOwnName().length() >= 5) {
	                o.setOwnName(o.getOwnName().substring(5));
	            }
	        }
	    }

	 // HP 퍼센트와 레벨 설정
	    int hp = 0xFF;
	    int lev = o instanceof PcInstance ? 0 : o.getLevel();

	    // HP 바 표시 조건
	    boolean isHpbar = 
	           (o instanceof SummonInstance && o.getOwnObjectId() == oo.getObjectId())
	        || (o.getPartyId() > 0 && o.getPartyId() == oo.getPartyId()) 
	        || (Lineage.monster_interface_hpbar && o instanceof MonsterInstance) 
	        || (Lineage.npc_interface_hpbar && o instanceof NpcInstance) 
	        || (o.isHpbar() && o.getObjectId() == oo.getObjectId())
	        ||  (Lineage.is_gm_pc_hpbar && oo.getGm() > 0 && o instanceof PcInstance && o.getObjectId() != oo.getObjectId())
	        || (Lineage.is_gm_mon_hpbar && oo.getGm() > 0 && o instanceof MonsterInstance);

	    // 조건 만족 시 HP 퍼센트 할당
	    if (isHpbar && o instanceof Character)
	    	hp = ((Character) o).getHpPercent();

	    // 메시지 (필요 시 아래에서 사용)
	    byte[] msg = null;

	    if (o instanceof ItemInstance) {
	        ItemInstance item = (ItemInstance) o;
	        StringBuilder sb = new StringBuilder();

	        // 속성 (원소) 정보 추가
	        Map<String, Integer> elementMap = new HashMap<>();
	        elementMap.put("풍령", item.getEnWind());
	        elementMap.put("지령", item.getEnEarth());
	        elementMap.put("수령", item.getEnWater());
	        elementMap.put("화령", item.getEnFire());

	        for (Map.Entry<String, Integer> entry : elementMap.entrySet()) {
	            if (entry.getValue() > 0) {
	                sb.append(entry.getKey()).append(":").append(entry.getValue()).append("단 ");
	                break;  // 첫 번째 속성만 추가
	            }
	        }

	        if (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance) {
	            sb.append(" ").append(item.getEnLevel() >= 0 ? "+" : "-").append(item.getEnLevel());
	        }

	        // 이름 표현
	        sb.append(" ").append(item.isDefinite() ? item.getItem().getName() : item.getItem().getItemId());

	        // 수량 표현
	        if (item.getCount() > 1) {
	            sb.append(" [").append(Util.changePrice(item.getCount())).append("]");
	        }

	        // 펫 목걸이
	        if (item instanceof DogCollar) {
	            DogCollar dc = (DogCollar) item;
	            sb.append(" [Lv.").append(dc.getPetLevel()).append(" ").append(dc.getPetName()).append("]");
	        }

	        // 특수 아이템 색상 적용
	        if (item.getItem().getNameIdNumber() == 5116) {
	            name = "\\f=" + sb.toString();
	        } else {
	            String itemName = CharacterMarbleDatabase.getItemName(o);
	            name = (itemName != null) ? itemName : sb.toString();
	        }
	    }

	    if (o instanceof PcInstance) {
	        Useshop us = UserShopController.find((PcInstance) o);
	        if (us != null) {
	            msg = us.getMsg();
	        }
	    }

	    // 패킷 데이터 작성
	    writeC(Opcodes.S_OPCODE_CHARPACK);
	    writeH(o.getX());
	    writeH(o.getY());
	    writeD(o.getObjectId());
	    writeH(o.getGfx());
	    writeC(o.getGfxMode());
	    writeC(o.getHeading());
	    writeC(o.getLight());
	    writeC(o.getSpeed());
	    writeD((int) o.getCount());
	    writeH(o.getLawful());
	    writeS(name);
	    writeS(o.isNameHidden() ? "" : o.getTitle());
	    writeC(o.getStatus(oo));
	    writeD(o.isNameHidden() ? 0 : o.getClanId());
	    writeS(o.isNameHidden() ? "" : o.getClanName());
	    writeS(o.isNameHidden() ? "" : o.getOwnName());
	    writeC(0);
	    writeC(hp);
	    writeC(0);
	    writeC(lev);
	    writeB(msg);
	    writeC(0xFF);
	    writeC(0xFF);
	}
	
	public static boolean isHpbar(object target, object viewer) {
	    // HP 바 표시 조건: 같은 파티원이거나, 소환수거나, GM이 볼 때 표시
	    if (target instanceof SummonInstance && target.getOwnObjectId() == viewer.getObjectId()) {
	        return true;
	    }
	    if (target.getPartyId() > 0 && target.getPartyId() == viewer.getPartyId()) {
	        return true;
	    }
	    if (viewer.getGm() > 0 && target.getObjectId() != viewer.getObjectId()) {
	        return target instanceof MonsterInstance || target instanceof NpcInstance || target instanceof PcInstance;
	    }
	    return target.isHpbar();
	}
}

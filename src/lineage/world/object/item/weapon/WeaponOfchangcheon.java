package lineage.world.object.item.weapon;

import java.sql.Connection;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class WeaponOfchangcheon extends ItemWeaponInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if(item == null)
			item = new WeaponOfchangcheon();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		super.toClick(cha, cbp);
		
		if(equipped)
			// 버프 등록
			BuffController.append(this, this);
		else
			// 버프 제거
			BuffController.remove(this, getClass());
	}

	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		super.toWorldJoin(con, pc);
		if(equipped)
			BuffController.append(this, this);
	}

	@Override
	public void toBuffStart(object o) {
	}

	@Override
	public void toBuffStop(object o) {
		equipped = false;
		BuffController.remove(cha, ShapeChange.class);
	}

	@Override
	public void toBuffEnd(object o) {
		if(cha==null || cha.isWorldDelete())
			return;
		
		// 종료 처리.
		toBuffStop(cha);
	}
	
	@Override
	public void toBuff(object o) {
		if(cha==null || cha.isWorldDelete())
		return;
		if(getTime()%1 == 0)
			cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
			if(getTime()<=1){
				if(isEquipped())
				toClick(cha, null);
				//아이템 제거멘트
				if( this instanceof WeaponOfchangcheon)
					ChattingController.toChatting(cha, "[" +getItem().getName()+ "] 시간이 모두 소진 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);	
				//인벤 제거
				cha.getInventory().count(this, 0, true);
			}
	}

	public void toWorldOut(object o) {
		if(cha==null || cha.isWorldDelete())
		return;
		toBuffEnd(o);
	}
	
}

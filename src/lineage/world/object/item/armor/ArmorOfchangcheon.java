package lineage.world.object.item.armor;

import java.sql.Connection;

import lineage.bean.database.Poly;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class ArmorOfchangcheon extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if(item == null)
			item = new ArmorOfchangcheon();
		// 시간 설정 - 깃털 갯수
		//	: 360개 6시간
		//	: 720개 18시간
		//	: 1170개 30시간
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
				if( this instanceof ArmorOfchangcheon)
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

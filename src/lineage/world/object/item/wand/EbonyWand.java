package lineage.world.object.item.wand;

import lineage.bean.database.Item;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.EnergyBolt;

public class EbonyWand extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new EbonyWand();
		return item;
	}

	@Override
	public ItemInstance clone(Item item) {

		quantity = Util.random(100, 200);

		return super.clone(item);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    // 은신 상태 체크
	    if (cha.isBuffInvisiBility() || cha.isInvis() || 
	        (cha instanceof PcInstance && ((PcInstance) cha).isFishing()) || 
	        World.isSafetyZone(cha.getX(), cha.getY(), cha.getMap())) {
	        return;
	    }

	    object o = null;
	    int obj_id = cbp.readD();
	    int x = cbp.readH();
	    int y = cbp.readH();

	    // 대상 객체 찾기.
	    if (obj_id == cha.getObjectId()) {
	        o = cha;
	    } else {
	        o = cha.findInsideList(obj_id);
	    }

	    // 안전지대 체크
	    if (o != null && World.isSafetyZone(o.getX(), o.getY(), o.getMap())) {
	        // 마을에서는 사용할 수 없다는 메시지 전송
	        ChattingController.toChatting(cha, "마을에서는 사용할수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return; // 안전지대에 있을 경우 더 이상 진행하지 않음
	    }

	    // 방향 전환.
	    cha.setHeading(Util.calcheading(cha, x, y));
	    // 수량 하향
	    setQuantity(quantity - 1);

	    cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	    
	    // 객체 찾기.
	    if (obj_id == cha.getObjectId()) {
	        o = cha;
	    } else {
	        o = cha.findInsideList(obj_id);
	    }
	    
	    // 처리.
	    if (o == null) {
	        cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha,
	                Lineage.GFX_MODE_WAND, getItem().getEffect(), x, y), cha instanceof PcInstance);
	    } else {
	        EnergyBolt.toBuffe(cha, o, null, Lineage.GFX_MODE_WAND, getItem().getEffect(), 30);
	    }
	    
	    // 수량이 0개일때 제거
	    if (getQuantity() < 1) {
	        cha.getInventory().count(this, getCount() - 1, true);
	    }
	}
}

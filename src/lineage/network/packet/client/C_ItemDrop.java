package lineage.network.packet.client;

import all_night.Lineage_Balance;
import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;

public class C_ItemDrop extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ItemDrop(data, length);
		else
			((C_ItemDrop)bp).clone(data, length);
		return bp;
	}
	
	public C_ItemDrop(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.getInventory()==null || !isRead(12) || pc.isDead() || pc.isWorldDelete())
			return this;
		
		if (!Lineage_Balance.is_drop_item) {
			ChattingController.toChatting(pc, "바닥에 아이템을 버릴 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return this;
		}
		
		if (Lineage.open_wait) {
			ChattingController.toChatting(pc, "[오픈대기] 아이템을 버릴 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return this;
		}			
		if (pc.getMap() ==800) {
			ChattingController.toChatting(pc, "이템을 버릴 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return this;
		}
		int x = readH();
		int y = readH();
		int object_id = readD();
		long count = readD();
		
//	    if (count > 100) {
//	        ChattingController.toChatting(pc, "한 번에 100개 이상의 아이템을 버릴 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
//	        return this;
//	    }
		
		Inventory inv = pc.getInventory();
		ItemInstance item = inv.value(object_id);

		// ✅ 아이템이 존재하고, 투명 상태가 아니거나 GM일 경우
		if (item != null && (pc.getGm() > 0 || !pc.isTransparent())) {

		    // ✅ 운영자(GM)는 강화 제한 무시 → 바로 드랍 가능
		    if (pc.getGm() == 0) {
		        if (item instanceof ItemWeaponInstance && item.getEnLevel() >= Lineage_Balance.is_weapon_enLevel) {
		            ChattingController.toChatting(
		                item.getCharacter(),
		                String.format("+%d %s 바닥에 버릴 수 없습니다.", item.getEnLevel(), Util.getStringWord(item.getItem().getName(), "은", "는")),
		                Lineage.CHATTING_MODE_MESSAGE
		            );
		            return this;
		        }

		        if (item instanceof ItemArmorInstance && item.getEnLevel() >= Lineage_Balance.is_armor_enLevel) {
		            ChattingController.toChatting(
		                item.getCharacter(),
		                String.format("+%d %s 바닥에 버릴 수 없습니다.", item.getEnLevel(), Util.getStringWord(item.getItem().getName(), "은", "는")),
		                Lineage.CHATTING_MODE_MESSAGE
		            );
		            return this;
		        }
		    }

		    // ✅ 드랍 좌표가 캐릭터 기준 2칸 이내일 경우만 드랍 허용
		    if (Util.isDistance(pc.getX(), pc.getY(), pc.getMap(), x, y, pc.getMap(), 2)) {
		        if (PluginController.init(C_ItemDrop.class, "init", this, pc, item) == null) {
		            inv.toDrop(item, count, x, y, true);
		        }
		    }
		}

		return this;

    }
}
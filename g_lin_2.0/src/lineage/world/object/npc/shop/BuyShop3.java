package lineage.world.object.npc.shop;

import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_SoundEffect;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class BuyShop3 extends object {

	private long lastSoundPlayTime = 0;

	public long getLastSoundPlayTime() {
		return lastSoundPlayTime;
	}

	public void setLastSoundPlayTime(long lastSoundPlayTime) {
		this.lastSoundPlayTime = lastSoundPlayTime;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		long currentTime = System.currentTimeMillis(); 
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "yadoshop2"));
		
		if (currentTime - getLastSoundPlayTime() >= 2500) {
			pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27818));
			setLastSoundPlayTime(currentTime);
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		pc.setTempShop(null);

		if (action.equalsIgnoreCase("Weapon")) {
			object shop = NpcSpawnlistDatabase.무기상점;

			if (shop != null) {
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}

		}
		if (action.equalsIgnoreCase("Armor")) {
			object shop = NpcSpawnlistDatabase.방어구상점;

			if (shop != null) {
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("Accessory")) {
			object shop = NpcSpawnlistDatabase.장신구상점;

			if (shop != null) {
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("Spellbook")) {
			object shop = NpcSpawnlistDatabase.마법서상점;

			if (shop != null) {
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("Consumable")) {
			object shop = NpcSpawnlistDatabase.잡화상점;

			if (shop != null) {
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
		if (action.equalsIgnoreCase("Scroll")) {
			object shop = NpcSpawnlistDatabase.주문서상점;

			if (shop != null) {
				shop.toTalk(pc, null);
				pc.setTempShop(shop);
			}
		}
	}
}

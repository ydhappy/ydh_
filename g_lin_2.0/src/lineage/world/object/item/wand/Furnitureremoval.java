package lineage.world.object.item.wand;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.item.etc.Furniture;

public class Furnitureremoval extends ItemInstance {

	private final List<BackgroundInstance> spawnedTiles = new ArrayList<>();

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null) {
			item = new Furnitureremoval();
		}
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		int obj_id = cbp.readD();
		int x = cbp.readH();
		int y = cbp.readH();
		int mapId = cha.getMap();

		// 로그 출력
		Furniture.removeNearbyTiles(x, y, mapId);

	}
}
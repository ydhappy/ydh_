package lineage.world.object.item.etc;

import java.util.ArrayList;
import java.util.List;

import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;

public class Furniture extends ItemInstance {

	// 각 Furniture 인스턴스가 관리하는 타일 리스트
	private static List<BackgroundInstance> managedTiles = new ArrayList<>();

	// Track the spawned tile for this Furniture item
	private BackgroundInstance spawnedTile;

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Furniture();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {

		int x = cha.getX();
		int y = cha.getY();
		int mapId = cha.getMap();
		if (World.isThroughObject(x, y, mapId, cha.getHeading())) {
			x += Util.getXY(cha.getHeading(), true);
			y += Util.getXY(cha.getHeading(), false);
		}

		// 아지트 내에서만 가구 설치 가능 여부를 확인
		if (!AgitController.isAgitLocation(cha)) {
			ChattingController.toChatting(cha, "아지트에서만 가구를 설치할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (spawnedTile != null && managedTiles.contains(spawnedTile)) {
		    if (Util.isDistance(spawnedTile, cha, 1)) {
		        removeNearbyTiles(x, y, mapId);
		    } else {
		    	ChattingController.toChatting(cha, "이미 가구가 배치되어있습니다.", Lineage.CHATTING_MODE_MESSAGE);
		    }
		    return;
		}

		synchronized (managedTiles) {
			int nameIdNumber = item.getNameIdNumber();
			int newTileGfx = getGfxForNameIdNumber(nameIdNumber);

			spawnedTile = createNewTile(x, y, mapId, newTileGfx); // Track the
																	// new tile
			managedTiles.add(spawnedTile);
		}
	}

	private int getGfxForNameIdNumber(int nameIdNumber) {
		switch (nameIdNumber) {
		case 5183:
			return 6043;
		case 5184:
			return 6045;
		case 5179:
			return 6065;
		case 5185:
			return 6056;
		case 15063:
			return 6054;
		case 5181:
			return 6074;
		case 5182:
			return 6071;
		case 5178:
			return 6062;
		case 5186:
			return 6067;
		case 15062:
			return 6068;
		case 5176:
			return 6058;
		case 5177:
			return 6060;
		default:
			return 0; // 기본값
		}
	}

	// Other methods...

	private BackgroundInstance createNewTile(int x, int y, int mapId, int gfx) {
		BackgroundInstance newTile = new BackgroundInstance();
		newTile.setGfx(gfx);
		newTile.setObjectId(ServerDatabase.nextEtcObjId());
		newTile.toTeleport(x, y, mapId, false);
		return newTile;
	}

	private static void removeTile(BackgroundInstance tile) {
		synchronized (managedTiles) {
			tile.clearList(true);
			World.remove(tile);
			managedTiles.remove(tile);
		}
	}

	public static void removeNearbyTiles(int x, int y, int mapId) {
		synchronized (managedTiles) {
			List<BackgroundInstance> tilesToRemove = new ArrayList<>();
			for (BackgroundInstance tile : managedTiles) {
				int tx = tile.getX();
				int ty = tile.getY();
				int tmapId = tile.getMap();

				if (tmapId == mapId && Math.abs(tx - x) <= 1 && Math.abs(ty - y) <= 1) {
					tilesToRemove.add(tile);
				}
			}
			// 주변 타일 제거
			for (BackgroundInstance tile : tilesToRemove) {
				removeTile(tile);
			}
		}
	}

	public void removeTile(int x, int y, int mapId) {
		synchronized (this) {
			List<BackgroundInstance> tilesToRemove = new ArrayList<>();
			for (BackgroundInstance tile : managedTiles) {
				int tx = tile.getX();
				int ty = tile.getY();
				int tmapId = tile.getMap();

				if (tmapId == mapId && Math.abs(tx - x) <= 1 && Math.abs(ty - y) <= 1) {
					tilesToRemove.add(tile);
				}
			}
			// 주변 타일 제거
			for (BackgroundInstance tile : tilesToRemove) {
				removeTile(tile);
			}
		}
	}
}
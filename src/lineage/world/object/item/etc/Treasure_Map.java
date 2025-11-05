package lineage.world.object.item.etc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Treasure_Map extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Treasure_Map();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		switch (getItem().getNameIdNumber()) {
		case 19580: // 등대
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "firsttmap"));
			break;
			//2단계
		case 19581: // 버려진 선착장 모양의 그림
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "secondtmapb"));
			break;
		case 19582: // 불에 타버린 집 모양의 그림
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "secondtmapa"));
			break;
		case 19583: // 두 마리의 참치가 그려져 
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "secondtmapc"));
			break;
			//3단계
		case 19584: // 다리가 두 개인 섬의 한 가운데.
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "thirdtmapd"));
			break;
		case 19585: // 진흙이 가득 찬 웅덩이
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "thirdtmape"));
			break;
		case 19586: // 엄지와 검지가 빠진 거인의 손가락
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "thirdtmapf"));
			break;
		case 19587: // 화단 안의 이상한 기둥 뒤
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "thirdtmapg"));
			break;
		case 19588: // 다리가 한 개인 섬의 구석
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "thirdtmaph"));
			break;
		case 19589: // 물레방아 도는데…
			cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "thirdtmapi"));
			break;
		}
	}
}

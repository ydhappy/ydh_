package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterKarma;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class UPManager extends CraftInstance {

	public UPManager(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		// 발록이 영혼석 파편 infamous01 맵번호(601)
		// 야히가 혈석 파편 infamous11 맵번호(608)
		int type = getMap() == 602 ? 0 : 1;
		// 레벨이 70보다 낮거나 우호도가 8미만이면 제작을 안함.
		if (pc.getLevel() < 45 || pc.getKarmaLevel() < 8)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "infamous" + type + "2"));
		// 레벨이70이상이면서 우호도가 8이면 제작창 띠움
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "infamous" + type + "1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getLevel() < 45)
			return;
		if (pc.getKarmaLevel() < 8)
			return;
		//
		int count = 0;
		if (action.equalsIgnoreCase("1")) {
			count = 1;
		} else if (action.equalsIgnoreCase("2")) {
			count = 10;
		} else if (action.equalsIgnoreCase("3")) {
			count = 100;
		}
		//
		if (count == 0)
			return;
		//
		if (pc.getMap() == 602) {
			pc.setKarma(pc.getKarma() - count * 400);
		} else 
			pc.setKarma(pc.getKarma() + count * 400);

		pc.toSender(new S_CharacterKarma((int) pc.getKarma()));

		String item_name = getMap() == 602 ? "영혼석 파편" : "혈석 파편";
		CraftController.toCraft(this, pc, ItemDatabase.find(item_name), count, true);
	}
}

package lineage.world.object.item.yadolan;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.DungeonBook;
import lineage.bean.database.FirstSpawn;
import lineage.database.DungeontellbookDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.WantedController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class HuntingZoneTeleportationBook extends ItemInstance {

    public static synchronized ItemInstance clone(ItemInstance item) {
        if (item == null) {
            item = new HuntingZoneTeleportationBook();
        }
        return item;
    }

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		List<String> msg = new ArrayList<String>();

		for (DungeonBook db : DungeontellbookDatabase.getList()) {
			if (db.getAden() != null && ItemDatabase.find(db.getAden()) != null && db.getCount() > 0) {
				msg.add(String.format("%s", db.getName()));
			} else {
				msg.add(String.format("%s", db.getName()));
			}
		}

		for (int i = 0; i < 100; i++) {
			msg.add(" ");
		}
		cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dunbook", null, msg));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc == null || pc.isWorldDelete() || pc.isDead() || pc.isLock() || pc.getInventory() == null) {
			return;
		}	
		
		if (!checkMap(pc)) {
			ChattingController.toChatting(pc, "해당 맵에서 이용 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		try {
			DungeonBook db = DungeontellbookDatabase.find(Integer.valueOf(action));
			
			if (db != null) {
				if (pc.getLevel() < db.getLevel()) {
					ChattingController.toChatting(pc, String.format("%d레벨 이상 입장하실 수 있습니다.", db.getLevel()), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (db.isClan()) {
					if (pc.getClanId() < 1) {
						ChattingController.toChatting(pc, "혈맹 가입중에만 입장하실 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
				}
				
				if (db.isWanted()) {
					if (!WantedController.checkWantedPc(pc)) {
						ChattingController.toChatting(pc, "수배자만 입장하실 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
				}
			
				if (db.getLoc_list() != null && db.getLoc_list().size() > 0) {
					FirstSpawn fs = db.getLoc_list().get(Util.random(0, db.getLoc_list().size() - 1));
					
					if (db.getAden() != null && db.getCount() > 0) {
						if (ItemDatabase.find(db.getAden()) != null) {
							if (pc.getInventory().isAden(db.getAden(), db.getCount(), true)) {
								pc.toPotal(fs.getX(), fs.getY(), fs.getMap());
								ChattingController.toChatting(pc, String.format("%S으로 이동 하였습니다.", db.getName()), Lineage.CHATTING_MODE_MESSAGE);
								//창닫기
								pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
							} else {
								ChattingController.toChatting(pc, String.format("%s(%,d)원이 부족합니다.", db.getAden(), db.getCount()), Lineage.CHATTING_MODE_MESSAGE);
							}
						}
					} else {		
						pc.toPotal(fs.getX(), fs.getY(), fs.getMap());
					}	
				}		
			}
		} catch (Exception e) {
		}
	}
	
	private boolean checkMap(PcInstance pc) {
		switch (pc.getMap()) {
		case 70: //잊혀진섬
		case 621: //수상한 마을
		case 5124: //낚시터
		case 101: //오만의탑 1
		case 102: //오만의탑 2
		case 103: //오만의탑 3
		case 104: //오만의탑 4
		case 105: //오만의탑 5
			return false;
		}
		
		return true;
	}
}
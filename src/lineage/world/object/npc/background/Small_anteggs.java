package lineage.world.object.npc.background;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Small_anteggs extends BackgroundInstance {

	private int SLEEP_TIME = 60 * 10; // 10분
	private int current_time = 0;

	public Small_anteggs() {
		CharacterController.toWorldJoin(this);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		// 닫혀있을때만 처리.
		if (gfxMode == 29) {
			ItemInstance itemegg = cha.getInventory().find(ItemDatabase.find(5554));
			if (itemegg == null) {
				ChattingController.toChatting(cha, "개미 부화 촉매제가 필요합니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			cha.getInventory().count(itemegg, itemegg.getCount() - 1, true);
			// 상자 열기.
			toOn();
			toSend();
			
			/**
			*- 무기 : 양손검 3, 대검 1048, 대형 도끼 412
			*- 방어구 : 청동 판금 갑옷 154, 미늘 갑옷155, 사각 방패429
			*- 주문서 : 축복받은 무기 마법 주문서244 0, 무기 마법 주문서244 1, 축복받은 갑옷 주문서249 0, 갑옷 주문서 429 1
			*- 기타 : 최고급 루비
			*/
			
			// 아이템 지급
			int count = 0;
			int bless = 0;
			Item item = null;
			int rand = Util.random(0, 1000);
			if (rand > 950) {
				// 대검	
				count = 1;
				bless = 1;
				item = ItemDatabase.find(1048);
			} else if (rand > 900) {
				// 양손검
				count = 1;
				bless = 1;
				item = ItemDatabase.find(3);
			} else if (rand > 850) {
				// 대형 도끼
				count = 1;
				bless = 1;
				item = ItemDatabase.find(412);
			} else if (rand > 800) {
				// 미늘 갑옷
				count = 1;
				bless = 1;
				item = ItemDatabase.find(155);
			} else if (rand > 750) {
				// 청동 판금 갑옷
				count = 1;
				bless = 1;
				item = ItemDatabase.find(154);
			} else if (rand > 650) {
				// 사각 방패
				count = 1;
				bless = 1;
				item = ItemDatabase.find(429);
			} else if (rand > 600) {
				// 무기 마법 주문서
				count = 1;
				bless = 0;
				item = ItemDatabase.find(244);
			} else if (rand > 300) {
				// 갑옷 마법 주문서
				count = 1;
				bless = 0;
				item = ItemDatabase.find(249);
			} else {
				// 그외에는 몬스터 스폰.
				int rnd = Util.random(0, 100);
				MonsterInstance mon = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(rnd < 50 ? "[개미굴] 거대 돌격 개미" : "[개미굴] 거대 산성 개미"));
				if (mon != null) {
					mon.setHomeX(x);
					mon.setHomeY(y);
					mon.setHomeMap(cha.getMap());
					mon.setHeading(cha.getHeading());
					mon.toTeleport(x, y, cha.getMap(), false);
					AiThread.append(mon);
				}
			}
			
			CraftController.toCraft(this, cha, item, count, true, 0, 0, 0, bless, 0, false);
		}
	}

	@Override
	public void toTimer(long time) {
		if (gfxMode == 29) {
			return;
		}
		if (current_time++ >= SLEEP_TIME) {
			current_time = 0;
			// 상자 닫기.
			toOff();
			toSend();
		}
	}

	public void toOn() {
		setGfxMode(28);
	}

	public void toOff() {
		setGfxMode(29);
	}

	public void toSend() {
		toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
	}
}


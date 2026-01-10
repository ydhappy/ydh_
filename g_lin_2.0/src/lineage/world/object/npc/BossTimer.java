package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lineage.bean.database.BossSpawn;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BossController;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class BossTimer extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		showHtml(pc);
	}

	public void showHtml(PcInstance pc) {
		List<String> bossList = new ArrayList<String>();
		bossList.clear();

		for (BossSpawn bossSpawn : MonsterBossSpawnlistDatabase.getSpawnList()) {
			bossList.add(String.format("[%s]", bossSpawn.getMonster()));
			bossList.add(String.format("%s", bossSpawn.getSpawnTime()));
			bossList.add(String.format("%s", bossSpawn.getSpawnDay().trim()));
			bossList.add(" ");
		}

		for (int i = 0; i < 150; i++)
			bossList.add(" ");

		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), pc, "bossList", null, bossList));
		ChattingController.toChatting(pc, "보스 스폰 시간표가 출력되었습니다.", Lineage.CHATTING_MODE_MESSAGE);

		if (BossController.getBossList().size() < 1) {
			ChattingController.toChatting(pc, "\\fR생존한 보스가 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		ChattingController.toChatting(pc, "\\fRㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);

		for (MonsterInstance boss : BossController.getBossList())
			ChattingController.toChatting(pc, String.format("\\fY%s %s", Util.getMapName(boss), boss.getMonster().getName()), Lineage.CHATTING_MODE_MESSAGE);

		ChattingController.toChatting(pc, "\\fRㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", Lineage.CHATTING_MODE_MESSAGE);
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		try {
			// ▶ GM이 아닐 경우 실행 금지
			if (pc.getGm() <= 0) {
				ChattingController.toChatting(pc, "\\fR해당 기능은 관리자(GM)만 사용할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			// "b00", "b01" 형식의 action인지 검증
			if (action != null && action.matches("^b\\d{2}$")) {
				int index = Integer.parseInt(action.substring(1));  // "b00" → 0

				List<BossSpawn> list = MonsterBossSpawnlistDatabase.getSpawnList();
				if (index >= 0 && index < list.size()) {
					BossSpawn bossSpawn = list.get(index);
					String monsterName = bossSpawn.getMonster();

					// 보스 정보 출력
//					ChattingController.toChatting(pc, String.format("▶ 선택된 보스: %s", monsterName), Lineage.CHATTING_MODE_MESSAGE);
//					ChattingController.toChatting(pc, String.format("스폰 시간: %s", bossSpawn.getSpawnTime()), Lineage.CHATTING_MODE_MESSAGE);
//					ChattingController.toChatting(pc, String.format("스폰 요일: %s", bossSpawn.getSpawnDay().trim()), Lineage.CHATTING_MODE_MESSAGE);

					// ✅ 현재 스폰된 보스 중 해당 보스가 있는지 확인
					for (MonsterInstance mi : lineage.world.controller.BossController.getBossList()) {
						if (mi.getMonster().getName().equalsIgnoreCase(monsterName)) {
							int baseX = mi.getX();
							int baseY = mi.getY();
							int mapId = mi.getMap();

							boolean teleported = false;
							for (int dx = -2; dx <= 2 && !teleported; dx++) {
								for (int dy = -2; dy <= 2 && !teleported; dy++) {
									int tx = baseX + dx;
									int ty = baseY + dy;

									// ⬇️ 객체 존재 여부는 getMapdynamic()으로 체크
									if (lineage.world.World.isThroughObject(tx, ty, mapId, 0) &&
										lineage.world.World.getMapdynamic(tx, ty, mapId) == 0) {

										pc.toTeleport(tx, ty, mapId, true);
										ChattingController.toChatting(pc,
											String.format("\\fG현재 스폰된 %s 위치로 이동했습니다.", monsterName),
											Lineage.CHATTING_MODE_MESSAGE);
										teleported = true;
									}
								}
							}

							if (!teleported) {
								ChattingController.toChatting(pc,
									String.format("\\fR현재 스폰된 %s 위치 주변에 이동 가능한 좌표가 없습니다.", monsterName),
									Lineage.CHATTING_MODE_MESSAGE);
							}
							return;
						}
					}

					// ❌ 현재 스폰된 보스가 없으면 저장된 좌표 중 랜덤 좌표로 이동
					List<String> coords = bossSpawn.getSpawnCoords();
					if (coords != null && !coords.isEmpty()) {
						Random random = new Random();
						String selected = coords.get(random.nextInt(coords.size()));  // 랜덤 좌표 선택
						String[] parts = selected.split(",");

						if (parts.length == 3) {
							try {
								int x = Integer.parseInt(parts[0].trim());
								int y = Integer.parseInt(parts[1].trim());
								int mapId = Integer.parseInt(parts[2].trim());

								pc.toTeleport(x, y, mapId, true);
								ChattingController.toChatting(pc,
									String.format("\\fG%s 위치로 이동했습니다.", monsterName),
									Lineage.CHATTING_MODE_MESSAGE);
							} catch (NumberFormatException e) {
								ChattingController.toChatting(pc, "\\fR좌표 숫자 형식 오류가 발생했습니다.", Lineage.CHATTING_MODE_MESSAGE);
								e.printStackTrace();
							}
						} else {
							ChattingController.toChatting(pc, "\\fR좌표 데이터 형식이 잘못되었습니다. (x,y,mapId)", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(pc, "\\fR보스의 스폰 좌표가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
					return;
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(pc, "\\fR보스 처리 중 알 수 없는 오류가 발생했습니다.", Lineage.CHATTING_MODE_MESSAGE);
			e.printStackTrace();
		}

		// 실패 처리
		ChattingController.toChatting(pc, "해당 보스를 찾을 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
	}
}

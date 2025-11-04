package lineage.network.packet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class C_SkillBuyOk_elf extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new C_SkillBuyOk_elf(data, length);
		else
			((C_SkillBuyOk_elf) bp).clone(data, length);
		return bp;
	}

	public C_SkillBuyOk_elf(byte[] data, int length) {
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc) {
		// 버그 방지: 플레이어 객체가 없거나 삭제된 경우 처리하지 않음
		if (pc == null || pc.isWorldDelete())
			return this;

		int count = readH();

		if (count > 0 && count <= 24) {
			final List<Skill> skillList = new ArrayList<>();
			int totalPrice = 0;

			for (int i = 0; i < count; i++) {
				Skill skill = SkillDatabase.find(readD() + 1);
				if (skill != null) {
					skillList.add(skill);
					totalPrice += skill.getPrice();
				}
			}
			if (!skillList.isEmpty()) {
				if (pc.getMap() == 16) {
					processSkillsInMap16(pc, skillList);
				} else {
					processSkillsInOtherMaps(pc, skillList, totalPrice);
				}
			}
		}

		return this;
	}

	private void processSkillsInMap16(PcInstance pc, List<Skill> skillList) {
	    List<Skill> pcSkills = SkillController.find(pc);

	    // 스킬에 맞는 (x/y) 값을 저장하는 맵
	    Map<String, String> skillCounts = new HashMap<>();
	    //1
	    skillCounts.put("힐", "4/0");
	    skillCounts.put("라이트", "4/0");
	    skillCounts.put("실드", "3/5");
	    skillCounts.put("에너지볼트", "3/0");
	    skillCounts.put("텔레포트", "5/0");
	    skillCounts.put("아이스 대거", "3/0");
	    skillCounts.put("윈드 커터", "3/0");
	    skillCounts.put("홀리 웨폰", "10/0");
	    //2
	    skillCounts.put("큐어 포이즌", "8/0");
	    skillCounts.put("칠 터치", "9/1");
	    skillCounts.put("커스: 포이즌", "10/10/1");
	    skillCounts.put("인챈트 웨폰", "15/0");
	    skillCounts.put("디텍션", "8/0");
	    skillCounts.put("디크리즈 웨이트", "20/5");
	    skillCounts.put("파이어 애로우", "3/0");
	    //3
	    skillCounts.put("라이트닝", "15/8");
	    skillCounts.put("턴 언데드", "15/0");
	    skillCounts.put("익스트라 힐", "13/0");
	    skillCounts.put("커스: 블라인드", "17/5");
	    skillCounts.put("블레스드 아머", "20/0");
	    skillCounts.put("프로즌 클라우드", "17/6");

	    for (Skill skill : skillList) {
	        String[] firstTierSkills = { "힐", "라이트", "실드", "에너지볼트", "텔레포트", "아이스 대거", "윈드 커터", "홀리 웨폰" };
	        String[] secondTierSkills = { "큐어 포이즌", "칠 터치", "커스: 포이즌", "인챈트 웨폰", "디텍션", "디크리즈 웨이트", "파이어 애로우" };
	        String[] thirdTierSkills = { "라이트닝", "턴 언데드", "익스트라 힐", "커스: 블라인드", "블레스드 아머", "프로즌 클라우드" };

	        // 해당 스킬에 맞는 (x/y) 값을 가져옵니다. 없으면 기본값 "0/0"을 사용.
	        String skillCount = skillCounts.getOrDefault(skill.getName(), "0/0");

	        if (processSkill(pc, pcSkills, skill, firstTierSkills, "미스릴", 50, "판의 갈기털", 10, "페어리 더스트", 100, "아라크네의 거미줄", 10, skillCount))
	            continue;
	        if (processSkill(pc, pcSkills, skill, secondTierSkills, "미스릴 실", 10, "버섯포자의 즙", 8, "판의 뿔", 1, "엔트의 껍질", 3, skillCount))
	            continue;
	        processSkill(pc, pcSkills, skill, thirdTierSkills, "오리하루콘", 45, "아라크네의 허물", 3, "판의 뿔", 3, "페어리의 날개", 3, skillCount);
	    }

	    SkillController.sendList(pc);
	}

	private boolean processSkill(PcInstance pc, List<Skill> pcSkills, Skill skill, String[] skillNames, String itemName1, int quantity1, String itemName2, int quantity2, String itemName3, int quantity3, String itemName4, int quantity4, String skillCount) {

	    for (String skillName : skillNames) {
	        if (skill.getName().startsWith(skillName)) {
	            // 스킬 이름 뒤에 해당 (x/y) 값을 추가
	            String skillWithCount = skill.getName() + " (" + skillCount + ")";

	            if (removeItemsAndAddSkill(pc, pcSkills, skill, itemName1, quantity1) || 
	                removeItemsAndAddSkill(pc, pcSkills, skill, itemName2, quantity2) || 
	                removeItemsAndAddSkill(pc, pcSkills, skill, itemName3, quantity3) || 
	                removeItemsAndAddSkill(pc, pcSkills, skill, itemName4, quantity4)) {
	                return true;
	            } else {
	                ChattingController.toChatting(pc, String.format("마법 %s 배우기 위해 기불할 재료가 부족합니다.", Util.getStringWord(skillWithCount, "을", "를")), Lineage.CHATTING_MODE_MESSAGE);
	                return false;
	            }
	        }
	    }
	    return false;
	}

	private boolean removeItemsAndAddSkill(PcInstance pc, List<Skill> pcSkills, Skill skill, String itemName, int quantity) {
		ItemInstance item = pc.getInventory().find3(itemName, quantity);

		if (item != null) {
			pc.getInventory().remove(item, quantity, true);
			pcSkills.add(skill);
			return true;
		}

		return false;
	}

	private void processSkillsInOtherMaps(PcInstance pc, List<Skill> skillList, int totalPrice) {
		if (pc.getInventory().isAden(totalPrice, true)) {
			List<Skill> pcSkills = SkillController.find(pc);
			pcSkills.addAll(skillList);
			SkillController.sendList(pc);
		} else {
			// \f1아데나가 충분치 않습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
		}
	}
}
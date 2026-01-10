package lineage.world.object.item.all_night;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Exp_Potion;

public class Exp_potion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Exp_potion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			Skill s = null;

			s = SkillDatabase.find(211);

			if (s != null) {
				if (checkBuff(cha, 211))
					Exp_Potion.onBuff(cha, s, s.getBuffDuration(), false);
				// 아이템 수량 갱신
				cha.getInventory().count(this, getCount() - 1, true);
			}
		}
	}

	public boolean checkBuff(Character cha, int uid) {
		BuffInterface b = BuffController.find(cha, SkillDatabase.find(uid));
		if (b != null && b.getTime() > 0) {
			if (b.getTime() / 3600 > 0) {
				ChattingController.toChatting(cha, String.format("%s: %d시간 %d분 %d초 후 사용 가능합니다.", b.getSkill().getName(), b.getTime() / 3600, b.getTime() % 3600 / 60, b.getTime() % 3600 % 60),
						Lineage.CHATTING_MODE_MESSAGE);
			} else if (b.getTime() % 3600 / 60 > 0) {
				ChattingController.toChatting(cha, String.format("%s: %d분 %d초 후 사용 가능합니다.", b.getSkill().getName(), b.getTime() % 3600 / 60, b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(cha, String.format("%s: %d초 후 사용 가능합니다.", b.getSkill().getName(), b.getTime() % 3600 % 60), Lineage.CHATTING_MODE_MESSAGE);
			}
			return false;
		}
		return true;
	}
}

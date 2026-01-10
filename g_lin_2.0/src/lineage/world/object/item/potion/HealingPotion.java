package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.AbsoluteBarrier;

public class HealingPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new HealingPotion();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isLvCheck(cha)) {
			if (!isClick(cha))
				return;

			if (cha.isBuffDecayPotion()) {
				ChattingController.toChatting(cha, "디케이 포션: 물약 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			// 앱솔상태 해제.
			if (cha.isBuffAbsoluteBarrier())
				BuffController.remove(cha, AbsoluteBarrier.class);

			// 패킷처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);

			// 물약 회복량
			int hp = Util.random(getItem().getSmallDmg(), getItem().getBigDmg());

			// 스탯에 따른 hp회복량 증가
			hp += (int) Math.round(hp * (cha.getDynamicHpPotion() * 0.01));

			// 체력 상승
			cha.setNowHp((int) (cha.getNowHp() + (cha.isBuffPolluteWater() ? (Math.round(hp * 0.5)) : hp)));
//			if(cha.isAutoPotionMent() && Lineage.healingpotion_message) //외부화가 전혀안대어있음
//			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 77)); // 물약 멘트 기분이 좋아졌습니다.
		
		if (getItem() != null && !getItem().getName().equalsIgnoreCase("무한 체력 회복 룬") && !getItem().getName().equalsIgnoreCase("무한 체력 회복 룬(3일)"))
			// 아이템 수량 갱신
			cha.getInventory().count(this, getCount() - 1, true);
		}
	}
}

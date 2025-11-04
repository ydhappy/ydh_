package lineage.network.packet.server;

import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.PcTradeShop;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ExchangeController;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.PcInstance;

public class S_PcTradeShopBuy extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, PcInstance pc, List<PcTradeShop> list) {
		if (bp == null)
			bp = new S_PcTradeShopBuy(pc, list);
		else
			((S_PcTradeShopBuy) bp).toClone(pc, list);
		return bp;
	}

	public S_PcTradeShopBuy(PcInstance pc, List<PcTradeShop> list) {
		toClone(pc, list);
	}

	public void toClone(PcInstance pc, List<PcTradeShop> list) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(ExchangeController.ExchangeNpc.getObjectId());

		// 일반상점 구성구간.
		writeH(list.size());

		for (PcTradeShop pts : list) {
			if (pts != null) {
				Item i = ItemDatabase.find(pts.getItemName());
				
				if (i != null) {
					writeD(pts.getItemObjectId());
					writeH(i.getInvGfx());
					
					if (Lineage.pc_trade_shop_buy_tax > 0) {
						long price = (long) (pts.getPrice() + (pts.getPrice() * Lineage.pc_trade_shop_buy_tax));
						
						writeD(price);
					} else {
						writeD(pts.getPrice());
					}

					StringBuffer sb = new StringBuffer();
					
					if (pts.getPcObjectId() == pc.getObjectId()) {
						sb.append("* ");
					}
					
					// 무기 속성 주문서
					if (pts.get무기속성() > 0) {
						sb.append(String.format("[%d단] ", pts.get무기속성()));
					}
					
					// 축저주 구분
					sb.append(pts.getBless() == 0 ? "(축) " : (pts.getBless() == 1 ? "" : "(저주) "));
					// 인첸트 레벨 표현
					if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor"))) {
						sb.append(pts.getEnLevel() >= 0 ? "+" : "-").append(pts.getEnLevel()).append(" ");
					}
					
					if (pts.getItemName() != null && pts.getItemName().equalsIgnoreCase("펜던트")) {
						int a = pts.getEnLevel();

						if (a >= 20 && a <= 29)
							sb.append("다미는 ");
						else if (a >= 30 && a <= 39)
							sb.append("영롱한 ");
						else if (a >= 40)
							sb.append("찬란한 ");
					}

					// 이름 표현
					String itemName = CharacterMarbleDatabase.getItemName(pts.getItemObjectId());
					if (itemName != null) {
						sb.append(itemName);
					} else {
						sb.append(i.getName());
					}

					// 수량 표현
					if (pts.getCount() > 1) {
						sb.append(" (").append(Util.changePrice(pts.getCount())).append(")");
					}
					
					if (i.getNameIdNumber() == 1173) {
						sb.delete(0, sb.length());
						
						sb.append(" [Lv.");
						sb.append(pts.getPetLevel());
						sb.append(" ");
						sb.append(pts.getPetName());
						sb.append("]");
					}
					
					writeS(sb.toString());

					if (Lineage.server_version > 144) {

						if (i.getType1().equalsIgnoreCase("armor")) {
							if (i.getName().equalsIgnoreCase("신성한 엘름의 축복"))
								toArmor(i, null, 0, pts.getEnLevel(), (int) i.getWeight(),
										pts.getEnLevel() > 4 ? (pts.getEnLevel() - 4) * i.getEnchantMr() : 0,
												pts.getBless(), pts.getEnLevel() * i.getEnchantStunDefense());
							else
								toArmor(i, null, 0, pts.getEnLevel(), (int) i.getWeight(),
										pts.getEnLevel() * i.getEnchantMr(), pts.getBless(),
										pts.getEnLevel() * i.getEnchantStunDefense());
						} else if (i.getType1().equalsIgnoreCase("weapon")) {
							toWeapon(i, null, 0, pts.getEnLevel(), (int) i.getWeight(), pts.getBless());
						} else {
							toEtc(i, (int) i.getWeight());
						}
					}
				}
			}
		}
	}
}

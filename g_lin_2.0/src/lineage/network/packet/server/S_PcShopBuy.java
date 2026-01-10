package lineage.network.packet.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lineage.bean.database.Item;
import lineage.bean.database.PcShop;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.instance.PcShopInstance;

public class S_PcShopBuy extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, PcShopInstance psi) {
		if (bp == null)
			bp = new S_PcShopBuy(psi);
		else
			((S_PcShopBuy) bp).toClone(psi);
		return bp;
	}

	public S_PcShopBuy(PcShopInstance psi) {
		toClone(psi);
	}

	public void toClone(PcShopInstance psi) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(psi.getObjectId());

		// 일반상점 구성구간.
	    writeH(psi.getListSize());
	    
	    Map<Integer, Integer> itemIdToGfx = new LinkedHashMap<>();
	    itemIdToGfx.put(244, 9994); // 무기 마법 주문서
	    itemIdToGfx.put(249, 9995); // 갑옷 마법 주문서
	    itemIdToGfx.put(230, 9998); // 순간이동 주문서
	    itemIdToGfx.put(230, 9998); // 순간이동 주문서
	    itemIdToGfx.put(261, 9952); // 변신 주문서
	    itemIdToGfx.put(257, 9951); // 부활 주문서
	    
	    Set<String> specialItems = new HashSet<>(Arrays.asList(
	        "무기 마법 주문서", 
	        "갑옷 마법 주문서", 
	        "순간이동 주문서",
	        "변신 주문서", 
	        "부활 주문서"
	    ));

	    for (PcShop s : psi.getShopList().values()) {
	        writeD(s.getInvItemObjectId());

	        int invGfx = s.getItem().getInvGfx();
	        int nameIdNumber = s.getItem().getNameIdNumber();

	        // 축복과 저주 아이템에 따른 그래픽스 ID 설정
	        if (s.getInvItemBress() == 0) {
	            invGfx = itemIdToGfx.getOrDefault(nameIdNumber, invGfx);
	        } else if (s.getInvItemBress() == 2) {
	            invGfx = itemIdToGfx.getOrDefault(nameIdNumber, invGfx);
	            // cursedItemMapping과 같은 추가 매핑을 사용하여 처리할 수 있습니다
	        }
	        
	        writeH(invGfx);
	        writeD(s.getPrice());

			StringBuffer sb = new StringBuffer();
			// 화폐타입
			if (!Lineage.is_market_only_aden) {
				sb.append("[").append(s.getAdenType().equalsIgnoreCase("아데나") ? "아덴" : "베릴").append("]");
		        // 축저주 구분
		        String itemName = s.getItem().getName();
		        if (!specialItems.contains(itemName)) {
		            String status = s.getInvItemBress() == 0 ? " (축)" : (s.getInvItemBress() == 2 ? " (저주)" : "");
		            sb.append(status);
		        }

				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnFire() > 0) {
					sb.append("화령 ");
					sb.append(s.getInvItemEnFire()).append("단계");
					sb.append(" ");
				}
				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWater() > 0) {
					sb.append("수령 ");
					sb.append(s.getInvItemEnWater()).append("단계");
					sb.append(" ");
				}
				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWind() > 0) {
					sb.append("풍령 ");
					sb.append(s.getInvItemEnWind()).append("단계");
					sb.append(" ");
				}
				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnEarth() > 0) {
					sb.append("지령 ");
					sb.append(s.getInvItemEnEarth()).append("단계");
					sb.append(" ");
				}
				// 인첸트 레벨 표현
				if ((s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor")))
					sb.append(" ").append(s.getInvItemEn() >= 0 ? "+" : "-").append(s.getInvItemEn()).append(" ");
			} else {
				// 축저주 구분
				String itemName = s.getItem().getName();
				if (!itemName.equalsIgnoreCase("무기 마법 주문서") && !itemName.equalsIgnoreCase("갑옷 마법 주문서") && !itemName.equalsIgnoreCase("순간이동 주문서")) {
					sb.append(s.getInvItemBress() == 0 ? "(축) " : (s.getInvItemBress() == 1 ? "" : "(저주) "));
				}

				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnFire() > 0) {
					sb.append("화령 ");
					sb.append(s.getInvItemEnFire()).append("단계");
					sb.append(" ");
				}
				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWater() > 0) {
					sb.append("수령 ");
					sb.append(s.getInvItemEnWater()).append("단계");
					sb.append(" ");
				}
				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnWind() > 0) {
					sb.append("풍령 ");
					sb.append(s.getInvItemEnWind()).append("단계");
					sb.append(" ");
				}
				if (s.getItem().getType1().equalsIgnoreCase("weapon") && s.getInvItemEnEarth() > 0) {
					sb.append("지령 ");
					sb.append(s.getInvItemEnEarth()).append("단계");
					sb.append(" ");
				}

				// 인첸트 레벨 표현
				if ((s.getItem().getType1().equalsIgnoreCase("weapon") || s.getItem().getType1().equalsIgnoreCase("armor")))
					sb.append(s.getInvItemEn() >= 0 ? "+" : "-").append(s.getInvItemEn()).append(" ");
			}

			// 이름 표현
			String itemName = CharacterMarbleDatabase.getItemName(s.getInvItemObjectId());
			if (itemName != null) {
				sb.append(itemName);
			} else {
				sb.append(" ").append(s.getItem().getName());
			}

			// 수량 표현
			if (s.getInvItemCount() > 1)
				sb.append(" (").append(Util.changePrice(s.getInvItemCount())).append(")");
			if (s.getItem().getNameIdNumber() == 1173) {
				sb.delete(0, sb.length());

				sb.append(" [Lv.");
				sb.append(s.getPetLevel());
				sb.append(" ");
				sb.append(s.getPetName());
				sb.append("]");
			}

			writeS(sb.toString());

			if (Lineage.server_version > 144) {
				if (s.getItem().getType1().equalsIgnoreCase("armor")) {
					if (s.getItem().getName().equalsIgnoreCase("신성한 엘름의 축복"))
						toArmor(s.getItem(), null, 0, s.getInvItemEn(), (int) s.getItem().getWeight(), s.getInvItemEn() > 4 ? (s.getInvItemEn() - 4) * s.getItem().getEnchantMr() : 0, s.getInvItemBress(),
								s.getInvItemEn() * s.getItem().getEnchantStunDefense());
					else
						toArmor(s.getItem(), null, 0, s.getInvItemEn(), (int) s.getItem().getWeight(), s.getInvItemEn() * s.getItem().getEnchantMr(), s.getInvItemBress(),
								s.getInvItemEn() * s.getItem().getEnchantStunDefense());
				} else if (s.getItem().getType1().equalsIgnoreCase("weapon")) {
					toWeapon(s.getItem(), null, 0, s.getInvItemEn(), (int) s.getItem().getWeight(), s.getInvItemBress());
				} else {
					toEtc(s.getItem(), (int) s.getItem().getWeight());
				}
			}
		}
	}

}

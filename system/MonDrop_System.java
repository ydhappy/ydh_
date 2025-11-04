package system;

import java.util.HashMap;
import java.util.Map;

import lineage.bean.database.Drop;
import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.server.S_Inventory;
import lineage.share.Lineage;

public class MonDrop_System extends S_Inventory {

	
	static synchronized public BasePacket clone(BasePacket bp, Monster mon) {
		if (bp == null)
			bp = new MonDrop_System(mon);
		else
			((MonDrop_System) bp).toClone(mon);
		return bp;
	}

	
	public MonDrop_System(Monster mon) {
		toClone(mon);
	}

	public void toClone(Monster m) {

		clear();
		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(0);

		// 일반상점 구성구간.
		writeH(m.getDropList().size());

		Map<String, Integer> itemMapping = new HashMap<>();
		itemMapping.put("무기 마법 주문서", 9994);
		itemMapping.put("갑옷 마법 주문서", 9995);
		itemMapping.put("순간이동 주문서", 9998);
		itemMapping.put("변신 주문서", 9952);
		itemMapping.put("부활 주문서", 9951);
		itemMapping.put("저주 풀기 주문서", 9950);
		itemMapping.put("투명 망토", 9993);
		itemMapping.put("판금 갑옷", 9992);
		itemMapping.put("일본도", 9991);
		itemMapping.put("양손검", 9990);
		itemMapping.put("마법 망토", 9987);
		itemMapping.put("투구", 9985);
		itemMapping.put("마법 방어 투구", 9985);
		itemMapping.put("큰 방패", 9983);
		itemMapping.put("활", 9980);
		itemMapping.put("요정족 방패", 9979);
		itemMapping.put("요정족 망토", 9978);
		itemMapping.put("난쟁이족 철 투구", 9977);
		itemMapping.put("난쟁이족 망토", 9976);
		itemMapping.put("난쟁이족 둥근 방패", 9975);
		itemMapping.put("난쟁이족 검", 9974);
		itemMapping.put("장갑", 9973);
		itemMapping.put("청동 판금 갑옷", 9972);
		itemMapping.put("언월도", 9971);
		itemMapping.put("띠 갑옷", 9969);
		itemMapping.put("넓은 창", 9967);
		itemMapping.put("싸울아비 장검", 9966);
		itemMapping.put("대검", 9965);
		itemMapping.put("미늘 갑옷", 9964);
		// 추가적인 매핑은 생략

		Map<String, Integer> cursedItemMapping = new HashMap<>();
		cursedItemMapping.put("무기 마법 주문서", 9996);
		cursedItemMapping.put("갑옷 마법 주문서", 9997);
		cursedItemMapping.put("양손검", 9989);
		cursedItemMapping.put("악운의 단검", 9988);
		cursedItemMapping.put("마법 망토", 9986);
		cursedItemMapping.put("투구", 9984);
		cursedItemMapping.put("마법 방어 투구", 9984);
		cursedItemMapping.put("큰 방패", 9982);
		cursedItemMapping.put("판금 갑옷", 9981);
		cursedItemMapping.put("언월도", 9970);
		cursedItemMapping.put("띠 갑옷", 9968);
		cursedItemMapping.put("미늘 갑옷", 9963);
		cursedItemMapping.put("그라디우스", 9962);
		cursedItemMapping.put("짧은 장화", 9961);
		cursedItemMapping.put("징박힌 가죽 갑옷", 9960);
		cursedItemMapping.put("비늘 갑옷", 9959);
		cursedItemMapping.put("오크족 단검", 9958);
		cursedItemMapping.put("오크족 활", 9957);
		cursedItemMapping.put("오크족 투구", 9956);
		cursedItemMapping.put("오크족 고리 갑옷", 9955);
		cursedItemMapping.put("오크족 사슬 갑옷", 9954);
		cursedItemMapping.put("우럭하이 방패", 9953);
		
		for (Drop s : m.getDropList()) {
		    Item i = ItemDatabase.find(s.getItemName());
		    if (i != null) {
		        writeD(0);
		        
		        int invGfx = i.getInvGfx();
		        if (s.getItemBress() == 0 && itemMapping.containsKey(i.getName())) {
		            invGfx = itemMapping.get(i.getName());
		        } else if (s.getItemBress() == 2 && cursedItemMapping.containsKey(i.getName())) {
		            invGfx = cursedItemMapping.get(i.getName());
		        }

		        writeH(invGfx);
		        writeD(0);

		        StringBuffer sb = new StringBuffer();
		        if (!itemMapping.containsKey(i.getName()) && !cursedItemMapping.containsKey(i.getName())) {
		            sb.append(s.getItemBress() == 0 ? "[축복]" : (s.getItemBress() == 2 ? "[저주]" : ""));
		        }
				// 인첸트 레벨 표현
				if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor")))
					sb.append(s.getItemEn() > 0 ? " +" : s.getItemEn() < 0 ? " -" : "")
							.append(s.getItemEn() > 0 ? s.getItemEn() : "");
				// 이름 표현
				sb.append(" ").append(i.getName());
				// 수량 표현
				if (s.getCountMax() > 1)
					sb.append(" (1 ~ ").append(s.getCountMax()).append(")");
				writeS(sb.toString());

				if (Lineage.server_version > 144) {
					if (i.getType1().equalsIgnoreCase("armor")) {
						toArmor(i, null, 0, s.getItemEn(), (int) i.getWeight(), 
								s.getItemEn() > 4 ? (s.getItemEn() - 4) * i.getEnchantMr() : 0,
								s.getItemBress(), s.getItemEn() * i.getEnchantStunDefense());
					} else if (i.getType1().equalsIgnoreCase("weapon")) {
						toWeapon(i, null, 0, s.getItemEn(), (int) i.getWeight(), s.getItemBress());
					} else {
						toEtc(i, (int) i.getWeight());
					}
				}
			}
		}
	}
}
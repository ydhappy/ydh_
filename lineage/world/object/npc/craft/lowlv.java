package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class lowlv extends object {

	private long lastSoundPlayTime = 0;
	
	public class CreateItem {
		public String itemName;
		public boolean isCheckBless;
		public int bless;
		public boolean isCheckEnchant;
		public int enchant;
		public int count;
		public int yitem = 0;

		/**
		 * @param itemName
		 *            : 재료 아이템 이름
		 * @param isCheckBless
		 *            : 재료 축여부 체크
		 * @param bless
		 *            : 축복(0~2)
		 * @param isCheckEnchant
		 *            : 재료 인첸트 체크
		 * @param enchant
		 *            : 인첸트
		 * @param count
		 *            : 수량
		 */
		public CreateItem(String itemName, boolean isCheckBless, int bless, boolean isCheckEnchant, int enchant, int count) {
			this.itemName = itemName;
			this.isCheckBless = isCheckBless;
			this.bless = bless;
			this.isCheckEnchant = isCheckEnchant;
			this.enchant = enchant;
			this.count = count;
		}
	}

	public long getLastSoundPlayTime() {
		return lastSoundPlayTime;
	}

	public void setLastSoundPlayTime(long lastSoundPlayTime) {
		this.lastSoundPlayTime = lastSoundPlayTime;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		long currentTime = System.currentTimeMillis(); 
		
		if (pc.getLevel() <= 10) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlvS1"));
		} else if (pc.getLevel() <= 29) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlvS2"));
			if (currentTime - getLastSoundPlayTime() >= 2800) {
			pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27828)); 
			setLastSoundPlayTime(currentTime);
			}
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlvno"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getInventory() != null) {
			List<CreateItem> createList = new ArrayList<CreateItem>();
			List<CreateItem> createList2 = new ArrayList<CreateItem>();
			List<ItemInstance> itemList = new ArrayList<ItemInstance>();

			if (action.startsWith("0")) {
				if (pc.getLevel() < 10)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlvS1"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlvS2"));

			} else if (action.equalsIgnoreCase("a")) {
				// 게렝
				CraftController.toCraft(this, pc, ItemDatabase.find(505), 1, false);
				pc.toPotal(32562, 33082, 0);
				// 이동후 창닫기
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			} else if (action.equalsIgnoreCase("b")) {
				// 로우풀
				CraftController.toCraft(this, pc, ItemDatabase.find(505), 1, false);
				pc.toPotal(33118, 32937, 4);
				// 이동후 창닫기
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			} else if (action.equalsIgnoreCase("c")) {
				// 카오틱
				CraftController.toCraft(this, pc, ItemDatabase.find(505), 1, false);
				pc.toPotal(32886, 32652, 4);
				// 이동후 창닫기
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));

			} else if (action.equalsIgnoreCase("k")) {
				if (pc.getInventory().find("상아탑의 반지") != null) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv45"));
				} else {
					CraftController.toCraft(this, pc, ItemDatabase.find(3294), 1, false);
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv44"));
				}

			} else if (action.equalsIgnoreCase("3")) {
				if (pc.getInventory().isAden(1000, true)) {
					CraftController.toCraft(this, pc, ItemDatabase.find(8430), 1, false);

					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv18"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv20"));
				}
			} else if (action.equalsIgnoreCase("4")) {
				if (pc.getInventory().isAden(1500, true)) {
					CraftController.toCraft(this, pc, ItemDatabase.find(8429), 1, false);

					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv19"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv20"));
				}
			} else if (action.equalsIgnoreCase("5")) {
				if (pc.getInventory().isAden(250, true)) {
					CraftController.toCraft(this, pc, ItemDatabase.find(8428), 1, false);

					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv21"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv20"));
				}
			} else if (action.equalsIgnoreCase("6")) {
				if (pc.getInventory().find("상아탑의 마법 주머니") != null) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv23"));
				} else if (pc.getInventory().isAden(2000, true)) {
					CraftController.toCraft(this, pc, ItemDatabase.find(8426), 1, false);

					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv22"));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv20"));
				}

			} else if (action.equalsIgnoreCase("2")) {

				if (pc.getInventory().find("상아탑의 가죽 투구") != null && pc.getInventory().find("상아탑의 가죽 갑옷") != null && pc.getInventory().find("상아탑의 가죽 장갑") != null && pc.getInventory().find("상아탑의 가죽 샌달") != null
						&& pc.getInventory().find("상아탑의 가죽 방패") != null && pc.getInventory().find("상아탑의 망토") != null && pc.getInventory().find("상아탑의 단검") != null

						&& pc.getInventory().find("상아탑의 한손검") != null && pc.getInventory().find("상아탑의 양손검") != null && pc.getInventory().find("상아탑의 도끼") != null && pc.getInventory().find("상아탑의 창") != null
						&& pc.getInventory().find("상아탑의 활") != null && pc.getInventory().find("상아탑의 석궁") != null && pc.getInventory().find("상아탑의 지팡이") != null && pc.getInventory().find("화살") != null) {
				}
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv17"));

				if (pc.getInventory().find("상아탑의 가죽 투구") != null && pc.getInventory().find("상아탑의 가죽 갑옷") != null && pc.getInventory().find("상아탑의 가죽 장갑") != null && pc.getInventory().find("상아탑의 가죽 샌달") != null
						&& pc.getInventory().find("상아탑의 가죽 방패") != null && pc.getInventory().find("상아탑의 망토") != null && pc.getInventory().find("상아탑의 단검") != null

						&& pc.getInventory().find("상아탑의 한손검") != null && pc.getInventory().find("상아탑의 양손검") != null && pc.getInventory().find("상아탑의 도끼") != null && pc.getInventory().find("상아탑의 창") != null
						&& pc.getInventory().find("상아탑의 활") != null && pc.getInventory().find("상아탑의 석궁") != null && pc.getInventory().find("상아탑의 지팡이") != null && pc.getInventory().find("화살") != null) {

				} else {
					if (pc.getInventory().find("상아탑의 가죽 투구") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(3289), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}

					if (pc.getInventory().find("상아탑의 가죽 갑옷") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(3290), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}

					if (pc.getInventory().find("상아탑의 가죽 장갑") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(3292), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}

					if (pc.getInventory().find("상아탑의 가죽 샌달") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(3291), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}

					if (pc.getInventory().find("상아탑의 가죽 방패") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(3293), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}

					if (pc.getInventory().find("상아탑의 망토") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(8544), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}

					if (pc.getInventory().find("상아탑의 단검") == null) {
						CraftController.toCraft(this, pc, ItemDatabase.find(3288), 1, false);
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
					}
					switch (pc.getClassType()) {
					case Lineage.LINEAGE_CLASS_ROYAL: // 군주
						if (pc.getInventory().find("상아탑의 한손검") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3279), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 양손검") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3280), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 도끼") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3281), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						break;
					case Lineage.LINEAGE_CLASS_KNIGHT:
						if (pc.getInventory().find("상아탑의 한손검") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3279), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 양손검") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3280), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 도끼") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3281), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 창") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3282), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						break;
					case Lineage.LINEAGE_CLASS_ELF:
						if (pc.getInventory().find("상아탑의 한손검") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3279), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 활") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3283), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("상아탑의 석궁") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3284), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						if (pc.getInventory().find("화살") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(61), 2000, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						break;
					case Lineage.LINEAGE_CLASS_WIZARD:
						if (pc.getInventory().find("상아탑의 지팡이") != null) {
						} else {
							CraftController.toCraft(this, pc, ItemDatabase.find(3285), 1, false);
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "lowlv16"));
						}
						break;
					}
				}
			}
		}
	}

	public void checkItem(PcInstance pc, List<CreateItem> createList, List<ItemInstance> itemList) {
		if (createList != null && itemList != null) {
			if (itemList.size() > 0)
				itemList.clear();

			for (CreateItem list : createList) {
				for (ItemInstance i : pc.getInventory().getList()) {
					if (i.getItem() != null && i.getItem().getName().equalsIgnoreCase(list.itemName) && i.getCount() >= list.count && !i.isEquipped()) {
						// 축여부 체크일 경우
						if (list.isCheckBless) {
							// 인첸트 체크일 경우
							if (list.isCheckEnchant) {
								if (i.getBless() == list.bless && i.getEnLevel() == list.enchant) {
									itemList.add(i);
									break;
								}
							} else {
								if (i.getBless() == list.bless) {
									itemList.add(i);
									break;
								}
							}
						} else {
							// 인첸트 체크일 경우
							if (list.isCheckEnchant) {
								if (i.getEnLevel() == list.enchant) {
									itemList.add(i);
									break;
								}
							} else {
								itemList.add(i);
								break;
							}
						}
					}
				}
			}
		}
	}

	public void createItem(PcInstance pc, List<CreateItem> createList, List<CreateItem> createList2, List<ItemInstance> itemList, String createItemName, int bless, int enchant, int count) {
		if ((createList.size() > 0 && itemList.size() > 0 && createList.size() == itemList.size()) || (createList2.size() > 0 && itemList.size() > 0 && createList2.size() == itemList.size())) {

			Item i = ItemDatabase.find(createItemName);

			if (i != null) {
				ItemInstance temp = pc.getInventory().find(i.getItemCode(), i.getName(), bless, i.isPiles());

				if (temp != null && (temp.getBless() != bless || temp.getEnLevel() != enchant))
					temp = null;

				if (temp == null) {
					// 겹칠수 있는 아이템이 존재하지 않을경우.
					if (i.isPiles()) {
						temp = ItemDatabase.newInstance(i);
						temp.setObjectId(ServerDatabase.nextItemObjId());
						temp.setBless(bless);
						temp.setEnLevel(enchant);
						temp.setCount(count);
						temp.setDefinite(false);
						pc.getInventory().append(temp, true);
					} else {
						for (int idx = 0; idx < count; idx++) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(bless);
							temp.setEnLevel(enchant);
							temp.setDefinite(false);
							pc.getInventory().append(temp, true);
						}
					}
				} else {
					// 겹치는 아이템이 존재할 경우.
					pc.getInventory().count(temp, temp.getCount() + count, true);
				}

				if (createList2.size() == 0) {
					for (CreateItem list : createList) {
						for (ItemInstance item : itemList) {
							if (item != null && item.getItem() != null && list.itemName.equalsIgnoreCase(item.getItem().getName()))
								pc.getInventory().count(item, item.getCount() - list.count, true);
						}
					}
				} else {
					for (CreateItem list : createList2) {
						for (ItemInstance item : itemList) {
							if (item != null && item.getItem() != null && list.itemName.equalsIgnoreCase(item.getItem().getName()))
								pc.getInventory().count(item, item.getCount() - list.count, true);
						}
					}
				}
			}
		} else {
			String msg = "";

			if (createList2.size() > 0) {
				int idx = 0;

				for (CreateItem list : createList) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList.size())
						msg += ", ";
				}

				idx = 0;
				msg += " 또는 ";

				for (CreateItem list : createList2) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList2.size())
						msg += ", ";
				}
			} else {
				int idx = 0;

				for (CreateItem list : createList) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList.size())
						msg += ", ";
				}
			}

			ChattingController.toChatting(pc, String.format("[%s] %s 이(가) 부족합니다.", createItemName, msg), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
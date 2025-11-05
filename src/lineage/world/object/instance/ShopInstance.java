package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.bean.lineage.Kingdom;
import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.item.RaceTicket;

public class ShopInstance extends object {

	protected Npc npc;
	// 성 정보
	protected Kingdom kingdom;

    private List<Shop> shopItems = new ArrayList<>(); // ✅ 가상 상점 아이템 목록 추가
    
	public ShopInstance() {
		this.npc = null;
		kingdom = null;
	}
	
	public ShopInstance(Npc npc) {
		this.npc = npc;
		kingdom = null;
	}

	public Npc getNpc() {
		return npc;
	}

	public void setNpc(Npc npc) {
		this.npc = npc;
	}
	
	 // ✅ 가상 상점 아이템 목록 저장
    public void setShopItems(List<Shop> shopItems) {
        this.shopItems = shopItems;
    }

    // ✅ 가상 상점 아이템 목록 반환
    public List<Shop> getShopItems() {
        return shopItems;
    }
    
	/**
	 * 현재 물가 추출.
	 * 
	 * @return
	 */
	public int getTax() {
		if (Lineage.shop_no_tax_npc != null && getNpc() != null && Lineage.shop_no_tax_npc.contains(getNpc().getName())) {
			return 0;
		}
		
		return kingdom == null ? 0 : kingdom.getTaxRate();
	}

	/**
	 * 세금으로인한 차액을 공금에 추가하기.
	 * 
	 * @param price
	 */
	public void addTax(int price) {
		if (kingdom != null && Lineage.add_tax)
			kingdom.toTax(price, true, "shop");
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		//자동판매 초기화
		pc.isAutoSellAdding = false;
		pc.isAutoSellDeleting = false;
		
		if (action.equalsIgnoreCase("buy")) {
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		} else if (action.equalsIgnoreCase("sell")) {
			List<ItemInstance> sell_list = new ArrayList<ItemInstance>();
			//쿠베라 상점 매입 부분 개편
			for (Shop s : npc.getShop_list()) {
				// 판매할 수 있도록 설정된 목록만 처리.
				if (s.isItemSell()) {
					List<ItemInstance> search_list = new ArrayList<ItemInstance>();
					pc.getInventory().findDbName(s.getItemName(), search_list);
					for (ItemInstance item : search_list) {
						if (!item.isEquipped() && item.getItem().isSell() && (s.getItemEnLevel() == 0 || s.getItemEnLevel() == item.getEnLevel())) {
							//
							if (isSellAdd(item) && !sell_list.contains(item) && item.getEnLevel() == s.getItemEnLevel())
								sell_list.add(item);
						}
					}
				}
			}
			if (sell_list.size() > 0){
//			    System.out.println("Items in sellList:");
//	        for (ItemInstance item : sell_list) {
//	            System.out.println("Item: " + item.getName() + " (EnLevel: " + item.getEnLevel() + ")");
//	        }
				pc.toSender(S_ShopSell.clone(BasePacketPooling.getPool(S_ShopSell.class), this, sell_list));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nosell"));
			}	
		} else if (action.indexOf("3") > 0 || action.indexOf("6") > 0 || action.indexOf("7") > 0) {
			List<String> list_html = new ArrayList<String>();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}

	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {    
	    int opcode = cbp.readC(); // opcode 읽기
	    
	    switch (opcode) {
	        case 0: // 상점 구입
	            toBuy(pc, cbp);
	            break;
	        case 1: // 상점 판매
	            toSell(pc, cbp);
	            break;
	        default:
	    }
	}

	   /**
     * 상점 구매 로직
     * - 아이템 생성 시 createItemInstance -> applyItemDuration
     * - applyItemDuration은 "최초 생성"에만 호출되어 만료 시간을 고정
     */
    protected void toBuy(PcInstance pc, ClientBasePacket cbp) {
        long count = cbp.readH(); // 구매 요청 아이템(종류) 개수

        if (count > 0 && count <= 100) {
            for (int j = 0; j < count; ++j) {
                long item_idx = cbp.readD();   // 아이템 ID
                long item_count = cbp.readD(); // 구매할 개수

                // [1] 가상 상점 확인
                if (pc.getTempGmShop() == null) {
                    pc.setTempGmShop(new ShopInstance());
                }
                ShopInstance tempShop = pc.getTempGmShop();

                // [2] NPC 상점 -> 가상 상점 순으로 아이템 검색
                Shop s = (npc != null) ? npc.findShop(item_idx) : null;
                boolean isFromVirtualShop = false;

                if (s == null && tempShop != null) {
                    for (Shop shopItem : tempShop.getShopItems()) {
                        Item tempItem = ItemDatabase.find_ItemCode(shopItem.getItemCode());
                        if (tempItem != null && tempItem.getItemCode() == item_idx) {
                            s = shopItem;
                            isFromVirtualShop = true;
                            break;
                        }
                    }
                }

                // 못 찾으면 종료
                if (s == null) {
                    return;
                }

                // 가상 상점 아니고 isItemBuy=false 이거나, 개수 범위 초과시
                if (!isFromVirtualShop && (!s.isItemBuy() || item_count <= 0 || item_count > 1000)) {
                    return;
                }

                // 아이템 DB
                Item i = ItemDatabase.find_ItemCode(s.getItemCode());
                if (i == null) {
                    return;
                }

                // 상점 판매 가격
                int shop_price = calcShopPrice(s, i);

                // 묶음 단위 반영
                long new_item_count = item_count * s.getItemCount();

                // 인벤토리 추가 가능 여부
                if (pc.getInventory().isAppend(i, count, i.isPiles() ? 1 : new_item_count)) {

                    // [A] 포인트 구매
                    if (s.getAdenType().equalsIgnoreCase("포인트")) {
                        long check2 = AccountDatabase.userpointcheck(pc.getAccountId());
                        long check1 = shop_price * item_count;

                        if (check1 > check2) {
                            pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 776, s.getAdenType()));
                            break;
                        } else {
                            // 포인트 차감
                            AccountDatabase.userpointbuy(pc.getAccountId(), check1);

                            ItemInstance temp = pc.getInventory().find(s.getItemCode(), s.getItemName(), s.getItemBress(), i.isPiles());
                            if (temp == null) {
                                createItemInstance(pc, s, i, new_item_count, item_count, shop_price, true);
                            } else {
                                pc.getInventory().count(temp, temp.getCount() + new_item_count, true);
                                logItemPurchase(pc, temp, s, item_count);
                            }
                        }

                    } else {
                        // [B] 아데나 구매 등
                        long totalCost = shop_price * item_count;
                        if (pc.getInventory().isAden(s.getAdenType(), totalCost, true)) {
                            ItemInstance temp = pc.getInventory().find(s.getItemCode(), s.getItemName(), s.getItemBress(), i.isPiles());
                            if (temp == null) {
                                createItemInstance(pc, s, i, new_item_count, item_count, shop_price, false);
                            } else {
                                pc.getInventory().count(temp, temp.getCount() + new_item_count, true);
                                logItemPurchase(pc, temp, s, item_count);
                            }

                            // 아데나면 세금
                            if (s.getAdenType().equalsIgnoreCase("아데나")) {
                                if (s.getPrice() != 0) {
                                    addTax((int)((shop_price - s.getPrice()) * item_count));
                                } else {
                                    addTax((int)((shop_price - i.getShopPrice()) * item_count));
                                }
                            }

                        } else {
                            pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 776, s.getAdenType()));
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 아이템 생성 (스택 가능/불가능 공통)
     * applyEnchantOptions -> applyItemDuration(최초 1회) -> 인벤토리 추가
     */
    private void createItemInstance(PcInstance pc, Shop s, Item i,
                                    long newItemCount, long requestCount, int shopPrice, boolean isPointType) {
        if (i.isPiles()) {
            // 스택 가능
            ItemInstance temp = ItemDatabase.newInstance(i);
            temp.setObjectId(ServerDatabase.nextItemObjId());
            temp.setCount(newItemCount);

            applyEnchantOptions(temp, s, i);
            // ****** 기간제 설정은 "최초 생성" 때만 ******
            applyItemDuration(pc, temp, s, i);

            pc.getInventory().append(temp, true);
            logItemPurchase(pc, temp, s, requestCount);
            tryAppendGuiLog(pc, temp, requestCount, shopPrice);

        } else {
            // 스택 불가 -> 개수만큼 생성
            for (int k = 0; k < newItemCount; k++) {
                ItemInstance temp = ItemDatabase.newInstance(i);
                temp.setObjectId(ServerDatabase.nextItemObjId());

                applyEnchantOptions(temp, s, i);
                // ****** 기간제 설정은 "최초 생성" 때만 ******
                applyItemDuration(pc, temp, s, i);

                pc.getInventory().append(temp, true);
                logItemPurchase(pc, temp, s, requestCount);
                tryAppendGuiLog(pc, temp, requestCount, shopPrice);
            }
        }
    }

    /**
     * 아이템에 인챈트(레벨, 축/저주, 속성) 적용
     */
    private void applyEnchantOptions(ItemInstance temp, Shop s, Item i) {
        if (s.isGamble()) {
            temp.setEnLevel(getGambleEnLevel());
            temp.setBless(getGambleBless());
        } else {
            temp.setEnLevel(s.getItemEnLevel());
            temp.setBless(s.getItemBress());
        }

        temp.setEnFire(s.getInvItemEnFire());
        temp.setEnWater(s.getInvItemEnWater());
        temp.setEnWind(s.getInvItemEnWind());
        temp.setEnEarth(s.getInvItemEnEarth());

        // "신성한 엘름의 축복" +4 이상 특수처리
        if (i.getName().equalsIgnoreCase("신성한 엘름의 축복")) {
            temp.setDynamicMr(s.getItemEnLevel() > 4 ? (s.getItemEnLevel() - 4) * i.getEnchantMr() : 0);
        } else {
            temp.setDynamicMr(s.getItemEnLevel() * i.getEnchantMr());
        }

        temp.setDynamicStunDefence(s.getItemEnLevel() * i.getEnchantStunDefense());
        temp.setDynamicStunHit(s.getItemEnLevel() * i.getEnchantStunHit());
        temp.setDynamicSp(s.getItemEnLevel() * i.getEnchantSp());
        temp.setDynamicReduction(s.getItemEnLevel() * i.getEnchantReduction());
        temp.setDynamicIgnoreReduction(s.getItemEnLevel() * i.getEnchantIgnoreReduction());
        temp.setDynamicSwordCritical(s.getItemEnLevel() * i.getEnchantSwordCritical());
        temp.setDynamicBowCritical(s.getItemEnLevel() * i.getEnchantBowCritical());
        temp.setDynamicMagicCritical(s.getItemEnLevel() * i.getEnchantMagicCritical());
        temp.setDynamicPvpDmg(s.getItemEnLevel() * i.getEnchantPvpDamage());
        temp.setDynamicPvpReduction(s.getItemEnLevel() * i.getEnchantPvpReduction());

        temp.setDefinite(true);
    }

    /**
     * 아이템 최초 생성 시 '기간제' 설정 메서드
     * (KST 기준, daysToAdd 일 후 시점을 epochMillis로 저장)
     */
    private void applyItemDuration(PcInstance pc, ItemInstance temp, Shop s, Item i) {
        int daysToAdd = 0;

        // 1) 아이템 이름에 따른 기간 설정
        if (s.getItemName().contains("1일")) {
            daysToAdd = 1;
        } else if (s.getItemName().contains("3일")) {
            daysToAdd = 3;
        } else if (s.getItemName().contains("7일")) {
            daysToAdd = 7;
        } else if (s.getItemName().contains("30일")) {
            daysToAdd = 30;
        } else {
            // 기존 기간 값 유지
            if (s.getItemTimeK() != null) {
                temp.setItemTimek(s.getItemTimeK());
            }
        }

        // 2) daysToAdd > 0이면 now + daysToAdd (Epoch Millis 저장)
        if (daysToAdd > 0) {
            ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            ZonedDateTime futureKST = nowKST.plusDays(daysToAdd);

            long epochMillis = futureKST.toInstant().toEpochMilli();
            temp.setItemTimek(Long.toString(epochMillis));

            // 안내 메시지
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
            String dateString = futureKST.format(fmt);
            String chatMsg = String.format("%s 아이템은 %s까지 사용 가능합니다.", temp.getItem().getName(), dateString);

            ChattingController.toChatting(pc, chatMsg, Lineage.CHATTING_MODE_MESSAGE);
        }

        // 3) 추가적인 시간 제한 (s.getItemTime(), s.getItemTimeLimit())
        if (s.getItemTime() > 0)
            temp.setNowTime(s.getItemTime());
        if (s.getItemTimeLimit() > 0)
            temp.setLimitTime(System.currentTimeMillis() + (s.getItemTime() * 1000));
    }



    /**
     * 아이템 구매 로그
     */
    private void logItemPurchase(PcInstance pc, ItemInstance item, Shop s, long itemCount) {
        Log.appendItem(
            pc,
            "type|상점구입",
            String.format("npc_name|%s", (npc != null ? npc.getName() : "UnknownNPC")),
            String.format("item_name|%s", s.getItemName()),
            String.format("target_name|%s", item.toStringDB()),
            String.format("item_objid|%d", item.getObjectId()),
            String.format("count|%d", itemCount),
            String.format("shop_uid|%d", s.getUid()),
            String.format("shop_count|%d", s.getItemCount())
        );
    }

    /**
     * GUI 로그 (예시)
     */
    private void tryAppendGuiLog(PcInstance pc, ItemInstance item, long itemCount, int shopPrice) {
        if (!Common.system_config_console && !(pc instanceof PcRobotInstance) && pc instanceof PcInstance) {
            long time = System.currentTimeMillis();
            String timeString = Util.getLocaleString(time, true);
            String log = String.format(
                "[%s]\t[%s]\t [캐릭터: %s] \t %s: \t구매 아이템: %s\t      구매 가격: %d",
                timeString, 
                "상점 구매", 
                pc.getName(), 
                (npc != null ? npc.getName() : "Unknown NPC"),
                Util.getItemNameToString(item, itemCount),
                (shopPrice * itemCount)
            );

            GuiMain.display.asyncExec(new Runnable() {
                public void run() {
                    GuiMain.getViewComposite().getConnectorComposite().toLog(log);
                }
            });
        }
    }

    /**
     * 상점 가격 계산 (원본 로직)
     */
    private int calcShopPrice(Shop s, Item i) {
        int shop_price = 0;

        if (s.getPrice() != 0) {
            shop_price = getTaxPrice(s.getPrice(), false);
        } else {
            if ((i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor"))
                && !i.getType2().equalsIgnoreCase("necklace")
                && !i.getType2().equalsIgnoreCase("ring")
                && !i.getType2().equalsIgnoreCase("belt")) {

                if (i.getType1().equalsIgnoreCase("weapon")) {
                    shop_price = getTaxPrice(
                        i.getShopPrice() * s.getItemCount()
                        + (s.getItemEnLevel() * ItemDatabase.find(244).getShopPrice()),
                        false
                    );
                } else {
                    shop_price = getTaxPrice(
                        i.getShopPrice() * s.getItemCount()
                        + (s.getItemEnLevel() * ItemDatabase.find(249).getShopPrice()),
                        false
                    );
                }
            } else {
                if ((i.getName().equalsIgnoreCase(Lineage.scroll_dane_fools)
                     || i.getName().equalsIgnoreCase(Lineage.scroll_zel_go_mer)
                     || i.getName().equalsIgnoreCase(Lineage.scroll_orim))
                    && (s.getItemBress() == 0 || s.getItemBress() == 2)) {

                    if (s.getItemBress() == 0) {
                        shop_price = getTaxPrice(
                            ItemDatabase.find(i.getNameIdNumber()).getShopPrice() * Lineage.sell_bless_item_rate,
                            false
                        );
                    } else {
                        shop_price = getTaxPrice(
                            ItemDatabase.find(i.getNameIdNumber()).getShopPrice() * Lineage.sell_curse_item_rate,
                            false
                        );
                    }
                } else {
                    shop_price = getTaxPrice(i.getShopPrice() * s.getItemCount(), false);
                }
            }
        }

        return shop_price;
    }
	    
	/**
	 * 상점 판매
	 */
	protected void toSell(PcInstance pc, ClientBasePacket cbp) {
		if (Lineage.open_wait && pc.getGm() == 0) {
			ChattingController.toChatting(pc, "[오픈 대기] 상점을 이용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		Connection con = null;
		int count = cbp.readH();
		if (count > 0) {
			try {
				con = DatabaseConnection.getLineage();
	
				
				for (int i = 0; i < count; ++i) {
					int inv_id = cbp.readD();
					long item_count = cbp.readD();
					ItemInstance temp = pc.getInventory().value(inv_id);
					
					
//					//자동상점 버그 수정 야도란
//					if (inv_id != temp.getObjectId()) {
//						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
//						return;
//					}
//					if ( item_count != 1) {
//						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
//						return;
//					}
//					if (item_count <= 0 || temp.getCount() <= 0) {
//						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
//						return;
//					}
//					if (item_count > temp.getCount()) {
//						item_count = temp.getCount();
//					}
					if (temp != null && !temp.isEquipped() && item_count > 0 && temp.getCount() >= item_count) {
						//
						String target_name = temp.toStringDB();
						long target_objid = temp.getObjectId();
						long aden_objid = 0;
						//
						Shop s = npc.findShopItemId(temp.getItem().getName(), temp.getBless());
						// 판매될수 있는 아이템만 처리.
						if (s != null && s.isItemSell()) {
							// 가격 체크
							long target_price = getPrice(con, temp);
							// 아덴 지급
							if (target_price > 0) {
								ItemInstance aden = pc.getInventory().find(s.getAdenType(), true);

								if (aden == null) {

									aden = ItemDatabase.newInstance(ItemDatabase.find(s.getAdenType()));
									aden.setObjectId(ServerDatabase.nextItemObjId());
									aden.setCount(0);

									if (!s.getAdenType().equalsIgnoreCase("포인트")) {
										pc.getInventory().append(aden, true);
									}

								}

								aden_objid = aden.getObjectId();

								long total = aden.getCount() + (target_price * item_count);

								if (s.getAdenType().equalsIgnoreCase("포인트")) {
									AccountDatabase.userpoint(pc.getAccountId(), total);

								} else {
									pc.getInventory().count(aden, aden.getCount() + (target_price * item_count), true);
								}
								//

								//
								Log.appendItem(pc, "type|상점판매금", "npc_name|" + getNpc().getName(), "aden_name|" + s.getAdenType(), "aden_objid|" + aden_objid, "target_name|" + target_name, "target_objid|" + target_objid,
										"target_price|" + target_price, "item_count|" + item_count);
								// 세금계산은 아데나일때만 처리.
								if (s.getAdenType().equalsIgnoreCase("아데나")) {
									// 세금으로인한 차액을 공금에 추가.
									if (s.getPrice() != 0)
										addTax((int) ((s.getPrice() * 0.5) - target_price));
									else
										addTax((int) ((temp.getItem().getShopPrice() * 0.5) - target_price));
								}

							}
							//
							Log.appendItem(pc, "type|상점판매", String.format("npc_name|%s", getNpc().getName()), String.format("target_name|%s", target_name), String.format("target_objid|%d", target_objid),
									String.format("target_price|%d", target_price), String.format("item_count|%d", item_count));

							// gui 로그
							if (!Common.system_config_console && !(pc instanceof PcRobotInstance) && pc instanceof PcInstance) {
								long time = System.currentTimeMillis();
								String timeString = Util.getLocaleString(time, true);
								String log = String.format("[%s]\t[%s]\t [캐릭터: %s] \t %s: \t판매 아이템: %s\t      판매 가격: %d", timeString, "상점 판매", pc.getName(), getNpc().getName(), Util.getItemNameToString(temp, item_count),
										(target_price * item_count));

								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getConnectorComposite().toLog(log);
									}
								});
							}
							// 판매되는 아이템 제거.
							pc.getInventory().count(temp, temp.getCount() - item_count, true);
						}
					}
				}
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}

	/**
	 * 설정된 세율에 따라 가격을 연산하여 리턴함.
	 * 
	 * @param price
	 * @return
	 */
	public int getTaxPrice(double price, boolean sell) {
		// sell 일경우 기본가격의 35%
		double a = sell ? price * Lineage.sell_item_rate : price;
		// 세율값 +@ 또는 -@ [원가에 지정된 세율만큼만]
		if (Lineage.add_tax) {
			if (sell)
				a -= a * (getTax() * 0.01);
			else
				a += a * (getTax() * 0.01);
		}
		// 반올림 처리.
		return (int) Math.round(a);
	}

	/**
	 * 겜블 상점에서 아이템 구매시 인첸트 값 추출해주는 함수.
	 * 
	 * @return
	 */
	private int getGambleEnLevel() {
		int en = 0;
		double percent = Math.random() * 100;
				
		if (percent < 0.05)
			en = 9;
		else if (percent < 0.1)
			en = 8;
		else if (percent < 0.5)
			en = 7;
		else if (percent < 2.5)
			en = 6;
		else if (percent < 5)
			en = 5;
		else if (percent < 10)
			en = 4;
		else if (percent < 20)
			en = 3;
		else if (percent < 30)
			en = 2;
		else if (percent < 50)
			en = 1;
	    
		return en;
	}

	/**
	 * 겜블 상점에서 아이템 구매시 축복 아이템 추출해주는 함수.
	 * 
	 * @return
	 */
	private int getGambleBless() {
		int bless = 1;
		double percent = Math.random() * 100;
				
	    // 10% 확률로 Bless 값을 0으로 설정
	    if (percent < 10) 
	    	bless = 0;	    
	    				   
		return bless;
	}
	
	/**
	 * 레이스 상점에 표현될 아이템에 가격을 추출.
	 * 
	 * @param item
	 * @param PcShop
	 * @return
	 */
	public int getPrice(Connection con, ItemInstance item) {
		// 슬라임 레이스표 가격 추출.
		if (item instanceof RaceTicket) {
			RaceTicket ticket = (RaceTicket) item;
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				// 로그 참고로 목록 만들기.
				st = con.prepareStatement("SELECT * FROM race_log WHERE uid=? AND race_idx=? AND type=?");
				st.setInt(1, ticket.getRaceUid());
				st.setInt(2, ticket.getRacerIdx());
				st.setString(3, ticket.getRacerType());
				rs = st.executeQuery();
				if (rs.next())
					return rs.getInt("price");
			} catch (Exception e) {
				lineage.share.System.println(ShopInstance.class + " : getPrice(Connection con, ItemInstance item)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
			// 당첨 안된거 0원
			return 0;
		}
		Shop shop = npc.findShopItemId2(item.getItem().getName(), item.getBless(),item.getEnLevel());
		// 그외 일반 아이템 가격 추출.
		if (item == null || shop == null/* || item.getItem().getType2().equalsIgnoreCase("arrow")*/) {
			// 버그도 무시.
			return 0;
		} else {
			if (shop.getPrice() != 0) {
				return shop.getPrice();
			} else {
				if ((item.getItem().getName().equalsIgnoreCase(Lineage.scroll_dane_fools) || item.getItem().getName().equalsIgnoreCase(Lineage.scroll_zel_go_mer)
						|| item.getItem().getName().equalsIgnoreCase(Lineage.scroll_orim)) && (item.getBless() == 0 || item.getBless() == 2))
					return getTaxPrice(item.getItem().getShopPrice() * (item.getBless() == 0 ? Lineage.sell_bless_item_rate : Lineage.sell_curse_item_rate), true);
				else
					return getTaxPrice(item.getItem().getShopPrice(), true);
			}

		}
	}

	/**
	 * 상점 판매목록에 추가해도 되는지 확인해주는 함수.
	 * 
	 * @return
	 */
	protected boolean isSellAdd(ItemInstance item) {
		return true;
	}

}

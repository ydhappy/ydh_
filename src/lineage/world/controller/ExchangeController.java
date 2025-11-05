package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lineage.bean.database.DungeonBook;
import lineage.bean.database.Item;
import lineage.bean.database.PcTradeShop;
import lineage.bean.database.PcTradeShopSell;
import lineage.bean.lineage.PcTradeShopAdd;
import lineage.bean.lineage.PcTradeShopBuy;
import lineage.bean.lineage.PcTradeShopBuyComparator;
import lineage.bean.lineage.PcTradeShopBuyListComparator;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.DungeontellbookDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_PcTradeShopBuy;
import lineage.network.packet.server.S_PcTradeShopChangePrice;
import lineage.network.packet.server.S_PcTradeShopSellAdd;
import lineage.network.packet.server.S_PcTradeShopSellAdd2;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.DogCollar;
import lineage.world.object.npc.ExchangeNpc;

public class ExchangeController {
	static Object sync_DB = new Object();
	static public object ExchangeNpc;
	static private List<PcTradeShop> list;
		
	static public void init(Connection con) {
		TimeLine.start("ExchangeController..");
		
		ExchangeNpc = new ExchangeNpc();
		ExchangeNpc.setObjectId(ServerDatabase.nextEtcObjId());
		
		list = new ArrayList<PcTradeShop>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {		
			st = con.prepareStatement("SELECT * FROM pc_trade_shop");
			rs = st.executeQuery();
			
			while (rs.next()) {
				PcTradeShop pts = new PcTradeShop();				
				pts.setPcObjectId(rs.getInt("pc_objId"));
				pts.setPcName(rs.getString("pc_name"));
				pts.setItemObjectId(rs.getInt("item_objId"));
				pts.setItemName(rs.getString("item_name"));
				pts.setBless(rs.getInt("bless"));
				pts.setEnLevel(rs.getInt("en_level"));
				pts.setDefinite(rs.getString("definite").equalsIgnoreCase("true"));
				pts.setCount(rs.getLong("count"));
				pts.setAdenType(rs.getString("aden_type"));
				pts.setPrice(rs.getLong("price"));		
				pts.setPetObjId(rs.getLong("pet_objId"));
				pts.setPetName(rs.getString("pet_name"));
				pts.setPetLevel(rs.getInt("pet_level"));
				pts.setPetClassId(rs.getInt("petClassId"));
				pts.setPetHp(rs.getInt("petHp"));
				pts.set무기속성(rs.getInt("무기속성"));
				
				try {
					pts.setSellTime(rs.getTimestamp("등록시간").getTime());
				} catch (Exception e) {
				}
				
				list.add(pts);
				append(pts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
		
	static public List<PcTradeShop> getList() {
		synchronized (list) {
			return new ArrayList<PcTradeShop>(list);
		}
	}
	
	static public PcTradeShop getPcShop(long itemObjId) {
		try {
			synchronized (list) {
				for (PcTradeShop pts : list) {
					if (pts != null) {
						if (pts.getItemObjectId() == itemObjId) {
							return pts;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	static public boolean append(PcTradeShop pts) {
		boolean is = true;
		
		try {
			if (pts != null) {				
				synchronized (list) {
					for (PcTradeShop temp : list) {
						if (temp != null) {
							if (temp.getItemObjectId() == pts.getItemObjectId()) {
								is = false;
							}
						}
					}
					
					if (is) {
						list.add(pts);
					}
				}
			}
		} catch (Exception e) {
			is = false;
			e.printStackTrace();
		}
		
		return is;
	}
	
	static public void remove(long itemObjId) {
		try {
			if (itemObjId > 0) {
				list.removeIf(temp -> temp.getItemObjectId() == itemObjId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public int getPcTradeShopCount(long objId) {
		int count = 0;
		
		if (objId > 0) {
			for (PcTradeShop pts : getList()) {
				if (pts != null) {
					if (pts.getPcObjectId() == objId) {
						count++;
					}
				}
			}
		}
		
		return count;
	}
	
	static public List<PcTradeShop> findPcTradeShop(PcInstance pc) {
		List<PcTradeShop> list = new ArrayList<PcTradeShop>();
		
		if (pc != null) {
			try {
				for (PcTradeShop pts : getList()) {
					if (pc.getObjectId() == pts.getPcObjectId()) {
						list.add(pts);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	static public boolean isPcTradeState(PcInstance pc) {
		if (pc == null || pc.isWorldDelete() || pc.isDead() || pc.isLock() || pc.getInventory() == null) {
			return false;
		}
		
		if (!World.isSafetyZone(pc.getX(), pc.getY(), pc.getMap())) {
			ChattingController.toChatting(pc, "[거래소] 세이프티존에서 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	static public void toCommand(object o) {
		if (o != null && o instanceof PcInstance) {
			if (ExchangeNpc != null) {
				ExchangeNpc.toTalk((PcInstance) o, null);
			}
		}
	}
	
	public static void appendHtml(PcInstance pc, ClientBasePacket cbp) {
		cbp.readC();
		
		if (pc.pc_trade_shop_step == 1) {
			appendItemHtml(pc, cbp);
		}
		
		if (pc.pc_trade_shop_step == 3) {
			appendItemHtml2(pc, cbp);
		}
		
		if (pc.pc_trade_shop_step == 4) {
			changeHtml2(pc, cbp);
		}
		
		if (pc.pc_trade_shop_step == 6) {
			removeHtml2(pc, cbp);
		}
		
		if (pc.pc_trade_shop_step == 8) {
			buyHtml3(pc, cbp);
		}	

		pc.pc_trade_shop_step++;
	}
	
	public static void appendHtml(PcInstance pc) {
	    if (pc == null) {
	        return;
	    }

	    if (!isPcTradeState(pc)) {
	        pc.pc_trade_shop_step = 0;
	        if (pc.pc_trade_shop_add_list != null) {
	            pc.pc_trade_shop_add_list.clear();
	        }
	        return;
	    }

	    // pc.pc_trade_shop_add_list 초기화 확인
	    if (pc.pc_trade_shop_add_list == null) {
	        pc.pc_trade_shop_add_list = new ArrayList<PcTradeShopAdd>();
	    }

	    try {
	        synchronized (pc.pc_trade_shop_add_list) {
	            List<ItemInstance> list = new ArrayList<ItemInstance>();

	            // 인벤토리 목록에서 판매할 아이템 찾기
	            for (ItemInstance item : pc.getInventory().getList()) {
	                if (isAppendItem(item) && !list.contains(item)) {
	                    list.add(item);
	                }
	            }

	            if (list.size() > 0) {
	                pc.pc_trade_shop_step = 1;

	                // 사용자에게 판매할 아이템 설정 요청 메시지 전송
	                ChattingController.toChatting(pc, "            \\fY판매할 아이템의 수량을 설정하십시오.", Lineage.CHATTING_MODE_MESSAGE);
	                
	                if (Lineage.pc_trade_shop_sell_tax > 0) {
	                    ChattingController.toChatting(pc, String.format("       \\fY아이템당 판매 가격 %.0f%%의 수수료가 필요합니다.", Lineage.pc_trade_shop_sell_tax * 100), Lineage.CHATTING_MODE_MESSAGE);
	                }

	                // 아이템 목록 전송
	                pc.toSender(S_PcTradeShopSellAdd.clone(BasePacketPooling.getPool(S_PcTradeShopSellAdd.class), ExchangeNpc, list));
	            } else {
	                // 판매할 아이템이 없는 경우 메시지 전송
	                ChattingController.toChatting(pc, "            \\fY판매 가능한 아이템이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
	            }
	        }
	    } catch (Exception e) {
	        // 예외 처리 시 메시지 및 스택 트레이스 출력
	        e.printStackTrace();
	        pc.pc_trade_shop_step = 0;
	    }
	}
	
	public static void appendItemHtml(PcInstance pc, ClientBasePacket cbp) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.pc_trade_shop_step = 0;
				pc.pc_trade_shop_add_list.clear();
			}

			return;
		}
		
		int tempCount = getPcTradeShopCount(pc.getObjectId()) + pc.pc_trade_shop_add_list.size();
		if (pc.getGm() == 0 && tempCount >= Lineage.pc_trade_shop_max_count) {
			pc.pc_trade_shop_step = 0;
			pc.pc_trade_shop_add_list.clear();
			ChattingController.toChatting(pc, String.format("\\fR[거래소] 최대 %d개까지 판매등록 가능합니다.", Lineage.pc_trade_shop_max_count), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		try {
			synchronized (pc.pc_trade_shop_add_list) {
				if (pc.pc_trade_shop_step == 1) {
					pc.pc_trade_shop_add_list.clear();

					int count = cbp.readH();

					if (count > 0) {
						for (int idx = 0; idx < count; ++idx) {
							int inv_id = cbp.readD();
							long item_count = cbp.readD();
							ItemInstance item = pc.getInventory().value(inv_id);
							
							if (isAppendItem(item)) {
								if (item_count <= item.getCount()) {					
									if (item_count < 1 || item_count > 2000000000) {
										continue;
									}

									if (containsList(pc, item) == null) {
										PcTradeShopAdd sell = new PcTradeShopAdd();
										sell.setItem(item);
										sell.setCount(item_count);
										pc.pc_trade_shop_add_list.add(sell);
									}
								}
							}
						}

						if (pc.pc_trade_shop_add_list.size() > 0) {
							pc.pc_trade_shop_step = 2;
							pc.toSender(S_PcTradeShopSellAdd2.clone(BasePacketPooling.getPool(S_PcTradeShopSellAdd2.class), ExchangeNpc, pc.pc_trade_shop_add_list));
							ChattingController.toChatting(pc, "            \\fY판매할 아이템의 가격을 설정하십시오.", Lineage.CHATTING_MODE_MESSAGE);
							
							if (Lineage.pc_trade_shop_sell_tax > 0) {
								ChattingController.toChatting(pc, String.format("       \\fY아이템당 판매 가격 %.0f%%의 수수료가 필요합니다.", Lineage.pc_trade_shop_sell_tax * 100), Lineage.CHATTING_MODE_MESSAGE);
							}
						}
					}
				} else {
					pc.pc_trade_shop_step = 0;
					pc.pc_trade_shop_add_list.clear();
				}
			}
		} catch (Exception e) {
			pc.pc_trade_shop_step = 0;
			pc.pc_trade_shop_add_list.clear();
		}
	}
	
	public static void appendItemHtml2(PcInstance pc, ClientBasePacket cbp) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.pc_trade_shop_step = 0;
				pc.pc_trade_shop_add_list.clear();
			}

			return;
		}
		
		int tempCount = getPcTradeShopCount(pc.getObjectId()) + pc.pc_trade_shop_add_list.size();
		if (pc.getGm() == 0 && tempCount > Lineage.pc_trade_shop_max_count) {
			pc.pc_trade_shop_step = 0;
			pc.pc_trade_shop_add_list.clear();
			ChattingController.toChatting(pc, String.format("\\fR[거래소] 최대 %d개까지 판매등록 가능합니다.", Lineage.pc_trade_shop_max_count), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if (pc.pc_trade_shop_add_list.size() > 0) {
			try {		
				synchronized (pc.pc_trade_shop_add_list) {
					if (pc.pc_trade_shop_step == 3) {
						int count = cbp.readH();

						if (count > 0) {
							for (int idx = 0; idx < count; ++idx) {
								int index = cbp.readD();
								long price = cbp.readD();
								ItemInstance item = pc.pc_trade_shop_add_list.get(index).getItem();
								
								if (isAppendItem(item)) {								
									if (price < 1 || price > 2000000000) {
										continue;
									}

									PcTradeShopAdd sell = containsList(pc, item);
									if (sell != null) {
										if (Lineage.pc_trade_shop_sell_tax > 0) {
											long tax = (long) ((price * sell.getCount()) * Lineage.pc_trade_shop_sell_tax);
											
											if (tax > 0) {
												if (!pc.getInventory().isAden(Lineage.pc_trade_shop_aden_type, tax, true)) {
													ChattingController.toChatting(pc, "           \\fR[거래소] 판매 수수료가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
													pc.pc_trade_shop_step = 0;
													pc.pc_trade_shop_add_list.clear();
													return;
												}
											}
										}
										
										sell.setPrice(price);								
										appendPcTradeShop(pc, sell);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				
			}
		}
		
		pc.pc_trade_shop_step = 0;
		pc.pc_trade_shop_add_list.clear();
	}
	
	public static String getItemString(PcTradeShop pts, long price, boolean isPrice) {
		StringBuffer msg = new StringBuffer();
		
		try {
			if (pts != null) {
				String itemName = CharacterMarbleDatabase.getItemName(pts.getItemObjectId());
				
				if (itemName != null) {
					msg.append(itemName);
				} else {
					Item i = ItemDatabase.find(pts.getItemName());
					
					if (i != null) {
						if (pts.getBless() == 0) {
							msg.append("[축] ");
						}
						
						if (pts.getBless() == 2) {
							msg.append("[저주] ");
						}
						
						if (pts.get무기속성() > 0) {
							msg.append(String.format("[%d단] ", pts.get무기속성()));
						}
						
						if (i.getNameIdNumber() == 1173) {
							msg.append(" [Lv.");
							msg.append(pts.getPetLevel());
							msg.append(" ");
							msg.append(pts.getPetName());
							msg.append("]");
						} else {
							if (i.getType1().equalsIgnoreCase("weapon") || i.getType1().equalsIgnoreCase("armor")) {
								if (pts.getEnLevel() >= 0) {
									msg.append("+");
								}
								
								msg.append(pts.getEnLevel());
								msg.append(" ");
							}
							
							if (i.getName().equalsIgnoreCase("펜던트")) {
								int a = pts.getEnLevel();

								if (a >= 20 && a <= 29)
									msg.append("빛나는 ");
								else if (a >= 30 && a <= 39)
									msg.append("영롱한 ");
								else if (a >= 40)
									msg.append("찬란한 ");
							}
							
							msg.append(pts.getItemName());
						}
						
						if (price > 1) {
							msg.append(String.format(" (%,d)", price));
						} else {
							if (pts.getCount() > 1) {
								msg.append(String.format(" (%,d)", pts.getCount()));
							}
						}
					}
				}
				
				if (isPrice) {
					msg.append(String.format(" (%,d %s)", pts.getPrice(), pts.getAdenType()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return msg.toString();
	}
	
	public static void changeHtml(PcInstance pc) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.pc_trade_shop_step = 0;
				pc.pc_trade_shop_add_list.clear();
			}

			return;
		}
		
		try {
			List<PcTradeShop> list = findPcTradeShop(pc);
			
			if (list != null) {
				pc.pc_trade_shop_step = 4;
				pc.toSender(S_PcTradeShopChangePrice.clone(BasePacketPooling.getPool(S_PcTradeShopChangePrice.class), ExchangeNpc, list));
				ChattingController.toChatting(pc, "            \\fY수정할 아이템의 가격을 설정하십시오.", Lineage.CHATTING_MODE_MESSAGE);
				
				if (Lineage.pc_trade_shop_sell_tax > 0) {
					ChattingController.toChatting(pc, String.format("       \\fS아이템당 판매 가격 %.0f%%의 수수료가 필요합니다.", Lineage.pc_trade_shop_sell_tax * 100), Lineage.CHATTING_MODE_MESSAGE);
				}
			}
		} catch (Exception e) {
			pc.pc_trade_shop_step = 0;
			pc.pc_trade_shop_add_list.clear();
		}
	}
	
	public static void changeHtml2(PcInstance pc, ClientBasePacket cbp) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.pc_trade_shop_step = 0;
				pc.pc_trade_shop_add_list.clear();
			}

			return;
		}
		
		try {
			if (pc.pc_trade_shop_step == 4) {
				int count = cbp.readH();

				if (count > 0) {
					for (int idx = 0; idx < count; ++idx) {
						int index = cbp.readD();
						long price = cbp.readD();
						List<PcTradeShop> list = findPcTradeShop(pc);
						PcTradeShop pts = list.get(index);
						
						if (price < 1 || price > 2000000000) {
							continue;
						}

						if (pts != null) {
							if (Lineage.pc_trade_shop_sell_tax > 0) {
								long tax = (long) ((price * pts.getCount()) * Lineage.pc_trade_shop_sell_tax);
								
								if (tax > 0) {
									if (!pc.getInventory().isAden(Lineage.pc_trade_shop_aden_type, tax, true)) {
										ChattingController.toChatting(pc, "           \\fR[거래소] 판매 수수료가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
										pc.pc_trade_shop_step = 0;
										pc.pc_trade_shop_add_list.clear();
										return;
									}
								}
							}
							
							pts.setDelay(System.currentTimeMillis() + 500);
							
							if (changePrice(pts, price)) {
								String temp = getItemString(pts, 0, true);
								pts.setPrice(price);
								ChattingController.toChatting(pc, String.format("[거래소] '%s' 가격 수정 완료", getItemString(pts, 0, true)), Lineage.CHATTING_MODE_MESSAGE);
								Log.append거래소_가격수정(pts, temp);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pc.pc_trade_shop_step = 0;
		pc.pc_trade_shop_add_list.clear();
	}
	
	public static void removeHtml(PcInstance pc) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.pc_trade_shop_step = 0;
				pc.pc_trade_shop_add_list.clear();
			}

			return;
		}
		
		try {
			List<PcTradeShop> list = findPcTradeShop(pc);
			
	        if (list == null || list.isEmpty()) {
	        	pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), ExchangeNpc, "tradesys6"));
	            return;
	        }
	        
			if (list != null) {
				pc.pc_trade_shop_step = 6;
				pc.toSender(S_PcTradeShopChangePrice.clone(BasePacketPooling.getPool(S_PcTradeShopChangePrice.class), ExchangeNpc, list));
				ChattingController.toChatting(pc, "            \\fY판매 종료할 아이템을 선택하십시오.", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			pc.pc_trade_shop_step = 0;
			pc.pc_trade_shop_add_list.clear();
		}
	}
	
	public static void removeHtml2(PcInstance pc, ClientBasePacket cbp) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.pc_trade_shop_step = 0;
				pc.pc_trade_shop_add_list.clear();
			}

			return;
		}
		
		try {
			if (pc.pc_trade_shop_step == 6) {
				int count = cbp.readH();

				if (count > 0) {
					List<PcTradeShop> removeList = new ArrayList<PcTradeShop>();
					
					for (int idx = 0; idx < count; ++idx) {
						int index = cbp.readD();
						long price = cbp.readD();
						List<PcTradeShop> list = findPcTradeShop(pc);
						
						if (price < 1) {
							continue;
						}
						
						removeList.add(list.get(index));
					}
					
					for (PcTradeShop pts : removeList) {
						if (pts != null) {
							pts.setDelay(System.currentTimeMillis() + 500);
							
							if (remove(pts)) {	
								아이템지급(pc, pts);
								ChattingController.toChatting(pc, String.format("[거래소] '%s' 판매 종료", getItemString(pts, 0, true)), Lineage.CHATTING_MODE_MESSAGE);
								remove(pts.getItemObjectId());						
								Log.append거래소_판매종료(pts, pts.getCount());
							}
						} else {
							ChattingController.toChatting(pc, "[거래소] 판매 종료에 실패하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					}
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(pc, "[거래소] 판매 종료에 실패하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
		
		pc.pc_trade_shop_step = 0;
		pc.pc_trade_shop_add_list.clear();
	}
	
	public static void 판매정산(PcInstance pc) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.set거래소정산금액(0);
				pc.set거래소정산화폐타입(null);
				pc.pc_trade_shop_sell_list.clear();
			}

			return;
		}

		try {
			if (pc.pc_trade_shop_sell_list != null) {
				pc.pc_trade_shop_sell_list.clear();

				Connection con = null;
				PreparedStatement st = null;
				ResultSet rs = null;

				try {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("SELECT * FROM pc_trade_shop_sell WHERE pc_objId=? AND 정산완료=0");
					st.setLong(1, pc.getObjectId());
					rs = st.executeQuery();

					synchronized (pc.pc_trade_shop_sell_list) {
						while (rs.next()) {
							PcTradeShopSell ptss = new PcTradeShopSell();
							ptss.setUid(rs.getLong("uid"));
							ptss.setPcObjId(rs.getLong("pc_objId"));
							ptss.setItemName(rs.getString("item_name"));
							ptss.setAdenType(rs.getString("aden_type"));
							ptss.setPrice(rs.getLong("price"));
							pc.pc_trade_shop_sell_list.add(ptss);
						}
					}
				} catch (Exception e) {
					lineage.share.System.printf("%s : 판매정산(PcInstance pc)\r\n", ExchangeController.class.toString());
					lineage.share.System.println(e);
					e.printStackTrace();
				} finally {
					DatabaseConnection.close(con, st, rs);
				}
				
				if (pc.pc_trade_shop_sell_list.size() < 1) {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), ExchangeNpc, "tradesys8"));
				} else {
					List<String> msg = new ArrayList<String>();
					long price = 0;
					String adenType = Lineage.pc_trade_shop_aden_type;
					
					for (PcTradeShopSell ptss : pc.pc_trade_shop_sell_list) {
						if (ptss != null) {
							price = price + ptss.getPrice();
							adenType = ptss.getAdenType();
						}
					}
					
					pc.set거래소정산금액(price);
					pc.set거래소정산화폐타입(adenType);
					msg.add(String.format("정산 금액: %s(%,d)", adenType, price));
					
					int idx = 1;
					for (PcTradeShopSell ptss : pc.pc_trade_shop_sell_list) {
						if (ptss != null) {
							msg.add(String.format("%d. %s", idx++, ptss.getItemName()));
						}
					}
					
					for (int i = 0; i < 200; i++) {
						msg.add(" ");
					}
					
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), ExchangeNpc, "tradesys7", null, msg));
				}
			}
		} catch (Exception e) {
			pc.set거래소정산금액(0);
			pc.set거래소정산화폐타입(null);
			pc.pc_trade_shop_sell_list.clear();
		}
	}
	
	public static void 판매정산2(PcInstance pc) {
		if (!isPcTradeState(pc)) {
			if (pc != null) {
				pc.set거래소정산금액(0);
				pc.set거래소정산화폐타입(null);
				pc.pc_trade_shop_sell_list.clear();
			}

			return;
		}

		try {
			if (pc.pc_trade_shop_sell_list != null) {
				synchronized (pc.pc_trade_shop_sell_list) {
					if (pc.get거래소정산화폐타입() == null || pc.get거래소정산금액() < 1 || pc.pc_trade_shop_sell_list.size() < 1) {
						pc.set거래소정산금액(0);
						pc.set거래소정산화폐타입(null);
						pc.pc_trade_shop_sell_list.clear();
						ExchangeNpc.toTalk(pc, null);
						ChattingController.toChatting(pc, "[판매 정산] 에러가 발생했습니다. 다시 시도해 주십시오.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
					
					Item item = ItemDatabase.find(pc.get거래소정산화폐타입());
					
					if (item != null) {
						ItemInstance tempItem = ItemDatabase.newInstance(item);
						tempItem.setCount(pc.get거래소정산금액());
						tempItem.setEnLevel(0);
						tempItem.setBless(1);
						tempItem.setDefinite(true);
						// 인벤에 등록처리.
						pc.getInventory().append(tempItem, tempItem.getCount());

						ChattingController.toChatting(pc, String.format("[거래소] 판매 정산 '%s(%,d)' 완료.", pc.get거래소정산화폐타입(), pc.get거래소정산금액()), Lineage.CHATTING_MODE_MESSAGE);
						
						Connection con = null;
						PreparedStatement st = null;

						try {
							con = DatabaseConnection.getLineage();

							for (PcTradeShopSell ptss : pc.pc_trade_shop_sell_list) {
								if (ptss != null) {
									try {
										// st = con.prepareStatement("UPDATE pc_trade_shop_sell SET 정산완료=1 WHERE uid=? AND pc_objId=?");
										st = con.prepareStatement("DELETE FROM pc_trade_shop_sell WHERE uid=? AND pc_objId=?");
										st.setLong(1, ptss.getUid());
										st.setLong(2, pc.getObjectId());
										st.executeUpdate();
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										DatabaseConnection.close(st);
									}
									
									try {
										Log.append거래소_정산(pc, ptss.getItemName());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						} catch (Exception e) {
							lineage.share.System.printf("%s : 판매정산2(PcInstance pc)\r\n", ExchangeController.class.toString());
							lineage.share.System.println(e);
							e.printStackTrace();
						} finally {
							DatabaseConnection.close(con, st);
						}
					}
				}
			}
		} catch (Exception e) {

		}
		
		ExchangeNpc.toTalk(pc, null);
		
		pc.set거래소정산금액(0);
		pc.set거래소정산화폐타입(null);
		pc.pc_trade_shop_sell_list.clear();
	}
	
	static private PcTradeShopAdd containsList(PcInstance pc, ItemInstance item) {
		if (pc != null && pc.pc_trade_shop_add_list != null) {
			for (PcTradeShopAdd pta : pc.pc_trade_shop_add_list) {
				if (pta != null && pta.getItem() != null) {
					if (pta.getItem().getObjectId() == item.getObjectId()) {
						return pta;
					}
				}
			}
		}
		
		return null;
	}
	
	static public boolean isAppendItem(ItemInstance item) {
		if (item != null && item.getItem() != null) {
			if (item.isEquipped()) {
				return false;
			}
			
			if (item.getCount() < 1) {
				return false;
			}
			
			// 거래 안되는 아이템은 무시.
			if (!item.getItem().isTrade() || item.getBless() < 0) {
				return false;
			}
			
			// 아데나 등록 못하도록 처리
			if (item.getItem().getNameIdNumber() == 4) {
				return false;
			}
		}
		
		return true;
	}
	
	static public void appendPcTradeShop(PcInstance pc, PcTradeShopAdd sell) {
		try {
			ItemInstance item = sell.getItem();
			
			if (item != null && item.getItem() != null) {	
				if (item.getCount() < sell.getCount()) {
					ChattingController.toChatting(pc, String.format("\\fR[%s] 수량이 부족합니다.", item.toStringSearch2(sell.getCount())), Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				
				if (getPcShop(item.getObjectId()) != null) {
					return;
				}
				
				PcTradeShop pts = new PcTradeShop();
				pts.setPcObjectId(pc.getObjectId());
				pts.setPcName(pc.getName());
				pts.setItemObjectId(item.getItem().isPiles() ? ServerDatabase.nextItemObjId() : item.getObjectId());
				pts.setItemName(item.getItem().getName());
				pts.setBless(item.getBless());
				pts.setEnLevel(item.getEnLevel());
				pts.setDefinite(true);
				pts.setCount(sell.getCount());
				pts.setPrice(sell.getPrice());
				pts.setAdenType(Lineage.pc_trade_shop_aden_type);
				pts.setSellTime(System.currentTimeMillis());
				
				if (item.getItem().getNameIdNumber() == 1173) {
					DogCollar dc = (DogCollar) item;
					pts.setPetObjId(item.getPetObjectId());
					pts.setPetLevel(dc.getPetLevel());
					pts.setPetName(dc.getPetName());
					pts.setPetClassId(dc.getPetClassId());
					pts.setPetHp(dc.getPetHp());
					SummonController.toPush(pc, item.getPetObjectId());
				}
				
				pts.set무기속성(item.get무기속성());
				
				if (append(pts)) {
					if (insertDB(pts)) {
						if (pts.getCount() > 1) {
							ChattingController.toChatting(pc, String.format("[거래소] '%s' 1개당 %,d(%s) 판매 등록", getItemString(pts, 0, false), pts.getPrice(), pts.getAdenType()), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							ChattingController.toChatting(pc, String.format("[거래소] '%s' 판매 등록", getItemString(pts, 0, true)), Lineage.CHATTING_MODE_MESSAGE);
						}
						
						pc.getInventory().count(item, item.getCount() - sell.getCount(), true);			
						Log.append거래소_판매등록(pts);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public boolean insertDB(PcTradeShop pts) {
		if (pts != null) {
			PreparedStatement st = null;
			Connection con = null;
			
			try {
				synchronized (list) {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("INSERT INTO pc_trade_shop SET pc_objId=?, pc_name=?, item_objId=?, item_name=?, bless=?, en_level=?, definite=?, count=?, aden_type=?, price=?, "
											+ "pet_objId=?, pet_name=?, pet_level=?, petClassId=?, petHp=?, 무기속성=?, 등록시간=?");
					st.setLong(1, pts.getPcObjectId());
					st.setString(2, pts.getPcName());
					st.setLong(3, pts.getItemObjectId());
					st.setString(4, pts.getItemName());
					st.setInt(5, pts.getBless());
					st.setInt(6, pts.getEnLevel());
					st.setString(7, pts.isDefinite() ? "true" : "false");
					st.setLong(8, pts.getCount());
					st.setString(9, pts.getAdenType());
					st.setLong(10, pts.getPrice());
					st.setLong(11, pts.getPetObjId());
					st.setString(12, pts.getPetName() == null ? "" : pts.getPetName());
					st.setInt(13, pts.getPetLevel());
					st.setInt(14, pts.getPetClassId());
					st.setInt(15, pts.getPetHp());
					st.setInt(16, pts.get무기속성());
					st.setTimestamp(17, new java.sql.Timestamp(pts.getSellTime()));
					st.executeUpdate();
					return true;
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : insertDB(PcTradeShop pts)\r\n", ExchangeController.class.toString());
				lineage.share.System.println(e);
				e.printStackTrace();
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
		
		return false;
	}
	
	static public void save(Connection con) {
		PreparedStatement st = null;
		
		try {
			synchronized (list) {
				st = con.prepareStatement("DELETE FROM pc_trade_shop");
				st.executeUpdate();
				st.close();
				
				for (PcTradeShop pts : list) {
					try {
						st = con.prepareStatement("INSERT INTO pc_trade_shop SET pc_objId=?, pc_name=?, item_objId=?, item_name=?, bless=?, en_level=?, definite=?, count=?, aden_type=?, price=?, "
								+ "pet_objId=?, pet_name=?, pet_level=?, petClassId=?, petHp=?, 무기속성=?, 등록시간=?");
						st.setLong(1, pts.getPcObjectId());
						st.setString(2, pts.getPcName());
						st.setLong(3, pts.getItemObjectId());
						st.setString(4, pts.getItemName());
						st.setInt(5, pts.getBless());
						st.setInt(6, pts.getEnLevel());
						st.setString(7, pts.isDefinite() ? "true" : "false");
						st.setLong(8, pts.getCount());
						st.setString(9, pts.getAdenType());
						st.setLong(10, pts.getPrice());
						st.setLong(11, pts.getPetObjId());
						st.setString(12, pts.getPetName() == null ? "" : pts.getPetName());
						st.setInt(13, pts.getPetLevel());
						st.setInt(14, pts.getPetClassId());
						st.setInt(15, pts.getPetHp());
						st.setInt(16, pts.get무기속성());
						st.setTimestamp(17, new java.sql.Timestamp(pts.getSellTime()));
						st.executeUpdate();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						DatabaseConnection.close(st);
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : insertDB(PcTradeShop pts)\r\n", ExchangeController.class.toString());
			lineage.share.System.println(e);
			e.printStackTrace();
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	static public boolean changePrice(PcTradeShop pts, long price) {
		if (pts != null) {
			PreparedStatement st = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("UPDATE pc_trade_shop SET price=? WHERE item_objId=?");
				st.setLong(1, price);
				st.setLong(2, pts.getItemObjectId());
				st.executeUpdate();
				return true;
			} catch (Exception e) {
				lineage.share.System.printf("%s : changePrice(PcTradeShop pts, long price)\r\n", ExchangeController.class.toString());
				lineage.share.System.println(e);
				e.printStackTrace();
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
		
		return false;
	}
	
	static public boolean changeCount(PcTradeShop pts, long count) {
		if (pts != null) {
			PreparedStatement st = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("UPDATE pc_trade_shop SET count=? WHERE item_objId=?");
				st.setLong(1, count);
				st.setLong(2, pts.getItemObjectId());
				st.executeUpdate();
				return true;
			} catch (Exception e) {
				lineage.share.System.printf("%s : changeCount(PcTradeShop pts, long count)\r\n", ExchangeController.class.toString());
				lineage.share.System.println(e);
				e.printStackTrace();
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
		
		return false;
	}
	
	static public boolean remove(PcTradeShop pts) {
		if (pts != null) {
			PreparedStatement st = null;
			Connection con = null;
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("DELETE FROM pc_trade_shop WHERE item_objId=?");
				st.setLong(1, pts.getItemObjectId());
				st.executeUpdate();
				return true;
			} catch (Exception e) {
				lineage.share.System.printf("%s : remove(PcTradeShop pts)\r\n", ExchangeController.class.toString());
				lineage.share.System.println(e);
				e.printStackTrace();
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
		
		return false;
	}
	
	static public void 아이템지급(PcInstance pc, PcTradeShop pts) {
		if (pts != null) {
			Item i = ItemDatabase.find(pts.getItemName());
			
			if (i != null) {
				// 사용자 아이템 추가.
				ItemInstance temp = pc.getInventory().find(i.getItemCode(), i.getName(), pts.getBless(),i.isPiles());
				
				if (temp == null) {
					// 겹칠수 있는 아이템이 존재하지 않을경우.
					if (i.isPiles()) {						
						temp = ItemDatabase.newInstance(i);
						temp.setObjectId(pts.getItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : pts.getItemObjectId());
						temp.setCount(pts.getCount());
						temp.setEnLevel(pts.getEnLevel());
						temp.setDefinite(pts.isDefinite());
						temp.setBless(pts.getBless());
						
						if (i.getName().equalsIgnoreCase("신성한 엘름의 축복")) {
							temp.setDynamicMr(pts.getEnLevel() > 4 ? (pts.getEnLevel() - 4) * i.getEnchantMr() : 0);
						} else {
							temp.setDynamicMr(pts.getEnLevel() * i.getEnchantMr());
						}
						
						temp.setDynamicStunDefence(pts.getEnLevel() * i.getEnchantStunDefense());
						temp.setPetObjectId(pts.getPetObjId());
						temp.set무기속성(pts.get무기속성());
						
						pc.getInventory().append(temp, true);
					} else {
						for (int k = 0; k < pts.getCount(); ++k) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(pts.getItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : pts.getItemObjectId());
							temp.setEnLevel(pts.getEnLevel());
							temp.setDefinite(pts.isDefinite());
							temp.setBless(pts.getBless());
							
							if (i.getName().equalsIgnoreCase("신성한 엘름의 축복")) {
								temp.setDynamicMr(pts.getEnLevel() > 4 ? (pts.getEnLevel() - 4) * i.getEnchantMr() : 0);
							} else {
								temp.setDynamicMr(pts.getEnLevel() * i.getEnchantMr());
							}
							
							temp.setDynamicStunDefence(pts.getEnLevel() * i.getEnchantStunDefense());
							temp.setPetObjectId(pts.getPetObjId());
							temp.set무기속성(pts.get무기속성());
							
							pc.getInventory().append(temp, true);
						}
					}
				} else {
					// 겹치는 아이템이 존재할 경우.
					pc.getInventory().count(temp, temp.getCount() + pts.getCount(), true);
				}
				
				if (temp instanceof DogCollar) {
					DogCollar dogCollar = (DogCollar) temp;
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						dogCollar.toWorldJoin(con, pc);
					} catch (Exception e) {
						
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		}
	}
	
	public static void buyHtml(PcInstance pc, String type) {
		if (!isPcTradeState(pc)) {
			return;
		}
		
		if (pc.pc_trade_shop_buy_list == null) {
			pc.pc_trade_shop_buy_list = new ArrayList<PcTradeShopBuy>();
		}
		
		pc.pc_trade_shop_buy_list.clear();
		
		try {
			synchronized (pc.pc_trade_shop_buy_list) {
				if (type.equalsIgnoreCase("weapon")) {
					for (Item i : ItemDatabase.getList()) {
						if (i != null && i.getType1().equalsIgnoreCase(type)) {
							for (PcTradeShop pts : getList()) {					
								if (pts != null) {
									if (pts.getItemName().contentEquals(i.getName())) {
										PcTradeShopBuy ptsb = new PcTradeShopBuy();
										ptsb.setItemName(i.getName());
										pc.pc_trade_shop_buy_list.add(ptsb);					
										break;
									}
								}
							}
						}
					}
				} else if (type.equalsIgnoreCase("t") || type.equalsIgnoreCase("helm") || type.equalsIgnoreCase("armor") || type.equalsIgnoreCase("cloak") ||
						type.equalsIgnoreCase("shield") || type.equalsIgnoreCase("guarder") || type.equalsIgnoreCase("glove") || type.equalsIgnoreCase("boot")) {
					for (Item i : ItemDatabase.getList()) {
						if (i != null && i.getType2().equalsIgnoreCase(type)) {
							for (PcTradeShop pts : getList()) {
								if (pts != null) {
									if (pts.getItemName().contentEquals(i.getName())) {
										PcTradeShopBuy ptsb = new PcTradeShopBuy();
										ptsb.setItemName(i.getName());
										pc.pc_trade_shop_buy_list.add(ptsb);
										break;
									}
								}
							}
						}
					}
				} else if (type.equalsIgnoreCase("acc")) {
					for (Item i : ItemDatabase.getList()) {
						if (i != null && (i.getType2().equalsIgnoreCase("necklace") || i.getType2().equalsIgnoreCase("ring") || 
								i.getType2().equalsIgnoreCase("belt") || i.getType2().equalsIgnoreCase("earring"))) {
							for (PcTradeShop pts : getList()) {
								if (pts != null) {
									if (pts.getItemName() == i.getName()) {
										PcTradeShopBuy ptsb = new PcTradeShopBuy();
										ptsb.setItemName(i.getName());
										pc.pc_trade_shop_buy_list.add(ptsb);
										break;
									}
								}
							}
						}
					}
				} else if (type.equalsIgnoreCase("etc")) {
					for (Item i : ItemDatabase.getList()) {
						if (i != null && (!i.getType1().equalsIgnoreCase("weapon") && !i.getType1().equalsIgnoreCase("armor"))) {
							for (PcTradeShop pts : getList()) {
								if (pts != null) {
									if (pts.getItemName().contentEquals(i.getName())) {
										PcTradeShopBuy ptsb = new PcTradeShopBuy();
										ptsb.setItemName(i.getName());
										pc.pc_trade_shop_buy_list.add(ptsb);
										break;
									}
								}
							}
						}
					}
				}
			
				for (PcTradeShopBuy ptsb : pc.pc_trade_shop_buy_list) {
					if (ptsb != null) {
						for (PcTradeShop pts : getList()) {
							if (pts != null) {
								if (pts.getItemName().contentEquals(ptsb.getItemName())) {
									ptsb.setCount(ptsb.getCount() + pts.getCount());
								}
							}
						}
					}
				}
				
				Collections.sort(pc.pc_trade_shop_buy_list, new PcTradeShopBuyComparator());

				if (pc.pc_trade_shop_buy_list.size() > 0) {
					List<String> htmlList = new ArrayList<String>();
					
					for (PcTradeShopBuy ptsb : pc.pc_trade_shop_buy_list) {
						if (ptsb != null) {
							htmlList.add(String.format("%s (%,d)", ptsb.getItemName(), ptsb.getCount()));
						}
					}
					
					for (int i = 0; i < 250; i++) {
						htmlList.add(" ");
					}
					
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), ExchangeNpc, "tradesys4", null, htmlList));
				} else {
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), ExchangeNpc, "tradesys5"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void buyHtml2(PcInstance pc, int idx) {
		if (!isPcTradeState(pc)) {
			return;
		}

		try {
			if (pc.pc_trade_shop_buy_list != null) {
				synchronized (pc.pc_trade_shop_buy_list) {
					PcTradeShopBuy ptsb = pc.pc_trade_shop_buy_list.get(idx);
				
					if (ptsb != null) {
						List<PcTradeShop> itemList = new ArrayList<PcTradeShop>();
						
						for (PcTradeShop pts : getList()) {
							if (pts != null) {
								if (pts.getItemName().contentEquals(ptsb.getItemName())) {
									itemList.add(pts);
								}
							}
						}
						
						if (itemList.size() > 0) {
							Collections.sort(itemList, new PcTradeShopBuyListComparator());
							
							pc.pc_trade_shop_step = 8;
							ChattingController.toChatting(pc, "            \\fY구매할 아이템의 수량을 선택하십시오.", Lineage.CHATTING_MODE_MESSAGE);
							
//							if (Lineage.pc_trade_shop_buy_tax > 0) {
//								ChattingController.toChatting(pc, String.format("       \\fS구매 가격 %.0f%%의 수수료가 필요합니다.", Lineage.pc_trade_shop_buy_tax * 100), Lineage.CHATTING_MODE_MESSAGE);
//							}
							
							pc.toSender(S_PcTradeShopBuy.clone(BasePacketPooling.getPool(S_PcTradeShopBuy.class), pc, itemList));
						}
					}
				}
			}
		} catch (Exception e) {
			pc.pc_trade_shop_step = 0;
		}
	}
	
	public static void buyHtml3(PcInstance pc, ClientBasePacket cbp) {
		if (!isPcTradeState(pc)) {
			return;
		}

		try {
			int count = cbp.readH();
			if (count > 0 && count <= 100) {
				for (int j = 0; j < count; ++j) {
					try {
						long item_idx = cbp.readD();
						long item_count = cbp.readD();
						if (item_count > 0) {
							PcTradeShop pts = getPcShop(item_idx);

							if (pts != null) {
								long time = System.currentTimeMillis();
								
								if (pts.getDelay() > time) {
									ChattingController.toChatting(pc, String.format("[거래소] '%s' 구매에 실패하였습니다.", getItemString(pts, item_count, false)), Lineage.CHATTING_MODE_MESSAGE);
									continue;
								}
								
								pts.setDelay(time + 100);
								
								Item i = ItemDatabase.find(pts.getItemName());
								
								if (i != null) {
									// 수량이 등록한것보다 많게 요청했을때.
									if (pts.getCount() < item_count) {
										item_count = pts.getCount();
									}
									
									long price = pts.getPrice() * item_count;
									long sellPrice = price;
									
									if (Lineage.pc_trade_shop_buy_tax > 0) {
										price = (long) (price + (price * Lineage.pc_trade_shop_buy_tax));
									}
									
									if (pc.getInventory().isAppend(i, item_count, i.isPiles() ? 1 : item_count)) {
										if ((pts.getPcObjectId() != pc.getObjectId() && pc.getInventory().isAden(pts.getAdenType(), price, false)) || pts.getPcObjectId() == pc.getObjectId()) {
											if (pts.getPcObjectId() != pc.getObjectId()) {
												pc.getInventory().isAden(pts.getAdenType(), price, true);
											}
											
											pts.setCount(pts.getCount() - item_count);

											// 사용자 아이템 추가.
											ItemInstance temp = pc.getInventory().find(i.getItemCode(), i.getName(), pts.getBless(), i.isPiles());
											if (temp == null) {
												// 겹칠수 있는 아이템이 존재하지 않을경우.
												if (i.isPiles()) {
													temp = ItemDatabase.newInstance(i);
													temp.setObjectId(pts.getItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : pts.getItemObjectId());
													temp.setCount(item_count);
													temp.setEnLevel(pts.getEnLevel());
													temp.setDefinite(pts.isDefinite());
													temp.setBless(pts.getBless());
													
													if (i.getName().equalsIgnoreCase("신성한 엘름의 축복")) {
														temp.setDynamicMr(pts.getEnLevel() > 4 ? (pts.getEnLevel() - 4) * i.getEnchantMr() : 0);
													} else {
														temp.setDynamicMr(pts.getEnLevel() * i.getEnchantMr());
													}
													
													temp.setDynamicStunDefence(pts.getEnLevel() * i.getEnchantStunDefense());
													temp.setPetObjectId(pts.getPetObjId());
													temp.set무기속성(pts.get무기속성());

													pc.getInventory().append(temp, true);
												} else {
													for (int k = 0; k < item_count; ++k) {
														temp = ItemDatabase.newInstance(i);
														temp.setObjectId(pts.getItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : pts.getItemObjectId());
														temp.setEnLevel(pts.getEnLevel());
														temp.setDefinite(pts.isDefinite());
														temp.setBless(pts.getBless());
														
														if (i.getName().equalsIgnoreCase("신성한 엘름의 축복")) {
															temp.setDynamicMr(pts.getEnLevel() > 4 ? (pts.getEnLevel() - 4) * i.getEnchantMr() : 0);
														} else {
															temp.setDynamicMr(pts.getEnLevel() * i.getEnchantMr());
														}
														
														temp.setDynamicStunDefence(pts.getEnLevel() * i.getEnchantStunDefense());
														temp.setPetObjectId(pts.getPetObjId());
														temp.set무기속성(pts.get무기속성());

														pc.getInventory().append(temp, true);
													}
												}
											} else {
												// 겹치는 아이템이 존재할 경우.
												pc.getInventory().count(temp, temp.getCount() + item_count, true);
											}

											if (temp instanceof DogCollar) {
												DogCollar dogCollar = (DogCollar) temp;
												Connection con = null;
												try {
													con = DatabaseConnection.getLineage();
													dogCollar.toWorldJoin(con, pc);
												} catch (Exception e) {

												} finally {
													DatabaseConnection.close(con);
												}
											}

											if (pts.getPcObjectId() != pc.getObjectId()) {
												// 아데나 지급.
												PcInstance sellPc = World.findPc(pts.getPcObjectId());
												
												// 판매자가 접속해있을 경우.
												if (sellPc != null) {
//													Item item = ItemDatabase.find(pts.getAdenType());
//													
//													if (item != null) {
//														ItemInstance tempItem = ItemDatabase.newInstance(item);
//														tempItem.setCount(sellPrice);
//														tempItem.setEnLevel(0);
//														tempItem.setBless(1);
//														tempItem.setDefinite(true);
//														// 인벤에 등록처리.
//														sellPc.getInventory().append(tempItem, tempItem.getCount());
//
//														ChattingController.toChatting(sellPc,
//																String.format("[거래소] '%s' 판매대금 %s(%,d) 입금", getItemString(pts, item_count, false), pts.getAdenType(), sellPrice),
//																Lineage.CHATTING_MODE_MESSAGE);
//													}
													
													ChattingController.toChatting(sellPc,
															String.format("[거래소] '%s' 판매완료", getItemString(pts, item_count, false)), Lineage.CHATTING_MODE_MESSAGE);
												} else {
//													// 판매자가 오프라인일 경우.
//													long aden_objectid = getAdenObjectId(pts.getPcObjectId(), pts.getAdenType());
//													if (aden_objectid == 0) {
//														// 등록
//														toInsertAden(pts.getPcObjectId(), sellPrice, pts.getAdenType());
//													} else {
//														// 갱신
//														toUpdateAden(pts.getPcObjectId(), sellPrice, pts.getAdenType());
//													}
												}
												
												insertDBSell(pts, item_count, sellPrice);
												
												ChattingController.toChatting(pc, String.format("[거래소] '%s' 구매 완료", getItemString(pts, item_count, false)), Lineage.CHATTING_MODE_MESSAGE);
												
//												LetterController.toLetter("거래소", pts.getPcName(), "판매완료",
//														String.format("'%s' 아이템이 판매완료되어 %s(%,d) 입금 되었습니다.", getItemString(pts, item_count, false), pts.getAdenType(), sellPrice), 0);
												
												LetterController.toLetter("거래소", pts.getPcName(), "판매완료",
														String.format("'%s' 아이템이 판매완료 되었습니다.", getItemString(pts, item_count, false)), 0);
												
												Log.append거래소_구매(pts, pc, price, item_count);
											} else {
												Log.append거래소_판매종료(pts, item_count);
												ChattingController.toChatting(pc, String.format("[거래소] '%s' 삭제 완료", getItemString(pts, item_count, false)), Lineage.CHATTING_MODE_MESSAGE);
											}
											
											// 디비 갱신.
											if (pts.getCount() < 1) {
												// 삭제.
												remove(pts.getItemObjectId());
												remove(pts);	
											} else {
												// 업데이트.
												changeCount(pts, pts.getCount());
											}
										} else {
											// 0%%s 충분치 않습니다.
											pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 776, pts.getAdenType()));
											break;
										}
									}
								}
							} else {
								ChattingController.toChatting(pc, "[거래소] 아이템 구매에 실패하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			pc.pc_trade_shop_step = 0;
		}

		pc.pc_trade_shop_step = 0;
	}
	
	static private long getAdenObjectId(long objId, String adenType) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM characters_inventory WHERE cha_objId=? AND name=? AND bress=1");
			st.setLong(1, objId);
			st.setString(2, adenType);
			rs = st.executeQuery();
			if (rs.next())
				return rs.getLong("objId");
		} catch (Exception e) {
			lineage.share.System.printf("%s : getAdenObjectId(long objId, String adenType)\r\n", ExchangeController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}

	static private void toInsertAden(long objId, long count, String adenType) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("INSERT INTO characters_inventory SET objId=?, cha_objId=?, name=?, count=?, definite=1");
			st.setLong(1, ServerDatabase.nextItemObjId());
			st.setLong(2, objId);
			st.setString(3, adenType);
			st.setLong(4, count);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : toInsertAden(long objId, long count, String adenType)\r\n", ExchangeController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}

	static private void toUpdateAden(long objId, long count, String adenType) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters_inventory SET count=count+? WHERE cha_objId=? AND name=? AND bress=1");
			st.setLong(1, count);
			st.setLong(2, objId);
			st.setString(3, adenType);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : toUpdateAden(long objId, long count, String adenType)\r\n", ExchangeController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
	}
	
	static private void insertDBSell(PcTradeShop pts, long item_count, long price) {
		if (pts != null) {
			Connection con = null;
			PreparedStatement st = null;
			
			try {
				synchronized (sync_DB) {
					PcTradeShopSell ptss = new PcTradeShopSell();
					ptss.setPcObjId(pts.getPcObjectId());
					ptss.setPcName(pts.getPcName());
					ptss.setItemName(String.format("'%s' %s(%,d)", getItemString(pts, item_count, false), pts.getAdenType(), price));
					ptss.setAdenType(pts.getAdenType());
					ptss.setPrice(price);
					
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("INSERT INTO pc_trade_shop_sell SET pc_objId=?, pc_name=?, item_name=?, aden_type=?, price=?");
					st.setLong(1, ptss.getPcObjId());
					st.setString(2, ptss.getPcName());
					st.setString(3, ptss.getItemName());
					st.setString(4, ptss.getAdenType());
					st.setLong(5, ptss.getPrice());
					st.executeUpdate();
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : insertDBSell(PcTradeShop pts, long item_count, long price)\r\n", ExchangeController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}	
	}
}

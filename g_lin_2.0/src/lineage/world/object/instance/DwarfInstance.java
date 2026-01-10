package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Warehouse;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.WarehouseClanLogDatabase;
import lineage.database.WarehouseDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_WareHouse;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.object.object;

public class DwarfInstance extends object {
	private Npc npc;
	protected Kingdom kingdom;

	public DwarfInstance(Npc npc) {
		this.npc = npc;
	}

	/**
	 * 창고를 이용할 수 있는 레벨인지 확인하는 메서드.
	 */
	static public boolean isLevel(int level) {
		return level >= Lineage.warehouse_level;
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {		
		//자동판매 초기화
		pc.isAutoSellAdding = false;
		pc.isAutoSellDeleting = false;
		
		synchronized (sync_dynamic) {
			int dwarf_type = Lineage.DWARF_TYPE_NONE; // 일반창고
			if (action.indexOf("pledge") > 0)
				dwarf_type = Lineage.DWARF_TYPE_CLAN; // 혈맹창고 
			else if (action.indexOf("elven") > 0)
				dwarf_type = Lineage.DWARF_TYPE_ELF; // 요정창고   미스릴 2 

			int id = dwarf_type == Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			
			// 혈맹 창고 사용못하는 버그 확인
			Clan clan = ClanController.find(pc);
			PcInstance use = null;
			
			if (clan != null) {
				use = World.findPc(clan.getWarehouseObjectId());
				
				if (use == null || !Util.isDistance(use, this, Lineage.SEARCH_LOCATIONRANGE)) {
					clan.setWarehouseObjectId(0L);
				}
			}
		

			if (dwarf_type == Lineage.DWARF_TYPE_CLAN && pc.getClanId() == 0) {
				// \f1창고: 혈맹 창고 이용 불가(혈맹 미가입)
				ChattingController.toChatting(pc, "창고: 혈맹 창고 이용 불가(혈맹 미가입)", Lineage.CHATTING_MODE_MESSAGE);
				//pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 208));
			} else if (dwarf_type == Lineage.DWARF_TYPE_CLAN && pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL && (pc.getTitle() == null || pc.getTitle().length() == 0)) {
				// 호칭을 받지 못한 혈맹원이나 견습 혈맹원은 혈맹창고를 사용할 수 없습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 728));
			} else if (dwarf_type == Lineage.DWARF_TYPE_CLAN && clan.getWarehouseObjectId() > 0L && clan.getWarehouseObjectId() != pc.getObjectId()) {
			//	pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 209)); // 창고 목록 
				if (use != null) {
					ChattingController.toChatting(pc, String.format("'%s' 님이 혈맹 창고를 사용중입니다.", use.getName()), Lineage.CHATTING_MODE_MESSAGE);
				}	
			} else {
				int cnt = WarehouseDatabase.getCount(id, dwarf_type);
				if (cnt == 0) {
					
				   pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "noitemret")); 
				} else {
					if (dwarf_type == Lineage.DWARF_TYPE_CLAN)
						clan.setWarehouseObjectId(pc.getObjectId());

					// 창고 목록 열람.
					List<Warehouse> list = WarehouseDatabase.getList(id, dwarf_type);
					pc.toSender(S_WareHouse.clone(BasePacketPooling.getPool(S_WareHouse.class), this, dwarf_type, list));
					for (Warehouse wh : list)
						WarehouseDatabase.setPool(wh);
					list.clear();
				}
			}
		}
	}

	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {
		if (Lineage.open_wait && pc.getGm() == 0) {
			ChattingController.toChatting(pc, "[오픈 대기] 창고를 이용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		int type = cbp.readC();
		switch (type) {
			case 2: insert(pc, Lineage.DWARF_TYPE_NONE, cbp); break;
			case 3: select(pc, Lineage.DWARF_TYPE_NONE, cbp); break;
			case 4: insert(pc, Lineage.DWARF_TYPE_CLAN, cbp); break;
			case 5:
				Clan clan = ClanController.find(pc);
				if (clan != null && System.currentTimeMillis() > clan.getClanWarehouseTime()) {
					clan.setClanWarehouseTime(System.currentTimeMillis() + 5000);
					select(pc, Lineage.DWARF_TYPE_CLAN, cbp);
				} else {
					ChattingController.toChatting(pc, "5초 후 다시 이용하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
				break;
			case 8: insert(pc, Lineage.DWARF_TYPE_ELF, cbp); break;
			case 9: select(pc, Lineage.DWARF_TYPE_ELF, cbp); break;
		}
	}
  
	// 1. 안전한 DB 업데이트 / 삭제 메서드
	private void applySafeUpdateOrDelete(PreparedStatement st2, long itemCount, long dbCount) throws Exception {
	    int affected = st2.executeUpdate();
	    if (affected == 0) {
	        throw new IllegalStateException("\uD83D\uDEA8 DB 업데이트 실패. 예상 수량 반영되지 않음. 요청: " + itemCount + ", 보유: " + dbCount);
	    }
	    st2.close();
	}

	// 수량 감소 또는 삭제 처리용 메서드
	private boolean updateWarehouseCount(Connection con, int dwarf_type, long itemCount, long dbCount, int db_uid) {
	    PreparedStatement st2 = null;
	    try {
	        if (dbCount - itemCount <= 0) {
	            switch (dwarf_type) {
	                case Lineage.DWARF_TYPE_CLAN:
	                    st2 = con.prepareStatement("DELETE FROM warehouse_clan WHERE uid=? AND count>=?");
	                    break;
	                case Lineage.DWARF_TYPE_ELF:
	                    st2 = con.prepareStatement("DELETE FROM warehouse_elf WHERE uid=? AND count>=?");
	                    break;
	                default:
	                    st2 = con.prepareStatement("DELETE FROM warehouse WHERE uid=? AND count>=?");
	                    break;
	            }
	            st2.setInt(1, db_uid);
	            st2.setLong(2, itemCount);
	        } else {
	            long newCount = dbCount - itemCount;
	            switch (dwarf_type) {
	                case Lineage.DWARF_TYPE_CLAN:
	                    st2 = con.prepareStatement("UPDATE warehouse_clan SET count=? WHERE uid=? AND count>=?");
	                    break;
	                case Lineage.DWARF_TYPE_ELF:
	                    st2 = con.prepareStatement("UPDATE warehouse_elf SET count=? WHERE uid=? AND count>=?");
	                    break;
	                default:
	                    st2 = con.prepareStatement("UPDATE warehouse SET count=? WHERE uid=? AND count>=?");
	                    break;
	            }
	            st2.setLong(1, newCount);
	            st2.setInt(2, db_uid);
	            st2.setLong(3, itemCount);
	        }

	        applySafeUpdateOrDelete(st2, itemCount, dbCount);
	        return true;

	    } catch (Exception e) {
	        System.println("updateWarehouseCount 오류: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    } finally {
	        DatabaseConnection.close(st2);
	    }
	}

	/**
	 * ====================================================
	 * 창고에서 아이템을 찾을 때 사용하는 메서드 (select)
	 * ====================================================
	 */
	private void select(PcInstance pc, int dwarf_type, ClientBasePacket cbp) {
	    Connection con = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    PreparedStatement st2 = null;
	    ResultSet rs2 = null;

	    Clan clan = (dwarf_type == Lineage.DWARF_TYPE_CLAN) ? ClanController.find(pc) : null;

	    if (dwarf_type == Lineage.DWARF_TYPE_CLAN && clan != null && clan.getWarehouseObjectId() != pc.getObjectId()) {
	        ChattingController.toChatting(pc, "[혈맹창고] 잘못된 접근입니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }

	    try {
	        con = DatabaseConnection.getLineage();
	        long count = cbp.readH();
	        int id = (dwarf_type == Lineage.DWARF_TYPE_CLAN) ? pc.getClanId() : pc.getClient().getAccountUid();
	        int w_Count = WarehouseDatabase.getCount(id, dwarf_type);

	        if (count <= 0 || count >= 2_100_000_000 || count > w_Count)
	            return;

	        for (int i = 0; i < count; i++) {
	            long item_id = cbp.readD();
	            long item_count = cbp.readD();

	            String query;
	            switch (dwarf_type) {
	                case Lineage.DWARF_TYPE_CLAN:
	                    query = "SELECT * FROM warehouse_clan WHERE uid=? AND clan_id=?";
	                    break;
	                case Lineage.DWARF_TYPE_ELF:
	                    query = "SELECT * FROM warehouse_elf WHERE uid=? AND account_uid=?";
	                    break;
	                default:
	                    query = "SELECT * FROM warehouse WHERE uid=? AND account_uid=?";
	                    break;
	            }

	            st = con.prepareStatement(query);
	            st.setLong(1, item_id);
	            st.setInt(2, id);
	            rs = st.executeQuery();

	            if (rs.next()) {
	                int db_uid = rs.getInt(1);
	                int db_inv_id = rs.getInt(3);
	                int pet_objid = rs.getInt(4);
	                int letter_id = rs.getInt(5);
	                int item_code = rs.getInt(6);
	                long db_count = rs.getLong(10);
	                int db_quantity = rs.getInt(11);
	                int db_en = rs.getInt(12);
	                boolean db_definite = (rs.getInt(13) == 1);
	                int db_bress = rs.getInt(14);
	                int db_durability = rs.getInt(15);
	                int db_time = rs.getInt(16);
	                int db_enfire = rs.getInt(17);
	                int db_enwater = rs.getInt(18);
	                int db_enwind = rs.getInt(19);
	                int db_enearth = rs.getInt(20);
	                int DolloptionA = rs.getInt(21);
	                int DolloptionB = rs.getInt(22);
	                int DolloptionC = rs.getInt(23);
	                int DolloptionD = rs.getInt(24);
	                int DolloptionE = rs.getInt(25);
	                String itemk = rs.getString(26);

	                ItemInstance temp = ItemDatabase.newInstance(ItemDatabase.find_ItemCode(item_code));

	                if (temp == null) {
	                    writeWarehouseLog(pc, dwarf_type, "비정상(없는 아이템 시도)", "알 수 없음", item_count, db_count, db_count);
	                    continue;
	                }

	                if (item_count <= 0 || item_count > db_count) {
	                    writeWarehouseLog(pc, dwarf_type, "비정상(수량 조작 시도)", temp.getItem().getName(), item_count, db_count, db_count);
	                    continue;
	                }

	                temp.setCount(item_count);
	                temp.setBless(db_bress);

	                boolean aden = (dwarf_type == Lineage.DWARF_TYPE_ELF) ?
	                        pc.getInventory().isMeterial(Lineage.warehouse_price_elf, true) :
	                        pc.getInventory().isAden(Lineage.warehouse_price, true);

	                if (!aden) {
	                    long nowInvCount = pc.getInventory().find(temp) != null ? pc.getInventory().find(temp).getCount() : 0;
	                    writeWarehouseLog(pc, dwarf_type, "실패(아데나/미스릴 부족)", temp.getItem().getName(), item_count, db_count, nowInvCount);
	                    pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), (dwarf_type == Lineage.DWARF_TYPE_ELF) ? 337 : 189, "미스릴"));
	                    ItemDatabase.setPool(temp);
	                    continue;
	                }

	                if (!updateWarehouseCount(con, dwarf_type, item_count, db_count, db_uid)) {
	                    ChattingController.toChatting(pc, "[오류] DB 갱신 실패. 아이템을 지급하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
	                    ItemDatabase.setPool(temp);
	                    continue;
	                }

	                long leftover = db_count - item_count;
	                writeWarehouseLog(pc, dwarf_type, "정상(찾기 완료)", temp.getItem().getName(), item_count, db_count, leftover);

	                ItemInstance temp2 = pc.getInventory().find(temp);
	                if (temp2 == null) {
	                    temp.setObjectId(ServerDatabase.nextItemObjId());
	                    temp.setQuantity(db_quantity);
	                    temp.setEnLevel(db_en);
	                    temp.setDefinite(db_definite);
	                    temp.setDurability(db_durability);
	                    temp.setTime(db_time);
	                    temp.setPetObjectId(pet_objid);
	                    temp.setLetterUid(letter_id);
	                    temp.setEnFire(db_enfire);
	                    temp.setEnWater(db_enwater);
	                    temp.setEnWind(db_enwind);
	                    temp.setEnEarth(db_enearth);
	                    temp.setInvDolloptionA(DolloptionA);
	                    temp.setInvDolloptionB(DolloptionB);
	                    temp.setInvDolloptionC(DolloptionC);
	                    temp.setInvDolloptionD(DolloptionD);
	                    temp.setInvDolloptionE(DolloptionE);
	                    temp.setItemTimek(itemk);
	                    pc.getInventory().append(temp, true);
	                    temp.toWorldJoin(con, pc);

	                    if (dwarf_type == Lineage.DWARF_TYPE_CLAN) {
	                        WarehouseClanLogDatabase.append(pc, temp, item_count, "remove");
	                        if (Lineage.clan_warehouse_message && clan != null) {
	                            String msg = String.format("[혈맹창고] %s 님이 %s 찾음", pc.getName(), temp.toStringDB());
	                            clan.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, msg));
	                        }
	                    }
	                } else {
	                    WarehouseClanLogDatabase.append(pc, temp, item_count, "remove");
	                    pc.getInventory().count(temp2, temp2.getCount() + temp.getCount(), true);
	                    ItemDatabase.setPool(temp);
	                    if (Lineage.clan_warehouse_message && dwarf_type == Lineage.DWARF_TYPE_CLAN && clan != null) {
	                        String msg = String.format("[혈맹창고] %s 님이 %s 찾음", pc.getName(), temp2.toStringDB());
	                        clan.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, msg));
	                    }
	                }

	                String item_name = temp.toStringDB();
	                Log.appendItem(
	                    pc,
	                    (dwarf_type == Lineage.DWARF_TYPE_CLAN) ? "type|혈맹창고찾기" : "type|창고찾기",
	                    String.format("item_name|%s", item_name),
	                    String.format("item_objid|%d", db_inv_id),
	                    String.format("count|%d", item_count)
	                );
	            }

	            rs.close();
	            st.close();
	        }

	    } catch (Exception e) {
	        lineage.share.System.println(DwarfInstance.class.toString() + " : select 메서드 오류");
	        lineage.share.System.println(e);
	        e.printStackTrace();

	    } finally {
	        DatabaseConnection.close(st2, rs2);
	        DatabaseConnection.close(con, st, rs);
	    }

	    if (dwarf_type == Lineage.DWARF_TYPE_CLAN && clan != null && clan.getWarehouseObjectId() == pc.getObjectId()) {
	        clan.setWarehouseObjectId(0L);
	    }
	}

	/**
	 * 창고에 아이템 맡길 때 처리하는 메서드 (로그 통합 버전)
	 */
	private void insert(PcInstance pc, int dwarf_type, ClientBasePacket cbp) {
	    Connection con = null;
	    try {
	        con = DatabaseConnection.getLineage();

	        int uid = (dwarf_type == Lineage.DWARF_TYPE_CLAN) ? pc.getClanId() : pc.getClient().getAccountUid();
	        int count = cbp.readH(); // 유저가 맡기려는 아이템 개수
	        int wCount = WarehouseDatabase.getCount(uid, dwarf_type);
	        boolean isValid = (dwarf_type != Lineage.DWARF_TYPE_CLAN || pc.getClanId() > 0);

	        if (count <= 0 || !isValid || wCount + count > Lineage.warehouse_max)
	            return;

	        for (int i = 0; i < count; i++) {
	            ItemInstance temp = pc.getInventory().value(cbp.readD());
	            if (temp == null || temp.getItem() == null)
	                continue;

	            String itemName = temp.getItem().getName().trim();
	            long requestCount = cbp.readD();

	            if (requestCount <= 0 || requestCount > Common.MAX_COUNT) {
	                ChattingController.toChatting(pc, String.format("[20억 초과 창고 보관 불가] %s", itemName), Lineage.CHATTING_MODE_MESSAGE);
	                writeWarehouseLog(pc, dwarf_type, "비정상(20억 초과)", itemName, requestCount, temp.getCount(), 0);
	                continue;
	            }

	            if (requestCount > temp.getCount()) {
	                writeWarehouseLog(pc, dwarf_type, "비정상(수량 조작 시도)", itemName, requestCount, temp.getCount(), temp.getCount() - requestCount);
	                continue;
	            }

	            if (temp.isEquipped())
	                continue;

	            if (!pc.getInventory().isRemove(temp, requestCount, true, true, true)) {
	                writeWarehouseLog(pc, dwarf_type, "비정상(창고 조작 시도)", itemName, requestCount, temp.getCount(), 0);
	                continue;
	            }

	            boolean allowed = 
	                (dwarf_type == Lineage.DWARF_TYPE_NONE && temp.getItem().isWarehouse()) ||
	                (dwarf_type == Lineage.DWARF_TYPE_CLAN && temp.getItem().isClanWarehouse()) ||
	                (dwarf_type == Lineage.DWARF_TYPE_ELF && temp.getItem().isElfWarehouse());

	            if (!allowed)
	                continue;

	            long inv_id = temp.getItem().isPiles()
	                ? WarehouseDatabase.isPiles(temp.getItem().isPiles(), uid, temp.getItem().getItemCode(), itemName, temp.getBless(), dwarf_type)
	                : 0;

	            if (inv_id > 0) {
	                boolean check = WarehouseDatabase.isCountCheck(pc, uid, temp.getItem().getItemCode(), itemName, temp.getBless(), dwarf_type, requestCount, temp.getCount());
	                if (!check) {
	                    ChattingController.toChatting(pc, String.format("[20억 초과 창고 보관 불가] %s", itemName), Lineage.CHATTING_MODE_MESSAGE);
	                    continue;
	                }

	                WarehouseDatabase.update(temp.getItem().getItemCode(), itemName, temp.getBless(), uid, requestCount, dwarf_type);
	            } else {
	                long insertObjId = (requestCount == temp.getCount()) ? temp.getObjectId() : ServerDatabase.nextItemObjId();
	                WarehouseDatabase.insert(temp, insertObjId, requestCount, uid, dwarf_type);
	            }

	            if (dwarf_type == Lineage.DWARF_TYPE_CLAN) {
	                WarehouseClanLogDatabase.append(pc, temp, requestCount, "append");
	                Log.appendItem(pc, "type|혈맹창고등록",
	                    String.format("item_name|%s", temp.toStringDB()),
	                    String.format("item_objid|%d", temp.getObjectId()),
	                    String.format("count|%d", requestCount),
	                    String.format("target_objid|%d", inv_id));
	            } else {
	                Log.appendItem(pc, "type|창고등록",
	                    String.format("item_name|%s", temp.toStringDB()),
	                    String.format("item_objid|%d", temp.getObjectId()),
	                    String.format("count|%d", requestCount),
	                    String.format("target_objid|%d", inv_id));
	            }

	            long oldCount = temp.getCount();
	            long leftover = oldCount - requestCount;
	            pc.getInventory().count(temp, leftover, true);

	            writeWarehouseLog(pc, dwarf_type, "정상(맡기기 완료)", itemName, requestCount, oldCount, leftover);
	        }

	    } catch (Exception e) {
	        lineage.share.System.println(DwarfInstance.class.toString() + " : insert(PcInstance, int, ClientBasePacket)");
	        lineage.share.System.println(e);
	        e.printStackTrace();

	    } finally {
	        DatabaseConnection.close(con);
	    }
	}

	/**
	 * 통합 로그 출력 메서드
	 */
	private void writeWarehouseLog(
	        final PcInstance pc,
	        final int dwarf_type,
	        final String status,
	        final String itemName,
	        final long requestCount,
	        final long dbOrInvCount,
	        final long leftover
	) {
	    long time = System.currentTimeMillis();
	    String timeString = Util.getLocaleString(time, true);

	    String cleanItemName = itemName.trim();
	    String warehouseType = (dwarf_type == Lineage.DWARF_TYPE_CLAN
	            ? "혈맹 창고"
	            : (dwarf_type == Lineage.DWARF_TYPE_ELF ? "엘프 창고" : "일반 창고"));
	    long safeLeftover = Math.max(0, leftover);

	    String requestLabel, originalLabel, leftoverLabel;
	    if (status.contains("맡기기")) {
	        requestLabel  = "맡기는 수량";
	        originalLabel = "기존 인벤 수량";
	        leftoverLabel = "현재 인벤 수량";
	    } else if (status.contains("찾기")) {
	        requestLabel  = "찾는 수량";
	        originalLabel = "기존 창고 수량";
	        leftoverLabel = "현재 창고 수량";
	    } else {
	        requestLabel  = "요청 수량";
	        originalLabel = "기존 수량";
	        leftoverLabel = "현재 수량";
	    }

	    String logMessage = String.format(
	         "[%s] [%s %s] [캐릭터: %s] [아이템: %s] [%s: %d] [%s: %d] [%s: %d]",
	         timeString,
	         warehouseType,
	         status,
	         pc.getName(),
	         cleanItemName,
	         requestLabel, requestCount,
	         originalLabel, dbOrInvCount,
	         leftoverLabel, safeLeftover
	    );

	    GuiMain.display.asyncExec(() -> {
	        GuiMain.getViewComposite().getWarehouseComposite().toLog(logMessage);
	    });
	}
}
package lineage.world.object.npc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.PcShop;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.controller.PcMarketController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcShopInstance;
import lineage.world.object.item.DogCollar;

public class ShopManagement extends object {

    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "market"));
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        PcShopInstance pc_shop = PcMarketController.shop_list.get(pc.getObjectId());
        if (pc_shop == null) {
            pc_shop = new PcShopInstance(pc.getObjectId(), pc.getName(), pc.getClassType(), pc.getClassSex());
            PcMarketController.shop_list.put(pc.getObjectId(), pc_shop);
        }

        if (action.equalsIgnoreCase("0")) {
            if (pc != null && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && pc.getInventory() != null) {
                if (LocationController.isTeleportZone(pc, true, true) && World.isSafetyZone(pc.getX(), pc.getY(), pc.getMap())) {
                    boolean teleported = false;

                    if ((pc.getX() < 33536 && pc.getX() > 33280 && pc.getY() < 33023 && pc.getY() > 32511) ||
                        (pc.getX() < 33920 && pc.getX() > 33536 && pc.getY() < 33023 && pc.getY() > 32575)) {
                        // 기란마을 체크
                        pc.toPotal(32698, 32843, 350);
                        teleported = true;
                    } else if ((pc.getX() < 33920 && pc.getX() > 33856 && pc.getY() < 32575 && pc.getY() > 32191) ||
                               (pc.getX() < 34304 && pc.getX() > 33920 && pc.getY() < 32739 && pc.getY() > 32127)) {
                        // 오렌마을 체크
                        pc.toPotal(32730, 32798, 360);
                        teleported = true;
                    } else if ((pc.getX() < 33088 && pc.getX() > 32960 && pc.getY() < 33087 && pc.getY() > 33023) ||
                               (pc.getX() < 33280 && pc.getX() > 32959 && pc.getY() < 33535 && pc.getY() > 33087)) {
                        // 은기사마을 체크
                        pc.toPotal(32732, 32793, 370);
                        teleported = true;
                    } else if ((pc.getX() < 32960 && pc.getX() > 32512 && pc.getY() < 33023 && pc.getY() > 32537)) {
                        // 글루딘마을 체크
                        pc.toPotal(32790, 32819, 340);
                        teleported = true;
                    }

                    if (teleported) {
                        pc.PcMarket_Step = 5;
                    } else {
                        ChattingController.toChatting(pc, "시장 이동이 불가능한 마을입니다.", Lineage.CHATTING_MODE_MESSAGE);
                    }
                } else {
                    ChattingController.toChatting(pc, "현재 위치에서는 시장으로 이동할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
                
                }
            }
        }

        if (action.equalsIgnoreCase("1")) {
            // 버그확인.
            if (pc.getMap() != Lineage.market_map1 && pc.getMap() != Lineage.market_map2 && pc.getMap() != Lineage.market_map3 && pc.getMap() != Lineage.market_map4) {
                ChattingController.toChatting(pc, "\\fR상점은 시장에서만 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
                return;
            }
            if (pc_shop.getX() == 0 || pc_shop.getY() == 0)
                PcMarketController.insertShopRobot(pc, pc_shop);

            // 사용자 샵 객체 스폰시키기.
            pc_shop.setHeading(pc.getHeading());
            pc_shop.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
            PcMarketController.updateShopRobot(pc_shop);
            ChattingController.toChatting(pc, "\\fR상점이 시작되었습니다. 아이템 등록 해주세요", Lineage.CHATTING_MODE_MESSAGE);
        }

        // 아이템/가격설정 등록
        if (action.equalsIgnoreCase("2")) {
            boolean check = false;
            for (object o : pc.getInsideList()) {
                if (o instanceof PcShopInstance && pc.getObjectId() == ((PcShopInstance) o).getPc_objectId()) {
                    check = true;
                    break;
                }
            }
            if (check) {
                pc.PcMarket_Step = 1;

                ChattingController.toChatting(pc, "판매할 아이템의 [판매가격, 판매수량]을 설정해주세요.", 20);
                ChattingController.toChatting(pc, "ex) 10000 1", 20);
            } else {
                ChattingController.toChatting(pc, "\\fR상점 시작 상태에서 가능합니다.", 20);
            }
        }

        // 등록된 아이템 검색
        if (action.equalsIgnoreCase("3")) {
            pc.PcMarket_Step = 4;
            ChattingController.toChatting(pc, "검색할 아이템명을 입력해주세요.", 20);
        }

        // 홍보 문구 등록
        if (action.equalsIgnoreCase("4")) {
            pc.PcMarket_Step = 3;
            ChattingController.toChatting(pc, "홍보 문구를 입력해주세요.", 20);
        }

        // 개인노점 철수하기
        if (action.equalsIgnoreCase("5")) {
            if (pc.getMap() != Lineage.market_map1 && pc.getMap() != Lineage.market_map2 && pc.getMap() != Lineage.market_map3 && pc.getMap() != Lineage.market_map4) {
                ChattingController.toChatting(pc, "\\fR상점은 시장에서만 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
                return;
            }
            boolean check2 = false;
            for (object o : pc.getInsideList()) {
                if (o instanceof PcShopInstance && pc.getObjectId() == ((PcShopInstance) o).getPc_objectId()) {
                    check2 = true;
                    break;
                }
            }
            if (check2) {
                if (pc_shop.getListSize() > 0) {
                    for (PcShop s : pc_shop.getShopList().values()) {
                        if (s.getItem() == null)
                            continue;
                        Item i = ItemDatabase.find(s.getItem().getName());
                        if (i != null) {
                            ItemInstance temp = pc.getInventory().find(i.getItemCode(), i.getName(), s.getInvItemBress(), i.isPiles());

                            if (temp != null && (temp.getBless() != s.getInvItemBress() || temp.getEnLevel() != s.getInvItemEn()))
                                temp = null;

                            if (temp == null) {
                                // 겹칠수 있는 아이템이 존재하지 않을경우.
                                if (i.isPiles()) {
                                    temp = ItemDatabase.newInstance(i);
                                    temp.setObjectId(s.getInvItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : s.getInvItemObjectId());
                                    temp.setBless(s.getInvItemBress());
                                    temp.setEnLevel(s.getInvItemEn());
                                    temp.setCount(s.getInvItemCount());
                                    temp.setEnFire(s.getInvItemEnFire());
                                    temp.setEnWater(s.getInvItemEnWater());
                                    temp.setEnWind(s.getInvItemEnWind());
                                    temp.setEnEarth(s.getInvItemEnEarth());
                                    temp.setInvDolloptionA(s.getInvDolloptionA());
                                    temp.setInvDolloptionB(s.getInvDolloptionB());
                                    temp.setInvDolloptionC(s.getInvDolloptionC());
                                    temp.setInvDolloptionD(s.getInvDolloptionD());
                                    temp.setInvDolloptionE(s.getInvDolloptionE());
                                    temp.setItemTimek(s.getInvItemK());
                                    temp.setPetObjectId(s.getPetObjId());
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

                                    temp.setDefinite(true);
                                    pc.getInventory().append(temp, true);
                                } else {
                                    for (int idx = 0; idx < s.getInvItemCount(); idx++) {
                                        temp = ItemDatabase.newInstance(i);
                                        temp.setObjectId(s.getInvItemObjectId() == 0 ? ServerDatabase.nextItemObjId() : s.getInvItemObjectId());
                                        temp.setBless(s.getInvItemBress());
                                        temp.setEnLevel(s.getInvItemEn());
                                        temp.setCount(s.getInvItemCount());
                                        temp.setEnFire(s.getInvItemEnFire());
                                        temp.setEnWater(s.getInvItemEnWater());
                                        temp.setEnWind(s.getInvItemEnWind());
                                        temp.setEnEarth(s.getInvItemEnEarth());
                                        temp.setInvDolloptionA(s.getInvDolloptionA());
                                        temp.setInvDolloptionB(s.getInvDolloptionB());
                                        temp.setInvDolloptionC(s.getInvDolloptionC());
                                        temp.setInvDolloptionD(s.getInvDolloptionD());
                                        temp.setInvDolloptionE(s.getInvDolloptionE());
                                        temp.setItemTimek(s.getInvItemK());
                                        temp.setPetObjectId(s.getPetObjId());
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

                                        temp.setDefinite(true);
                                        pc.getInventory().append(temp, true);
                                    }
                                }
                            } else {
                                // 겹치는 아이템이 존재할 경우.
                                pc.getInventory().count(temp, temp.getCount() + s.getInvItemCount(), true);
                            }

                            PcMarketController.deleteItem(s);
                        }
                    }
                    pc_shop.clearShopList();
                }
                pc_shop.close();
                PcMarketController.removeShop(pc.getObjectId());
                PcMarketController.deleteShopRobot(pc);

                ChattingController.toChatting(pc, "\\fR상점이 종료되었습니다.", Lineage.CHATTING_MODE_MESSAGE);

            } else {
                ChattingController.toChatting(pc, "\\fR 개설중인 상점이 없습니다.", 20);
            }
        }
        // 시세 검색
        if (action.equalsIgnoreCase("7")) {
            List<String> shopList1 = new ArrayList<String>();
            shopList1.clear();
            if (pc_shop != null) {
                if (pc_shop.getShopList().size() < 1) {
                    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), pc, "pcShopItem"));
                    return;
                }
                int idx = 1;

                for (PcShop s : pc_shop.getShopList().values()) {
                    if (s.getItem() == null) {
                        continue;
                    }
                    shopList1.add(String.format("%d. %s", idx++, Util.getItemNameToString(s.getItem().getName(), s.getInvItemBress(), s.getInvItemEn(), s.getInvItemCount())));
                    shopList1.add(String.format("가격: %s %s", Util.changePrice(s.getPrice()), s.getAdenType()));
                    shopList1.add(String.format(" "));
                }

                if (shopList1.isEmpty()) {
                    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), pc, "pcShopItem"));
                } else {
                    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), pc, "pcShopList", null, shopList1));
                }
            }
        }
    }
}
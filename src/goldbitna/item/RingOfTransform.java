package goldbitna.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lineage.bean.database.Poly;
import lineage.bean.lineage.BuffInterface;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Ext_BuffTime;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectPolyIcon;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.BuffFight_04;
import lineage.world.object.magic.ShapeChange;

public class RingOfTransform extends ItemInstance {

    private final List<String> baseTransformList = Arrays.asList("마커스", "가드리아", "진게렝", "아툰", "기르타스", "신화변신");
    private List<String> polylist = new ArrayList<>();

    static synchronized public ItemInstance clone(ItemInstance item) {
        return (item == null) ? new RingOfTransform() : item;
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp) {
        ItemInstance item = cha.getInventory().find("변신 지배 반지");
        ItemInstance item1 = cha.getInventory().find("변신 지배 반지(3일)");

        if (item != null || item1 != null) {
            polylist.clear();

            // 현재 변신 상태 반영
            String currentTransform = cha.getQuickPolymorph1();
            polylist.add(currentTransform);  // 첫 번째 위치에 현재 변신 상태를 추가
            polylist.addAll(baseTransformList); // 이후 나머지 변신 추가

            // HTML 파일 선택
            String htmlFile;
            switch (cha.getClassType()) {
                case Lineage.LINEAGE_CLASS_ROYAL:
                    htmlFile = (cha.getClassSex() == 0) ? "polylist1" : "polylist2";
                    break;
                case Lineage.LINEAGE_CLASS_KNIGHT:
                    htmlFile = (cha.getClassSex() == 0) ? "polylist3" : "polylist4";
                    break;
                case Lineage.LINEAGE_CLASS_ELF:
                    htmlFile = (cha.getClassSex() == 0) ? "polylist5" : "polylist6";
                    break;
                case Lineage.LINEAGE_CLASS_WIZARD:
                    htmlFile = (cha.getClassSex() == 0) ? "polylist7" : "polylist8";
                    break;
                default:
                    htmlFile = "polylist";
                    break;
            }
            cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, htmlFile, null, polylist));
        }
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        List<String> actionKeywords = Arrays.asList("polylist1-0", "polylist1-1", "polylist1-2", "polylist1-3", "polylist1-4", "polylist1-5", "polylist1-6");

        if (actionKeywords.contains(action)) {
            int actionIndex = actionKeywords.indexOf(action);

            // polylist에서 해당 인덱스의 변신 이름 가져오기
            if (actionIndex < polylist.size()) {
                String poly = polylist.get(actionIndex);

                // 빠른변신(현재 변신) 처리
                if (actionIndex == 0) {
                    poly = pc.getQuickPolymorph1();
                }

                pc.setQuickPolymorph1(poly); // 선택한 변신을 QuickPolymorph1에 등록

                // 변신 실행
                transform(pc, poly);
            } else {
                ChattingController.toChatting(pc, "변신이 올바르게 선택되지 않았습니다.", Lineage.CHATTING_MODE_MESSAGE);
            }
        }
    }

    private void transform(PcInstance pc, String poly) {
        if (pc.isDead() || pc.isWorldDelete() || pc.getInventory() == null) return;
        if (pc.getMap() == 807 || pc.getMap() == 5143) {
            ChattingController.toChatting(pc, "해당 지역에서는 변신이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
            return;
        }
        if (World.isTeamBattleMap(pc) || pc.isFishing()) {
            ChattingController.toChatting(pc, "현재 상태에서는 변신할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
            return;
        }

        // 변신 제한 체크
        BuffInterface temp = BuffController.find(pc, SkillDatabase.find(208));
        if (temp != null && temp.getTime() == -1) {
            ChattingController.toChatting(pc, "세트 아이템 착용 중에는 변신할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
            return;
        }

        // 변신 이름 설정
        String polyName = getPolyName(pc, poly);
        Poly p = PolyDatabase.getName(polyName);
        if (p == null) return;

        // 장비 해제 후 변신
        PolyDatabase.toEquipped(pc, p);
        pc.setGfx(p.getGfxId());

        // 무기 속도에 따른 그래픽 모드 설정
        if (Lineage.is_weapon_speed) {
            if (!pc.checkSpear()) {
                ItemInstance weapon = pc.getInventory().getSlot(Lineage.SLOT_WEAPON);

                if (weapon != null && weapon.getItem() != null &&
                    SpriteFrameDatabase.findGfxMode(pc.getGfx(), weapon.getItem().getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
                    pc.setGfxMode(weapon.getItem().getGfxMode());
                } else {
                    pc.setGfxMode(p.getGfxMode());
                }
            }
        } else {
            pc.setGfxMode(p.getGfxMode());
        }

        // 변신 적용
        pc.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), pc), true);
        if (Lineage.server_version > 182) {
            pc.toSender(S_ObjectPolyIcon.clone(BasePacketPooling.getPool(S_ObjectPolyIcon.class), getItem().getDuration()));
        }

        // 버프 적용
        BuffController.append(pc, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), getItem().getDuration()));
        BuffFight_04.onBuff(pc, SkillDatabase.find(602));
        pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 6082), true);

        // HTML 초기화
        pc.toSender(new S_Html(this, "", null, null));
    }


    private String getPolyName(PcInstance pc, String poly) {
        if (poly.equals("신화변신")) {
            switch (pc.getClassType()) {
                case Lineage.LINEAGE_CLASS_ROYAL: return pc.getClassSex() == 0 ? "왕자 신화 변신" : "공주 신화 변신";
                case Lineage.LINEAGE_CLASS_KNIGHT: return pc.getClassSex() == 0 ? "남기사 신화 변신" : "여기사 신화 변신";
                case Lineage.LINEAGE_CLASS_ELF: return pc.getClassSex() == 0 ? "남요정 신화 변신" : "여요정 신화 변신";
                case Lineage.LINEAGE_CLASS_WIZARD: return pc.getClassSex() == 0 ? "남법사 신화 변신" : "여법사 신화 변신";
                case Lineage.LINEAGE_CLASS_DARKELF: return pc.getClassSex() == 0 ? "남다엘 신화 변신" : "여다엘 신화 변신";
            }
        }
        return poly;
    }
}

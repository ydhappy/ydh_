package goldbitna.item;

import java.util.ArrayList;
import java.util.List;

import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class PetAdoptionDocument extends ItemInstance {

    // 펫 이름 목록 (toClick, toTalk 모두에서 사용)
    private List<String> petList = new ArrayList<String>();

    static synchronized public ItemInstance clone(ItemInstance item) {
        return (item == null) ? new PetAdoptionDocument() : item;
    }

    public PetAdoptionDocument() {
        // 초기 펫 목록 구성 (최초 클릭 시에도 사용)
        petList.clear();
        petList.add("도베르만");
        petList.add("세퍼드");
        petList.add("비글");
        petList.add("늑대");
        petList.add("세인트 버나드");
        petList.add("아기 진돗개");
        petList.add("아기 캥거루");
        petList.add("아기 판다곰");
        petList.add("허스키");
        petList.add("호랑이");
        petList.add("열혈토끼");
        petList.add("여우");
        petList.add("곰");
        petList.add("콜리");
        petList.add("해츨링 수컷");
        petList.add("해츨링 암컷");
        petList.add("고양이");        
        petList.add("라쿤");
    }

    @Override
    public void toClick(Character cha, ClientBasePacket cbp) {
        // 플레이어 인벤토리에서 "펫 분양 계약서"를 찾아 HTML 창으로 분양 가능 목록 전송
        ItemInstance item = cha.getInventory().find("펫 분양 계약서");
        if(item != null) {        	
            // HTML 파일명 "petlist"를 이용하여 목록을 보여줌
            cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "petlist", null, petList));
        }
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        // 내부 클래스: 각 펫의 정보를 저장
        class PetInfo {
            String petName;
            int petLevel;
            int petHp;
            int petMp;
            String documentName;
        }
        PetInfo info = null;

        // 서버 버전 144 이상일 경우에만 처리 (기본적으로 분양 기능 적용)
        if(Lineage.server_version > 144) {
            info = new PetInfo();
            info.documentName = "펫 분양 계약서";
            switch(action) {
                case "pet01":
                    info.petName = petList.get(0);
                    info.petLevel = 10;
                    break;
                case "pet02":
                    info.petName = petList.get(1);
                    info.petLevel = 6;
                    break;
                case "pet03":
                    info.petName = petList.get(2);
                    info.petLevel = 6;
                    break;
                case "pet04":
                    info.petName = petList.get(3);
                    info.petLevel = 6;
                    break;
                case "pet05":
                    info.petName = petList.get(4);
                    info.petLevel = 6;
                    break;
                case "pet06":
                    info.petName = petList.get(5);
                    info.petLevel = 6;
                    break;
                case "pet07":
                    info.petName = petList.get(6);
                    info.petLevel = 6;
                    break;
                case "pet08":
                    info.petName = petList.get(7);
                    info.petLevel = 6;
                    break;
                case "pet09":
                    info.petName = petList.get(8);
                    info.petLevel = 6;
                    break;
                case "pet10":
                    info.petName = petList.get(9);
                    info.petLevel = 6;
                    break;
                case "pet11":
                    info.petName = petList.get(10);
                    info.petLevel = 6;
                    break;
                case "pet12":
                    info.petName = petList.get(11);
                    info.petLevel = 6;
                    break;
                case "pet13":
                    info.petName = petList.get(12);
                    info.petLevel = 6;
                    break;
                case "pet14":
                    info.petName = petList.get(13);
                    info.petLevel = 6;
                    break;
                case "pet15":
                    info.petName = petList.get(14);
                    info.petLevel = 6;
                    break;
                case "pet16":
                    info.petName = petList.get(15);
                    info.petLevel = 6;
                    break;
                case "pet17":
                    info.petName = petList.get(16);
                    info.petLevel = 6;
                    break;
                case "pet18":
                    info.petName = petList.get(17);
                    info.petLevel = 6;
                    break;    
                default:
                    break;
            }
            // 모든 케이스에 대해 공통 HP, MP 계산
            info.petHp = 50 + Util.random(4, 10);
            info.petMp = 30 + Util.random(1, 5);
        }
        if(info == null || info.petName == null)
            return;

        // 펫 분양 계약서 아이템이 충분한지 확인
        if(pc.getInventory().find(info.documentName) == null) {
            ChattingController.toChatting(pc, info.documentName + "이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
            // HTML 창 닫기 (빈 HTML 전송)
            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
            return;
        }

        // 생성할 펫의 MonsterInstance 생성 및 속성 설정
        MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find(info.petName));
        if(mi == null) {
            ChattingController.toChatting(pc, "펫 인스턴스 생성에 실패했습니다.", Lineage.CHATTING_MODE_MESSAGE);
            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
            return;
        }
        mi.setLevel(info.petLevel);
        mi.setMaxHp(info.petHp);
        mi.setMaxMp(info.petMp);
        mi.setNowHp(info.petHp);
        mi.setNowMp(info.petMp);
        mi.setX(pc.getX());
        mi.setY(pc.getY());
        mi.setMap(pc.getMap());

        // 펫 소환 시도
        if(SummonController.toPet(pc, mi)) {
            // 펫 소환에 성공하면 인벤토리에서 펫 분양 계약서를 소모
            pc.getInventory().count(this, getCount() - 1, true);
            MonsterSpawnlistDatabase.setPool(mi);
            // HTML 창 닫기: 빈 HTML 전송하여 클라이언트 창을 닫음
            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
            // 분양 계약 완료 메시지 출력
            ChattingController.toChatting(pc, String.format("펫 분양 계약이 완료되었습니다. \\fV[펫 이름 : %s]", info.petName), Lineage.CHATTING_MODE_MESSAGE);
        }
    }
}

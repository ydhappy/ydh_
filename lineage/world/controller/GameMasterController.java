package lineage.world.controller;

import goldbitna.telegram.TeleBotServer;
import goldbitna.SetGameMaster;
import lineage.database.SkillDatabase;
import lineage.share.Admin;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.Wafer;

public class GameMasterController {

    private static void processGmSetting(Character cha, int accLv, String action) {
        if (cha == null) {
            System.out.println("** ERROR ** cha = NULL");
            return;
        }

        // GM 권한을 설정 또는 해제
        cha.setGm(Math.max(cha.getGm(), 0)); // 권한이 0 미만으로 내려가지 않도록 처리
        String actionMessage = action.equals("SET") 
            ? String.format("[알림] 운영자 권한 설정  [대상:%s, ACCESS LEVEL:%d]", cha.getName(), cha.getGm())
            : String.format("[알림] 운영자 권한 해제  [대상:%s, ACCESS LEVEL:%d]", cha.getName(), cha.getGm());

        // 채팅 메시지와 시스템 로그 출력
        ChattingController.toChatting(cha, actionMessage, Lineage.CHATTING_MODE_MESSAGE);
        System.out.println(actionMessage);
        if (Admin.tele_enable) { 
            TeleBotServer.myTeleBot.sendText(null, String.format("운영자 %s 완료 : [%s] / ACCESS LEVEL : [%d]", 
                action.equals("SET") ? "설정" : "해제", cha.getName(), cha.getGm()));
        }
    }

    static public void SetAdmin(Character cha, int accLv) {
        if (cha != null) {
            // 운영자 리스트가 비어 있으면 기본 운영자 추가
            if (Admin.gmList.isEmpty()) {
                Admin.gmList.add(new SetGameMaster("메티스", 99));
            }

            // GM 권한 부여 처리
            if (accLv > 0) {
                cha.setGm(accLv); // GM 권한 부여

                // 운영자 리스트에 추가 (중복 체크)
                SetGameMaster existingGM = SetGameMaster.findGMByName(Admin.gmList, cha.getName());
                if (existingGM == null) {
                    Admin.gmList.add(new SetGameMaster(cha.getName(), accLv));
                } else {
                    existingGM.setAccessLevel(accLv);
                }

                processGmSetting(cha, accLv, "SET");

                // 운영자 버프 적용
                if (cha.getGm() > 0) {
                    BuffController.append(cha, Haste.clone(BuffController.getPool(Haste.class), SkillDatabase.find(43), -1, false));
                    if (cha.getClassType() == Lineage.LINEAGE_CLASS_ROYAL || cha.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT) {
                        BuffController.append(cha, Bravery.clone(BuffController.getPool(Bravery.class), SkillDatabase.find(201), -1));
                    }
                    if (cha.getClassType() == Lineage.LINEAGE_CLASS_ELF) {
                        BuffController.append(cha, Wafer.clone(BuffController.getPool(Wafer.class), SkillDatabase.find(200), -1, false));
                    }
                    if (cha.getClassType() == Lineage.LINEAGE_CLASS_WIZARD) {
                        BuffController.append(cha, HolyWalk.clone(BuffController.getPool(HolyWalk.class), SkillDatabase.find(7, 3), -1));
                    }
                    ChattingController.toChatting(cha, "[알림] 운영자 버프가 적용되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
                }
            } else {
                cha.setGm(Math.max(cha.getGm(), 0));
                String errorMessage = String.format("** ERROR ** NAME [%s] or ACCESS LEVEL : [%d]", cha.getName(), accLv);
                ChattingController.toChatting(cha, errorMessage, Lineage.CHATTING_MODE_MESSAGE);
                if (Admin.tele_enable) { 
                    TeleBotServer.myTeleBot.sendText(null, errorMessage);
                }
                System.out.println(errorMessage);
            }
        } else {
            System.out.println("** ERROR ** cha = NULL");
        }
    }

    static public void RstAdmin(Character cha, int accLv) {
        if (cha != null) {
            if (cha.getGm() == 0) {
                if (Admin.tele_enable) { 
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 해당 캐릭터는 운영자가 아닙니다.");
                }
                System.out.println("** ERROR ** 해당 캐릭터는 운영자가 아닙니다.");
                return;
            }

            if (cha.getGm() > accLv) {
                cha.setGm(accLv);

                // 운영자 리스트에서 제거
                SetGameMaster existingGM = SetGameMaster.findGMByName(Admin.gmList, cha.getName());
                if (existingGM != null) {
                    Admin.gmList.remove(existingGM);
                }

                processGmSetting(cha, accLv, "RST");
            }
        } else {
            System.out.println("** ERROR ** cha = NULL");
        }
    }

    static public void checkAndSetGm(Character cha) {
        if (cha != null) {
            String characterName = cha.getName();

            // GM 목록에 존재하는지 확인
            SetGameMaster gm = SetGameMaster.findGMByName(Admin.gmList, characterName);
            if (gm == null) {
                cha.setGm(0);
            } else {
                cha.setGm(gm.getAccessLevel());
            }
        } else {
            System.out.println("** ERROR ** cha = NULL");
        }
    }
}

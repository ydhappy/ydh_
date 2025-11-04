package lineage.share;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import goldbitna.SetGameMaster;

public final class Admin {

    static public boolean is_load = false;

    // 운영자 설정 (GM 리스트)
    static public List<SetGameMaster> gmList = new ArrayList<>();
    static public int access_level = 99; // 기본 접근 레벨 (GM 목록에 없을 경우 사용)

    // Telegram Server Manager (기본값 false)
    static public String bot_token = null;
    static public Long user_Id = null;
    static public boolean tele_enable = false;

    private int gm = access_level;

    public int getGm() {
        return gm;
    }

    public void setGm(int gm) {
        this.gm = gm;
    }

    /**
     * GM 설정에 사용되는 정보 변수 초기화 함수.
     */
    static public void init() {
        is_load = true;
        TimeLine.start("Admin..");

        // 기존 GM 리스트 초기화
        gmList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("admin.conf"))) {            
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || isBlank(line)) // 빈 줄 스킵
                    continue;

                int pos = line.indexOf("=");
                if (pos > 0) {
                    String key = line.substring(0, pos).trim();
                    String value = line.substring(pos + 1).trim();

                    switch (key.toLowerCase()) {
                        case "is_gm_character_name":
                            toGameMasterList(gmList, value, access_level);
                            break;
                        case "access_level":
                            access_level = parseIntSafe(value, 99);
                            break;
                        case "bot_token":
                            bot_token = isBlank(value) ? null : value; // NPE 방지
                            break;
                        case "user_id":
                            user_Id = parseLongSafe(value, null); // 숫자가 아니면 null
                            break;
                        case "tele_enable":
                            tele_enable = value.equalsIgnoreCase("true"); // 기본 false 유지
                            break;
                        default:
                            lineage.share.System.printf("알 수 없는 설정 키: %s\n", key);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            lineage.share.System.printf("%s : init()\n", Admin.class.toString());
            lineage.share.System.println("에러 발생: " + e.getMessage());
            lineage.share.System.println(e);
        }

        // GM 설정이 비어 있으면 기본 GM 추가
        if (gmList.isEmpty()) {
            gmList.add(new SetGameMaster("메티스", 99));
        }

        is_load = false; // 예외 발생 여부와 관계없이 `is_load`는 false로 변경
        TimeLine.end();
    }

    /**
     * GM 캐릭터 리스트에 추가하는 함수.
     * (이름이 비어 있으면 "메티스"로 설정하고, 접근 레벨이 없으면 기본값을 사용)
     */
    static private void toGameMasterList(List<SetGameMaster> list, String value, int defaultAccessLevel) {
        if (!isBlank(value)) {
            StringTokenizer st = new StringTokenizer(value, ",");
            Set<String> existingNames = new HashSet<>(); // 중복 체크용 Set

            // 기존 GM 목록의 이름을 Set에 저장
            for (SetGameMaster gm : list) {
                existingNames.add(gm.getName().toLowerCase()); // 대소문자 구분 없이 중복 확인
            }

            while (st.hasMoreTokens()) {
                try {
                    String gmEntry = st.nextToken().trim();
                    if (isBlank(gmEntry)) {
                        continue;
                    }

                    // GM 이름 및 (access_level) 파싱
                    int startIdx = gmEntry.indexOf("(");
                    int endIdx = gmEntry.indexOf(")");
                    String name;
                    int accessLevel = defaultAccessLevel; // 기본 접근 레벨 사용

                    if (startIdx > 0 && endIdx > startIdx) {
                        name = gmEntry.substring(0, startIdx).trim();
                        String levelStr = gmEntry.substring(startIdx + 1, endIdx).trim();
                        accessLevel = parseIntSafe(levelStr, defaultAccessLevel);
                    } else {
                        name = gmEntry; // 괄호가 없는 경우 기본 access_level 사용
                    }

                    if (isBlank(name)) { 
                        name = "메티스"; // 이름이 비어 있으면 기본값 설정
                    }

                    // 중복 체크 후 추가
                    if (!existingNames.contains(name.toLowerCase())) {
                        list.add(new SetGameMaster(name, accessLevel));
                        existingNames.add(name.toLowerCase()); // Set에 추가하여 중복 방지
                    } else {
//                        lineage.share.System.printf("[중복 경고] GM '%s'이(가) 이미 존재합니다. 중복 추가를 방지합니다.\n", name);
                    }

                } catch (Exception e) {
                    lineage.share.System.printf("%s : toGameMasterList()\n", Admin.class.toString());
                    lineage.share.System.println(e);
                    break;
                }
            }
        } else {
            // GM 설정이 없을 경우 기본값 추가
            list.add(new SetGameMaster("메티스", 99));
        }
    }

    /**
     * 문자열이 `null`이거나 공백인지 확인하는 메서드
     */
    static private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열을 안전하게 정수로 변환하는 유틸리티 함수.
     */
    static private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            lineage.share.System.printf("정수 변환 오류: %s -> 기본값 %d 사용\n", value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 문자열을 안전하게 Long 타입으로 변환하는 유틸리티 함수.
     */
    static private Long parseLongSafe(String value, Long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            lineage.share.System.printf("Long 변환 오류: %s -> 기본값 %s 사용\n", value, defaultValue);
            return defaultValue;
        }
    }
}

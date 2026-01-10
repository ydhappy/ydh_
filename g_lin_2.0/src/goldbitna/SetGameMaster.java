package goldbitna;

import java.util.List;

public class SetGameMaster {
    private String name;
    private int accessLevel;

    public SetGameMaster(String name, int accessLevel) {
        // 이름이 null이거나 빈 값이면 기본값 설정
        this.name = (name == null || name.trim().isEmpty()) ? "메티스" : name;
        // 접근 레벨이 0 이하이면 기본값 99 적용
        this.accessLevel = (accessLevel > 0) ? accessLevel : 99;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? "메티스" : name;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = (accessLevel > 0) ? accessLevel : 99;
    }

    @Override
    public String toString() {
        return String.format("SetGameMaster{name='%s', accessLevel=%d}", name, accessLevel);
    }

    /**
     * GM 목록에서 특정 GM을 찾는 메서드 (이름 기준)
     */
    public static SetGameMaster findGMByName(List<SetGameMaster> gmList, String name) {
        if (name == null) return null;
        for (SetGameMaster gm : gmList) {
            if (gm.getName().equalsIgnoreCase(name)) {
                return gm;
            }
        }
        return null;
    }
}

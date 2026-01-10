package lineage.bean.database;

import java.util.*;

public class RobotMent {
    private int uid;
    private int type;
    private String ment;

    private static final Map<String, Integer> typeMapping = new HashMap<>();
    private static final Map<Integer, String> reverseTypeMapping = new HashMap<>();

    static {
        typeMapping.put("공격하다", 0);
        typeMapping.put("공격받다", 1);
        typeMapping.put("스킬사용", 2);
        typeMapping.put("스킬피격", 3);
        typeMapping.put("도망", 4);
        typeMapping.put("죽다", 5);
        typeMapping.put("죽임", 6);
        typeMapping.put("아이템", 7);
        typeMapping.put("공성", 8);
        typeMapping.put("수성", 9);
        typeMapping.put("마을", 10);
        typeMapping.put("조우", 11);
        typeMapping.put("앱솔", 12);
        typeMapping.put("캔슬", 13);
        typeMapping.put("이뮨", 14);
        typeMapping.put("디케이", 15);
        typeMapping.put("힐", 16);
        typeMapping.put("투망", 17);
        typeMapping.put("워터", 18);
        typeMapping.put("어바", 19);
        typeMapping.put("웨폰", 20);
        typeMapping.put("외성문", 21);
        typeMapping.put("에볼단장", 22);  
        typeMapping.put("에볼단원", 23); 
        typeMapping.put("마나부족", 24); 
        typeMapping.put("먹자", 25); 
        typeMapping.put("픽업", 26);
        for (Map.Entry<String, Integer> entry : typeMapping.entrySet()) {
            reverseTypeMapping.put(entry.getValue(), entry.getKey());
        }
    }

    public RobotMent(int uid, int type, String ment) {
        this.uid = uid;
        this.type = type;
        this.ment = ment;
    }

    // 🛠 Getter 및 Setter
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public String getTypeString() { return reverseTypeMapping.get(type); }
    
    public String getMent() { return ment; }
    public void setMent(String ment) { this.ment = ment; }

    // 📌 타입 매핑 정보 반환
    public static Map<String, Integer> getTypeMapping() {
        return typeMapping;
    }
}

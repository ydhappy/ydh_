package lineage.bean.database;

import java.util.*;

public class RobotTalk {
    private int uid;
    private String keyword;
    private String ment;

    public RobotTalk(int uid, String keyword, String ment) {
        this.uid = uid;
        this.keyword = keyword;
        this.ment = ment;
    }

    // Getter & Setter
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getMent() { return ment; }
    public void setMent(String ment) { this.ment = ment; }

    /**
     * 키워드 문자열을 ']' 기준으로 분할한 리스트로 반환
     */
    public List<String> getKeywordList() {
        if (keyword == null || keyword.isEmpty())
            return Collections.emptyList();
        String[] parts = keyword.split("]");
        List<String> list = new ArrayList<>();
        for (String k : parts) {
            if (!k.trim().isEmpty())
                list.add(k.trim());
        }
        return list;
    }

    /**
     * 멘트 문자열을 ']' 기준으로 분할한 리스트로 반환
     */
    public List<String> getMentList() {
        if (ment == null || ment.isEmpty())
            return Collections.emptyList();
        String[] parts = ment.split("]");
        List<String> list = new ArrayList<>();
        for (String m : parts) {
            if (!m.trim().isEmpty())
                list.add(m.trim());
        }
        return list;
    }

    /**
     * 랜덤 멘트 반환
     */
    public String getRandomMent() {
        List<String> list = getMentList();
        if (list.isEmpty()) return null;
        return list.get(new Random().nextInt(list.size()));
    }

    @Override
    public String toString() {
        return keyword + " → " + ment;
    }
}
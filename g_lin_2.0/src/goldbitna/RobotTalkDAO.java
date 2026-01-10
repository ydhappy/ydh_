package goldbitna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.RobotTalk;
import lineage.database.DatabaseConnection;
import lineage.share.TimeLine;

public final class RobotTalkDAO {

    private static final List<RobotTalk> list = new ArrayList<>();

    static public void init(Connection con) {
        TimeLine.start("RobotTalkDAO..");

        list.clear();

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = con.prepareStatement("SELECT uid, keyword, ment FROM _robot_talk");
            rs = st.executeQuery();

            while (rs.next()) {
                RobotTalk talk = new RobotTalk(
                    rs.getInt("uid"),
                    rs.getString("keyword"),
                    rs.getString("ment")
                );
                list.add(talk);
            }

        } catch (Exception e) {
            lineage.share.System.printf("%s : init(Connection con)\r\n", RobotTalkDAO.class.toString());
            lineage.share.System.println(e);
        } finally {
            DatabaseConnection.close(st, rs);
        }

        TimeLine.end();
    }

    static public void reload() {
        TimeLine.start("로봇 talk (_robot_talk) 테이블 리로드 완료 - ");

        list.clear();

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getLineage();
            st = con.prepareStatement("SELECT uid, keyword, ment FROM _robot_talk");
            rs = st.executeQuery();

            while (rs.next()) {
                RobotTalk talk = new RobotTalk(
                    rs.getInt("uid"),
                    rs.getString("keyword"),
                    rs.getString("ment")
                );
                list.add(talk);
            }

        } catch (Exception e) {
            lineage.share.System.printf("%s : reload()\r\n", RobotTalkDAO.class.toString());
            lineage.share.System.println(e);
        } finally {
            DatabaseConnection.close(con, st, rs);
        }

        TimeLine.end();
    }

    static public List<RobotTalk> getList() {
        return list;
    }

    /**
     * 여러 키워드 중 하나라도 맞으면 해당 talk 반환 (type 비교 제거)
     */
    static public RobotTalk findTalkByKeyword(String keyword) {
        for (RobotTalk talk : list) {
            for (String k : talk.getKeywordList()) {
                if (k.equalsIgnoreCase(keyword)) {
                    return talk;
                }
            }
        }
        return null;
    }
}

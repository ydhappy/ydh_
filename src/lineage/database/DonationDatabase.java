package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Donation;


public class DonationDatabase {

    // 후원 정보를 데이터베이스에 저장하는 메서드
	public void insertDonation(String name, String account, int account_uid, 
            long amount, long date, boolean provide) {
		 Connection con = null;
		    PreparedStatement st = null;
		    try {
		        con = DatabaseConnection.getLineage(); // 데이터베이스 연결

            // SQL 쿼리 준비
            st = con.prepareStatement(
                    "INSERT INTO donation (name, account, account_uid, amount, date, provide) VALUES (?, ?, ?, ?, ?, ?)");
            
            
         // 값 설정
            st.setString(1, name);
            st.setString(2, account);
            st.setInt(3, account_uid);
            st.setLong(4, amount);
            st.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            st.setBoolean(6, provide);
            
            // 쿼리 실행
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // SQL 예외 처리
            lineage.share.System.printf(
                "%s : insertDonation(String name, String account, int account_uid, long amount, long date, boolean provide)\r\n",
                DonationDatabase.class.toString());
        } catch (Exception e) {
            e.printStackTrace(); // 기타 예외 처리
        } finally {
            // 자원 해제
            DatabaseConnection.close(st);
            DatabaseConnection.close(con); // Connection 객체도 해제
        }
    }

    // 후원 목록 조회 메서드
    public List<Donation> getDonationList(String name) {
        List<Donation> donations = new ArrayList<>();
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
        	con = DatabaseConnection.getLineage(); // 데이터베이스 연결
            
            // SQL 쿼리 준비 (provide가 false인 항목만 조회)
            st = con.prepareStatement("SELECT * FROM donation WHERE name = ? AND provide = ?");
            st.setString(1, name);
            st.setBoolean(2, false); // 제공되지 않은 후원 내역만 조회
            
            // 쿼리 실행
            rs = st.executeQuery();
            while (rs.next()) {
                Donation donation = new Donation();
                donation.setName(rs.getString("name"));
                donation.setAccount(rs.getString("account"));
                donation.setAccount_uid(rs.getInt("account_uid"));
                donation.setAmount(rs.getLong("amount"));
                donation.setDate(rs.getLong("date"));
                donation.setProvide(rs.getBoolean("provide"));
                donations.add(donation);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // SQL 예외 처리
        } catch (Exception e) {
            e.printStackTrace(); // 기타 예외 처리
        } finally {
            DatabaseConnection.close(rs);
            DatabaseConnection.close(st);
            DatabaseConnection.close(con); // Connection 객체 해제
        }
        return donations;
    }

    // 후원 내역을 제공 완료로 설정하는 메서드
    public void updateDonationList(String name) {
        Connection con = null;
        PreparedStatement st = null;
        try {
        	con = DatabaseConnection.getLineage(); // 데이터베이스 연결
            
            // SQL 쿼리 준비
            st = con.prepareStatement("UPDATE donation SET provide = ? WHERE name = ?");
            st.setBoolean(1, true); // 제공 완료로 설정
            st.setString(2, name); // 후원 name 설정
            
            // 쿼리 실행
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // SQL 예외 처리
        } catch (Exception e) {
            e.printStackTrace(); // 기타 예외 처리
        } finally {
            DatabaseConnection.close(st);
            DatabaseConnection.close(con); // Connection 객체 해제
        }
    }
}
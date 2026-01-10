package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lineage.share.Common;
import lineage.share.Mysql;
import lineage.share.TimeLine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DatabaseConnection {
	
	/** HikariCP */
	private static HikariDataSource fairy;
	private static HikariDataSource donation_fairy;
	
	static public void init(){
		TimeLine.start("DatabaseConnection..");
		
		try {
			HikariConfig config = new HikariConfig();

			config.setDriverClassName(Mysql.driver);
			config.setJdbcUrl(Mysql.url);
			config.setUsername(Mysql.id);
			config.setPassword(Mysql.pw);
			/**서버 환경에 맞게끔 수정*/
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("useServerPrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.setMinimumIdle(30);
			config.setMaximumPoolSize(800);
			config.setMaxLifetime(60000);

			fairy = new HikariDataSource(config);
			
			if (Mysql.is_donation) {
				config.setDriverClassName(Mysql.driver);
				config.setJdbcUrl(Mysql.donation_url);
				config.setUsername(Mysql.id);
				config.setPassword(Mysql.pw);
				/**서버 환경에 맞게끔 수정*/
				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("useServerPrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
				config.setMaximumPoolSize(10);
				
				donation_fairy = new HikariDataSource(config);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", DatabaseConnection.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
	static public void close(){
		fairy.close();
		
		if (donation_fairy != null) {
			donation_fairy.close();
		}
	}
	
	/**
	 * 풀에 등록된 컨넥션 한개 추출하기.
	 * @return
	 * @throws Exception
	 */
	static public Connection getLineage() throws Exception {		
		Connection con = null;
		do {
			con = fairy.getConnection();
			Thread.sleep(Common.THREAD_SLEEP);
		} while (con == null);
		return con;
	}
	
	static public Connection getDonation() throws Exception {
		Connection con = null;
		do {
			con = donation_fairy.getConnection();
			Thread.sleep(Common.THREAD_SLEEP);
		} while (con == null);
		return con;
	}
	
	static public void close(Connection con) {
		try { con.close(); } catch (Exception e) {}
	}
	
	static public void close(Connection con, PreparedStatement st) {
		close(st);
		close(con);
	}
	
	static public void close(Connection con, PreparedStatement st, ResultSet rs) {
		close(st, rs);
		close(con);
	}
	
	static public void close(PreparedStatement st) {
		try { st.close(); } catch (Exception e) {}
	}
	
	static public void close(PreparedStatement st, ResultSet rs) {
		try { rs.close(); } catch (Exception e) {}
		close(st);
	}
	
	static public void close(ResultSet rs) {
		if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace(); // 예외 처리
            }
        }
    }
}

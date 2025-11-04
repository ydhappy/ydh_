package lineage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class AuthClient extends Thread {

	InputStream is;
	OutputStream os;
	Socket socket;
	byte[] bytes = null;
	String message = null;

	public AuthClient(Socket socket) {
		this.socket = socket;
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (IOException e) {
			System.err.println("클라이언트 에러" + e);
		}
	}

	@Override
	public void run() {

	    // 클라이언트의 IP 주소를 가져옵니다
	    InetAddress clientInetAddress = socket.getInetAddress();
	    String clientIPAddress = clientInetAddress.getHostAddress();

	    while (true) {
	        try {
	            bytes = new byte[200];
	            int readByteCount = is.read(bytes);
	            // 메시지를 문자열로 변환합니다
	            message = new String(bytes, 0, readByteCount, "UTF-8");
	            // 데이터베이스에 IP 주소를 삽입합니다
	            Insert(clientIPAddress);
	            // 현재 스레드나 소켓을 종료합니다
	            Quit(this);
	            return;
	        } catch (IOException e) {
	            // 예외 발생 시 처리 로직을 추가합니다
	            handleException(e);
	            Quit(this);
	            return;
	        }
	    }
	}

	public void Insert(String ip) {
	    // 데이터베이스 접속 정보
	    String host = "localhost";
	    String port = "3306";
	    String sid = "lin200";
	    String id = "root";
	    String pw = "";
	    String driver = "com.mysql.jdbc.Driver";

	    // 실행할 쿼리
	    String query = "INSERT INTO AuthClients(ip) VALUES(?)";

	    try {
	        Class.forName(driver);

	        // 데이터베이스에 연결합니다
	        String url = "jdbc:mysql://" + host + ":" + port + "/" + sid;
	        try (Connection con = DriverManager.getConnection(url, id, pw);
	             PreparedStatement pstmt = con.prepareStatement(query)) {
	             
	            // 쿼리에 IP 주소를 설정합니다
	            pstmt.setString(1, ip);
	            // 쿼리 실행
	            int result = pstmt.executeUpdate();
	            // 결과를 로그에 남길 수 있습니다 (선택 사항)
	            // logResult(result);

	        } catch (Exception e) {
	            handleException(e);
	        }
	    } catch (ClassNotFoundException e) {
	        handleException(e);
	    }
	}

	private void handleException(Exception e) {
	    System.err.println("Exception: " + e.getMessage());
	}

	public void Quit(AuthClient client) {
		try {
			client.is.close();
			client.os.close();
			client.socket.close();
		} catch (IOException e) {
			System.err.println("Quit" + e);
		}
	}
}

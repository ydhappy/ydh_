package goldbitna.telegram;


import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.world.object.object;

public class TeleBotController {
		
	public object findName(String name) {
	    // 1. 데이터베이스에서 모든 캐릭터 이름을 가져옵니다.
	    List<String> characterNames = new ArrayList<>();
	    Connection con = null;
	    try {
	        con = DatabaseConnection.getLineage();
	        CharactersDatabase.getNameAllList(con, characterNames);  // 캐릭터 이름 목록을 가져옴
	    } catch (Exception e) {
	        lineage.share.System.println("데이터베이스에서 케릭터 이름을 가져오는 동안 오류가 발생 하였습니다.");
	        lineage.share.System.println(e);
	        return null;
	    } finally {
	        DatabaseConnection.close(con, null, null);  // 데이터베이스 연결 종료
	    }

	    	// 주어진 이름과 데이터베이스에서 가져온 캐릭터 이름을 비교
	    for (String characterName : characterNames) {
	        if (name.equalsIgnoreCase(characterName)) {
	            // 일치하는 이름을 찾으면 해당 이름에 맞는 PcInstance 객체를 생성하여 반환
	        	object find_o = new object();  // 새로운 object 객체 생성
	        	find_o.setName(name);  // 이름을 설정
	        	return find_o;  // 일치하는 객체 반환
	        }
	    }
	    // 데이터베이스에서 해당 이름을 찾지 못했을 경우 null 반환
	    return null;
	}
}
	


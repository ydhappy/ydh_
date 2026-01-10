package goldbitna.item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Book;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Book;
import lineage.share.Lineage;
import lineage.world.controller.BookController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Memorybeads extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Memorybeads();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    List<Book> list = BookController.find((PcInstance) cha);

	    try {
	        BufferedReader lnrr1 = new BufferedReader(new FileReader("Memorybeads.conf"));
	        String line1;
	        while ((line1 = lnrr1.readLine()) != null) {
	            if (line1.startsWith("#"))
	                continue;

	            int pos = line1.indexOf("=");
	            if (pos > 0) {
	                String key = line1.substring(0, pos).trim();
	                String value = line1.substring(pos + 1).trim();

	                if (key.equalsIgnoreCase("Memorybeads_list")) {
	                    if (!value.isEmpty()) {
	                        List<String[]> Memorybeads_list = new ArrayList<>();

	                        String[] s1 = value.split(";");
	                        for (String temp : s1) {
	                            String[] s2 = temp.split(",");
	                            try {
	                                String name = s2[0].trim();
	                                String x = s2[1].trim();
	                                String y = s2[2].trim();
	                                String map = s2[3].trim();
	                                String[] data = {name, x, y, map};
	                                Memorybeads_list.add(data);
	                            } catch (Exception e) {
	                                lineage.share.System.println("Memorybeads_list 의 값이 잘못되었습니다. 값 : " + temp);
	                                lineage.share.System.println(e);
	                            }
	                        }

	                        if (!Memorybeads_list.isEmpty()) {
	                            boolean isAdded = false; // 중복 여부 확인을 위한 변수
	                            for (String[] a : Memorybeads_list) {
	                                if (!isContains(list, a[0])) {
	                                    Book b = new Book();
	                                    b.setLocation(a[0]);
	                                    b.setX(Integer.valueOf(a[1]));
	                                    b.setY(Integer.valueOf(a[2]));
	                                    b.setMap(Integer.valueOf(a[3]));
	                                    list.add(b);
	                                    cha.toSender(S_Book.clone(BasePacketPooling.getPool(S_Book.class), b));
	                                    isAdded = true; // 새로운 기억이 추가됨
	                                }
	                            }
	                            if (isAdded) {
	                                // 등록된 기억 정보를 데이터베이스에 저장
	                                Connection con = null;
	                                try {
	                                    con = DatabaseConnection.getLineage();
	                                    CharactersDatabase.saveBook(con, (PcInstance) cha);
	                                } catch (Exception e) {
	                                    e.printStackTrace();
	                                } finally {
	                                    DatabaseConnection.close(con);
	                                }
	                                // 기억 목록이 추가되었음을 알림
	                                ChattingController.toChatting(cha, "기억 목록이 추가되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	                            } else {
	                                // 중복된 기억이 있을 때 알림
	                                ChattingController.toChatting(cha, "기억 목록이 이미 추가되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        lnrr1.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 아이템 수량 갱신
	    cha.getInventory().count(this, getCount() - 1, true);
	}

	private boolean isContains(List<Book> list, String location) {
	    for (Book b : list) {
	        if (b.getLocation().equalsIgnoreCase(location)) {
	            return true;
	        }
	    }
	    return false;
	}
}
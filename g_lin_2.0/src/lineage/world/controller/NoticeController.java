package lineage.world.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lineage.bean.lineage.Kingdom;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;

public class NoticeController {

	static private long real_notice_time;
	static private long kingdom_war_notice_time;
	static private int real_notice_idx;
	static private List<String> real_notice;

	static public void init() {
		TimeLine.start("NoticeController..");
		real_notice = new ArrayList<String>();
		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("notice.txt"));
			String line;
			while ((line = lnrr.readLine()) != null) {
				line = line.trim();
				if (line.contains("#") || line.length() <= 0)
					continue;

				real_notice.add(line);
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", NoticeController.class.toString());
			lineage.share.System.println(e);
		}

		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("notice.txt 파일 리로드 완료 - ");
		
		real_notice.clear();
		
		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("notice.txt"));
			String line;
			while ((line = lnrr.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#") || line.length() <= 0)
					continue;

				real_notice.add(line);
			}
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", NoticeController.class.toString());
			lineage.share.System.println(e);
		}

		TimeLine.end();
	}

	static public void toTimer(long time) {
	    if (time - real_notice_time >= Lineage.notice_delay) {        
	        real_notice_time = time;
	        
	        if (real_notice.size() == 0)
	            return;
	        if (real_notice.size() <= real_notice_idx)
	            real_notice_idx = 0;

	        World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), real_notice.get(real_notice_idx++)));

	        if (Lineage.open_wait) {
	            World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), 
	                    String.format("테스트 서버는 현재 오픈 대기중입니다.")));
	        }
	    }
	    
	    if ((time - kingdom_war_notice_time >= Lineage.kingdom_war_notice_delay) && Lineage.is_kingdom_war && Lineage.is_kingdom_war_notice) {
	        kingdom_war_notice_time = time;
	        // 공성전 시간 알림
	        kingdomWarNoticeNew(time);
	    }
	}

	@SuppressWarnings("deprecation")
	public static void kingdomWarNoticeNew(long time) {
	    try {
	        Calendar nowCalendar = Calendar.getInstance();
	        nowCalendar.setTimeInMillis(time);
	        Date nowDate = nowCalendar.getTime();
	        nowDate.setTime(time);
	        
	        List<Integer> warDays = Lineage.getKingdomWarDayList(); // 공성 요일 리스트
	        
	        for (int i = 0; i < Lineage.kingdom_war_list.size(); i++) {
	            if (i >= warDays.size())
	                break; // 매칭되는 요일이 없으면 종료
	            
	            Integer kingdomId = Lineage.kingdom_war_list.get(i);
	            Integer day = warDays.get(i);
	            
	            Kingdom k = KingdomController.find(kingdomId);
	            if (k == null)
	                continue;
	            
	            boolean toDay = nowDate.getDay() == day;
	            
	            String dayString = "";
	            switch (day) {
	                case 0: dayString = "일"; break;
	                case 1: dayString = "월"; break;
	                case 2: dayString = "화"; break;
	                case 3: dayString = "수"; break;
	                case 4: dayString = "목"; break;
	                case 5: dayString = "금"; break;
	                case 6: dayString = "토"; break;
	            }
	            
	            String noticeMessage = "";
	            if (toDay) {
	                if (Lineage.kingdom_war_hour < 12)
	                    noticeMessage = String.format("%s의 공성전이 오늘 오전 %d시 %s에 진행됩니다.", 
	                            k.getName(), (Lineage.kingdom_war_hour == 0 ? 12 : Lineage.kingdom_war_hour), 
	                            (Lineage.kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.kingdom_war_min)));
	                else
	                    noticeMessage = String.format("%s의 공성전이 오늘 오후 %d시 %s에 진행됩니다.", 
	                            k.getName(), (Lineage.kingdom_war_hour - 12 == 0 ? 12 : Lineage.kingdom_war_hour - 12), 
	                            (Lineage.kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.kingdom_war_min)));
	            } else {
	                if (Lineage.kingdom_war_hour < 12)
	                    noticeMessage = String.format("%s의 공성전은 (%s요일) 오전 %d시 %s에 진행됩니다.", 
	                            k.getName(), dayString, (Lineage.kingdom_war_hour == 0 ? 12 : Lineage.kingdom_war_hour), 
	                            (Lineage.kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.kingdom_war_min)));
	                else
	                    noticeMessage = String.format("%s의 공성전은 (%s요일) 오후 %d시 %s에 진행됩니다.", 
	                            k.getName(), dayString, (Lineage.kingdom_war_hour - 12 == 0 ? 12 : Lineage.kingdom_war_hour - 12), 
	                            (Lineage.kingdom_war_min == 0 ? "정각" : String.format("%d분", Lineage.kingdom_war_min)));
	            }
	            
	            World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), noticeMessage));
	        }
	    } catch (Exception e) {
	        lineage.share.System.printf("%s : 공성 시간 공지 오류\r\n", NoticeController.class.toString());
	        lineage.share.System.println(e);
	    }
	}
}

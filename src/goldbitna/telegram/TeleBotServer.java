package goldbitna.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import lineage.share.TimeLine;

public class TeleBotServer {

	public static TeleBotManager myTeleBot = null;
	static public void init() throws Exception {
		
		TimeLine.start("TeleBotServer..");
		
		try {
	        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
	        myTeleBot = new TeleBotManager();
	        botsApi.registerBot(myTeleBot);
		} catch (Exception e) {
			lineage.share.System.printf("%s : init() Error\r\n", TeleBotServer.class.toString());
			lineage.share.System.println(e);
		}
		
        TimeLine.end();
      
		
	}
	
}


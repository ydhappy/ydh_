package goldbitna.telegram;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import goldbitna.telegram.TeleBotServer;
import goldbitna.SetGameMaster;
import goldbitna.telegram.TeleBotController;

import lineage.bean.database.Donation;
import lineage.database.DonationDatabase;
import lineage.share.Lineage;
import lineage.share.Admin;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.DonationController;
import lineage.world.controller.GameMasterController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class TeleBotManager extends TelegramLongPollingBot {

	static Long userId = Admin.user_Id;
	
    public String getBotUsername() {
        return "linServer_manager_bot";
    }
    
    // 운영자 귓속말 수신 상태를 나타내는 변수
    public static boolean isWhisperEnabled;  

    // 운영자 귓속말 수신 상태를 설정하는 메서드
    public static void setWhisperEnabled(boolean enable) {
        isWhisperEnabled = enable;
    }

    // 운영자 귓속말 수신 상태를 나타내는 변수
    public static boolean isChattingEnalbe; 

    // 운영자 귓속말 수신 상태를 설정하는 메서드
    public static void setChattingEnalbe(boolean enable) {
    	isChattingEnalbe = enable;
    }
            
    @Override
    public String getBotToken() {
        return Admin.bot_token;
    }

    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();
        
        
        if(userId == null || userId != user.getId()){
        	userId = user.getId();
        	//System.out.println(userId);
        	//TeleBotServer.myTeleBot.sendText(null, String.format("%s",userId));
    }
    
		StringTokenizer st = new StringTokenizer(msg.getText(), " ");
		String[] tokens = msg.getText().split(" ", 5);

		if (msg.getText().startsWith("/텔레")) {
            st.nextToken();  // "/텔레" 명령어를 처리하기 위해 첫 번째 토큰을 넘깁니다.
            try {
                // 토큰 분리 후 명령어 확인
                String command = st.nextToken();
                
                // "/텔레 켬" 처리
                if (command.equalsIgnoreCase("켬")) {
                	Admin.tele_enable = true;  // 텔레그램 활성화
                    TeleBotServer.myTeleBot.sendText(null, "텔레그램이 활성화되었습니다.");
                }
                // "/텔레 끔" 처리
                else if (command.equalsIgnoreCase("끔")) {
                	Admin.tele_enable = false;  // 텔레그램 비활성화
                    TeleBotServer.myTeleBot.sendText(null, "텔레그램이 비활성화되었습니다.");
                } else {
                    // 잘못된 명령어 처리
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 잘못된 명령어입니다. 사용법 : /텔레 <켬|끔>");
                }
            } catch (Exception e) {
                // 예외 처리 및 에러 로그 출력
                e.printStackTrace();  // 예외의 스택 트레이스 출력
                // 오류 메시지 전송
                TeleBotServer.myTeleBot.sendText(null, "채팅 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            }
        }		
		
		if (Admin.tele_enable && msg.getText().startsWith("/아이디")) {
			if(userId != null) {
			TeleBotServer.myTeleBot.sendText(null, String.format("User Id : %s",userId));
			}
		}

		if (Admin.tele_enable && msg.getText().startsWith("/공지")) { 
        	 ChattingController.toChatting(null,tokens[1], Lineage.CHATTING_MODE_GLOBAL );
        }
        
        if (Admin.tele_enable && msg.getText().startsWith("/차단")) { 
        	st.nextToken();
        	CommandController.toBan(null, st);
        }
        
        if (Admin.tele_enable && msg.getText().startsWith("/올버프")) { 
        	st.nextToken();
        	CommandController.toBuffAll(null);
        }            

        if (Admin.tele_enable && msg.getText().startsWith("/선물")) { 
            st.nextToken(); 
        	PcInstance pc = World.findPc(st.nextToken());
        	try {
        	if (pc != null) {
        		if (pc.getInventory() != null)
        			CommandController.toGiveItem(null, pc, st);

                 } else 
                	 TeleBotServer.myTeleBot.sendText(null, String.format("해당 캐릭터가 존재하지 않습니다."));
        	
        		 } catch (Exception e) { 	
        	 		 TeleBotServer.myTeleBot.sendText(null, String.format("/선물 포멧 에러!ex.선물 대상 수량 인챈트 축복"));
        	 	 }		   
        }  			  
        
        if (Admin.tele_enable && msg.getText().startsWith("/전체선물")) {
            try {
                st.nextToken(); // 명령어 부분 "/전체선물"을 건너뜀
                CommandController.toAllGiveItem(null, st);
            } catch (Exception e) {
                TeleBotServer.myTeleBot.sendText(null, "/전체선물 포멧 에러! ex. /전체선물 아이템이름 수량 인챈트 축복여부");
            }
        }  	
        
        if (Admin.tele_enable && msg.getText().startsWith("/후원지급")) { 
        	st.nextToken();
        	try {
		    	String name = st.nextToken();  // 입력한 캐릭터 이름을 가져옵니다.
		    	PcInstance pc = World.findPc(name);  // 캐릭터 인스턴스를 찾습니다.
		    	
		    	// 후원 목록을 조회합니다.
		        List<Donation> donations = new DonationDatabase().getDonationList(name);
		        
		        if (donations.isEmpty()) {
		            // 후원 내역이 없을 경우
		        	TeleBotServer.myTeleBot.sendText(null, String.format("[%s] 의 후원 내역이 없습니다.", name)); 
		            return;
		        }
		     // 후원 내역이 있을 경우 아이템 지급
		        for (Donation donation : donations) {
		        	long amount = donation.getAmount();
		        	String username = donation.getName();
		        	
		        	// 아이템 지급 로직
		        	DonationController.toGiveandTake(pc, amount);
		        	
		        	// 후원 지급 완료 메시지
		        	TeleBotServer.myTeleBot.sendText(null, String.format("[%s] 에게 후원코인(%d) 정상 지급되었습니다.", username, amount)); 
		            // 후원 내역을 제공 완료로 업데이트
		            new DonationDatabase().updateDonationList(name);
		        }
		        
		    } catch (NoSuchElementException e) {
		    	TeleBotServer.myTeleBot.sendText(null, String.format("캐릭터 이름을 입력해야 합니다."));
		    } catch (Exception e) {
		    	TeleBotServer.myTeleBot.sendText(null, String.format("후원 지급 처리 중 오류가 발생했습니다."));
		        e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력 (디버깅 용도)
		    }
		    return;
        }
        
        if (Admin.tele_enable && msg.getText().startsWith("/채팅")) {
            st.nextToken();  // "/채팅" 명령어를 처리하기 위해 첫 번째 토큰을 넘깁니다.
            try {
                // 토큰 분리 후 명령어 확인
                String command = st.nextToken();
                
                // "/채팅 켬" 처리
                if (command.equalsIgnoreCase("켬")) {
                	setChattingEnalbe(true);  // 채팅 수신 활성화
                    TeleBotServer.myTeleBot.sendText(null, "전체 채팅 수신이 활성화되었습니다.");
                }
                // "/채팅 끔" 처리
                else if (command.equalsIgnoreCase("끔")) {
                	setChattingEnalbe(false);  // 채팅 수신 비활성화
                    TeleBotServer.myTeleBot.sendText(null, "전체 채팅 수신이 비활성화되었습니다.");
                } else {
                    // 잘못된 명령어 처리
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 잘못된 명령어입니다. 사용법 : /채팅 <켬|끔>");
                }
            } catch (Exception e) {
                // 예외 처리 및 에러 로그 출력
                System.out.println("토큰 분리 실패: " + e.getMessage());
                e.printStackTrace();  // 예외의 스택 트레이스 출력
                // 오류 메시지 전송
                TeleBotServer.myTeleBot.sendText(null, "채팅 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            }
        }
        
        if (Admin.tele_enable && msg.getText().startsWith("/귓말")) {
            st.nextToken();  // "/귓말" 명령어를 처리하기 위해 첫 번째 토큰을 넘깁니다.
            try {
                // 토큰 분리 후 명령어 확인
                String command = st.nextToken();
                
                // "/귓말 켬" 처리
                if (command.equalsIgnoreCase("켬")) {
                    setWhisperEnabled(true);  // 귓말 수신 활성화
                    TeleBotServer.myTeleBot.sendText(null, "귓말 수신이 활성화되었습니다.");
                }
                // "/귓말 끔" 처리
                else if (command.equalsIgnoreCase("끔")) {
                    setWhisperEnabled(false);  // 귓말 수신 비활성화
                    TeleBotServer.myTeleBot.sendText(null, "귓말 수신이 비활성화되었습니다.");
                } else {
                    // 잘못된 명령어 처리
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 잘못된 명령어입니다. 사용법 : /귓말 <켬|끔>");
                }
            } catch (Exception e) {
                // 예외 처리 및 에러 로그 출력
                e.printStackTrace();  // 예외의 스택 트레이스 출력
                // 오류 메시지 전송
                TeleBotServer.myTeleBot.sendText(null, "귓말 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            }
        }


        if (Admin.tele_enable && msg.getText().startsWith("/DM")) {
            st.nextToken();
            try {
                // 1️ 운영자 리스트가 비어 있으면 기본 운영자 추가
                if (Admin.gmList.isEmpty()) {
                    Admin.gmList.add(new SetGameMaster("메티스", 99));  // 기본 운영자 추가
                }

                // 2️ 운영자 리스트에서 첫 번째 GM을 발신자로 설정
                SetGameMaster sender = Admin.gmList.get(0);

                // 3️ GM 정보를 이용해 object 생성
                TeleBotController teleBotController = new TeleBotController();
                object o = teleBotController.findName(sender.getName()); // GM 이름 기반으로 object 검색
                if (o == null) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 지정한 GM 캐릭터를 찾을 수 없습니다.");
                    return;
                }

                // 4️ 수신자 이름 추출
                if (!st.hasMoreTokens()) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 사용법: /DM <수신자> <메시지>");
                    return;
                }
                String toName = st.nextToken();

                // 5️ 수신자 캐릭터 찾기
                PcInstance pc = World.findPc(toName);
                if (pc == null) {
                    TeleBotServer.myTeleBot.sendText(null, String.format("[%s]님은 게임에 접속해 있지 않습니다.", toName));
                    return;
                }

                // 6️ 메시지 내용 추출
                if (!st.hasMoreTokens()) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 전송할 메시지를 입력하세요.");
                    return;
                }
                String message = st.nextToken("").trim();

                // 7️ 귓속말 전송 (운영자 이름 포함)
                ChattingController.toWhisper(o, toName, String.format("[GM %s] %s", sender.getName(), message));

            } catch (Exception e) {
                e.printStackTrace();
                TeleBotServer.myTeleBot.sendText(null, "메시지 전송 중 오류가 발생했습니다.");
            }
        }


        if (Admin.tele_enable && msg.getText().startsWith("/영자설정")) {
            st.nextToken();
            try {
                if (!st.hasMoreTokens()) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 사용법: /영자설정 <캐릭터명> <Access Level>");
                    return;
                }

                String isName = st.nextToken();
                if (!st.hasMoreTokens()) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** Access Level을 입력하세요.");
                    return;
                }

                String levelString = st.nextToken();
                int accLv = Integer.parseInt(levelString);

                PcInstance gmInstance = World.findPc(isName);
                if (gmInstance == null) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 해당 캐릭터를 찾을 수 없습니다.");
                    return;
                }

                // 기존 GM인지 확인 후 업데이트 또는 추가
                SetGameMaster existingGM = SetGameMaster.findGMByName(Admin.gmList, isName);
                if (existingGM != null) {
                    existingGM.setAccessLevel(accLv);
                } else {
                    Admin.gmList.add(new SetGameMaster(isName, accLv));
                }

                GameMasterController.SetAdmin((lineage.world.object.Character) gmInstance, accLv);
                TeleBotServer.myTeleBot.sendText(null, String.format("운영자 %s님의 Access Level이 %d로 설정되었습니다.", isName, accLv));

            } catch (Exception e) {
                e.printStackTrace();
                TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 처리 중 오류가 발생했습니다.");
            }
        }

        if (Admin.tele_enable && msg.getText().startsWith("/영자해제")) {
            st.nextToken();
            try {
                if (!st.hasMoreTokens()) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 캐릭터 이름을 입력하세요.");
                    return;
                }

                String isName = st.nextToken();
                PcInstance gmInstance = World.findPc(isName);
                if (gmInstance == null) {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 해당 캐릭터를 찾을 수 없습니다.");
                    return;
                }

                // GM 리스트에서 삭제
                SetGameMaster existingGM = SetGameMaster.findGMByName(Admin.gmList, isName);
                if (existingGM != null) {
                    Admin.gmList.remove(existingGM);
                } else {
                    TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 해당 캐릭터는 운영자가 아닙니다.");
                    return;
                }

                GameMasterController.RstAdmin((lineage.world.object.Character) gmInstance, 0);
                TeleBotServer.myTeleBot.sendText(null, String.format("운영자 %s님의 권한이 해제되었습니다.", isName));

            } catch (Exception e) {
                e.printStackTrace();
                TeleBotServer.myTeleBot.sendText(null, "** ERROR ** 처리 중 오류가 발생했습니다.");
            }
        }
	}
            
    //System.out.println(user.getFirstName() + " wrote " + msg.getText());
       // Long id = user.getId();
        //sendText(id, msg.getText());

    
    public void sendText(Long who, String what) {
        if (who == null)
            who = userId;

        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) // Who are we sending a message to
                .text(what).build();    // Message content
        boolean messageSent = false;

        while (!messageSent) {
            try {
                execute(sm);            // Actually sending the message
                messageSent = true;     // 메시지가 성공적으로 전송됨
            } catch (TelegramApiException e) {
                if (e.getMessage().contains("429")) { // Too Many Requests
                    int retryAfter = extractRetryAfter(e.getMessage());
                    System.out.println("Rate limit exceeded. Retrying after " + retryAfter + " seconds.");
                    try {
                        Thread.sleep(retryAfter * 1000); // 제한 시간만큼 대기
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                } else {
                    throw new RuntimeException(e); // 다른 오류는 그대로 던짐
                }
            }
        }
    }

    // 429 오류에서 retry_after 값을 추출
    private int extractRetryAfter(String errorMessage) {
        try {
            String[] parts = errorMessage.split("retry_after");
            String retryAfter = parts[1].replaceAll("[^0-9]", "");
            return Integer.parseInt(retryAfter);
        } catch (Exception e) {
            return 1; // 기본 대기 시간 (1초)
        }
    }


    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        boolean messageSent = false;

        while (!messageSent) {
            try {
                execute(sm);
                messageSent = true;
            } catch (TelegramApiException e) {
                if (e.getMessage().contains("429")) { // Too Many Requests
                    int retryAfter = extractRetryAfter(e.getMessage());
                    System.out.println("Rate limit exceeded. Retrying after " + retryAfter + " seconds.");
                    try {
                        Thread.sleep(retryAfter * 1000); // 제한 시간만큼 대기
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                } else {
                    throw new RuntimeException(e); // 다른 오류는 그대로 던짐
                }
            }
        }
    }
}
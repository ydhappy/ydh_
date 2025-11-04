package lineage.world.object.robot.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lineage.bean.lineage.Trade;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.RobotController;
import lineage.world.controller.TradeController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.magic.AdvanceSpirit;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.BurningWeapon;
import lineage.world.object.magic.EarthSkin;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.IronSkin;
import lineage.world.object.magic.WindShot;

public class BuffRobotInstance3 extends RobotInstance {
	
	// 타이머 처리에 사용되는 함수. 멘트표현에 딜레이를 주기 위해. 
	protected long time_ment;
	// 홍보 멘트 목록
	protected List<String> list_ment;
	// 현재 표현된 홍보문구 위치 확인용.
	protected int idx_ment;
	// 거래가 요청된 시간에서 10초 정도 더 늘린값. 타이머함수에서 해당 시간이 오바되면 자동 거래취소되게 하기위해.
	protected long time_trade;
	// 같이 버프시전할 객체들
	protected List<BuffRobotInstance3> list;
	
	private static int mainObjectId;
	private boolean isTrading;
	// 멘트 출력 횟수를 추적할 변수
	private int chatCount = 0;
	// 멘트 출력 횟수의 상한값
	private static final int CHAT_LIMIT = 2;
	
	public BuffRobotInstance3() {
		//
	}
	
	public BuffRobotInstance3(int x, int y, int map, int heading, String name, String title, int lawful, int gfx, int gfxMode) {
		this.objectId = ServerDatabase.nextEtcObjId();
		mainObjectId = (int) this.objectId;
		this.x = x;
		this.y = y;
		this.map = map;
		this.heading = heading;
		this.name = name;
		this.title = title;
		this.lawful = lawful;
		this.gfx = gfx;
		this.gfxMode = gfxMode;
		
		// 보조 케릭
		list = new ArrayList<BuffRobotInstance3>();

		for(int r=1 ; r<4 ; ++r) {
			
			BuffRobotInstance3 o = new BuffRobotInstance3();
			o.setObjectId(ServerDatabase.nextEtcObjId());
			o.setX(33061);
			o.setY(33392 + r);
			o.setMap(map);
			o.setHeading(heading);
			o.setLawful(-12000);
			o.setGfx(Util.random(0, 1)==0 ? 16234 : 16235);
			o.setName(String.format("%s%d", "은말보조", r));
			o.setTitle("장사중");
			list.add(o);
		  }
		for(int r=1 ; r<6 ; ++r) {
			
			BuffRobotInstance3 o = new BuffRobotInstance3();
			o.setObjectId(ServerDatabase.nextEtcObjId());
			o.setX(33060);
			o.setY(33392 + r);
			o.setMap(map);
			o.setHeading(heading);
			o.setLawful(-32767);
			o.setGfx(Util.random(0, 1)==0 ? 16234 : 16235);
			o.setName(String.format("%s%d", "은말샵", r));
			o.setTitle("장사중");
			list.add(o);
		}
		for(int r=1 ; r<7 ; ++r) {
			
			BuffRobotInstance3 o = new BuffRobotInstance3();
			o.setObjectId(ServerDatabase.nextEtcObjId());
			o.setX(33058);
			o.setY(33392 + r);
			o.setMap(map);
			o.setHeading(heading);
			o.setLawful(-32767);
			o.setGfx(Util.random(0, 1)==0 ? 16234 : 16235);
			o.setName(String.format("%s%d", "은말버프", r));
			o.setTitle("장사중");
			list.add(o);
		}
		for(int r=1 ; r<7 ; ++r) {
			
			BuffRobotInstance3 o = new BuffRobotInstance3();
			o.setObjectId(ServerDatabase.nextEtcObjId());
			o.setX(33059);
			o.setY(33392 + r);
			o.setMap(map);
			o.setHeading(heading);
			o.setLawful(-32767);
			o.setGfx(Util.random(0, 1)==0 ? 16234 : 16235);
			o.setName(String.format("%s%d", "은말지킴이", r));
			o.setTitle("장사중");
			list.add(o);
		}
		// 멘트 목록.
		list_ment = new ArrayList<String>();
		list_ment.add(String.format("[은말헤이샵] %d원 서비스 팍팍해드립니다.", Lineage.robot_auto_buff_aden3));
		list_ment.add("앞에서 어택해주세요.");
	}
	

	
	@Override
	public void toWorldJoin() {
		super.toWorldJoin();
		
		if(list != null) {
			for(object o : list)
				o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
			
			RobotController.count += list.size();
		}
	}
	
	@Override
	public void toWorldOut() {
		super.toWorldOut();
		
		if(list != null) {
			for(object o : list){
				o.clearList(true);
				World.remove(o);
			}
			
			RobotController.count -= list.size();
		}
	}
	
	@Override
	public int getGm(){
		return 1;
	}
	
	@Override
	public void setNowHp(int nowhp){}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (this.objectId == mainObjectId) {
			// 방향
			setHeading(Util.calcheading(this, cha.getX(), cha.getY()));
			toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
			// 교환 요청.
			TradeController.toTrade(this, cha);
			// 랜덤 객체 생성
			Random random = new Random();
			// 랜덤 값 생성 (0 또는 1)
			int randomValue = random.nextInt(2);

			// 멘트치기
			if (chatCount < CHAT_LIMIT) {
				if (randomValue == 0) {
					ChattingController.toChatting(this, String.format("%s님 어서오세요^^", cha.getName()), Lineage.CHATTING_MODE_NORMAL);
				} else {
					ChattingController.toChatting(this, String.format("%s님 또 오셨네요~", cha.getName()), Lineage.CHATTING_MODE_NORMAL);
				}
				chatCount++;
			} else {
				// 경고 멘트
				ChattingController.toChatting(this, "자꾸 이러시면 영업방해로 경찰에 신고할 겁니다.", Lineage.CHATTING_MODE_NORMAL);
			}
			// 교환창에 아이템등록까지 대기시간 설정.
			time_trade = System.currentTimeMillis() + (1000 * 10);
			isTrading = true;
		}
	}
	
	@Override
	public void toTradeCancel(Character cha) {
		isTrading = false;
		chatCount = 0;
	}
	
	private void sendThankYouMessage() {
	    ChattingController.toChatting(this, "\f         \\\\f;f;감사합니다^^ 또 오세요~", Lineage.CHATTING_MODE_NORMAL);
	}
	
	@Override
	public void toTradeOk(Character cha){
	    // 멘트치기
	    ChattingController.toChatting(this, "\f         \\\\f<f<만피 체워 드립니다 써비스!", Lineage.CHATTING_MODE_NORMAL);
	    // 풀힐
	    Heal.init(this, SkillDatabase.find(8, 0), cha.getObjectId());
	    //만피 체워주기
	    cha.setNowHp(cha.getTotalHp());
	    
	    new Timer().schedule(new TimerTask() {
	        @Override
	        public void run() {
	            sendThankYouMessage();
	        }
	    }, 3000); 
	    
		// 인사
		if (SpriteFrameDatabase.findGfxMode(getGfx(), 68))
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 68), true);

	    // 헤이스트 서비스
	    for (int i = 0; i < 18; i++) {
	        Haste.init(list.get(i), SkillDatabase.find(6, 2), cha.getObjectId());
	    }
	}

	@Override
	public void toTimer(long time){
		if(isWorldDelete())
			return;
		
		// 거래에 등록된 아이템 확인.
		Trade t = TradeController.find(this);
		if(t!=null && t.isAden(false, Lineage.robot_auto_buff_aden3)){
			// 거래 승인 요청.
			TradeController.toTradeOk(this);
		}
		// 거래요청된 상태에서 10초가 지낫을경우 자동 취소처리.
		if (time_trade != 0 && time_trade - time <= 0) {
			time_trade = 0;
			isTrading = false;
			chatCount = 0;
			// 거래 취소 요청.
			TradeController.toTradeCancel(this);
		}
		// 멘트 처리 구간.
		if (!isTrading && time_ment - time <= 0) {
			// 5초마다 홍보하기.
			time_ment = time + (1000 * 20);
			// 표현할 문구 위치값이 오바됫을경우 초기화.
			if(idx_ment >= list_ment.size())
				idx_ment = 0;
			// 홍보문구 표현.
			ChattingController.toChatting(this, list_ment.get(idx_ment++), Lineage.CHATTING_MODE_NORMAL);
		}
	}
	
}
package lineage.world.object.npc.background;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.item.Meat;
import lineage.world.object.npc.background.door.Door;

public class Switch extends BackgroundInstance {

	// 스위치 온오프 상태.
	private boolean status;
	private boolean status_temp;
	private int gfxMode_temp;
	private int timer_cnt; // toTimer 에서 사용되는 변수.

	public Switch() {
		CharacterController.toWorldJoin(this);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		// 닫혀있을때만 처리.
		if (gfxMode == 29) {
			// 스위치 변화.
			toOn();
			toSend();
			// 문 찾아서 열기.
			Door d = searchDoor();
			if (d != null) {
				d.toOpen();
				d.toSend();
			}
		}
	}

	@Override
	public void toTimer(long time) {
		switch (getMap()) {
		case 201: // 마법사 30레벨 퀘스트
			// 현재상태 임시 저장.
			status_temp = status;
			// 좌표값에 객체존재여부따라 gfxmode 변경.
			if (World.isMapdynamic(x, y, map)) 
				toOn();
			else
				toOff();
			// 임시저장값과 다르면 패킷처리.
			if (status != status_temp) {
				toSend();
				// 주변에 문을 찾아서 해당 문에 카운팅값을 증가 및 감소.
				// 카운팅값이 4이상이라면 문을 오픈. 이하라면 클로즈.
				Door d = searchDoor();
				if (d != null) {
					// 현재값 임시 저장
					gfxMode_temp = d.getGfxMode();
					// 스위치 상태에따라 카운팅
					if (status)
//						d.setTempCount(d.getTempCount() + 1);
//					else
//						d.setTempCount(d.getTempCount() - 1);

					// 카운팅값 확인해서 문 열고 닫기.
//					if (d.getTempCount() > 3)
						d.toOpen();
					else
						d.toClose();
					// 변화가 이뤄졌다면 패킷 처리.
					if (gfxMode_temp != d.getGfxMode())
						d.toSend();
				}
			}
			break;
		case 2: // 말섬던전2층 스위치.
			// 문이 열려잇을경우 자동으로 닫히게 하기위해.
			if (gfxMode == 28) {
				if (++timer_cnt % Lineage.door_open_delay == 0) {
					// 스위치 변화.
					toOff();
					toSend();
					// 문찾아서 닫기.
					Door d = searchDoor();
					if (d != null) {
						d.toClose();
						d.toSend();
					}
				}
			}
			break;
		case 815: // 뉴 말섬던전2층 스위치.
			// 문이 열려잇을경우 자동으로 닫히게 하기위해.
			if (gfxMode == 28) {
				if (++timer_cnt % Lineage.door_open_delay == 0) {
					// 스위치 변화.
					toOff();
					toSend();
					// 문찾아서 닫기.
					Door d = searchDoor();
					if (d != null) {
						d.toClose();
						d.toSend();
					}
				}
			}
			break;
		}
	}

	public void toOn() {
		setGfxMode(28);
		status = true;
	}

	public void toOff() {
		setGfxMode(29);
		status = false;
	}

	public void toSend() {
		toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
	}

	private Door searchDoor() {
	    int searchRadius = 80; // 검색 반경 설정

	    for (object o : getInsideList()) {
	        if (o instanceof Door) {
	            Door d = (Door) o;
	            // 좌표를 double로 캐스팅하여 정확한 계산을 수행
	            double dx = (double) d.getX();
	            double dy = (double) d.getY();
	            double cx = (double) x;
	            double cy = (double) y;

	            // 올바른 거리 계산 방식: 제곱근을 사용하는 유클리드 거리
	            double distance = Math.sqrt(Math.pow(dx - cx, 2) + Math.pow(dy - cy, 2));
	            
	            // 반경 내 문 객체를 찾음
	            if (distance <= searchRadius && d.getKey() < 0) {
	                return d;
	            }
	        }
	    }
	    return null;
	}
	
	private Door searchDoor1() {
	    // 검색 반경을 픽셀 단위로 정의
	    int searchRadiusPixels = 50;

	    for (object o : getInsideList()) {
	        if (o instanceof Door) {
	            Door d = (Door) o;
	            
	            // 문 좌표와 스위치 좌표를 거리 단위로 변환
	            double dx = (double) d.getX();
	            double dy = (double) d.getY();
	            double cx = (double) x;
	            double cy = (double) y;

	            double distance = Math.sqrt(Math.pow(dx - cx, 2) + Math.pow(dy - cy, 2));
	            
	            // 계산된 거리와 검색 반경을 비교하여 문을 찾음
	            if (distance <= searchRadiusPixels && d.getKey() < 0) {
	                System.out.println("문 객체 찾음: " + d.getName() + ", 거리: " + distance);
	                return d;
	            }
	        }
	    }
	    System.out.println("문 객체를 찾을 수 없음");
	    return null;
	}
}

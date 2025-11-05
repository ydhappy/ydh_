package lineage.world.object.instance;

import java.sql.Connection;

import lineage.world.World;
import lineage.world.controller.InventoryController;

public class RobotInstance extends PcInstance {

    // Robot 상태 비트 저장용 변수
    private long robotStatus = 0;
    
	public RobotInstance() {
		super(null);
	}

    // Robot 상태 가져오기
    public long getRobotStatus() {
        return robotStatus;
    }
    
    // Robot 상태 설정하기
    public void setRobotStatus(long robotStatus) {
        this.robotStatus = robotStatus;
    }
    
	@Override
	public void toTimer(long time) {

	}

	@Override
	public void toWorldJoin() {
		InventoryController.toWorldJoin(this);
		toTeleport(getX(), getY(), getMap(), false);
	} 

	@Override
	public void toWorldOut() {
		clearList(true);
		World.remove(this);

		InventoryController.toWorldOut(this);
	}

	@Override
	public void toSave(Connection con) {

	}
}

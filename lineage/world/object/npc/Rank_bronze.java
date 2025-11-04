package lineage.world.object.npc;

import java.util.Timer;
import java.util.TimerTask;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.world.object.object;

public class Rank_bronze extends object {

    private Timer timer;
    private int heading = 0; // 방향 초기화 (0부터 7까지)

    public Rank_bronze() {
        startRotationTimer();
    }

    private void startRotationTimer() {
        timer = new Timer(true); // 데몬 스레드로 타이머 생성
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rotateHeading();
            }
        }, 0, 300); // 0ms 딜레이 후 시작, 2000ms 간격으로 실행
    }

    private void rotateHeading() {
        heading = (heading - 1 + 8) % 8; // 방향을 시계 반대 방향으로 회전시킵니다 (0~7 사이)
        // 현재 객체의 위치와 회전 방향을 기반으로 heading을 설정
        setHeading(heading);
        // 클라이언트에게 방향 변경을 전송
        toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
    }
}
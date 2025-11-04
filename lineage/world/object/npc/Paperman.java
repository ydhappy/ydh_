package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;

public class Paperman extends NpcInstance {

    private long lastChatTime;  // 마지막으로 멘트를 한 시간을 기록
    private int chatIndex;      // 현재 사용할 멘트의 인덱스
    
    public Paperman(Npc npc) {
        super(npc);
        this.lastChatTime = 0;
        this.chatIndex = 0;
    }
    
    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
        pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Paperman"));
    }
    
    @Override
    protected void toAiWalk(long time){
        // 마지막 멘트 이후 10초(10,000 밀리초)가 지났는지 확인
        if (time - lastChatTime >= 30000) { 
            switch (chatIndex) {
                case 0:
                    ChattingController.toChatting(this, "청소중...  청소중...", Lineage.CHATTING_MODE_NORMAL);
                    break;
                case 1:
                    ChattingController.toChatting(this, "상아탑 내부... 이상무", Lineage.CHATTING_MODE_NORMAL);
                    break;
                case 2:
                    ChattingController.toChatting(this, "휴우, 다리 아파라...", Lineage.CHATTING_MODE_NORMAL);
                    break;
            }
            chatIndex = (chatIndex + 1) % 3;  // 다음 멘트로 이동
            lastChatTime = time;  // 마지막 멘트 시간을 갱신
        }

        super.toAiWalk(time);
    }
}
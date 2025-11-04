package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_SoundEffect;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Kirius extends TeleportInstance {
	
	private long lastSoundPlayTime = 0; // 마지막 사운드 재생 시간을 저장할 변수

	public Kirius(Npc npc) {
		super(npc);
	}

	public long getLastSoundPlayTime() {
		return lastSoundPlayTime;
	}

	public void setLastSoundPlayTime(long lastSoundPlayTime) {
		this.lastSoundPlayTime = lastSoundPlayTime;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		long currentTime = System.currentTimeMillis(); // 현재 시간(밀리초)
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kirius1"));
		if (currentTime - getLastSoundPlayTime() >= 2700) {
			pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27799));
			// 현재 시간을 마지막 사운드 재생 시간으로 업데이트
			setLastSoundPlayTime(currentTime);
		}
	}

	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kirius2", null, list.get(0)));
		}else if(action.equalsIgnoreCase("teleportURLA")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kirius3", null, list.get(1)));
		}else{
			super.toTalk(pc, action, type, cbp);
		}
	}

}

package lineage.world.object.magic;

import lineage.bean.lineage.BuffInterface;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BuffMajok;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.world.controller.BuffController;
import lineage.world.object.object;
import lineage.world.object.magic.Magic;

public class Yahi_buff extends Magic {

	public Yahi_buff() {
		super(null, null);
	}

	static synchronized public BuffInterface clone(BuffInterface bi, int time) {
		if (bi == null)
			bi = new Yahi_buff();
		bi.setTime(time);
		return bi;
	}

	@Override
	public void toBuffStart(object o) {
		toBuffUpdate(o);
	}
	
	@Override
	public void toBuffUpdate(object o) {
		//
		o.toSender(S_BuffMajok.clone(BasePacketPooling.getPool(S_BuffMajok.class), 1, getTime()));
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 750), true);
		// 권한 부여: 거대한 마족 공격 가능
		o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 1127));
	}
	
	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o) {
		o.toSender(S_BuffMajok.clone(BasePacketPooling.getPool(S_BuffMajok.class), 1, 0));
	}

	static public void onBuff(object o, int time) {
		BuffController.append(o, Yahi_buff.clone(BuffController.getPool(Yahi_buff.class), time));
	}

}

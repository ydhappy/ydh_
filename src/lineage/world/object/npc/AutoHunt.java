package lineage.world.object.npc;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class AutoHunt extends object {


    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
			if (pc != null) {
				pc.showAutoHuntHtml();
			}
		}
	}


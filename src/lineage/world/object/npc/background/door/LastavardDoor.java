package lineage.world.object.npc.background.door;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Door;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;

public class LastavardDoor extends Door {

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		// 수동으로는 문을열 수 없음.
	}

	@Override
	public void toDoorSend(object o) {
		if ((getHeading() == 4) || (getHeading() == 0)) {
			for (int i = 0; i < 4; i++) {
				if (o == null) {
					toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x - i, y, heading, isDoorClose()), false);
					toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x - i, y - 1, heading, isDoorClose()), false);
					if (i != 0) {
						toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + i, y, heading, isDoorClose()), false);
						toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + i, y - 1, heading, isDoorClose()), false);
					}
				} else {
					o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x - i, y, heading, isDoorClose()));
					o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x - i, y - 1, heading, isDoorClose()));
					if (i != 0) {
						o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + i, y, heading, isDoorClose()));
						o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + i, y - 1, heading, isDoorClose()));
					}
				}

				int val = World.getDoorTile(this.x - i, this.y + 1, this.map, getHeading(), isDoorClose());
				World.set_map(this.x - i, this.y + 1, this.map, val);
				if (i != 0) {
					val = World.getDoorTile(this.x + i, this.y + 1, this.map, getHeading(), isDoorClose());
					World.set_map(this.x + i, this.y + 1, this.map, val);
				}
			}
		} else {
			for (int i = 0; i < 4; i++)
			{
				if (o == null) {
					toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y - i, heading, isDoorClose()), false);
					toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + 1, y - i, heading, isDoorClose()), false);
					if (i != 0) {
						toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y + i, heading, isDoorClose()), false);
						toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + 1, y + i, heading, isDoorClose()), false);
					}
				} else {
					o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y - i, heading, isDoorClose()));
					o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + 1, y - i, heading, isDoorClose()));
					if (i != 0) {
						o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y + i, heading, isDoorClose()));
						o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x + 1, y + i, heading, isDoorClose()));
					}
				}

				int val = World.getDoorTile(this.x - 1, this.y - i, this.map, getHeading(), isDoorClose());
				World.set_map(this.x - 1, this.y - i, this.map, val);
				if (i != 0) {
					val = World.getDoorTile(this.x - 1, this.y + i, this.map, getHeading(), isDoorClose());
					World.set_map(this.x - 1, this.y + i, this.map, val);
				}
			}
		}
	}

}

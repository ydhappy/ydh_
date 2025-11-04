package lineage.world.object.item;

import lineage.bean.lineage.Map;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class SilverFlute extends ItemInstance {
    
    private static final int ALLOWED_X = 32621;
    private static final int ALLOWED_Y = 33122;
    private static final int ALLOWED_MAP = 440;
    
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SilverFlute();
		return item;
	}
	
	private static boolean isMonsterSpawned = false;

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
	    int itemX = cha.getX();
	    int itemY = cha.getY();
	    int itemMap = cha.getMap();

		
	    if (cha.getInventory().find("친구의 가방") != null) {
	        ChattingController.toChatting(cha, "퀘스트를 완료하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }
	    
	    if (itemX != ALLOWED_X || itemY != ALLOWED_Y || itemMap != ALLOWED_MAP) {
	        ChattingController.toChatting(cha, "해당 위치에서는 아이템을 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }
	    
	    if (isMonsterSpawned) {
	        ChattingController.toChatting(cha, "이미 몬스터가 스폰되어있습니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }
	    cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), getItem().getEffect()), true);
	    
	    MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("라버본"));
	    Map m = World.get_map(cha.getMap());
	    if (m == null) {
	        return;
	    }

	    if (mi == null) {
	        ChattingController.toChatting(cha, "몬스터가 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
	        return;
	    }

	    mi.setHomeX(cha.getX());
	    mi.setHomeY(cha.getY());
	    mi.setHomeMap(cha.getMap());
	    mi.setHeading(Util.random(0, 7));

	    if (mi.getMonster().isHaste()) {
	        mi.setSpeed(1);
	    }

	    if (mi.getMonster().isBravery()) {
	        mi.setBrave(true);
	    }

	    int range = Util.random(2, 3);
	    int x = cha.getX();
	    int y = cha.getY();
	    int map = cha.getMap();
	    int x1 = m.locX1;
	    int x2 = m.locX2;
	    int y1 = m.locY1;
	    int y2 = m.locY2;
	    int lx = x;
	    int ly = y;

	    if (range > 1) {
	        int roop_cnt = 0;
	        do {
	            lx = Util.random(x - range < x1 ? x1 : x - range, x + range > x2 ? x2 : x + range);
	            ly = Util.random(y - range < y1 ? y1 : y - range, y + range > y2 ? y2 : y + range);
	            if (roop_cnt++ > 100) {
	                lx = x;
	                ly = y;
	                break;
	            }
	        } while (
	            !World.isThroughObject(lx, ly + 1, map, 0) || 
	            !World.isThroughObject(lx, ly - 1, map, 4) || 
	            !World.isThroughObject(lx - 1, ly, map, 2) || 
	            !World.isThroughObject(lx + 1, ly, map, 6) ||
	            !World.isThroughObject(lx - 1, ly + 1, map, 1) ||
	            !World.isThroughObject(lx + 1, ly - 1, map, 5) || 
	            !World.isThroughObject(lx + 1, ly + 1, map, 7) || 
	            !World.isThroughObject(lx - 1, ly - 1, map, 3) ||
	            World.isNotMovingTile(lx, ly, map)
	        );
	    }

	    mi.toTeleport(lx, ly, cha.getMap(), false);
	    AiThread.append(mi);
	    World.appendMonster(mi);

	    isMonsterSpawned = true;
	}

	public static void onMonsterDisappear(MonsterInstance mi) {
	    if (mi.getMonster().getName().equals("라버본")) {
	        isMonsterSpawned = false; 
	    }
	}
}
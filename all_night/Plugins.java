package all_night;

import java.util.StringTokenizer;

import lineage.bean.database.Skill;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.client.C_ItemClick;
import lineage.plugin.Plugin;
import lineage.share.Lineage;
import lineage.world.controller.AutoHuntCheckController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.FightController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.PcTradeController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Plugins implements Plugin {

	public Object init(Class<?> c, Object... opt) {

		if (c.isAssignableFrom(CommandController.class)) {
			if (opt[0].equals("toCommand")) {
				object o = (object) opt[1];
				String cmd = (String) opt[2];
				StringTokenizer st = (StringTokenizer) opt[3];

				if (FightController.isCommand(cmd))
					return FightController.toCommand(o, cmd, st);
				else
					return PcMarketController.toCommand(o, cmd, st);
			}
		}

		if (c.isAssignableFrom(ChattingController.class)) {
			if (opt[0].equals("toAutoHuntAnswer")) {
				PcInstance pc = (PcInstance) opt[1];
				String answer = (String) opt[2];

				if (Lineage.auto_hunt_monster_kill_count <= pc.getAutoHuntMonsterCount())
					return AutoHuntCheckController.checkMessage(pc, answer);
			} else if (opt[0].equals("swap")) {
				PcInstance pc = (PcInstance) opt[1];
				String key = (String) opt[2];

				if (pc.insertSwap(key))
					return true;
			}
		}

		if(c.isAssignableFrom(SkillController.class) ) {
			if(opt[0].equals("getSp")) {
				Character cha = (Character)opt[1];
				boolean packet = (boolean)opt[2];
				int sp = cha.getTotalSp();

				if(packet)
					return sp;
				switch(cha.getClassType()){
					case 0x00:
						if(cha.getLevel()<10)
							sp += 0;
						else if(cha.getLevel()<20)
							sp += 1;
						else
							sp += 2;
						break;
					case 0x01:
						if(cha.getLevel()<50)
							sp += 0;
						else
							sp += 1;
						break;
					case 0x02:
						if(cha.getLevel()<8)
							sp += 0;
						else if(cha.getLevel()<16)
							sp += 1;
						else if(cha.getLevel()<24)
							sp += 2;
						else if(cha.getLevel()<32)
							sp += 3;
						else if(cha.getLevel()<40)
							sp += 4;
						else if(cha.getLevel()<48)
							sp += 5;
						else
							sp += 6;
						break;
					case 0x03:
						if(cha.getLevel()<4)
							sp += 0;
						else if(cha.getLevel()<8)
							sp += 1;
						else if(cha.getLevel()<12)
							sp += 2;
						else if(cha.getLevel()<16)
							sp += 3;
						else if(cha.getLevel()<20)
							sp += 4;
						else if(cha.getLevel()<24)
							sp += 5;
						else if(cha.getLevel()<28)
							sp += 6;
						else if(cha.getLevel()<32)
							sp += 7;
						else if(cha.getLevel()<36)
							sp += 8;
						else if(cha.getLevel()<40)
							sp += 9;
						else if(cha.getLevel()<44)
							sp += 10;
						else if(cha.getLevel()<48)
							sp += 10;
						else if(cha.getLevel()<50)
							sp += 10;
						else
							sp += 10;
						break;
					case 0x04:
						if(cha.getLevel()>=12) sp += 1;
						if(cha.getLevel()>=24) sp += 1;
						break;
					case 0x05:
						if(cha.getLevel()>=20) sp += 1;
						if(cha.getLevel()>=40) sp += 1;
						break;
					case 0x06:
						if(cha.getLevel()>=6) sp += 1;
						if(cha.getLevel()>=12) sp += 1;
						if(cha.getLevel()>=18) sp += 1;
						if(cha.getLevel()>=24) sp += 1;
						if(cha.getLevel()>=30) sp += 1;
						if(cha.getLevel()>=36) sp += 1;
						if(cha.getLevel()>=42) sp += 1;
						if(cha.getLevel()>=48) sp += 1;
						break;
					default:
						if(cha.getLevel()<10)
							sp += 0;
						else if(cha.getLevel()<20)
							sp += 1;
						else
							sp += 2;
						break;
				}
				
				switch(cha.getTotalInt()){
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						sp += -1;
						break;
					case 9:
					case 10:
					case 11:
						sp += 0;
						break;
					case 12:
					case 13:
					case 14:
						sp += 1;
						break;
					case 15:
					case 16:
					case 17:
						sp += 2;
						break;
					case 18:
						sp += 3;
						break;
					default:
							sp += 3 + (cha.getTotalInt()-18);
						break;
				}
				return sp;
			}
		}
		
		if (c.isAssignableFrom(C_ItemClick.class)) {
			// 현금 거래 게시판.
			if (opt[0].equals("pcTrade")) {
				C_ItemClick cid = (C_ItemClick) opt[1];
				PcInstance pc = (PcInstance) opt[2];
				ItemInstance item = (ItemInstance) opt[3];

				if (PcTradeController.insertItemFinal(pc, item, item.getCount()))
					return true;
			} else if (opt[0].equals("pcShop")) {
				// 무인 상점.
				C_ItemClick cid = (C_ItemClick) opt[1];
				PcInstance pc = (PcInstance) opt[2];
				ItemInstance item = (ItemInstance) opt[3];

				if (PcMarketController.isShopToAppend(pc, item, item.getCount()))
					return true;
			}
		}

		return null;
	}

}

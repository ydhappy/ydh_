package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.System;
import lineage.world.controller.ExchangeController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ExchangeNpc extends object {

	private long lastSoundPlayTime = 0;
    
	public long getLastSoundPlayTime() {
		return lastSoundPlayTime;
	}

	public void setLastSoundPlayTime(long lastSoundPlayTime) {
		this.lastSoundPlayTime = lastSoundPlayTime;
	}

	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		long currentTime = System.currentTimeMillis(); 
		
	    if (pc != null && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock() && pc.getInventory() != null) {
	        if (pc.isItemClick()) {
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tradesys2"));
	            pc.setItemClick(false);
	        } else {
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tradesys3"));
	    		if (currentTime - getLastSoundPlayTime() >= 3000) {
	    			pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 27807));
	    			setLastSoundPlayTime(currentTime);
	    		}
	    	
	        }
	    } else {
	    }
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc == null) {
			return;
		}

		if (pc.isWorldDelete() || pc.isDead() || pc.isLock() || pc.getInventory() == null) {
			return;
		}

		try {
			if (!ExchangeController.isPcTradeState(pc)) {
				return;
			}

			pc.pc_trade_shop_step = 0;
			if (pc.pc_trade_shop_add_list != null) {
				pc.pc_trade_shop_add_list.clear();
			}

			if (action == null) {
				return;
			}

			if (action.equalsIgnoreCase("판매 등록")) {
				ExchangeController.appendHtml(pc);
			} else if (action.equalsIgnoreCase("판매 판매 가격 수정")) {
				ExchangeController.changeHtml(pc);
			} else if (action.equalsIgnoreCase("판매 정산")) {
				ExchangeController.판매정산(pc);
			} else if (action.equalsIgnoreCase("판매 정산 2")) {
				ExchangeController.판매정산2(pc);
			} else if (action.equalsIgnoreCase("판매 종료")) {
				ExchangeController.removeHtml(pc);
			} else if (action.contains("type_")) {
				String itemType = getType(action);
				if (itemType != null) {
					ExchangeController.buyHtml(pc, itemType);
				} else {
				}
			} else if (action.contains("but_list_")) {
				try {
					int idx = Integer.valueOf(action.replace("but_list_", ""));
					ExchangeController.buyHtml2(pc, idx);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getType(String action) {
		String type = null;

		action = action.replace("type_", "");

		switch (action) {
		case "무기":
			type = "weapon";
			break;
		case "티셔츠":
			type = "t";
			break;
		case "투구":
			type = "helm";
			break;
		case "갑옷":
			type = "armor";
			break;
		case "망토":
			type = "cloak";
			break;
		case "방패":
			type = "shield";
			break;
		case "가더":
			type = "guarder";
			break;
		case "장갑":
			type = "glove";
			break;
		case "부츠":
			type = "boot";
			break;
		case "장신구":
			type = "acc";
			break;
		case "기타":
			type = "etc";
			break;
		}

		return type;
	}
}
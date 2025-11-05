package lineage.world.object.npc.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Little extends ShopInstance {

	public Little(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Little"));
	}
}
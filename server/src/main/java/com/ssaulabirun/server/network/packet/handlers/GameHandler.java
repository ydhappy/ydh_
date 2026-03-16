package com.ssaulabirun.server.network.packet.handlers;

import com.ssaulabirun.server.db.dao.CharacterDao;
import com.ssaulabirun.server.game.character.L1NpcInstance;
import com.ssaulabirun.server.game.character.L1PcInstance;
import com.ssaulabirun.server.game.combat.CombatEngine;
import com.ssaulabirun.server.game.world.GameMap;
import com.ssaulabirun.server.game.world.L1World;
import com.ssaulabirun.server.network.ClientSession;
import com.ssaulabirun.server.network.packet.PacketReader;
import com.ssaulabirun.server.network.packet.PacketType;
import com.ssaulabirun.server.network.packet.PacketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameHandler.class);
    private static final CharacterDao charDao = new CharacterDao();

    public static void handleMove(ClientSession session, PacketReader reader) {
        L1PcInstance player = session.getPlayer();
        if (player == null) return;
        int newX = reader.readInt();
        int newY = reader.readInt();
        int heading = reader.readInt();

        GameMap map = L1World.getInstance().getMap(player.getMapId());
        if (map != null && map.canMove(newX, newY)) {
            map.removeChar(player.getId());
            player.setX(newX);
            player.setY(newY);
            player.setHeading(heading);
            map.addChar(player);
        }
    }

    public static void handleAttack(ClientSession session, PacketReader reader) {
        L1PcInstance player = session.getPlayer();
        if (player == null) return;
        long targetId = reader.readLong();
        boolean isNpc = reader.readByte() == 1;

        if (isNpc) {
            GameMap map = L1World.getInstance().getMap(player.getMapId());
            if (map == null) return;
            L1NpcInstance npc = map.getNpcs().get(targetId);
            if (npc == null || npc.isDead()) return;

            if (CombatEngine.calculateHit(player, npc)) {
                int damage = CombatEngine.calculateDamage(player, npc);
                npc.takeDamage(damage);

                PacketWriter dmgPw = new PacketWriter(PacketType.DAMAGE.getOpcode());
                dmgPw.writeLong(npc.getId());
                dmgPw.writeInt(damage);
                dmgPw.writeByte(npc.isAlive() ? 0 : 1);
                session.sendPacket(dmgPw);

                if (!npc.isAlive()) {
                    player.gainExp(npc.getTemplate().getExpReward());
                    player.setAdena(player.getAdena() + npc.getTemplate().getAdenaReward());
                    charDao.saveCharacter(player);
                    PacketWriter infoPw = new PacketWriter(PacketType.CHARACTER_INFO.getOpcode());
                    writeCharInfo(infoPw, player);
                    session.sendPacket(infoPw);
                }
            }
        }
    }

    public static void handleChat(ClientSession session, PacketReader reader) {
        L1PcInstance player = session.getPlayer();
        if (player == null) return;
        String message = reader.readString();
        String chatLine = "[" + player.getName() + "] " + message;

        PacketWriter pw = new PacketWriter(PacketType.CHAT.getOpcode());
        pw.writeString(player.getName());
        pw.writeString(message);

        for (ClientSession s : L1World.getInstance().getSessions().values()) {
            if (s.getPlayer() != null && s.getPlayer().getMapId() == player.getMapId()) {
                s.sendPacket(pw);
            }
        }
        logger.info("Chat: {}", chatLine);
    }

    public static void handleLogout(ClientSession session, PacketReader reader) {
        L1PcInstance player = session.getPlayer();
        if (player != null) {
            charDao.saveCharacter(player);
            L1World.getInstance().removePlayer(player.getId());
            session.setPlayer(null);
        }
        if (session.getAccountName() != null) {
            L1World.getInstance().removeSession(session.getAccountName());
        }
        PacketWriter pw = new PacketWriter(PacketType.DISCONNECT.getOpcode());
        session.sendPacket(pw);
        logger.info("Player logged out: {}", session.getAccountName());
    }

    private static void writeCharInfo(PacketWriter pw, L1PcInstance pc) {
        pw.writeLong(pc.getId());
        pw.writeString(pc.getName());
        pw.writeInt(pc.getCharClass().getId());
        pw.writeInt(pc.getLevel());
        pw.writeLong(pc.getExp());
        pw.writeInt(pc.getHp());
        pw.writeInt(pc.getMaxHp());
        pw.writeInt(pc.getMp());
        pw.writeInt(pc.getMaxMp());
        pw.writeLong(pc.getAdena());
    }
}

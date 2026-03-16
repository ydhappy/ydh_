package com.ssaulabirun.server.network.packet.handlers;

import com.ssaulabirun.server.config.ServerConfig;
import com.ssaulabirun.server.db.dao.CharacterDao;
import com.ssaulabirun.server.game.character.CharacterClass;
import com.ssaulabirun.server.game.character.L1PcInstance;
import com.ssaulabirun.server.network.ClientSession;
import com.ssaulabirun.server.network.packet.PacketReader;
import com.ssaulabirun.server.network.packet.PacketType;
import com.ssaulabirun.server.network.packet.PacketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CharacterHandler {
    private static final Logger logger = LoggerFactory.getLogger(CharacterHandler.class);
    private static final CharacterDao charDao = new CharacterDao();

    public static void handleCharList(ClientSession session, PacketReader reader) {
        if (!session.isAuthenticated()) return;
        List<L1PcInstance> chars = charDao.findByAccountName(session.getAccountName());
        PacketWriter pw = new PacketWriter(PacketType.CHAR_LIST_RESPONSE.getOpcode());
        pw.writeByte(chars.size());
        for (L1PcInstance pc : chars) {
            pw.writeLong(pc.getId());
            pw.writeString(pc.getName());
            pw.writeInt(pc.getCharClass().getId());
            pw.writeInt(pc.getLevel());
            pw.writeInt(pc.getHp());
            pw.writeInt(pc.getMaxHp());
        }
        session.sendPacket(pw);
    }

    public static void handleCharCreate(ClientSession session, PacketReader reader) {
        if (!session.isAuthenticated()) return;
        String charName = reader.readString();
        int classId = reader.readInt();

        PacketWriter pw = new PacketWriter(PacketType.CHAR_CREATE_RESPONSE.getOpcode());

        Optional<L1PcInstance> existing = charDao.findByName(charName);
        if (existing.isPresent()) {
            pw.writeByte(0);
            pw.writeString("이미 사용 중인 이름입니다.");
            session.sendPacket(pw);
            return;
        }

        CharacterClass cls = CharacterClass.getById(classId);
        ServerConfig cfg = ServerConfig.getInstance();

        L1PcInstance pc = new L1PcInstance();
        pc.setAccountName(session.getAccountName());
        pc.setName(charName);
        pc.setCharClass(cls);
        pc.setLevel(1);
        pc.setExp(0);
        pc.setStr(cls.getBaseStr());
        pc.setDex(cls.getBaseDex());
        pc.setCon(cls.getBaseCon());
        pc.setIntel(cls.getBaseInt());
        pc.setWis(cls.getBaseWis());
        pc.setCha(cls.getBaseCha());
        int conMod = (cls.getBaseCon() - 10) / 2;
        int intMod = (cls.getBaseInt() - 10) / 2;
        pc.setMaxHp(100 + conMod * 5);
        pc.setHp(pc.getMaxHp());
        pc.setMaxMp(50 + intMod * 5);
        pc.setMp(pc.getMaxMp());
        pc.setMapId(cfg.getStartMapId());
        pc.setX(cfg.getStartX());
        pc.setY(cfg.getStartY());
        pc.setAdena(100);

        boolean ok = charDao.createCharacter(pc);
        pw.writeByte(ok ? 1 : 0);
        pw.writeString(ok ? "캐릭터가 생성되었습니다!" : "캐릭터 생성에 실패했습니다.");
        session.sendPacket(pw);
    }

    public static void handleCharSelect(ClientSession session, PacketReader reader) {
        if (!session.isAuthenticated()) return;
        long charId = reader.readLong();

        List<L1PcInstance> chars = charDao.findByAccountName(session.getAccountName());
        L1PcInstance selected = null;
        for (L1PcInstance pc : chars) {
            if (pc.getId() == charId) {
                selected = pc;
                break;
            }
        }

        PacketWriter pw = new PacketWriter(PacketType.CHAR_SELECT_RESPONSE.getOpcode());
        if (selected == null) {
            pw.writeByte(0);
            pw.writeString("캐릭터를 찾을 수 없습니다.");
            session.sendPacket(pw);
            return;
        }

        session.setPlayer(selected);
        pw.writeByte(1);
        pw.writeString("월드에 접속합니다!");
        session.sendPacket(pw);
        logger.info("Character selected: {} for account {}", selected.getName(), session.getAccountName());
    }
}

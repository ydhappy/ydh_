package com.ssaulabirun.server.network.packet.handlers;

import com.ssaulabirun.server.db.dao.AccountDao;
import com.ssaulabirun.server.network.ClientSession;
import com.ssaulabirun.server.network.packet.PacketReader;
import com.ssaulabirun.server.network.packet.PacketType;
import com.ssaulabirun.server.network.packet.PacketWriter;
import com.ssaulabirun.server.game.world.L1World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private static final AccountDao accountDao = new AccountDao();

    public static void handle(ClientSession session, PacketReader reader) {
        String username = reader.readString();
        String password = reader.readString();
        logger.info("Login attempt: {}", username);

        Optional<Map<String, Object>> accountOpt = accountDao.findByUsername(username);
        PacketWriter pw = new PacketWriter(PacketType.LOGIN_RESPONSE.getOpcode());

        if (accountOpt.isEmpty()) {
            pw.writeByte(0);
            pw.writeString("계정을 찾을 수 없습니다.");
            session.sendPacket(pw);
            return;
        }

        Map<String, Object> account = accountOpt.get();
        String storedHash = (String) account.get("password_hash");
        String inputHash = AccountDao.hashPassword(password);

        if (!storedHash.equals(inputHash)) {
            pw.writeByte(0);
            pw.writeString("비밀번호가 틀렸습니다.");
            session.sendPacket(pw);
            return;
        }

        session.setAccountName(username);
        session.setAuthenticated(true);
        L1World.getInstance().addSession(username, session);
        accountDao.updateLastLogin(username);

        pw.writeByte(1);
        pw.writeString("로그인 성공!");
        session.sendPacket(pw);
        logger.info("User logged in: {}", username);
    }
}

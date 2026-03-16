package com.ssaulabirun.server.network;

import com.ssaulabirun.server.game.character.L1PcInstance;
import com.ssaulabirun.server.game.world.L1World;
import com.ssaulabirun.server.network.packet.PacketReader;
import com.ssaulabirun.server.network.packet.PacketType;
import com.ssaulabirun.server.network.packet.handlers.CharacterHandler;
import com.ssaulabirun.server.network.packet.handlers.GameHandler;
import com.ssaulabirun.server.network.packet.handlers.LoginHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(GameServerHandler.class);
    private static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("session");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ClientSession session = new ClientSession(ctx);
        ctx.channel().attr(SESSION_KEY).set(session);
        logger.info("New connection from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ClientSession session = ctx.channel().attr(SESSION_KEY).get();
        if (session != null) {
            L1PcInstance player = session.getPlayer();
            if (player != null) {
                L1World.getInstance().removePlayer(player.getId());
            }
            if (session.getAccountName() != null) {
                L1World.getInstance().removeSession(session.getAccountName());
            }
        }
        logger.info("Connection closed: {}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        ClientSession session = ctx.channel().attr(SESSION_KEY).get();
        if (session == null) return;

        if (msg.readableBytes() < 2) return;
        int opcode = msg.readShortLE() & 0xFFFF;
        byte[] payload = new byte[msg.readableBytes()];
        msg.readBytes(payload);
        PacketReader reader = new PacketReader(payload);

        PacketType type = PacketType.fromOpcode(opcode);
        if (type == null) {
            logger.warn("Unknown opcode: 0x{}", Integer.toHexString(opcode));
            return;
        }

        switch (type) {
            case LOGIN_REQUEST -> LoginHandler.handle(session, reader);
            case CHAR_LIST_REQUEST -> CharacterHandler.handleCharList(session, reader);
            case CHAR_CREATE_REQUEST -> CharacterHandler.handleCharCreate(session, reader);
            case CHAR_SELECT -> CharacterHandler.handleCharSelect(session, reader);
            case MOVE -> GameHandler.handleMove(session, reader);
            case ATTACK -> GameHandler.handleAttack(session, reader);
            case CHAT -> GameHandler.handleChat(session, reader);
            case LOGOUT -> GameHandler.handleLogout(session, reader);
            default -> logger.warn("Unhandled packet type: {}", type);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Channel exception", cause);
        ctx.close();
    }
}

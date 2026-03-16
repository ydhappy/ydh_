package com.ssaulabirun.server.network;

import com.ssaulabirun.server.game.character.L1PcInstance;
import com.ssaulabirun.server.network.packet.PacketWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ClientSession {
    private final ChannelHandlerContext ctx;
    private String accountName;
    private L1PcInstance player;
    private boolean authenticated;

    public ClientSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.authenticated = false;
    }

    public void sendPacket(PacketWriter pw) {
        byte[] data = pw.toByteArray();
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        ctx.writeAndFlush(buf);
    }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public L1PcInstance getPlayer() { return player; }
    public void setPlayer(L1PcInstance player) { this.player = player; }
    public ChannelHandlerContext getCtx() { return ctx; }
}

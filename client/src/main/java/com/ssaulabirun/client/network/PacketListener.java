package com.ssaulabirun.client.network;

public interface PacketListener {
    void onPacket(short opcode, PacketReader reader);
}

package com.ssaulabirun.server.network.packet;

public class Packet {
    private final short opcode;
    private final byte[] data;

    public Packet(short opcode, byte[] data) {
        this.opcode = opcode;
        this.data = data;
    }

    public short getOpcode() {
        return opcode;
    }

    public byte[] getData() {
        return data;
    }
}

package com.ssaulabirun.server.network.packet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class PacketWriter {
    private final ByteArrayOutputStream baos;
    private final int opcode;

    public PacketWriter(int opcode) {
        this.opcode = opcode;
        this.baos = new ByteArrayOutputStream();
    }

    public void writeByte(int value) {
        baos.write(value & 0xFF);
    }

    public void writeShort(int value) {
        baos.write(value & 0xFF);
        baos.write((value >> 8) & 0xFF);
    }

    public void writeInt(int value) {
        baos.write(value & 0xFF);
        baos.write((value >> 8) & 0xFF);
        baos.write((value >> 16) & 0xFF);
        baos.write((value >> 24) & 0xFF);
    }

    public void writeLong(long value) {
        writeInt((int) (value & 0xFFFFFFFFL));
        writeInt((int) ((value >> 32) & 0xFFFFFFFFL));
    }

    public void writeString(String value) {
        if (value == null) value = "";
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeShort(bytes.length);
        baos.write(bytes, 0, bytes.length);
    }

    /**
     * Returns the completed packet: 2-byte little-endian total length (including the 2-byte opcode),
     * 2-byte little-endian opcode, then payload bytes.
     */
    public byte[] toByteArray() {
        byte[] payload = baos.toByteArray();
        // total content = 2 (opcode) + payload
        int contentLen = 2 + payload.length;
        byte[] result = new byte[2 + contentLen];
        // length prefix (little-endian)
        result[0] = (byte) (contentLen & 0xFF);
        result[1] = (byte) ((contentLen >> 8) & 0xFF);
        // opcode (little-endian)
        result[2] = (byte) (opcode & 0xFF);
        result[3] = (byte) ((opcode >> 8) & 0xFF);
        System.arraycopy(payload, 0, result, 4, payload.length);
        return result;
    }
}

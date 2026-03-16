package com.ssaulabirun.client.network;

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

    public byte[] toByteArray() {
        byte[] payload = baos.toByteArray();
        int contentLen = 2 + payload.length;
        byte[] result = new byte[2 + contentLen];
        result[0] = (byte) (contentLen & 0xFF);
        result[1] = (byte) ((contentLen >> 8) & 0xFF);
        result[2] = (byte) (opcode & 0xFF);
        result[3] = (byte) ((opcode >> 8) & 0xFF);
        System.arraycopy(payload, 0, result, 4, payload.length);
        return result;
    }
}

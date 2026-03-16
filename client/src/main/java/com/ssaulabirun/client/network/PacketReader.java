package com.ssaulabirun.client.network;

import java.nio.charset.StandardCharsets;

public class PacketReader {
    private final byte[] buffer;
    private int position;

    public PacketReader(byte[] buffer) {
        this.buffer = buffer;
        this.position = 0;
    }

    public byte readByte() {
        return buffer[position++];
    }

    public short readShort() {
        int b0 = buffer[position++] & 0xFF;
        int b1 = buffer[position++] & 0xFF;
        return (short) ((b1 << 8) | b0);
    }

    public int readInt() {
        int b0 = buffer[position++] & 0xFF;
        int b1 = buffer[position++] & 0xFF;
        int b2 = buffer[position++] & 0xFF;
        int b3 = buffer[position++] & 0xFF;
        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public long readLong() {
        long lo = readInt() & 0xFFFFFFFFL;
        long hi = readInt() & 0xFFFFFFFFL;
        return (hi << 32) | lo;
    }

    public String readString() {
        short len = readShort();
        if (len <= 0) return "";
        String s = new String(buffer, position, len, StandardCharsets.UTF_8);
        position += len;
        return s;
    }

    public int remaining() {
        return buffer.length - position;
    }
}

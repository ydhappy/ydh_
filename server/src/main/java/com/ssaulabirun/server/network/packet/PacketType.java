package com.ssaulabirun.server.network.packet;

public enum PacketType {
    LOGIN_REQUEST(0x01),
    LOGIN_RESPONSE(0x02),
    CHAR_LIST_REQUEST(0x03),
    CHAR_LIST_RESPONSE(0x04),
    CHAR_CREATE_REQUEST(0x05),
    CHAR_CREATE_RESPONSE(0x06),
    CHAR_SELECT(0x07),
    CHAR_SELECT_RESPONSE(0x08),
    ENTER_WORLD(0x09),
    MOVE(0x0A),
    ATTACK(0x0B),
    CHAT(0x0C),
    LOGOUT(0x0D),
    SERVER_MESSAGE(0x0E),
    CHARACTER_INFO(0x0F),
    NPC_INFO(0x10),
    ITEM_INFO(0x11),
    DAMAGE(0x12),
    DISCONNECT(0xFF);

    private final int opcode;

    PacketType(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    public static PacketType fromOpcode(int opcode) {
        for (PacketType type : values()) {
            if (type.opcode == opcode) {
                return type;
            }
        }
        return null;
    }
}

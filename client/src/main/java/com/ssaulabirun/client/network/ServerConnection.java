package com.ssaulabirun.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerConnection {
    private static final Logger logger = LoggerFactory.getLogger(ServerConnection.class);

    private final String host;
    private final int port;
    private Socket socket;
    private OutputStream outputStream;
    private final List<PacketListener> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean running;

    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();
            running = true;
            Thread reader = new Thread(this::readLoop, "packet-reader");
            reader.setDaemon(true);
            reader.start();
            logger.info("서버에 연결되었습니다: {}:{}", host, port);
            return true;
        } catch (IOException e) {
            logger.error("서버 연결 실패", e);
            return false;
        }
    }

    private void readLoop() {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            while (running) {
                // Read 2-byte little-endian length
                int lo = dis.read();
                if (lo < 0) break;
                int hi = dis.read();
                if (hi < 0) break;
                int length = lo | (hi << 8);
                if (length < 2) continue;

                byte[] frame = new byte[length];
                dis.readFully(frame);

                // First 2 bytes = opcode (little-endian)
                short opcode = (short) ((frame[0] & 0xFF) | ((frame[1] & 0xFF) << 8));
                byte[] payload = new byte[length - 2];
                System.arraycopy(frame, 2, payload, 0, payload.length);
                PacketReader reader = new PacketReader(payload);

                for (PacketListener l : listeners) {
                    try {
                        l.onPacket(opcode, reader);
                    } catch (Exception e) {
                        logger.error("Listener error", e);
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                logger.error("연결 끊김", e);
            }
        }
        running = false;
    }

    public void sendPacket(byte[] data) {
        if (outputStream == null || !running) return;
        try {
            synchronized (outputStream) {
                outputStream.write(data);
                outputStream.flush();
            }
        } catch (IOException e) {
            logger.error("패킷 전송 실패", e);
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.error("연결 종료 오류", e);
        }
    }

    public void addListener(PacketListener listener) {
        listeners.add(listener);
    }

    public boolean isConnected() {
        return running && socket != null && socket.isConnected();
    }
}

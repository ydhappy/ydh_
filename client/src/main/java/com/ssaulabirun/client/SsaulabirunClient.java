package com.ssaulabirun.client;

import com.ssaulabirun.client.config.ClientConfig;
import com.ssaulabirun.client.network.ServerConnection;
import com.ssaulabirun.client.ui.LoginFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class SsaulabirunClient {
    private static final Logger logger = LoggerFactory.getLogger(SsaulabirunClient.class);

    public static void main(String[] args) {
        logger.info("싸울아비 클라이언트 시작");
        ClientConfig config = ClientConfig.getInstance();

        ServerConnection connection = new ServerConnection(config.getServerHost(), config.getServerPort());
        boolean connected = connection.connect();

        SwingUtilities.invokeLater(() -> {
            if (!connected) {
                JOptionPane.showMessageDialog(null,
                        "서버에 연결할 수 없습니다.\n" + config.getServerHost() + ":" + config.getServerPort(),
                        "연결 오류", JOptionPane.ERROR_MESSAGE);
            }
            LoginFrame loginFrame = new LoginFrame(connection);
            loginFrame.setVisible(true);
        });
    }
}

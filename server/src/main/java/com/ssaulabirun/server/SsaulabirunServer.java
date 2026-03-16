package com.ssaulabirun.server;

import com.ssaulabirun.server.config.ServerConfig;
import com.ssaulabirun.server.db.DatabaseManager;
import com.ssaulabirun.server.game.world.L1World;
import com.ssaulabirun.server.generator.ResourceGenerator;
import com.ssaulabirun.server.network.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SsaulabirunServer {
    private static final Logger logger = LoggerFactory.getLogger(SsaulabirunServer.class);

    public static void main(String[] args) throws Exception {
        logger.info("싸울아비 서버가 시작되었습니다");

        ServerConfig config = ServerConfig.getInstance();
        logger.info("서버 버전: {}, 포트: {}", config.getVersion(), config.getPort());

        DatabaseManager.getInstance();
        logger.info("데이터베이스 초기화 완료");

        L1World.getInstance();

        ResourceGenerator generator = new ResourceGenerator();
        generator.generate();

        GameServer server = new GameServer(config.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("서버 종료 중...");
            server.stop();
            DatabaseManager.getInstance().close();
        }));

        server.start();
    }
}

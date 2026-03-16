package com.ssaulabirun.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);
    private static ServerConfig instance;

    private int port;
    private String name;
    private int maxPlayers;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private int startMapId;
    private int startX;
    private int startY;
    private String version;

    private ServerConfig() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("server.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                logger.warn("server.properties not found in classpath, using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load server.properties", e);
        }
        port = Integer.parseInt(props.getProperty("server.port", "7777"));
        name = props.getProperty("server.name", "Ssaulabirun");
        maxPlayers = Integer.parseInt(props.getProperty("server.maxPlayers", "1000"));
        dbUrl = props.getProperty("db.url", "jdbc:h2:./data/ssaulabirun");
        dbUser = props.getProperty("db.user", "ssaulabirun");
        dbPassword = props.getProperty("db.password", "ssaulabirun2024");
        startMapId = Integer.parseInt(props.getProperty("world.startMapId", "1"));
        startX = Integer.parseInt(props.getProperty("world.startX", "100"));
        startY = Integer.parseInt(props.getProperty("world.startY", "100"));
        version = props.getProperty("server.version", "1.0.0");
    }

    public static synchronized ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
        }
        return instance;
    }

    public int getPort() { return port; }
    public String getName() { return name; }
    public int getMaxPlayers() { return maxPlayers; }
    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    public int getStartMapId() { return startMapId; }
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public String getVersion() { return version; }
}

package com.ssaulabirun.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(ClientConfig.class);
    private static ClientConfig instance;

    private String serverHost;
    private int serverPort;
    private int windowWidth;
    private int windowHeight;
    private String version;

    private ClientConfig() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("client.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                logger.warn("client.properties not found, using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load client.properties", e);
        }
        serverHost = props.getProperty("server.host", "localhost");
        serverPort = Integer.parseInt(props.getProperty("server.port", "7777"));
        windowWidth = Integer.parseInt(props.getProperty("window.width", "1024"));
        windowHeight = Integer.parseInt(props.getProperty("window.height", "768"));
        version = props.getProperty("client.version", "1.0.0");
    }

    public static synchronized ClientConfig getInstance() {
        if (instance == null) {
            instance = new ClientConfig();
        }
        return instance;
    }

    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }
    public String getVersion() { return version; }
}

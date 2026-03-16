package com.ssaulabirun.server.db;

import com.ssaulabirun.server.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.stream.Collectors;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseManager() {
        ServerConfig config = ServerConfig.getInstance();
        this.url = config.getDbUrl();
        this.user = config.getDbUser();
        this.password = config.getDbPassword();
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 driver not found", e);
        }
        initSchema();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initSchema() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sql/schema.sql")) {
            if (is == null) {
                logger.warn("schema.sql not found");
                return;
            }
            String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
            logger.info("Database schema initialized");
        } catch (IOException | SQLException e) {
            logger.error("Failed to initialize schema", e);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    public void close() {
        // No pool to close for simple DriverManager usage
    }
}

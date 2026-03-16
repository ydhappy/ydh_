package com.ssaulabirun.server.db.dao;

import com.ssaulabirun.server.db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountDao {
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    public Optional<Map<String, Object>> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, email, created_at, last_login FROM accounts WHERE username = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("username", rs.getString("username"));
                    row.put("password_hash", rs.getString("password_hash"));
                    row.put("email", rs.getString("email"));
                    row.put("created_at", rs.getTimestamp("created_at"));
                    row.put("last_login", rs.getTimestamp("last_login"));
                    return Optional.of(row);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding account by username", e);
        }
        return Optional.empty();
    }

    public boolean createAccount(String username, String password, String email) {
        String hash = hashPassword(password);
        String sql = "INSERT INTO accounts (username, password_hash, email) VALUES (?, ?, ?)";
        try {
            DatabaseManager.getInstance().executeUpdate(sql, username, hash, email);
            return true;
        } catch (SQLException e) {
            logger.error("Error creating account", e);
            return false;
        }
    }

    public void updateLastLogin(String username) {
        String sql = "UPDATE accounts SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        try {
            DatabaseManager.getInstance().executeUpdate(sql, username);
        } catch (SQLException e) {
            logger.error("Error updating last login", e);
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}

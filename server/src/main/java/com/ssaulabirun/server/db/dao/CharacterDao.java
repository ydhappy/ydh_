package com.ssaulabirun.server.db.dao;

import com.ssaulabirun.server.db.DatabaseManager;
import com.ssaulabirun.server.game.character.CharacterClass;
import com.ssaulabirun.server.game.character.L1PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class CharacterDao {
    private static final Logger logger = LoggerFactory.getLogger(CharacterDao.class);

    public List<L1PcInstance> findByAccountName(String accountName) {
        List<L1PcInstance> list = new ArrayList<>();
        String sql = "SELECT * FROM characters WHERE account_name = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding characters for account", e);
        }
        return list;
    }

    public Optional<L1PcInstance> findByName(String charName) {
        String sql = "SELECT * FROM characters WHERE char_name = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, charName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding character by name", e);
        }
        return Optional.empty();
    }

    public boolean createCharacter(L1PcInstance pc) {
        String sql = "INSERT INTO characters (account_name, char_name, class_id, level, exp, hp, mp, " +
                "str, dex, con, intel, wis, cha, map_id, x, y, adena, magic_circle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pc.getAccountName());
            ps.setString(2, pc.getName());
            ps.setInt(3, pc.getCharClass().getId());
            ps.setInt(4, pc.getLevel());
            ps.setLong(5, pc.getExp());
            ps.setInt(6, pc.getHp());
            ps.setInt(7, pc.getMp());
            ps.setInt(8, pc.getStr());
            ps.setInt(9, pc.getDex());
            ps.setInt(10, pc.getCon());
            ps.setInt(11, pc.getIntel());
            ps.setInt(12, pc.getWis());
            ps.setInt(13, pc.getCha());
            ps.setInt(14, pc.getMapId());
            ps.setInt(15, pc.getX());
            ps.setInt(16, pc.getY());
            ps.setLong(17, pc.getAdena());
            ps.setInt(18, pc.getMagicCircle());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pc.setId(keys.getLong(1));
                }
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error creating character", e);
            return false;
        }
    }

    public boolean saveCharacter(L1PcInstance pc) {
        String sql = "UPDATE characters SET level=?, exp=?, hp=?, mp=?, str=?, dex=?, con=?, " +
                "intel=?, wis=?, cha=?, map_id=?, x=?, y=?, adena=?, magic_circle=? WHERE id=?";
        try {
            DatabaseManager.getInstance().executeUpdate(sql,
                    pc.getLevel(), pc.getExp(), pc.getHp(), pc.getMp(),
                    pc.getStr(), pc.getDex(), pc.getCon(), pc.getIntel(),
                    pc.getWis(), pc.getCha(), pc.getMapId(), pc.getX(), pc.getY(),
                    pc.getAdena(), pc.getMagicCircle(), pc.getId());
            return true;
        } catch (SQLException e) {
            logger.error("Error saving character", e);
            return false;
        }
    }

    public boolean deleteCharacter(long charId) {
        try {
            DatabaseManager.getInstance().executeUpdate("DELETE FROM characters WHERE id=?", charId);
            return true;
        } catch (SQLException e) {
            logger.error("Error deleting character", e);
            return false;
        }
    }

    private L1PcInstance mapRow(ResultSet rs) throws SQLException {
        L1PcInstance pc = new L1PcInstance();
        pc.setId(rs.getLong("id"));
        pc.setAccountName(rs.getString("account_name"));
        pc.setName(rs.getString("char_name"));
        pc.setCharClass(CharacterClass.getById(rs.getInt("class_id")));
        pc.setLevel(rs.getInt("level"));
        pc.setExp(rs.getLong("exp"));
        int savedHp = rs.getInt("hp");
        int savedMp = rs.getInt("mp");
        int conMod = (rs.getInt("con") - 10) / 2;
        int intMod = (rs.getInt("intel") - 10) / 2;
        int calcMaxHp = 100 + conMod * 5 + (rs.getInt("level") - 1) * (10 + conMod);
        int calcMaxMp = 50 + intMod * 5 + (rs.getInt("level") - 1) * (5 + intMod);
        pc.setMaxHp(Math.max(calcMaxHp, savedHp));
        pc.setHp(Math.min(savedHp, pc.getMaxHp()));
        pc.setMaxMp(Math.max(calcMaxMp, savedMp));
        pc.setMp(Math.min(savedMp, pc.getMaxMp()));
        pc.setStr(rs.getInt("str"));
        pc.setDex(rs.getInt("dex"));
        pc.setCon(rs.getInt("con"));
        pc.setIntel(rs.getInt("intel"));
        pc.setWis(rs.getInt("wis"));
        pc.setCha(rs.getInt("cha"));
        pc.setMapId(rs.getInt("map_id"));
        pc.setX(rs.getInt("x"));
        pc.setY(rs.getInt("y"));
        pc.setAdena(rs.getLong("adena"));
        pc.setMagicCircle(rs.getInt("magic_circle"));
        return pc;
    }
}

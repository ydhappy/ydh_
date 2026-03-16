package com.ssaulabirun.client.ui;

import com.ssaulabirun.client.network.PacketType;
import com.ssaulabirun.client.network.PacketWriter;
import com.ssaulabirun.client.network.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameFrame extends JFrame {
    private static final Color BG = new Color(0x1a1a2e);
    private static final Color FG = new Color(0xe0e0e0);
    private static final Color ACCENT = new Color(0xc0392b);

    private final ServerConnection connection;

    // Player state
    private String playerName = "플레이어";
    private String playerClass = "없음";
    private int playerLevel = 1;
    private int playerHp = 100, playerMaxHp = 100;
    private int playerMp = 50, playerMaxMp = 50;
    private int playerStr = 10, playerDex = 10, playerCon = 10;
    private int playerInt = 10, playerWis = 10, playerCha = 10;
    private long playerAdena = 0;
    private int playerX = 100, playerY = 100;

    // NPC list (id, x, y, name)
    private static class NpcInfo { long id; int x, y; String name; }
    private final List<NpcInfo> npcs = new CopyOnWriteArrayList<>();

    // Damage numbers (x, y, value, ttl)
    private static class DamageNum { int x, y; int val; int ttl; }
    private final List<DamageNum> damageNums = new CopyOnWriteArrayList<>();

    // UI components
    private GameCanvas gameCanvas;
    private MinimapPanel minimapPanel;
    private JLabel nameLabel, classLabel, levelLabel;
    private JProgressBar hpBar, mpBar;
    private JLabel[] statLabels;
    private JTextArea chatArea;
    private JTextField chatInput;

    public GameFrame(ServerConnection connection) {
        this.connection = connection;
        setTitle("싸울아비 - Ssaulabirun");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        setupPacketListener();
        startGameLoop();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(4, 4));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Left: minimap (150px)
        minimapPanel = new MinimapPanel();
        minimapPanel.setPreferredSize(new Dimension(150, 500));
        minimapPanel.setBackground(new Color(0x0f3460));
        root.add(minimapPanel, BorderLayout.WEST);

        // Center: game canvas
        gameCanvas = new GameCanvas();
        gameCanvas.setPreferredSize(new Dimension(620, 500));
        gameCanvas.setBackground(Color.BLACK);
        gameCanvas.setFocusable(true);
        gameCanvas.addKeyListener(new GameKeyListener());
        root.add(gameCanvas, BorderLayout.CENTER);

        // Right: char info (200px)
        JPanel infoPanel = buildInfoPanel();
        infoPanel.setPreferredSize(new Dimension(200, 500));
        root.add(infoPanel, BorderLayout.EAST);

        // Bottom: chat (150px)
        JPanel chatPanel = buildChatPanel();
        chatPanel.setPreferredSize(new Dimension(1024, 150));
        root.add(chatPanel, BorderLayout.SOUTH);

        setContentPane(root);

        SwingUtilities.invokeLater(() -> gameCanvas.requestFocusInWindow());
    }

    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0x16213e));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        nameLabel = styledLabel(playerName, Font.BOLD, 16);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(4));

        classLabel = styledLabel("직업: " + playerClass, Font.PLAIN, 12);
        panel.add(classLabel);
        levelLabel = styledLabel("레벨: " + playerLevel, Font.PLAIN, 12);
        panel.add(levelLabel);
        panel.add(Box.createVerticalStrut(8));

        JLabel hpLabel = styledLabel("HP", Font.BOLD, 11);
        panel.add(hpLabel);
        hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setForeground(new Color(0xe74c3c));
        hpBar.setBackground(new Color(0x2c3e50));
        hpBar.setStringPainted(true);
        hpBar.setString(playerHp + "/" + playerMaxHp);
        hpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        panel.add(hpBar);
        panel.add(Box.createVerticalStrut(4));

        JLabel mpLabel = styledLabel("MP", Font.BOLD, 11);
        panel.add(mpLabel);
        mpBar = new JProgressBar(0, 100);
        mpBar.setValue(100);
        mpBar.setForeground(new Color(0x2980b9));
        mpBar.setBackground(new Color(0x2c3e50));
        mpBar.setStringPainted(true);
        mpBar.setString(playerMp + "/" + playerMaxMp);
        mpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        panel.add(mpBar);
        panel.add(Box.createVerticalStrut(10));

        // Stats table
        String[] statNames = {"힘", "민첩", "체력", "지능", "지혜", "매력"};
        statLabels = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(new Color(0x16213e));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            row.add(styledLabel(statNames[i], Font.PLAIN, 12), BorderLayout.WEST);
            statLabels[i] = styledLabel("10", Font.BOLD, 12);
            row.add(statLabels[i], BorderLayout.EAST);
            panel.add(row);
        }
        panel.add(Box.createVerticalStrut(8));

        JLabel adenaLabel = styledLabel("아데나: " + playerAdena, Font.PLAIN, 12);
        adenaLabel.setName("adena");
        panel.add(adenaLabel);

        return panel;
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(new Color(0x16213e));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(0x0f3460));
        chatArea.setForeground(FG);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputRow = new JPanel(new BorderLayout(4, 0));
        inputRow.setBackground(new Color(0x16213e));

        chatInput = new JTextField();
        chatInput.setBackground(new Color(0x0f3460));
        chatInput.setForeground(FG);
        chatInput.setCaretColor(FG);
        chatInput.setBorder(BorderFactory.createLineBorder(new Color(0x2c3e50)));
        chatInput.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chatInput.addActionListener(e -> sendChat());
        inputRow.add(chatInput, BorderLayout.CENTER);

        JButton sendBtn = new JButton("전송");
        sendBtn.setBackground(ACCENT);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        sendBtn.addActionListener(e -> sendChat());
        inputRow.add(sendBtn, BorderLayout.EAST);
        panel.add(inputRow, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel styledLabel(String text, int style, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("SansSerif", style, size));
        return l;
    }

    private void sendChat() {
        String text = chatInput.getText().trim();
        if (text.isEmpty()) return;
        PacketWriter pw = new PacketWriter(PacketType.CHAT.getOpcode());
        pw.writeString(text);
        connection.sendPacket(pw.toByteArray());
        chatInput.setText("");
    }

    private void updateCharInfo() {
        SwingUtilities.invokeLater(() -> {
            nameLabel.setText(playerName);
            classLabel.setText("직업: " + playerClass);
            levelLabel.setText("레벨: " + playerLevel);
            hpBar.setMaximum(playerMaxHp);
            hpBar.setValue(playerHp);
            hpBar.setString(playerHp + "/" + playerMaxHp);
            mpBar.setMaximum(playerMaxMp);
            mpBar.setValue(playerMp);
            mpBar.setString(playerMp + "/" + playerMaxMp);
            if (statLabels != null) {
                int[] vals = {playerStr, playerDex, playerCon, playerInt, playerWis, playerCha};
                for (int i = 0; i < 6; i++) statLabels[i].setText(String.valueOf(vals[i]));
            }
        });
    }

    private void addChatLine(String line) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(line + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void startGameLoop() {
        Timer timer = new Timer(33, e -> {
            // tick damage numbers
            damageNums.removeIf(d -> --d.ttl <= 0);
            gameCanvas.repaint();
            minimapPanel.repaint();
        });
        timer.start();
    }

    private void setupPacketListener() {
        connection.addListener((opcode, reader) -> {
            PacketType type = PacketType.fromOpcode(opcode & 0xFFFF);
            if (type == null) return;
            switch (type) {
                case CHARACTER_INFO -> {
                    long id = reader.readLong();
                    playerName = reader.readString();
                    int classId = reader.readInt();
                    playerClass = "클래스" + classId;
                    playerLevel = reader.readInt();
                    long exp = reader.readLong();
                    playerHp = reader.readInt();
                    playerMaxHp = reader.readInt();
                    playerMp = reader.readInt();
                    playerMaxMp = reader.readInt();
                    if (reader.remaining() >= 4) playerStr = reader.readInt();
                    if (reader.remaining() >= 4) playerDex = reader.readInt();
                    if (reader.remaining() >= 4) playerCon = reader.readInt();
                    if (reader.remaining() >= 4) playerInt = reader.readInt();
                    if (reader.remaining() >= 4) playerWis = reader.readInt();
                    if (reader.remaining() >= 4) playerCha = reader.readInt();
                    if (reader.remaining() >= 4) { int mapId = reader.readInt(); }
                    if (reader.remaining() >= 4) playerX = reader.readInt();
                    if (reader.remaining() >= 4) playerY = reader.readInt();
                    if (reader.remaining() >= 8) playerAdena = reader.readLong();
                    updateCharInfo();
                }
                case NPC_INFO -> {
                    long npcId = reader.readLong();
                    String name = reader.readString();
                    int nx = reader.readInt();
                    int ny = reader.readInt();
                    NpcInfo info = new NpcInfo();
                    info.id = npcId; info.name = name; info.x = nx; info.y = ny;
                    npcs.removeIf(n -> n.id == npcId);
                    npcs.add(info);
                }
                case DAMAGE -> {
                    long targetId = reader.readLong();
                    int damage = reader.readInt();
                    DamageNum dn = new DamageNum();
                    dn.x = playerX + (int)(Math.random() * 20 - 10);
                    dn.y = playerY - 20;
                    dn.val = damage;
                    dn.ttl = 40;
                    damageNums.add(dn);
                }
                case CHAT -> {
                    String who = reader.readString();
                    String msg = reader.readString();
                    addChatLine("[" + who + "] " + msg);
                }
                case SERVER_MESSAGE -> {
                    String msg = reader.readString();
                    addChatLine("[시스템] " + msg);
                }
                case DISCONNECT -> {
                    addChatLine("[시스템] 서버와의 연결이 끊어졌습니다.");
                }
                default -> {}
            }
        });
    }

    // Inner game canvas
    private class GameCanvas extends JPanel {
        private static final int TILE_SIZE = 16;
        private static final int VIEW_W = 620;
        private static final int VIEW_H = 500;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int camX = playerX - VIEW_W / (2 * TILE_SIZE);
            int camY = playerY - VIEW_H / (2 * TILE_SIZE);

            // Draw tiles
            for (int tx = 0; tx < VIEW_W / TILE_SIZE + 2; tx++) {
                for (int ty = 0; ty < VIEW_H / TILE_SIZE + 2; ty++) {
                    int worldX = camX + tx;
                    int worldY = camY + ty;
                    int screenX = tx * TILE_SIZE;
                    int screenY = ty * TILE_SIZE;
                    // Simple noise-based coloring
                    int noise = ((worldX * 7 + worldY * 13) & 0xF);
                    Color tileColor = new Color(
                            30 + noise, 80 + noise * 2, 30 + noise);
                    g2.setColor(tileColor);
                    g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    g2.setColor(new Color(20, 60, 20));
                    g2.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                }
            }

            int centerScreenX = (playerX - camX) * TILE_SIZE;
            int centerScreenY = (playerY - camY) * TILE_SIZE;

            // Draw NPCs
            for (NpcInfo npc : npcs) {
                int sx = (npc.x - camX) * TILE_SIZE;
                int sy = (npc.y - camY) * TILE_SIZE;
                if (sx >= -TILE_SIZE && sx < VIEW_W + TILE_SIZE && sy >= -TILE_SIZE && sy < VIEW_H + TILE_SIZE) {
                    g2.setColor(new Color(0xc0392b));
                    g2.fillRect(sx + 1, sy + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                    g2.setColor(FG);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.drawString(npc.name.length() > 6 ? npc.name.substring(0, 6) : npc.name, sx - 2, sy - 2);
                }
            }

            // Draw player (white circle)
            g2.setColor(Color.WHITE);
            g2.fillOval(centerScreenX + 1, centerScreenY + 1, TILE_SIZE - 2, TILE_SIZE - 2);
            g2.setColor(FG);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.drawString(playerName.length() > 8 ? playerName.substring(0, 8) : playerName,
                    centerScreenX - 4, centerScreenY - 2);

            // Draw damage numbers
            for (DamageNum dn : damageNums) {
                int sx = (dn.x - camX) * TILE_SIZE;
                int sy = (dn.y - camY) * TILE_SIZE - (40 - dn.ttl) * 2;
                float alpha = dn.ttl / 40f;
                g2.setColor(new Color(1f, 0.3f, 0.3f, alpha));
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString(String.valueOf(dn.val), sx, sy);
            }

            // Coordinates HUD
            g2.setColor(new Color(FG.getRed(), FG.getGreen(), FG.getBlue(), 180));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("위치: (" + playerX + ", " + playerY + ")", 4, VIEW_H - 4);
        }
    }

    // Minimap panel
    private class MinimapPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(0x0f3460));
            g2.fillRect(0, 0, w, h);

            int scale = 1; // 1 pixel per tile in minimap for a portion of the 200x200 map
            int viewRange = 50;
            int offsetX = playerX - viewRange / 2;
            int offsetY = playerY - viewRange / 2;

            // Draw map area
            for (int tx = 0; tx < viewRange; tx++) {
                for (int ty = 0; ty < viewRange; ty++) {
                    int px = tx * (w / viewRange);
                    int py = ty * (h / viewRange);
                    int cellW = w / viewRange;
                    int cellH = h / viewRange;
                    int noise = (((offsetX + tx) * 7 + (offsetY + ty) * 13) & 0xF);
                    g2.setColor(new Color(20 + noise, 50 + noise, 20 + noise));
                    g2.fillRect(px, py, cellW, cellH);
                }
            }

            // Draw NPCs on minimap
            g2.setColor(new Color(0xc0392b));
            for (NpcInfo npc : npcs) {
                int dx = npc.x - offsetX;
                int dy = npc.y - offsetY;
                if (dx >= 0 && dx < viewRange && dy >= 0 && dy < viewRange) {
                    int px = dx * (w / viewRange) + (w / viewRange) / 2;
                    int py = dy * (h / viewRange) + (h / viewRange) / 2;
                    g2.fillRect(px - 1, py - 1, 3, 3);
                }
            }

            // Draw player on minimap
            int cx = (playerX - offsetX) * (w / viewRange) + (w / viewRange) / 2;
            int cy = (playerY - offsetY) * (h / viewRange) + (h / viewRange) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(cx - 3, cy - 3, 6, 6);

            // Label
            g2.setColor(FG);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.drawString("미니맵", w / 2 - 18, 12);
        }
    }

    // Keyboard input
    private class GameKeyListener extends KeyAdapter {
        private static final int MOVE_STEP = 1;

        @Override
        public void keyPressed(KeyEvent e) {
            int newX = playerX, newY = playerY, heading = 0;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W, KeyEvent.VK_UP    -> { newY -= MOVE_STEP; heading = 0; }
                case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> { newY += MOVE_STEP; heading = 4; }
                case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> { newX -= MOVE_STEP; heading = 6; }
                case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> { newX += MOVE_STEP; heading = 2; }
                default -> { return; }
            }
            playerX = newX;
            playerY = newY;
            PacketWriter pw = new PacketWriter(PacketType.MOVE.getOpcode());
            pw.writeInt(newX);
            pw.writeInt(newY);
            pw.writeInt(heading);
            connection.sendPacket(pw.toByteArray());
        }
    }
}

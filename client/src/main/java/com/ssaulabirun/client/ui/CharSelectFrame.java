package com.ssaulabirun.client.ui;

import com.ssaulabirun.client.network.PacketType;
import com.ssaulabirun.client.network.PacketWriter;
import com.ssaulabirun.client.network.ServerConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CharSelectFrame extends JFrame {
    private static final Color BG = new Color(0x1a1a2e);
    private static final Color FG = new Color(0xe0e0e0);
    private static final Color ACCENT = new Color(0xc0392b);
    private static final Color TABLE_BG = new Color(0x16213e);

    private final ServerConnection connection;
    private DefaultTableModel tableModel;
    private JTable charTable;
    private final List<Long> charIds = new ArrayList<>();

    public CharSelectFrame(ServerConnection connection) {
        this.connection = connection;
        setTitle("싸울아비 - 캐릭터 선택");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        setupPacketListener();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("캐릭터 선택", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(ACCENT);
        root.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"이름", "직업 ID", "레벨", "HP"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        charTable = new JTable(tableModel);
        charTable.setBackground(TABLE_BG);
        charTable.setForeground(FG);
        charTable.setSelectionBackground(ACCENT);
        charTable.setSelectionForeground(Color.WHITE);
        charTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        charTable.getTableHeader().setBackground(new Color(0x0f3460));
        charTable.getTableHeader().setForeground(FG);
        charTable.setRowHeight(28);
        charTable.setGridColor(new Color(0x2c3e50));

        JScrollPane scroll = new JScrollPane(charTable);
        scroll.setBackground(TABLE_BG);
        scroll.getViewport().setBackground(TABLE_BG);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x0f3460)));
        root.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG);

        JButton selectBtn = createButton("접속", ACCENT);
        selectBtn.addActionListener(e -> doSelect());
        buttonPanel.add(selectBtn);

        JButton createBtn = createButton("새 캐릭터 생성", new Color(0x27ae60));
        createBtn.addActionListener(e -> openCreate());
        buttonPanel.add(createBtn);

        JButton deleteBtn = createButton("캐릭터 삭제", new Color(0x7f8c8d));
        buttonPanel.add(deleteBtn);

        root.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void requestCharList() {
        PacketWriter pw = new PacketWriter(PacketType.CHAR_LIST_REQUEST.getOpcode());
        connection.sendPacket(pw.toByteArray());
    }

    private void doSelect() {
        int row = charTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "캐릭터를 선택하세요.", "안내", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        long charId = charIds.get(row);
        PacketWriter pw = new PacketWriter(PacketType.CHAR_SELECT.getOpcode());
        pw.writeLong(charId);
        connection.sendPacket(pw.toByteArray());
    }

    private void openCreate() {
        CharCreateFrame create = new CharCreateFrame(connection, this);
        create.setVisible(true);
    }

    private void setupPacketListener() {
        connection.addListener((opcode, reader) -> {
            PacketType type = PacketType.fromOpcode(opcode & 0xFFFF);
            if (type == PacketType.CHAR_LIST_RESPONSE) {
                int count = reader.readByte() & 0xFF;
                List<Object[]> rows = new ArrayList<>();
                List<Long> ids = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    long cid = reader.readLong();
                    String name = reader.readString();
                    int classId = reader.readInt();
                    int level = reader.readInt();
                    int hp = reader.readInt();
                    int maxHp = reader.readInt();
                    rows.add(new Object[]{name, classId, level, hp + "/" + maxHp});
                    ids.add(cid);
                }
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    charIds.clear();
                    charIds.addAll(ids);
                    for (Object[] row : rows) {
                        tableModel.addRow(row);
                    }
                });
            } else if (type == PacketType.CHAR_SELECT_RESPONSE) {
                byte success = reader.readByte();
                String message = reader.readString();
                SwingUtilities.invokeLater(() -> {
                    if (success == 1) {
                        GameFrame game = new GameFrame(connection);
                        game.setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CharSelectFrame.this, message, "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });
    }
}

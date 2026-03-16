package com.ssaulabirun.client.ui;

import com.ssaulabirun.client.network.PacketType;
import com.ssaulabirun.client.network.PacketWriter;
import com.ssaulabirun.client.network.ServerConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CharCreateFrame extends JFrame {
    private static final Color BG = new Color(0x1a1a2e);
    private static final Color FG = new Color(0xe0e0e0);
    private static final Color ACCENT = new Color(0xc0392b);
    private static final Color FIELD_BG = new Color(0x16213e);

    private static final String[][] CLASSES = {
        {"0","없음"}, {"1","마법사"}, {"2","전사"}, {"3","성기사"}, {"4","궁수"},
        {"5","도적"}, {"6","사제"}, {"7","주술사"}, {"8","강령술사"}, {"9","광전사"},
        {"10","수도사"}, {"11","암살자"}, {"12","음유시인"}, {"13","드루이드"},
        {"14","기사"}, {"15","대마법사"}, {"16","전쟁군주"}, {"17","싸울아비"}
    };

    private static final int[][] BASE_STATS = {
        {10,10,10,10,10,10}, {8,10,8,16,14,10}, {16,12,14,8,8,10},
        {14,10,14,10,12,14}, {10,16,10,12,12,10}, {10,16,10,12,10,12},
        {8,10,10,12,16,12}, {10,12,10,14,14,10}, {8,10,8,16,12,10},
        {18,12,14,6,6,10}, {14,14,12,10,12,8}, {12,18,10,10,8,12},
        {8,12,8,12,12,18}, {10,10,12,12,16,10}, {14,10,16,8,10,14},
        {6,10,6,18,16,10}, {16,12,16,10,8,12}, {18,16,16,14,12,12}
    };

    private final ServerConnection connection;
    private final CharSelectFrame parent;
    private JTextField nameField;
    private JComboBox<String> classCombo;
    private JLabel[] statLabels;

    public CharCreateFrame(ServerConnection connection, CharSelectFrame parent) {
        this.connection = connection;
        this.parent = parent;
        setTitle("싸울아비 - 캐릭터 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        setupPacketListener();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("새 캐릭터 생성", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(ACCENT);
        root.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel nameLabel = styledLabel("이름:");
        centerPanel.add(nameLabel, gbc);
        nameField = new JTextField(15);
        styleField(nameField);
        gbc.gridx = 1; gbc.weightx = 0.7;
        centerPanel.add(nameField, gbc);

        // Class
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        centerPanel.add(styledLabel("직업:"), gbc);
        String[] classNames = new String[CLASSES.length];
        for (int i = 0; i < CLASSES.length; i++) classNames[i] = CLASSES[i][1];
        classCombo = new JComboBox<>(classNames);
        classCombo.setBackground(FIELD_BG);
        classCombo.setForeground(FG);
        classCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.weightx = 0.7;
        centerPanel.add(classCombo, gbc);

        // Stats preview
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel statsTitle = styledLabel("기본 스탯:");
        statsTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        centerPanel.add(statsTitle, gbc);

        String[] statNames = {"힘(STR)", "민첩(DEX)", "체력(CON)", "지능(INT)", "지혜(WIS)", "매력(CHA)"};
        statLabels = new JLabel[6];
        gbc.gridwidth = 1;
        for (int i = 0; i < 6; i++) {
            gbc.gridx = 0; gbc.gridy = 3 + i; gbc.weightx = 0.4;
            centerPanel.add(styledLabel(statNames[i]), gbc);
            statLabels[i] = styledLabel("10");
            statLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridx = 1; gbc.weightx = 0.6;
            centerPanel.add(statLabels[i], gbc);
        }
        root.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(BG);
        JButton createBtn = createButton("생성", ACCENT);
        createBtn.addActionListener(e -> doCreate());
        btnPanel.add(createBtn);
        JButton cancelBtn = createButton("취소", new Color(0x7f8c8d));
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(cancelBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
        updateStats(0);
        classCombo.addActionListener(e -> updateStats(classCombo.getSelectedIndex()));
    }

    private void updateStats(int index) {
        if (index < 0 || index >= BASE_STATS.length) return;
        int[] stats = BASE_STATS[index];
        for (int i = 0; i < 6; i++) {
            statLabels[i].setText(String.valueOf(stats[i]));
        }
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(FG);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(FG);
        field.setCaretColor(FG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x0f3460)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void doCreate() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "캐릭터 이름을 입력하세요.", "안내", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int classId = classCombo.getSelectedIndex();
        PacketWriter pw = new PacketWriter(PacketType.CHAR_CREATE_REQUEST.getOpcode());
        pw.writeString(name);
        pw.writeInt(classId);
        connection.sendPacket(pw.toByteArray());
    }

    private void setupPacketListener() {
        connection.addListener((opcode, reader) -> {
            PacketType type = PacketType.fromOpcode(opcode & 0xFFFF);
            if (type == PacketType.CHAR_CREATE_RESPONSE) {
                byte success = reader.readByte();
                String message = reader.readString();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(CharCreateFrame.this, message,
                            success == 1 ? "성공" : "실패",
                            success == 1 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                    if (success == 1) {
                        dispose();
                        parent.requestCharList();
                    }
                });
            }
        });
    }
}

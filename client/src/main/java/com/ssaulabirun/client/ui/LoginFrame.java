package com.ssaulabirun.client.ui;

import com.ssaulabirun.client.network.PacketType;
import com.ssaulabirun.client.network.PacketWriter;
import com.ssaulabirun.client.network.ServerConnection;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final Color BG = new Color(0x1a1a2e);
    private static final Color FG = new Color(0xe0e0e0);
    private static final Color ACCENT = new Color(0xc0392b);
    private static final Color FIELD_BG = new Color(0x16213e);

    private final ServerConnection connection;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(ServerConnection connection) {
        this.connection = connection;
        setTitle("싸울아비 - Ssaulabirun");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        setupPacketListener();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel title = new JLabel("싸울아비", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        root.add(title, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel userLabel = new JLabel("아이디:");
        userLabel.setForeground(FG);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        form.add(userLabel, gbc);

        usernameField = new JTextField(15);
        styleField(usernameField);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(usernameField, gbc);

        JLabel passLabel = new JLabel("비밀번호:");
        passLabel.setForeground(FG);
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        styleField(passwordField);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(passwordField, gbc);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BG);

        JButton loginBtn = createButton("로그인");
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        buttonPanel.add(loginBtn);

        JButton registerBtn = createButton("회원가입");
        registerBtn.setBackground(new Color(0x2c3e50));
        registerBtn.addActionListener(e -> doRegister());
        buttonPanel.add(registerBtn);

        root.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(FG);
        field.setCaretColor(FG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x0f3460)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PacketWriter pw = new PacketWriter(PacketType.LOGIN_REQUEST.getOpcode());
        pw.writeString(username);
        pw.writeString(password);
        connection.sendPacket(pw.toByteArray());
    }

    private void doRegister() {
        JOptionPane.showMessageDialog(this, "현재 서버를 통한 계정 생성만 지원됩니다.\n서버 관리자에게 문의하세요.", "안내", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupPacketListener() {
        connection.addListener((opcode, reader) -> {
            PacketType type = PacketType.fromOpcode(opcode & 0xFFFF);
            if (type == PacketType.LOGIN_RESPONSE) {
                byte success = reader.readByte();
                String message = reader.readString();
                SwingUtilities.invokeLater(() -> {
                    if (success == 1) {
                        CharSelectFrame charSelect = new CharSelectFrame(connection);
                        charSelect.setVisible(true);
                        charSelect.requestCharList();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, message, "로그인 실패", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });
    }
}

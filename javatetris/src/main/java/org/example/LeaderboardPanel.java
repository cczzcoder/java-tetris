package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * LeaderboardPanel - 排行榜显示面板
 *
 * 用于显示游戏排行榜信息，包括：
 * - 排行榜记录的列表显示
 * - 不同游戏模式的排行榜切换
 * - 排行榜的实时更新
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class LeaderboardPanel extends JPanel {
    /** 排行榜系统引用 */
    private Leaderboard leaderboard;
    /** 当前显示的游戏模式 */
    private GameMode currentMode;
    /** 排行榜显示区域 */
    private JTextArea leaderboardArea;
    /** 模式选择下拉框 */
    private JComboBox<GameMode> modeComboBox;
    /** 最高分显示标签 */
    private JLabel highScoreLabel;

    /**
     * 构造函数 - 创建排行榜显示面板
     *
     * @param leaderboard 排行榜系统引用
     */
    public LeaderboardPanel(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
        this.currentMode = GameMode.NORMAL;
        setupPanel();
        updateDisplay();
    }

    /**
     * 设置面板布局和组件
     */
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("排行榜"));
        setPreferredSize(new Dimension(250, 300));

        // 创建顶部控制面板
        JPanel topPanel = new JPanel(new FlowLayout());

        // 模式选择下拉框
        modeComboBox = new JComboBox<>(GameMode.values());
        modeComboBox.setSelectedItem(currentMode);
        modeComboBox.addActionListener(e -> {
            currentMode = (GameMode) modeComboBox.getSelectedItem();
            updateDisplay();
        });

        // 最高分显示标签
        highScoreLabel = new JLabel("最高分: 0");
        highScoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

        topPanel.add(new JLabel("模式:"));
        topPanel.add(modeComboBox);
        topPanel.add(highScoreLabel);

        // 创建排行榜显示区域
        leaderboardArea = new JTextArea();
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        leaderboardArea.setBackground(Color.WHITE);
        leaderboardArea.setLineWrap(true);
        leaderboardArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(leaderboardArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 创建底部按钮面板
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton refreshBtn = new JButton("刷新");
        JButton clearBtn = new JButton("清空");

        refreshBtn.addActionListener(e -> updateDisplay());
        clearBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "确定要清空所有排行榜吗？", "确认清空",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                leaderboard.clearLeaderboard();
                updateDisplay();
            }
        });

        bottomPanel.add(refreshBtn);
        bottomPanel.add(clearBtn);

        // 组装面板
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 更新排行榜显示
     */
    public void updateDisplay() {
        List<Leaderboard.ScoreRecord> records = leaderboard.getLeaderboard(currentMode);

        // 更新最高分显示
        int highScore = leaderboard.getHighScore(currentMode);
        highScoreLabel.setText(String.format("最高分: %d", highScore));

        // 更新排行榜显示
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s 排行榜 ===\n\n", currentMode.getDisplayName()));

        if (records.isEmpty()) {
            sb.append("暂无记录\n");
        } else {
            for (int i = 0; i < records.size(); i++) {
                Leaderboard.ScoreRecord record = records.get(i);
                sb.append(String.format("%d. %s\n", i + 1, record.toString()));
            }
        }

        leaderboardArea.setText(sb.toString());
        leaderboardArea.setCaretPosition(0); // 滚动到顶部
    }

    /**
     * 设置当前显示的游戏模式
     *
     * @param mode 游戏模式
     */
    public void setCurrentMode(GameMode mode) {
        this.currentMode = mode;
        modeComboBox.setSelectedItem(mode);
        updateDisplay();
    }

    /**
     * 获取当前显示的游戏模式
     *
     * @return 当前游戏模式
     */
    public GameMode getCurrentMode() {
        return currentMode;
    }

    /**
     * 显示排行榜对话框
     * 在独立窗口中显示完整的排行榜信息
     */
    public void showLeaderboardDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "排行榜", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();

        for (GameMode mode : GameMode.values()) {
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("微软雅黑", Font.PLAIN, 12));

            List<Leaderboard.ScoreRecord> records = leaderboard.getLeaderboard(mode);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("=== %s 排行榜 ===\n\n", mode.getDisplayName()));

            if (records.isEmpty()) {
                sb.append("暂无记录\n");
            } else {
                for (int i = 0; i < records.size(); i++) {
                    Leaderboard.ScoreRecord record = records.get(i);
                    sb.append(String.format("%d. %s\n", i + 1, record.toString()));
                }
            }

            area.setText(sb.toString());
            area.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(area);
            tabbedPane.addTab(mode.getDisplayName(), scrollPane);
        }

        dialog.add(tabbedPane, BorderLayout.CENTER);

        // 添加关闭按钮
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
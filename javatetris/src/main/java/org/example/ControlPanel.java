package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * ControlPanel - 控制面板类
 *
 * 提供游戏的基本控制按钮，包括：
 * - 新游戏按钮
 * - 暂停/继续按钮
 * - 保存游戏按钮
 * - 加载游戏按钮
 * - 排行榜按钮
 * - 操作说明标签
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class ControlPanel extends JPanel {
    /** 游戏主控制器引用 */
    private TetrisGame game;

    /**
     * 构造函数 - 创建控制面板
     *
     * @param game 游戏主控制器引用
     */
    public ControlPanel(TetrisGame game) {
        this.game = game;
        setupPanel();
    }

    /**
     * 设置面板布局和组件
     * 创建各种控制按钮和说明标签
     */
    private void setupPanel() {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton newGameBtn = new JButton("New Game");
        JButton pauseBtn = new JButton("Pause");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");
        JButton leaderboardBtn = new JButton("排行榜");

        newGameBtn.addActionListener(e -> game.startNewGame());
        pauseBtn.addActionListener(e -> game.togglePause());
        saveBtn.addActionListener(e -> game.saveGame());
        loadBtn.addActionListener(e -> game.loadGame());
        leaderboardBtn.addActionListener(e -> game.getLeaderboardPanel().showLeaderboardDialog());

        add(newGameBtn);
        add(pauseBtn);
        add(saveBtn);
        add(loadBtn);
        add(leaderboardBtn);

        // 添加说明标签
        JLabel instructionLabel = new JLabel("Instructions: Use arrow keys to move, up key to rotate, space key to drop faster, P key to pause");
        instructionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        add(instructionLabel);
    }
}
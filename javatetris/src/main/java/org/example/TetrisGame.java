package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * TetrisGame - 俄罗斯方块游戏主类
 *
 * 这是俄罗斯方块游戏的主窗口类，负责：
 * - 创建和管理游戏界面
 * - 协调各个游戏组件（游戏面板、分数面板、道具管理器等）
 * - 处理菜单操作（新游戏、暂停、保存、加载等）
 * - 管理游戏模式切换
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class TetrisGame extends JFrame {
    /** 游戏面板宽度（方块数量） */
    private static final int BOARD_WIDTH = 10;
    /** 游戏面板高度（方块数量） */
    private static final int BOARD_HEIGHT = 20;
    /** 每个方块的像素大小 */
    private static final int BLOCK_SIZE = 30;

    /** 游戏面板组件 */
    private GameBoard gameBoard;
    /** 信息面板（分数、等级、下一个方块等） */
    private InfoPanel infoPanel;
    /** 控制按钮面板 */
    private ControlPanel controlPanel;
    /** 当前游戏模式 */
    private GameMode gameMode;
    /** 游戏数据管理器 */
    private GameData gameData;
    /** 道具管理器 */
    private PowerUpManager powerUpManager;
    /** 排行榜系统 */
    private Leaderboard leaderboard;
    /** 排行榜显示面板 */
    private LeaderboardPanel leaderboardPanel;
    /** 防止排行榜弹窗重复弹出 */
    private boolean gameOverFlag = false;

    /**
     * 构造函数 - 创建俄罗斯方块游戏主窗口
     */
    public TetrisGame() {
        setTitle("俄罗斯方块游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
        setupLayout();
        setupMenu();

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * 初始化游戏组件
     * 创建所有必要的游戏组件实例
     */
    private void initComponents() {
        gameData = new GameData();
        gameMode = GameMode.NORMAL;
        powerUpManager = new PowerUpManager();
        leaderboard = new Leaderboard();
        leaderboardPanel = new LeaderboardPanel(leaderboard);

        infoPanel = new InfoPanel();
        gameBoard = new GameBoard(BOARD_WIDTH, BOARD_HEIGHT, BLOCK_SIZE, this);
        controlPanel = new ControlPanel(this);
    }

    /**
     * 设置游戏界面布局
     * 将各个组件按照合理的布局排列
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 左侧面板：游戏区域
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(gameBoard, BorderLayout.CENTER);
        leftPanel.add(controlPanel, BorderLayout.SOUTH);

        // 右侧面板：信息显示
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(infoPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(powerUpManager.getPanel());
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(leaderboardPanel);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * 设置游戏菜单
     * 创建菜单栏和菜单项，绑定相应的事件处理
     */
    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        // 游戏菜单
        JMenu gameMenu = new JMenu("游戏");
        JMenuItem newGameItem = new JMenuItem("新游戏");
        JMenuItem pauseItem = new JMenuItem("暂停");
        JMenuItem saveItem = new JMenuItem("保存游戏");
        JMenuItem loadItem = new JMenuItem("加载游戏");
        JMenuItem exitItem = new JMenuItem("退出");

        newGameItem.addActionListener(e -> startNewGame());
        pauseItem.addActionListener(e -> togglePause());
        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.add(pauseItem);
        gameMenu.addSeparator();
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        // 模式菜单
        JMenu modeMenu = new JMenu("模式");
        JMenuItem normalModeItem = new JMenuItem("普通模式");
        JMenuItem timeModeItem = new JMenuItem("时间模式");
        JMenuItem speedModeItem = new JMenuItem("速度模式");

        normalModeItem.addActionListener(e -> setMode(GameMode.NORMAL));
        timeModeItem.addActionListener(e -> setMode(GameMode.TIME));
        speedModeItem.addActionListener(e -> setMode(GameMode.SPEED));

        modeMenu.add(normalModeItem);
        modeMenu.add(timeModeItem);
        modeMenu.add(speedModeItem);

        // 排行榜菜单
        JMenu leaderboardMenu = new JMenu("排行榜");
        JMenuItem viewLeaderboardItem = new JMenuItem("查看排行榜");
        JMenuItem clearLeaderboardItem = new JMenuItem("清空排行榜");

        viewLeaderboardItem.addActionListener(e -> leaderboardPanel.showLeaderboardDialog());
        clearLeaderboardItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "确定要清空所有排行榜吗？", "确认清空",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                leaderboard.clearLeaderboard();
                leaderboardPanel.updateDisplay();
            }
        });

        leaderboardMenu.add(viewLeaderboardItem);
        leaderboardMenu.add(clearLeaderboardItem);

        menuBar.add(gameMenu);
        menuBar.add(modeMenu);
        menuBar.add(leaderboardMenu);
        setJMenuBar(menuBar);
    }

    /**
     * 开始新游戏
     * 重置游戏面板、分数面板和道具管理器
     */
    public void startNewGame() {
        gameOverFlag = false;
        gameBoard.startNewGame();
        infoPanel.reset();
        powerUpManager.reset();
    }

    /**
     * 切换游戏暂停状态
     * 暂停或恢复游戏
     */
    public void togglePause() {
        gameBoard.togglePause();
    }

    /**
     * 保存当前游戏状态
     * 将游戏数据序列化到文件中
     */
    public void saveGame() {
        try {
            gameData.saveGame(gameBoard.getGameState(), infoPanel.getScore(),
                    gameMode, powerUpManager.getPowerUpList());
            JOptionPane.showMessageDialog(this, "游戏已保存！");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存失败：" + e.getMessage());
        }
    }

    /**
     * 加载已保存的游戏
     * 从文件中反序列化游戏数据并恢复游戏状态
     */
    public void loadGame() {
        try {
            GameState state = gameData.loadGame();
            gameBoard.loadGameState(state);
            infoPanel.setScore(state.getScore());
            setMode(state.getGameMode());
            powerUpManager.setPowerUpList(state.getPowerUpList());
            JOptionPane.showMessageDialog(this, "游戏已加载！");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "加载失败：" + e.getMessage());
        }
    }

    /**
     * 设置游戏模式
     *
     * @param mode 要设置的游戏模式
     */
    public void setMode(GameMode mode) {
        this.gameMode = mode;
        gameBoard.setMode(mode);
        infoPanel.setMode(mode);
        leaderboardPanel.setCurrentMode(mode);
    }

    /**
     * 更新游戏分数
     *
     * @param score 新的分数值
     */
    public void updateScore(int score) {
        infoPanel.setScore(score);
    }

    /**
     * 更新下一个方块显示
     *
     * @param piece 下一个方块对象
     */
    public void updateNextPiece(Tetromino piece) {
        infoPanel.setNextPiece(piece);
    }

    /**
     * 保存分数到排行榜
     *
     * @param score 分数
     * @param gameMode 游戏模式
     */
    public void saveScoreToLeaderboard(int score, GameMode gameMode) {
        if (gameOverFlag) return;
        if (score <= 0) return;
        if (leaderboard.canEnterLeaderboard(score, gameMode)) {
            gameOverFlag = true;
            SwingUtilities.invokeLater(() -> {
                String playerName = JOptionPane.showInputDialog(this,
                        String.format("恭喜！你的分数 %d 进入了排行榜！\n请输入你的名字：", score),
                        "新纪录！");
                if (playerName != null && !playerName.trim().isEmpty()) {
                    boolean added = leaderboard.addScore(playerName.trim(), score, gameMode);
                    leaderboardPanel.updateDisplay();
                    if (added) {
                        JOptionPane.showMessageDialog(this,
                            "分数已保存到排行榜！",
                            "保存成功",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "分数未能保存到排行榜。",
                            "保存失败",
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        }
    }

    /**
     * 游戏结束处理
     * 显示游戏结束对话框，询问是否开始新游戏
     *
     * @param won 是否获胜
     */
    public void gameOver(boolean won) {
        String message = won ? "恭喜你赢了！" : "游戏结束！";
        int choice = JOptionPane.showConfirmDialog(this,
                message + "\n是否开始新游戏？", "游戏结束",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    /**
     * 获取道具管理器
     *
     * @return 道具管理器实例
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    /**
     * 获取当前游戏模式
     *
     * @return 当前游戏模式
     */
    public GameMode getMode() {
        return gameMode;
    }

    /**
     * 获取信息面板
     *
     * @return 信息面板实例
     */
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    /**
     * 获取排行榜系统
     *
     * @return 排行榜系统实例
     */
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    /**
     * 获取排行榜显示面板
     *
     * @return 排行榜显示面板实例
     */
    public LeaderboardPanel getLeaderboardPanel() {
        return leaderboardPanel;
    }

    /**
     * 程序入口点
     * 在事件调度线程中创建并显示游戏窗口
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TetrisGame().setVisible(true);
        });
    }
}
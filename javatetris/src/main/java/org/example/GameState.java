package org.example;

import java.io.Serializable;
import java.util.List;

/**
 * GameState - 游戏状态类
 *
 * 表示游戏的完整状态，用于游戏存档和加载，包含：
 * - 游戏面板的当前状态
 * - 当前方块和下一个方块
 * - 游戏分数、等级、消除行数
 * - 游戏模式和开始时间
 * - 道具列表
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class GameState implements Serializable {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 游戏面板数据矩阵 */
    private int[][] board;
    /** 当前下落的方块 */
    private Tetromino currentPiece;
    /** 下一个方块 */
    private Tetromino nextPiece;
    /** 当前分数 */
    private int score;
    /** 当前等级 */
    private int level;
    /** 已消除的行数 */
    private int linesCleared;
    /** 游戏模式 */
    private GameMode gameMode;
    /** 游戏开始时间 */
    private long gameStartTime;
    /** 道具列表 */
    private List<PowerUpManager.PowerUp> powerUps;

    /**
     * 构造函数 - 创建游戏状态对象
     *
     * @param board 游戏面板数据
     * @param currentPiece 当前方块
     * @param nextPiece 下一个方块
     * @param score 分数
     * @param level 等级
     * @param linesCleared 消除行数
     * @param gameMode 游戏模式
     * @param gameStartTime 游戏开始时间
     */
    public GameState(int[][] board, Tetromino currentPiece, Tetromino nextPiece,
                     int score, int level, int linesCleared, GameMode gameMode,
                     long gameStartTime) {
        this.board = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, board[i].length);
        }
        this.currentPiece = currentPiece;
        this.nextPiece = nextPiece;
        this.score = score;
        this.level = level;
        this.linesCleared = linesCleared;
        this.gameMode = gameMode;
        this.gameStartTime = gameStartTime;
    }

    /**
     * 获取游戏面板数据
     *
     * @return 游戏面板数据矩阵的副本
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * 获取当前方块
     *
     * @return 当前方块对象
     */
    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    /**
     * 获取下一个方块
     *
     * @return 下一个方块对象
     */
    public Tetromino getNextPiece() {
        return nextPiece;
    }

    /**
     * 获取游戏分数
     *
     * @return 当前分数
     */
    public int getScore() {
        return score;
    }

    /**
     * 获取游戏等级
     *
     * @return 当前等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 获取消除行数
     *
     * @return 已消除的行数
     */
    public int getLinesCleared() {
        return linesCleared;
    }

    /**
     * 获取游戏模式
     *
     * @return 当前游戏模式
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * 获取游戏开始时间
     *
     * @return 游戏开始时间（毫秒时间戳）
     */
    public long getGameStartTime() {
        return gameStartTime;
    }

    /**
     * 获取道具列表
     *
     * @return 道具列表
     */
    public java.util.List<PowerUpManager.PowerUp> getPowerUpList() {
        return powerUps;
    }

    /**
     * 设置道具列表
     *
     * @param powerUps 新的道具列表
     */
    public void setPowerUpList(java.util.List<PowerUpManager.PowerUp> powerUps) {
        this.powerUps = powerUps;
    }
}
package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * InfoPanel - 信息面板类
 *
 * 负责显示分数、等级、消除行数和下一个方块的预览。
 * 合并了原ScorePanel和NextPiecePanel的功能。
 */
public class InfoPanel extends JPanel {
    /** 当前分数 */
    private int score;
    /** 当前等级 */
    private int level;
    /** 已消除行数 */
    private int linesCleared;
    /** 下一个方块对象 */
    private Tetromino nextPiece;
    /** 方块显示大小 */
    private static final int BLOCK_SIZE = 20;
    /** 时间模式剩余时间 */
    private long remainTime = -1;
    /** 当前模式 */
    private GameMode mode = GameMode.NORMAL;

    /**
     * 构造函数 - 创建信息面板
     */
    public InfoPanel() {
        setPreferredSize(new Dimension(120, 180));
        setBorder(BorderFactory.createTitledBorder("信息"));
        setBackground(Color.BLACK);
        reset();
    }

    /**
     * 重置面板数据
     */
    public void reset() {
        score = 0;
        level = 1;
        linesCleared = 0;
        remainTime = -1;
        repaint();
    }

    /**
     * 设置分数
     * @param score 分数
     */
    public void setScore(int score) {
        this.score = score;
        repaint();
    }

    /**
     * 设置等级
     * @param level 等级
     */
    public void setLevel(int level) {
        this.level = level;
        repaint();
    }

    /**
     * 设置消除行数
     * @param linesCleared 行数
     */
    public void setLinesCleared(int linesCleared) {
        this.linesCleared = linesCleared;
        repaint();
    }

    /**
     * 设置下一个方块
     * @param piece 下一个方块对象
     */
    public void setNextPiece(Tetromino piece) {
        this.nextPiece = piece;
        repaint();
    }

    /**
     * 设置剩余时间（仅时间模式）
     * @param remainTime 剩余秒数
     */
    public void updateTime(long remainTime) {
        this.remainTime = remainTime;
        repaint();
    }

    /**
     * 设置当前模式
     * @param mode 游戏模式
     */
    public void setMode(GameMode mode) {
        this.mode = mode;
        repaint();
    }

    /**
     * 获取分数
     * @return 分数
     */
    public int getScore() {
        return score;
    }

    /**
     * 绘制面板内容
     * @param g 图形上下文
     */
    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("InfoPanel.paintComponent 被调用");
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 14));
        int y = 30;
        g.drawString("分数: " + score, 20, y);
        y += 30;
        g.drawString("等级: " + level, 20, y);
        y += 30;
        g.drawString("消除: " + linesCleared + " 行", 20, y);
        y += 30;
        if (mode == GameMode.TIME && remainTime >= 0) {
            g.drawString("剩余时间: " + remainTime + "s", 20, y);
            y += 30;
        }
        g.drawString("下一个方块:", 20, y);
        y += 10;
        if (nextPiece != null) {
            System.out.println("InfoPanel.paintComponent: nextPiece != null, shape=" + nextPiece.getShape());
            int[][] blocks = nextPiece.getBlocks();
            System.out.println("blocks=" + java.util.Arrays.deepToString(blocks));
            Graphics2D g2d = (Graphics2D) g;
            Color color = nextPiece.getColor();
            int rows = blocks.length;
            int cols = blocks[0].length;
            int offsetX = (getWidth() - cols * BLOCK_SIZE) / 2;
            int offsetY = y + 10;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (blocks[i][j] == 1) {
                        g2d.setColor(color);
                        g2d.fill3DRect(offsetX + j * BLOCK_SIZE, offsetY + i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(offsetX + j * BLOCK_SIZE, offsetY + i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        } else {
            System.out.println("InfoPanel.paintComponent: nextPiece == null");
        }
    }
} 
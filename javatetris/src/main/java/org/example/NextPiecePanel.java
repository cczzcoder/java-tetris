package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * NextPiecePanel - 下一个方块预览面板
 *
 * 显示下一个将要出现的方块，让玩家提前规划策略，包含：
 * - 下一个方块的预览显示
 * - 方块的3D效果绘制
 * - 面板的布局和样式
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class NextPiecePanel extends JPanel {
    /** 下一个方块对象 */
    private Tetromino nextPiece;
    /** 方块显示大小 */
    private static final int BLOCK_SIZE = 20;

    /**
     * 构造函数 - 创建下一个方块预览面板
     */
    public NextPiecePanel() {
        setPreferredSize(new Dimension(100, 100));
        setBorder(BorderFactory.createTitledBorder("next"));
        setBackground(Color.BLACK);
    }

    /**
     * 设置下一个方块
     *
     * @param piece 下一个方块对象
     */
    public void setNextPiece(Tetromino piece) {
        this.nextPiece = piece;
        repaint();
    }

    /**
     * 绘制面板内容
     * 绘制下一个方块的预览效果
     *
     * @param g 图形上下文
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (nextPiece != null) {
            int[][] blocks = nextPiece.getBlocks();
            Color color = nextPiece.getColor();

            int startX = (getWidth() - blocks[0].length * BLOCK_SIZE) / 2;
            int startY = (getHeight() - blocks.length * BLOCK_SIZE) / 2;

            for (int i = 0; i < blocks.length; i++) {
                for (int j = 0; j < blocks[i].length; j++) {
                    if (blocks[i][j] == 1) {
                        int x = startX + j * BLOCK_SIZE;
                        int y = startY + i * BLOCK_SIZE;
                        g2d.setColor(color);
                        g2d.fillRect(x + 1, y + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
                        g2d.setColor(color.brighter());
                        g2d.drawLine(x + 1, y + 1, x + BLOCK_SIZE - 2, y + 1);
                        g2d.drawLine(x + 1, y + 1, x + 1, y + BLOCK_SIZE - 2);
                        g2d.setColor(color.darker());
                        g2d.drawLine(x + BLOCK_SIZE - 2, y + 1, x + BLOCK_SIZE - 2, y + BLOCK_SIZE - 2);
                        g2d.drawLine(x + 1, y + BLOCK_SIZE - 2, x + BLOCK_SIZE - 2, y + BLOCK_SIZE - 2);
                    }
                }
            }
        }
    }
}
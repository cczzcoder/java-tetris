package org.example;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

/**
 * Tetromino - 俄罗斯方块类
 *
 * 表示俄罗斯方块游戏中的一个方块组合，包含：
 * - 七种经典俄罗斯方块形状（I、O、T、S、Z、J、L）
 * - 方块的位置坐标和旋转状态
 * - 方块的颜色和移动方法
 * - 序列化支持，用于游戏存档
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class Tetromino implements Serializable {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /**
     * 方块形状枚举
     * 定义了七种经典的俄罗斯方块形状
     */
    public enum Shape {
        /** I形方块 - 长条 */
        I,
        /** O形方块 - 正方形 */
        O,
        /** T形方块 - T形 */
        T,
        /** S形方块 - S形 */
        S,
        /** Z形方块 - Z形 */
        Z,
        /** J形方块 - J形 */
        J,
        /** L形方块 - L形 */
        L
    }

    /** 方块形状 */
    private Shape shape;
    /** 方块数据矩阵（1表示有方块，0表示空） */
    private int[][] blocks;
    /** 方块在游戏面板中的X坐标 */
    private int x, y;
    /** 方块颜色 */
    private Color color;

    /** 所有方块形状的旋转状态数据 */
    private static final int[][][] SHAPES = {
            // I 形状
            {{1, 1, 1, 1}},
            {{1}, {1}, {1}, {1}},

            // O 形状
            {{1, 1}, {1, 1}},

            // T 形状
            {{0, 1, 0}, {1, 1, 1}},
            {{1, 0}, {1, 1}, {1, 0}},
            {{1, 1, 1}, {0, 1, 0}},
            {{0, 1}, {1, 1}, {0, 1}},

            // S 形状
            {{0, 1, 1}, {1, 1, 0}},
            {{1, 0}, {1, 1}, {0, 1}},

            // Z 形状
            {{1, 1, 0}, {0, 1, 1}},
            {{0, 1}, {1, 1}, {1, 0}},

            // J 形状
            {{1, 0, 0}, {1, 1, 1}},
            {{1, 1}, {1, 0}, {1, 0}},
            {{1, 1, 1}, {0, 0, 1}},
            {{0, 1}, {0, 1}, {1, 1}},

            // L 形状
            {{0, 0, 1}, {1, 1, 1}},
            {{1, 0}, {1, 0}, {1, 1}},
            {{1, 1, 1}, {1, 0, 0}},
            {{1, 1}, {0, 1}, {0, 1}}
    };

    /** 每种形状对应的颜色 */
    private static final Color[] COLORS = {
            Color.CYAN,    // I
            Color.YELLOW,  // O
            Color.MAGENTA, // T
            Color.GREEN,   // S
            Color.RED,     // Z
            Color.BLUE,    // J
            Color.ORANGE   // L
    };

    /** 每种形状的旋转次数 */
    private static final int[] ROTATION_COUNT = {2, 1, 4, 2, 2, 4, 4};

    /** 每种形状在SHAPES数组中的起始索引 */
    private static final int[] SHAPE_START_INDEX = {0, 2, 3, 7, 9, 11, 15};

    /**
     * 构造函数 - 创建指定形状的方块
     *
     * @param shape 方块形状
     */
    public Tetromino(Shape shape) {
        this.shape = shape;
        this.color = COLORS[shape.ordinal()];

        int startIndex = SHAPE_START_INDEX[shape.ordinal()];
        int rotationCount = ROTATION_COUNT[shape.ordinal()];

        this.blocks = new int[SHAPES[startIndex].length][];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = SHAPES[startIndex][i].clone();
        }
        resetPosition();
    }

    /**
     * 生成随机方块
     * 随机选择一种形状创建方块
     *
     * @return 随机生成的方块
     */
    public static Tetromino random() {
        Random random = new Random();
        Shape[] shapes = Shape.values();
        return new Tetromino(shapes[random.nextInt(shapes.length)]);
    }

    /**
     * 重置方块位置到初始位置
     * 将方块移动到游戏面板顶部中央
     */
    public void resetPosition() {
        x = 3;
        y = 0;
    }

    /**
     * 旋转方块
     * 将方块顺时针旋转90度
     */
    public void rotate() {
        int rows = blocks.length;
        int cols = blocks[0].length;
        int[][] rotated = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = blocks[i][j];
            }
        }

        blocks = rotated;
    }

    /**
     * 向左移动方块
     */
    public void moveLeft() {
        x--;
    }

    /**
     * 向右移动方块
     */
    public void moveRight() {
        x++;
    }

    /**
     * 向下移动方块
     */
    public void moveDown() {
        y++;
    }

    /**
     * 向上移动方块
     */
    public void moveUp() {
        y--;
    }

    /**
     * 获取方块的X坐标
     *
     * @return X坐标
     */
    public int getX() {
        return x;
    }

    /**
     * 获取方块的Y坐标
     *
     * @return Y坐标
     */
    public int getY() {
        return y;
    }

    /**
     * 设置方块的X坐标
     *
     * @param x 新的X坐标
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * 设置方块的Y坐标
     *
     * @param y 新的Y坐标
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * 获取方块数据矩阵
     *
     * @return 方块数据矩阵（二维数组）
     */
    public int[][] getBlocks() {
        return blocks;
    }

    /**
     * 获取方块颜色
     *
     * @return 方块颜色
     */
    public Color getColor() {
        return color;
    }

    /**
     * 获取方块形状
     *
     * @return 方块形状枚举值
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * 获取方块宽度（列数）
     *
     * @return 方块宽度
     */
    public int getWidth() {
        return blocks[0].length;
    }

    /**
     * 获取方块高度（行数）
     *
     * @return 方块高度
     */
    public int getHeight() {
        return blocks.length;
    }

    /**
     * 复制方块
     * 创建一个与当前方块相同的新方块
     *
     * @return 复制的方块
     */
    public Tetromino copy() {
        Tetromino copy = new Tetromino(shape);
        copy.x = this.x;
        copy.y = this.y;
        copy.blocks = new int[blocks.length][];
        for (int i = 0; i < blocks.length; i++) {
            copy.blocks[i] = blocks[i].clone();
        }
        return copy;
    }
}
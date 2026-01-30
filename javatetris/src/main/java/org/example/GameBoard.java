package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

/**
 * GameBoard - 游戏面板类
 *
 * 这是俄罗斯方块游戏的核心游戏面板，负责：
 * - 绘制游戏界面和方块
 * - 处理键盘输入和游戏逻辑
 * - 管理方块移动、旋转、碰撞检测
 * - 处理行消除和分数计算
 * - 控制游戏速度和定时器
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class GameBoard extends JPanel {
    /** 游戏面板宽度 */
    private int width, height, blockSize;
    /** 游戏面板数据矩阵 */
    private int[][] board;
    /** 当前下落的方块 */
    private Tetromino currentPiece;
    /** 下一个方块 */
    private Tetromino nextPiece;
    /** 游戏主控制器引用 */
    private TetrisGame game;
    /** 游戏定时器（使用Swing Timer替代多线程Timer） */
    private Timer gameTimer;
    /** 游戏是否暂停 */
    private boolean isPaused = false;
    /** 游戏是否结束 */
    private boolean isGameOver = false;
    /** 当前游戏模式 */
    private GameMode gameMode;
    /** 当前分数 */
    private int score = 0;
    /** 当前等级 */
    private int level = 1;
    /** 已消除的行数 */
    private int linesCleared = 0;
    /** 游戏开始时间 */
    private long gameStartTime;
    /** 时间模式的时间限制（秒） */
    private int timeLimit = 300; // 5 minutes time mode
    /** 信息面板（分数、等级、下一个方块等） */
    private InfoPanel infoPanel;
    /** 道具效果相关变量 */
    private boolean doublePointsActive = false;
    private long doublePointsEndTime = 0;
    private int speedModifier = 0; // +1加速，-1减速，0正常
    private long speedEffectEndTime = 0;
    /** 自动使用加速道具相关 */
    private long speedUpAppearTime = -1;
    private int speedModeTick = 0; // 速度模式用
    private long lastSpeedUpTime = 0; // 速度模式用
    private long lastTimeAlert = 0;
    private JDialog timeDialog = null;

    /**
     * 构造函数 - 创建游戏面板
     *
     * @param width 面板宽度（方块数量）
     * @param height 面板高度（方块数量）
     * @param blockSize 每个方块的像素大小
     * @param game 游戏主控制器引用
     */
    public GameBoard(int width, int height, int blockSize, TetrisGame game) {
        this.width = width;
        this.height = height;
        this.blockSize = blockSize;
        this.game = game;
        this.gameMode = GameMode.NORMAL;
        this.infoPanel = game.getInfoPanel();

        setPreferredSize(new Dimension(width * blockSize, height * blockSize));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        setupKeyBindings();
        initBoard();
    }

    /**
     * 设置键盘绑定
     * 为各种游戏操作绑定键盘快捷键
     */
    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "rotate");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "drop");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pause");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "usePowerUp1");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "usePowerUp2");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "usePowerUp3");

        actionMap.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) moveLeft();
            }
        });

        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) moveRight();
            }
        });

        actionMap.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) {
                    if (!tryMoveDown()) {
                        placeBlock();
                        clearFullRows();
                        generateNewPiece();
                        if (collisionCheck(currentPiece, 0, 0)) {
                            gameOver(false);
                        }
                    }
                }
            }
        });

        actionMap.put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) rotate();
            }
        });

        actionMap.put("drop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) drop();
            }
        });

        actionMap.put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        actionMap.put("usePowerUp1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) {
                    PowerUpManager.PowerUp pu = game.getPowerUpManager().usePowerUp(0);
                    if (pu != null) applyPowerUp(pu);
                }
            }
        });

        actionMap.put("usePowerUp2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) {
                    PowerUpManager.PowerUp pu = game.getPowerUpManager().usePowerUp(1);
                    if (pu != null) applyPowerUp(pu);
                }
            }
        });

        actionMap.put("usePowerUp3", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && !isGameOver) {
                    PowerUpManager.PowerUp pu = game.getPowerUpManager().usePowerUp(2);
                    if (pu != null) applyPowerUp(pu);
                }
            }
        });
    }

    /**
     * 初始化游戏面板
     * 清空面板数据矩阵
     */
    private void initBoard() {
        board = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board[i][j] = 0;
            }
        }
    }

    /**
     * 开始新游戏
     * 重置所有游戏状态并启动游戏定时器
     */
    public void startNewGame() {
        System.out.println("startNewGame called");
        initBoard();
        currentPiece = Tetromino.random();
        currentPiece.resetPosition();
        nextPiece = Tetromino.random();
        nextPiece.resetPosition();
        if (currentPiece == null) {
            System.out.println("startNewGame: currentPiece is null, generating new");
            currentPiece = Tetromino.random();
            currentPiece.resetPosition();
        }
        if (nextPiece == null) {
            System.out.println("startNewGame: nextPiece is null, generating new");
            nextPiece = Tetromino.random();
            nextPiece.resetPosition();
        }
        score = 0;
        level = 1;
        linesCleared = 0;
        isGameOver = false;
        isPaused = false;
        gameStartTime = System.currentTimeMillis();
        infoPanel.setScore(score);
        infoPanel.setLevel(level);
        infoPanel.setLinesCleared(linesCleared);
        infoPanel.setNextPiece(nextPiece);
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new Timer(getGameSpeed(), e -> {
            System.out.println("Timer tick");
            if (!isPaused && !isGameOver) {
                update();
                repaint();
            }
        });
        gameTimer.start();
        lastSpeedUpTime = 0;
    }

    /**
     * 获取当前游戏速度（毫秒）
     * 根据游戏模式和等级计算下落速度
     *
     * @return 游戏速度间隔（毫秒）
     */
    private int getGameSpeed() {
        if (gameMode == GameMode.SPEED) {
            return Math.max(50, 1000 - (level * 150));
        }
        return 1000 - (level * 50);
    }

    /**
     * 游戏主更新逻辑
     * 处理时间模式、方块下落、碰撞检测等
     */
    private void update() {
        if (isGameOver) return;
        long now = System.currentTimeMillis();
        // 时间模式处理
        if (gameMode == GameMode.TIME) {
            long elapsedTime = (now - gameStartTime) / 1000;
            long remainTime = timeLimit - elapsedTime;
            infoPanel.updateTime(remainTime);
            if (remainTime <= 0) {
                if (timeDialog != null) {
                    timeDialog.dispose();
                    timeDialog = null;
                }
                gameOver(false);
                return;
            }
            // 剩余30秒时弹出实时倒计时弹窗
            if (remainTime <= 30) {
                showTimeDialog((int)remainTime);
            } else {
                if (timeDialog != null) {
                    timeDialog.dispose();
                    timeDialog = null;
                }
            }
        }
        // 速度模式处理：每30秒加快一次速度
        if (gameMode == GameMode.SPEED) {
            if (lastSpeedUpTime == 0) lastSpeedUpTime = now;
            if (now - lastSpeedUpTime >= 30000) { // 每30秒
                level++;
                lastSpeedUpTime = now;
                updateGameSpeed();
                infoPanel.setLevel(level);
            }
        }
        if (!tryMoveDown()) {
            placeBlock();
            clearFullRows();
            generateNewPiece();
            if (currentPiece == null) {
                System.out.println("update: currentPiece is null after generateNewPiece, generating new");
                currentPiece = Tetromino.random();
            }
            if (collisionCheck(currentPiece, 0, 0)) {
                gameOver(false);
            }
        }
        // 道具效果计时
        if (doublePointsActive && now > doublePointsEndTime) {
            doublePointsActive = false;
        }
        if (speedModifier != 0 && now > speedEffectEndTime) {
            speedModifier = 0;
            updateGameSpeed();
        }
        // 自动使用加速道具
        java.util.List<PowerUpManager.PowerUp> powerUps = game.getPowerUpManager().getPowerUpList();
        boolean foundSpeedUp = false;
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUpManager.PowerUp pu = powerUps.get(i);
            if (pu.getType() == PowerUpManager.Type.SPEED_UP) {
                foundSpeedUp = true;
                if (speedUpAppearTime == -1) speedUpAppearTime = now;
                if (now - speedUpAppearTime >= 10000) {
                    PowerUpManager.PowerUp used = game.getPowerUpManager().usePowerUp(i);
                    if (used != null) applyPowerUp(used);
                    speedUpAppearTime = -1;
                }
                break;
            }
        }
        if (!foundSpeedUp) speedUpAppearTime = -1;
    }

    /**
     * 向左移动当前方块
     */
    private void moveLeft() {
        if (!collisionCheck(currentPiece, -1, 0)) {
            currentPiece.moveLeft();
            repaint();
            if (collisionCheck(currentPiece, 0, 1)) {
                placeBlock();
                clearFullRows();
                generateNewPiece();
                if (collisionCheck(currentPiece, 0, 0)) {
                    gameOver(false);
                }
            }
        }
    }

    /**
     * 向右移动当前方块
     */
    private void moveRight() {
        if (!collisionCheck(currentPiece, 1, 0)) {
            currentPiece.moveRight();
            repaint();
            if (collisionCheck(currentPiece, 0, 1)) {
                placeBlock();
                clearFullRows();
                generateNewPiece();
                if (collisionCheck(currentPiece, 0, 0)) {
                    gameOver(false);
                }
            }
        }
    }

    /**
     * 向下移动当前方块
     *
     * @return 如果移动成功返回true，否则返回false
     */
    private boolean tryMoveDown() {
        if (!collisionCheck(currentPiece, 0, 1)) {
            currentPiece.moveDown();
            repaint();
            return true;
        }
        return false;
    }

    /**
     * 旋转当前方块
     */
    private void rotate() {
        Tetromino rotated = currentPiece.copy();
        rotated.rotate();
        if (!collisionCheck(rotated, 0, 0)) {
            currentPiece.rotate();
            repaint();
            if (collisionCheck(currentPiece, 0, 1)) {
                placeBlock();
                clearFullRows();
                generateNewPiece();
                if (collisionCheck(currentPiece, 0, 0)) {
                    gameOver(false);
                }
            }
        }
    }

    /**
     * 快速下落当前方块
     */
    private void drop() {
        while (tryMoveDown()) {
            score += doublePointsActive ? 4 : 2;
        }
        infoPanel.setScore(score);
    }

    /**
     * 碰撞检测
     * 检查方块在指定偏移位置是否会发生碰撞
     *
     * @param piece 要检测的方块
     * @param dx X方向偏移
     * @param dy Y方向偏移
     * @return 如果发生碰撞返回true，否则返回false
     */
    private boolean collisionCheck(Tetromino piece, int dx, int dy) {
        if (piece == null) {
            System.out.println("collisionCheck: piece is null!");
            return true; // 视为有碰撞，防止空指针
        }
        int[][] blocks = piece.getBlocks();
        int x = piece.getX() + dx;
        int y = piece.getY() + dy;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] == 1) {
                    int boardX = x + j;
                    int boardY = y + i;
                    if (boardX < 0 || boardX >= width ||
                            boardY >= height ||
                            (boardY >= 0 && board[boardY][boardX] == 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 将当前方块放置到游戏面板上
     */
    private void placeBlock() {
        int[][] blocks = currentPiece.getBlocks();
        int x = currentPiece.getX();
        int y = currentPiece.getY();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] == 1) {
                    int boardX = x + j;
                    int boardY = y + i;
                    if (boardY >= 0) {
                        board[boardY][boardX] = 1;
                    }
                }
            }
        }
    }

    /**
     * 清除已填满的行
     * 检测并消除填满的行，计算分数和等级
     */
    private void clearFullRows() {
        int linesToClear = 0;
        for (int i = height - 1; i >= 0; i--) {
            boolean isFull = true;
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                linesToClear++;
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k-1], 0, board[k], 0, width);
                }
                for (int j = 0; j < width; j++) {
                    board[0][j] = 0;
                }
                i++;
            }
        }
        if (linesToClear > 0) {
            linesCleared += linesToClear;
            int addScore = linesToClear * 100 * level;
            if (doublePointsActive) addScore *= 2;
            score += addScore;
            level = (linesCleared / 10) + 1;
            infoPanel.setScore(score);
            infoPanel.setLinesCleared(linesCleared);
            infoPanel.setLevel(level);
        }
        // 测试：每次clearFullRows都生成道具
        game.getPowerUpManager().generateRandomPowerUp();
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer.setDelay(getGameSpeed());
            gameTimer.start();
        }
    }

    /**
     * 生成新的方块
     * 将下一个方块设为当前方块，并生成新的下一个方块
     */
    private void generateNewPiece() {
        System.out.println("generateNewPiece called");
        if (nextPiece == null) {
            System.out.println("generateNewPiece: nextPiece is null, generating new");
            nextPiece = Tetromino.random();
        }
        currentPiece = nextPiece;
        currentPiece.resetPosition();
        nextPiece = Tetromino.random();
        if (currentPiece == null) {
            System.out.println("generateNewPiece: currentPiece is null after assign, generating new");
            currentPiece = Tetromino.random();
            currentPiece.resetPosition();
        }
        if (nextPiece == null) {
            System.out.println("generateNewPiece: nextPiece is null after assign, generating new");
            nextPiece = Tetromino.random();
        }
        infoPanel.setNextPiece(nextPiece);
        if (collisionCheck(currentPiece, 0, 0)) {
            System.out.println("新方块生成后立即碰撞，游戏结束");
            gameOver(false);
            return;
        }
        isPaused = false;
        isGameOver = false;
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer.setDelay(getGameSpeed());
            gameTimer.start();
        }
    }

    /**
     * 游戏结束处理
     *
     * @param won 是否获胜
     */
    private void gameOver(boolean won) {
        isGameOver = true;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        game.saveScoreToLeaderboard(score, gameMode);
        game.gameOver(won);
    }

    /**
     * 切换游戏暂停状态
     */
    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            if (gameTimer != null) {
                gameTimer.stop();
            }
            setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        } else {
            if (gameTimer != null) {
                gameTimer.start();
            }
            setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        }
        repaint();
    }

    /**
     * 设置游戏模式
     *
     * @param mode 游戏模式
     */
    public void setMode(GameMode mode) {
        this.gameMode = mode;
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer.setDelay(getGameSpeed());
            gameTimer.start();
        }
    }

    /**
     * 绘制游戏面板
     *
     * @param g 图形上下文
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw background grid
        g2d.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= height; i++) {
            g2d.drawLine(0, i * blockSize, width * blockSize, i * blockSize);
        }
        for (int j = 0; j <= width; j++) {
            g2d.drawLine(j * blockSize, 0, j * blockSize, height * blockSize);
        }

        // draw placed blocks
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 1) {
                    drawBlock(g2d, j, i, Color.GRAY);
                }
            }
        }

        // draw current block
        if (currentPiece != null) {
            drawTetromino(g2d, currentPiece);
        }

        // draw pause or game over message
        if (isPaused) {
            drawCenteredText(g2d, "Game Paused", Color.YELLOW);
        } else if (isGameOver) {
            drawCenteredText(g2d, "Game Over", Color.RED);
        }
    }

    /**
     * 绘制单个方块
     *
     * @param g2d 图形上下文
     * @param x 方块X坐标
     * @param y 方块Y坐标
     * @param color 方块颜色
     */
    private void drawBlock(Graphics2D g2d, int x, int y, Color color) {
        g2d.setColor(color);
        g2d.fillRect(x * blockSize + 1, y * blockSize + 1, blockSize - 2, blockSize - 2);

        g2d.setColor(color.brighter());
        g2d.drawLine(x * blockSize + 1, y * blockSize + 1,
                x * blockSize + blockSize - 2, y * blockSize + 1);
        g2d.drawLine(x * blockSize + 1, y * blockSize + 1,
                x * blockSize + 1, y * blockSize + blockSize - 2);

        g2d.setColor(color.darker());
        g2d.drawLine(x * blockSize + blockSize - 2, y * blockSize + 1,
                x * blockSize + blockSize - 2, y * blockSize + blockSize - 2);
        g2d.drawLine(x * blockSize + 1, y * blockSize + blockSize - 2,
                x * blockSize + blockSize - 2, y * blockSize + blockSize - 2);
    }

    /**
     * 绘制方块组合
     *
     * @param g2d 图形上下文
     * @param piece 方块组合对象
     */
    private void drawTetromino(Graphics2D g2d, Tetromino piece) {
        int[][] blocks = piece.getBlocks();
        Color color = piece.getColor();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] == 1) {
                    int x = piece.getX() + j;
                    int y = piece.getY() + i;
                    if (y >= 0) {
                        drawBlock(g2d, x, y, color);
                    }
                }
            }
        }
    }

    /**
     * 绘制居中文本
     *
     * @param g2d 图形上下文
     * @param text 要绘制的文本
     * @param color 文本颜色
     */
    private void drawCenteredText(Graphics2D g2d, String text, Color color) {
        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2;
        g2d.drawString(text, x, y);
    }

    /**
     * 获取当前游戏状态
     *
     * @return 游戏状态对象
     */
    public GameState getGameState() {
        return new GameState(board, currentPiece, nextPiece, score, level,
                linesCleared, gameMode, gameStartTime);
    }

    /**
     * 加载游戏状态
     *
     * @param state 要加载的游戏状态
     */
    public void loadGameState(GameState state) {
        this.board = state.getBoard();
        this.currentPiece = state.getCurrentPiece();
        this.nextPiece = state.getNextPiece();
        this.score = state.getScore();
        this.level = state.getLevel();
        this.linesCleared = state.getLinesCleared();
        this.gameMode = state.getGameMode();
        this.gameStartTime = state.getGameStartTime();

        infoPanel.setScore(score);
        infoPanel.setLinesCleared(linesCleared);
        infoPanel.setLevel(level);

        repaint();
    }

    // 应用道具效果
    private void applyPowerUp(PowerUpManager.PowerUp pu) {
        PowerUpManager.Type type = pu.getType();
        if (type == PowerUpManager.Type.SPEED_UP) {
            speedModifier = 1;
            speedEffectEndTime = System.currentTimeMillis() + 10000; // 10秒
            updateGameSpeed();
        } else if (type == PowerUpManager.Type.SLOW_DOWN) {
            speedModifier = -1;
            speedEffectEndTime = System.currentTimeMillis() + 10000; // 10秒
            updateGameSpeed();
        } else if (type == PowerUpManager.Type.DOUBLE_POINTS) {
            doublePointsActive = true;
            doublePointsEndTime = System.currentTimeMillis() + 10000; // 10秒
        } else if (type == PowerUpManager.Type.CLEAR_LINE) {
            clearOneLine();
        }
    }

    // 更新游戏速度（根据道具效果）
    private void updateGameSpeed() {
        int baseSpeed = getGameSpeed();
        int speed = baseSpeed;
        if (speedModifier == 1) speed = Math.max(50, baseSpeed / 2);
        if (speedModifier == -1) speed = baseSpeed * 2;
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer.setDelay(speed);
            gameTimer.start();
        }
    }

    // 清除一行（道具效果）
    private void clearOneLine() {
        for (int i = height - 1; i >= 0; i--) {
            boolean hasBlock = false;
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 1) {
                    hasBlock = true;
                    break;
                }
            }
            if (hasBlock) {
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k-1], 0, board[k], 0, width);
                }
                for (int j = 0; j < width; j++) {
                    board[0][j] = 0;
                }
                linesCleared++;
                score += 100 * level;
                infoPanel.setScore(score);
                infoPanel.setLinesCleared(linesCleared);
                infoPanel.setLevel(level);
                repaint();
                break;
            }
        }
    }

    // 实时显示剩余时间的弹窗
    private void showTimeDialog(int remainTime) {
        if (timeDialog == null) {
            java.awt.Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof java.awt.Frame) {
                timeDialog = new JDialog((java.awt.Frame) window, "时间提醒", false);
            } else if (window instanceof java.awt.Dialog) {
                timeDialog = new JDialog((java.awt.Dialog) window, "时间提醒", false);
            } else {
                timeDialog = new JDialog((java.awt.Frame) null, "时间提醒", false);
            }
            JLabel label = new JLabel();
            label.setFont(new Font("微软雅黑", Font.BOLD, 32));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            timeDialog.add(label);
            timeDialog.setSize(300, 120);
            timeDialog.setLocationRelativeTo(this);
            timeDialog.setAlwaysOnTop(true);
            timeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            timeDialog.setVisible(true);
        }
        JLabel label = (JLabel) timeDialog.getContentPane().getComponent(0);
        label.setText("剩余 " + remainTime + " 秒");
    }
}
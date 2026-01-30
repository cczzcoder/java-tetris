package org.example;

import java.io.*;

/**
 * GameData - 游戏数据管理类
 *
 * 负责游戏的存档和加载功能，包括：
 * - 游戏状态的序列化保存
 * - 游戏状态的反序列化加载
 * - 存档文件的存在性检查
 * - 存档文件的删除操作
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class GameData {
    /** 存档文件名 */
    private static final String SAVE_FILE = "tetris_save.txt";

    /**
     * 保存游戏状态到文件（文本格式）
     */
    public void saveGame(GameState state, int score, GameMode gameMode, java.util.List<PowerUpManager.PowerUp> powerUps) throws IOException {
        state.setPowerUpList(powerUps);
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            // 基本信息
            writer.printf("%d|%s|%d|%d|%d|%d|%d|%d\n", score, gameMode.name(), state.getLevel(), state.getLinesCleared(), state.getGameStartTime(), state.getCurrentPiece() != null ? 1 : 0, state.getNextPiece() != null ? 1 : 0, state.getBoard()[0].length);
            // 记录棋盘
            int[][] board = state.getBoard();
            writer.println(board.length + "|" + board[0].length);
            for (int[] row : board) {
                for (int cell : row) {
                    writer.print(cell + " ");
                }
                writer.println();
            }
            // 当前方块
            Tetromino cp = state.getCurrentPiece();
            if (cp != null) {
                writer.printf("%s|%d|%d|%d|%d\n", cp.getShape().name(), cp.getX(), cp.getY(), cp.getHeight(), cp.getWidth());
                int[][] cblocks = cp.getBlocks();
                for (int[] crow : cblocks) {
                    for (int ccell : crow) {
                        writer.print(ccell + " ");
                    }
                    writer.println();
                }
            }
            // 下一个方块
            Tetromino np = state.getNextPiece();
            if (np != null) {
                writer.printf("%s|%d|%d|%d|%d\n", np.getShape().name(), np.getX(), np.getY(), np.getHeight(), np.getWidth());
                int[][] nblocks = np.getBlocks();
                for (int[] nrow : nblocks) {
                    for (int ncell : nrow) {
                        writer.print(ncell + " ");
                    }
                    writer.println();
                }
            }
            // 道具
            writer.println(powerUps.size());
            for (PowerUpManager.PowerUp pu : powerUps) {
                writer.printf("%s|%d\n", pu.getType().name(), pu.getDuration());
            }
        }
    }

    /**
     * 从文件加载游戏状态（文本格式）
     */
    public GameState loadGame() throws IOException, ClassNotFoundException {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String[] base = reader.readLine().split("\\|");
            int score = Integer.parseInt(base[0]);
            GameMode gameMode = GameMode.valueOf(base[1]);
            int level = Integer.parseInt(base[2]);
            int linesCleared = Integer.parseInt(base[3]);
            long gameStartTime = Long.parseLong(base[4]);
            boolean hasCurrent = Integer.parseInt(base[5]) == 1;
            boolean hasNext = Integer.parseInt(base[6]) == 1;
            // 棋盘
            String[] size = reader.readLine().split("\\|");
            int rows = Integer.parseInt(size[0]);
            int cols = Integer.parseInt(size[1]);
            int[][] board = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                String[] cells = reader.readLine().trim().split(" ");
                for (int j = 0; j < cols; j++) {
                    board[i][j] = Integer.parseInt(cells[j]);
                }
            }
            // 当前方块
            Tetromino currentPiece = null;
            if (hasCurrent) {
                String[] cpinfo = reader.readLine().split("\\|");
                Tetromino.Shape cshape = Tetromino.Shape.valueOf(cpinfo[0]);
                int cx = Integer.parseInt(cpinfo[1]);
                int cy = Integer.parseInt(cpinfo[2]);
                int cheight = Integer.parseInt(cpinfo[3]);
                int cwidth = Integer.parseInt(cpinfo[4]);
                int[][] cblocks = new int[cheight][cwidth];
                for (int i = 0; i < cheight; i++) {
                    String[] cvals = reader.readLine().trim().split(" ");
                    for (int j = 0; j < cwidth; j++) {
                        cblocks[i][j] = Integer.parseInt(cvals[j]);
                    }
                }
                currentPiece = new Tetromino(cshape);
                currentPiece.setX(cx);
                currentPiece.setY(cy);
                // 直接赋值blocks
                try {
                    java.lang.reflect.Field blocksField = Tetromino.class.getDeclaredField("blocks");
                    blocksField.setAccessible(true);
                    blocksField.set(currentPiece, cblocks);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            // 下一个方块
            Tetromino nextPiece = null;
            if (hasNext) {
                String[] npinfo = reader.readLine().split("\\|");
                Tetromino.Shape nshape = Tetromino.Shape.valueOf(npinfo[0]);
                int nx = Integer.parseInt(npinfo[1]);
                int ny = Integer.parseInt(npinfo[2]);
                int nheight = Integer.parseInt(npinfo[3]);
                int nwidth = Integer.parseInt(npinfo[4]);
                int[][] nblocks = new int[nheight][nwidth];
                for (int i = 0; i < nheight; i++) {
                    String[] nvals = reader.readLine().trim().split(" ");
                    for (int j = 0; j < nwidth; j++) {
                        nblocks[i][j] = Integer.parseInt(nvals[j]);
                    }
                }
                nextPiece = new Tetromino(nshape);
                nextPiece.setX(nx);
                nextPiece.setY(ny);
                try {
                    java.lang.reflect.Field blocksField = Tetromino.class.getDeclaredField("blocks");
                    blocksField.setAccessible(true);
                    blocksField.set(nextPiece, nblocks);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            int powerUpCount = Integer.parseInt(reader.readLine());
            java.util.List<PowerUpManager.PowerUp> powerUps = new java.util.ArrayList<>();
            for (int i = 0; i < powerUpCount; i++) {
                String[] pu = reader.readLine().split("\\|");
                PowerUpManager.Type type = PowerUpManager.Type.valueOf(pu[0]);
                int duration = Integer.parseInt(pu[1]);
                PowerUpManager.PowerUp powerUp = new PowerUpManager.PowerUp(type);
                powerUp.setDuration(duration);
                powerUps.add(powerUp);
            }
            GameState state = new GameState(board, currentPiece, nextPiece, score, level, linesCleared, gameMode, gameStartTime);
            state.setPowerUpList(powerUps);
            return state;
        }
    }

    /**
     * 检查是否存在存档文件
     *
     * @return 如果存档文件存在返回true，否则返回false
     */
    public boolean hasSaveFile() {
        return new File(SAVE_FILE).exists();
    }

    /**
     * 删除存档文件
     * 如果存档文件存在则删除它
     */
    public void deleteSaveFile() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
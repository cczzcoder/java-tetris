package org.example;

import java.io.*;
import java.util.*;

/**
 * Leaderboard - 排行榜系统类
 *
 * 管理游戏的排行榜功能，包括：
 * - 分数记录的存储和管理
 * - 排行榜的排序和显示
 * - 排行榜数据的持久化保存
 * - 不同游戏模式的排行榜
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class Leaderboard {
    /** 排行榜文件名 */
    private static final String LEADERBOARD_FILE = "tetris_leaderboard.txt";
    /** 最大排行榜记录数 */
    private static final int MAX_RECORDS = 10;

    /** 排行榜记录类 */
    public static class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
        /** 序列化版本号 */
        private static final long serialVersionUID = 1L;

        /** 玩家名称 */
        private String playerName;
        /** 分数 */
        private int score;
        /** 游戏模式 */
        private GameMode gameMode;
        /** 记录时间 */
        private long timestamp;

        /**
         * 构造函数 - 创建分数记录
         *
         * @param playerName 玩家名称
         * @param score 分数
         * @param gameMode 游戏模式
         */
        public ScoreRecord(String playerName, int score, GameMode gameMode) {
            this.playerName = playerName;
            this.score = score;
            this.gameMode = gameMode;
            this.timestamp = System.currentTimeMillis();
        }

        /**
         * 获取玩家名称
         *
         * @return 玩家名称
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * 获取分数
         *
         * @return 分数
         */
        public int getScore() {
            return score;
        }

        /**
         * 获取游戏模式
         *
         * @return 游戏模式
         */
        public GameMode getGameMode() {
            return gameMode;
        }

        /**
         * 获取记录时间
         *
         * @return 记录时间戳
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * 获取格式化的时间字符串
         *
         * @return 格式化的时间字符串
         */
        public String getFormattedTime() {
            Date date = new Date(timestamp);
            return String.format("%tY-%<tm-%<td %<tH:%<tM", date);
        }

        /**
         * 比较方法 - 按分数降序排列
         *
         * @param other 另一个记录
         * @return 比较结果
         */
        @Override
        public int compareTo(ScoreRecord other) {
            return Integer.compare(other.score, this.score); // 降序排列
        }

        /**
         * 重写toString方法
         *
         * @return 记录字符串表示
         */
        @Override
        public String toString() {
            return String.format("%s - %d (%s) - %s",
                    playerName, score, gameMode.getDisplayName(), getFormattedTime());
        }
    }

    /** 所有排行榜记录 */
    private Map<GameMode, List<ScoreRecord>> leaderboards;

    /**
     * 构造函数 - 创建排行榜系统
     */
    public Leaderboard() {
        leaderboards = new HashMap<>();
        for (GameMode mode : GameMode.values()) {
            leaderboards.put(mode, new ArrayList<>());
        }
        loadLeaderboard();
    }

    /**
     * 添加分数记录
     *
     * @param playerName 玩家名称
     * @param score 分数
     * @param gameMode 游戏模式
     * @return 如果进入排行榜返回true，否则返回false
     */
    public boolean addScore(String playerName, int score, GameMode gameMode) {
        List<ScoreRecord> records = leaderboards.get(gameMode);
        ScoreRecord newRecord = new ScoreRecord(playerName, score, gameMode);

        // 检查是否能够进入排行榜
        if (records.size() < MAX_RECORDS || score > records.get(records.size() - 1).getScore()) {
            records.add(newRecord);
            Collections.sort(records);

            // 保持最大记录数
            if (records.size() > MAX_RECORDS) {
                records = records.subList(0, MAX_RECORDS);
                leaderboards.put(gameMode, records);
            }

            saveLeaderboard();
            return true;
        }

        return false;
    }

    /**
     * 获取指定模式的排行榜
     *
     * @param gameMode 游戏模式
     * @return 排行榜记录列表
     */
    public List<ScoreRecord> getLeaderboard(GameMode gameMode) {
        return new ArrayList<>(leaderboards.get(gameMode));
    }

    /**
     * 获取所有排行榜
     *
     * @return 所有排行榜的映射
     */
    public Map<GameMode, List<ScoreRecord>> getAllLeaderboards() {
        Map<GameMode, List<ScoreRecord>> copy = new HashMap<>();
        for (Map.Entry<GameMode, List<ScoreRecord>> entry : leaderboards.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * 获取指定模式的最高分
     *
     * @param gameMode 游戏模式
     * @return 最高分，如果没有记录返回0
     */
    public int getHighScore(GameMode gameMode) {
        List<ScoreRecord> records = leaderboards.get(gameMode);
        if (records.isEmpty()) {
            return 0;
        }
        return records.get(0).getScore();
    }

    /**
     * 检查分数是否能够进入排行榜
     *
     * @param score 分数
     * @param gameMode 游戏模式
     * @return 如果能够进入排行榜返回true，否则返回false
     */
    public boolean canEnterLeaderboard(int score, GameMode gameMode) {
        List<ScoreRecord> records = leaderboards.get(gameMode);
        return records.size() < MAX_RECORDS || score > records.get(records.size() - 1).getScore();
    }

    /**
     * 保存排行榜到文件（文本格式，每条记录一行，字段用|分隔）
     */
    private void saveLeaderboard() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (GameMode mode : GameMode.values()) {
                List<ScoreRecord> records = leaderboards.get(mode);
                for (ScoreRecord record : records) {
                    writer.printf("%s|%d|%s|%d\n",
                        record.getPlayerName().replace("|", "/"),
                        record.getScore(),
                        mode.name().toLowerCase(),
                        record.getTimestamp()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("保存排行榜失败: " + e.getMessage());
        }
    }

    /**
     * 从文件加载排行榜（文本格式，每条记录一行，字段用|分隔）
     */
    private void loadLeaderboard() {
        File file = new File(LEADERBOARD_FILE);
        for (GameMode mode : GameMode.values()) {
            leaderboards.put(mode, new ArrayList<>());
        }
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        String playerName = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        GameMode mode = GameMode.valueOf(parts[2].toUpperCase());
                        long timestamp = Long.parseLong(parts[3]);
                        ScoreRecord record = new ScoreRecord(playerName, score, mode);
                        // 反射设置时间戳
                        try {
                            java.lang.reflect.Field tsField = ScoreRecord.class.getDeclaredField("timestamp");
                            tsField.setAccessible(true);
                            tsField.setLong(record, timestamp);
                        } catch (Exception ignore) {}
                        leaderboards.get(mode).add(record);
                    }
                }
                // 排序
                for (GameMode mode : GameMode.values()) {
                    Collections.sort(leaderboards.get(mode));
                    if (leaderboards.get(mode).size() > MAX_RECORDS) {
                        leaderboards.put(mode, leaderboards.get(mode).subList(0, MAX_RECORDS));
                    }
                }
            } catch (IOException e) {
                System.err.println("加载排行榜失败: " + e.getMessage());
            }
        }
    }

    /**
     * 清空排行榜
     */
    public void clearLeaderboard() {
        for (GameMode mode : GameMode.values()) {
            leaderboards.put(mode, new ArrayList<>());
        }
        saveLeaderboard();
    }

    /**
     * 获取排行榜统计信息
     *
     * @return 统计信息字符串
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("排行榜统计:\n");

        for (GameMode mode : GameMode.values()) {
            List<ScoreRecord> records = leaderboards.get(mode);
            stats.append(String.format("%s: %d 条记录",
                    mode.getDisplayName(), records.size()));

            if (!records.isEmpty()) {
                stats.append(String.format(", 最高分: %d", records.get(0).getScore()));
            }
            stats.append("\n");
        }

        return stats.toString();
    }
}
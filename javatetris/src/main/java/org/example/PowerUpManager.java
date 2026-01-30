package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * PowerUpManager - 道具管理器类
 *
 * 负责管理游戏中的所有道具，包括：
 * - 道具的添加、使用和移除
 * - 道具状态的更新和显示
 * - 道具面板的界面管理
 * - 随机道具生成
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public class PowerUpManager {
    /** 道具列表 */
    private List<PowerUp> powerUps;
    /** 道具显示面板 */
    private JPanel panel;
    /** 道具标签数组 */
    private JLabel[] powerUpLabels;
    /** 最大道具数量 */
    private static final int MAX_POWER_UPS = 3;

    /**
     * 构造函数 - 创建道具管理器
     */
    public PowerUpManager() {
        powerUps = new ArrayList<>();
        setupPanel();
    }

    /**
     * 设置道具面板
     * 创建道具显示界面
     */
    private void setupPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("道具栏"));
        panel.setPreferredSize(new Dimension(120, 180));
        powerUpLabels = new JLabel[MAX_POWER_UPS];
        for (int i = 0; i < MAX_POWER_UPS; i++) {
            powerUpLabels[i] = new JLabel("空");
            powerUpLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            powerUpLabels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            powerUpLabels[i].setPreferredSize(new Dimension(100, 36));
            powerUpLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(powerUpLabels[i]);
            panel.add(Box.createVerticalStrut(5));
        }
    }

    /**
     * 添加道具到道具栏
     *
     * @param powerUp 要添加的道具
     */
    public void addPowerUp(PowerUp powerUp) {
        if (powerUps.size() < MAX_POWER_UPS) {
            powerUps.add(powerUp);
            updateDisplay();
        }
    }

    /**
     * 使用指定索引的道具
     *
     * @param index 道具索引
     * @return 使用的道具，如果索引无效返回null
     */
    public PowerUp usePowerUp(int index) {
        if (index >= 0 && index < powerUps.size()) {
            PowerUp powerUp = powerUps.remove(index);
            updateDisplay();
            return powerUp;
        }
        return null;
    }

    /**
     * 更新所有道具状态
     * 减少持续时间并移除已过期的道具
     */
    public void updatePowerUp() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update();
            if (!powerUp.isActive()) {
                iterator.remove();
            }
        }
        updateDisplay();
    }

    /**
     * 重置道具管理器
     * 清空所有道具
     */
    public void reset() {
        powerUps.clear();
        updateDisplay();
    }

    /**
     * 获取道具列表副本
     *
     * @return 道具列表的副本
     */
    public java.util.List<PowerUp> getPowerUpList() {
        return new ArrayList<>(powerUps);
    }

    /**
     * 设置道具列表
     *
     * @param powerUps 新的道具列表
     */
    public void setPowerUpList(java.util.List<PowerUp> powerUps) {
        this.powerUps.clear();
        if (powerUps != null) {
            this.powerUps.addAll(powerUps);
        }
        updateDisplay();
    }

    /**
     * 获取道具面板
     *
     * @return 道具显示面板
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * 检查是否有指定类型的激活道具
     *
     * @param type 要检查的道具类型
     * @return 如果有激活的道具返回true，否则返回false
     */
    public boolean hasActivePowerUp(Type type) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.getType() == type && powerUp.isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 随机生成道具
     * 随机选择一种道具类型并添加到道具栏
     */
    public void generateRandomPowerUp() {
        Random random = new Random();
        Type[] types = Type.values();
        Type randomType = types[random.nextInt(types.length)];
        addPowerUp(new PowerUp(randomType));
    }

    /**
     * 更新道具显示
     * 刷新道具面板上的显示内容
     */
    private void updateDisplay() {
        for (int i = 0; i < MAX_POWER_UPS; i++) {
            if (i < powerUps.size()) {
                PowerUp powerUp = powerUps.get(i);
                powerUpLabels[i].setText(powerUp.getName());
                powerUpLabels[i].setFont(new Font("微软雅黑", Font.BOLD, 18));
                // 背景色较浅时用黑色字体，否则用白色
                Color bg = powerUp.getColor();
                int brightness = bg.getRed() + bg.getGreen() + bg.getBlue();
                powerUpLabels[i].setForeground(brightness > 400 ? Color.BLACK : Color.WHITE);
                powerUpLabels[i].setBackground(bg);
                powerUpLabels[i].setOpaque(true);
            } else {
                powerUpLabels[i].setText("空");
                powerUpLabels[i].setFont(new Font("微软雅黑", Font.PLAIN, 16));
                powerUpLabels[i].setForeground(Color.DARK_GRAY);
                powerUpLabels[i].setBackground(Color.WHITE);
                powerUpLabels[i].setOpaque(true);
            }
        }
    }

    /**
     * 道具类型枚举
     */
    public enum Type {
        SLOW_DOWN("减速", "降低游戏速度", Color.BLUE),
        SPEED_UP("加速", "提高游戏速度", Color.RED),
        CLEAR_LINE("消行", "立即消除一行", Color.GREEN),
        DOUBLE_POINTS("双倍分数", "获得双倍分数", Color.YELLOW);
        // 已移除FREEZE和GHOST_PIECE

        private final String name;
        private final String description;
        private final Color color;

        Type(String name, String description, Color color) {
            this.name = name;
            this.description = description;
            this.color = color;
        }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Color getColor() { return color; }
    }

    /**
     * 道具数据结构
     */
    public static class PowerUp implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private Type type;
        private int duration;
        public PowerUp(Type type) {
            this.type = type;
            this.duration = type == Type.CLEAR_LINE ? -1 : 10;
        }
        public Type getType() { return type; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public boolean isActive() { return duration != 0; }
        public void update() { if (duration > 0) duration--; }
        public String getName() { return type.getName(); }
        public String getDescription() { return type.getDescription(); }
        public Color getColor() { return type.getColor(); }
        @Override public String toString() { return type.getName(); }
    }

    public static void main(String[] args) {
        PowerUpManager manager = new PowerUpManager();
        PowerUpManager.PowerUp powerUp = new PowerUpManager.PowerUp(Type.SLOW_DOWN);
        System.out.println("道具类型: " + powerUp.getName());
        System.out.println("道具描述: " + powerUp.getDescription());
        System.out.println("道具持续时间: " + powerUp.getDuration());
    }
}
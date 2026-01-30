package org.example;

import java.io.Serializable;

/**
 * GameMode - 游戏模式枚举
 *
 * 定义了游戏中可用的三种模式：
 * - 普通模式：经典的俄罗斯方块玩法
 * - 时间模式：在限定时间内获得最高分
 * - 速度模式：随着时间推移速度逐渐加快
 *
 * @author Java课设
 * @version 1.0
 * @since 2024
 */
public enum GameMode implements Serializable {
    /** 普通模式 - 经典俄罗斯方块玩法 */
    NORMAL("normal mode", "classic Russian block game"),
    /** 时间模式 - 在限定时间内获得最高分 */
    TIME("time mode", "get the highest score within the limited time"),
    /** 速度模式 - 随着时间推移速度逐渐加快 */
    SPEED("speed mode", "speed gradually increases with time");

    /** 模式显示名称 */
    private final String displayName;
    /** 模式描述 */
    private final String description;

    /**
     * 构造函数 - 创建游戏模式
     *
     * @param displayName 显示名称
     * @param description 模式描述
     */
    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 获取模式显示名称
     *
     * @return 模式的显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取模式描述
     *
     * @return 模式的详细描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 重写toString方法
     * 返回模式的显示名称
     *
     * @return 模式的显示名称
     */
    @Override
    public String toString() {
        return displayName;
    }
}
package com.KiKi.KunoRconShield.Config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    // 加载配置文件
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    // 获取Rcon端口
    public int getRconPort() {
        return config.getInt("rcon.port", 25575); // 默认为25575
    }

    // 获取最大尝试次数
    public int getMaxAttempts() {
        return config.getInt("rcon.max_attempts", 5); // 默认为5
    }

    // 获取封禁时间（秒），0为永久
    public int getBanTime() {
        return config.getInt("rcon.ban_time", 0); // 默认为0（永久）
    }
}

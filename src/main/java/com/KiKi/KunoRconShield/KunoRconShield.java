package com.KiKi.KunoRconShield;

import com.KiKi.KunoRconShield.Command.KunoRcon;
import com.KiKi.KunoRconShield.Config.ConfigManager;
import com.KiKi.KunoRconShield.Utils.FirewallUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class KunoRconShield extends JavaPlugin {
    private ConfigManager configManager;

    @Override
    public void onEnable() {

        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield]" + ChatColor.AQUA + "\n" +
                " _   __                     ______                    _____  _      _        _      _ \n" +
                "| | / /                     | ___ \\                  /  ___|| |    (_)      | |    | |\n" +
                "| |/ /  _   _  _ __    ___  | |_/ / ___  ___   _ __  \\ `--. | |__   _   ___ | |  __| |\n" +
                "|    \\ | | | || '_ \\  / _ \\ |    / / __|/ _ \\ | '_ \\  `--. \\| '_ \\ | | / _ \\| | / _` |\n" +
                "| |\\  \\| |_| || | | || (_) || |\\ \\| (__| (_) || | | |/\\__/ /| | | || ||  __/| || (_| |\n" +
                "\\_| \\_/ \\__,_||_| |_| \\___/ \\_| \\_|\\___|\\___/ |_| |_|\\____/ |_| |_||_| \\___||_| \\__,_|\n" +
                "                                                                                      \n" +
                "                                                                                      "

        );
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield] " + ChatColor.GREEN + "插件版本: Ver-" + ChatColor.AQUA + getDescription().getVersion());
        // 检查防火墙是否启用
        if (FirewallUtils.isFirewallEnabled()) {
            // 获取系统版本和防火墙类型
            String osName = System.getProperty("os.name").toLowerCase();  // 操作系统名称
            String osVersion = System.getProperty("os.version");  // 操作系统版本
            String firewallType = FirewallUtils.getFirewallType();
            // 将操作系统名称首字母大写
            osName = osName.substring(0, 1).toUpperCase() + osName.substring(1);
            // 输出操作系统名称和版本
            String osInfo = osName + " " + osVersion;
            // 输出系统版本和防火墙类型
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield] " + ChatColor.GREEN + "防火墙已启用");
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield] " + ChatColor.YELLOW + "操作系统: " + osInfo);
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield] " + ChatColor.YELLOW + "防火墙类型: " + firewallType);
            // 注册组件
            register();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "插件加载完成");
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield] " + ChatColor.RED + "未检测到防火墙启用，插件无法正常工作，正在卸载...");
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[KunoRconShield] " + ChatColor.GREEN + "插件卸载完成");
    }
    public void register(){
        Objects.requireNonNull(this.getCommand("kunorcon")).setExecutor(new KunoRcon(this));
    }
}

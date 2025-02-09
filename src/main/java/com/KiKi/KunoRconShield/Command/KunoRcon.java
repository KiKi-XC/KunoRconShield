package com.KiKi.KunoRconShield.Command;

import com.KiKi.KunoRconShield.Config.ConfigManager;
import com.KiKi.KunoRconShield.Config.IPStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KunoRcon implements TabExecutor {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final IPStorage ipStorage;
    private final Map<String, Integer> rconAttemptMap; // 记录每个 IP 的 RCON 尝试次数
    private Set<String> blockedIPs; // 已封禁的 IP 集合

    public KunoRcon(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
        this.ipStorage = new IPStorage(plugin);
        this.rconAttemptMap = new HashMap<>();
        this.blockedIPs = ipStorage.loadIPInfo(); // 加载已封禁的 IP 地址
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kunorcon.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "你没有足够的权限");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "请输入子命令: enable, disable");
            return false;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "enable":
                enableProtection(sender);
                return true;
            case "disable":
                disableProtection(sender);
                return true;
            case "reload":
                reloadConfig(sender);
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "未知子命令");
                return false;
        }
    }

    private void enableProtection(CommandSender sender) {
        int maxAttempts = configManager.getMaxAttempts();
        int banTime = configManager.getBanTime();
        int rconPort = configManager.getRconPort();

        sender.sendMessage(ChatColor.GREEN + "防爆破功能已启用!");
        sender.sendMessage(ChatColor.YELLOW + "最大尝试次数: " + maxAttempts);
        sender.sendMessage(ChatColor.YELLOW + "封禁时间: " + (banTime == 0 ? "永久" : banTime + "秒"));
        sender.sendMessage(ChatColor.YELLOW + "Rcon端口: " + rconPort);

        // 启用防爆破功能
        // TODO: 监听 RCON 登录事件，检测尝试次数

    }

    private void disableProtection(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "防爆破功能已禁用！");
        // 禁用防爆破功能
    }

    private void reloadConfig(CommandSender sender) {
        configManager.loadConfig();
        sender.sendMessage(ChatColor.GREEN + "配置文件已重新加载");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("enable", "disable", "reload");
        }
        return null;
    }

    // 在此方法中添加封禁IP的逻辑
    public void addBlockedIP(String ip) {
        blockedIPs.add(ip);
        ipStorage.saveIPInfo(blockedIPs); // 将更新后的IP保存到文件
        rconAttemptMap.remove(ip); // 清除该IP的登录尝试记录

        // 使用系统防火墙封禁IP
        blockIPWithFirewall(ip);
    }

    // 获取已封禁的IP集合
    public Set<String> getBlockedIPs() {
        return blockedIPs;
    }

    // RCON 登录尝试次数处理
    public void handleRconAttempt(String ip) {
        if (blockedIPs.contains(ip)) {
            Bukkit.getLogger().info("该IP已被封禁: " + ip);
            return; // 如果该 IP 已被封禁，直接返回
        }

        // 获取最大尝试次数
        int maxAttempts = configManager.getMaxAttempts();
        int attempts = rconAttemptMap.getOrDefault(ip, 0);

        if (attempts >= maxAttempts) {
            addBlockedIP(ip); // 超过最大尝试次数，封禁该 IP
            Bukkit.getLogger().info("RCON 尝试次数超过最大限制，IP 已被封禁: " + ip);
        } else {
            // 记录下该 IP 的尝试次数
            rconAttemptMap.put(ip, attempts + 1);
        }
    }

    // 使用系统防火墙封禁 IP
    private void blockIPWithFirewall(String ip) {
        // 获取操作系统类型和防火墙类型
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            // Windows 系统：使用 netsh 命令封禁 IP
            try {
                String command = "netsh advfirewall firewall add rule name=\"KunoRconShield Block IP\" dir=in action=block remoteip=" + ip;
                Runtime.getRuntime().exec(command);
                Bukkit.getLogger().info("使用防火墙封禁 IP: " + ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (osName.contains("nix") || osName.contains("nux")) {
            // Linux 系统：使用 iptables 命令封禁 IP
            try {
                String command = "iptables -A INPUT -s " + ip + " -j DROP";
                Runtime.getRuntime().exec(command);
                Bukkit.getLogger().info("使用防火墙封禁 IP: " + ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().warning("不支持的操作系统，无法封禁 IP: " + ip);
        }
    }
}

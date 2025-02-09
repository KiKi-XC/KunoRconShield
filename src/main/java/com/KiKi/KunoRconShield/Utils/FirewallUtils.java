package com.KiKi.KunoRconShield.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FirewallUtils {

    // 检查防火墙是否启用
    public static boolean isFirewallEnabled() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows系统
            return isWindowsFirewallEnabled();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Linux/Mac系统
            return isLinuxFirewallEnabled();
        } else {
            // 如果是其他系统，返回 false
            return false;
        }
    }

    // 获取防火墙类型
    public static String getFirewallType() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows 防火墙 (Defender)";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            return "Linux 防火墙 (iptables)";
        } else {
            return "未知防火墙";
        }
    }

    // 检查Windows防火墙是否启用
    private static boolean isWindowsFirewallEnabled() {
        try {
            // 执行 netsh 命令，检查防火墙状态
            Process process = Runtime.getRuntime().exec("netsh advfirewall show allprofiles state");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK")); // 使用 GBK 编码读取
            String line;
            boolean firewallEnabled = false;

            while ((line = reader.readLine()) != null) {
                // 检查防火墙是否启用
                if (line.toLowerCase().contains("启用")) {
                    firewallEnabled = true;
                    break;  // 一旦找到防火墙启用状态，退出循环
                }
            }
            // 判断防火墙是否启用
            return firewallEnabled;
        } catch (IOException e) {
            System.err.println("无法检查 Windows 防火墙状态：" + e.getMessage());
            return false;
        }
    }


    // 检查Linux防火墙是否启用
    private static boolean isLinuxFirewallEnabled() {
        try {
            // Linux系统通过 iptables 命令检查防火墙状态
            Process process = Runtime.getRuntime().exec("iptables -L");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("accept")) {
                    return true;  // 找到规则表示防火墙启用
                }
            }
        } catch (IOException e) {
            System.err.println("无法检查 Linux 防火墙状态：" + e.getMessage());
        }
        return false;  // 防火墙未启用或检查失败
    }
}

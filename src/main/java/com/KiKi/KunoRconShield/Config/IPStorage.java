package com.KiKi.KunoRconShield.Config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class IPStorage {

    private final JavaPlugin plugin;
    private final File file;
    private final Gson gson;

    public IPStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "ip_data.json");
        this.gson = new Gson();
    }

    // 保存IP信息
    public void saveIPInfo(Set<String> ipSet) {
        JsonArray jsonArray = new JsonArray();
        for (String ip : ipSet) {
            jsonArray.add(ip);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("ips", jsonArray);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载IP信息
    public Set<String> loadIPInfo() {
        Set<String> ipSet = new HashSet<>();
        if (!file.exists()) {
            return ipSet;  // 如果文件不存在，返回空集合
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray jsonArray = jsonObject.getAsJsonArray("ips");
            for (int i = 0; i < jsonArray.size(); i++) {
                ipSet.add(jsonArray.get(i).getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipSet;
    }
}

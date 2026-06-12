package io.github.Novma.duels.util;

import io.github.Novma.duels.DuelsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static File configFile, spawnsFile;
    private static YamlConfiguration config, spawns;

    public FileManager() {
        configFile = new File(DuelsPlugin.getInstance().getDataFolder(), "config.yml");
        spawnsFile = new File(DuelsPlugin.getInstance().getDataFolder(), "spawns.yml");
        load();
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
        spawns = YamlConfiguration.loadConfiguration(spawnsFile);
    }

    public static String getWorldName() {
        return config.getString("world");
    }
    public static String getEndWorldName() {
        return config.getString("end-world-name");
    }
    public static List<Map<?, ?>> getSpawns() {
        return spawns.getMapList("two-players");
    }
    public static boolean getDebug() {
        return config.getBoolean("debug");
    }
    public static String getMessage(String key) {
        return config.getString("messages." + key);
    }
}

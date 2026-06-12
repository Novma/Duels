package io.github.Novma.duels.util;

import org.bukkit.plugin.Plugin;

public class Logger {
    private static Plugin plugin;
    private static boolean debug;

    public static void init(Plugin plugin, boolean debug) {
        Logger.plugin = plugin;
        Logger.debug = debug;
    }

    public static void info(String message) {
        plugin.getLogger().info(message);
    }

    public static void warning(String message) {
        plugin.getLogger().warning(message);
    }

    public static void severe(String message) {
        plugin.getLogger().severe(message);
    }

    public static void debug(String message) {
        if (debug) {
            plugin.getLogger().info(message);
        }
    }
}

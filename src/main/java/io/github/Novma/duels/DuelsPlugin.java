package io.github.Novma.duels;

import io.github.Novma.duels.command.CommandDuel;
import io.github.Novma.duels.duel.DuelsManager;
import io.github.Novma.duels.duel.SpawnManager;
import io.github.Novma.duels.gui.GUIsManager;
import io.github.Novma.duels.listener.PlayerListener;
import io.github.Novma.duels.listener.GUIListener;
import io.github.Novma.duels.util.FileManager;
import io.github.Novma.duels.util.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DuelsPlugin extends JavaPlugin {
    private static Plugin instance;
    private FileManager fileManager;
    private SpawnManager spawnManager;
    private DuelsManager duelsManager;
    private static Economy econ;
    private GUIsManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("spawns.yml", false);

        instance = this;
        fileManager = new FileManager();
        Logger.init(this, FileManager.getDebug());
        if (!setupEconomy() ) {
            getLogger().severe("[%s] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        spawnManager = new SpawnManager();
        duelsManager = new DuelsManager(spawnManager);
        guiManager = new GUIsManager(duelsManager);

        getServer().getPluginManager().registerEvents(new PlayerListener(spawnManager, duelsManager), this);
        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);

        Objects.requireNonNull(getCommand("duel")).setExecutor(new CommandDuel(guiManager, duelsManager));
    }

    @Override
    public void onDisable() {

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }
}

package io.github.Novma.duels.duel;

import io.github.Novma.duels.DuelsPlugin;
import io.github.Novma.duels.util.FileManager;
import io.github.Novma.duels.util.Logger;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class DuelsManager {
    private final SpawnManager spawnManager;
    private final Map<UUID, Duel> activeDuels;
    private final Map<UUID, Duel> pendingDuels;

    public DuelsManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
        activeDuels = new HashMap<>();
        pendingDuels = new HashMap<>();
    }

    public void add(Player player1, Player player2, int prize) {
        // check if player1 has a duel
        if (getPendingDuel(player1) != null) {
            player1.sendMessage(Component.text(FileManager.getMessage("cannot-create")));
        }
        // add duel
        pendingDuels.put(player1.getUniqueId(), new Duel(player1, player2, prize));
        Logger.debug("Added duel");
    }

    public void accept(Player player1, Player player2) {
        // check if player2 has a duel
        if (getPendingDuel(player2) != null) {
            player2.sendMessage(Component.text(FileManager.getMessage("cannot-participate")));
            return;
        }
        // get duel from player1
        Duel duel = getPendingDuel(player1);
        if (duel == null) {
            player2.sendMessage(Component.text(FileManager.getMessage("old-gui")));
            return;
        }
        Player challenger = duel.getPlayer2();
        if (challenger != null) {
            if (!challenger.getUniqueId().equals(player2.getUniqueId())) {
                player2.sendMessage(Component.text(FileManager.getMessage("no-duel")));
                return;
            }
        }
        // check balance
        Economy econ = DuelsPlugin.getEconomy();
        if (econ.has(player1, duel.getPrize())) {
            player1.sendMessage(Component.text(FileManager.getMessage("no-money")));
            player2.sendMessage(Component.text(FileManager.getMessage("other-no-money")));
            return;
        }
        if (econ.has(player2, duel.getPrize())) {
            player1.sendMessage(Component.text(FileManager.getMessage("other-no-money")));
            player2.sendMessage(Component.text(FileManager.getMessage("no-money")));
            return;
        }
        // check if arena free
        SpawnManager.SpawnPair spawn = spawnManager.getSpawnPair(activeDuels.size());
        if (spawn == null) {
            player1.sendMessage(Component.text(FileManager.getMessage("no-spawn")));
            player2.sendMessage(Component.text(FileManager.getMessage("no-spawn")));
            return;
        }
        // start duel
        duel.accept(player2);
        pendingDuels.remove(player1.getUniqueId());
        activeDuels.put(player1.getUniqueId(), duel);

        econ.withdrawPlayer(player1, duel.getPrize());
        econ.withdrawPlayer(player2, duel.getPrize());

        player1.heal(40);
        player2.heal(40);

        player1.teleportAsync(spawn.first());
        player2.teleportAsync(spawn.second());
    }

    public void endDuel(Player winner, Player loser) {
        // get duel
        Duel duel = getActiveDuel(winner);
        if (duel == null) {
            duel = getPendingDuel(winner);
            if (duel == null) {
                Logger.severe("No active duel!");
                return;
            }
        }
        // give prize
        Economy econ = DuelsPlugin.getEconomy();
        econ.depositPlayer(winner, duel.getPrize() * 2);
        // end duel
        activeDuels.remove(duel.getUUID());
        World world = Bukkit.getWorld(FileManager.getEndWorldName());
        if (world == null) {
            Logger.severe("End world not found!");
            return;
        }
        winner.teleportAsync(world.getSpawnLocation());
        loser.teleportAsync(world.getSpawnLocation());
    }

    private Duel getActiveDuel(Player player) {
        return activeDuels.get(player.getUniqueId());
    }

    private Duel getPendingDuel(Player player) {
        return pendingDuels.get(player.getUniqueId());
    }

    public List<Duel> getPublicDuels(Player except) {
        List<Duel> publicDuels = new ArrayList<>();
        for (Duel duel : pendingDuels.values()) {
            if (duel.getPlayer2() == null) {
                if (!duel.getPlayer1().getUniqueId().equals(except.getUniqueId()))
                    publicDuels.add(duel);
            }
        }
        return publicDuels;
    }

    public void playerQuit(Player player) {
        // end active
        Duel duel = getActiveDuel(player);
        if (duel != null) {
            Player winner;
            if (duel.getPlayer1().getUniqueId().equals(player.getUniqueId())) {
                winner = duel.getPlayer2();
            } else {
                winner = duel.getPlayer1();
            }
            endDuel(winner, player);
            Logger.debug("Duel quit active");
        }
        // remove pending
        duel = getPendingDuel(player);
        if (duel != null) {
            pendingDuels.remove(player.getUniqueId());
            Logger.debug("Duel quit pending");
        }
    }

    public List<Duel> getPrivateDuels(Player player) {
        List<Duel> privateDuels = new ArrayList<>();
        for (Duel duel : pendingDuels.values()) {
            if (duel.getPlayer2().getUniqueId().equals(player.getUniqueId())) {
                privateDuels.add(duel);
            }
        }
        return privateDuels;
    }
}

package io.github.Novma.duels.command;

import io.github.Novma.duels.duel.DuelsManager;
import io.github.Novma.duels.gui.GUIsManager;
import io.github.Novma.duels.util.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDuel implements CommandExecutor {
    private GUIsManager guiManager;
    private DuelsManager duelsManager;

    public CommandDuel(GUIsManager guiManager, DuelsManager duelsManager) {
        this.guiManager = guiManager;
        this.duelsManager = duelsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can duel.");
            return true;
        }
        if (args.length == 0) {
            return false;
        }
        switch (args[0]) {
            case "open":
                if (!player.hasPermission("duel.open")) {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
                guiManager.openMenu(player);
                return true;
            case "create":
                if (!player.hasPermission("duel.create")) {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
                return handleCreate(player, args);
        }

        return false;
    }

    private boolean handleCreate(Player player, String[] args) {
        Logger.debug("Creating duel");
        if (args.length == 2) {
            // public duel
            int prize;
            try {
                prize = Integer.parseInt(args[1]);
                duelsManager.add(player, null, prize);
            } catch (NumberFormatException e) {
                player.sendMessage("Non hai inserito un numero!");
                return true;
            }
            Logger.debug("Created duel public");
        } else if (args.length == 3) {
            // private duel

        }
        return false;
    }
}

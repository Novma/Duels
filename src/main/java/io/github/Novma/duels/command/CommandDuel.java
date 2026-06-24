package io.github.Novma.duels.command;

import io.github.Novma.duels.gui.GUIsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDuel implements CommandExecutor {
    private GUIsManager guiManager;

    public CommandDuel(GUIsManager guiManager) {
        this.guiManager = guiManager;
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
        }

        return false;
    }
}

package io.github.Novma.duels.duel;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Duel {
    private final Player player1;
    private Player player2;
    private final int prize;
    private ItemStack item;

    public Duel(Player player1, Player player2, int prize) {
        this.player1 = player1;
        this.player2 = player2;
        this.prize = prize;
        item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(player1.getName()));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(prize));
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    public void accept(Player player) {
        this.player2 = player;
    }

    public UUID getUUID() {
        return player1.getUniqueId();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public int getPrize() {
        return prize;
    }

    public ItemStack getItem() {
        return item;
    }
}

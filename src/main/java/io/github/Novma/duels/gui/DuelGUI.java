package io.github.Novma.duels.gui;

import io.github.Novma.duels.duel.Duel;
import io.github.Novma.duels.duel.DuelsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DuelGUI implements InventoryHolder {
    private DuelsManager duelsManager;
    private Inventory menu;
    private ItemStack publicDuelsItem, privateDuelsItem, createDuelItem;

    public DuelGUI(DuelsManager duelsManager) {
        this.duelsManager = duelsManager;
        setup();
    }

    private void setup() {
        publicDuelsItem = new ItemStack(Material.DIAMOND_SWORD);
        privateDuelsItem = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = publicDuelsItem.getItemMeta();
        meta.itemName(Component.text("Duelli pubblici"));
        meta.setRarity(ItemRarity.EPIC);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Partecipa a duelli pubblici!"));
        meta.lore(lore);
        publicDuelsItem.setItemMeta(meta);
        meta = privateDuelsItem.getItemMeta();
        meta.itemName(Component.text("Duelli privati"));
        meta.setRarity(ItemRarity.EPIC);
        lore.set(0, Component.text("Partecipa a duelli privati!"));
        meta.lore(lore);
        privateDuelsItem.setItemMeta(meta);

        menu = Bukkit.createInventory(this, 27, Component.text("Duelli"));
        menu.setItem(11, publicDuelsItem);
        menu.setItem(15, privateDuelsItem);

        createDuelItem = new ItemStack(Material.NETHER_STAR);
        meta = createDuelItem.getItemMeta();
        meta.itemName(Component.text("Crea un duello"));
        meta.setRarity(ItemRarity.EPIC);
        lore = new ArrayList<>();
        lore.add(Component.text("Puoi creare un solo duello (pubblico o privato) alla volta."));
        meta.lore(lore);
        createDuelItem.setItemMeta(meta);
    }

    public void open(Player player) {
        player.openInventory(menu);
    }

    public void publicDuels(Player player) {
        List<Duel> duels = duelsManager.getPublicDuels();
        Inventory inventory = Bukkit.createInventory(this, 45, Component.text("Duelli pubblici"));
        inventory.setItem(40, createDuelItem);
        if (duels.size() > 36) {

        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(null, 9);
    }
}

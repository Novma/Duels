package io.github.Novma.duels.gui;

import io.github.Novma.duels.DuelsPlugin;
import io.github.Novma.duels.duel.Duel;
import io.github.Novma.duels.duel.DuelsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUIsManager implements InventoryHolder {
    private final Map<UUID, DuelGUI> guis;
    private DuelsManager duelsManager;
    private Inventory menu;
    private ItemStack createDuelItem, nextPage, previousPage, back;

    public GUIsManager(DuelsManager duelsManager) {
        this.guis = new HashMap<>();
        this.duelsManager = duelsManager;
        setup();
    }

    private void setup() {
        ItemStack publicDuelsItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack privateDuelsItem = new ItemStack(Material.NETHERITE_SWORD);
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

        nextPage = new ItemStack(Material.ENDER_PEARL);
        meta = nextPage.getItemMeta();
        meta.itemName(Component.text("Pagina successiva"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        nextPage.setItemMeta(meta);

        previousPage = new ItemStack(Material.ENDER_PEARL);
        meta = previousPage.getItemMeta();
        meta.itemName(Component.text("Pagina precedente"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        previousPage.setItemMeta(meta);

        back = new ItemStack(Material.ARROW);
        meta = back.getItemMeta();
        meta.itemName(Component.text("Indietro"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        back.setItemMeta(meta);
    }

    public void openMenu(Player player) {
        guis.put(player.getUniqueId(), new DuelGUI());
        openMenuGUI(player);
    }

    private void openMenuGUI(Player player) {
        DuelGUI gui = guis.get(player.getUniqueId());
        gui.setPage(0);
        gui.setType(DuelGUI.Type.MENU);
        gui.setInventory(menu);
        player.openInventory(menu);
    }

    public void handleClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        switch (item.displayName().toString()) {
            case "Duelli pubblici":
                openPublic(player);
                break;
            case "Duelli privati":
                openPrivate(player);
                break;
            case "Crea un duello":
                if (guis.get(player.getUniqueId()).getType() == DuelGUI.Type.PUBLIC) {
                    openCreatePublic(player);
                } else {
                    openCreatePrivate(player);
                }
                break;
            case "Pagina successiva":
                guis.get(player.getUniqueId()).setPage(guis.get(player.getUniqueId()).getPage() + 1);
                nextPage(player);
                break;
            case "Pagina precedente":
                guis.get(player.getUniqueId()).setPage(guis.get(player.getUniqueId()).getPage() - 1);
                previousPage(player);
                break;
            case "Indietro":
                openMenuGUI(player);
        }
    }

    private void openPublic(Player player) {
        DuelGUI gui = guis.get(player.getUniqueId());
        List<Duel> duels = duelsManager.getPublicDuels(player);
        Inventory inventory = Bukkit.createInventory(this, 45, Component.text("Duelli pubblici"));
        inventory.setItem(44, back);
        inventory.setItem(40, createDuelItem);
        if (duels.size() > 36) {
            inventory.setItem(43, nextPage);
        }
        for (int i = 0; i < Math.min(36, duels.size()); i++) {
            inventory.setItem(i, duels.get(i).getItem());
        }
        gui.setInventory(inventory);
        gui.setType(DuelGUI.Type.PUBLIC);
        gui.setDuels(duels);
        Bukkit.getScheduler().runTask(DuelsPlugin.getInstance(), () -> {
            player.openInventory(inventory);
        });
    }

    private void openPrivate(Player player) {

    }

    private void openCreatePublic(Player player) {

    }

    private void openCreatePrivate(Player player) {

    }

    private void nextPage(Player player) {
        DuelGUI gui = guis.get(player.getUniqueId());
        List<Duel> duels = gui.getDuels();
        Inventory inventory = gui.getInventory();
        inventory.setItem(37, previousPage);
        if (duels.size() > 36 * gui.getPage() + 1) {
            inventory.setItem(37, nextPage);
        }
        for (int i = 36 * gui.getPage(); i < Math.min(36 * gui.getPage() + 1, duels.size()); i++) {
            inventory.setItem(i, duels.get(i).getItem());
        }
    }

    private void previousPage(Player player) {
        DuelGUI gui = guis.get(player.getUniqueId());
        List<Duel> duels = gui.getDuels();
        Inventory inventory = gui.getInventory();
        if (gui.getPage() > 0) {
            inventory.setItem(37, previousPage);
        }
        inventory.setItem(37, nextPage);
        for (int i = 36 * gui.getPage(); i < Math.min(36 * gui.getPage() + 1, duels.size()); i++) {
            inventory.setItem(i, duels.get(i).getItem());
        }
    }

    public void close(Player player) {
        guis.remove(player.getUniqueId());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(null, 9);
    }
}

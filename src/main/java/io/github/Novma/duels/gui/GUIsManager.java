package io.github.Novma.duels.gui;

import io.github.Novma.duels.DuelsPlugin;
import io.github.Novma.duels.duel.Duel;
import io.github.Novma.duels.duel.DuelsManager;
import io.github.Novma.duels.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GUIsManager implements InventoryHolder {
    private final Map<UUID, DuelGUI> guis;
    private DuelsManager duelsManager;
    private Inventory menu;
    private ItemStack createDuelItem, nextPage, previousPage, back, prizeItem, namePlayer, acceptItem;
    private final NamespacedKey key = new NamespacedKey(DuelsPlugin.getInstance(), "item-gui-type");
    private final NamespacedKey keyValue = new NamespacedKey(DuelsPlugin.getInstance(), "item-gui-value");

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
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "public-duels");
        publicDuelsItem.setItemMeta(meta);
        meta = privateDuelsItem.getItemMeta();
        meta.itemName(Component.text("Duelli privati"));
        meta.setRarity(ItemRarity.EPIC);
        lore.set(0, Component.text("Partecipa a duelli privati!"));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "private-duels");
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
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "create-duel");
        createDuelItem.setItemMeta(meta);

        nextPage = new ItemStack(Material.ENDER_PEARL);
        meta = nextPage.getItemMeta();
        meta.itemName(Component.text("Pagina successiva"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "next-page");
        nextPage.setItemMeta(meta);

        previousPage = new ItemStack(Material.ENDER_PEARL);
        meta = previousPage.getItemMeta();
        meta.itemName(Component.text("Pagina precedente"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "previous-page");
        previousPage.setItemMeta(meta);

        back = new ItemStack(Material.ARROW);
        meta = back.getItemMeta();
        meta.itemName(Component.text("Indietro"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "back");
        back.setItemMeta(meta);

        prizeItem = new ItemStack(Material.GOLD_INGOT);
        meta = prizeItem.getItemMeta();
        meta.itemName(Component.text("premio"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "prize");
        meta.getPersistentDataContainer().set(keyValue, PersistentDataType.INTEGER, 0);
        prizeItem.setItemMeta(meta);

        namePlayer = new ItemStack(Material.NAME_TAG);
        meta = namePlayer.getItemMeta();
        meta.itemName(Component.text("nome player"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "name");
        meta.getPersistentDataContainer().set(keyValue, PersistentDataType.STRING, "player");
        namePlayer.setItemMeta(meta);

        acceptItem = new ItemStack(Material.NETHER_STAR);
        meta = acceptItem.getItemMeta();
        meta.itemName(Component.text("Conferma"));
        meta.setRarity(ItemRarity.EPIC);
        meta.lore(new ArrayList<>());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "accept");
        acceptItem.setItemMeta(meta);
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
        String id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (id == null) {
            if (item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                accept(player, item.displayName().toString());
            }
        } else {
            switch (id) {
                case "public-duels":
                    openPublic(player);
                    break;
                case "private-duels":
                    openPrivate(player);
                    break;
                case "create-duel":
                    if (guis.get(player.getUniqueId()).getType() == DuelGUI.Type.PUBLIC) {
                        openCreatePublic(player);
                    } else {
                        openCreatePrivate(player);
                    }
                    break;
                case "next-page":
                    guis.get(player.getUniqueId()).setPage(guis.get(player.getUniqueId()).getPage() + 1);
                    nextPage(player);
                    break;
                case "previous-page":
                    guis.get(player.getUniqueId()).setPage(guis.get(player.getUniqueId()).getPage() - 1);
                    previousPage(player);
                    break;
                case "back":
                    openMenuGUI(player);
                    break;
                case "prize":
                    enterPrize(player);
                    break;
                case "name":

                    break;
                case "accept":

            }
        }
    }

    private void openPublic(Player player) {
        Logger.debug("Opening public duels");
        DuelGUI gui = guis.get(player.getUniqueId());
        List<Duel> duels = duelsManager.getPublicDuels(player);
        // set gui
        Inventory inv = createDuelsInventory("Duelli pubblici", duels);
        gui.setInventory(inv);
        gui.setType(DuelGUI.Type.PUBLIC);
        gui.setDuels(duels);
        // open gui
        Bukkit.getScheduler().runTask(DuelsPlugin.getInstance(), () -> {
            player.openInventory(inv);
        });
    }

    private void openPrivate(Player player) {
        Logger.debug("Opening private duels");
        DuelGUI gui = guis.get(player.getUniqueId());
        List<Duel> duels = duelsManager.getPrivateDuels(player);
        // set gui
        Inventory inv = createDuelsInventory("Duelli privati", duels);
        gui.setInventory(inv);
        gui.setType(DuelGUI.Type.PRIVATE);
        gui.setDuels(duels);
        // open gui
        Bukkit.getScheduler().runTask(DuelsPlugin.getInstance(), () -> {
            player.openInventory(inv);
        });
    }

    private Inventory createDuelsInventory(String name, List<Duel> duels) {
        Inventory inventory = Bukkit.createInventory(this, 45, Component.text(name));
        inventory.setItem(44, back);
        inventory.setItem(40, createDuelItem);
        if (duels.size() > 36) {
            inventory.setItem(43, nextPage);
        }
        for (int i = 0; i < Math.min(36, duels.size()); i++) {
            inventory.setItem(i, duels.get(i).getItem());
        }
        return inventory;
    }

    private void openCreatePublic(Player player) {
        Logger.debug("Opening create public duel");
        DuelGUI gui = guis.get(player.getUniqueId());
        // create inventory
        Inventory inventory =  Bukkit.createInventory(this, 45, Component.text("Creazione duello pubblico"));
        inventory.setItem(44, back);
        inventory.setItem(13, prizeItem);
        inventory.setItem(22, acceptItem);
        // set gui
        gui.setInventory(inventory);
        gui.setType(DuelGUI.Type.CREATE);
        gui.setDuels(null);
        // open gui
        Bukkit.getScheduler().runTask(DuelsPlugin.getInstance(), () -> {
            player.openInventory(inventory);
        });
    }

    private void openCreatePrivate(Player player) {
        Logger.debug("Opening create private duel");
        DuelGUI gui = guis.get(player.getUniqueId());
        // create inventory
        Inventory inventory =  Bukkit.createInventory(this, 45, Component.text("Creazione duello privato"));
        inventory.setItem(44, back);
        inventory.setItem(11, prizeItem);
        inventory.setItem(15, namePlayer);
        inventory.setItem(22, acceptItem);
        // set gui
        gui.setInventory(inventory);
        gui.setType(DuelGUI.Type.CREATE);
        gui.setDuels(null);
        // open gui
        Bukkit.getScheduler().runTask(DuelsPlugin.getInstance(), () -> {
            player.openInventory(inventory);
        });
    }

    private void nextPage(Player player) {
        Logger.debug("Next page");
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
        Logger.debug("Previous page");
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

    private void accept(Player player2, String name) {
        Logger.debug("Accept duel");
        Player player1 = Bukkit.getPlayerExact(name);
        if (player1 == null) return;
        duelsManager.accept(player1, player2);
    }

    private void enterPrize(Player player) {

    }

    public void close(Player player) {
        guis.remove(player.getUniqueId());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(null, 9);
    }
}

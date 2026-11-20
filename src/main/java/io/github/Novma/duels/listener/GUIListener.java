package io.github.Novma.duels.listener;

import io.github.Novma.duels.gui.GUIsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GUIListener implements Listener {
    private GUIsManager guiManager;

    public GUIListener(GUIsManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null) return;
        if (inv.getHolder() instanceof GUIsManager) {
            event.setCancelled(true);
            guiManager.handleClick(event);
        }
    }

    @EventHandler
    
}

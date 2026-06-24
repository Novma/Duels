package io.github.Novma.duels.gui;

import io.github.Novma.duels.duel.Duel;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class DuelGUI {
    private int page;
    private Type type;
    private Inventory inventory;
    private List<Duel> duels;

    public DuelGUI() {
        page = 0;
        type = Type.MENU;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Duel> getDuels() {
        return duels;
    }

    public void setDuels(List<Duel> duels) {
        this.duels = duels;
    }

    public enum Type {
        MENU,
        PUBLIC,
        PRIVATE,
        CREATE
    }
}

package io.github.Novma.duels.listener;

import io.github.Novma.duels.duel.DuelsManager;
import io.github.Novma.duels.duel.SpawnManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private SpawnManager spawnManager;
    private DuelsManager duelsManager;

    public DeathListener(SpawnManager spawnManager, DuelsManager duelsManager) {
        this.spawnManager = spawnManager;
        this.duelsManager = duelsManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        // check world
        if (!victim.getWorld().getUID().equals(spawnManager.getWorld().getUID())) {
            return;
        }
        // delete event
        e.setCancelled(true);
        // end duel
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }
        duelsManager.endDuel(killer, victim);
    }
}

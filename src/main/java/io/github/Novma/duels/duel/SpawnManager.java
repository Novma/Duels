package io.github.Novma.duels;

import io.github.Novma.duels.util.FileManager;
import io.github.Novma.duels.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpawnManager {
    private List<SpawnPair> spawnLocations;

    public SpawnManager() {
        load();
    }

    public void load() {
        spawnLocations = new ArrayList<>();

        World world = Bukkit.getWorld(FileManager.getWorldName());
        if (world == null) {
            Logger.severe("World not found!");
            return;
        }

        for (Map<?, ?> pair : FileManager.getSpawns()) {

            List<?> first = (List<?>) pair.get("first");
            List<?> second = (List<?>) pair.get("second");

            Location loc1 = new Location(
                    world,
                    ((Number) first.get(0)).doubleValue(),
                    ((Number) first.get(1)).doubleValue(),
                    ((Number) first.get(2)).doubleValue()
            );

            Location loc2 = new Location(
                    world,
                    ((Number) second.get(0)).doubleValue(),
                    ((Number) second.get(1)).doubleValue(),
                    ((Number) second.get(2)).doubleValue()
            );

            spawnLocations.add(new SpawnPair(loc1, loc2));
        }
    }

    public record SpawnPair(Location first, Location second) {}
}

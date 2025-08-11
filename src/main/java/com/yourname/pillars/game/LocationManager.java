package com.yourname.pillars.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class LocationManager {
    private final Random random = new Random();

    public Location getNextLocation() {
        World world = Bukkit.getWorlds().get(0);
        Location loc;
        int attempts = 0;

        do {
            int x = random.nextInt(1000) - 500;
            int z = random.nextInt(1000) - 500;
            int y = 100;
            loc = new Location(world, x, y, z);
            attempts++;
        } while (isOverWater(loc) && attempts < 10);

        return loc;
    }

    private boolean isOverWater(Location loc) {
        Location check = loc.clone();
        check.setY(63); // sea level
        Material mat = check.getBlock().getType();
        return mat == Material.WATER || mat == Material.KELP || mat == Material.SEAGRASS;
    }
}
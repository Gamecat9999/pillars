package com.yourname.pillars.game;

import com.yourname.pillars.PillarsPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PillarGenerator {
    private final PillarsPlugin plugin;

    public PillarGenerator(PillarsPlugin plugin) {
        this.plugin = plugin;
    }

    public List<Location> generatePillars(Location base) {
        List<Location> locations = new ArrayList<>();
        int spacing = plugin.getConfig().getInt("pillar.spacing");
        int height = plugin.getConfig().getInt("pillar.height");
        Material blockType = Material.valueOf(plugin.getConfig().getString("pillar.block"));

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = base.clone().add(x * spacing, 0, z * spacing);
                buildPillar(loc, height, blockType);
                locations.add(loc.clone().add(0, height + 3, 0)); // teleport above platform
            }
        }
        return locations;
    }

    private void buildPillar(Location loc, int height, Material blockType) {
        World world = loc.getWorld();

        // Build vertical pillar
        for (int y = 0; y < height; y++) {
            Block block = world.getBlockAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ());
            block.setType(blockType);
        }

        // Build 3x3 platform
        int topY = loc.getBlockY() + height;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block platform = world.getBlockAt(loc.getBlockX() + dx, topY, loc.getBlockZ() + dz);
                platform.setType(blockType);
            }
        }

        // Add 2-block cover in center
        world.getBlockAt(loc.getBlockX(), topY + 1, loc.getBlockZ()).setType(blockType);
        world.getBlockAt(loc.getBlockX(), topY + 2, loc.getBlockZ()).setType(blockType);
    }

    public void spawnCenterLoot(Location loc) {
        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();
        Inventory inv = chest.getInventory();

        for (String itemStr : plugin.getConfig().getStringList("center_loot")) {
            Material mat = Material.valueOf(itemStr);
            inv.addItem(new ItemStack(mat));
        }
    }
}
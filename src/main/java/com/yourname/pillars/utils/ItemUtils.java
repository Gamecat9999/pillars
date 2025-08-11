package com.yourname.pillars.utils;

import com.yourname.pillars.PillarsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Random;

public class ItemUtils {
    private static final Random random = new Random();

    public static ItemStack getRandomItem(PillarsPlugin plugin, Player player) {
        List<String> items = plugin.getConfig().getStringList("items");
        String entry = items.get(random.nextInt(items.size()));

        String[] parts = entry.split(":");
        Material mat;
        int amount = 1;

        if (entry.startsWith("POTION")) {
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();

            if (parts.length > 1) {
                PotionType type = PotionType.valueOf(parts[1].toUpperCase());
                meta.setBasePotionData(new PotionData(type));
                potion.setItemMeta(meta);
            }

            return potion;
        }

        mat = Material.valueOf(parts[0]);
        if (parts.length > 1) {
            amount = Integer.parseInt(parts[1]);
        }

        ItemStack item = new ItemStack(mat, amount);

        if (mat == Material.BOW || mat == Material.CROSSBOW) {
            player.getInventory().addItem(new ItemStack(Material.ARROW, 16));
        }

        return item;
    }
}
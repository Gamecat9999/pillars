package com.yourname.pillars.game;

import com.yourname.pillars.PillarsPlugin;
import com.yourname.pillars.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class ItemDropScheduler {
    private final PillarsPlugin plugin;
    private int taskId = -1;

    public ItemDropScheduler(PillarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startDrops(Set<UUID> players) {
        stopDrops();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (UUID id : players) {
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline()) {
                    ItemStack item = ItemUtils.getRandomItem(plugin, p);
                    p.getInventory().addItem(item);
                    p.sendMessage("§bYou received: §f" + item.getType());
                }
            }
        }, 0L, 200L); // 10 seconds
    }

    public void stopDrops() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
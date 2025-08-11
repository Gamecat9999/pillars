package com.yourname.pillars.game;

import com.yourname.pillars.PillarsPlugin;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class GameManager {
    private final PillarsPlugin plugin;
    private final Set<UUID> alivePlayers = new HashSet<>();
    private final PillarGenerator generator;
    private final ItemDropScheduler dropScheduler;
    private final LocationManager locationManager;

    private int platformY = 0;
    private int fallMonitorTaskId = -1;

    public GameManager(PillarsPlugin plugin) {
        this.plugin = plugin;
        this.generator = new PillarGenerator(plugin);
        this.dropScheduler = new ItemDropScheduler(plugin);
        this.locationManager = new LocationManager();
    }

    public void startGame() {
        Location base = locationManager.getNextLocation();
        List<Location> pillars = generator.generatePillars(base);
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        alivePlayers.clear();
        platformY = base.getBlockY() + plugin.getConfig().getInt("pillar.height") + 3;

        for (int i = 0; i < players.size() && i < 8; i++) {
            Player p = players.get(i);
            p.teleport(pillars.get(i));
            p.getInventory().clear();
            alivePlayers.add(p.getUniqueId());
        }

        generator.spawnCenterLoot(pillars.get(4)); // center pillar
        dropScheduler.startDrops(alivePlayers);
        startFallMonitor();

        Bukkit.broadcastMessage("§ePillars game has begun!");
    }

    public void handleDeath(Player player) {
        alivePlayers.remove(player.getUniqueId());
        teleportToSpectatorLobby(player);

        if (alivePlayers.size() == 1) {
            UUID winnerId = alivePlayers.iterator().next();
            Player winner = Bukkit.getPlayer(winnerId);
            showWinSequence(winner);
            Bukkit.getScheduler().runTaskLater(plugin, this::startGame, 200L); // 10 sec
        }
    }

    public void showWinSequence(Player winner) {
        Location loc = winner.getLocation();
        World world = loc.getWorld();

        // Title message
        winner.sendTitle("§6Victory!", "§eYou are the last one standing!", 10, 60, 10);

        // Broadcast
        Bukkit.broadcastMessage("§6⚔ " + winner.getName() + " has won the Pillars match! ⚔");

        // Fireworks
        for (int i = 0; i < 5; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Firework fw = world.spawn(loc, Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.ORANGE)
                    .withFade(Color.YELLOW)
                    .with(FireworkEffect.Type.STAR)
                    .trail(true)
                    .flicker(true)
                    .build());
                meta.setPower(2);
                fw.setFireworkMeta(meta);
            }, i * 20L);
        }

        // Lightning
        world.strikeLightningEffect(loc);
    }

    public void teleportToSpectatorLobby(Player player) {
        World world = Bukkit.getWorlds().get(0);
        Location base = new Location(world, 0, 250, 0); // fixed location

        // Build barrier floor
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                world.getBlockAt(base.clone().add(x, 0, z)).setType(Material.BARRIER);
            }
        }

        // Build barrier walls
        for (int y = 1; y <= 5; y++) {
            for (int x = -5; x <= 5; x++) {
                world.getBlockAt(base.clone().add(x, y, -5)).setType(Material.BARRIER);
                world.getBlockAt(base.clone().add(x, y, 5)).setType(Material.BARRIER);
            }
            for (int z = -5; z <= 5; z++) {
                world.getBlockAt(base.clone().add(-5, y, z)).setType(Material.BARRIER);
                world.getBlockAt(base.clone().add(5, y, z)).setType(Material.BARRIER);
            }
        }

        player.teleport(base.clone().add(0, 1, 0));
        player.sendMessage("§7You are now spectating from the sky lobby.");
    }

    public void startFallMonitor() {
        stopFallMonitor(); // cancel previous if running

        fallMonitorTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (UUID id : new HashSet<>(alivePlayers)) {
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline()) {
                    if (p.getLocation().getY() < platformY - 30) {
                        p.setHealth(0.0); // instant death
                        p.sendMessage("§cYou fell too far and were eliminated!");
                    }
                }
            }
        }, 0L, 20L); // every second
    }

    public void stopFallMonitor() {
        if (fallMonitorTaskId != -1) {
            Bukkit.getScheduler().cancelTask(fallMonitorTaskId);
            fallMonitorTaskId = -1;
        }
    }

    public void cleanup() {
        dropScheduler.stopDrops();
        stopFallMonitor();
    }
}
package com.yourname.pillars;

import com.yourname.pillars.commands.PillarsCommand;
import com.yourname.pillars.game.GameManager;
import com.yourname.pillars.listeners.JoinQuitListener;
import com.yourname.pillars.listeners.PlayerDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PillarsPlugin extends JavaPlugin {
    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        gameManager = new GameManager(this);
        getCommand("pillars").setExecutor(new PillarsCommand(gameManager));
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
    }

    @Override
    public void onDisable() {
        gameManager.cleanup();
    }
}
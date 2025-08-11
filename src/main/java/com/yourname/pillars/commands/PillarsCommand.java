package com.yourname.pillars.commands;

import com.yourname.pillars.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PillarsCommand implements CommandExecutor {
    private final GameManager gameManager;

    public PillarsCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        gameManager.startGame();
        sender.sendMessage("§aPillars game started!");
        return true;
    }
}
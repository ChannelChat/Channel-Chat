package com.feildmaster.ChanChat.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface ChatInterface extends CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

    public void invalidCommand(CommandSender sender, String name);
}

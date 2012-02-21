package com.feildmaster.channelchat.command.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface Executor extends CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

    public void invalidCommand(CommandSender sender, String name);
}

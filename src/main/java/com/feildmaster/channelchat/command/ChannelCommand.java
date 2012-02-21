package com.feildmaster.channelchat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public abstract class ChannelCommand extends Command {
    protected final Plugin plugin;

    public ChannelCommand(String name, Plugin plugin) {
        super(name); // Name, Description, Usage, Aliases
        this.plugin = plugin;
    }

    public abstract void execute(CommandSender sender, String[] args, String[] flags);

    public abstract String getPermission();
}

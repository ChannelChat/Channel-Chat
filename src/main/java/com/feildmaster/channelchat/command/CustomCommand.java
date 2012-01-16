package com.feildmaster.channelchat.command;

import org.bukkit.command.CommandSender;

public abstract class CustomCommand {
    private final String name;
    private final String[] aliases;

    public CustomCommand(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void execute(CommandSender sender, String[] args, String[] flags);

    public abstract String getPermission();

    public int minArgs() {
        return 0;
    }

    public int maxArgs() {
        return -1;
    }
}

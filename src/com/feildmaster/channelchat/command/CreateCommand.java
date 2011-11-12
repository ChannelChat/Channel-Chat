package com.feildmaster.channelchat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateCommand extends BaseCommands {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0 || args[0].equals("?"))
            invalidCommand(sender, label);
        else
            createChannel(args[0], sender);

        return true;
    }

    public void invalidCommand(CommandSender sender, String name) {
        sender.sendMessage(ChatColor.RED+"Syntax: /"+name.toLowerCase()+" <channel>");
    }

}

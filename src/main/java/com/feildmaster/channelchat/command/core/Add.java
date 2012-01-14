package com.feildmaster.channelchat.command.core;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Add extends BaseCommands {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        if(args.length == 0 || args[0].equals("?"))
            invalidCommand(sender, label);
        else
            for(String name : args)
                addPlayer((Player)sender, name);

        return true;
    }

    public void invalidCommand(CommandSender sender, String name) {
        sender.sendMessage(ChatColor.RED+"Syntax: /"+name.toLowerCase()+" <players...>");
    }
}

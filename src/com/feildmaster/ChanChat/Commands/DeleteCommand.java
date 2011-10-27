package com.feildmaster.chanchat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand extends BaseCommands {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            if(isPlayer(sender))
                deleteActiveChannel((Player)sender);
            else
                invalidCommand(sender, label);
        } else if(args[0].equals("?"))
            invalidCommand(sender, label);
        else
            for(String name : args)
                deleteChannel(name, sender);

        return true;
    }

    public void invalidCommand(CommandSender sender, String name) {
        sender.sendMessage(ChatColor.RED+"Syntax: /"+name.toLowerCase()+
                (sender instanceof Player ? " [channels...]":" <channels...>"));
    }

}

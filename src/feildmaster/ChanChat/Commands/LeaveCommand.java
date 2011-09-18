package feildmaster.ChanChat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends BaseCommands{

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        if(args.length == 0)
            leaveActiveChannel((Player)sender);
        else if(args[0].equals("?"))
            invalidCommand(sender, label);
        else
            for(String chan : args)
                leaveChannel(chan, (Player)sender);

        return true;
    }

    public void invalidCommand(CommandSender sender, String name) {
        // String.format("Format String", "Object", "Object");
        sender.sendMessage(ChatColor.RED+"Syntax: /"+name.toLowerCase()+" [channels...]");
    }

}

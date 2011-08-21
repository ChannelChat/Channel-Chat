package feildmaster.ChanChat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends BaseCommands {
    String playerCommands = "create, delete, join, leave, add, set, list, who";
    String serverCommands = "create, delete";
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        int size = args.length;

        if(size != 0 && args[0].equalsIgnoreCase("reload")) // Reload handler
            return reload();

        if (size==1) {
            if (!isReserved(args[0]))
                createChannel(args[0], sender);

            else if(args[0].equalsIgnoreCase("leave") && isPlayer(sender))
                leaveActiveChannel((Player)sender);

            else if(args[0].equalsIgnoreCase("list") && isPlayer(sender))
                listChannels((Player)sender);
                    
            else if(args[0].equalsIgnoreCase("delete") && isPlayer(sender))
                deleteActiveChannel((Player)sender);

            else if(args[0].equalsIgnoreCase("who") && isPlayer(sender))
                getChannelMembers((Player)sender);
            else
                invalidCommand(sender, name);

        } else if (size == 2) {
            if(args[0].equalsIgnoreCase("create"))
                createChannel(args[1], sender);

            else if(args[0].equalsIgnoreCase("set") && isPlayer(sender))
                setChannel(args[1], (Player) sender);

            else if(args[0].equalsIgnoreCase("join") && isPlayer(sender))
                joinChannel(args[1], (Player)sender);

            else if(args[0].equalsIgnoreCase("leave") && isPlayer(sender))
                leaveChannel(args[1], (Player)sender);

            else if(args[0].equalsIgnoreCase("add") && isPlayer(sender))
                addPlayer((Player)sender, args[1]);

            else if(args[0].equalsIgnoreCase("delete"))
                deleteChannel(args[1], sender);

            else if(args[0].equalsIgnoreCase("who") && isPlayer(sender))
                getChannelMembers(args[1], (Player)sender);

            else
                invalidCommand(sender, name);

        } else if (channelExists(args[0]) && isPlayer(sender) && size > 1)
            quickChat(args[0], (Player)sender, args);

        else
            invalidCommand(sender, name);

        return true;
    }
    
    public void invalidCommand(CommandSender sender, String name) {
        sender.sendMessage(ChatColor.RED+"Syntax: /"+name.toLowerCase()+" "+ChatColor.GRAY+"<command>"+ChatColor.RED+" [args...]");
        sender.sendMessage(ChatColor.GRAY+(isPlayer(sender)?playerCommands:serverCommands));
    }
}

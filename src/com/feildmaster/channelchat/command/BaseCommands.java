package com.feildmaster.channelchat.command;

import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.feildmaster.channelchat.channel.ChannelManager.*;

// TODO: admin commands
// TODO: clean this more
public abstract class BaseCommands implements ChatExecutor {
    // "list" commands
    protected void listChannels(Player player, Boolean mem) {
        StringBuilder string = new StringBuilder();
        boolean color = true;
        for(Channel chan : getManager().getChannels())
            if(chan.isListed())
                string.append(string.length()!=0?", ":"").append((color=!color)?ChatColor.GRAY:ChatColor.WHITE).append(chan.isMember(player)?"*":"").append(chan.getName());
        player.sendMessage("Channels: "+string.toString());
    }

    // "quick chat"
    protected void quickChat(String name, Player player, String[] args) {
        Channel chan = getManager().getChannel(name);
        if(chan == null)
            player.sendMessage(info("Channel does not exist"));
        else if(chan.isMember(player)) {
            StringBuilder str = new StringBuilder();
            for(int x=1; x<args.length; x++)
                str.append(x!=1?" ":"").append(args[x]);

            getManager().sendMessage(player, chan, str.toString());
        } else
            player.sendMessage(info("You are not a member of \""+chan.getName()+".\""));
    }

    // "who" commands
    protected void getChannelMembers(String name, Player player) {
        Channel chan = getManager().getChannel(name);

        if(getManager().channelExists(name) && chan.isMember(player)) {
            StringBuilder string = new StringBuilder();
            boolean color = true;
            if(chan.getMembers(player).size() > 1)
                for(String p : chan.getMembers(player))
                    string.append(string.length()!=0?", ":"").append((color=!color)?ChatColor.GRAY:ChatColor.WHITE).append(ChatColor.stripColor(Chat.plugin().getServer().getPlayer(p).getDisplayName()));
            else
                string.append(ChatColor.WHITE).append("Only you");
            player.sendMessage(string.insert(0, ChatColor.GRAY+"Members: ").toString());
        } else
            player.sendMessage(ChatColor.GRAY+"The channel doesn't exist!");
    }
    protected void getChannelMembers(Player player) {
        getChannelMembers(getManager().getActiveName(player), player);
    }

    // "create" commands
    protected void createChannel(String name, CommandSender sender) {
        if(getManager().channelExists(name)) {
            sender.sendMessage("Channel Already Exists!");
            return;
        } else if(getManager().isReserved(name)) {
            sender.sendMessage("Channel Blacklisted.");
            return;
        }

        Channel chan = null;

        if(sender instanceof Player) {
            if(!((Player)sender).hasPermission("ChanChat.create")){
                sender.sendMessage("You can't do that.");
                return;
            }

            // !!! Should handle this in the manager
            chan = getManager().createChannel(name, Channel.Type.Private);
            Player player = (Player)sender;

            chan.setOwner(player);
            chan.addMember(player);

            // !!! Channel Create Event

            if(getManager().getActiveName(player) == null)
                getManager().setActiveChannel(player, chan);

            getManager().addChannel(chan);
        } else
            getManager().addChannel(chan = getManager().createChannel(name, Channel.Type.Global));

        chan.sendMessage(" Created");
    }

    // "delete" commands
    protected void deleteActiveChannel(Player player) {
        deleteChannel(getManager().getActiveChannel(player), player);
    }
    protected void deleteChannel(String name, CommandSender sender) {
        if(getManager().channelExists(name)) {
            deleteChannel(getManager().getChannel(name), sender);
        } else
            sender.sendMessage("The channel doesn't exists!");
    }
    protected void deleteChannel(Channel channel, CommandSender sender) {
        if(getManager().channelExists(channel)) {
            if(sender instanceof Player) {
                // !!! Should handle this in the manager ?
                if (channel.isOwner((Player)sender) || sender.hasPermission("ChanChat.admin"))
                    /// !!! ChannelDeleteEvent?
                    getManager().delChannel(channel);
                else
                    sender.sendMessage("You can't do that.");
            } else
                getManager().delChannel(channel);
        }
    }

    // "join" commands
    protected void joinChannel(String name, Player player) {
        if(getManager().channelExists(name)) {
            Channel chan = getManager().getChannel(name);
            if(chan.isMember(player))
                player.sendMessage(ChatColor.GRAY+"You are already in \""+chan.getName()+".\"");
            else {
                if(chan.getPass() != null) { // !!! Move this to an event? :o
                    player.sendMessage(ChatColor.GRAY+"["+chan.getName()+"] Please enter the password");
                    getManager().addToWaitlist(player, name);
                } else {
                    // !!! Channel Join Event
                    chan.addMember(player, true);
                }
            }
        } else
            createChannel(name, player);
    }

    // "leave" commands
    protected void leaveChannel(String name, Player player) {
        if(name.equalsIgnoreCase("all"))
            leaveAll(player);
        else if(getManager().channelExists(name))
            leaveChannel(player, getManager().getChannel(name));
        else
            player.sendMessage(error("Channel \""+name+"\" doesn't exist."));
    }
    protected void leaveActiveChannel(Player player) {
        Channel chan = getManager().getActiveChannel(player);
        if(chan != null) {
            leaveChannel(player, chan);
        } else
            player.sendMessage("You are not in any channels to leave!");
    }
    private void leaveChannel(Player player, Channel chan) {
         // !!! LeaveChannelEvent?
        chan.delMember(player, true);
    }
    private void leaveAll(Player player) {
        for(Channel c : getManager().getJoinedChannels(player))
            c.delMember(player); // Go through leaveChannel(player, c)?

        player.sendMessage(info("You have left all channels."));
    }

    // "add" commands
    protected void addPlayer(Player player, String i) {
        Player added = Chat.plugin().getServer().getPlayer(i);
        if(added == null) {
            player.sendMessage(error("Player ["+i+"] not found"));
            return;
        }

        Channel chan = getManager().getActiveChannel(player);

        if(chan == null)
            player.sendMessage(error("Active channel not set, or you have not joined a channel."));
        else if(chan.isMember(player) && !chan.isMember(added)) {
            chan.sendMessage(info(ChatColor.stripColor(added.getDisplayName())+
                    " has been added by "+ChatColor.stripColor(player.getDisplayName())));
            // !!! ChannelInviteEvent
            chan.addMember(added);
            added.sendMessage(info("You have been added to \""+chan.getName()+".\""));
        } else if (chan.isMember(player) && chan.isMember(added))
            player.sendMessage(info("Player is already in channel \""+chan.getName()+".\""));

    }

    // "set" commands
    protected void setChannel(String name, Player player) {
        if(getManager().channelExists(name)) {
            Channel chan = getManager().getChannel(name);
            if(chan.isMember(player)) {
                getManager().setActiveChannel(player, chan);
                player.sendMessage(info("Now talking in \""+chan.getName()+".\""));
            } else
                player.sendMessage(info("You are not in \""+chan.getName()+".\""));
        } else
            player.sendMessage(error("Channel \""+name+"\" doesn't exist."));
    }

    // Reload
    protected Boolean reload(CommandSender sender) {
        if(isPlayer(sender)) {
            Player p = (Player)sender;
            if(p.hasPermission("ChanChat.reload")) {
                Chat.plugin().reload();
                p.sendMessage("[CC] Reloaded");
            }
        } else {
            Chat.plugin().reload();
            sender.sendMessage("[CC] Reloaded");
        }
        return true;
    }

    //
    protected void replyActive(Player player) {
        player.sendMessage("Active Channel: "+getManager().getActiveName(player));
    }

    // Booleans
    protected Boolean isReserved(String name) {
        return getManager().isReserved(name);
    }
    protected Boolean channelExists(String name) {
        return getManager().channelExists(name);
    }
    protected Boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected Channel getChannel(String name) {
        return getManager().getChannel(name);
    }

    public String error(String msg) {
        return ChatColor.RED+msg;
    }
    public String info(String msg) {
        return ChatColor.YELLOW+msg;
    }
}

package com.feildmaster.ChanChat.Commands;

import com.feildmaster.ChanChat.Chan.Channel;
import com.feildmaster.ChanChat.Chan.ChannelManager;
import com.feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


// TODO: admin commands
// TODO: clean this more
public abstract class BaseCommands implements ChatInterface {
    private final ChannelManager cm = ChatUtil.getCM();

    // "list" commands
    protected void listChannels(Player player, Boolean mem) {
        StringBuilder string = new StringBuilder();
        boolean color = true;
        for(Channel chan : cm.getChannels())
            if(chan.isListed())
                string.append(string.length()!=0?", ":"").append((color=!color)?ChatColor.GRAY:ChatColor.WHITE).append(chan.isMember(player)?"*":"").append(chan.getName());
        player.sendMessage("Channels: "+string.toString());
    }

    // "quick chat"
    protected void quickChat(String name, Player player, String[] args) {
        Channel chan = cm.getChannel(name);
        if(chan == null)
            player.sendMessage(info("Channel does not exist"));
        else if(chan.isMember(player)) {
            StringBuilder str = new StringBuilder();
            for(int x=1; x<args.length; x++)
                str.append(x!=1?" ":"").append(args[x]);

            cm.sendMessage(player, chan, str.toString());
        } else
            player.sendMessage(info("You are not a member of \""+chan.getName()+".\""));
    }

    // "who" commands
    protected void getChannelMembers(String name, Player player) {
        Channel chan = cm.getChannel(name);

        if(cm.channelExists(name) && chan.isMember(player)) {
            StringBuilder string = new StringBuilder();
            boolean color = true;
            if(chan.getMembers(player).size() > 1)
                for(String p : chan.getMembers(player))
                    string.append(string.length()!=0?", ":"").append((color=!color)?ChatColor.GRAY:ChatColor.WHITE).append(ChatColor.stripColor(ChatUtil.getPlayer(p).getDisplayName()));
            else
                string.append(ChatColor.WHITE).append("Only you");
            player.sendMessage(string.insert(0, ChatColor.GRAY+"Members: ").toString());
        } else
            player.sendMessage(ChatColor.GRAY+"The channel doesn't exist!");
    }
    protected void getChannelMembers(Player player) {
        getChannelMembers(cm.getActiveName(player), player);
    }

    // "create" commands
    protected void createChannel(String name, CommandSender sender) {
        if(cm.channelExists(name)) {
            sender.sendMessage("Channel Already Exists!");
            return;
        } else if(cm.isReserved(name)) {
            sender.sendMessage("Channel Blacklisted.");
            return;
        }

        Channel chan = null;

        if(sender instanceof Player) {
            if(!((Player)sender).hasPermission("ChanChat.create")){
                sender.sendMessage("You can't do that.");
                return;
            }

            chan = cm.createChannel(name, Channel.Type.Private);
            Player player = (Player)sender;

            chan.setOwner(player);
            chan.addMember(player);

            if(cm.getActiveName(player) == null)
                cm.setActiveChan(player, chan.getName());

            cm.addChannel(chan);
        } else
            cm.addChannel(chan = cm.createChannel(name, Channel.Type.Global));

        chan.sendMessage(" Created");
    }

    // "delete" commands
    protected void deleteActiveChannel(Player player) {
        deleteChannel(cm.getActiveName(player), player);
    }
    protected void deleteChannel(String name, CommandSender sender) {
        if(cm.channelExists(name)) {
            Channel chan = cm.getChannel(name);
            if(sender instanceof Player) {
                if (chan.isOwner((Player)sender) || sender.hasPermission("ChanChat.admin"))
                    cm.delChannel(name);
                else
                    sender.sendMessage("You can't do that.");
            } else
                cm.delChannel(name);
        } else
            sender.sendMessage("The channel doesn't exists!");
    }

    // "join" commands
    protected void joinChannel(String name, Player player) {
        if(cm.channelExists(name)) {
            Channel chan = cm.getChannel(name);
            if(chan.isMember(player))
                player.sendMessage(ChatColor.GRAY+"You are already in \""+chan.getName()+".\"");
            else {
                if(chan.getPass() != null) {
                    player.sendMessage(ChatColor.GRAY+"["+chan.getName()+"] Please enter the password");
                    cm.addToWaitlist(player, name);
                } else
                    chan.addMember(player, true);
            }
        } else
            createChannel(name, player);
    }

    // "leave" commands
    protected void leaveChannel(String name, Player player) {
        if(name.equalsIgnoreCase("all"))
            leaveAll(player);
        else if(cm.channelExists(name))
            leaveChannel(player, cm.getChannel(name));
        else
            player.sendMessage(error("Channel \""+name+"\" doesn't exist."));
    }
    protected void leaveActiveChannel(Player player) {
        Channel chan = cm.getActiveChan(player);
        if(chan != null) {
            leaveChannel(player, chan);
        } else
            player.sendMessage("You are not in any channels to leave!");
    }
    private void leaveChannel(Player player, Channel chan) {
        chan.delMember(player, true);
    }
    private void leaveAll(Player player) {
        for(Channel c : cm.getJoinedChannels(player))
            c.delMember(player);

        player.sendMessage(info("You have left all channels."));
    }

    // "add" commands
    protected void addPlayer(Player player, String i) {
        Player added = ChatUtil.getPlayer(i);
        if(added == null) {
            player.sendMessage(error("Player ["+i+"] not found"));
            return;
        }

        Channel chan = cm.getActiveChan(player);

        if(chan == null)
            player.sendMessage(error("Active channel not set, or you have not joined a channel."));
        else if(chan.isMember(player) && !chan.isMember(added)) {
            chan.sendMessage(info(ChatColor.stripColor(added.getDisplayName())+
                    " has been added by "+ChatColor.stripColor(player.getDisplayName())));
            chan.addMember(added);
            added.sendMessage(info("You have been added to \""+chan.getName()+".\""));
        } else if (chan.isMember(player) && chan.isMember(added))
            player.sendMessage(info("Player is already in channel \""+chan.getName()+".\""));

    }

    // "set" commands
    protected void setChannel(String name, Player player) {
        if(cm.channelExists(name)) {
            Channel chan = cm.getChannel(name);
            if(chan.isMember(player)) {
                cm.setActiveByChan(player, chan);
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
                ChatUtil.reload();
                p.sendMessage("[CC] Reloaded");
            }
        } else {
            ChatUtil.reload();
            sender.sendMessage("[CC] Reloaded");
        }
        return true;
    }

    //
    protected void replyActive(Player player) {
        player.sendMessage("Active Channel: "+cm.getActiveName(player));
    }

    // Booleans
    protected Boolean isReserved(String name) {
        return cm.isReserved(name);
    }
    protected Boolean channelExists(String name) {
        return cm.channelExists(name);
    }
    protected Boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected Channel getChannel(String name) {
        return cm.getChannel(name);
    }

    public String error(String msg) {
        return ChatColor.RED+msg;
    }
    public String info(String msg) {
        return ChatColor.YELLOW+msg;
    }
}

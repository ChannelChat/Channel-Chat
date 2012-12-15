package com.feildmaster.channelchat.command.core;

import org.bukkit.Bukkit;
import com.feildmaster.channelchat.event.channel.*;
import com.feildmaster.channelchat.event.*;
import org.bukkit.command.ConsoleCommandSender;
import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import org.apache.commons.lang.StringUtils;

// TODO: admin command manager
// TODO: clean this more
public abstract class Base implements Executor {
    // "list" commands
    protected void listChannels(Player player, Boolean mem) {
        StringBuilder string = new StringBuilder();
        boolean color = true;
        for (Channel channel : getManager().getChannels()) {
            if (channel.isListed() || channel.isMember(player)) {
                if (string.length() != 0) {
                    string.append(", ");
                }
                if (color = !color) {
                    string.append(ChatColor.GRAY);
                } else {
                    string.append(ChatColor.WHITE);
                }
                if (channel.isMember(player)) {
                    string.append("*");
                }
                string.append(channel.getName());
            }
        }
        player.sendMessage("Channels: " + string.toString());
    }

    // "quick chat"
    protected void quickChat(String name, Player player, String[] args) {
        Channel channel = getManager().getChannel(name);
        if (channel == null) {
            player.sendMessage(info("Channel does not exist"));
        } else if (channel.isMember(player)) {
            getManager().sendMessage(player, channel, StringUtils.join(args, " "));
        } else {
            player.sendMessage(info("You are not a member of \"" + channel.getName() + ".\""));
        }
    }

    // "who" commands
    protected void getChannelMembers(String name, Player player) {
        Channel channel = getManager().getChannel(name);

        if (getManager().channelExists(name) && channel.isMember(player)) {
            StringBuilder string = new StringBuilder();
            boolean color = true;
            if (channel.getMembers(player).size() > 1) {
                for (String p : channel.getMembers(player)) {
                    if (p.equals(player.getName())) {
                        continue;
                    }

                    Player pl = Chat.plugin().getServer().getPlayer(p);
                    if (pl != null && player.canSee(pl)) {
                        String plName = pl.getDisplayName() == null ? p : ChatColor.stripColor(pl.getDisplayName());
                        string.append(string.length() != 0 ? ", " : "").append((color = !color) ? ChatColor.GRAY : ChatColor.WHITE).append(plName);
                    }
                }
                if (string.length() == 0) {
                    string.append(ChatColor.WHITE).append("Only you");
                }
            } else {
                string.append(ChatColor.WHITE).append("Only you");
            }
            player.sendMessage(string.insert(0, ChatColor.GRAY + "Members: ").toString());
        } else {
            player.sendMessage(ChatColor.GRAY + "The channel doesn't exist!");
        }
    }

    protected void getChannelMembers(Player player) {
        getChannelMembers(getManager().getActiveName(player), player);
    }

    // "create" commands
    protected void createChannel(String name, CommandSender sender) {
        if (getManager().channelExists(name)) {
            sender.sendMessage("Channel Already Exists!");
            return;
        } else if (getManager().isChannelReserved(name)) {
            sender.sendMessage("Channel Blacklisted.");
            return;
        }

        Channel channel;

        if (sender instanceof Player) {
            channel = getManager().createChannel(name, Channel.Type.Private);
            Player player = (Player) sender;

            channel.setOwner(player);
            channel.addMember(player);

            ChannelCreateEvent event = new ChannelCreateEvent(player, channel);

            if (!(sender.hasPermission("ChanChat.create") || Chat.plugin().getConfig().allowCreateChannels())) { // Allows for overriding permission
                event.setCancelled(true);
                event.setCancelReason("You can't do that.");
            }

            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                sendCancelMessage(player, event);
                return;
            }

            getManager().addChannel(channel);

            if (getManager().getActiveChannel(player) == null) {
                getManager().setActiveChannel(player, channel);
            }

            getManager().sendMessage(channel, "Created");
        } else if (sender instanceof ConsoleCommandSender) {
            getManager().addChannel(channel = getManager().createChannel(name, Channel.Type.Global));
            channel.sendMessage(" Created");
        }
    }

    // "delete" commands
    protected void deleteActiveChannel(Player player) {
        deleteChannel(getManager().getActiveChannel(player), player);
    }

    protected void deleteChannel(String name, CommandSender sender) {
        if (getManager().channelExists(name)) {
            deleteChannel(getManager().getChannel(name), sender);
        } else {
            sender.sendMessage("The channel doesn't exists!");
        }
    }

    private void deleteChannel(Channel channel, CommandSender sender) {
        if (getManager().channelExists(channel)) {
            if (sender instanceof Player) {
                ChannelDeleteEvent event = new ChannelDeleteEvent((Player) sender, channel);
                if (!(channel.isOwner((Player) sender) || sender.hasPermission("ChanChat.admin"))) {
                    event.setCancelled(true);
                    event.setCancelReason("You can't do that.");
                }

                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    sendCancelMessage(event.getPlayer(), event);
                    return;
                }

                getManager().deleteChannel(channel);
            } else {
                getManager().deleteChannel(channel);
            }
        }
    }

    // "join" commands
    protected void joinChannel(String name, Player player) {
        if (getManager().channelExists(name)) {
            Channel channel = getManager().getChannel(name);
            if (channel.isMember(player)) {
                player.sendMessage(ChatColor.GRAY + "You are already in \"" + channel.getName() + ".\"");
                return;
            }

            if (channel.getPass() != null) { // !!! Move this to an event? :o
                player.sendMessage(ChatColor.GRAY + "[" + channel.getName() + "] Please enter the password");
                getManager().addToWaitlist(player, name);
            } else {
                ChannelJoinEvent event = ChannelEventFactory.callChannelJoinEvent(player, channel, name);

                if (event.isCancelled()) {
                    sendCancelMessage(player, event);
                    return;
                }

                channel.addMember(player, true);

                if (Chat.plugin().getConfig().autoSet()) {
                    setChannel(channel, player);
                }
            }
        } else {
            createChannel(name, player);
        }
    }

    // "leave" commands
    protected void leaveChannel(String name, Player player) {
        if (name.equalsIgnoreCase("all")) {
            leaveAll(player);
        } else if (getManager().channelExists(name)) {
            leaveChannel(player, getManager().getChannel(name));
        } else {
            player.sendMessage(error("Channel \"" + name + "\" doesn't exist."));
        }
    }

    protected void leaveActiveChannel(Player player) {
        Channel chan = getManager().getActiveChannel(player);
        if (chan != null) {
            leaveChannel(player, chan);
        } else {
            player.sendMessage("You are not in any channels to leave!");
        }
    }

    private void leaveChannel(Player player, Channel channel) {
        CancelReason event = ChannelEventFactory.ChannelLeaveEvent(player, channel);
        if (event.isCancelled()) {
            if (event.getCancelReason() != null) {
                return;
            }
        }
        channel.delMember(player, true);
    }

    private void leaveAll(Player player) {
        for (Channel c : getManager().getJoinedChannels(player)) {
            c.delMember(player); // Go through leaveChannel(player, c)?
        }
        player.sendMessage(info("You have left all channels."));
    }

    // "add" commands
    protected void addPlayer(Player player, String invitee) {
        Player added = Chat.plugin().getServer().getPlayer(invitee);
        if (added == null) {
            player.sendMessage(error("Player [" + invitee + "] not found"));
            return;
        }

        Channel channel = getManager().getActiveChannel(player);

        if (channel == null) {
            player.sendMessage(error("Active channel not set, or you have not joined a channel."));
        } else if (channel.isMember(player) && channel.isMember(added)) {
            player.sendMessage(info("Player is already in channel \"" + channel.getName() + ".\""));
        } else if (channel.isMember(player) && !channel.isMember(added)) {
            CancelReason event = ChannelEventFactory.callChannelInviteEvent(added, player, channel);

            if (event.isCancelled()) {
                sendCancelMessage(added, event);
                return;
            }

            channel.sendMessage(info(ChatColor.stripColor(added.getDisplayName()) + " has been added by " + ChatColor.stripColor(player.getDisplayName())));

            channel.addMember(added);
            added.sendMessage(info("You have been added to \"" + channel.getName() + ".\""));
        }

    }

    // "set" commands
    protected void setChannel(String name, Player player) {
        if (getManager().channelExists(name)) {
            setChannel(getManager().getChannel(name), player);
        } else {
            player.sendMessage(error("Channel \"" + name + "\" doesn't exist."));
        }
    }

    private void setChannel(Channel channel, Player player) {
        if (getManager().channelExists(channel)) {
            if (channel.isMember(player)) {
                getManager().setActiveChannel(player, channel);
                player.sendMessage(info("Now talking in \"" + channel.getName() + ".\""));
            } else {
                player.sendMessage(info("You are not in \"" + channel.getName() + ".\""));
            }
        }
    }

    // Reload
    protected Boolean reload(CommandSender sender) {
        if (!sender.hasPermission("ChanChat.reload")) {
            return true;
        }
        Chat.plugin().reloadConfig();
        sender.sendMessage("[CC] Reloaded");
        return true;
    }

    protected void replyActive(Player player) {
        player.sendMessage("Active Channel: " + getManager().getActiveName(player));
    }

    // Booleans
    protected Boolean isReserved(String name) {
        return getManager().isChannelReserved(name);
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
        return ChatColor.RED + msg;
    }

    public String info(String msg) {
        return ChatColor.YELLOW + msg;
    }

    public void sendCancelMessage(Player player, CancelReason event) {
        if (event.getCancelReason() != null) {
            Chat.plugin().sendMessage(player, event.getCancelReason());
        }
    }
}

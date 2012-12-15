package com.feildmaster.channelchat.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.*;
import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import static com.feildmaster.channelchat.Chat.*;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import com.feildmaster.channelchat.event.player.ChannelPlayerChatEvent;
import org.apache.commons.lang.StringUtils;

/**
 * ChatListener <p /> Handles player chat
 */
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEarlyPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled() || event instanceof ChannelPlayerChatEvent) {
            return;
        }

        Player player = event.getPlayer();
        String msg = event.getMessage();

        if (getManager().inWaitlist(player)) { // TODO: Modularize
            Channel chan = getManager().getWaitingChan(player);
            if (chan.getPass().equals(msg)) {
                chan.addMember(player);
                chan.sendMessage(ChatColor.YELLOW + ChatColor.stripColor(player.getDisplayName()) + " has joined.");
                getManager().deleteFromWaitlist(player);
            } else if (msg.equalsIgnoreCase("cancel")) {
                getManager().deleteFromWaitlist(player);
            } else {
                player.sendMessage(ChatColor.GRAY + "Password incorrect, try again. (cancel to stop)");
            }
            event.setCancelled(true);
        } else if (msg.startsWith("#")) {
            msg = msg.substring(1);

            String[] args = msg.split(" ");
            Channel chan = getManager().getChannel(args[0]);

            if (args.length > 1 && chan != null) {
                if (chan.isMember(player)) {
                    getManager().sendMessage(player, chan, StringUtils.join(args, " "), event.isAsynchronous());
                } else {
                    player.sendMessage("Not member of channel");
                }
            } else if (args.length == 1 && chan != null) {
                getManager().setActiveChannel(player, chan);
                player.sendMessage(info("Now talking in \"" + chan.getName() + ".\""));
            } else if (args.length >= 1 && chan == null) {
                player.sendMessage(ChatColor.RED + "Channel not found");
            } else {
                player.sendMessage(ChatColor.RED + "Pleasy specify a channel.");
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event instanceof ChannelPlayerChatEvent) {
            ((ChannelPlayerChatEvent) event).getChannel().handleEvent(event);
            return;
        }

        Player player = event.getPlayer();

        if (getManager().getJoinedChannels(player).isEmpty()) {
            player.sendMessage(info("You are not in any channels."));
            event.setCancelled(true);
            return;
        }

        getManager().checkActive(player);
        Channel chan = getManager().getChannel(event);

        if (chan == null) {
            plugin().getServer().getLogger().info("[ChannelChat] Error Occured That Shouldn't Happen (chatListener.java)");
            player.sendMessage("[ChannelChat] Error Occured That Shouldn't Happen");
            event.setCancelled(true);
            return;
        }

        chan.handleEvent(event);
    }
}

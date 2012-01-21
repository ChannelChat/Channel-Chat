package com.feildmaster.channelchat.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.*;
import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import static com.feildmaster.channelchat.Chat.*;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;

/**
 * ChatListener
 * <p>
 * Handles player chat
 */
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEarlyPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        String msg = event.getMessage();

        if(getManager().inWaitlist(player)) {
            Channel chan =getManager().getWaitingChan(player);
            if(chan.getPass().equals(msg)) {
                chan.addMember(player);
                chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
                getManager().deleteFromWaitlist(player);
            } else if(msg.equalsIgnoreCase("cancel")) {
                getManager().deleteFromWaitlist(player);
            } else
                player.sendMessage(ChatColor.GRAY+"Password incorrect, try again. (cancel to stop)");
            event.setCancelled(true);
        } else if (msg.startsWith("#")) {
            msg = msg.substring(1);

            String[] args = msg.split(" ");

            if(args.length > 1 && getManager().channelExists(args[0])) {
                StringBuilder msg1 = new StringBuilder();
                for(int x = 1; x<args.length; x++)
                    msg1.append(msg1.length() == 0?"":" ").append(args[x]);
                if(msg1.length() > 0 && getManager().getChannel(args[0]).isMember(player))
                    getManager().sendMessage(player, args[0], msg1.toString());
                else
                    player.sendMessage("Not member of channel");
            } else if (args.length == 1 && getManager().channelExists(args[0]))
                player.sendMessage(ChatColor.YELLOW+"Please include a message");
            else if (args.length >= 1 && !getManager().channelExists(args[0]))
                player.sendMessage(ChatColor.RED+"Channel not found");
            else
                player.sendMessage(ChatColor.RED+"Pleasy specify a channel.");

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        if(getManager().getJoinedChannels(player).isEmpty()) {
            player.sendMessage(info("You are not in any channels."));
            event.setCancelled(true);
            return;
        }

        getManager().checkActive(player);
        Channel chan = getManager().getChannel(event);

        if(chan == null) {
            plugin().getServer().getLogger().info("[ChannelChat] Error Occured That Shouldn't Happen (chatListener.java)");
            player.sendMessage("[ChannelChat] Error Occured That Shouldn't Happen");
            event.setCancelled(true);
            return;
        }

        chan.handleEvent(event);
    }
}

package com.feildmaster.channelchat.listener;

import com.feildmaster.channelchat.channel.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;


public class EarlyChatListener extends PlayerListener {
    public void onPlayerChat(PlayerChatEvent event) {
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
}

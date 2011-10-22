package com.feildmaster.ChanChat.Listeners;

import com.feildmaster.ChanChat.Chan.*;
import com.feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class EarlyChatListener extends PlayerListener {
    private ChannelManager cm = ChatUtil.getCM();

    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        String msg = event.getMessage();

        if(cm.inWaitlist(player)) {
            Channel chan =cm.getWaitingChan(player);
            if(chan.getPass().equals(msg)) {
                chan.addMember(player);
                chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
                cm.deleteFromWaitlist(player);
            } else if(msg.equalsIgnoreCase("cancel")) {
                cm.deleteFromWaitlist(player);
            } else
                player.sendMessage(ChatColor.GRAY+"Password incorrect, try again. (cancel to stop)");
            event.setCancelled(true);
        } else if (msg.startsWith("#")) {
            String chan = msg.substring(1, msg.indexOf(' '));
            cm.sendMessage(player, chan, msg.substring(msg.indexOf(' ')+1));
        }
    }
}

package com.feildmaster.channelchat.listener;

import com.feildmaster.channelchat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.ChatColor;
import com.feildmaster.channelchat.channel.Channel;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import static com.feildmaster.channelchat.Chat.format;

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getManager().joinAutoChannels(player);
        getManager().checkActive(player);
        player.sendMessage(format(ChatColor.WHITE, ChatColor.YELLOW+"Your active channel is: "+getManager().getActiveName(player)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(Chat.plugin().getConfig().persistRelog()) return;
        Player player = event.getPlayer();
        for(Channel chan : getManager().getJoinedChannels(player)) {
           chan.delMember(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled()) return;
        //if(Chat.plugin().getConfig().persistRelog()) return;

        Player player = event.getPlayer();
        for(Channel chan : getManager().getJoinedChannels(player)) {
           chan.delMember(player);
        }
    }
}

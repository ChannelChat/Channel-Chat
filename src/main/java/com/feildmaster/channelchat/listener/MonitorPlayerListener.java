package com.feildmaster.channelchat.listener;

import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.ChatColor;
import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import static com.feildmaster.channelchat.Chat.format;

public class MonitorPlayerListener extends PlayerListener {
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : getManager().getAutoChannels())
            chan.addMember(player);
        getManager().checkActive(player);
        player.sendMessage(format(ChatColor.WHITE, ChatColor.YELLOW+"Your active channel is: "+getManager().getActiveName(player)));
    }
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : getManager().getJoinedChannels(player))
           chan.delMember(player);
    }
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : getManager().getJoinedChannels(player))
           chan.delMember(player);
    }
}

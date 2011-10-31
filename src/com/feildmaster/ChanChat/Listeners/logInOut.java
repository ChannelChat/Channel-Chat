package com.feildmaster.chanchat.Listeners;

import com.feildmaster.chanchat.Chan.Channel;
import com.feildmaster.chanchat.Chan.ChannelManager;
import com.feildmaster.chanchat.Chat;
import com.feildmaster.chanchat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogInOut extends PlayerListener {
    ChannelManager cm = Chat.getChannelManager();

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : cm.getAutoChannels())
            chan.addMember(player);
        cm.checkActive(player);
        player.sendMessage(ChatUtil.format(ChatColor.WHITE, ChatColor.YELLOW+"Your active channel is: "+cm.getActiveName(player)));
    }
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : cm.getJoinedChannels(player))
           chan.delMember(player);
    }
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : cm.getJoinedChannels(player))
           chan.delMember(player);
    }
}

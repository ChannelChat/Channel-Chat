package com.feildmaster.ChanChat.Listeners;

import com.feildmaster.ChanChat.Chan.Channel;
import com.feildmaster.ChanChat.Chan.ChannelManager;
import com.feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 *  logInOut:
 *      Handles players loging in and out
 */
public class logInOut extends PlayerListener {
    ChannelManager cm = ChatUtil.getCM();

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for(Channel chan : cm.getAutoChannels())
            chan.addMember(player);
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

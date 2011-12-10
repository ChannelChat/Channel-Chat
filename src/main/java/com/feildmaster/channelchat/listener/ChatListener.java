package com.feildmaster.channelchat.listener;

import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.Chat;
import com.feildmaster.channelchat.event.player.ChannelPlayerChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import static com.feildmaster.channelchat.Chat.info;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;

/*
 * ChatListener
 * <p>
 * Handles player chat
 */
public class ChatListener extends PlayerListener {
    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        if(getManager().getJoinedChannels(player).isEmpty()) {
            player.sendMessage(info("You are not in any channels."));
            event.setCancelled(true);
            return;
        }

        getManager().checkActive(player);
        Channel chan = event instanceof ChannelPlayerChatEvent ? ((ChannelPlayerChatEvent)event).getChannel() : getManager().getActiveChannel(player);

        if(chan == null) {
            Chat.plugin().getServer().getLogger().info("[ChannelChat] Error Occured That Shouldn't Happen (chatListener.java)");
            player.sendMessage("[ChannelChat] Error Occured That Shouldn't Happen");
            event.setCancelled(true);
            return;
        }

        chan.handleEvent(event);
    }
}

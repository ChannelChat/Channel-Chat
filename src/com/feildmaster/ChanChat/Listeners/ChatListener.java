package com.feildmaster.chanchat.Listeners;

import com.feildmaster.chanchat.Chan.Channel;
import com.feildmaster.chanchat.Chan.ChannelManager;
import com.feildmaster.chanchat.Events.ChannelPlayerChatEvent;
import com.feildmaster.chanchat.Util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/*
 * ChatListener
 * <p>
 * Handles player chat
 */
public class ChatListener extends PlayerListener {
    private final ChannelManager cm = ChatUtil.getCM();

    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        if(cm.getJoinedChannels(player).isEmpty()) {
            player.sendMessage(ChatUtil.info("You are not in any channels."));
            event.setCancelled(true);
            return;
        }

        cm.checkActive(player);
        Channel chan = event instanceof ChannelPlayerChatEvent ? ((ChannelPlayerChatEvent)event).getChannel() : cm.getActiveChannel(player);

        if(chan == null) {
            ChatUtil.log().info("[ChannelChat] Error Occured That Shouldn't Happen (chatListener.java)");
            player.sendMessage("[ChannelChat] Error Occured That Shouldn't Happen");
            event.setCancelled(true);
            return;
        }

        // !!! I want to format outside of the handleEvent...
        // This wasn't supposed to be released! It breaks peoples formats!!
        //event.setMessage(chan.getChatColor()+event.getMessage());

        chan.handleEvent(event);
    }
}

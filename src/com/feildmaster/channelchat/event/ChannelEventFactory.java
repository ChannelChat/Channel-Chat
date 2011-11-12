package com.feildmaster.channelchat.event;

import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.event.channel.*;
import org.bukkit.entity.Player;
import static com.feildmaster.channelchat.channel.ChannelManager.callEvent;

public class ChannelEventFactory {
    public static void callReloadEvent() {
        // Reload will actually have parameters at some point?
        // !!! ie: /cc reload <module>
        callEvent(new ReloadEvent());
    }

    public static void callSaveEvent() {
        callEvent(new SaveEvent());
    }

    // !!! Set Join/Deny message
    public static ChannelJoinEvent callChannelJoinEvent(Player player, Channel channel, String message) {
        return (ChannelJoinEvent) callEvent(new ChannelJoinEvent(player, channel));
    }
    
    public static ChannelJoinEvent callChannelInviteEvent(Player invited, Player inviter, Channel channel) {
        return (ChannelJoinEvent) callEvent(new ChannelInviteEvent(invited, inviter, channel));
    }

    // !!! Set leave message
    public ChannelLeaveEvent ChannelLeaveEvent(Player player, Channel channel, String message) {
        ChannelLeaveEvent event = new ChannelLeaveEvent(player, channel);
        callEvent(event);
        return event;
    }

    public ChannelCreateEvent ChannelCreateEvent(Player player, Channel channel) {
        return (ChannelCreateEvent) callEvent(new ChannelCreateEvent(player, channel));
    }
}

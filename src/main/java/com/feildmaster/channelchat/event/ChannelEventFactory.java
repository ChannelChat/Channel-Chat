package com.feildmaster.channelchat.event;

import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.event.channel.*;
import org.bukkit.entity.Player;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;

public class ChannelEventFactory {
    public static void callReloadEvent() {
        // Reload will actually have parameters at some point?
        // ie: /cc reload <module>
        getManager().callEvent(new ReloadEvent());
    }

    public static void callSaveEvent() {
        getManager().callEvent(new SaveEvent());
    }

    public static ChannelJoinEvent callChannelJoinEvent(Player player, Channel channel, String message) {
        ChannelJoinEvent event = new ChannelJoinEvent(player, channel);
        getManager().callEvent(event);
        return event;
    }

    public static ChannelInviteEvent callChannelInviteEvent(Player invited, Player inviter, Channel channel) {
        ChannelInviteEvent event = new ChannelInviteEvent(invited, inviter, channel);
        getManager().callEvent(event);
        return event;
    }

    public static ChannelLeaveEvent ChannelLeaveEvent(Player player, Channel channel) {
        ChannelLeaveEvent event = new ChannelLeaveEvent(player, channel);
        getManager().callEvent(event);
        return event;
    }

    public static ChannelCreateEvent ChannelCreateEvent(Player player, Channel channel) {
        ChannelCreateEvent event = new ChannelCreateEvent(player, channel);
        getManager().callEvent(event);
        return event;
    }
}

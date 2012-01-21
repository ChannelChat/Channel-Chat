package com.feildmaster.channelchat.event;

import org.bukkit.Bukkit;
import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.event.channel.*;
import org.bukkit.entity.Player;

public class ChannelEventFactory {
    public static ReloadEvent callReloadEvent() {
        // Reload will actually have parameters at some point?
        // ie: /cc reload <module>
        ReloadEvent event = new ReloadEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static SaveEvent callSaveEvent() {
        SaveEvent event = new SaveEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static ChannelJoinEvent callChannelJoinEvent(Player player, Channel channel, String message) {
        ChannelJoinEvent event = new ChannelJoinEvent(player, channel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static ChannelInviteEvent callChannelInviteEvent(Player invited, Player inviter, Channel channel) {
        ChannelInviteEvent event = new ChannelInviteEvent(invited, inviter, channel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static ChannelLeaveEvent ChannelLeaveEvent(Player player, Channel channel) {
        ChannelLeaveEvent event = new ChannelLeaveEvent(player, channel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static ChannelCreateEvent ChannelCreateEvent(Player player, Channel channel) {
        ChannelCreateEvent event = new ChannelCreateEvent(player, channel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}

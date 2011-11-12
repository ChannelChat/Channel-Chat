package com.feildmaster.channelchat.event;

import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.event.channel.ChannelCreateEvent;
import com.feildmaster.channelchat.event.channel.ChannelJoinEvent;
import com.feildmaster.channelchat.event.channel.ChannelLeaveEvent;
import com.feildmaster.channelchat.event.channel.ReloadEvent;
import com.feildmaster.channelchat.event.channel.SaveEvent;
import org.bukkit.entity.Player;
import static com.feildmaster.channelchat.channel.ChannelManager.callEvent;

public class ChannelEventFactory {
    public static ReloadEvent callReloadEvent() {
        ReloadEvent event = new ReloadEvent();
        callEvent(event); // Reload will actually have parameters at some point. :D
        return event;
    }

    public static void callSaveEvent() {
        callEvent(new SaveEvent());
    }

    public static ChannelJoinEvent callChannelJoinEvent(Player player, Channel channel, String message) {
        ChannelJoinEvent event = new ChannelJoinEvent(player, channel);

        // !!! Set Join message
        // !!! Set Deny message

        callEvent(event);
        return event;
    }

    public ChannelLeaveEvent ChannelLeaveEvent(Player player, Channel channel, String message) {
        ChannelLeaveEvent event = new ChannelLeaveEvent(player, channel);

        // !!! Set leave message

        callEvent(event);
        return event;
    }

    public ChannelCreateEvent ChannelCreateEvent(Player player, Channel channel) {
        ChannelCreateEvent event = new ChannelCreateEvent(player, channel);
        callEvent(event);
        return event;
    }
}

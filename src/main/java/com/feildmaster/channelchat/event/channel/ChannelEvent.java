package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.event.Event;

public class ChannelEvent extends Event {
    private Channel channel;

    protected ChannelEvent(Channel chan) {
        channel = chan;
    }

    public Channel getChannel() {
        return channel;
    }
}

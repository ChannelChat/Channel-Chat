package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.event.Event;

public class ChannelEvent extends Event {
    private Channel channel;
    private Type type;

    protected ChannelEvent(Channel chan) {
        channel = chan;
    }
    @Deprecated
    protected ChannelEvent(Type type) {}

    public Channel getChannel() {
        return channel;
    }

    @Deprecated
    public enum Type {
        /**
         * Called when a player creates a channel.
         */
        CREATE,
        /**
         * Called when a player deletes a channel
         */
        DELETE,
        /**
         * Called when a player joins a channel
         */
        JOIN,
        /**
         * Called when a player leaves a channel
         */
        LEAVE;
    }

    public Type getEventType() {
        return type;
    }
}

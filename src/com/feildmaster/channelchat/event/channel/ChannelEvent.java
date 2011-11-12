package com.feildmaster.channelchat.event.channel;

import org.bukkit.event.Event;

public class ChannelEvent extends Event {
    private Type type;
    protected ChannelEvent(Type type) {
        super("ChannelEvent");
        this.type = type;
    }

    public enum Type {
        /**
         * Called on /cc reload
         */
        RELOAD,
        /**
         * Called when ChannelChat is disabled, or a save call is made.
         */
        SAVE,
        /**
         * Called when a player creates a channel.
         */
        CREATE,
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

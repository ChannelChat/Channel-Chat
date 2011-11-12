package com.feildmaster.channelchat.event.channel;

import org.bukkit.event.Event;

public class ChannelEvent extends Event {
    private Type type;
    protected ChannelEvent(Type type) {
        super(Event.Type.CUSTOM_EVENT);
    }

    public enum Type {
        RELOAD,
        SAVE,
        CHANNEL_CREATE,
        CHANNEL_JOIN,
        CHANNEL_LEAVE;
    }

    public Type getEventType() {
        return type;
    }
}

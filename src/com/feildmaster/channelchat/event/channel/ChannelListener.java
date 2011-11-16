package com.feildmaster.channelchat.event.channel;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.AuthorNagException;

public class ChannelListener extends CustomEventListener {
    public void onReload(ReloadEvent event) {}

    public void onSave(SaveEvent event) {}

    public void onChannelCreate(ChannelCreateEvent event) {}

    public void onChannelDelete(ChannelDeleteEvent event) {}

    public void onChannelJoin(ChannelJoinEvent event) {}

    public void onChannelLeave(ChannelLeaveEvent event) {}

    /**
     * A custom event called through Bukkit's Event Handler.
     * You should never use this, use the corisponding event function.
     */
    public final void onCustomEvent(Event event) {
        if(!(event instanceof ChannelEvent)) return;

        if(event instanceof ChannelCreateEvent)
            onChannelCreate((ChannelCreateEvent) event);
        else if(event instanceof ChannelDeleteEvent)
            onChannelDelete((ChannelDeleteEvent) event);
        else if (event instanceof ChannelJoinEvent)
            onChannelJoin((ChannelJoinEvent) event);
        else if (event instanceof ChannelLeaveEvent)
            onChannelLeave((ChannelLeaveEvent) event);
        else if (event instanceof ReloadEvent)
            onReload((ReloadEvent) event);
        else if (event instanceof SaveEvent)
            onSave((SaveEvent) event);
        else
            throw new AuthorNagException("Unsupported ChannelEvent Class");
    }
}
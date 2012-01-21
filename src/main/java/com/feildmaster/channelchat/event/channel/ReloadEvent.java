package com.feildmaster.channelchat.event.channel;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// TODO: Store "module" variable to reload single module?
// Better way to do that?
public class ReloadEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

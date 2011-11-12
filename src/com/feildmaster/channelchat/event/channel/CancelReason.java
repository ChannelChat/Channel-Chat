package com.feildmaster.channelchat.event.channel;

import org.bukkit.event.Cancellable;

public interface CancelReason extends Cancellable {
    public String getReason();

    public void setReason(String reason);
}

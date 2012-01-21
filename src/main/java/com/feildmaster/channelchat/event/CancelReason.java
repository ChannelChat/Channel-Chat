package com.feildmaster.channelchat.event;

import org.bukkit.event.Cancellable;

public interface CancelReason extends Cancellable {
    public String getCancelReason();

    public void setCancelReason(String reason);
}

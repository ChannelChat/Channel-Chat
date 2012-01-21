package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.event.CancelReason;
import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ChannelCreateEvent extends ChannelPlayerEvent implements CancelReason {
    private String cancelReason;
    private boolean canceled;

    public ChannelCreateEvent(Player player, Channel channel) {
        super(channel, player);
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String reason) {
        cancelReason = reason;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    private static HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

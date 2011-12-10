package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelCreateEvent extends ChannelPlayerEvent implements CancelReason {
    private String cancelReason;
    private boolean canceled;

    public ChannelCreateEvent(Player player, Channel channel) {
        super(channel, player, Type.CREATE);
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

    public enum Reason {
        ALLOW,
        DENY_PERMISSION,
    }
}

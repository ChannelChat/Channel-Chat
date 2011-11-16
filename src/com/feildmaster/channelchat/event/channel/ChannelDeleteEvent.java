package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelDeleteEvent extends ChannelPlayerEvent implements CancelReason{
    private String cancelReason;
    private boolean canceled;

    public ChannelDeleteEvent(Player player, Channel channel) {
        super(channel, player, Type.DELETE);
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
}

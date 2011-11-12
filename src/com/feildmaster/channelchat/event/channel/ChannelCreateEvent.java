package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ChannelCreateEvent extends ChannelPlayerEvent implements Cancellable {
    private String cancelReason;
    private boolean canceled;

    public ChannelCreateEvent(Player player, Channel channel) {
        super(channel, player, Type.CHANNEL_CREATE);
    }

    public String getReason() {
        return cancelReason;
    }

    public void setReason(String reason) {
        cancelReason = reason;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}

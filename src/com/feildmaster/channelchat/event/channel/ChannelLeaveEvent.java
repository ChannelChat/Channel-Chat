package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ChannelLeaveEvent extends ChannelPlayerEvent implements Cancellable {
    private String leaveMessage;
    private boolean canceled;

    public ChannelLeaveEvent(Player player, Channel channel) {
        super(channel, player, Type.CHANNEL_LEAVE);
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String message) {
        leaveMessage = message;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}

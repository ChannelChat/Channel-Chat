package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelLeaveEvent extends ChannelPlayerEvent implements CancelReason {
//    private String leaveMessage;
    private String cancelReason;
    private boolean canceled;

    public ChannelLeaveEvent(Player player, Channel channel) {
        super(channel, player, Type.LEAVE);
    }
//
//    public String getLeaveMessage() {
//        return leaveMessage;
//    }
//
//    public void setLeaveMessage(String message) {
//        leaveMessage = message;
//    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String reason) {
        cancelReason = reason;
    }
}
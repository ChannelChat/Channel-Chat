package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelJoinEvent extends ChannelPlayerEvent implements CancelReason {
    private String cancelReason;
    private boolean canceled;
//    private String joinMessage;

    public ChannelJoinEvent(Player player, Channel channel) {
        super(channel, player, Type.JOIN);
    }
//    // !!!
//    public String getJoinMessage() {
//        return joinMessage;
//    }
//
//    public void setJoinMessage(String message) {
//        joinMessage = message;
//    }

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
        Allowed,
        Deny_Channel
    }
}
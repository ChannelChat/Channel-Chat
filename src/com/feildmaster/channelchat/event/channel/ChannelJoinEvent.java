package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelJoinEvent extends ChannelPlayerEvent implements CancelReason {
    private String cancelReason;
    private boolean canceled;

    public ChannelJoinEvent(Player player, Channel channel) {
        super(channel, player, Type.JOIN);
    }
//
//    private String joinMessage;
//    public String getJoinMessage() {
//        return joinMessage;
//    }
//
//    public void setJoinMessage(String message) {
//        joinMessage = message;
//    }

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
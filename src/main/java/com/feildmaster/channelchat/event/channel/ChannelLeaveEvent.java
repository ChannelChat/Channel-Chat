package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.event.CancelReason;
import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ChannelLeaveEvent extends ChannelPlayerEvent implements CancelReason {
//    private String leaveMessage;
    private String cancelReason;
    private boolean canceled;

    public ChannelLeaveEvent(Player player, Channel channel) {
        super(channel, player);
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

    private static HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
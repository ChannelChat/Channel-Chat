package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.event.CancelReason;
import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ChannelJoinEvent extends ChannelPlayerEvent implements CancelReason {
    private String cancelReason;
    private boolean canceled;
//    private String joinMessage;

    public ChannelJoinEvent(Player player, Channel channel) {
        super(channel, player);
    }
//    // !!! Messages through here!
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

    private static HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
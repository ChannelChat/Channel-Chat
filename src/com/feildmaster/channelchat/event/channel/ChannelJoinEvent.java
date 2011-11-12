package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ChannelJoinEvent extends ChannelPlayerEvent implements Cancellable {
    private String joinMessage;
    private String denyMessage;
    private boolean canceled;

    public ChannelJoinEvent(Player player, Channel channel) {
        super(channel, player, Type.CHANNEL_JOIN);
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String message) {
        joinMessage = message;
    }

    public String getDenyMessage() {
        return denyMessage;
    }

    public void setDenyMessage(String message) {
        denyMessage = message;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}

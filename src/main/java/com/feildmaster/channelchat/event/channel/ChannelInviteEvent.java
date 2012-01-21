package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ChannelInviteEvent extends ChannelJoinEvent {
    private Player inviter;

    public ChannelInviteEvent(Player invited, Player inviter, Channel channel) {
        super(invited, channel);

        this.inviter = inviter;
    }

    public Player getInviter() {
        return inviter;
    }

    private static HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

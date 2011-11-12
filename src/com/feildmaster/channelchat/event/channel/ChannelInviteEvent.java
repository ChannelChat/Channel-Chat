package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelInviteEvent extends ChannelJoinEvent {
    private Player inviter;

    public ChannelInviteEvent(Player invited, Player inviter, Channel channel) {
        super(invited, channel);

        this.inviter = inviter;
    }

    public Player getInviter() {
        return inviter;
    }
}

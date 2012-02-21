package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public abstract class ChannelPlayerEvent extends ChannelEvent {
    private Player player;

    public Player getPlayer() {
        return player;
    }

    protected ChannelPlayerEvent(Channel channel, Player player) {
        super(channel);
        this.player = player;
    }
}

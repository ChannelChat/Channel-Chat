package com.feildmaster.channelchat.event.channel;

import com.feildmaster.channelchat.channel.Channel;
import org.bukkit.entity.Player;

public class ChannelPlayerEvent extends ChannelEvent {
    private Channel channel;
    private Player player;

    public Channel getChannel() {
        return channel;
    }

    public Player getPlayer() {
        return player;
    }

    protected ChannelPlayerEvent(Channel channel, Player player, Type type) {
        super(type);
        this.channel = channel;
        this.player = player;
    }
}

package com.feildmaster.channelchat.event.player;

import com.feildmaster.channelchat.channel.Channel;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChannelPlayerChatEvent extends AsyncPlayerChatEvent {
    private final Channel chan;

    public ChannelPlayerChatEvent(Player player, Channel chan, String msg, final Set<Player> players) {
        this(player, chan, msg, players, false);
    }

    public ChannelPlayerChatEvent(Player player, Channel chan, String msg, final Set<Player> players, boolean async) {
        super(async, player, msg, players);
        this.chan = chan;
    }

    public Channel getChannel() {
        return chan;
    }
}

package com.feildmaster.chanchat.Events;

import com.feildmaster.chanchat.Chan.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class ChannelPlayerChatEvent extends PlayerChatEvent {
    private Channel chan;
    public ChannelPlayerChatEvent(Player player, Channel chan, String msg) {
        super(Type.PLAYER_CHAT, player, msg);
        this.chan = chan;
    }
    public Channel getChannel() {
        return chan;
    }
}

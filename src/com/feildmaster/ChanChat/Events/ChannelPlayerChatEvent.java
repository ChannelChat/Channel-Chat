package com.feildmaster.ChanChat.Events;

import com.feildmaster.ChanChat.Chan.Channel;
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

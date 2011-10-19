package feildmaster.ChanChat.Chan;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class CustomChannel extends Channel{
    private Class<? extends CustomChannel> clazz; // Don't have to cast it...?

    public CustomChannel(String name, Class<? extends CustomChannel> c) {
        super(name, Type.Custom);
        clazz = c;
    }

    public void passEvent(PlayerChatEvent event) {
        clazz.cast(this).handleEvent(event);
    }

    public Boolean passIsMember(Player player) {
        return clazz.cast(this).isMember(player);
    }

    public void passJoinMessage(Player player) {
        clazz.cast(this).sendJoinMessage(player);
    }
}

package feildmaster.ChanChat.Chan;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class WorldChannel extends Channel {
    private World world;

    protected WorldChannel(String name) {
        super(name, Type.World);
    }
    protected WorldChannel(Channel chan) {
        super(chan, Type.World);
    }

    protected String getDisplayName() {
        return getTag()==null?"["+getName()+"]":getTag().replaceAll("(?i)`(?=[0-F])", "\u00A7").replace("{World}", world.getName())+ChatColor.WHITE;
    }

    public Boolean isMember(Player player) {
        return world.equals(player.getWorld());
    }

    public void handleEvent(PlayerChatEvent event) {
        world = event.getPlayer().getWorld();

        super.handleEvent(event);
    }
}

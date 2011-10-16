package feildmaster.ChanChat.Chan;

import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class WorldChannel extends Channel {
    private String world_name = null;

    public WorldChannel(String name) {
        super(name);
        setType(Type.World);
    }

    protected String getDisplayName() {
        return getTag()==null?"["+getName()+"]":getTag().replaceAll("(?i)`(?=[0-F])", "\u00A7").replace("{World}", world_name)+ChatColor.WHITE;
    }

    public void handleEvent(PlayerChatEvent event) {
        world_name = event.getPlayer().getWorld().getName();
        for(Player p : new HashSet<Player>(event.getRecipients()))
            if(!p.getWorld().equals(event.getPlayer().getWorld()))
                event.getRecipients().remove(p);
        event.setFormat(format(event.getFormat()));
    }
}

package feildmaster.ChanChat.Chan;

import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class LocalChannel extends Channel {
    private int range = 1000;
    private int range_squared = 1000000;
    private String out_of_range = "No one within range of your voice...";

    public LocalChannel(String name) {
        super(name, Type.Local);
    }

    // Range functions
    public Channel setRange(int r) {
        if(range == r) return this;
        range = r;
        range_squared = (int) Math.pow(r, 2);
        return this;
    }
    public int getRange() {
        return range;
    }

    public Boolean outOfRange(Location l, Location ll) {
        if(l.equals(ll))
            return false;
        if(l.distanceSquared(ll)>range_squared)
            return true;

        return false;
    }

    public void setNullMessage(String string) {
        if(string == null) return;

        out_of_range = string;
    }

    public String getNullMessage() {
        return out_of_range;
    }

    public void handleEvent(PlayerChatEvent event) {
        for(Player p : new HashSet<Player>(event.getRecipients()))
            if(!p.getWorld().equals(event.getPlayer().getWorld()) || outOfRange(event.getPlayer().getLocation(), p.getLocation()))
                event.getRecipients().remove(p);

        if(event.getRecipients().size() == 1 && getNullMessage() != null) {
            event.getPlayer().sendMessage(getNullMessage());
            event.setCancelled(true);
        }
    }
}

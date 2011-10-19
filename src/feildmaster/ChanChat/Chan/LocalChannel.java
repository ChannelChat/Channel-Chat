package feildmaster.ChanChat.Chan;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public final class LocalChannel extends Channel {
    private int range = 1000;
    private int range_squared = 1000000;
    private String out_of_range = "No one within range of your voice...";

    private Location location;

    protected LocalChannel(String name) {
        super(name, Type.Local);
    }
    protected LocalChannel(Channel chan) {
        super(chan, Type.Local);
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

    public Boolean outOfRange(Location l) {
        if(!location.getWorld().equals(l.getWorld())) return true;

        if(location.equals(l)) return false;
        if(location.distanceSquared(l)>range_squared) return true;

        return false;
    }

    public void setNullMessage(String string) {
        if(string == null) return;

        out_of_range = string;
    }

    public String getNullMessage() {
        return out_of_range;
    }

    public Boolean isMember(Player player) {
        if(super.isMember(player) && location == null) return true;
        if(location == null) return false;
        return super.isMember(player) && !outOfRange(player.getLocation());
    }

    public void handleEvent(PlayerChatEvent event) {
        location = event.getPlayer().getLocation();

        super.handleEvent(event);
        if(event.getRecipients().size() == 1 && getNullMessage() != null) {
            event.getPlayer().sendMessage(getNullMessage());
            event.setCancelled(true);
        }

        location = null;
    }
}

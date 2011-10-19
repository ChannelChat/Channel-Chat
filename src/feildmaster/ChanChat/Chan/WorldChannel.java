package feildmaster.ChanChat.Chan;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public final class WorldChannel extends Channel {
    private World world = null;
    // TODO: Store members

    protected WorldChannel(String name) {
        super(name, Type.World);
    }
    protected WorldChannel(Channel chan) {
        super(chan, Type.World);
    }

    public String getDisplayName() {
        if(getTag() == null) return "["+getName()+"]";

        return getTag().replaceAll("(?i)`(?=[0-F])", "\u00A7").replaceAll("(?i)\\{world}", world == null?"":world.getName())+ChatColor.WHITE;
    }

    public void sendJoinMessage(Player player) {
        world = player.getWorld();

        super.sendJoinMessage(player);

        world = null;
    }

    public Boolean isMember(Player player) {
        if(super.isMember(player) && world == null) return true;
        if(world == null) return false;
        return super.isMember(player) && world.equals(player.getWorld());
    }

    public void handleEvent(PlayerChatEvent event) {
        world = event.getPlayer().getWorld();

        super.handleEvent(event);

        world = null;
    }
}
